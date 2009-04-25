package pl.graniec.coralreef.light2d;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Box;

import pl.graniec.coralreef.geometry.Box2;
import pl.graniec.coralreef.geometry.Geometry;
import pl.graniec.coralreef.geometry.Point2;

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

/**
 * @author Piotr Korzuszek <piotr.korzuszek@gmail.com>
 *
 */
public class LightResistor extends Geometry {
	
	/** Bounding box of this geometry. If null then it must be created. */
	private Box2 bbox;
	
	/*
	 * @see pl.graniec.coralreef.geometry.Geometry#addVerticle(pl.graniec.coralreef.geometry.Point2)
	 */
	public void addVerticle(Point2 point) {
		super.addVerticle(point);
		bbox = null;
	}
	
	/*
	 * @see pl.graniec.coralreef.geometry.Geometry#addVerticles(java.util.Collection)
	 */
	public void addVerticles(Collection verticles) {
		super.addVerticles(verticles);
		bbox = null;
	}
	
	/*
	 * @see pl.graniec.coralreef.geometry.Geometry#addVerticles(pl.graniec.coralreef.geometry.Point2[])
	 */
	public void addVerticles(Point2[] points) {
		super.addVerticles(points);
		bbox = null;
	}
	
	private void calculateBBox() {
		
		if (verticles.isEmpty()) {
			return;
		}
		
		// get first point and set it's values
		final Point2 firstPoint = (Point2) verticles.get(0);
		bbox = new Box2(firstPoint.x, firstPoint.y, firstPoint.x, firstPoint.y);
		
		for (final Iterator itor = verticles.iterator(); itor.hasNext();) {
			final Point2 p = (Point2) itor.next();
			
			if (p == firstPoint) {
				continue;
			}
			
			if (p.x < bbox.left) {
				bbox.left = p.x;
			}
			else if (p.x > bbox.right) {
				bbox.right = p.x;
			}
			
			if (p.y > bbox.top) {
				bbox.top = p.y;
			}
			else if (p.y < bbox.bottom) {
				bbox.bottom = p.y;
			}
		}
		
	}

	public Box2 getBoundingBox() {
		if (bbox == null) {
			calculateBBox();
		}
		
		return bbox;
	}
}
