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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import core.Boid;
import core.Obstacle;
import core.Simulation;
import core.Vector2D;

/**
 * This class draws the simulation.
 */
public class DrawingArea extends JPanel implements Runnable {
    private Simulation sim;
    private long sleepTime;
    private double boidSize;
    private Color boidColor;
    private Color boidEdgeColor;
    private Color boidSteeringColor;
    private Color boidVelocityColor;
    private Color sightColor;
    private Color obstacleColor;
    private Color obstacleDragColor;
    private Color obstacleInvalidColor;
    private Color arrowColor;
    private int width;
    private int height;
    private boolean stopped;
    private boolean showControlVector;
    private boolean showBoidSight;
    private boolean showBoidVelocity;
    private boolean antiAliasing;
    private Graphics2D gbuffer;
    private BufferedImage buffer;
    private MouseDragger mouseDragger;
    
    /**
     * Creates a new drawing area with the given size.
     * 
     * @param simulation simulation object
     * @param w width of the area
     * @param h height of the area
     */
    public DrawingArea(final Simulation simulation, int w, int h) {
        super();
        this.sim = simulation;
        this.sleepTime = 10;
        this.boidSize = 10;
        this.boidColor = Color.BLUE;
        this.boidEdgeColor = Color.BLACK;
        this.boidSteeringColor = Color.DARK_GRAY;
        this.boidVelocityColor = Color.RED;
        this.sightColor = Color.GRAY;
        this.obstacleColor = Color.GRAY;
        this.obstacleDragColor = Color.BLACK;
        this.obstacleInvalidColor = Color.RED;
        this.width = w;
        this.height = h;
        this.stopped = false;

        this.showControlVector = true;
        this.showBoidSight = false;
        this.showBoidVelocity = true;
        this.antiAliasing = true;
        
        // create a buffer to allow double buffering
        this.buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        this.gbuffer = (Graphics2D) this.buffer.getGraphics();
        
        this.mouseDragger = new MouseDragger(this.sim);
        this.addMouseListener(this.mouseDragger);
        this.addMouseMotionListener(this.mouseDragger);
    }

