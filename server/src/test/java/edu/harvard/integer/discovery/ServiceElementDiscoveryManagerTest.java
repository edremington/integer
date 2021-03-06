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

import java.io.File;
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

import edu.harvard.integer.access.snmp.CommonSnmpOids;
import edu.harvard.integer.common.ID;
import edu.harvard.integer.common.IDType;
import edu.harvard.integer.common.TestUtil;
import edu.harvard.integer.common.discovery.DiscoveryParseElement;
import edu.harvard.integer.common.discovery.DiscoveryParseElementTypeEnum;
import edu.harvard.integer.common.discovery.DiscoveryParseString;
import edu.harvard.integer.common.discovery.SnmpContainment;
import edu.harvard.integer.common.discovery.SnmpContainmentType;
import edu.harvard.integer.common.discovery.SnmpLevelOID;
import edu.harvard.integer.common.discovery.SnmpServiceElementTypeDescriminatorIntegerValue;
import edu.harvard.integer.common.discovery.SnmpServiceElementTypeDiscriminator;
import edu.harvard.integer.common.discovery.SnmpVendorDiscoveryTemplate;
import edu.harvard.integer.common.discovery.VendorContainmentSelector;
import edu.harvard.integer.common.discovery.VendorIdentifier;
import edu.harvard.integer.common.discovery.VendorSignature;
import edu.harvard.integer.common.discovery.VendorSignatureTypeEnum;
import edu.harvard.integer.common.exception.IntegerException;
import edu.harvard.integer.common.service.managementobjectcapability.snmp.ImportMIBTest;
import edu.harvard.integer.common.snmp.MIBImportInfo;
import edu.harvard.integer.common.snmp.SNMP;
import edu.harvard.integer.common.topology.Category;
import edu.harvard.integer.common.topology.CategoryTypeEnum;
import edu.harvard.integer.common.topology.FieldReplaceableUnitEnum;
import edu.harvard.integer.common.topology.ServiceElementInstanceUniqueSignature;
import edu.harvard.integer.common.topology.ServiceElementManagementObject;
import edu.harvard.integer.common.topology.ServiceElementType;
import edu.harvard.integer.common.topology.SignatureTypeEnum;
import edu.harvard.integer.common.topology.SignatureValueOperator;
import edu.harvard.integer.common.topology.ValueOpertorEnum;
import edu.harvard.integer.service.discovery.ServiceElementDiscoveryManagerInterface;
import edu.harvard.integer.service.discovery.snmp.containment.ContainmentGenerator;
import edu.harvard.integer.service.distribution.DistributionManager;
import edu.harvard.integer.service.distribution.ManagerTypeEnum;
import edu.harvard.integer.service.managementobject.ManagementObjectCapabilityManagerInterface;
import edu.harvard.integer.service.managementobject.snmp.SnmpManagerInterface;
import edu.harvard.integer.util.FileUtil;

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
	private ManagementObjectCapabilityManagerInterface managementObjectCapabilityManager;

	@Inject
	private SnmpManagerInterface snmpMaager;


	private String firmwareVer = "Firmware1";
	private String model = "ModelT";
	private String softwareVer = "2.1";
	private String vendor = "Cisco";
	private String vendorSubType = "CiscoSubType";

	@Deployment
	public static Archive<?> createTestArchive() {
		return TestUtil
				.createTestArchive("ServiceElementDiscoveryManagerTest.war");
	}

	@Before
	public void initializeLogger() {
		// BasicConfigurator.configure();
		// org.apache.log4j.Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	

	@Test
	public void getTopLevelPolls() {

		System.out.println("GetToplLevelPolls");

		List<SNMP> topLevelPolls = serviceElementDiscoveryManger
				.getToplLevelOIDs();

		assert (topLevelPolls != null);

		if (topLevelPolls.size() == 0) {
			// Running with H2 db. so need to create the data.

			try {
				snmpMaager.updateSNMP(TestUtil.createOid("sysName",
						CommonSnmpOids.sysName));
				snmpMaager.updateSNMP(TestUtil.createOid("sysDescr",
						CommonSnmpOids.sysDescr));
				snmpMaager.updateSNMP(TestUtil.createOid("sysLocation",
						CommonSnmpOids.sysLocation));
				snmpMaager.updateSNMP(TestUtil.createOid("sysObjectID",
						CommonSnmpOids.sysObjectID));

				topLevelPolls = serviceElementDiscoveryManger
						.getToplLevelOIDs();

			} catch (IntegerException e) {

				e.printStackTrace();
				fail(e.toString());
			}

		}

		assert (topLevelPolls.size() > 0);

		for (ServiceElementManagementObject serviceElementManagementObject : topLevelPolls) {

			logger.info("Got " + serviceElementManagementObject.getName() + " "
					+ serviceElementManagementObject.getDisplayName());
		}

	}

	@Test
	public void createSnmpVendorDiscoveryTemplate() {

		SnmpManagerInterface snmpManager = null;
		try {
			snmpManager = DistributionManager
					.getManager(ManagerTypeEnum.SnmpManager);
		} catch (IntegerException e) {

			e.printStackTrace();
			fail(e.getMessage());
		}

		SnmpVendorDiscoveryTemplate template = new SnmpVendorDiscoveryTemplate();
		IDType type = new IDType(VendorIdentifier.class.getName());
		ID vendorId = new ID(Long.valueOf(9), "Cisco", type);
		template.setVendorId(vendorId);
		try {
			logger.info("Manager is " + snmpManager);
			assert (snmpManager != null);

			template.setModel(snmpManager.getSNMPByOid(CommonSnmpOids.sysDescr));
			template.setFirmware(snmpManager
					.getSNMPByOid(CommonSnmpOids.sysDescr));
			template.setSoftwareRevision(snmpManager
					.getSNMPByOid(CommonSnmpOids.sysDescr));

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
			template = serviceElementDiscoveryManger
					.updateSnmpVendorDiscoveryTemplate(template);

			logger.info("Created SnmpVendorDiscoveryTemplate " + template);
		} catch (IntegerException e) {

			e.printStackTrace();
			fail(e.toString());
		}
	}

	public void loadAllSnmpVendorTemplates() {
		try {
			SnmpVendorDiscoveryTemplate[] templates = serviceElementDiscoveryManger
					.getAllSnmpVendorDiscoveryTemplates();
			assert (templates != null);
			assert (templates.length > 0);

			logger.info("Found " + templates.length + " SnmpVendorTempaltes");

		} catch (IntegerException e) {

			e.printStackTrace();

			fail(e.toString());
		}
	}

	@Test
	public void getVendorByID() {

		String vendorId = new String("9");

		try {
			VendorIdentifier vendorIdentifier = serviceElementDiscoveryManger
					.getVendorIdentifier(vendorId);
			if (vendorIdentifier == null) {

				vendorIdentifier = new VendorIdentifier();
				vendorIdentifier.setVendorOid(vendorId);
				vendorIdentifier.setName("Cisco");

				serviceElementDiscoveryManger
						.updateVendorIdentifier(vendorIdentifier);

				vendorIdentifier = serviceElementDiscoveryManger
						.getVendorIdentifier(vendorId);

			}

			assert (vendorIdentifier != null);

			logger.info("Found vendor " + vendorIdentifier.getName()
					+ " for vendorId " + vendorId);

		} catch (IntegerException e) {

			e.printStackTrace();

			fail("Error loading Vendor by ID! " + e.toString());
		}
	}

	private void loadProductMib(String name) {

		MIBImportInfo mibFile = new MIBImportInfo();
		mibFile.setFileName(name);
		mibFile.setName(mibFile.getFileName());

		File file = new File(ImportMIBTest.MibDir + name);
		mibFile.setMib(FileUtil.readInMIB(file));

		try {
			snmpMaager.importProductMib("Cisco Entity", mibFile);
		} catch (IntegerException e1) {
			e1.printStackTrace();
			fail("Error loading " + name + " Error " + e1.toString());
		}
	}

	private void loadMib(String name) {

		MIBImportInfo mibFile = new MIBImportInfo();
		mibFile.setFileName(name);
		mibFile.setName(mibFile.getFileName());

		File file = new File(ImportMIBTest.MibDir + name);
		mibFile.setMib(FileUtil.readInMIB(file));

		try {
			snmpMaager.importMib(new MIBImportInfo[] { mibFile });
		} catch (IntegerException e1) {
			e1.printStackTrace();
			fail("Error loading " + name + " Error " + e1.toString());
		}
	}

	@Test
	public void getVendorIndentifierSubTree() {

		loadMib("SNMPv2-SMI");
		loadProductMib("CISCO-SMI.my");
		loadProductMib("CISCO-ENTITY-VENDORTYPE-OID-MIB.my");

		String rootOid = "1.3.6.1.4.1.9.12.3.1.9";
		try {

			List<VendorIdentifier> vendorSubTree = serviceElementDiscoveryManger
					.findVendorSubTree(rootOid);
			if (vendorSubTree == null) {
				loadMib("SNMPv2-SMI");
				loadProductMib("CISCO-SMI.my");
				loadProductMib("CISCO-ENTITY-VENDORTYPE-OID-MIB.my");

				vendorSubTree = serviceElementDiscoveryManger
						.findVendorSubTree(rootOid);
			}

			assert (vendorSubTree != null);

			logger.info("Found " + vendorSubTree.size()
					+ " VendorIdentifers in subtree of " + rootOid);

			assert (vendorSubTree.size() > 0);

		} catch (IntegerException e) {
			e.printStackTrace();
			fail("Error getting subtree of " + rootOid + " Error "
					+ e.toString());
		}
	}

	@Test
	public void getVendorIdentifierBySubTypeName() {

		try {
			VendorIdentifier vendorIdentifier = serviceElementDiscoveryManger
					.getVenderIdentiferBySubTypeName("cevModuleCommonCards");
			if (vendorIdentifier == null) {
				loadMib("SNMPv2-SMI");
				loadProductMib("CISCO-SMI.my");
				loadProductMib("CISCO-ENTITY-VENDORTYPE-OID-MIB.my");

				vendorIdentifier = serviceElementDiscoveryManger
						.getVenderIdentiferBySubTypeName("cevModuleCommonCards");
			}

			assert (vendorIdentifier != null);

		} catch (IntegerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void createServiceElementType() {

		ServiceElementType type = new ServiceElementType();
		Category category = null;
		try {
			category = managementObjectCapabilityManager
					.getCategoryByName(CategoryTypeEnum.port.getName());
			if (category == null) {
				category = new Category();
				category.setName(CategoryTypeEnum.port.getName());
				category = managementObjectCapabilityManager
						.updateCategory(category);
			}

		} catch (IntegerException e) {

			e.printStackTrace();
			fail(e.toString());
		}

		type.setCategory(category);
		type.addSignatureValue(null, SignatureTypeEnum.Vendor, vendor);

		type.addSignatureValue(null, SignatureTypeEnum.VendorSubType,
				vendorSubType);

		type.setVendorSpecificSubType(vendorSubType);

		try {
			managementObjectCapabilityManager.updateServiceElementType(type);

		} catch (IntegerException e) {

			e.printStackTrace();
			fail(e.toString());
		}

	}

	@Test
	public void createInterfaceServiceElementType() {
		addInterfaceServiceElementType();
	}

	/**
	 * 
	 */
	private ServiceElementType addInterfaceServiceElementType() {

		ServiceElementType type = new ServiceElementType();
		type.setName("interface");
		Category category = null;
		try {
			category = managementObjectCapabilityManager
					.getCategoryByName(CategoryTypeEnum.portIf.getName());
			if (category == null) {
				category = new Category();
				category.setName(CategoryTypeEnum.portIf.getName());
				category = managementObjectCapabilityManager
						.updateCategory(category);
			}

			assert (category != null);

		} catch (IntegerException e) {
			e.printStackTrace();
			fail(e.toString());
		}
		type.setCategory(category);
		type.addSignatureValue(null, SignatureTypeEnum.Vendor, vendor);

		type.addSignatureValue(null, SignatureTypeEnum.VendorSubType,
				vendorSubType);

		type.setVendorSpecificSubType(vendorSubType);

		try {
			return managementObjectCapabilityManager.updateServiceElementType(type);

		} catch (IntegerException e) {

			e.printStackTrace();
			fail(e.toString());
		}
		
		return null;
	}

	@Test
	public void getServiceElementByCategoryAndVendor() {
		try {

			createInterfaceServiceElementType();

			assert (serviceElementDiscoveryManger != null);

			Category category = managementObjectCapabilityManager
					.getCategoryByName(CategoryTypeEnum.portIf.getName());

			assert (category != null);

			ServiceElementType[] serviceElementTypes = serviceElementDiscoveryManger
					.getServiceElementTypesByCategoryAndVendor(category, vendor);

			assert (serviceElementTypes != null);
			assert (serviceElementTypes.length > 0);

			logger.info("Found "
					+ serviceElementTypes.length
					+ " ServiceElementTypes for category 'Port' and Vendor 'Cisco'");

		} catch (IntegerException e) {

			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void findServiceElementTypeByVendorAndVendorSubType() {

		ServiceElementType[] serviceElementTypes = null;

		try {
			serviceElementTypes = serviceElementDiscoveryManger
					.getServiceElementTypesBySubtypeAndVendor(vendorSubType,
							vendor);
		} catch (IntegerException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		if (serviceElementTypes == null || serviceElementTypes.length == 0) {

			createServiceElementType();

			try {
				serviceElementTypes = serviceElementDiscoveryManger
						.getServiceElementTypesBySubtypeAndVendor(
								vendorSubType, vendor);
			} catch (IntegerException e) {
				e.printStackTrace();
				fail(e.toString());
			}
		}

		assert (serviceElementTypes != null);
		assert (serviceElementTypes.length > 0);

		logger.info("Found " + serviceElementTypes.length
				+ " ServiceElementTypes for Vendor " + vendor + " and Subtype "
				+ vendorSubType);

	}

	@Test
	public void createVendorContainmentSelector() {
		VendorContainmentSelector vendorContainmentSelector = new VendorContainmentSelector();
		vendorContainmentSelector
				.setContainmentId(new ID(Long.valueOf(1), "SnmpContainment",
						new IDType(SnmpContainment.class.getName())));
		
		vendorContainmentSelector.addEqualSignature(VendorSignatureTypeEnum.Firmware,  firmwareVer);
		vendorContainmentSelector.addEqualSignature(VendorSignatureTypeEnum.Model, model);
		vendorContainmentSelector.addEqualSignature(VendorSignatureTypeEnum.SoftwareVersion, softwareVer);
		vendorContainmentSelector.addEqualSignature(VendorSignatureTypeEnum.Vendor, "Vendor");

		SnmpContainment containment = new SnmpContainment();
		containment.setName("Test containment");
		List<SnmpLevelOID> levels = new ArrayList<SnmpLevelOID>();
		levels.add(createSnmpLevelOID());
		containment.setSnmpLevels(levels);

		containment.setServiceElementTypeId(addInterfaceServiceElementType().getID());
		
		try {
			containment = serviceElementDiscoveryManger.updateSnmpContainment(containment);
		} catch (IntegerException e1) {
			e1.printStackTrace();
			fail(e1.toString());
		}
		
		vendorContainmentSelector.setContainmentId(containment.getID());
		
		try {
			vendorContainmentSelector = serviceElementDiscoveryManger.updateVendorContainmentSelector(vendorContainmentSelector);
					
		} catch (IntegerException e) {

			e.printStackTrace();
			fail(e.toString());
		}

	}

	public SnmpLevelOID createSnmpLevelOID() {
		SnmpLevelOID level = new SnmpLevelOID();
		SNMP snmp = null;
		try {
			snmp = snmpMaager.updateSNMP(TestUtil.createOid("hrDeviceEntry",
					CommonSnmpOids.hrDeviceEntry));
		} catch (IntegerException e1) {
			
			e1.printStackTrace();
			fail(e1.toString());
			
		}
		
		level.setContextOID(snmp);
		level.setName("level 1");
		try {
			snmp = snmpMaager.updateSNMP(TestUtil.createOid("hrDeviceType",
					CommonSnmpOids.hrDeviceType));
		} catch (IntegerException e1) {
			
			e1.printStackTrace();
			fail(e1.toString());
		}
		
		level.setDescriminatorOID(snmp);
		
		List<SnmpServiceElementTypeDiscriminator> disriminators = new ArrayList<SnmpServiceElementTypeDiscriminator>();
		SnmpServiceElementTypeDiscriminator descrimator = new SnmpServiceElementTypeDiscriminator();
		descrimator.setDiscriminatorValue(null);
		descrimator.setServiceElementTypeId(addInterfaceServiceElementType().getID());
		level.setDisriminators(disriminators);
	
		return level;
	}
	
	public void getAllVendorContainmentSelectors() {
		try {
			VendorContainmentSelector[] selectors = serviceElementDiscoveryManger
					.getAllVendorContainmentSelectors();
			if (selectors == null || selectors.length == 0) {
				createVendorContainmentSelector();
				selectors = serviceElementDiscoveryManger
						.getAllVendorContainmentSelectors();
			}

			assert (selectors != null);

			logger.info("Found " + selectors.length
					+ " VendorContianmentSelectors");

			assert (selectors.length > 0);

		} catch (IntegerException e) {

			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void getVendorContainmentSelector() {

		createInterfaceServiceElementType();

		createVendorContainmentSelector();

		VendorContainmentSelector vs = new VendorContainmentSelector();
		List<VendorSignature> signatures = new ArrayList<VendorSignature>();
		
		signatures.add(createEqualStringOperator(VendorSignatureTypeEnum.Firmware, firmwareVer));
		signatures.add(createEqualStringOperator(VendorSignatureTypeEnum.Model, model));
		signatures.add(createEqualStringOperator(VendorSignatureTypeEnum.SoftwareVersion, softwareVer));
		signatures.add(createEqualStringOperator(VendorSignatureTypeEnum.Vendor, vendor));
		
		vs.setSignatures(signatures);
		
		ServiceElementType set = null;
		SnmpContainment snmpContainment = null;

		try {
			snmpContainment = serviceElementDiscoveryManger.getSnmpContainment(vs);
			System.out.println("Found SnmpContainment " + snmpContainment);
		} catch (IntegerException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		
		if (snmpContainment != null && snmpContainment instanceof SnmpContainment) {

			System.out.println("SnmpContainment " + snmpContainment.getID().toDebugString() + " ServiceElementID " + snmpContainment.getServiceElementTypeId());
			
			try {
				set = serviceElementDiscoveryManger
						.getServiceElementTypeById(snmpContainment.getServiceElementTypeId());
			} catch (IntegerException e) {
				e.printStackTrace();
				fail(e.toString());
			}
			// discoverNode.setTopServiceElementType(set);
		}

		if (snmpContainment == null) {

			SnmpContainmentType containmentType = SnmpContainmentType.EntityMib;

			set = new ServiceElementType();
			set.addSignatureValue(null, SignatureTypeEnum.Vendor, vendor);
			set.addSignatureValue(null, SignatureTypeEnum.Model, model);
			set.setFieldReplaceableUnit(FieldReplaceableUnitEnum.Yes);

			// ContainmentGenerator.setUpTopServiceElementProperty(
			// discoverNode.getElementEndPoint(), set, containmentType);

			try {
				set = managementObjectCapabilityManager
						.updateServiceElementType(set);
			} catch (IntegerException e) {
				e.printStackTrace();
				fail(e.toString());
			}
			// discoverNode.setTopServiceElementType(set);

			try {
				snmpContainment = ContainmentGenerator.generator(set, containmentType);
			} catch (IntegerException e) {
				e.printStackTrace();
				fail(e.toString());
			}
			SnmpContainment updateSnmpContainment = null;
			try {
				updateSnmpContainment = managementObjectCapabilityManager
						.updateSnmpContainment(snmpContainment);
			} catch (IntegerException e) {
				e.printStackTrace();
				fail(e.toString());
			}

			VendorContainmentSelector vendorContainmentSelector = new VendorContainmentSelector();
			vendorContainmentSelector.setContainmentId(updateSnmpContainment
					.getID());
			

			vendorContainmentSelector.addEqualSignature(VendorSignatureTypeEnum.Firmware, firmwareVer);
			vendorContainmentSelector.addEqualSignature(VendorSignatureTypeEnum.Model, model);
			vendorContainmentSelector.addEqualSignature(VendorSignatureTypeEnum.SoftwareVersion, softwareVer);
			vendorContainmentSelector.addEqualSignature(VendorSignatureTypeEnum.Vendor, vendor);
			
			try {
				serviceElementDiscoveryManger
						.updateVendorContainmentSelector(vendorContainmentSelector);
			} catch (IntegerException e) {
				e.printStackTrace();
				fail(e.toString());
			}

			logger.info("Created SnmpContainment "
					+ updateSnmpContainment.getID());

		}

		try {
			snmpContainment = serviceElementDiscoveryManger.getSnmpContainment(vs);
			assert (snmpContainment != null);

		} catch (IntegerException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	/**
	 * @param firmware
	 * @param firmwareVer
	 * @return
	 */
	private VendorSignature createEqualStringOperator(
			VendorSignatureTypeEnum valueType, String value) {
		
		VendorSignature signature = new VendorSignature();
		signature.setName(value);
		signature.setSignatureType(valueType);
		
		SignatureValueOperator operator = new SignatureValueOperator();
		operator.setOperator(ValueOpertorEnum.Equal);
		operator.setValue(value);
		
		signature.setValueOperator(operator);
		
		return signature;
	}

	@Test
	public void createSnmpContainment() {
		SnmpContainment snmpContainment = new SnmpContainment();
		snmpContainment.setContainmentType(SnmpContainmentType.EntityMib);
		snmpContainment.setName("MyContainment");
		snmpContainment.setServiceElementTypeId(new ID(Long.valueOf(2),
				"ServiceElementType", new IDType(ServiceElementType.class
						.getName())));

		SnmpLevelOID snmpLevelOid = new SnmpLevelOID();
		snmpLevelOid.setName("My level oid");

		SNMP contextOID = new SNMP();
		contextOID.setOid(CommonSnmpOids.sysName);
		contextOID.setName("sysName");
		snmpLevelOid.setContextOID(contextOID);

		SNMP descriminatorOID = new SNMP();
		descriminatorOID.setOid(CommonSnmpOids.sysObjectID);
		descriminatorOID.setName("SysObjectID");
		snmpLevelOid.setDescriminatorOID(descriminatorOID);

		List<SnmpServiceElementTypeDiscriminator> descriminators = new ArrayList<SnmpServiceElementTypeDiscriminator>();
		for (int i = 0; i < 10; i++)
			descriminators.add(createSETDiscriminator(i));

		snmpLevelOid.setDisriminators(descriminators);

		List<SnmpLevelOID> list = new ArrayList<SnmpLevelOID>();
		list.add(snmpLevelOid);
		snmpContainment.setSnmpLevels(list);

		try {
			SnmpContainment updateSnmpContainment = managementObjectCapabilityManager
					.updateSnmpContainment(snmpContainment);
			logger.info("Created SnmpContainment "
					+ updateSnmpContainment.getID());

		} catch (IntegerException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	private SnmpServiceElementTypeDiscriminator createSETDiscriminator(int value) {
		SnmpServiceElementTypeDiscriminator serviceElementTypeDescriminator = new SnmpServiceElementTypeDiscriminator();

		ID serviceElementTypeId = new ID(Long.valueOf(value), "SET",
				new IDType(ServiceElementType.class.getName()));
		serviceElementTypeDescriminator
				.setServiceElementTypeId(serviceElementTypeId);
		SnmpServiceElementTypeDescriminatorIntegerValue descriminatorValue = new SnmpServiceElementTypeDescriminatorIntegerValue();
		descriminatorValue.setValue(Integer.valueOf(value));
		serviceElementTypeDescriminator
				.setDiscriminatorValue(descriminatorValue);

		return serviceElementTypeDescriminator;
	}

	@Test
	public void getSnmpContainment() {
		createSnmpContainment();

		try {
			SnmpContainment[] containments = managementObjectCapabilityManager
					.getAllSnmpContainments();

			assert (containments != null);
			assert (containments.length > 0);

			logger.info("Found " + containments.length + " SnmpContainments");

		} catch (IntegerException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void getSnmpContainmentById() {
		createSnmpContainment();

		try {
			SnmpContainment[] containments = managementObjectCapabilityManager
					.getAllSnmpContainments();
			if (containments != null)
				logger.info("Found " + containments.length
						+ " SnmpContainments");

			assert (containments != null);
			assert (containments.length > 0);

			for (SnmpContainment snmpContainment : containments) {
				SnmpContainment snmpContainmentById = managementObjectCapabilityManager
						.getSnmpContainmentById(snmpContainment.getID());
				assert (snmpContainmentById != null);

				logger.info("found SnmpContainment "
						+ snmpContainmentById.getID());
			}

		} catch (IntegerException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void checkSnmpContainmentByVendor() {
		createVendorContainmentSelector();

		VendorContainmentSelector vs = new VendorContainmentSelector();

		vs.addEqualSignature(VendorSignatureTypeEnum.Vendor, "vendor");

		try {
			SnmpContainment sc = serviceElementDiscoveryManger
					.getSnmpContainment(vs);
			
			assert (sc != null);
			
			printSnmpContainment(sc);
		} catch (IntegerException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void checkSnmpContainment() {

		try {
			SnmpContainment[] containments = serviceElementDiscoveryManger
					.getAllSnmpContainments();
			for (SnmpContainment snmpContainment : containments) {
				printSnmpContainment(snmpContainment);
			}
		} catch (IntegerException e) {

			e.printStackTrace();
		}

	}

	/**
	 * @param snmpContainment
	 */
	private void printSnmpContainment(SnmpContainment snmpContainment) {
		if (snmpContainment == null)
			return;

		StringBuffer b = new StringBuffer();

		b.append("SnmpContainment: " + snmpContainment.getName()).append('\n');
		b.append(" Type: ").append(snmpContainment.getContainmentType())
				.append('\n');

		if (snmpContainment.getSnmpLevels() != null) {
			b.append(" SnmpLevels: ")
					.append(snmpContainment.getSnmpLevels().size())
					.append('\n');

			for (SnmpLevelOID level : snmpContainment.getSnmpLevels()) {
				if (level.getContextOID() != null)
					b.append(" Context: ").append(level.getContextOID().getOid())
					.append('\n');

				if (level.getDisriminators() != null) {
					b.append(" Discriminators: ")
							.append(level.getDisriminators().size())
							.append('\n');
					
					for (SnmpServiceElementTypeDiscriminator discriminator : level
							.getDisriminators()) {
						b.append("    Discrimator value: ")
								.append(discriminator.getDiscriminatorValue())
								.append('\n');
						if (discriminator.getServiceElementTypeId() != null)
							b.append("    Servie element Type: ")
									.append(discriminator
											.getServiceElementTypeId()
											.toDebugString()).append('\n');
					}
				}
			}
		}
		logger.info(b.toString());

	}

	@Test
	public void addServiceElementInstanceUniqueSignature() {
		ServiceElementInstanceUniqueSignature signature = new ServiceElementInstanceUniqueSignature();
		signature.setDescription("A Description");
		
		List<ServiceElementManagementObject> uniqueSemos = new ArrayList<ServiceElementManagementObject>();
		try {
			uniqueSemos.add(snmpMaager.updateSNMP(TestUtil.createOid("sysName", CommonSnmpOids.sysName)));
		
			uniqueSemos.add(snmpMaager.updateSNMP(TestUtil.createOid("sysOid", CommonSnmpOids.sysObjectID)));
		} catch (IntegerException e) {
			
			e.printStackTrace();
			fail(e.toString());
		}
		
		List<ServiceElementManagementObject> changedSemos = new ArrayList<ServiceElementManagementObject>();
		try {
			changedSemos.add(snmpMaager.updateSNMP(TestUtil.createOid("sysContact", CommonSnmpOids.sysContact)));
		
			changedSemos.add(snmpMaager.updateSNMP(TestUtil.createOid("sysDescr", CommonSnmpOids.sysDescr)));
		} catch (IntegerException e) {
			
			e.printStackTrace();
			fail(e.toString());
		}
		
		try {
			serviceElementDiscoveryManger.updateServiceElementInstanceUniqueSignature(signature);
		} catch (IntegerException e) {
		
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void getAllServiceElementInstanceUniqueSignatures() {
		try {
			ServiceElementInstanceUniqueSignature[] signatures = serviceElementDiscoveryManger.getAllServiceElementInstanceUniqueSignature();
			if (signatures == null || signatures.length == 0) { 
				addServiceElementInstanceUniqueSignature();
			
				signatures = serviceElementDiscoveryManger.getAllServiceElementInstanceUniqueSignature();
			}
			
			assert(signatures != null);
			assert(signatures.length > 0);
			
		} catch (IntegerException e) {
			
			e.printStackTrace();
			fail(e.toString());
		}
	}
}