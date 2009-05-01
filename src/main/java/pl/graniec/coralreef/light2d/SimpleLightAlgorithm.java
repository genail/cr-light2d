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
import java.util.TreeMap;

import pl.graniec.coralreef.geometry.Box2;
import pl.graniec.coralreef.geometry.Geometry;
import pl.graniec.coralreef.geometry.Point2;
import pl.graniec.coralreef.geometry.Segment;
import pl.graniec.coralreef.geometry.Vector2;

/**
 * @author Piotr Korzuszek <piotr.korzuszek@gmail.com>
 *
 */
public class SimpleLightAlgorithm extends AbstractLightingAlgorithm {

	public static final class Direction {
		public static final int None = 1;
		public static final int Left = 2;
		public static final int Right = 3;
		
		private Direction() {
		}
	}
	
	private static final class ResistorBind {
		final LightResistor resistor;
		final int pointIndex;
		
		public ResistorBind(LightResistor resistor, int pointIndex) {
			super();
			this.resistor = resistor;
			this.pointIndex = pointIndex;
		}
		
	}
	
	private static final class ResistorSegment extends Segment {
		
		final LightResistor resistor;

		public ResistorSegment(final LightResistor resistor, float x1, float y1, float x2, float y2) {
			super(x1, y1, x2, y2);
			this.resistor = resistor;
		}
		
	}
	
	/**
	 * Segment point that can tell the angle on which it residents.
	 *
	 */
	static final class ViewportPoint extends Point2 implements Comparable {

		final ResistorSegment segment;
		final float angle;

		public ViewportPoint(final LightSource source, final ResistorSegment segment, float x, float y) {
			super(x - source.x, y - source.y);
			this.segment = segment;
			
			this.angle = Vector2.angle(this.x, this.y);
		}

		public int compareTo(Object obj) {
			if (getClass() != obj.getClass()) {
				throw new ClassCastException("cannnot cast " + obj.getClass() + " to " + getClass());
			}
			
			final ViewportPoint other = (ViewportPoint) obj;
			
			if (angle < other.angle) {
				return -1;
			} else if (angle > other.angle) {
				return +1;
			} else {
				return 0;
			}
		}
		
		/*
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return getClass().getSimpleName() + "[x=" + x + ",y=" + y + ",angle=" + angle + "]";
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			ViewportPoint other = (ViewportPoint) obj;
			if (Float.floatToIntBits(angle) != Float
					.floatToIntBits(other.angle))
				return false;
			if (segment == null) {
				if (other.segment != null)
					return false;
			} else if (!segment.equals(other.segment))
				return false;
			return true;
		}

		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + Float.floatToIntBits(angle);
			result = prime * result
					+ ((segment == null) ? 0 : segment.hashCode());
			return result;
		}
		
		
		
	}
	
	static final float getAngleDifference(final float from, final float to) {
		final float transCurrent = to - from;
		
		if (transCurrent > 180.0f) {
			return -180.0f + (transCurrent - 180.0f);
		} else if (transCurrent < -180.0f) {
			return 180.0f + (transCurrent + 180.0f);
		}
		
		return transCurrent;
	}

	static final int getDirection(final float lastAngle, final float currentAngle) {
		return (getAngleDifference(lastAngle, currentAngle) > 0) ? Direction.Left : Direction.Right;
	}
	
	static final SortedMap/*<Float, ViewportPoint>*/ buildViewport(final LightSource source, final List/*<LightResistor>*/ nearResistors) {
		
		// build viewport
		final SortedMap/*<Float, ViewportPoint>*/ viewport = new TreeMap();
		
		// make segments from all resistors and extract the points
		final List/*<ResistorSegment>*/ segments = new LinkedList();
//		final List/*<ViewportPoint>*/ points = new LinkedList();
		
		for (final Iterator itor = nearResistors.iterator(); itor.hasNext();) {
			final LightResistor resistor = (LightResistor) itor.next();
			
			final Point2[] verticles = resistor.getVerticles();
			
			for (int i = 0; i < verticles.length; ++i) {
				final int i2 = (i + 1 >= verticles.length) ? i + 1 - verticles.length : i + 1;
				
				final ResistorSegment segment = new ResistorSegment(
					resistor,
					verticles[i].x, verticles[i].y,
					verticles[i2].x, verticles[i2].y
				); 
				
				segments.add(segment);
				
				// extract point from this segment
				final ViewportPoint point1 = new ViewportPoint(source, segment, segment.x1, segment.y1);
				final ViewportPoint point2 = new ViewportPoint(source, segment, segment.x2, segment.y2);
				
				viewport.put(new Float(point1.angle), point1);
				viewport.put(new Float(point2.angle), point2);
			}
		}
		
		return viewport;
	}
	
	
	/*
	 * @see pl.graniec.coralreef.light2d.AbstractLightingAlgorithm#createRays(pl.graniec.coralreef.light2d.LightSource)
	 */
	public Geometry createRays(final LightSource source) {
		
		// build resistors list that can make the shadow (its near light source)
		final List nearResistors = determineNearResistors(source);
		
		// Create one dimensional axis with left and right side point of a
		// resistor like this:
		//
		//                                           ______r3______
		//        _____r1____                  __r2_|___           |
		//       |           |                |     |   |          |
		// ---------------------------------------------------------------->
		// |     |           |           |                                |
		// 0     a           b          180                              360
		//
		//
		// This is how the light source sees its surroundings. 
		//
		// On this picture there is resistor 'r1' that has its boundary from
		// 'a' (left side) to 'b' (right side). Resistors 'r2' and 'r3'
		// overlaps so layer we must decide which one is on front.
		//
		// The resistors bounding points exists in resistor geomery as one
		// of its verticles.
		
		final SortedMap viewport = buildViewport(source, nearResistors);
		
		return null;
	}

	/**
	 * Creates a list of resistors that can create shadow (they're in
	 * light distance).
	 */
	private List/*<LightResistor>*/ determineNearResistors(final LightSource source) {
		
		final List result = new LinkedList();
		
		Box2 bbox;
		
		float diagonal;
		float distance, distanceMin;
		Point2 otherVerticles[] = new Point2[3];
		boolean foundOther;
		
		for (final Iterator itor = resistors.iterator(); itor.hasNext();) {
			final LightResistor r = (LightResistor) itor.next();
			bbox = r.getBoundingBox();
			diagonal = bbox.diagonal();
		
			distance = Segment.length(bbox.left, bbox.top, source.x, source.y);
			
			// get the minimal possible distance
			distanceMin = distance - diagonal;
			
			if (distanceMin > source.intensity) {
				// this resistor is far away of the light
				continue;
			}
			
			if (distance <= source.intensity) {
				// this is most probably in light radius
				result.add(r);
				break;
			}
			
			// check the other 3 bounding verticles
			otherVerticles[0] = new Point2(bbox.right, bbox.top);
			otherVerticles[1] = new Point2(bbox.right, bbox.bottom);
			otherVerticles[2] = new Point2(bbox.left, bbox.bottom);
			
			foundOther = false;
			
			for (int i = 0; i < otherVerticles.length; ++i) {
				final Point2 p = otherVerticles[i];
				distance = Segment.length(p.x, p.y, source.x, source.y);
				
				if (distance <= source.intensity) {
					foundOther = true;
					break;
				}
			}
			
			if (foundOther) {
				result.add(r);
			}
		}
		
		return result;
		
	}

}
