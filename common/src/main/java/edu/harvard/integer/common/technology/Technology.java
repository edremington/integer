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

package edu.harvard.integer.common.technology;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OrderColumn;
import javax.validation.constraints.Size;

import edu.harvard.integer.common.BaseEntity;
import edu.harvard.integer.common.ID;
import edu.harvard.integer.common.topology.DiscoveryTypeEnum;
import edu.harvard.integer.common.topology.LayerTypeEnum;

/**
 * This class which can contain parents and children is used to organize details
 * of a particular technology. For example in the routing technology area, BGP
 * might be a mail element followed by information about neighbors, and then
 * more detailed information about them. Users can define as many
 * levels/refinements as they find useful. Some technologies will have very
 * short hierarchies. For example specific technologies for redundancy of one
 * type or another (e.g., RAID or VRRP).
 * 
 * @author David Taylor
 * 
 */
@Entity
public class Technology extends BaseEntity {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A short description of the ServiceTechnology
	 */
	@Size(min=1, max=5000)
	private String description = null;


	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "identifier", column = @Column(name = "parentId")),
			@AttributeOverride(name = "idType.classType", column = @Column(name = "parentType")),
			@AttributeOverride(name = "name", column = @Column(name = "parentName")) })
	private ID parentId = null;
	
	private Boolean hasChildren = null;
	
	/**
	 * Mechanisms for this technology. The technology can only have mechanisms
	 * for the lowest technology. So the technologies list must be empty to have
	 * mechanisms
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = "idx")
	private List<ID> mechanisims = null;

	@Enumerated(EnumType.STRING)
	private LayerTypeEnum layer = null;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = "idx")	
	private List<DiscoveryTypeEnum> discoveryTypes = null;
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = checkLentght(description, 5000);
	}

	/**
	 * @return the parentId
	 */
	public ID getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(ID parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the hasChildren
	 */
	public Boolean getHasChildren() {
		return hasChildren;
	}

	/**
	 * @param hasChildren the hasChildren to set
	 */
	public void setHasChildren(Boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	/**
	 * @return the mechanisims
	 */
	public List<ID> getMechanisims() {
		return mechanisims;
	}

	/**
	 * @param mechanisims
	 *            the mechanisims to set
	 */
	public void setMechanisims(List<ID> mechanisims) {
		this.mechanisims = mechanisims;
	}

	/**
	 * @return the layer
	 */
	public LayerTypeEnum getLayer() {
		return layer;
	}

	/**
	 * @param layer the layer to set
	 */
	public void setLayer(LayerTypeEnum layer) {
		this.layer = layer;
	}

	/**
	 * @return the discoveryTypes
	 */
	public List<DiscoveryTypeEnum> getDiscoveryTypes() {
		return discoveryTypes;
	}

	/**
	 * @param discoveryTypes the discoveryTypes to set
	 */
	public void setDiscoveryTypes(List<DiscoveryTypeEnum> discoveryTypes) {
		this.discoveryTypes = discoveryTypes;
	}

}
