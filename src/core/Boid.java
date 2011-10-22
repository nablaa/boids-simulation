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
import java.util.Random;
import java.util.Vector;

/**
 * This class represents a flying boid. The boid uses a simple vehicle physics model.
 */
public class Boid {
    private Vector2D velocity;
    private Vector2D position;
    private Vector2D force;
    private Settings set;
    private double wanderAngle;
    private Random random;
    
    /**
     * The constructor
     * 
     * @param position position vector
     * @param velocity velocity vector
     * @param settings simulation settings
     * @param random random number generator
     */
    public Boid(Vector2D position, Vector2D velocity, Settings settings, Random random) {
        this.position = position;
        this.velocity = velocity;
        this.set = settings;
        this.force = new Vector2D();
        this.wanderAngle = 0;
        this.random = random;
    }
    
    /**
     * Get the position of the boid.
     * 
     * @return position vector
     */
    public Vector2D getPosition() {
        return this.position;
    }
    
    /**
     * Get the velocity of the boid.
     * 
     * @return velocity vector
     */
    public Vector2D getVelocity() {
        return this.velocity;
    }

    /**
     * Updates the position by applying the acceleration. This method will also
     * make sure that the boid won't fly inside an obstacle.
     * 
     * @param obstacles obstacles
     */
    public void updatePosition(Vector<Obstacle> obstacles) {
        this.position = this.checkObstacleCollision(obstacles);
        Vector2D acceleration = this.force.div(this.set.getMass());
        this.velocity = this.velocity.add(acceleration).limit(this.set.getMaxSpeed());
        this.position = this.position.add(this.velocity);
    }

    /**
     * Checks whether the boid is flying inside an obstacle or not. If the boid
     * is flying inside an obstacle, it will be moved outside the obstacle.
     * 
     * @param obstacles obstacles
     * @return the new boid position
     */
    private Vector2D checkObstacleCollision(Vector<Obstacle> obstacles) {
        synchronized (obstacles) {
            for (Obstacle o : obstacles) {
                Vector2D d = this.position.sub(o.getPosition());
                double minDist = o.getRadius() + this.set.getCollisionThreshold();
                double diff = minDist - d.norm();
                if (diff > 0) { // collision occurred
                    this.force = this.force.add(d.unit().mul(diff));
                    return this.position.add(d.unit().mul(diff));
                }
            }
        }
        return this.position;
    }

    /**
     * Returns a steering vector to avoid area borders. If the distance between
     * a border and the boid is greater than 50 units, steering force is
     * generated with strength 1/r. If the boid is outside the area, a vector
     * with strength of 1/r^2 is generated.
     * 
     * @return steering vector
     */
    private Vector2D avoidBorders() {
        Vector2D c = new Vector2D();
        double x = this.position.getX();
        double y = this.position.getY();
        
        try {
            if (!this.set.isWrapArea()) {
                if (x <= 0) {
                    c = c.add(new Vector2D(1, 0).mul(-x));
                }
                c = c.add(new Vector2D(1, 0).div(x));
                if (y <= 0) {
                    c = c.add(new Vector2D(0, 1).mul(-y));
                }
                c = c.add(new Vector2D(0, 1).div(y));
                double dx = x - this.set.getAreaWidth();
                if (x >= this.set.getAreaWidth()) {
                    c = c.add(new Vector2D(-1, 0).mul(dx));
                }
                c = c.add(new Vector2D(-1, 0).div(-dx));
                double dy = y - this.set.getAreaHeight();
                if (y >= this.set.getAreaHeight()) {
                    c = c.add(new Vector2D(0, -1).mul(dy));
                }
                c = c.add(new Vector2D(0, -1).div(-dy));
            } else {
                if (x < 0) {
                    this.position.setX(this.set.getAreaWidth() - x);
                }
                if (y < 0) {
                    this.position.setY(this.set.getAreaHeight() - y);
                }
                if (x > this.set.getAreaWidth()) {
                    this.position.setX(x - this.set.getAreaWidth());
                }
                if (y > this.set.getAreaHeight()) {
                    this.position.setY(y - this.set.getAreaHeight());
                }
            }
        } catch (ArithmeticException e) {
            // division by zero
        }
        return c;
    }
    
    /**
     * Calculates acceleration using the following five rules:
     * 1) Separation
     * 2) Alignment
     * 3) Cohesion
     * 4) Border avoidance
     * 5) Obstacle avoidance
     * 6) Wandering
     * The force generated by the above rules is limited and the acceleration is
     * generated by dividing the force by mass.
     * 
     * @param others neighbour boids
     * @param obstacles obstacles
     */
    public void calculateSteering(ArrayList<Boid> others, Vector<Obstacle> obstacles) {  
        ArrayList<Boid> boids = this.getNeighbourBoids(others);   
        Vector2D wander = this.wander().mul(this.set.getWanderFactor());
        Vector2D separation = this.calculateSeparation(boids).mul(this.set.getSeparationFactor());
        Vector2D alignment = this.calculateAlignment(boids).mul(this.set.getAlignmentFactor());
        Vector2D cohesion = this.calculateCohesion(boids).mul(this.set.getCohesionFactor());
        Vector2D borders = this.avoidBorders().mul(this.set.getBorderAvoidanceFactor());
        Vector2D avoidance = this.calculateObstacleAvoidance(obstacles).mul(this.set.getCollisionAvoidanceFactor());
        
        this.force = wander;
        this.force = this.force.add(separation);
        this.force = this.force.add(alignment);
        this.force = this.force.add(cohesion);
        this.force = this.force.add(borders);
        this.force = this.force.add(avoidance);   
        this.force = this.force.limit(this.set.getMaxForce());
    }
    
