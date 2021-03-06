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

package edu.harvard.integer.service.yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import edu.harvard.integer.common.ID;
import edu.harvard.integer.common.discovery.RelationMappingTypeEnum;
import edu.harvard.integer.common.discovery.SnmpAssociation;
import edu.harvard.integer.common.discovery.SnmpContainment;
import edu.harvard.integer.common.discovery.SnmpContainmentRelation;
import edu.harvard.integer.common.discovery.SnmpContainmentType;
import edu.harvard.integer.common.discovery.SnmpLevelOID;
import edu.harvard.integer.common.discovery.SnmpParentChildRelationship;
import edu.harvard.integer.common.discovery.SnmpRelationship;
import edu.harvard.integer.common.discovery.SnmpServiceElementTypeDiscriminator;
import edu.harvard.integer.common.discovery.SnmpServiceElementTypeDiscriminatorStringValue;
import edu.harvard.integer.common.discovery.SnmpServiceElementTypeDiscriminatorValue;
import edu.harvard.integer.common.discovery.SnmpUniqueDiscriminator;
import edu.harvard.integer.common.discovery.VendorContainmentSelector;
import edu.harvard.integer.common.discovery.VendorSignature;
import edu.harvard.integer.common.discovery.VendorSignatureTypeEnum;
import edu.harvard.integer.common.exception.IntegerException;
import edu.harvard.integer.common.exception.YamlParserErrrorCodes;
import edu.harvard.integer.common.snmp.SNMP;
import edu.harvard.integer.common.snmp.SNMPTable;
import edu.harvard.integer.common.technology.Technology;
import edu.harvard.integer.common.topology.Capability;
import edu.harvard.integer.common.topology.Category;
import edu.harvard.integer.common.topology.FieldReplaceableUnitEnum;
import edu.harvard.integer.common.topology.ServiceElementAssociationType;
import edu.harvard.integer.common.topology.ServiceElementInstanceUniqueSignature;
import edu.harvard.integer.common.topology.ServiceElementType;
import edu.harvard.integer.common.topology.Signature;
import edu.harvard.integer.common.topology.SignatureValueOperator;
import edu.harvard.integer.common.topology.ValueOpertorEnum;
import edu.harvard.integer.common.yaml.YamlBaseInfoInterface;
import edu.harvard.integer.common.yaml.YamlDomainData;
import edu.harvard.integer.common.yaml.YamlLocation;
import edu.harvard.integer.common.yaml.YamlServiceElementType;
import edu.harvard.integer.common.yaml.YamlSnmpAssociation;
import edu.harvard.integer.common.yaml.vendorcontainment.YamlCategory;
import edu.harvard.integer.common.yaml.vendorcontainment.YamlSnmpContainmentRelation;
import edu.harvard.integer.common.yaml.vendorcontainment.YamlSnmpLevelOID;
import edu.harvard.integer.common.yaml.vendorcontainment.YamlSnmpParentChildRelationship;
import edu.harvard.integer.common.yaml.vendorcontainment.YamlSnmpServiceElementTypeDiscriminator;
import edu.harvard.integer.common.yaml.vendorcontainment.YamlSnmpUniqueDiscriminator;
import edu.harvard.integer.common.yaml.vendorcontainment.YamlVendorContainment;
import edu.harvard.integer.common.yaml.vendorcontainment.YamlVendorSignature;
import edu.harvard.integer.service.BaseManager;
import edu.harvard.integer.service.discovery.ServiceElementDiscoveryManagerInterface;
import edu.harvard.integer.service.distribution.DistributionManager;
import edu.harvard.integer.service.distribution.ManagerTypeEnum;
import edu.harvard.integer.service.managementobject.ManagementObjectCapabilityManagerInterface;
import edu.harvard.integer.service.managementobject.snmp.SnmpManagerInterface;
import edu.harvard.integer.service.persistance.PersistenceManagerInterface;
import edu.harvard.integer.service.persistance.dao.discovery.SnmpAssociationDAO;
import edu.harvard.integer.service.persistance.dao.managementobject.CapabilityDAO;
import edu.harvard.integer.service.persistance.dao.snmp.SNMPDAO;
import edu.harvard.integer.service.persistance.dao.topology.ServiceElementAssociationTypeDAO;
import edu.harvard.integer.service.persistance.dao.topology.ServiceElementInstanceUniqueSignatureDAO;
import edu.harvard.integer.service.persistance.dao.topology.ServiceElementTypeDAO;
import edu.harvard.integer.service.persistance.dao.topology.SnmpUniqueDiscriminatorDAO;
import edu.harvard.integer.service.persistance.dao.topology.vendortemplate.SnmpLevelOIDDAO;
import edu.harvard.integer.service.persistance.dao.topology.vendortemplate.SnmpRelationshipDAO;
import edu.harvard.integer.service.technology.TechnologyManagerInterface;

/**
 * @see YamlManagerInterface
 * 
 * @author David Taylor
 * 
 */
