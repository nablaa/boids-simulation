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
 * A class for 2D (surface) vector.
 */
public class Vector2D {
    private double x;
    private double y;
    
    /**
     * Creates a new vector with the given coordinates.
     * 
     * @param x x coordinate
     * @param y y coordinate
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Creates a new zero vector.
     */
    public Vector2D() {
        this(0, 0);
    }
    
    /**
     * Gets the x coordinate.
     * 
     * @return x coordinate
     */
    public double getX() {
        return this.x;
    }
    
    /**
     * Gets the y coordinate.
     * 
     * @return y coordinate
     */
    public double getY() {
        return this.y;
    }
    
    /**
     * Sets the x coordinate.
     * 
     * @param x new x coordinate
     */
    public void setX(double x) {
        this.x = x;
    }
    
    /**
     * Sets the y coordinate.
     * 
     * @param y new y coordinate
     */
    public void setY(double y) {
        this.y = y;
    }
    
    /**
     * Adds two vectors. Returns a new vector.
     * 
     * @param other other vector
     * @return sum of two vectors
     */
    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }
    
    /**
     * Subtracts two vectors. Returns a new vector.
     * 
     * @param other other vector
     * @return subtraction of two vectors
     */
    public Vector2D sub(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }
    
    /**
     * Multiplies the vector with the given scalar. Returns a new vector.
     * 
     * @param n scalar
     * @return new multiplied vector
     */
    public Vector2D mul(double n) {
        return new Vector2D(this.x * n, this.y * n);
    }
    
    /**
     * Divides the vector with the given scalar. Returns a new vector.
     * If the scalar is zero an ArithmeticException is thrown.
     * 
     * @param n scalar
     * @return new divided vector
     */
    public Vector2D div(double n) {
        this.checkZero(n);
        return this.mul(1 / n);
    }
    
    /**
     * Generates a new unit vector from this vector.
     * 
     * @return new unit vector
     */
    public Vector2D unit() {
        double norm = this.norm();
        try {
            this.checkZero(norm);
            return new Vector2D(this.x / norm, this.y / norm);
        } catch (ArithmeticException e) {
            return new Vector2D();
        }
    }

    /**
     * Returns the norm (length) of the vector.
     * 
     * @return norm
     */
    public double norm() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }
    
    /**
     * Dot product with another vector.
     * 
     * @param other other vector
     * @return dot product
     */
    public double dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }
    
    /**
     * Generates a new vector that is perpendicular to this vector. If the given
     * vector is (x, y) then the perpendicular vector is (-y, x).
     * 
     * @return new perpendicular vector
     */
    public Vector2D perpendicular() {
        return new Vector2D(-this.y, this.x);
    }
    
    /**
     * Limits the length of the vector to the given scalar.
     * 
     * @param max max length
     * @return new scaled vector
     */
    public Vector2D limit(double max) {
        if (this.norm() < max) {
            return this;
        }
        
        return this.unit().mul(max);
    }
    
    /**
     * Represents the vector in the orthogonal base spanned by two vectors.
     * The given vectors must be orthogonal.
     * 
     * @param a vector
     * @param b vector
     * @return vector in new base
     */
    public Vector2D inOrthogonalBasis(Vector2D a, Vector2D b) {
        double na = a.norm();
        double nb = b.norm();
        try {
            this.checkZero(na);
            this.checkZero(nb);
        } catch (ArithmeticException e) {
            return new Vector2D();
        }
        return new Vector2D(this.dot(a) / (na * na), this.dot(b) / (nb * nb));
    }
    
    @Override
    /**
     * Are the given vectors equal, i.e. are the coordinates the same. The
     * vectors are considered equal if the coordinates differ less than
     * 0.000001.
     * 
     * @param other other vector
     */
    public boolean equals(Object other) {
        Vector2D v = (Vector2D) other;
        if (v == null) {
            return false;
        }
        
        return Math.abs(this.x - v.x) < 0.000001 && Math.abs(this.y - v.y) < 0.000001;
    }
    
    @Override
    /**
     * Returns the string representation of the vector. The format is as follows:
     * (x, y)
     * 
     * @return string representing the vector
     */
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
    
    /**
     * Makes sure the given number is not zero to avoid division by zero.
     * Throws an ArithmeticException if the number is zero.
     * 
     * @param n number
     */
    private void checkZero(double n) {
        if (n >= -0.000001 && n <= 0.000001) {
            throw new ArithmeticException("Division by zero");
        }
    }

    /**
     * Is the vector a zero vector. The vector is considered zero vector if the
     * components are inside the range [-0.000001, 0.000001].
     * 
     * @return true if the vector is zero vector, false otherwise.
     */
    public boolean isZero() {
        return x >= -0.000001 && x <= 0.000001 && y >= -0.000001 && y <= 0.000001;
    }
    
}
