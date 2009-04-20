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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.graniec.coralreef.light2d.SimpleLightAlgorithm.Direction;

/**
 * @author Piotr Korzuszek <piotr.korzuszek@gmail.com>
 *
 */
public class SimpleLightAlgorithmTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link pl.graniec.coralreef.light2d.SimpleLightAlgorithm#getDirection(float, float)}.
	 */
	@Test
	public void testGetDirection() {
		assertEquals(Direction.Left, SimpleLightAlgorithm.getDirection(0, 45));
		assertEquals(Direction.Right, SimpleLightAlgorithm.getDirection(45, 0));
		
		assertEquals(Direction.Left, SimpleLightAlgorithm.getDirection(-45, 45));
		assertEquals(Direction.Right, SimpleLightAlgorithm.getDirection(45, -45));
		
		assertEquals(Direction.Left, SimpleLightAlgorithm.getDirection(170, -170));
		assertEquals(Direction.Right, SimpleLightAlgorithm.getDirection(-170, 170));
	}
	
	@Test
	public void testGetAngleDifference() {
		assertEquals(45f, SimpleLightAlgorithm.getAngleDifference(0, 45), 0.0001f);
		assertEquals(-45f, SimpleLightAlgorithm.getAngleDifference(45, 0), 0.0001f);
		
		assertEquals(90f, SimpleLightAlgorithm.getAngleDifference(-45, 45), 0.0001f);
		assertEquals(-90f, SimpleLightAlgorithm.getAngleDifference(45, -45), 0.0001f);
		
		assertEquals(20f, SimpleLightAlgorithm.getAngleDifference(170, -170), 0.0001f);
		assertEquals(-20f, SimpleLightAlgorithm.getAngleDifference(-170, 170), 0.0001f);
	}

}
