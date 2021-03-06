/*
 *  Copyright (c) 2013 Harvard University and the persons
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
package edu.harvard.integer.common;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

/**
 * This class is used to pass the identifier of a data object in the Integer
 * system. The name and type are included so the GUI can display the object
 * correctly and allows for a common way to ask for an object.
 * 
 * @author David Taylor
 * 
 */
@Embeddable
public class ID implements IDInterface, Serializable {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	private Long identifier = null;

	@Size(min = 1, max = 50)
	private String name = null;

	private IDType idType = null;

	public ID() {
		super();
		idType = null;
	}

	/**
	 * @param identifier2
	 * @param name2
	 * @param idType2
	 */
	public ID(Long identifier, String name, IDType idType) {
		this.identifier = identifier;
		this.name = name;
		this.idType = idType;
	}

	/**
	 * @return the identifier
	 */
	public Long getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the idType
	 */
	public IDType getIdType() {
		return idType;
	}

	/**
	 * @param idType
	 *            the idType to set
	 */
	public void setIdType(IDType idType) {
		this.idType = idType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		if (identifier != null)
			return identifier.hashCode();
		else if (name != null)
			return name.hashCode();
		else
			return 1;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ID))
			return false;

		ID other = (ID) obj;

		if (identifier != null)
			return identifier.equals(other.getIdentifier());
		else {
			if (other.getIdentifier() != null)
				return false;
			else
				return true;
		}

	}

	@Override
	public String toString() {
		if (name != null)
			return name;

		if (identifier != null)
			return idType + ":" + identifier;

		return super.toString();
	}

	public String toDebugString() {
		StringBuffer b = new StringBuffer();

		if (getName() != null) {
			b.append(getName());
		}

		b.append('(').append(getIdType()).append(':');
		b.append(getIdentifier()).append(')');

		return b.toString();
	}

}
