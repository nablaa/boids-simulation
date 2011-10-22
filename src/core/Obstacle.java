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
 * A simple round obstacle with a position and a radius.
 */
public class Obstacle {
    private Vector2D position;
    private int radius;
    
    /**
     * Creates a new obstacle with the given position and radius.
     * 
     * @param x x position
     * @param y y position
     * @param r radius
     */
    public Obstacle(int x, int y, int r) {
        this.position = new Vector2D(x, y);
        this.radius = r;
    }
    
    /**
     * Returns the position vector
     * 
     * @return position vector
     */
    public synchronized Vector2D getPosition() {
        return this.position;
    }

    /**
     * Returns the radius
     * 
     * @return radius
     */
    public synchronized int getRadius() {
        return radius;
    }    

}
