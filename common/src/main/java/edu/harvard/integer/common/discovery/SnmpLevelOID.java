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

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;

import edu.harvard.integer.common.BaseEntity;
import edu.harvard.integer.common.snmp.SNMP;
import edu.harvard.integer.common.topology.Category;

/**
 * 
 * These objects are used to identify the OIDs starting at the top level of a
 * physical or virtual entity when the Entity MIB or other standard containment
 * model is not used.
 * 
 * <p>
 * There will always be one (potentially) many
 * SnmpServiceElementTypeDiscrininator instances associated with an instance of
 * this object. The Discriminator instances take the value returned from the
 * contextOid to determine the service element type.
 * 
 * @author David Taylor
 * 
 */
@Entity
public class SnmpLevelOID extends BaseEntity {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This is the OID whose value is retrieved from the target system. The
	 * discovery system uses this value - the container for this level. For
	 * example, this could be the device table.
	 */
	@ManyToOne
	private SNMP contextOID = null;

	/**
	 * The OID that whose return value is evaluated to determine the
	 * serviceElementType. For example in the HR Device table there is a device
	 * type object. That can return 4 different values. In this case there will
	 * be four SnmpServiceElementTypeDiscriminator instances associated with the
	 * Parent SnmpLevelOID that is telling the system to get the HR Device
	 * table.
	 * 
	 * Only one SnmpDiscriminatorOID instance is permitted for each SNMPLevelOID
	 */
	@ManyToOne
	private SNMP descriminatorOID = null;
	
	/**
	 * The global scalar OID to be used to indicate the technology capability of 
	 * the system to support the next level on the containment tree.  For example
	 * for Cisco cdp, this OID can be referred to cdpGlobalRun.  When this value is false,
	 * no service element related to cdp will be created.
	 */
	@ManyToOne
	private SNMP globalDiscriminatorOID = null;


	/**
	 * Some table may point to other tables that are contained in the higher
	 * level object. This list contains the oids for the objects at the next
	 * level down in the containment hierarchy. For example the device table may
	 * contain storage which may contain file systems.
	 */
	@ElementCollection(fetch=FetchType.EAGER)
	@OrderColumn(name = "idx")
	private List<SnmpLevelOID> children = null;

	/**
	 * List of SnmpServiceElementTypeDiscriptors for this SnmpLevel
	 */
	@ElementCollection(fetch=FetchType.EAGER)
	@OrderColumn(name = "idx")
	private List<SnmpServiceElementTypeDiscriminator> disriminators = null;
	
	
	/**
	 * List of SnmpAssociation for this SnmpLevel
	 */
	@ElementCollection(fetch=FetchType.EAGER)
	@OrderColumn(name = "idx")
	private List<SnmpAssociation> associations = null;
	


	/**
	 * SnmpContainmentRelation definition to map the ServiceElement from this level to a
	 * different ServiceElement. 
	 * <p>
	 * Ex. entAliasMappingTable. Maps the logical
	 * components and physical components to and external to the entity MIB
	 * object. entAliasMappingIdentifier.33.0 = ifIndex.6
	 */
	@ManyToOne
	private SnmpRelationship relationToParent = null;

	/**
	 * Category that this SnmpLevelOID is valid for. This will
	 * be null when not needed or not valid.
	 */
	@ManyToOne
	private Category category = null;
	
	
	
	
	
	/**
	 * @return the contextOID
	 */
	public SNMP getContextOID() {
		return contextOID;
	}

	/**
	 * @param contextOID
	 *            the contextOID to set
	 */
	public void setContextOID(SNMP contextOID) {
		this.contextOID = contextOID;
	}

	/**
	 * @return the descriminatorOID
	 */
	public SNMP getDescriminatorOID() {
		return descriminatorOID;
	}

	/**
	 * @param descriminatorOID
	 *            the descriminatorOID to set
	 */
	public void setDescriminatorOID(SNMP descriminatorOID) {
		this.descriminatorOID = descriminatorOID;
	}

	/**
	 * @return the children
	 */
	public List<SnmpLevelOID> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<SnmpLevelOID> children) {
		this.children = children;
	}

	/**
	 * @return the disriminators
	 */
	public List<SnmpServiceElementTypeDiscriminator> getDisriminators() {
		return disriminators;
	}

	/**
	 * @param disriminators
	 *            the disriminators to set
	 */
	public void setDisriminators(
			List<SnmpServiceElementTypeDiscriminator> disriminators) {
		this.disriminators = disriminators;
	}

	/**
	 * @return the relationToParent
	 */
	public SnmpRelationship getRelationToParent() {
		return relationToParent;
	}

	
	/**
	 * @param relationToParent the relationToParent to set
	 */
	public void setRelationToParent(SnmpRelationship relationToParent) {
		this.relationToParent = relationToParent;
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
	}
	

	/**
	 * 
	 * @return
	 */
	public SNMP getGlobalDiscriminatorOID() {
		return globalDiscriminatorOID;
	}

	/**
	 * 
	 * @param globalDescriminatorOID
	 */
	public void setGlobalDiscriminatorOID(SNMP globalDiscriminatorOID) {
		this.globalDiscriminatorOID = globalDiscriminatorOID;
	}


	public List<SnmpAssociation> getAssociations() {
		return associations;
	}

	public void setAssociations(List<SnmpAssociation> associations) {
		this.associations = associations;
	}
}
