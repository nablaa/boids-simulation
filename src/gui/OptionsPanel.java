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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.Simulation;

/**
 * Provides a panel which contains controls to affect the simulation when it's
 * running.
 */
public class OptionsPanel extends JPanel implements Observer {
    private Simulation sim;
    private DrawingArea area;
    private JSpinner numberOfBoids;
    private JSpinner boidSpeed;
    private JSpinner boidFovAngle;
    private JSpinner boidFovDist;
    private JSpinner boidSeparationFactor;
    private JSpinner boidAlignmentFactor;
    private JSpinner boidCohesionFactor;
    private JSpinner sleepTime;
    private JSpinner maxForce;
    private JSpinner mass;
    private JButton startButton;
    private JCheckBox wrapArea;
    private Properties messages;
    
    /**
     * Creates new options panel.
     * 
     * @param simulation simulation
     * @param thread simulation thread
     * @param area drawing area
     * @param messages messages
     */
    public OptionsPanel(final Simulation simulation, final Thread thread, final DrawingArea area, final Properties messages) {
        super();
        this.sim = simulation;
        this.area = area;
        this.messages = messages;
        this.setLayout(new GridLayout(4, 6));
        
        startButton = new JButton(messages.getProperty("STR_BUTTON_PAUSE"));
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!thread.isAlive()) {
                    thread.start();
                    startButton.setText(messages.getProperty("STR_BUTTON_PAUSE"));
                } else {
                    if (area.isStopped()) {
                        area.start();
                        startButton.setText(messages.getProperty("STR_BUTTON_PAUSE"));
                    } else {
                        area.stop();
                        startButton.setText(messages.getProperty("STR_BUTTON_RESUME"));
                    }
                }
            }
        });
        
        this.add(new JLabel(messages.getProperty("STR_PAUSE_RESUME")));
        this.add(startButton);
        
        numberOfBoids = new JSpinner(new SpinnerNumberModel(this.sim.getFlock().getSize(), 0, null, 1));
        numberOfBoids.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int n = ((SpinnerNumberModel) numberOfBoids.getModel())
                        .getNumber().intValue()
                        - sim.getFlock().getSize();
                if (n < 0) {
                    sim.getFlock().removeBoids(-n);
                } else {
                    sim.getFlock().addRandomBoids(0, 0,
                            area.getSize().width, area.getSize().height, n);
                }
            }
            
        });
        
        this.add(new JLabel(messages.getProperty("STR_BOIDS")));
        this.add(numberOfBoids);

        boidSpeed = new JSpinner(new SpinnerNumberModel(this.sim.getSettings().getMaxSpeed(), 1, 100, 0.01));

        boidSpeed.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                sim.getSettings().setMaxSpeed(((SpinnerNumberModel)boidSpeed.getModel()).getNumber().intValue());
            }
        });
        
        this.add(new JLabel(messages.getProperty("STR_SPEED")));
        this.add(boidSpeed);
        
        boidFovAngle = new JSpinner(new SpinnerNumberModel(sim.getSettings().getViewAngle() * 2, 1, 360, 5));
        
        boidFovAngle.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                sim.getSettings().setViewAngle(((SpinnerNumberModel)boidFovAngle.getModel()).getNumber().intValue() / 2);
            }
        });
        
        this.add(new JLabel(messages.getProperty("STR_VIEW_ANGLE")));
        this.add(boidFovAngle);
        
        boidFovDist = new JSpinner(new SpinnerNumberModel(sim.getSettings().getViewDistance(), 5, 500, 1));
        
        boidFovDist.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                sim.getSettings().setViewDistance(((SpinnerNumberModel)boidFovDist.getModel()).getNumber().intValue());
            }
        });
        
        this.add(new JLabel(messages.getProperty("STR_VIEW_DISTANCE")));
        this.add(boidFovDist);
        
        boidSeparationFactor = new JSpinner(new SpinnerNumberModel(sim.getSettings().getSeparationFactor(), 0, 10000, 0.01));
        boidSeparationFactor.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                sim.getSettings().setSeparationFactor(((SpinnerNumberModel)boidSeparationFactor.getModel()).getNumber().doubleValue());
            }
        });
        
        this.add(new JLabel(messages.getProperty("STR_SEPARATION_FACTOR")));
        this.add(boidSeparationFactor);
        
        boidAlignmentFactor = new JSpinner(new SpinnerNumberModel(sim.getSettings().getAlignmentFactor(), 0, 10000, 0.01));
        boidAlignmentFactor.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                sim.getSettings().setAlignmentFactor(((SpinnerNumberModel)boidAlignmentFactor.getModel()).getNumber().doubleValue());
            }
        });
        
        this.add(new JLabel(messages.getProperty("STR_ALIGNMENT_FACTOR")));
        this.add(boidAlignmentFactor);
        
        boidCohesionFactor = new JSpinner(new SpinnerNumberModel(sim.getSettings().getCohesionFactor(), 0, 10000, 0.01));
        boidCohesionFactor.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                sim.getSettings().setCohesionFactor(((SpinnerNumberModel)boidCohesionFactor.getModel()).getNumber().doubleValue());
            }
        });
        
        this.add(new JLabel(messages.getProperty("STR_COHESION_FACTOR")));
        this.add(boidCohesionFactor);
        
        maxForce = new JSpinner(new SpinnerNumberModel(sim.getSettings().getMaxForce(), 0.1, 1000, 0.01));
        maxForce.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                sim.getSettings().setMaxForce(((SpinnerNumberModel)maxForce.getModel()).getNumber().doubleValue());
            }            
        });
        
        this.add(new JLabel(messages.getProperty("STR_MAX_FORCE")));
        this.add(maxForce);
        
        mass = new JSpinner(new SpinnerNumberModel(sim.getSettings().getMass(), 0.1, 1000, 0.01));
        mass.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                sim.getSettings().setMass(((SpinnerNumberModel)mass.getModel()).getNumber().doubleValue());
            }            
        });
        
        this.add(new JLabel(messages.getProperty("STR_MASS")));
        this.add(mass);
        
        sleepTime = new JSpinner(new SpinnerNumberModel(area.getSleepTime(), 0, 10000, 1));
        sleepTime.addChangeListener(new ChangeListener() {
           public void stateChanged(ChangeEvent e) {
               area.setSleepTime(((SpinnerNumberModel)sleepTime.getModel()).getNumber().longValue());
           }
        });
        
        this.add(new JLabel(messages.getProperty("STR_SLEEP_TIME")));
        this.add(sleepTime);
        
        wrapArea = new JCheckBox(messages.getProperty("STR_WRAP_AREA"));
        wrapArea.setSelected(sim.getSettings().isWrapArea());
        wrapArea.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBox item = (JCheckBox) e.getSource();
                sim.getSettings().setWrapArea(item.isSelected());
            }
        });
        
        this.add(wrapArea);
    }

    /**
     * Updates the number of boids. This will be called when the number of boids
     * changes in the flock.
     */
    public void update(Observable obs, Object object) {
        if (object == this.sim.getFlock()) {
            this.numberOfBoids.setValue(this.sim.getFlock().getSize());
        }
    }

    /**
     * Resets the gui components. This must be called after a simulation is
     * loaded from a file.
     */
    public void reset() {
        numberOfBoids.setValue(this.sim.getFlock().getSize());
        boidSpeed.setValue(this.sim.getSettings().getMaxSpeed());
        boidFovAngle.setValue(this.sim.getSettings().getViewAngle() * 2);
        boidFovDist.setValue(this.sim.getSettings().getViewDistance());
        boidSeparationFactor.setValue(this.sim.getSettings().getSeparationFactor());
        boidAlignmentFactor.setValue(this.sim.getSettings().getAlignmentFactor());
        boidCohesionFactor.setValue(this.sim.getSettings().getCohesionFactor());
        sleepTime.setValue(this.area.getSleepTime());
        wrapArea.setSelected(this.sim.getSettings().isWrapArea());
    }
    
    /**
     * Sets the simulation. This must be called after a simulation is
     * loaded from a file.
     * 
     * @param sim simulation
     */
    public void setSim(Simulation sim) {
        this.sim = sim;
    }

    /**
     * Changes the pause button text to 'Resume'. This should be called whenever
     * the game is paused.
     */
    public void pause() {
        this.startButton.setText(this.messages.getProperty("STR_BUTTON_RESUME"));
    }
    
}
