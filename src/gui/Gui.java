/*
 *  Copyright (C) 2008 Miika-Petteri Matikainen
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import core.SaveFile;
import core.Simulation;
import core.XMLSaveFile;

/**
 * This class provides a graphical user interface to the boids simulation
 */
public class Gui extends JFrame {
    private Simulation sim;
    private DrawingArea area;
    private Thread thread;
    private OptionsPanel options;
    private JFileChooser fc;
    private Properties messages;
    private SaveFile saveFile;
    
    /**
     * Creates a new gui window. Loads Gui strings from 'messages' file.
     * 
     * @param sim simulation
     */
    public Gui(Simulation sim) {
        this.messages = this.loadMessages("messages");
        this.setTitle(this.messages.getProperty("STR_TITLE"));
        this.sim = sim;
        this.area = new DrawingArea(this.sim, 800, 800);
        this.thread = new Thread(this.area);
        this.sim.getFlock().addRandomBoids(200, 200, 400, 400, 50);
        this.fc = new JFileChooser(".");
        this.saveFile = new XMLSaveFile();
        
        // set the filename extension filter to the file chooser
        String description = this.saveFile.getFilenameDescription();
        String extension = this.saveFile.getFilenameExtension();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extension);
        this.fc.setFileFilter(filter);
        
