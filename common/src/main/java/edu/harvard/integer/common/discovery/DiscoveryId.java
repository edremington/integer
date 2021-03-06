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

package edu.harvard.integer.common.discovery;

import java.io.Serializable;

import edu.harvard.integer.common.ID;

/**
 * @author David Taylor
 * 
 */
public class DiscoveryId implements Serializable {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	private Long serverId = null;

	private Long discoveryId = null;

	private ID discoveryRuleId = null;
	
	public DiscoveryId() {
	}
	
	/**
	 * @param serverId
	 * @param discoveryId
	 */
	public DiscoveryId(Long serverId, Long discoveryId) {
		super();
		this.serverId = serverId;
		this.discoveryId = discoveryId;
	}

	/**
	 * @return the serverId
	 */
	public Long getServerId() {
		return serverId;
	}

	/**
	 * @param serverId
	 *            the serverId to set
	 */
	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}

	/**
	 * @return the discoveryId
	 */
	public Long getDiscoveryId() {
		return discoveryId;
	}

	/**
	 * @param discoveryId
	 *            the discoveryId to set
	 */
	public void setDiscoveryId(Long discoveryId) {
		this.discoveryId = discoveryId;
	}

	/**
	 * @return the discoveryRuleId
	 */
	public ID getDiscoveryRuleId() {
		return discoveryRuleId;
	}

	/**
	 * @param discoveryRuleId the discoveryRuleId to set
	 */
	public void setDiscoveryRuleId(ID discoveryRuleId) {
		this.discoveryRuleId = discoveryRuleId;
	}

}
