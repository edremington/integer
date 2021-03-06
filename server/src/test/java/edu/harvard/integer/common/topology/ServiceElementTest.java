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

package edu.harvard.integer.common.topology;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import edu.harvard.integer.common.TestUtil;
import edu.harvard.integer.common.exception.IntegerException;
import edu.harvard.integer.common.managementobject.ManagementObjectIntegerValue;
import edu.harvard.integer.common.managementobject.ManagementObjectValue;
import edu.harvard.integer.common.snmp.SNMP;
import edu.harvard.integer.service.managementobject.snmp.SnmpManagerInterface;
import edu.harvard.integer.service.topology.device.ServiceElementAccessManagerInterface;

/**
 * @author David Taylor
 * 
 */
@RunWith(Arquillian.class)
public class ServiceElementTest {
	@Inject
	private ServiceElementAccessManagerInterface serviceElementManager;

	@Inject
	private Logger logger;
	
	@Inject
	private SnmpManagerInterface snmpMaager;
	
	@Deployment
	public static Archive<?> createTestArchive() {
		return TestUtil.createTestArchive("ServiceElementTest.war");
	}

	@Before
	public void initializeLogger() {
		//BasicConfigurator.configure();
	}

	@Test
	public void addServiceElement() {

		ServiceElement serviceElement = createServiceElement();
		
		ManagementObjectIntegerValue intValue = new ManagementObjectIntegerValue();
		SNMP oid = TestUtil.createOid("MO Name", "1.2.3.4.5");
		try {
			oid = snmpMaager.updateSNMP(oid);
		} catch (IntegerException e) {
			e.printStackTrace();
			fail(e.toString());
		}
		intValue.setManagementObject(oid.getID());
		intValue.setValue(Integer.valueOf(2));
		intValue.setName("MO name value");
		List<ManagementObjectValue> values = new ArrayList<ManagementObjectValue>();
		values.add(intValue);
		serviceElement.setAttributeValues(values);
		
		try {
			serviceElementManager.updateServiceElement(serviceElement);

		} catch (IntegerException e) {
			e.printStackTrace();

			fail(e.toString());
		}

	}
	
	public static ServiceElement createServiceElement() {
		ServiceElement serviceElement = new ServiceElement();
		serviceElement.setName("My ServiceElement");
		serviceElement.setDescription("A description");

		ServiceElementProtocolInstanceIdentifier identifier = new ServiceElementProtocolInstanceIdentifier();
		List<FCAPSEnum> fcaps = new ArrayList<FCAPSEnum>();
		fcaps.add(FCAPSEnum.Configuration);
		fcaps.add(FCAPSEnum.Fault);

		// identifier.setFcaps(fcaps);
		identifier.setValue("1");

		List<ServiceElementProtocolInstanceIdentifier> ids = new ArrayList<ServiceElementProtocolInstanceIdentifier>();
		ids.add(identifier);
		serviceElement.setValues(ids);

		return serviceElement;
	}

	@Test
	public void getAllServiceElements() {

		try {
			ServiceElement[] serviceElements = serviceElementManager
					.getAllServiceElements();
			logger.info("Found " + serviceElements.length + " ServiceElements");

			for (ServiceElement serviceElement : serviceElements) {
				logger.info("ServiceElement " + serviceElement.getID() + " "
						+ serviceElement.getDescription());

				assert (serviceElement.getValues() != null);

				logger.info("ServiceElement has "
						+ serviceElement.getValues().size() + " Values");

				for (ServiceElementProtocolInstanceIdentifier value : serviceElement
						.getValues()) {
					logger.info("Value " + value.getValue());
				}
			}
		} catch (IntegerException e) {

			e.printStackTrace();

			fail(e.toString());
		}
	}

	@Test
	public void getAllTopLevelServiceElements() {
		try {
			ServiceElement[] serviceElements = serviceElementManager.getTopLevelServiceElements();
			if (serviceElements == null || serviceElements.length == 0) {
				addServiceElement();
				serviceElements = serviceElementManager.getTopLevelServiceElements();
			}
			
			assert (serviceElements != null);

			logger.info("found " + serviceElements.length + " top level service elements");
			
			assert (serviceElements.length > 0);
			
		} catch (IntegerException e) {
			e.printStackTrace();

			fail(e.toString());
		}
	}

	@Test
	public void getDeviceDetails() {
		try {
			ServiceElement[] serviceElements = serviceElementManager.getTopLevelServiceElements();
			if (serviceElements == null || serviceElements.length == 0) {
				addServiceElement();
				serviceElements = serviceElementManager.getTopLevelServiceElements();
			}
			
			assert (serviceElements != null);

			logger.info("found " + serviceElements.length + " top level service elements");
			
			assert (serviceElements.length > 0);
			
			for (ServiceElement serviceElement : serviceElements) {
				DeviceDetails deviceDetails = serviceElementManager.getDeviceDetails(serviceElement.getID());
				logger.info("DeviceDetails " + deviceDetails.getServiceElementId()
						+ " " + deviceDetails.getDescription()
						+ " " + deviceDetails.getCreated()
						+ " " + deviceDetails.getUpdated()
						+ " " + deviceDetails.getPrimaryLocation()
						+ " " + deviceDetails.getComment());
						
			}
		} catch (IntegerException e) {
			e.printStackTrace();

			fail(e.toString());
		}
		
	}
	
	@Test
	public void getServiceElementByID() {
		try {
			ServiceElement[] serviceElements = serviceElementManager.getAllServiceElements();
			if (serviceElements == null || serviceElements.length == 0) {
				addServiceElement();
				serviceElements = serviceElementManager.getAllServiceElements();
			}
			
			assert (serviceElements != null);

			logger.info("found " + serviceElements.length + " top level service elements");
			
			assert (serviceElements.length > 0);
			
		} catch (IntegerException e) {
			e.printStackTrace();

			fail(e.toString());
		}
	}
	
}