    /**
     * Calculates steering force to avoid collision with other boids.
     * The steering force to separate two boids is the difference between the
     * position vectors weighted with 1/r, where r is the distance between
     * two boids. The total steering force is a sum of all steering forces.
     * 
     * @param boids neighbour boids
     * @return total steering force
     */
    private Vector2D calculateSeparation(ArrayList<Boid> boids) {
        Vector2D rval = new Vector2D();

        for (Boid b : boids) {
            Vector2D c = this.position.sub(b.position);
            double r = c.norm();
            try {
                rval = rval.add(c.unit().div(r));
            } catch (ArithmeticException e) {
                // division by zero
            }
        }

        return rval;
    }
    
    /**
     * Calculates steering to align with the velocities of other boids.
     * The steering is the sum of all velocity vectors divided by the number of
     * boids and subtracted by the boid velocity.
     * 
     * @param boids neighbour boids
     * @return steering force
     */
    private Vector2D calculateAlignment(ArrayList<Boid> boids) {
        Vector2D vel = new Vector2D();
        if (boids.isEmpty()) {
            return vel;
        }
        
        for (Boid b : boids) {
            vel = vel.add(b.velocity);
        }

        return vel.div(boids.size()).sub(this.velocity);
    }
    
    /**
     * Calculates steering force to seek towards the center of mass of other
     * boids. The steering force is the sum of all position vectors divided by
     * the number of boids and subtracted by the boid position.
     * 
     * @param boids neighbour boids
     * @return steering force
     */
    private Vector2D calculateCohesion(ArrayList<Boid> boids) {
        Vector2D pos = new Vector2D();
        if (boids.isEmpty()) {
            return pos;
        }
        
        for (Boid b : boids) {
            pos = pos.add(b.position);
        }
        
        return pos.div(boids.size()).sub(this.position);
    }
    
    /**
     * Returns the list of neighbour boids, i.e. the boids which are inside the
     * view distance and in the view angle.
     * 
     * @param boids all boids in the simulation
     * @return the list of neighbour boids
     */
    public ArrayList<Boid> getNeighbourBoids(ArrayList<Boid> boids) {
        ArrayList<Boid> rval = new ArrayList<Boid>();
        
        for (Boid b : boids) {
            if (b == this) {
                continue;
            }
            
            Vector2D w = b.position.sub(this.position);
            
            // do not add the boids which are outside the view distance
            if (w.norm() > this.set.getViewDistance()) {
                continue;
            }
            
            // optimization: do not make unnecessary calculations if the view
            // field is a whole circle
            if (this.set.getViewAngle() != 180) {
                Vector2D r = this.velocity.unit().mul(-this.set.getViewDistance());
                double beta = 180 - this.set.getViewAngle();
                double theta = 180.0 / Math.PI * Math.acos(w.dot(r) / (w.norm() * this.set.getViewDistance()));
                if (Math.abs(theta) < beta) {
                    continue;
                }
            }
            rval.add(b);
        }
        
        return rval;
    }
    
    /**
     * Calculates steering force to avoid collision with the nearest obstacle.
     * 
     * @param obstacles obstacles in the simulation
     * @return steering force to avoid collision, zero vector if there are no obstacles to avoid
     */
    private Vector2D calculateObstacleAvoidance(Vector<Obstacle> obstacles) {
        Vector2D nearest = null; // the position of the nearest obstacle
        
        // new orthogonal basis
        Vector2D x = this.velocity.unit();
        Vector2D y = this.velocity.perpendicular().unit();
        
        synchronized (obstacles) {
            for (Obstacle o : obstacles) {
                Vector2D t = o.getPosition().sub(this.position).inOrthogonalBasis(x, y);
                
                // the obstacle is either behind the boid or too far away
                if (t.getX() <= 0
                        || t.getX() - o.getRadius() > this.set.getAvoidanceDistance()
                        || Math.abs(t.getY()) > this.set.getCollisionThreshold() + o.getRadius()) {
                    continue;
                }
                
                if (nearest == null || t.getX() < nearest.getX()) {
                    nearest = t;
                }
            }
        }
        
        if (nearest != null) {
            return y.unit().mul(nearest.getY()).limit(-this.set.getMaxForce() / 2.0);
        }
        
        return new Vector2D();
    }
    
    /**
     * Generates a steering force to simulate wandering.
     * 
     * @return steering force
     */
    private Vector2D wander() {
        double d = 40; // distance to circle
        double r = 30; // circle radius
        
        this.wanderAngle += (this.random.nextDouble() - 0.5) / 8.0;
        Vector2D offset = new Vector2D(r * Math.cos(this.wanderAngle), r * Math.sin(this.wanderAngle));
        Vector2D circleLocation = this.velocity.unit().mul(d);
        return circleLocation.add(offset).unit();
    }

    /**
     * Returns the steering force.
     * 
     * @return steering force
     */
    public Vector2D getForce() {
        return this.force;
    }

    /**
     * Set the settings. This must be called when the simulation is
     * loaded from a file.
     * 
     * @param settings simulation settings
     */
    public void setSettings(Settings settings) {
        this.set = settings;
    }

}
