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
package edu.harvard.integer.discovery;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;

import edu.harvard.integer.access.element.ElementEndPoint;
import edu.harvard.integer.access.snmp.CommonSnmpOids;
import edu.harvard.integer.access.snmp.CommunityAuth;
import edu.harvard.integer.access.snmp.SnmpService;
import edu.harvard.integer.common.exception.IntegerException;
import edu.harvard.integer.common.snmp.SnmpV2cCredentail;

/**
 * @author dchan
 *
 */
public class EntityMibTest {
	private Logger logger = LoggerFactory.getLogger(EntityMibTest.class);
	@Test
	public void retrieveTable() {
		
		SnmpV2cCredentail snmpV2c = new SnmpV2cCredentail();
		snmpV2c.setReadCommunity("integerrw");
		snmpV2c.setWriteCommunity("integerrw");
		
		OID[] eOid = new OID[5];
		eOid[0] = new OID(CommonSnmpOids.entPhysicalClass);
		eOid[1] = new OID(CommonSnmpOids.entPhysicalContainedIn);
		eOid[2] = new OID(CommonSnmpOids.entPhysicalDescr);
		eOid[3] = new OID(CommonSnmpOids.entPhysicalModelName);
		eOid[4] = new OID(CommonSnmpOids.entPhysicalVendorType);
		
		CommunityAuth ca = new CommunityAuth(snmpV2c);
		
		String hostAddress = "10.240.127.121";
		ElementEndPoint ePoint = new ElementEndPoint( hostAddress, 161, ca);
		
		try {
			SnmpService.instance().getTablePdu(ePoint, eOid);
		} catch (IntegerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e) {
			logger.error("Unable to connect to " + hostAddress);
		}
		
		System.out.println("Done with snmp table get ........ ");
	}
	
	
}
