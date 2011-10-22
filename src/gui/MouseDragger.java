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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import core.Obstacle;
import core.Simulation;
import core.Vector2D;

/**
 * This class provides mouse drag for drawing area.
 */
public class MouseDragger implements MouseListener, MouseMotionListener {
    private Simulation sim;
    private boolean dragging;
    private int x0;
    private int y0;
    private int x1;
    private int y1;
    private int button;
    
    /**
     * The constructor.
     * 
     * @param sim simulation
     */
    public MouseDragger(Simulation sim) {
        this.sim = sim;
        this.dragging = false;
        this.x0 = 0;
        this.y0 = 0;
        this.x1 = 0;
        this.y1 = 0;
        this.button = 0;
    }
    
    /**
     * Mouse clicked event. Adds random boids to the cursor location if the
     * first mouse button is pressed. Creates an obstacle if the second button
     * is pressed.
     * 
     * @param e mouse event.
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            int x = e.getX() - 50;
            int y = e.getY() - 50;
            sim.getFlock().addRandomBoids(x, y, 100, 100, 20);
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            synchronized (sim.getObstacles()) {
                if (sim.obstacleAllowed(e.getX(), e.getY(), 50)) {
                    sim.getObstacles().add(new Obstacle(e.getX(), e.getY(), 50));
                }
            }
        }
    }

    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    /**
     * Mouse pressed. Stores the button for future use.
     * @param e mouse event
     */
    public void mousePressed(MouseEvent e) {
        button = e.getButton();
    }

    /**
     * The mouse is released i.e. the dragging has stopped. If the first mouse
     * button was pressed, creates new boids to the drag start location facing
     * the drag end location. If the second mouse button was pressed, creates
     * an obstacle to the drag start location. The radius of the obstacle is the
     * distance between drag start end end locations.
     */
    public void mouseReleased(MouseEvent e) {
        if (dragging) {
            dragging = false;
            x1 = e.getX();
            y1 = e.getY();
            int r = (int) Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1)
                    * (y0 - y1));
            if (button == MouseEvent.BUTTON1) {
                Vector2D v = new Vector2D(x1 - x0, y1 - y0);
                int x = x0 - 50;
                int y = y0 - 50;
                sim.getFlock().addRandomBoidsDirection(x, y, 100, 100, 20, v);
            } else if (button == MouseEvent.BUTTON3) {
                synchronized (sim.getObstacles()) {
                    if (sim.obstacleAllowed(x0, y0, r)) {
                        sim.getObstacles().add(new Obstacle(x0, y0, r));
                    }
                }
            }
        }
    }
    
    /**
     * The mouse is dragged.
     * @param e mouse event
     */
    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            x1 = e.getX();
            y1 = e.getY();
        } else {
            dragging = true;
            x0 = e.getX();
            y0 = e.getY();
        }
    }

    public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }
    
    /**
     * Gets the length of the mouse drag.
     * 
     * @return drag length
     */
    public int dragLength() {
        return (int) Math.sqrt((this.x0 - this.x1) * (this.x0 - this.x1)
                + (this.y0 - this.y1) * (this.y0 - this.y1));
    }
    
    /**
     * Is the drag enabled.
     * 
     * @return true if drag is enabled, false otherwise
     */
    public boolean isDragging() {
        return this.dragging;
    }
    
    /**
     * Get the drag mouse button.
     * 
     * @return mouse button
     */
    public int getButton() {
        return this.button;
    }
    
    /**
     * Get drag start x coordinate.
     * 
     * @return start x coordinate
     */
    public int getX0() {
        return this.x0;
    }
    
    /**
     * Get drag end x coordinate.
     * 
     * @return end x coordinate
     */
    public int getX1() {
        return this.x1;
    }
    
    /**
     * Get drag start y coordinate.
     * 
     * @return start y coordinate
     */
    public int getY0() {
        return this.y0;
    }
    
    /**
     * Get drag end y coordinate.
     * 
     * @return end y coordinate
     */
    public int getY1() {
        return this.y1;
    }

    /**
     * Sets the simulation. This must be called after a simulation is loaded
     * from a file.
     * 
     * @param sim simulation
     */
    public void setSimulation(Simulation sim) {
        this.sim = sim;
    }

}
