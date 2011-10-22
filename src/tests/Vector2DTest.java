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
import org.junit.*;
import core.Vector2D;

/**
 * Unit tests for Vector2D class.
 */
public class Vector2DTest {
    private Vector2D zero;
    private Vector2D v1;
    private Vector2D v2;
    private Vector2D v3;
    private Vector2D v4;
    private Vector2D v5;
    
    @Before
    public void setUp() {
        zero = new Vector2D();
        v1 = new Vector2D(3, 5);
        v2 = new Vector2D(-4, -2);
        v3 = new Vector2D(2, 0);
        v4 = new Vector2D(3, 5);
        v5 = new Vector2D(-1, 3);
    }
    
    @Test
    public void testGet() {
        assertEquals(-1, v5.getX());
        assertEquals(3, v5.getY());
    }
    
    @Test
    public void testSet() {
        v1.setX(-10);
        v1.setY(61);
        assertEquals(-10, v1.getX());
        assertEquals(61, v1.getY());
    }
    
    @Test
    public void testAdd() {
        assertEquals(v5, v1.add(v2));
        assertEquals(v5, v2.add(v1));
        assertEquals(zero, zero.add(zero));
        assertEquals(new Vector2D(5, 5), v4.add(v3));
    }
    
    @Test
    public void testSub() {
        assertEquals(new Vector2D(7, 7), v1.sub(v2));
        assertEquals(zero, zero.sub(zero));
        assertEquals(new Vector2D(-3, -5), v2.sub(v5));
        assertEquals(zero, v4.sub(v1));
    }
    
    @Test
    public void testMul() {
        assertEquals(zero, zero.mul(3.6));
        assertEquals(new Vector2D(-3.5, 10.5), v5.mul(3.5));
        assertEquals(new Vector2D(8, 4), v2.mul(-2));
    }
    
    @Test
    public void testDiv() {
        assertEquals(zero, zero.div(4));
        assertEquals(new Vector2D(2, 1), v2.div(-2));
        
        try {
            v2.div(0);
            assertFalse(true);
        } catch (ArithmeticException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testUnit() {
        assertEquals(new Vector2D(3 / Math.sqrt(34), 5 / Math.sqrt(34)), v1.unit());
        assertEquals(new Vector2D(), zero.unit());
    }
    
    @Test
    public void testDot() {
        assertEquals(-22, v1.dot(v2));
        assertEquals(v1.dot(v2), v2.dot(v1));
        assertEquals(0, zero.dot(v1));
    }
    
    @Test
    public void testNorm() {
        assertEquals(0, zero.norm());
        assertEquals(Math.sqrt(20), v2.norm());
    }

    @Test
    public void testPerpendicular() {
        Vector2D p1 = new Vector2D(-3, -1);
        Vector2D p2 = new Vector2D(3, 1);
        assertTrue(v5.perpendicular().equals(p1) || v5.perpendicular().equals(p2));
        assertEquals(zero, zero.perpendicular());
    }
    
    @Test
    public void testClamp() {
        Vector2D v = new Vector2D(-4, 3);
        assertEquals(new Vector2D(3.0 * -4.0 / 5.0, 3.0 * 3.0 / 5.0), v.limit(3));
        assertEquals(v1, v4.limit(10));
    }
    
    @Test
    public void testBase() {
        Vector2D a = new Vector2D(1, 1);
        Vector2D b = new Vector2D(1, -1);
        Vector2D c = new Vector2D(3, -5);
        Vector2D d = new Vector2D(2, 3);
        assertEquals(c, c.inOrthogonalBasis(new Vector2D(1, 0), new Vector2D(0, 1)));
        assertEquals(new Vector2D(5.0 / 2.0, -0.5), d.inOrthogonalBasis(a, b));
        assertEquals(new Vector2D(-1, 4), c.inOrthogonalBasis(a, b));
        assertEquals(zero, c.inOrthogonalBasis(zero, zero));
    }
    
    @Test
    public void testToString() {
        assertEquals("(0.0, 0.0)", zero.toString());
        assertEquals("(-1.0, 3.0)", v5.toString());
    }
    
    @Test
    public void testEquals() {
        assertFalse(zero.equals(v1));
        assertEquals(v1, v4);
        assertEquals(v2, v2);
    }
    
    @Test
    public void testZero() {
        assertTrue(zero.isZero());
        assertFalse(v1.isZero());
    }
    
    
}
