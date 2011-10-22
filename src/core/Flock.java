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

import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;
import java.util.Vector;

/**
 * This class represents a flock containing multiple boids.
 */
public class Flock extends Observable {
    private ArrayList<Boid> boids;
    private Settings set;
    private Random random;
    
    /**
     * Creates a new flock with no boids.
     * 
     * @param random random number generator
     * @param set simulation settings
     */
    public Flock(Random random, Settings set) {
        this.boids = new ArrayList<Boid>();
        this.random = random;
        this.set = set;
    }
    
    /**
     * Updates the boids, i.e. calculates new accelerations for all boids and
     * then moves them.
     * 
     * @param obstacles obstacles
     */
    public synchronized void updateBoids(Vector<Obstacle> obstacles) {
        for (Boid b : boids) {
            b.calculateSteering(boids, obstacles);
        }
        
        for (Boid b : boids) {
            b.updatePosition(obstacles);
        }
    }

    /**
     * Add a boid to the flock.
     * 
     * @param boid boid to add
     */
    public synchronized void addBoid(Boid boid) {
        this.boids.add(boid);
        this.setChanged();
        this.notifyObservers(this); // notify the OptionsPanel
    }

    /**
     * Add boids with random positions and velocities to the flock. The
     * positions are bounded inside a bounding box.
     * 
     * @param x0 bounding box left coordinate
     * @param y0 bounding box upper coordinate
     * @param w bounding box width
     * @param h bounding box height
     * @param n number of boids to add
     */
    public synchronized void addRandomBoids(int x0, int y0, int w, int h, int n) {
        this.addRandomBoidsDirection(x0, y0, w, h, n, null);
    }
    
    /**
     * Add boids with random positions to the flock. The position
     * is bounded inside a bounding box.
     * 
     * @param x0 bounding box left coordinate
     * @param y0 bounding box upper coordinate
     * @param w bounding box width
     * @param h bounding box height
     * @param n number of boids to add
     * @param velocity velocity of boids. If null, random velocity is generated
     */
    public synchronized void addRandomBoidsDirection(int x0, int y0, int w, int h, int n, Vector2D velocity) {
        Vector2D v = velocity;
        
        for (int i = 0; i < n; i++) {
            // generate a new position
            Vector2D p = new Vector2D(x0 + this.random.nextInt(w), y0 + this.random.nextInt(h));
            
            // generate a new velocity if not specified
            if (velocity == null) {
                // +0.1 to make sure there is no division by zero
                v = new Vector2D(-5 + this.random.nextInt(10) + 0.1, -5 + this.random.nextInt(10) + 0.1);
            }
            
            this.addBoid(new Boid(p, v.limit(this.set.getMaxSpeed()), this.set, this.random));
        }
        
        this.setChanged();
        this.notifyObservers(this); // notify the OptionsPanel
    }
    
    /**
     * Removes a boid from the flock.
     * 
     * @param boid boid to remove
     */
    public synchronized void removeBoid(Boid boid) {
        this.boids.remove(boid);
        this.setChanged();
        this.notifyObservers(this); // notify the OptionsPanel
    }
    
    /**
     * Removes boids from the flock. Removes the newest boids first (LIFO).
     * 
     * @param n number of boids to remove
     */
    public synchronized void removeBoids(int n) {
        for (int i = 0; i < n && !this.boids.isEmpty(); i++) {
            this.boids.remove(this.boids.size() - 1);
        }
        
        this.setChanged();
        this.notifyObservers(this); // notify the OptionsPanel
    }
    
    /**
     * Gets the boids.
     * 
     * @return boids
     */
    public synchronized ArrayList<Boid> getBoids() {
        return this.boids;
    }
    
    /**
     * Gets the size of the flock, i.e. the number of boids.
     * 
     * @return number of boids
     */
    public synchronized int getSize() {
        return this.boids.size();
    }

    /**
     * Sets the settings. This must be called after a simulation is loaded from
     * a file. This will also update the settings for each individual boid.
     * 
     * @param settings settings
     */
    public void setSettings(Settings settings) {
        this.set = settings;
        
        for (Boid b : boids) {
            b.setSettings(settings);
        }   
    }
    
}
