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
package edu.harvard.integer.agent;

import edu.harvard.integer.agent.serviceelement.discovery.ElementDiscoveryBase;
import edu.harvard.integer.agent.serviceelement.discovery.NetworkDiscovery;
import edu.harvard.integer.agent.serviceelement.discovery.NetworkDiscoveryBase;
import edu.harvard.integer.agent.serviceelement.discovery.snmp.SNMPElementDiscover;

/**
 * A factory for creating AgentProvider objects which provide discovery, configuration,
 * event manager providers.
 *
 * @author dchan
 */
public class AgentProviderFactory {

	
	
	/**
	 * Define service element access type.  A service element can be accessed in different ways for
	 * different purposes.  For example, using SNMP to get information from an IP node SNMP agent.
	 * Using PUPPET to talk to PUPPET client or PUPPET master etc.
	 *
	 */
	public enum ElementAccessTypeE {
      
		SNMP, CISCOCLI, PUPPET, AWS
	}
	
	
	/**
	 * Get element discovery based on element access type.
	 * 
	 * @param accessType  -- Access type which associates a discover provider.  
	 * @return  -- An object used for element discovery,
	 */
	public static ElementDiscoveryBase  getElementDiscovery( ElementAccessTypeE accessType ) {
		
		switch (accessType) {
		 
		   case SNMP:			  
			   return SNMPElementDiscover.getInstance();

		    default:
			  break;
		}	
		return null;		
	}
	
	
	
	
	/**
	 * 
	 * Get network discover provider.  It is considering IP based network discover.
	 * 
	 * @return -- An object used for topo discovery.
	 */
	public static NetworkDiscoveryBase getNetworkDiscover() {
		
		return NetworkDiscovery.getInstance();
	}
}
