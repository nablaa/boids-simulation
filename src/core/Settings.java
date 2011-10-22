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

/**
 * This class contains the settings for the simulation.
 */
public class Settings {
    private double separationFactor;
    private double alignmentFactor;
    private double cohesionFactor;
    private double wanderFactor;
    private double maxSpeed;
    private double maxForce;
    private double mass;
    private double viewAngle;
    private double viewDistance;
    private double avoidanceDistance;
    private double collisionThreshold;
    private double borderAvoidanceFactor;
    private double collisionAvoidanceThreshold;
    private int areaWidth;
    private int areaHeight;
    private boolean wrapArea;
    
    private final int version = 1; // Save file version to avoid loading incompatible save files
    private final static double SEPARATION_FACTOR = 2.0;
    private final static double ALIGNMENT_FACTOR = 1.5;
    private final static double COHESION_FACTOR = 0.01;
    private final static double WANDER_FACTOR = 1.0;
    private final static double MAX_SPEED = 5.0;
    private final static double MAX_FORCE = 5.0;
    private final static double MASS = 10.0;
    private final static double VIEW_ANGLE = 120;
    private final static double VIEW_DISTANCE = 50;
    private final static double AVOIDANCE_DISTANCE = 100;
    private final static double COLLISION_THRESHOLD = 8;
    private final static double BORDER_AVOIDANCE_FACTOR = 10;
    private final static double COLLISION_AVOIDANCE_THRESHOLD = 2;
    private final static int AREA_WIDTH = 800;
    private final static int AREA_HEIGHT = 800;
    private final static boolean WRAP_AREA = false;
    private final static int OBSTACLE_MIN_DISTANCE = 30;
    
    /**
     * Creates new Settings with default values.
     */
    public Settings() {
        this.reset();
    }
    
    /**
     * Get the save file version. This is used to detect incompatible save
     * files.
     * 
     * @return save file version
     */
    public int getVersion() {
        return this.version;
    }

    /**
     * Sets the separation factor.
     * @param separationFactor factor
     */
    public void setSeparationFactor(double separationFactor) {
        this.separationFactor = separationFactor;
    }

    /**
     * Gets the separation factor.
     * @return separation factor
     */
    public double getSeparationFactor() {
        return separationFactor;
    }

    /**
     * Sets the alignment factor.
     * @param alignmentFactor factor
     */
    public void setAlignmentFactor(double alignmentFactor) {
        this.alignmentFactor = alignmentFactor;
    }

    /**
     * Gets the separation factor.
     * @return alignment factor
     */
    public double getAlignmentFactor() {
        return alignmentFactor;
    }

    /**
     * Sets the cohesion factor.
     * @param cohesionFactor factor
     */
    public void setCohesionFactor(double cohesionFactor) {
        this.cohesionFactor = cohesionFactor;
    }

    /**
     * Gets the separation factor.
     * @return cohesion factor
     */
    public double getCohesionFactor() {
        return cohesionFactor;
    }

    /**
     * Gets the wander factor.
     * @return wander factor
     */
    public double getWanderFactor() {
        return this.wanderFactor;
    }
    
    /**
     * Sets the maximum speed for boids.
     * @param maxSpeed max speed
     */
    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * Gets the maximum speed of boids
     * @return max speed
     */
    public double getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Sets the view angle for boids.
     * @param viewAngle angle in degrees
     */
    public void setViewAngle(double viewAngle) {
        this.viewAngle = viewAngle;
    }

    /**
     * Gets the view angle of boids.
     * @return angle in degrees
     */
    public double getViewAngle() {
        return viewAngle;
    }

    /**
     * Sets the view distance for boids.
     * @param viewDistance distance
     */
    public void setViewDistance(double viewDistance) {
        this.viewDistance = viewDistance;
    }

    /**
     * Gets the view angle of boids.
     * @return view distance
     */
    public double getViewDistance() {
        return viewDistance;
    }

    /**
     * Sets the area width
     * @param areaWidth width
     */
    public void setAreaWidth(int areaWidth) {
        this.areaWidth = areaWidth;
    }

