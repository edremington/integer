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

package edu.harvard.integer.common.yaml.vendorcontainment;

import javax.persistence.ManyToOne;


/**
 * @author David Taylor
 * 
 */
public class YamlSnmpContainmentRelation extends YamlSnmpRelationship {
	@ManyToOne
	private String childTable = null;

	@ManyToOne
	private String mappingTable = null;

	@ManyToOne
	private String mappingOid = null;
	
	private String mappingType;

	private String valueIsParent = "false";


	public String getValueIsParent() {
		return valueIsParent;
	}

	public void setValueIsParent(String valueIsParent) {
		this.valueIsParent = valueIsParent;
	}

	public String getMappingType() {
		return mappingType;
	}

	public void setMappingType(String mappingType) {
		this.mappingType = mappingType;
	}

	/**
	 * @return the childTable
	 */
	public String getChildTable() {
		return childTable;
	}

	/**
	 * @param childTable
	 *            the childTable to set
	 */
	public void setChildTable(String childTable) {
		this.childTable = childTable;
	}

	/**
	 * @return the mappingTable
	 */
	public String getMappingTable() {
		return mappingTable;
	}

	/**
	 * @param mappingTable
	 *            the mappingTable to set
	 */
	public void setMappingTable(String mappingTable) {
		this.mappingTable = mappingTable;
	}

	/**
	 * @return the mappingOid
	 */
	public String getMappingOid() {
		return mappingOid;
	}

	/**
	 * @param mappingOid
	 *            the mappingOid to set
	 */
	public void setMappingOid(String mappingOid) {
		this.mappingOid = mappingOid;
	}

}
