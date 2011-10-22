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
package tests;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.*;
import core.Boid;
import core.Settings;
import core.Vector2D;

/**
 * Unit tests for Boid class.
 */
public class BoidTest {
    private Boid b;
    private Boid b1;
    private Boid b2;
    private Boid b3;
    private Boid b4;
    private Boid b5;
    private Boid b6;
    private Boid b7;
    private ArrayList<Boid> boids;
    
    @Before
    public void setUp() {
        Settings set = new Settings();
        Vector2D p = new Vector2D(40, 60);
        Vector2D v = new Vector2D(5, -5);        
        b = new Boid(p, v, set, null);
        set.setViewAngle(120);
        set.setViewDistance(20);
        
        p = new Vector2D(19, 60);
        b1 = new Boid(p, v, set, null);
        
        p = new Vector2D(61, 60);
        b2 = new Boid(p, v, set, null);
        
        p = new Vector2D(40, 39);
        b3 = new Boid(p, v, set, null);
        
        p = new Vector2D(40, 81);
        b4 = new Boid(p, v, set, null);
        
        p = new Vector2D(50, 50);
        b5 = new Boid(p, v, set, null);
        
        p = new Vector2D(30, 70);
        b6 = new Boid(p, v, set, null);
        
        p = new Vector2D(42, 70);
        b7 = new Boid(p, v, set, null);
        
        boids = new ArrayList<Boid>();
        boids.add(b);
        boids.add(b1);
        boids.add(b2);
        boids.add(b3);
        boids.add(b4);
        boids.add(b5);
        boids.add(b6);
        boids.add(b7);
    }
    
    @Test
    public void testGetNeighbourBoids() {
        assertFalse(b.getNeighbourBoids(boids).contains(b1));
        assertFalse(b.getNeighbourBoids(boids).contains(b2));
        assertFalse(b.getNeighbourBoids(boids).contains(b3));
        assertFalse(b.getNeighbourBoids(boids).contains(b4));
        assertTrue(b.getNeighbourBoids(boids).contains(b5));
        assertFalse(b.getNeighbourBoids(boids).contains(b6));
        assertFalse(b.getNeighbourBoids(boids).contains(b7));
    }
    

}
