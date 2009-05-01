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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;

import junit.framework.TestCase;
import pl.graniec.coralreef.geometry.Point2;
import pl.graniec.coralreef.light2d.SimpleLightAlgorithm.ViewportPoint;

/**
 * @author Piotr Korzuszek <piotr.korzuszek@gmail.com>
 *
 */
public class SimpleLightAlgorithmTest extends TestCase {

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
		
		final SortedMap viewport = SimpleLightAlgorithm.buildViewport(source, resistors);
		
		final Iterator itor = viewport.keySet().iterator();
		Float angle;
		ViewportPoint point;
		
		angle = (Float) itor.next();
		point = (ViewportPoint) viewport.get(angle);
		
		assertEquals(45f, angle.floatValue(), 0f);
		assertEquals(2f, point.x, 0f);
		assertEquals(2f, point.y, 0f);
		
		
		angle = (Float) itor.next();
		point = (ViewportPoint) viewport.get(angle);
		
		assertEquals(71.56f, angle.floatValue(), 0.01f);
		assertEquals(2f, point.x, 0f);
		assertEquals(6f, point.y, 0f);
		
		
		angle = (Float) itor.next();
		point = (ViewportPoint) viewport.get(angle);
		
		assertEquals(108.43f, angle.floatValue(), 0.01f);
		assertEquals(-2f, point.x, 0f);
		assertEquals(6f, point.y, 0f);
		
		
		angle = (Float) itor.next();
		point = (ViewportPoint) viewport.get(angle);
		
		assertEquals(135f, angle.floatValue(), 0f);
		assertEquals(-2f, point.x, 0f);
		assertEquals(2f, point.y, 0f);
		
	}

}