        JPanel panel = new JPanel();
        this.setContentPane(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        panel.add(area);
        
        options = new OptionsPanel(this.sim, this.thread, this.area, this.messages);
        panel.add(options);
        this.sim.getFlock().addObserver(options);
        
        this.createMenu();
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
    
    /**
     * Loads the gui strings from a file.
     * 
     * @param filename filename
     * @return messages
     */
    private Properties loadMessages(String filename) {
        Properties properties = new Properties();
        FileReader fr = null;
        
        try {
            fr = new FileReader(filename);
            properties.load(fr);
            fr.close();
        } catch (FileNotFoundException e) {
            System.err.println("Could not find: " + filename);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Could not read: " + filename);
            System.exit(1);
        }
        return properties;
    }
    
    /**
     * Creates the menubar.
     */
    private void createMenu() {
        JMenuBar menubar = new JMenuBar();
        JMenu simulationMenu = new JMenu(this.messages.getProperty("STR_MENU_SIMULATION"));
        JMenu optionsMenu = new JMenu(this.messages.getProperty("STR_MENU_OPTIONS"));
        simulationMenu.setMnemonic(KeyEvent.VK_S);
        optionsMenu.setMnemonic(KeyEvent.VK_O);
        
        JMenuItem newSimulation = new JMenuItem(this.messages.getProperty("STR_MENU_NEW_SIMULATION"), KeyEvent.VK_N);
        newSimulation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sim.reset();
                options.reset();
                options.pause();
                area.stop();
                setTitle(messages.getProperty("STR_TITLE"));
            }
        });
        
        JMenuItem clear = new JMenuItem(this.messages.getProperty("STR_MENU_CLEAR"), KeyEvent.VK_C);
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sim.getObstacles().clear();
                sim.getFlock().removeBoids(sim.getFlock().getSize());
                options.pause();
                area.stop();
                setTitle(messages.getProperty("STR_TITLE"));
            }
        });
        
        JMenuItem save = new JMenuItem(this.messages.getProperty("STR_MENU_SAVE"), KeyEvent.VK_S);
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                area.stop(); // stop the simulation when saving
                options.pause(); // indicate the options panel that the simulation is paused
                if (fc.showSaveDialog(Gui.this) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                
                File file = fc.getSelectedFile();
                sim.getFlock().deleteObservers(); // delete the observers (optionsPanel) to
                                                  // avoid saving unnecessary gui
                                                  // objects to save file
                
                try {
                    saveFile.saveSimulation(sim, file);
                    setTitle(messages.getProperty("STR_TITLE") + " " + file.getName()); // show filename in title
                } catch (IOException e1) {
                    // the save was not successful, show a warning to the user
                    String message = messages.getProperty("STR_SAVE_FAIL") + file.getAbsolutePath();
                    JOptionPane.showMessageDialog(Gui.this, message, message, JOptionPane.ERROR_MESSAGE);
                }
                
                sim.getFlock().addObserver(options); // restore the observer
            }
        });
        
        JMenuItem load = new JMenuItem(this.messages.getProperty("STR_MENU_LOAD"), KeyEvent.VK_L);
        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                area.stop(); // stop the simulation when saving
                options.pause(); // indicate the options panel that the simulation is paused
                if (fc.showOpenDialog(Gui.this) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                
                File file = fc.getSelectedFile();
                try {
                    Simulation newSim = saveFile.loadSimulation(file);
                    
                    // check that the save file version is correct
                    if (newSim.getSettings().getVersion() != sim.getSettings().getVersion()) {
                        String message = messages.getProperty("STR_FILE_VERSION_MISMATCH");
                        JOptionPane.showMessageDialog(Gui.this, message, message, JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // set the new simulation for every class
                    sim = newSim;
                    area.setSim(sim);
                    options.setSim(sim);
                    options.reset();
                    
                    sim.getFlock().addObserver(options); // Restore the observer
                    sim.getFlock().setSettings(sim.getSettings()); // make sure everything uses the same settings object
                    setTitle(messages.getProperty("STR_TITLE") + " " + file.getName()); // show filename in title
                } catch (IOException e1) {
                    // the load was not successful, show a warning to the user
                    String message = messages.getProperty("STR_LOAD_FAIL") + file.getAbsolutePath();
                    JOptionPane.showMessageDialog(Gui.this, message, message, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JMenuItem quit = new JMenuItem(this.messages.getProperty("STR_MENU_QUIT"), KeyEvent.VK_Q);
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        JCheckBoxMenuItem aliasing = new JCheckBoxMenuItem(this.messages.getProperty("STR_MENU_ANTIALIASING"));
        aliasing.setSelected(this.area.isAntiAliasing());
        aliasing.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
                area.setAntiAliasing(item.isSelected());
            }            
        });
        
        JCheckBoxMenuItem control = new JCheckBoxMenuItem(this.messages.getProperty("STR_MENU_CONTROL_VECTORS"));
        control.setSelected(this.area.isShowControlVector());
        control.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
                area.setShowControlVector(item.isSelected());
            }           
        });
        
        JCheckBoxMenuItem velocity = new JCheckBoxMenuItem(this.messages.getProperty("STR_MENU_VELOCITY_VECTORS"));
        velocity.setSelected(this.area.isShowVelocityVector());
        velocity.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
                area.setShowVelocityVector(item.isSelected());
            }           
        });
        
        JCheckBoxMenuItem boidSight = new JCheckBoxMenuItem(this.messages.getProperty("STR_MENU_BOID_SIGHT"));
        boidSight.setSelected(this.area.isShowBoidSight());
        boidSight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
                area.setShowBoidSight(item.isSelected());
            }           
        });
        
        simulationMenu.add(newSimulation);
        simulationMenu.add(clear);
        simulationMenu.add(save);
        simulationMenu.add(load);
        simulationMenu.addSeparator();
        simulationMenu.add(quit);
        
        optionsMenu.add(aliasing);
        optionsMenu.add(control);
        optionsMenu.add(velocity);
        optionsMenu.add(boidSight);

        simulationMenu.getPopupMenu().setLightWeightPopupEnabled(false); // avoid drawing area overlap
        optionsMenu.getPopupMenu().setLightWeightPopupEnabled(false); // avoid drawing area overlap
        
        menubar.add(simulationMenu);
        menubar.add(optionsMenu);
        this.setJMenuBar(menubar);
    }

    /**
     * Creates a gui and starts the simulation.
     * 
     * @param args not used
     */
    public static void main(String[] args) {
        Simulation sim = new Simulation();
        Gui gui = new Gui(sim);
        gui.thread.start();
    }
    
}
