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

	private static final class ResistorBind {
		final LightResistor resistor;
		final int pointIndex;
		
		public ResistorBind(LightResistor resistor, int pointIndex) {
			super();
			this.resistor = resistor;
			this.pointIndex = pointIndex;
		}
		
	}
	
	/*
	 * @see pl.graniec.coralreef.light2d.AbstractLightingAlgorithm#createRays(pl.graniec.coralreef.light2d.LightSource)
	 */
	@Override
	public Geometry createRays(final LightSource source) {
		
		// build resistors list that can make the shadow (its near light source)
		final List<LightResistor> nearResistors = determineNearResistors(source);
		
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
		
		final SortedMap<Float, ResistorBind> viewport = buildViewport(source, nearResistors);
		
		return null;
	}

	private SortedMap<Float, ResistorBind> buildViewport(final LightSource source, final List<LightResistor> nearResistors) {
		final SortedMap<Float, ResistorBind> viewport = new TreeMap<Float, ResistorBind>();
		
		float x, y;
		float angle;
		
		float leftMostAngle, rightMostAngle;
		int leftMostIndex, rightMostIndex;
		boolean firstPoint;
		int index;
		
		// keeps the angle => verticle index map to find boundary verticles
//		final SortedMap<Float, Integer> angleIndexMap = new TreeMap<Float, Integer>();
		
		for (final LightResistor r : nearResistors) {
			
			firstPoint = true;
			index = -1;
			
			for (final Point2 p : r.getVerticles()) {
				
				++index;
				
				// let's move light source to the origin of 2-dimensional world.
				x = p.x - source.x;
				y = p.y - source.y;
				
				// get the angle
				angle = Vector2.angle(x, y);
				
				if (firstPoint) {
					// let's say for now that this is left-most and right-most point
					leftMostIndex = rightMostIndex = index;
					leftMostAngle = rightMostAngle = angle;
					
					firstPoint = false;
					
					continue;
				}
				
				// TODO: tutaj skończyłem
				// Mój pomysł jest taki, by śledzić w którym kierunku prowadzą kolejne
				// wierzchołki i w momencie zwrotnym zapamiętać wierzchołek jako
				// graniczny po odpowiedniej stronie. Tak mógłbym zapisać poprawnie
				// strony spirali i fakt jak wiele obrotów zrobiła. Najważniejszy
				// jest tylko pierwszy pełny obrót i najbardziej kłopotliwy
				
			}
		}
		
		return viewport;
	}

	/**
	 * Creates a list of resistors that can create shadow (they're in
	 * light distance).
	 */
	private List<LightResistor> determineNearResistors(final LightSource source) {
		
		final List<LightResistor> result = new LinkedList<LightResistor>();
		
		Box2 bbox;
		float diagonal;
		float distance, distanceMin;
		Point2 otherVerticles[] = new Point2[3];
		boolean foundOther;
		
		for (LightResistor r : resistors) {
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
			
			for (Point2 p : otherVerticles) {
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
