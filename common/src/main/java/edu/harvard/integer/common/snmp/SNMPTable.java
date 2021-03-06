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

package edu.harvard.integer.common.snmp;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;

/**
 * @author David Taylor
 * 
 *         This class holds the definition of a MIB Table.
 * 
 */
@Entity
public class SNMPTable extends SNMP implements Serializable {
	/**
	 * Serialization version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * List of index OID's for this table.
	 * 
	 */
	@ManyToMany(fetch=FetchType.EAGER)
	@OrderColumn(name = "tableIndexIdx")
	@CollectionTable(name = "SNMPTable_Index")
	private List<SNMP> index = null;

	/**
	 * 
	 */
	@ManyToMany(fetch=FetchType.EAGER)
	@OrderColumn(name = "tableOidIdx")
	@CollectionTable(name = "SNMPTable_OIDS")
	private List<SNMP> tableOids = null;

	/**
	 * @return the index
	 */
	public List<SNMP> getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(List<SNMP> index) {
		this.index = index;
	}

	/**
	 * @return the tableOids
	 */
	public List<SNMP> getTableOids() {
		return tableOids;
	}

	/**
	 * @param tableOids
	 *            the tableOids to set
	 */
	public void setTableOids(List<SNMP> tableOids) {
		this.tableOids = tableOids;
	}

}
