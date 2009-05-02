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

import java.util.HashSet;
import java.util.Set;

import pl.graniec.coralreef.geometry.Geometry;

/**
 * @author Piotr Korzuszek <piotr.korzuszek@gmail.com>
 *
 */
public abstract class AbstractLightingAlgorithm {
	/** All light resistors */
	protected final Set resistors = new HashSet();
	/** Number of parts of which light with no resistance should be build of */
	protected int partsNum = 32;
	
	public void addLightResistor(LightResistor resistor) {
		resistors.add(resistor);
	}
	
	public abstract Geometry createRays(LightSource source);
	
	/**
	 * See {@link #setPartsNum(int)}
	 * 
	 * @return Number of parts of light without resistance.
	 * 
	 */
	public int getPartsNum() {
		return partsNum;
	}
	
	/**
	 * Sets the number of parts that light geometry without any resistance
	 * should be build of. If there is resistors in light radius, then
	 * geometry will match to resistor geometry in occurrence position
	 * in order to simulate natural behavior of light.
	 * <p>
	 * By default number of parts is set to <code>32</code>.
	 * 
	 * @param partsNum The number of parts.
	 */
	public void setPartsNum(int partsNum) {
		this.partsNum = partsNum;
	} 
}