    /**
     * Runs the thread. Updates the simulation state and draws everything.
     * If the simulation is stopped, the simulation state won't be updated.
     */
    public void run() {
        while (true) {
            if (!this.stopped) {
                synchronized (this.sim.getFlock()) {
                    this.sim.makeStep();
                }
            }
            
            this.draw();
            
            try {
                Thread.sleep(this.sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    /**
     * Draws the simulation state. Draws all obstacles and boids.
     * Draws the dragging hints if the mouse is dragged.
     */
    private void draw() {
        int w = this.getSize().width;
        int h = this.getSize().height;

        // set anti-aliasing
        if (this.antiAliasing) {
            gbuffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            gbuffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        
        // clear the buffer
        gbuffer.setBackground(Color.WHITE);
        gbuffer.clearRect(0, 0, w, h);
        
        synchronized (this.sim.getFlock()) {
            this.drawBoids(gbuffer);
        }
        
        synchronized (this.sim.getObstacles()) {
            this.drawObstacles(gbuffer);
        }
        
        if (this.mouseDragger.isDragging()) {
            if (this.mouseDragger.getButton() == MouseEvent.BUTTON1) {
                this.drawDragArrow(gbuffer);
            } else if (this.mouseDragger.getButton() == MouseEvent.BUTTON3) {
                this.drawDragCircle(gbuffer);
            }
        }
        
        // draw the back buffer on screen
        this.getGraphics().drawImage(buffer, 0, 0, w, h, 0, 0, w, h, null);
    }
    
    /**
     * Draws a circle with radius text.
     * 
     * @param g graphics
     */
    private void drawDragCircle(Graphics2D g) {
        int r = this.mouseDragger.dragLength();
        
        if (this.sim.obstacleAllowed(this.mouseDragger.getX0(), this.mouseDragger.getY0(), r)) {
            g.setColor(this.obstacleDragColor);
        } else {
            g.setColor(this.obstacleInvalidColor);
        }
        
        // calculate the bounding box coordinates
        g.drawOval(this.mouseDragger.getX0() - r, this.mouseDragger.getY0() - r, 2 * r, 2 * r);
        g.drawLine(this.mouseDragger.getX0(), this.mouseDragger.getY0(), this.mouseDragger.getX1(), this.mouseDragger.getY1());
        g.drawString("" + r, this.mouseDragger.getX0(), this.mouseDragger.getY0());
    }
    
    /**
     * Draws an arrow.
     * 
     * @param g graphics
     */
    private void drawDragArrow(Graphics2D g) {
        g.setColor(this.arrowColor);
        g.drawLine(this.mouseDragger.getX0(), this.mouseDragger.getY0(), this.mouseDragger.getX1(), this.mouseDragger.getY1());
    }

    /**
     * Draws the boids. Draws also their sights and steering vectors if they are
     * enabled.
     * 
     * @param g graphics
     */
    private void drawBoids(Graphics2D g) {
        for (Boid b : this.sim.getFlock().getBoids()) {
            if (this.showBoidSight) {
                this.drawBoidSight(b, g);
            }
            if (this.showControlVector) {
                this.drawBoidSteering(b, g);
            }
            if (this.showBoidVelocity) {
                this.drawBoidVelocity(b, g);
            }
            this.drawBoid(b, g);
        }
    }
    
    /**
     * Draw the velocity vector of a boid.
     * @param b boid
     * @param g graphics
     */
    private void drawBoidVelocity(Boid b, Graphics2D g) {
        Vector2D p = b.getPosition();
        Vector2D v = b.getVelocity().mul(10); // times 10 to make the line long enough to see it
        g.setColor(this.boidVelocityColor);
        g.drawLine((int)p.getX(), (int)p.getY(), (int)(p.getX() + v.getX()), (int)(p.getY() + v.getY()));
    }

    /**
     * Draws the obstacles.
     * 
     * @param g graphics
     */
    private void drawObstacles(Graphics2D g) {
        g.setColor(this.obstacleColor);
        synchronized (this.sim.getObstacles()) {
            for (Obstacle o : this.sim.getObstacles()) {
                g.drawOval((int)(o.getPosition().getX() - o.getRadius()), (int)(o.getPosition().getY() - o.getRadius()), 2 * o.getRadius(), 2 * o.getRadius());
            }
        }
    }
    
    /**
     * Stops updating the simulation state. Does not stop the drawing.
     */
    public void stop() {
        this.stopped = true;
    }
    
    /**
     * Is the simulation stopped.
     * @return true if the simulation is stopped, false otherwise
     */
    public boolean isStopped() {
        return this.stopped;
    }
    
    /**
     * Starts the simulation.
     */
    public synchronized void start() {
        this.stopped = false;
    }

    /**
     * Draws a boid. The boid is represented by a triangle.
     * 
     * @param b boid to draw
     * @param g graphics
     */
    private void drawBoid(Boid b, Graphics2D g) {
        g.setColor(this.boidColor);
        Vector2D t = b.getVelocity().unit().mul(this.boidSize); // front vector
        
        // if the boid is stopped, draw the boid facing right
        if (t.isZero()) {
            t = new Vector2D(this.boidSize, 0);
        }
        
        Vector2D r = t.perpendicular().unit().mul(this.boidSize / 2.5); // left vector
        Vector2D s = r.mul(-1); // right vector
        Vector2D p1 = b.getPosition().add(r); // right point
        Vector2D p2 = b.getPosition().add(s); // left point
        Vector2D p3 = b.getPosition().add(t); // front point
        
        // draw a polygon
        Polygon poly = new Polygon();
        poly.addPoint((int)p1.getX(), (int)p1.getY());
        poly.addPoint((int)p2.getX(), (int)p2.getY());
        poly.addPoint((int)p3.getX(), (int)p3.getY());
        g.fillPolygon(poly);
        
        // draw edges
        g.setColor(this.boidEdgeColor);
        g.drawPolygon(poly);
    }
    
    /**
     * Draws the boid sight.
     * 
     * @param b boid
     * @param g graphics
     */
    private void drawBoidSight(Boid b, Graphics2D g) {
        g.setColor(this.sightColor);
        
        // calculate bounding box for the view distance circle
        int x0 = (int)(b.getPosition().getX() - this.sim.getSettings().getViewDistance());
        int y0 = (int)(b.getPosition().getY() - this.sim.getSettings().getViewDistance());
        int w = (int)(this.sim.getSettings().getViewDistance()) * 2;
        int h = (int)(this.sim.getSettings().getViewDistance()) * 2;
        g.drawOval(x0, y0, w, h);
        
        // calculate the lines to represent the view angle
        double beta = 180 - this.sim.getSettings().getViewAngle();
        double dx = this.sim.getSettings().getViewDistance() * Math.cos(beta * Math.PI / 180.0);
        double dy = this.sim.getSettings().getViewDistance() * Math.cos((90 - beta) * Math.PI / 180.0);
        Vector2D rv = b.getVelocity().unit().mul(-1 * dx);
        Vector2D rs = b.getVelocity().perpendicular().unit().mul(dy);
        Vector2D t = b.getPosition().add(rv).add(rs);
        Vector2D s = b.getPosition().add(rv).add(rs.mul(-1));
        g.drawLine((int)b.getPosition().getX(), (int)b.getPosition().getY(), (int)t.getX(), (int)t.getY());
        g.drawLine((int)b.getPosition().getX(), (int)b.getPosition().getY(), (int)s.getX(), (int)s.getY());
    }
    
    /**
     * Draws the boid steering force.
     * 
     * @param b boid
     * @param g graphics
     */
    private void drawBoidSteering(Boid b, Graphics2D g) {
        g.setColor(this.boidSteeringColor);
        Vector2D end = b.getPosition().add(b.getForce().mul(30));
        g.drawLine((int)b.getPosition().getX(), (int)b.getPosition().getY(), (int)end.getX(), (int)end.getY());
    }
    
    @Override
    /**
     * Gets the preferred size.
     * 
     * @return preferred size
     */
    public Dimension getPreferredSize() {
        return new Dimension(this.width, this.height);
    }
    
    @Override
    /**
     * Gets the minimum size.
     * 
     * @return minimum size
     */
    public Dimension getMinimumSize() {
        return new Dimension(this.width, this.height);
    }
    
    @Override
    /**
     * Gets the maximum size.
     * 
     * @return maximum size
     */
    public Dimension getMaximumSize() {
        return new Dimension(this.width, this.height);
    }
        
    /**
     * Gets the thread sleep time.
     * 
     * @return thread sleep time
     */
    public long getSleepTime() {
        return this.sleepTime;
    }
    
    /**
     * Sets the thread sleep time.
     * 
     * @param time thread sleep time
     */
    public void setSleepTime(long time) {
        this.sleepTime = time;
    }

    /**
     * Should we draw the steering vectors.
     * 
     * @param showControlVector true draws the vectors, false doesn't
     */
    public void setShowControlVector(boolean showControlVector) {
        this.showControlVector = showControlVector;
    }

    /**
     * Are we drawing the steering vectors.
     * 
     * @return true if the vectors are drawn, false otherwise
     */
    public boolean isShowControlVector() {
        return showControlVector;
    }
    
    /**
     * Should we draw the velocity vectors.
     * @param show true draws the vectors, false doesn't
     */
    public void setShowVelocityVector(boolean show) {
        this.showBoidVelocity = show;
    }
    
    /**
     * Are we drawing the velocity vectors.
     * @return true if the vectors are drawn, false otherwise
     */
    public boolean isShowVelocityVector() {
        return this.showBoidVelocity;
    }

    /**
     * Should we draw the boid sight.
     * 
     * @param showBoidSight true draws the sight, false doesn't
     */
    public void setShowBoidSight(boolean showBoidSight) {
        this.showBoidSight = showBoidSight;
    }

    /**
     * Are we drawing the boid sight.
     * 
     * @return true if the sight is drawn, false otherwise
     */
    public boolean isShowBoidSight() {
        return showBoidSight;
    }

    /**
     * Sets the simulation. Must be called after the simulation is loaded from a
     * file.
     * 
     * @param sim simulation
     */
    public void setSim(Simulation sim) {
        this.sim = sim;
        this.mouseDragger.setSimulation(sim);
    }
    
    /**
     * Set anti-aliasing on/off.
     * 
     * @param value anti-aliasing
     */
    public void setAntiAliasing(boolean value) {
        this.antiAliasing = value;
    }
    
    /**
     * Is the anti-aliasing enabled.
     * 
     * @return true is anti-aliasing is enabled, false otherwise
     */
    public boolean isAntiAliasing() {
        return this.antiAliasing;
    }

}
