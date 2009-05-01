/**
 * Copyright (c) 2009, Coral Reef Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of the Coral Reef Project nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package pl.graniec.coralreef.light2d;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import junit.framework.TestCase;
import pl.graniec.coralreef.geometry.Geometry;
import pl.graniec.coralreef.geometry.Point2;
import pl.graniec.coralreef.light2d.SimpleLightAlgorithm.ViewportPoint;

/**
 * @author Piotr Korzuszek <piotr.korzuszek@gmail.com>
 *
 */
public class SimpleLightAlgorithmTest extends TestCase {
	
	private class DisplayFrame extends JFrame {
		
		private class CustomPanel extends JPanel {
			public CustomPanel() {
			}
			
			public void paint(Graphics g) {
				g.setColor(Color.white);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				g.setColor(Color.black);
				
				for (Iterator itor = points.iterator(); itor.hasNext();) {
					Point2 point = (Point2) itor.next();
					g.drawLine((int) point.x, (int) point.y, (int) point.x, (int) point.y);
				}
			}
		}
		
		CustomPanel panel = new CustomPanel();
		List points = new LinkedList();
		
		public DisplayFrame() {
			add(panel);
			setSize(640, 480);
		}
		
		public void addPoint(Point2 point) {
			points.add(point);
		}
	}

	/*
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link pl.graniec.coralreef.light2d.SimpleLightAlgorithm#buildViewport(pl.graniec.coralreef.light2d.LightSource, java.util.List)}.
	 */
	public void testBuildViewport() {
		
		final List resistors = new LinkedList(); 
		
		final LightResistor r1 = new LightResistor();
		r1.addVerticle(new Point2(2, 2));
		r1.addVerticle(new Point2(2, 6));
		r1.addVerticle(new Point2(-2, 6));
		r1.addVerticle(new Point2(-2, 2));
		
		resistors.add(r1);
		
		final LightSource source = new LightSource(0, 0, 100);
		
		final List viewport = SimpleLightAlgorithm.buildViewport(source, resistors);
		
		final Iterator itor = viewport.iterator();
		ViewportPoint point;
		
		point = (ViewportPoint) itor.next();
		
		assertEquals(45f, point.angle, 0f);
		assertEquals(2f, point.x, 0f);
		assertEquals(2f, point.y, 0f);
		
		
		point = (ViewportPoint) itor.next();
		
		assertEquals(71.56f, point.angle, 0.01f);
		assertEquals(2f, point.x, 0f);
		assertEquals(6f, point.y, 0f);
		
		
		point = (ViewportPoint) itor.next();
		
		assertEquals(108.43f, point.angle, 0.01f);
		assertEquals(-2f, point.x, 0f);
		assertEquals(6f, point.y, 0f);
		
		
		point = (ViewportPoint) itor.next();
		
		assertEquals(135f, point.angle, 0f);
		assertEquals(-2f, point.x, 0f);
		assertEquals(2f, point.y, 0f);
		
	}
	
	public void testOverall() throws InterruptedException {
		
		final DisplayFrame frame = new DisplayFrame();
		final SimpleLightAlgorithm algorithm = new SimpleLightAlgorithm();
		
		final LightSource light = new LightSource(320, 240, 300);
		final LightResistor resistor = new LightResistor();
		resistor.addVerticle(new Point2(340, 220));
		resistor.addVerticle(new Point2(340, 200));
		resistor.addVerticle(new Point2(300, 200));
		resistor.addVerticle(new Point2(300, 220));
		
		algorithm.addLightResistor(resistor);
		final Geometry rays = algorithm.createRays(light);
		
		System.out.println(rays);
		
		final Point2[] verts = rays.getVerticles();
		for (int i = 0; i < verts.length; ++i) {
			frame.addPoint(new Point2(verts[i].x, verts[i].y));
		}
		
//		frame.addPoint(new Point2(100, 100));
		frame.setVisible(true);
		
		Thread.sleep(5000);
		
	}

}