    /**
     * Gets the area width
     * @return width
     */
    public int getAreaWidth() {
        return areaWidth;
    }

    /**
     * Sets the area height
     * @param areaHeight height
     */
    public void setAreaHeight(int areaHeight) {
        this.areaHeight = areaHeight;
    }

    /**
     * Sets the area height
     * @return height
     */
    public int getAreaHeight() {
        return areaHeight;
    }

    /**
     * Sets the obstacle avoidance distance
     * @param avoidanceDistance distance
     */
    public void setAvoidanceDistance(double avoidanceDistance) {
        this.avoidanceDistance = avoidanceDistance;
    }

    /**
     * Gets the obstacle avoidance distance
     * @return avoidance distance
     */
    public double getAvoidanceDistance() {
        return avoidanceDistance;
    }

    /**
     * Sets the collision threshold for obstacles
     * @param collisionThreshold threshold
     */
    public void setCollisionThreshold(double collisionThreshold) {
        this.collisionThreshold = collisionThreshold;
    }

    /**
     * Sets the collision threshold of obstacles
     * @return threshold
     */
    public double getCollisionThreshold() {
        return collisionThreshold;
    }

    /**
     * Sets the mass for boids
     * @param mass mass
     */
    public void setMass(double mass) {
        this.mass = mass;
    }

    /**
     * Gets the mass of boids
     * @return mass
     */
    public double getMass() {
        return mass;
    }
    
    /**
     * Gets the max force of boids.
     * @return max force
     */
    public double getMaxForce() {
        return this.maxForce;
    }
    
    /**
     * Sets the max force for boids.
     * @param force max force
     */
    public void setMaxForce(double force) {
        this.maxForce = force;
    }

    /**
     * Sets the border avoidance factor for boids.
     * @param borderAvoidanceFactor factor
     */
    public void setBorderAvoidanceFactor(double borderAvoidanceFactor) {
        this.borderAvoidanceFactor = borderAvoidanceFactor;
    }

    /**
     * Gets the border avoidance factor of boids.
     * @return factor
     */
    public double getBorderAvoidanceFactor() {
        return borderAvoidanceFactor;
    }
    
    /**
     * Gets the collision avoidance factor.
     * @return factor
     */
    public double getCollisionAvoidanceFactor() {
        return this.collisionAvoidanceThreshold;
    }
    
    /**
     * Is the area wrapped.
     * @return true if the area is wrapped, false otherwise
     */
    public boolean isWrapArea() {
        return this.wrapArea;
    }
    
    /**
     * Sets the area wrap.
     * @param wrap is the area wrapped
     */
    public void setWrapArea(boolean wrap) {
        this.wrapArea = wrap;
    }

    /**
     * Gets the obstacle minimum distance.
     * @return minimum distance
     */
    public static int getObstacleMinDistance() {
        return OBSTACLE_MIN_DISTANCE;
    }
    
    /**
     * Reset the settings to their default values.
     */
    public void reset() {
        this.separationFactor = Settings.SEPARATION_FACTOR;
        this.alignmentFactor = Settings.ALIGNMENT_FACTOR;
        this.cohesionFactor = Settings.COHESION_FACTOR;
        this.wanderFactor = Settings.WANDER_FACTOR;
        this.maxSpeed = Settings.MAX_SPEED;
        this.maxForce = Settings.MAX_FORCE;
        this.mass = Settings.MASS;
        this.viewAngle = Settings.VIEW_ANGLE;
        this.viewDistance = Settings.VIEW_DISTANCE;
        this.avoidanceDistance = Settings.AVOIDANCE_DISTANCE;
        this.collisionThreshold = Settings.COLLISION_THRESHOLD;
        this.borderAvoidanceFactor = Settings.BORDER_AVOIDANCE_FACTOR;
        this.collisionAvoidanceThreshold = Settings.COLLISION_AVOIDANCE_THRESHOLD;
        this.areaWidth = Settings.AREA_WIDTH;
        this.areaHeight = Settings.AREA_HEIGHT;
        this.wrapArea = Settings.WRAP_AREA;
    }

    
}
