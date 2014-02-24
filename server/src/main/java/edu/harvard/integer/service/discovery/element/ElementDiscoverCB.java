/*
 *  Copyright (c) 2014 Harvard University and the persons
 *  identified as authors of the code.  All rights reserved. 
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 * 	.    Redistributions of source code must retain the above copyright
 * 		 notice, this list of conditions and the following disclaimer.
 * 
 * 	.    Redistributions in binary form must reproduce the above copyright
 * 		 notice, this list of conditions and the following disclaimer in the
 * 		 documentation and/or other materials provided with the distribution.
 * 
 * 	.    Neither the name of Harvard University, nor the names of specific
 * 		 contributors, may be used to endorse or promote products derived from
 * 		 this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *      
 */
package edu.harvard.integer.service.discovery.element;

import edu.harvard.integer.common.exception.NetworkErrorCodes;
import edu.harvard.integer.common.topology.ServiceElement;
import edu.harvard.integer.service.discovery.TopoNetwork;

/**
 * The Interface ElementDiscoverCB defines callback methods during discovery.
 * This interface can be applied to a subnet discover or discover a list network node.
 * 
 * Note the discover is also including a topology discover.
 * 
 *
 * @author dchan
 * @param <T> the generic type of ServiceElement
 */
public interface ElementDiscoverCB <T extends ServiceElement> {

	/**
	 * Discovered topo network.  This method will be called after discover each topo network.
	 *
	 * @param tb the discovered topoNetwork.
	 */
	public void discoveredTopoNet( TopoNetwork tb );
	
	/**
	 * Discovered element. -- Be called after discovered each service element.
	 *
	 * @param elm the discoverd element.
	 */
	public void discoveredElement( T elm );
	
	/**
	 * Use to notify for progress.
	 * @param msg
	 */
	public void progressNotification( String msg );
	
	/**
	 * Error occur -- Call when errors occurs during discovering.
	 *
	 * @param errorCode the error code.
	 * @param msg the associated message.
	 */
	public void errorOccur( NetworkErrorCodes errorCode, String msg );
	
	/**
	 * Done. To be called after done with discovered.
	 */
	public void doneDiscover();
	
}
