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
package core;

import java.util.Random;
import java.util.Vector;

/**
 * The simulation. Contains everything that is needed for the boids simulation.
 */
public class Simulation {
    private Settings settings;
    private Random random;
    private Vector<Obstacle> obstacles;
    private Flock flock;
    
    /**
     * Creates new simulation. Initializes the random number generator.
     * Creates an empty flock.
     */
    public Simulation() {
        this.settings = new Settings();
        this.random = new Random();
        this.flock = new Flock(this.random, this.settings);
        this.obstacles = new Vector<Obstacle>();
    }
    
    /**
     * Gets the flock.
     * 
     * @return flock
     */
    public Flock getFlock() {
        return this.flock;
    }
    
    /**
     * Gets the obstacles.
     * 
     * @return obstacles
     */
    public synchronized Vector<Obstacle> getObstacles() {
        return this.obstacles;
    }
    
    /**
     * Gets the settings.
     * 
     * @return settings
     */
    public Settings getSettings() {
        return this.settings;
    }
    
    /**
     * Gets the random number generator.
     * 
     * @return random number generator
     */
    public Random getRandom() {
        return this.random;
    }
    
    /**
     * Performs a single step in simulation, i.e. moves all boids according to
     * rules.
     */
    public void makeStep() {
        this.flock.updateBoids(this.obstacles);
    }

    /**
     * Resets the simulation. This resets the settings and removes all boids and
     * obstacles.
     */
    public void reset() {
        this.settings.reset();
        this.flock.removeBoids(this.flock.getSize());
        this.obstacles.clear();
    }

    /**
     * This will check an obstacle is allowed in the given position.
     * The obstacle may not be too close to an edge (20 units) or to another
     * obstacle.
     * 
     * @param x obstacle x coordinate
     * @param y obstacle y coordinate
     * @param r obstacle radius
     * @return true the obstacle is allowed in the given position, false otherwise
     */
    public boolean obstacleAllowed(int x, int y, int r) {
        double borderDist = 20;
        // check if too close to borders
        if (x - r < borderDist || x + r > this.settings.getAreaWidth() - borderDist
                || y - r < borderDist || y + r > this.settings.getAreaHeight() - borderDist) {
            return false;
        }

        // check if too close to other obstacles
        Vector2D p = new Vector2D(x, y);
        for (Obstacle o : this.obstacles) {
            if (p.sub(o.getPosition()).norm() < r + o.getRadius() + Settings.getObstacleMinDistance()) {
                return false;
            }
        }
        return true;
    }

}
