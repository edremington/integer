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

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Level;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import edu.harvard.integer.access.snmp.CommonSnmpOids;
import edu.harvard.integer.common.discovery.DiscoveryParseElement;
import edu.harvard.integer.common.discovery.DiscoveryParseElementTypeEnum;
import edu.harvard.integer.common.discovery.DiscoveryParseString;
import edu.harvard.integer.common.discovery.SnmpVendorDiscoveryTemplate;
import edu.harvard.integer.common.exception.IntegerException;
import edu.harvard.integer.common.snmp.SNMP;
import edu.harvard.integer.common.topology.ServiceElementManagementObject;
import edu.harvard.integer.service.discovery.ServiceElementDiscoveryManagerInterface;
import edu.harvard.integer.service.distribution.DistributionManager;
import edu.harvard.integer.service.distribution.ManagerTypeEnum;
import edu.harvard.integer.service.managementobject.snmp.SnmpManagerInterface;
import edu.harvard.integer.service.persistance.PersistenceManagerInterface;
import edu.harvard.integer.service.persistance.dao.snmp.SNMPDAO;

/**
 * @author David Taylor
 *
 */
@RunWith(Arquillian.class)
public class ServiceElementDiscoveryManagerTest {


	@Inject
	private Logger logger;
	
	@Inject 
	private ServiceElementDiscoveryManagerInterface serviceElementDiscoveryManger;
	
	@Inject
	private SnmpManagerInterface snmpMaager;
	
	@Deployment
	public static Archive<?> createTestArchive() {
		return ShrinkWrap
				.create(WebArchive.class, "ServiceElementDiscoveryManagerTest.war")
				.addPackages(true, "edu.harvard.integer")
				.addPackages(true, "net.percederberg")
				.addPackages(true, "org.apache.commons")
				.addPackages(true, "org.snmp4j")
				.addPackages(true, "uk.co.westhawk.snmp")
				//.addPackages(true, "org.jboss")
				//.addPackages(true, "org.wildfly")
				.addPackages(true, "org.xnio")
				.addAsResource("META-INF/test-persistence.xml",
						"META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				// Deploy our test data source
				.addAsWebInfResource("test-ds.xml");
	}
	
	@Before
	public void initializeLogger() {
		//BasicConfigurator.configure();
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	private SNMP createOid(String name, String oidString) {
		SNMP oid = new SNMP();
		oid.setName(name);
		oid.setDisplayName(name);
		oid.setOid(oidString);
		
		return oid;
	}
	
	@Test
	public void getTopLevelPolls() {
		
		System.out.println("GetToplLevelPolls");
		
		List<ServiceElementManagementObject> topLevelPolls = serviceElementDiscoveryManger.getTopLevelPolls();
		
		assert(topLevelPolls != null);
		
		if (topLevelPolls.size() == 0) {
			// Running with H2 db. so need to create the data.

			try {
				snmpMaager.updateSNMP(createOid("sysName", CommonSnmpOids.sysName));
				snmpMaager.updateSNMP(createOid("sysDescr", CommonSnmpOids.sysDescr));
				snmpMaager.updateSNMP(createOid("sysLocation", CommonSnmpOids.sysLocation));
				snmpMaager.updateSNMP(createOid("sysObjectID", CommonSnmpOids.sysObjectID));
				
				topLevelPolls = serviceElementDiscoveryManger.getTopLevelPolls();
				
			} catch (IntegerException e) {
				
				e.printStackTrace();
				fail(e.toString());
			}
			
		}
		
		assert(topLevelPolls.size() > 0);
		
		for (ServiceElementManagementObject serviceElementManagementObject : topLevelPolls) {
			
			logger.info("Got " + serviceElementManagementObject.getName() + " " 
					+ serviceElementManagementObject.getDisplayName());
		}
		
	}
	
	@Test
	public void createSnmpVendorDiscoveryTemplate() {
		
		SnmpManagerInterface snmpManager = null;
		try {
			snmpManager = DistributionManager.getManager(ManagerTypeEnum.SnmpManager);
		} catch (IntegerException e) {
			
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		SnmpVendorDiscoveryTemplate template = new SnmpVendorDiscoveryTemplate();
		template.setVendor("Cisco");
		try {
			template.setModel(snmpManager.getSNMPByOid(CommonSnmpOids.sysObjectID));
		} catch (IntegerException e) {
			
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		template.setDescription("Cisco Snmp Vendor template");
		DiscoveryParseString parseString = new DiscoveryParseString();
		parseString.setName("Cisco SysDescr parse string");
		
		List<DiscoveryParseElement> elements = new ArrayList<DiscoveryParseElement>();
		
		DiscoveryParseElement element = new DiscoveryParseElement();
		element.setParseElementType(DiscoveryParseElementTypeEnum.FirmwareVersion);
		element.setParseElement("Cisco IOS Software,");
		element.setName("Firmware");
		elements.add(element);
		
		element = new DiscoveryParseElement();
		element.setParseElementType(DiscoveryParseElementTypeEnum.SoftwareVersion);
		element.setParseElement(", Version");
		element.setName("Software");
		elements.add(element);
		
		parseString.setParseStrings(elements);
		template.setParseString(parseString);
		
		try {
			template = serviceElementDiscoveryManger.updateSnmpVendorDiscoveryTemplate(template);
			
			logger.info("Created SnmpVendorDiscoveryTemplate " + template);
		} catch (IntegerException e) {
		
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	public void loadAllSnmpVendorTemplates() {
		try {
			SnmpVendorDiscoveryTemplate[] templates = serviceElementDiscoveryManger.getAllSnmpVendorDiscoveryTemplates();
			assert (templates != null);
			assert (templates.length > 0);
			
			logger.info("Found " + templates.length + " SnmpVendorTempaltes");
			
		} catch (IntegerException e) {

			e.printStackTrace();
			fail(e.toString());
		}
	}
}