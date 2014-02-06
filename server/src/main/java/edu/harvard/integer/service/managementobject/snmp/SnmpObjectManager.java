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

package edu.harvard.integer.service.managementobject.snmp;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import edu.harvard.integer.common.exception.IntegerException;
import edu.harvard.integer.common.snmp.MIBImportInfo;
import edu.harvard.integer.common.snmp.MIBImportResult;
import edu.harvard.integer.common.snmp.MIBInfo;
import edu.harvard.integer.common.snmp.SNMP;
import edu.harvard.integer.common.topology.Capability;
import edu.harvard.integer.service.managementobject.provider.snmp.MibParser;
import edu.harvard.integer.service.managementobject.provider.snmp.MibParserFactory;

/**
 * @author David Taylor
 *
 */
@Stateless
public class SnmpObjectManager implements SnmpObjectManagerLocalInterface {
	
	@Inject
	private MibLoaderLocalInterface mibLoader;

	@Inject
	private Logger logger;

	/* (non-Javadoc)
	 * @see edu.harvard.integer.service.managementobject.snmp.SnmpObjectManagerLocalInterface#importMib(java.lang.String)
	 */
	@Override
	public MIBImportResult[] importMib(MIBImportInfo[] mibFile) throws IntegerException {

		try {
			logger.info("Importing " + mibFile.length + " MIB's");
			for (MIBImportInfo mibImportInfo : mibFile) {
				String fileName = mibImportInfo.getFileName();
				int fileNameIdx = fileName.lastIndexOf('/');
				if (fileNameIdx > 0)
					fileName = fileName.substring(fileNameIdx + 1);
				
				fileNameIdx = fileName.lastIndexOf('\\');
				if (fileNameIdx > 0)
					fileName = fileName.substring(fileNameIdx + 1);
				
				mibImportInfo.setFileName(fileName);
				
				
				logger.info("MIB " + mibImportInfo.getFileName() + " Size " + mibImportInfo.getMib().length());
			}
			MibParser mibParser =  MibParserFactory.getParserSource(MibParserFactory.ParserProvider.MIBBLE);
			MIBImportResult[] results = mibParser.importMIB(mibFile, true);
			for (MIBImportResult result : results) {
				if (result.getErrors() == null || result.getErrors().length == 0)
					mibLoader.load(result);	
				else {
					logger.error("Error importing " + result.getFileName() + " Errors " + Arrays.toString(result.getErrors()));
				}
					
			}
			
			logger.info("Load of mibs complete!");	

			return results;
		}
    	catch (IntegerException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.integer.service.managementobject.snmp.SnmpObjectManagerLocalInterface#getImportedMibs()
	 */
	@Override
	public MIBInfo[] getImportedMibs() {
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.integer.service.managementobject.snmp.SnmpObjectManagerLocalInterface#getAllSNMPCapabilites()
	 */
	@Override
	public List<Capability> getAllSNMPCapabilites() {
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.integer.service.managementobject.snmp.SnmpObjectManagerLocalInterface#setSNMP(edu.harvard.integer.common.topology.Capability, edu.harvard.integer.common.topology.SNMP)
	 */
	@Override
	public Capability setSNMP(Capability capability, SNMP snmpObject) {
		
		return capability;
	}

}