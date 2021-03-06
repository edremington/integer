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

import edu.harvard.integer.access.element.ElementEndPoint;
import edu.harvard.integer.common.discovery.VendorDiscoveryTemplate;
import edu.harvard.integer.common.exception.IntegerException;
import edu.harvard.integer.common.topology.ServiceElement;
import edu.harvard.integer.common.topology.ServiceElementManagementObject;
import edu.harvard.integer.service.discovery.subnet.DiscoverNode;

/**
 * This interface layout methods for discover IP based service element
 * which including SNMP elements and NON-SNMP elements.  Currently discovery
 * for an IP element is synchronized call.  It may changed in future.
 * 
 * @author dchan
 *
 */
public interface ElementDiscoveryBase {

	
	/**
	 * Discover element with a given node service element templete. It is considering a full discovery,
	 *  
	 * @param endEpt
	 * @param elementNode
	 * @param topoPattern
	 * @throws IntegerException
	 */
	public void discoverElement( VendorDiscoveryTemplate<ServiceElementManagementObject> template, 
			                     DiscoverNode disNode ) throws IntegerException;
	
	
	/**
	 * Scan element with a given service element. It is considering a full scan. However it should
	 * not have any new sub-component being added or deleted any sub-component during scan.  
	 *
	 * @param endEpt the end ept
	 * @param element the element
	 * @throws IntegerException the integer exception
	 */
	public void scanElement( ElementEndPoint endEpt, ServiceElement element ) throws IntegerException;
	
	
	/**
	 * Check alive for that element.  If no exception occurs, the element is reachable.
	 *
	 * @param endEpt the element endPoint.
	 * @throws IntegerException the integer exception
	 * @return -- The identify of the element node such as sysObjectID
	 */
	public String checkReachable( ElementEndPoint endEpt ) throws IntegerException;
	
	
	/**
	 * Used to stop discovery.
	 */
	public void stopDiscovery();
}