@Stateless
public class YamlManager extends BaseManager implements
		YamlManagerLocalInterface, YamlManagerRemoteInterface {

	@Inject
	private Logger logger;

	@Inject
	private TechnologyManagerInterface technologyManager;

	@Inject
	PersistenceManagerInterface persistanceManager;

	@Inject
	ServiceElementDiscoveryManagerInterface discoveryManager;

	@Inject
	SnmpManagerInterface snmpManager;
	
	@Inject
	ManagementObjectCapabilityManagerInterface managementObjectManager;

	/**
	 * @param managerType
	 */
	public YamlManager() {
		super(ManagerTypeEnum.YamlManager);
	}

	
	

	/**
	 * Helper method to parse an array of objects.
	 * 
	 * @param parentId
	 *            . Parent ID that all the objects in the list belong to.
	 * @param indent
	 *            . Used for prity printing the imported tree.
	 * @param list
	 * @throws IntegerException
	 */
	private void parseArrayList(ID parentId, String indent,
			ArrayList<Object> list) throws IntegerException {
		for (Object value : list) {
			if (logger.isDebugEnabled())
				logger.debug("Item " + value + " class "
						+ value.getClass().getName());
			parseObject(parentId, indent, value);
		}
	}

	/**
	 * Parse a hashmap of YAML objects.
	 * 
	 * @param parentId
	 *            . Parent ID that all the objects in the hashmap belong to.
	 * @param indent
	 *            . Use for pretty print of the imported YAML.
	 * @param map
	 * @throws IntegerException
	 */
	private void parseHashMap(ID parentId, String indent,
			LinkedHashMap<String, Object> map) throws IntegerException {
		for (String key : map.keySet()) {
			Object value = map.get(key);

			if (logger.isDebugEnabled())
				logger.debug(indent + "Key " + key);

			if (key.contains("placeholder"))
				continue;

			Technology technology = technologyManager.getTechnologyByName(key);
			if (technology == null) {
				technology = createTechnology(key, indent, parentId);
			}
			parentId = technology.getID();

			parseObject(parentId, indent, value);
		}
	}

	/**
	 * Helper method to parse the give object from a YAML file.
	 * 
	 * @param parentId
	 *            . ID of the parent object that this object belongs to.
	 * @param indent
	 *            . Used for pretty printing of the YAML import file.
	 * @param value
	 *            . Value to be parsed.
	 * @throws IntegerException
	 */
	@SuppressWarnings("unchecked")
	private void parseObject(ID parentId, String indent, Object value)
			throws IntegerException {
		if (value instanceof ArrayList)
			parseArrayList(parentId, indent + "  ", (ArrayList<Object>) value);
		else if (value instanceof LinkedHashMap)
			parseHashMap(parentId, indent + "  ",
					(LinkedHashMap<String, Object>) value);
		else if (value instanceof String) {
			logger.info(indent + "String " + value);
			createTechnology((String) value, indent, parentId);
		} else
			logger.error("Unknown Value type " + value.getClass().getName()
					+ " " + value);
	}

	/**
	 * Create a Technology with the given name and link to the parent ID.
	 * 
	 * @param name
	 *            . Name of the technolgoy to create.
	 * @param indent
	 *            . Used to pretty print the imported YAML file.
	 * @param parentId
	 *            . Parent ID that this technology belongs to.
	 * @return Create Technology
	 * @throws IntegerException
	 */
	private Technology createTechnology(String name, String indent, ID parentId)
			throws IntegerException {

		if (name.contains("placeholder"))
			return null;

		Technology technology = technologyManager.getTechnologyByName(name);
		if (technology == null)
			technology = new Technology();

		int indexOf = name.indexOf('(');
		if (indexOf > 0) {
			technology.setName(name.substring(0, name.indexOf('(') - 1));

			int endIndex = name.lastIndexOf(')');
			if (endIndex < indexOf)
				endIndex = name.length();

			technology.setDescription(name.substring(name.indexOf('(') + 1,
					endIndex));
		} else {
			technology.setDescription(name);
			technology.setName(name);
		}

		logger.info("Create Technology " + technology.getName() + "("
				+ technology.getName().length() + ")" + "== "
				+ technology.getDescription() + "("
				+ technology.getDescription().length() + ")");

		technology.setParentId(parentId);

		StringBuffer b = new StringBuffer();
		b.append('\n');
		b.append(indent).append("- name: ").append(technology.getName())
				.append('\n');
		b.append(indent).append("  description: ")
				.append(technology.getDescription()).append('\n');
		b.append(indent).append("  parent: ")
				.append(technology.getParentId().getName()).append('\n');

		logger.info(b.toString());

		technology = technologyManager.updateTechnology(technology);

		return technology;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.harvard.integer.service.yaml.YamlManagerInterface#loadServiceElementType
	 * (java.lang.String)
	 */
	@Override
	public String loadServiceElementType(String content)
			throws IntegerException {
		Yaml yaml = new Yaml(new CustomClassLoaderConstructor(
				YamlDomainData.class, getClass().getClassLoader()));

		YamlDomainData load = null;

		try {
			load = (YamlDomainData) yaml.load(content);
		} catch (Throwable e) {
			logger.error("Unexpected error reading in YAML! " + e.toString());
			e.printStackTrace();
			throw new IntegerException(e, YamlParserErrrorCodes.ParsingError);
		}

		logger.info("YAML Object is " + load.getClass().getName());

		ServiceElementTypeDAO serviceElementTypeDao = persistanceManager
				.getServiceElementTypeDAO();
		
		ServiceElementAssociationTypeDAO associationDao = persistanceManager.getServiceElementAssociationTypeDAO();
		SNMPDAO snmpDao = persistanceManager.getSNMPDAO();
		CapabilityDAO capabilityDAO = persistanceManager.getCapabilityDAO();
		ServiceElementInstanceUniqueSignatureDAO signatureDAO = persistanceManager.getServiceElementInstanceUniqueSignatureDAO();
		
		YamlServiceElementTypeParser parser = new YamlServiceElementTypeParser(serviceElementTypeDao, associationDao, snmpDao, capabilityDAO, signatureDAO);
		
		parser.parse((YamlServiceElementType[]) load.getServiceElementTypes().toArray(new YamlServiceElementType[load.getServiceElementTypes().size()]));

		return "Success";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.harvard.integer.service.yaml.YamlManagerInterface#loadVendorContainment
	 * (java.lang.String)
	 */
	@Override
	public String loadVendorContainment(String content) throws IntegerException {
		Yaml yaml = new Yaml(new CustomClassLoaderConstructor(
				YamlVendorContainment.class, getClass().getClassLoader()));

		YamlVendorContainment load = null;

		try {
			load = (YamlVendorContainment) yaml.load(content);
		} catch (Throwable e) {
			logger.error("Unexpected error reading in YAML! " + e.toString());
			e.printStackTrace();
			throw new IntegerException(e, YamlParserErrrorCodes.ParsingError);
		}

		logger.info("YAML Object is " + load.getClass().getName());

		parseVendorDeployment(load);

		return "Success";

	}

	/**
	 * Helper method to parse the vendor containment selector.
	 * 
	 * @param load
	 *            . YamlVendorContainment that holds the data to create the
	 *            VendorContainment for.
	 * @throws IntegerException
	 */
	private void parseVendorDeployment(YamlVendorContainment load)
			throws IntegerException {
		ServiceElementDiscoveryManagerInterface discoveryManager = DistributionManager
				.getManager(ManagerTypeEnum.ServiceElementDiscoveryManager);

		if (discoveryManager == null) {
			logger.error("Unable to load VendorContianment YAML! NO ServiceElementDiscoveryManager found!!");
			return;
		}

		VendorContainmentSelector selector = new VendorContainmentSelector();
		selector.setSignatures(createSignatures(load.getSignatures()));
		

		selector = discoveryManager
				.updateVendorContainmentSelector(selector);
				
		SnmpContainment snmpContainment = discoveryManager
				.getSnmpContainment(selector);
		
//		VendorContainmentSelector[] vendorContainmentSelectors = discoveryManager
//				.getVendorContainmentSelector(selector);

//		if (vendorContainmentSelectors == null
//				|| vendorContainmentSelectors.length == 0) {
//			selector = discoveryManager
//					.updateVendorContainmentSelector(selector);
//		} else
//			selector = vendorContainmentSelectors[0];


		if (snmpContainment == null) {
			snmpContainment = new SnmpContainment();
			StringBuffer b = new StringBuffer();
			if (selector.getSignatures() != null) {
				for (VendorSignature signature : selector.getSignatures()) {
					if (b.length() > 1)
						b.append(", ");

					b.append(signature.getSignatureType().name());
					b.append(" ").append(signature.getValueOperator().getOperator().name());
					b.append(" ").append(signature.getValueOperator().getValue());
				}
			} else
				b.append("All");
			
			logger.info("SnmpContainment name: " + b.toString());
			if (b.toString().length() >= 50) {
				logger.info("Generated name too long. " + b.toString());
				snmpContainment.setName(b.toString().substring(0, 45));
			} else
				snmpContainment.setName(b.toString());
		}

		SnmpContainmentType contanmentType = SnmpContainmentType.valueOf(load
				.getSnmpContainment().getContainmentType());
		snmpContainment.setContainmentType(contanmentType);

		snmpContainment.setSnmpLevels(createSnmpLevelOIDs(load
				.getSnmpContainment().getSnmpLevels(), snmpContainment
				.getSnmpLevels()));

		ServiceElementType serviceElementType = null;
		if ( load.getSnmpContainment().getServiceElementTypeName() != null ) {
			ServiceElementTypeDAO dao = persistanceManager.getServiceElementTypeDAO();
			serviceElementType = dao.findByName(load.getSnmpContainment().getServiceElementTypeName());
			if (serviceElementType == null) {
				logger.error("ServiceElementType (" + load.getSnmpContainment().getServiceElementTypeName() + ") not found!! "
						+ " Vendor Containment " + load.getName() + " will not be loaded");
				return;
			}
			snmpContainment.setServiceElementTypeId(serviceElementType.getID());
		}
		else {
			snmpContainment.setServiceElementTypeId(createServiceElement(load
					.getSnmpContainment().getServiceElementType()));
		}
		
		if ( load.getSnmpContainment().getUniqueDescriminators() != null ) {
			
			snmpContainment.setUniqueDiscriminators(new ArrayList<SnmpUniqueDiscriminator>());
			
			SNMPDAO snmpDao = persistanceManager.getSNMPDAO();
			SnmpUniqueDiscriminatorDAO uniqueDao = persistanceManager.getSnmpUniqueDiscriminatorDAO();
			for ( YamlSnmpUniqueDiscriminator yUniqueDescr : load.getSnmpContainment().getUniqueDescriminators() ) {
				
				SnmpUniqueDiscriminator uniqueDescr = new SnmpUniqueDiscriminator();
				
				uniqueDescr.setName(yUniqueDescr.getName());
				if ( yUniqueDescr.getContaxtOID() != null ) {
					SNMP snmp = snmpDao.findByName(yUniqueDescr.getContaxtOID());
					if ( snmp == null ) {
						logger.error("OID not found for " + yUniqueDescr.getContaxtOID()
								+ " Unable to create SnmpUniqueDescriminator!");

						throw new IntegerException(null,
								YamlParserErrrorCodes.ContextOidNotFound);
					}
					uniqueDescr.setContextOID(snmp);
				}
				if ( yUniqueDescr.getDiscriminatorOID() != null ) {
					SNMP snmp = snmpDao.findByName(yUniqueDescr.getDiscriminatorOID());
					if ( snmp == null ) {
						logger.error("OID not found for " + yUniqueDescr.getDiscriminatorOID()
								+ " Unable to create SnmpUniqueDescriminator!");

						throw new IntegerException(null,
								YamlParserErrrorCodes.ParsingError);
					}
					uniqueDescr.setDescriminatorOID(snmp);
					if ( yUniqueDescr.getDiscriminatorValue() == null ) {
						logger.error("No descriminator value avaiable " + yUniqueDescr.getDiscriminatorOID()
								+ " Unable to create SnmpUniqueDescriminator!");

						throw new IntegerException(null,
								YamlParserErrrorCodes.ParsingError);
					}
					
					SnmpServiceElementTypeDiscriminatorStringValue stringValue = new SnmpServiceElementTypeDiscriminatorStringValue();
					stringValue.setValue(yUniqueDescr.getDiscriminatorValue());
					stringValue.setName(yUniqueDescr.getDiscriminatorValue());
					uniqueDescr.setDescriminatorValue(stringValue);
				}	
				
//				ServiceElementInstanceUniqueSignatureDAO signatureDAO = persistanceManager.getServiceElementInstanceUniqueSignatureDAO();
//				ServiceElementInstanceUniqueSignature[] instSignagtures =  signatureDAO.findByServiceElementTypeId(serviceElementType.getID());
				uniqueDescr.setUniqueIdentifierSemos(new ArrayList<ID>());
				
				for ( String attr : yUniqueDescr.getUniqueIdentifierSemos() ) {
				
					SNMP snmp = snmpDao.findByName(attr);
					if ( snmp == null ) {
						logger.error("OID not found for " + attr + " Unable to create SnmpUniqueDescriminator!");

						throw new IntegerException(null,
								YamlParserErrrorCodes.ParsingError);
					}
					/*
					boolean found = false;
					for ( ID id : uniqueIds ) {
					
						if ( id.getIdentifier().longValue() == snmp.getIdentifier().longValue() ) {
							found = true;
							break;
						}
					}
					if ( found ) {
						uniqueDescr.getUniqueIdentifierSemos().add(snmp.getID());
					}
					else {
						logger.error("SEMO " + attr + " is not belong to " + serviceElementType.getName()  + " Unable to create SnmpUniqueDescriminator!");

						throw new IntegerException(null,
								YamlParserErrrorCodes.ParsingError);
					}
					*/
					uniqueDescr.getUniqueIdentifierSemos().add(snmp.getID());
				}
				uniqueDao.update(uniqueDescr);
				snmpContainment.getUniqueDiscriminators().add(uniqueDescr);
			}
		}

		snmpContainment = discoveryManager.updateSnmpContainment(snmpContainment);
		selector.setContainmentId(snmpContainment.getID());

		discoveryManager.updateVendorContainmentSelector(selector);
	}

	/**
	 * @param signatures
	 * @return
	 */
	private List<VendorSignature> createSignatures(
			List<YamlVendorSignature> signatures) {
		
		if (signatures == null)
			return null;
		
		List<VendorSignature> vendorSignatures = new ArrayList<VendorSignature>();
		
		for (YamlVendorSignature yamlVendorSignature : signatures) {
			VendorSignature vendorSignature = new VendorSignature();
			vendorSignature.setSignatureType(VendorSignatureTypeEnum.valueOf(yamlVendorSignature.getName()));
			
			SignatureValueOperator operator = new SignatureValueOperator();
			operator.setValue(yamlVendorSignature.getValue());
			operator.setOperator(ValueOpertorEnum.valueOf(yamlVendorSignature.getOperator()));
			vendorSignature.setValueOperator(operator);
			
			vendorSignatures.add(vendorSignature);
		}
		
		return vendorSignatures;
	}

	/**
	 * Helper method to create the SnmpLevelOID and child SnmpLevelOID's.
	 * 
	 * @param yamlSnmpLevelOids. Data to create the SnmpLevelOID from.
	 * @param topLevels. List of all SnmpLevelOIDs created in this import. 
	 * @return Saved SnmpLEvelOID's created.
	 * @throws IntegerException
	 */
	private List<SnmpLevelOID> createSnmpLevelOIDs(
			List<YamlSnmpLevelOID> yamlSnmpLevelOids,
			List<SnmpLevelOID> topLevels) throws IntegerException {

		SnmpLevelOIDDAO levelDao = persistanceManager.getSnmpLevelOIDDAO();
		SNMPDAO snmpDao = persistanceManager.getSNMPDAO();

		SnmpRelationshipDAO relationshipDAO = persistanceManager.getSnmpSnmpRelationshipDAO();
		
		List<SnmpLevelOID> dbLevels = new ArrayList<SnmpLevelOID>();
		for (YamlSnmpLevelOID levelOid : yamlSnmpLevelOids) {
			
			SNMP snmp = snmpDao.findByName(levelOid.getContextOID());
			if (snmp == null) {
				logger.error("OID not found for " + levelOid.getContextOID()
						+ " Unable to create SnmpLevelOID!");

				throw new IntegerException(null,
						YamlParserErrrorCodes.ContextOidNotFound);
			}

			SnmpLevelOID dbLevelOid = findSnmpLevelOID(
					levelOid.getContextOID(), topLevels);

			if (dbLevelOid == null) {
				dbLevelOid = new SnmpLevelOID();
				dbLevelOid.setContextOID(getSnmpOid(levelOid.getContextOID()));
			}
			
			if (levelOid.getCategory() != null) {
				try {
					dbLevelOid.setCategory(managementObjectManager.getCategoryByName(levelOid
							.getCategory()));
				} catch (IllegalArgumentException e) {
					logger.error("Unable to create category from "
							+ levelOid.getCategory());
					throw e;
				}
			}
			
			dbLevelOid.setDescriminatorOID(getSnmpOid(levelOid.getDescriminatorOID()));
			dbLevelOid.setGlobalDiscriminatorOID(getSnmpOid(levelOid.getGlobalDescriminatorOID()));
			
			dbLevelOid
					.setDisriminators(createDiscriminatorList(
							levelOid.getDisriminators(),
							dbLevelOid.getDisriminators()));

			if (levelOid.getContainmentRelationship() != null) {
				SnmpRelationship contianmentRelationship = createContainmentRelation(
						levelOid.getContainmentRelationship(),
						dbLevelOid.getRelationToParent(), topLevels);

				dbLevelOid.setRelationToParent(contianmentRelationship);
			} else if (levelOid.getParentChildRelationship() != null) {

				SnmpRelationship parentChildRelationship = createParentChildRelationship(
						levelOid.getParentChildRelationship(),
						dbLevelOid.getRelationToParent());
				parentChildRelationship = relationshipDAO.update(parentChildRelationship);
				dbLevelOid.setRelationToParent(parentChildRelationship);
			} else {

			}
			if (levelOid.getChildren() != null)
				dbLevelOid.setChildren(createSnmpLevelOIDs(
						levelOid.getChildren(), topLevels));

			if ( levelOid.getAssociations() != null ) {
				
				SnmpRelationshipDAO relDao = persistanceManager.getSnmpSnmpRelationshipDAO();
				ServiceElementAssociationTypeDAO assTypeDao = persistanceManager.getServiceElementAssociationTypeDAO();
				SnmpAssociationDAO assDao = persistanceManager.getSnmpAssociationDAO();
				
				List<SnmpAssociation> associations = new ArrayList<>();
				for ( YamlSnmpAssociation yass : levelOid.getAssociations() ) {
					
					SnmpAssociation sass = new SnmpAssociation();
					sass.setName(yass.getName());
					
					ServiceElementAssociationType assType = assTypeDao.findByName(yass.getAssociationName());
					if (assType != null)
						sass.setAssociationTypeId(assType.getID());
					
					if ( yass.getContainmentRelationship() != null ) {
						SnmpContainmentRelation contaimentRel = new SnmpContainmentRelation();
						contaimentRel.setName(yass.getName());
						contaimentRel.setMappingType(RelationMappingTypeEnum.InstanceOnly);
						SNMP s = snmpDao.findByName(yass.getContainmentRelationship().getMappingOid());
						contaimentRel.setMappingOid(s);
						SNMPTable st = (SNMPTable) snmpDao.findByName(yass.getContainmentRelationship().getMappingTable());
						contaimentRel.setMappingTable(st);
						
						contaimentRel = relDao.update(contaimentRel);
						sass.setRelationToAssociation(contaimentRel);
					}
					sass = assDao.update(sass);
					associations.add(sass);
				}
				if ( associations.size() > 0 ) {
				     dbLevelOid.setAssociations(associations);
				}
			}
			
			dbLevelOid = levelDao.update(dbLevelOid);
			dbLevels.add(dbLevelOid);
			
			
		}

		return dbLevels;
	}

	/**
	 * Helper method to create a parent child relationship.
	 * 
	 * @param parentChildRelationship. Input data to create the parent child relationship from
	 * @param relationToParent. existing SnmpRelation to update.
	 * @throws IntegerException
	 */
	private SnmpRelationship createParentChildRelationship(
			YamlSnmpParentChildRelationship yamlParentChildRelation,
			SnmpRelationship relationToParent) throws IntegerException {

		if (yamlParentChildRelation == null)
			return relationToParent;

		if (relationToParent == null
				|| !(relationToParent instanceof SnmpParentChildRelationship))
			relationToParent = new SnmpParentChildRelationship();

		SnmpParentChildRelationship parentChildRelation = (SnmpParentChildRelationship) relationToParent;

		parentChildRelation
				.setContainmentOid(getSnmpOid(yamlParentChildRelation
						.getContainmentOid()));

		parentChildRelation.setModelOid(getSnmpOid(yamlParentChildRelation
				.getModelOid()));

		parentChildRelation.setSiblingOid(getSnmpOid(yamlParentChildRelation
				.getSiblingOid()));

		parentChildRelation
				.setSoftwareVersionOid(getSnmpOid(yamlParentChildRelation
						.getSoftwareVersionOid()));

		parentChildRelation.setSubTypeOid(getSnmpOid(yamlParentChildRelation
				.getSubTypeOid()));

		if (yamlParentChildRelation.getMappingType() != null) {

			parentChildRelation.setMappingType(RelationMappingTypeEnum
					.valueOf(yamlParentChildRelation.getMappingType()));
		}

		return parentChildRelation;
	}

	/**
	 * Helper method to create the SnmpRelationship.
	 * 
	 * @param yamlSnmpRelationship. Input data
	 * @param relationToParent. Existing SnmpRelationship. If not not it will be updated.
	 * @return Created/updated SnmpRelationship.
	 * 
	 * @throws IntegerException
	 */
	private SnmpRelationship createContainmentRelation(
			YamlSnmpContainmentRelation yamlSnmpRelationship,
			SnmpRelationship dbRelationToParent, List<SnmpLevelOID> levels)
			throws IntegerException {

		if (yamlSnmpRelationship == null)
			return dbRelationToParent;

		if (dbRelationToParent == null
				|| !(dbRelationToParent instanceof SnmpContainmentRelation))
			dbRelationToParent = new SnmpContainmentRelation();

		SnmpContainmentRelation containmentRelation = (SnmpContainmentRelation) dbRelationToParent;

		containmentRelation.setMappingOid(getSnmpOid(yamlSnmpRelationship
				.getMappingOid()));

		containmentRelation
				.setMappingTable((SNMPTable) getSnmpOid(yamlSnmpRelationship
						.getMappingTable()));

		if (yamlSnmpRelationship.getChildTable() != null) {
			SnmpLevelOID childSnmpLevel = findSnmpLevelOID(
					yamlSnmpRelationship.getChildTable(), levels);
			containmentRelation.setChildTable(childSnmpLevel);
		}
		
		if ( yamlSnmpRelationship.getValueIsParent() != null ) {			
			containmentRelation.setValueIsParent(Boolean.valueOf(yamlSnmpRelationship.getValueIsParent()));
		}
		else {
			containmentRelation.setValueIsParent(false);
		}
		
		if (yamlSnmpRelationship.getMappingType() != null) {

			containmentRelation.setMappingType(RelationMappingTypeEnum
					.valueOf(yamlSnmpRelationship.getMappingType()));
		}
		else if ( yamlSnmpRelationship.getMappingType() == null ) {
			
			containmentRelation.setMappingType(RelationMappingTypeEnum.FullOid);
		}
		return containmentRelation;
	}

	/**
	 * Helper method to create SnmpServiceElementTypeDiscriminator
	 * @param disriminators. Input data.
	 * @param list. List of existing SnmpServiceElementTypeDiscriminator's
	 * @return Created/updated SnmpServiceElementTypeDiscriminator's
	 * @throws IntegerException
	 */
	private List<SnmpServiceElementTypeDiscriminator> createDiscriminatorList(
			List<YamlSnmpServiceElementTypeDiscriminator> disriminators,
			List<SnmpServiceElementTypeDiscriminator> list)
			throws IntegerException {

		if (disriminators == null)
			return null;

		if (list == null)
			list = new ArrayList<SnmpServiceElementTypeDiscriminator>();

		ServiceElementTypeDAO dao = persistanceManager.getServiceElementTypeDAO();
		
		for (YamlSnmpServiceElementTypeDiscriminator yamlSnmpServiceElementTypeDiscriminator : disriminators) {
			
			SnmpServiceElementTypeDiscriminator dbDiscriminator = findDiscriminator(
					yamlSnmpServiceElementTypeDiscriminator, list);
			if (dbDiscriminator == null) {
				dbDiscriminator = new SnmpServiceElementTypeDiscriminator();
				dbDiscriminator.setDiscriminatorValue(createDiscrimintatorValue(yamlSnmpServiceElementTypeDiscriminator));
				
				if ( yamlSnmpServiceElementTypeDiscriminator.getServiceElementTypeName() != null ) {
							
					ServiceElementType dbSet = dao.findByName(yamlSnmpServiceElementTypeDiscriminator.getServiceElementTypeName());
					if (dbSet == null) {
						logger.error("Error ServiceElement " + yamlSnmpServiceElementTypeDiscriminator.getServiceElementTypeName() 
								+ " not found!! Can not add SnmpLevelOID discriminator " );
						
						continue;
					}
					dbDiscriminator.setServiceElementTypeId(dbSet.getID());
				}
				else if ( yamlSnmpServiceElementTypeDiscriminator.getServiceElementType() != null ) {
					edu.harvard.integer.common.yaml.vendorcontainment.YamlServiceElementType yset =  yamlSnmpServiceElementTypeDiscriminator.getServiceElementType();	
					ID sid = createServiceElement(yset);
					dbDiscriminator.setServiceElementTypeId(sid);
				}
				
				if ( yamlSnmpServiceElementTypeDiscriminator.getGlobaldiscriminatorValue() != null ) {
					
					dbDiscriminator.setGlobaldiscriminatorValue(createGlobleDiscrimintatorValue(yamlSnmpServiceElementTypeDiscriminator));
				}
				
				list.add(dbDiscriminator);
			}

		}

		return list;
	}

	/**
	 * Helper method to create ServiceElement.
	 * @param serviceElementType. Input data.
	 * @return ID of the created ServiceElement
	 * @throws IntegerException
	 */
	private ID createServiceElement(
			edu.harvard.integer.common.yaml.vendorcontainment.YamlServiceElementType serviceElementType)
			throws IntegerException {

		if (serviceElementType == null)
			return null;

		ServiceElementTypeDAO dao = persistanceManager
				.getServiceElementTypeDAO();
		ServiceElementType dbSet = dao.findByName(serviceElementType.getName());
		if (dbSet != null) {
			return dbSet.getID();
		}

		dbSet = new ServiceElementType();
		dbSet.setName(serviceElementType.getName());
		dbSet.setAttributeIds(createAttributeList(
				serviceElementType.getAttributes(),
				serviceElementType.getName()));
		if (serviceElementType.getFieldReplaceableUnit() != null
				&& (serviceElementType.getFieldReplaceableUnit()
						.equalsIgnoreCase("Yes") || serviceElementType
						.getFieldReplaceableUnit().equalsIgnoreCase("true")))
			dbSet.setFieldReplaceableUnit(FieldReplaceableUnitEnum.Yes);
		else
			dbSet.setFieldReplaceableUnit(FieldReplaceableUnitEnum.No);

		dbSet.setDescription(serviceElementType.getDescription());

		dbSet.setDefaultNameCababilityId(getCapability(serviceElementType
				.getDefaultNameCabability()));
		if (serviceElementType.getCategory() != null)
			dbSet.setCategory(managementObjectManager.getCategoryByName(serviceElementType
					.getCategory()));

		dbSet.setUniqueIdentifierCapabilities(createAttributeList(
				serviceElementType.getUniqueIdentifierCapabilities(),
				serviceElementType.getName()));

		dbSet = dao.update(dbSet);

		return dbSet.getID();
	}

	/**
	 * Get the capability with the given name from the database if one exists.
	 * 
	 * @param defaultNameCabability. Name of capability to get.
	 * @return ID of capability found in the database or null if not found.
	 */
	private ID getCapability(String defaultNameCabability) {
		CapabilityDAO dao = persistanceManager.getCapabilityDAO();
		Capability capability = dao.findByName(defaultNameCabability);

		if (capability != null)
			return capability.getID();
		else {
			logger.error("Capability not found with name "
					+ defaultNameCabability);
		}

		return null;
	}

	/**
	 * Helper method to create a list of attributes for a service element.
	 * @param attributes. Input data.
	 * @return List of ID's for the attributes (SNMP) created.
	 * @throws IntegerException
	 */
	private List<ID> createAttributeList(List<String> attributes,
			String serviceElementName) throws IntegerException {
		List<ID> ids = new ArrayList<ID>();

		if (attributes == null)
			return ids;

		for (String string : attributes) {
			SNMP oid = getSnmpOid(string);
			if (oid != null)
				ids.add(oid.getID());
			else {
				logger.error("OID " + string
						+ " Not found!! Can not add to ServiceElement "
						+ serviceElementName);
			}
		}

		return ids;
	}

	/**
	 * Helper method to create a SnmpServiceElementTypeDiscriminatorValue
	 * @param yamlDiscriminator. input data.
	 * @return Created SnmpServiceElementTypeDiscriminatorValue.
	 */
	private SnmpServiceElementTypeDiscriminatorValue<?> createDiscrimintatorValue(
			YamlSnmpServiceElementTypeDiscriminator yamlDiscriminator) {
		SnmpServiceElementTypeDiscriminatorStringValue stringValue = new SnmpServiceElementTypeDiscriminatorStringValue();
		stringValue.setValue(yamlDiscriminator.getDiscriminatorValue());
		stringValue.setName(yamlDiscriminator.getDiscriminatorValue());

		return stringValue;
	}
	
	
	/**
	 * Helper method to create a SnmpServiceElementTypeDiscriminatorValue
	 * @param yamlDiscriminator. input data.
	 * @return Created SnmpServiceElementTypeDiscriminatorValue.
	 */
	private SnmpServiceElementTypeDiscriminatorValue<?> createGlobleDiscrimintatorValue(
			YamlSnmpServiceElementTypeDiscriminator yamlDiscriminator) {
		
		SnmpServiceElementTypeDiscriminatorStringValue stringValue = new SnmpServiceElementTypeDiscriminatorStringValue();
		stringValue.setValue(yamlDiscriminator.getGlobaldiscriminatorValue());
		stringValue.setName(yamlDiscriminator.getGlobaldiscriminatorValue());

		return stringValue;
	}
	
	

	/**
	 * Helper method to find an existing SnmpServiceElementTypeDiscriminator 
	 * in the database.
	 * @param yamlDescriminator. Input data.
	 * @param descrimintators. Input data.
	 * @return List of SnmpServiceElementTypeDiscriminator's created or found in
	 * the database.
	 */
	private SnmpServiceElementTypeDiscriminator findDiscriminator(
			YamlSnmpServiceElementTypeDiscriminator yamlDescriminator,
			List<SnmpServiceElementTypeDiscriminator> descrimintators) {

		for (SnmpServiceElementTypeDiscriminator snmpServiceElementTypeDiscriminator : descrimintators) {
			
			boolean findDiscrValue = false;
			String discriminatorValue = null;
			if ( snmpServiceElementTypeDiscriminator.getDiscriminatorValue() != null &&
					snmpServiceElementTypeDiscriminator.getDiscriminatorValue().getValue() != null) {
				discriminatorValue = snmpServiceElementTypeDiscriminator.getDiscriminatorValue().getValue().toString();
			}
			if ( discriminatorValue != null && discriminatorValue.equals(yamlDescriminator.getDiscriminatorValue())) {
				findDiscrValue = true;
			}
			else if ( discriminatorValue == null ) {
				findDiscrValue = true;
			}
			
			if ( findDiscrValue ) {
				
				if ( snmpServiceElementTypeDiscriminator.getGlobaldiscriminatorValue() != null ) {
					discriminatorValue = snmpServiceElementTypeDiscriminator.getGlobaldiscriminatorValue().getValue().toString();
					if ( discriminatorValue.equals(yamlDescriminator.getGlobaldiscriminatorValue()) ) {
						return snmpServiceElementTypeDiscriminator;
					}
				}
				else if ( snmpServiceElementTypeDiscriminator.getGlobaldiscriminatorValue() == null ) {
					return snmpServiceElementTypeDiscriminator;
				}
			}
		}

		return null;
	}
	
	

	/**
	 * Helper method to find a SnmpLeveleOID in the passed in list of SnmpLevelOID's
	 * @param contextOidName. Name to find in the list.
	 * @param levels. List of SnmpLevelOID's to look in.
	 * @return SnmpLevelOID found in list or null if not found.
	 */
	private SnmpLevelOID findSnmpLevelOID(String contextOidName,
			List<SnmpLevelOID> levels) {
		if (levels == null)
			return null;

		for (SnmpLevelOID snmpLevelOID : levels) {
			if (snmpLevelOID.getContextOID().getName().equals(contextOidName))
				return snmpLevelOID;

			// Check the child SnmpLevelOID's for this context OID
			if (snmpLevelOID.getChildren() != null) {
				SnmpLevelOID childLevel = findSnmpLevelOID(contextOidName,
						snmpLevelOID.getChildren());

				if (childLevel != null)
					return childLevel;
			}
		}

		return null;
	}

	/**
	 * Return the SNMP object for the OID given.
	 * 
	 * @param oidString
	 * @return
	 * @throws IntegerException
	 */
	private SNMP getSnmpOid(String oidString) throws IntegerException {
		SNMPDAO snmpdao = persistanceManager.getSNMPDAO();
		SNMP findByName = snmpdao.findByName(oidString);
		if (findByName == null)
			findByName = snmpdao.findByOid(oidString);

		if (findByName != null)
			return findByName;

		return null;
	}
	
	@Override
	public String loadCategory(String content) throws IntegerException {
		Yaml yaml = new Yaml(new CustomClassLoaderConstructor(
				YamlCategory.class, getClass().getClassLoader()));

		YamlCategory yamlCategory = null;

		try {
			yamlCategory = (YamlCategory) yaml.load(content);
		} catch (Throwable e) {
			logger.error("Unexpected error reading in YAML! " + e.toString());
			e.printStackTrace();
			throw new IntegerException(e, YamlParserErrrorCodes.ParsingError);
		}

		logger.info("YAML Object is " + yamlCategory.getClass().getName());

		HashMap<String, YamlCategory> categories = new HashMap<String, YamlCategory>();
		HashMap<String, Category> dbCategories = new HashMap<String, Category>();
		
		YamlCategoryParser parser = new YamlCategoryParser(categories, dbCategories, persistanceManager.getCategoryDAO());
		
		// First parse the parents list to add the child categories to the parents.
		parser.parseParentCategory(yamlCategory);
		
		// Parse the parents and add to the database.
		parser.parseCategory(yamlCategory);

		parser.printCategories(yamlCategory, "");
		
		return "Success";

	}

	@Override
	public String importLocation(String content) throws IntegerException {
		Yaml yaml = new Yaml(new CustomClassLoaderConstructor(
				YamlLocation[].class, getClass().getClassLoader()));

		YamlLocation[] yamlLocations = null;

		try {
			yamlLocations = (YamlLocation[]) yaml.load(content);
		} catch (Throwable e) {
			logger.error("Unexpected error reading in YAML! " + e.toString());
			e.printStackTrace();
			throw new IntegerException(e, YamlParserErrrorCodes.ParsingError);
		}

		logger.info("YAML Object is " + yamlLocations.getClass().getName());
		
		YamlLocationParser parser = new YamlLocationParser(yamlLocations);
		
		return parser.parse();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String importYAML(String data, Class<? extends YamlBaseInfoInterface> objectType, YamlParserInterface parser) throws IntegerException {
		Yaml yaml = new Yaml(new CustomClassLoaderConstructor(
				objectType, getClass().getClassLoader()));

		YamlBaseInfoInterface yamlObject = null;

		try {
			yamlObject = (YamlBaseInfoInterface) yaml.load(data);
		} catch (Throwable e) {
			logger.error("Unexpected error reading in YAML! " + e.toString());
			e.printStackTrace();
			throw new IntegerException(e, YamlParserErrrorCodes.ParsingError);
		}

		logger.info("YAML Object is " + yamlObject.getClass().getName());
	
		return parser.parse(yamlObject);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String importYAML(String data, Class<? extends YamlBaseInfoInterface[]> objectType, YamlListParserInterface parser) throws IntegerException {
		Yaml yaml = new Yaml(new CustomClassLoaderConstructor(
				objectType, getClass().getClassLoader()));

		YamlBaseInfoInterface[] yamlObject = null;

		try {
			yamlObject = (YamlBaseInfoInterface[]) yaml.load(data);
		} catch (Throwable e) {
			logger.error("Unexpected error reading in YAML! " + e.toString());
			e.printStackTrace();
			throw new IntegerException(e, YamlParserErrrorCodes.ParsingError);
		}

		logger.info("YAML Object is " + yamlObject.getClass().getName());
	
		return parser.parse(yamlObject);
	}
}
