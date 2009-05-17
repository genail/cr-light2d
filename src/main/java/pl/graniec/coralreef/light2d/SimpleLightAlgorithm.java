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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

	static class AngledPoint extends Point2 implements Comparable {

		final float angle;
		
		public AngledPoint(final float x, final float y) {
			super(x, y);
			this.angle = Vector2.angle(x, y);
		}

		public int compareTo(Object obj) {
			if (getClass() != obj.getClass()) {
				throw new ClassCastException("cannnot cast " + obj.getClass() + " to " + getClass());
			}
			
			final AngledPoint other = (AngledPoint) obj;
			
			if (angle < other.angle) {
				return -1;
			} else if (angle > other.angle) {
				return +1;
			} else {
				return 0;
			}
		}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			AngledPoint other = (AngledPoint) obj;
			if (Float.floatToIntBits(angle) != Float
					.floatToIntBits(other.angle))
				return false;
			return true;
		}

		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + Float.floatToIntBits(angle);
			return result;
		}

		/*
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return getClass().getSimpleName() + "[x=" + x + ",y=" + y + ",angle=" + angle + "]";
		}
	}
	
//	private static final class ResistorSegment extends Segment {
//		
////		final LightResistor resistor;
//
//		public ResistorSegment(final LightResistor resistor, float x1, float y1, float x2, float y2) {
//			super(x1, y1, x2, y2);
////			this.resistor = resistor;
//		}
//		
//	}
	
	public static final class Direction {
		public static final int None = 1;
		public static final int Left = 2;
		public static final int Right = 3;
		
		private Direction() {
		}
	}
	
	/**
	 * Segment point that can tell the angle on which it residents.
	 *
	 */
	static final class ViewportPoint extends AngledPoint {

		final Segment segment;
		ViewportPoint other;
		
		int hash = -1;

		public ViewportPoint(final Segment segment, float x, float y) {
			super(x, y);
			this.segment = segment;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			ViewportPoint other = (ViewportPoint) obj;
			if (this.other == null) {
				if (other.other != null)
					return false;
			} else if (!this.other.equals(other.other))
				return false;
			if (segment == null) {
				if (other.segment != null)
					return false;
			} else if (!segment.equals(other.segment))
				return false;
			return true;
		}

		public int hashCode() {
			
			if (hash == -1) {
				final int prime = 31;
				hash = super.hashCode();
				hash = prime * hash
						+ ((segment == null) ? 0 : segment.hashCode());
			}
			
			return hash;
		}

		
		
	}
	
	/**
	 * Translates all given <code>resistors</code> to segments.
	 * 
	 * @param resistors The resistors to translate.
	 * 
	 * @return Segments of all resistors.
	 */
	static final List/*<Segment>*/ buildSegments(final List/*<LightResistor>*/ resistors, final LightSource source) {
		final List/*<Segment>*/ result = new LinkedList();
		
		for (final Iterator itor = resistors.iterator(); itor.hasNext();) {
			final LightResistor resistor = (LightResistor) itor.next();
			final Point2[] verticles = resistor.getVerticles();
			
			Point2 first = null, prev, next = null;
			
			for (int i = 0; i < verticles.length; ++i) {
				prev = next;
				next = verticles[i];
				
				if (first == null) {
					first = next;
				}
				
				if (prev != null) {
					result.add(new Segment(prev, next));
				}
			}
			
			if (verticles.length >= 3) {
				result.add(new Segment(next, first));
			}
		}
		
		return result;
	}

	static final List/*<ViewportPoint>*/ buildViewport(final LightSource source, final List/*<Segments>*/ segments) {
		
		// build viewport
		final List/*<Float, ViewportPoint>*/ viewport = new LinkedList();
		
		// get all points from segments
		for (final Iterator itor = segments.iterator(); itor.hasNext();) {
			final Segment segment = (Segment) itor.next();
			
			final ViewportPoint point1 = new ViewportPoint(segment, segment.x1, segment.y1);
			final ViewportPoint point2 = new ViewportPoint(segment, segment.x2, segment.y2);
			
			point1.other = point2;
			point2.other = point1;
			
			viewport.add(point1);
			viewport.add(point2);
		}
		
		return viewport;
		
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
	
	static final boolean isVisible(final ViewportPoint point, final List viewport, final List/*ViewportPoint*/ openActions) {
		// make copy of actions
		final Set/*<ViewportPoint>*/ actions = new HashSet(openActions);
		
		for (final Iterator itor = viewport.iterator(); itor.hasNext();) {
			final ViewportPoint vp = (ViewportPoint) itor.next();

			
			if (!actions.add(vp)) {
				System.err.println("" + vp + " already in actions list");
				continue;
			}
			
			if (vp.angle > point.angle) {
				// this is the break point
				// where intersection should be checked
				break;
			}
			
			actions.remove(vp.other);
			
		}
		
		// check intersection with all segments
		final Segment checkedSegment = new Segment(0, 0, point.x, point.y);
		
		for (final Iterator itor = actions.iterator(); itor.hasNext();) {
			final Segment otherSegment = ((ViewportPoint) itor.next()).segment;
			
			if (checkedSegment.intersects(otherSegment)) {
				return false;
			}
		}
		
		return true;
	}
	
	
	/*
	 * @see pl.graniec.coralreef.light2d.AbstractLightingAlgorithm#createRays(pl.graniec.coralreef.light2d.LightSource)
	 */
	public Geometry createRays(final LightSource source) {
		
		// build resistors list that can make the shadow (its near light source)
		final List/*<LightResistor>*/ nearResistors = determineNearResistors(source);
		
		// build segments from this resistors
		final List/*<Segment>*/ segments = buildSegments(nearResistors, source);
		
		// translate them to be relative to light source
		makeRelative(segments, source);
		
		// expand all segments to prevent possible holes (calculation inaccuracy)
		expandSegments(segments);
		
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
		
		final List viewport/*<ViewportPoint>*/ = buildViewport(source, segments);
		
		// its very important to have this viewport sorted
		Collections.sort(viewport);
		
		// get the actions that are open on 180 angle
		final List/*<ViewportPoint>*/ startActions = getStartActions(segments, source, viewport); 
		
		// go thru all points and create a light geometry
		// but first create a hash set to remove duplicates
		final Set/*<AngledPoint>*/ points = new HashSet();
		final float delta = 360f / partsNum;
		float position = -180f;
		
		for (final Iterator itor = viewport.iterator(); itor.hasNext();) {
			final ViewportPoint point = (ViewportPoint) itor.next();
		
			// non-resistance rays
			while (point.angle - position > delta) {
				position += delta;
				tryPoint(position, source, points, viewport, startActions);
			}
			
			if (isVisible(point, viewport, startActions)) {
				tryPoint(point.angle - 0.01f, source, points, viewport, startActions);
				points.add(new AngledPoint(point.x, point.y));
				tryPoint(point.angle + 0.01f, source, points, viewport, startActions);
			}
			
			position = point.angle;
		}
		
		// end non-resistance rays
		while (position + delta <= 180f) {
			position += delta;
			tryPoint(position, source, points, viewport, startActions);
		}
		
		final Geometry light = new Geometry();
		
		final List pointList/*<AngledPoint>*/ = new LinkedList(points);
		Collections.sort(pointList);
		
		for (final Iterator itor = pointList.iterator(); itor.hasNext();) {
			final Point2 p = (Point2) itor.next();
			light.addVerticle(new Point2(p.x + source.x, p.y + source.y));
		}
		
		return light;
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
				continue;
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

	private void expandSegments(final List/*<Segment>*/ segments) {
		for (final Iterator itor = segments.iterator(); itor.hasNext();) {
			final Segment segment = (Segment) itor.next();
			segment.resize(1.01f);
		}
	}

	/**
	 * Gets the segment point <code>point1</code> from viewport.
	 * The segment must be build from <code>point1</code> to <code>point2</code>
	 * but only the first one is wanted.
	 */
	private ViewportPoint getPoint(final Point2 point1, final Point2 point2, final List viewport) {
		for (final Iterator itor = viewport.iterator(); itor.hasNext();) {
			final ViewportPoint vp = (ViewportPoint) itor.next();
			final Segment segment = vp.segment;
			
			if (
					point1.x == segment.x1 &&
					point1.y == segment.y1 &&
					point2.x == segment.x2 &&
					point2.y == segment.y2
					||
					point2.x == segment.x1 &&
					point2.y == segment.y1 &&
					point1.x == segment.x2 &&
					point1.y == segment.y2
					) {
				return new ViewportPoint(segment, point1.x, point1.y);
			}
			
			
		}
		
		throw new RuntimeException("Point " + point1 + " and " + point2 + " as a segment not found");
	}

	private List/*<ViewportPoint>*/ getStartActions(final List/*<Segment*/ segments, final LightSource source, final List/*<ViewportPoint>*/ viewport) {
		
		final List/*<ViewportPoint>*/ startActions = new LinkedList();
		
		final Segment borderSegment = new Segment(0, 0, -source.intensity, 0);
		
		for (final Iterator itor = segments.iterator(); itor.hasNext();) {
			final Segment segment = (Segment) itor.next();
			
			if (segment.intersects(borderSegment)) {
				// if one point is on border segment and the other
				// on the positive y half, then ignore this intersection
				if (
						segment.y1 == 0 && -segment.x1 <= source.intensity && segment.y2 >= 0 ||
						segment.y2 == 0 && -segment.x2 <= source.intensity && segment.y1 >= 0
						) {
					continue;
				}
				
				// got intersection. Get point with y >= 0
				ViewportPoint vp;
				
				if (segment.y1 >= 0) {
					vp = getPoint(new Point2(segment.x1, segment.y1), new Point2(segment.x2, segment.y2), viewport);
				} else {
					vp = getPoint(new Point2(segment.x2, segment.y2), new Point2(segment.x1, segment.y1), viewport);
				}
				
				startActions.add(vp);
			}
			
			// check also if there are points that lies on the border segment
			// then this can be a open actions too
			else if (segment.y1 == 0 && segment.x1 < 0 && -segment.x1 <= source.intensity && segment.y2 < 0) {
				startActions.add(getPoint(new Point2(segment.x1, segment.y1), new Point2(segment.x2, segment.y2), viewport));
			}
			else if (segment.y2 == 0 && segment.x2 < 0 && -segment.x2 <= source.intensity && segment.y1 < 0) {
				startActions.add(getPoint(new Point2(segment.x2, segment.y2), new Point2(segment.x1, segment.y1), viewport));
			}
		}
		
		return startActions;
	}

	private void makeRelative(final List segments, final LightSource source) {
		for (final Iterator itor = segments.iterator(); itor.hasNext();) {
			final Segment segment = (Segment) itor.next();
			
			segment.x1 -= source.x;
			segment.x2 -= source.x;
			segment.y1 -= source.y;
			segment.y2 -= source.y;
		}
	}

	private void tryPoint(
			final float angle,
			final LightSource source,
			final Set/*<Point2>*/ points,
			final List/*<ViewportPoint>*/ viewport,
			final List/*<ViewportPoint>*/ openActions) {
	
		final float rad = (float) Math.toRadians(angle);
		
		final float x = (float) Math.cos(rad) * source.intensity;
		final float y = (float) Math.sin(rad) * source.intensity;
		
		final ViewportPoint point = new ViewportPoint(null, x, y);
		
		if (isVisible(point, viewport, openActions)) {
			points.add(new AngledPoint(point.x, point.y));
		}
	}

}
