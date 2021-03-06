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
package edu.harvard.integer.access.snmp;

import java.util.Arrays;

import org.snmp4j.mp.SnmpConstants;

import edu.harvard.integer.access.AccessTypeEnum;
import edu.harvard.integer.access.Authentication;
import edu.harvard.integer.common.ID;

/**
 * The Class SnmpSecureAccess contains SNMPv3 access information.
 *
 * @author dchan
 */
public class SnmpSecureAuth extends SnmpAuthentication {

	/** The security level. */
	private int securityLevel;
	
	/** The security model. */
	private int securityModel;
	
	/** The security name. */
	private String securityName;
	
	/** The engine id. */
	private byte[]  engineID;
	
	
	/**
	 * Gets the security level.
	 *
	 * @return the security level
	 */
	public int getSecurityLevel() {
		return securityLevel;
	}
	
	
	/**
	 * Sets the security level.
	 *
	 * @param securityLevel the new security level
	 */
	public void setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
	}
	
	/**
	 * Gets the security model.
	 *
	 * @return the security model
	 */
	public int getSecurityModel() {
		return securityModel;
	}
	
	/**
	 * Sets the security model.
	 *
	 * @param securityModel the new security model
	 */
	public void setSecurityModel(int securityModel) {
		this.securityModel = securityModel;
	}
	
	/**
	 * Gets the security name.
	 *
	 * @return the security name
	 */
	public String getSecurityName() {
		return securityName;
	}
	
	/**
	 * Sets the security name.
	 *
	 * @param securityName the new security name
	 */
	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}
	

	/**
	 * Gets the engine id.
	 *
	 * @return the engine id
	 */
	public byte[] getEngineID() {
		return engineID;
	}

	/**
	 * Sets the engine id.
	 *
	 * @param engineID the new engine id
	 */
	public void setEngineID(byte[] engineID) {
		this.engineID = engineID;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.integer.agent.serviceelement.Access#getAccessType()
	 */
	@Override
	public AccessTypeEnum getAccessType() {
		return AccessTypeEnum.SNMPv3;
	}


	/* (non-Javadoc)
	 * @see edu.harvard.integer.access.snmp.SnmpAuthentication#getSnmpVersion()
	 */
	@Override
	public int getSnmpVersion() {
		
		return SnmpConstants.version3;
	}


	/* (non-Javadoc)
	 * @see edu.harvard.integer.access.Authentication#isSame(edu.harvard.integer.access.Authentication)
	 */
	@Override
	public boolean isSame(Authentication auth) {
		
		if ( auth instanceof SnmpSecureAuth ) {
			
			SnmpSecureAuth sauth = (SnmpSecureAuth) auth;
			if ( Arrays.equals(getEngineID(), sauth.engineID ) && sauth.securityLevel == getSecurityLevel() 
					&& sauth.securityModel == getSecurityModel() && sauth.securityName.equals(getSecurityName()) ) {
				return true;
			}
		}
		return false;
	}


	/* 
	 * No SNMPV3 Credential implemented yet.
	 *  
	 * (non-Javadoc)
	 * @see edu.harvard.integer.access.Authentication#getCredentailID()
	 */
	@Override
	public ID getCredentailID() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
