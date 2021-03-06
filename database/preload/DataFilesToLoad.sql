
drop table if exists DataPreLoadFile;

CREATE TABLE `DataPreLoadFile` (
  `identifier` bigint(20) NOT NULL AUTO_INCREMENT,
  `classType` varchar(255) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `dataFile` varchar(255) DEFAULT NULL,
  `errorMessage` varchar(255) DEFAULT NULL,
  `fileType` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `timeLoaded` datetime DEFAULT NULL,
  `timeToLoad` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `DataPreLoadFile` (classType, `name`, `dataFile`, `fileType`, `status`, `timeLoaded`, `errorMessage`)
VALUES
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'RFC1065-SMI', 'RFC1065-SMI', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'RFC1155-SMI', 'RFC1155-SMI', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'RFC-1212', 'RFC-1212', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'RFC1213-MIB', 'RFC1213-MIB', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'SNMPv2-SMI', 'SNMPv2-SMI', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'SNMPv2-TC.my', 'SNMPv2-TC', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'SNMPv2-CONF.my', 'SNMPv2-CONF', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'SNMPv2-MIB.my', 'SNMPv2-MIB.my', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'IANAifType-MIB', 'IANAifType-MIB', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'IF-MIB.my', 'IF-MIB.my', 'MIB', NULL, NULL, NULL),
    ('edu.harvard.integer.common.persistence.DataPreLoadFile', 'SNMP-FRAMEWORK-MIB.my', 'SNMP-FRAMEWORK-MIB.my', 'MIB', NULL, NULL, NULL),
    
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'ENTITY-MIB', 'ENTITY-MIB', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'HOST-RESOURCES-MIB.my', 'HOST-RESOURCES-MIB.my', 'MIB', NULL, NULL, NULL),
 	
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'INET-ADDRESS-MIB.my', 'INET-ADDRESS-MIB.my', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'IP-MIB', 'IP-MIB', 'MIB', NULL, NULL, NULL),
 	
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'DIFFSERV-DSCP-TC.my', 'DIFFSERV-DSCP-TC.my', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'HCNUM-TC.my', 'HCNUM-TC.my', 'MIB', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'RMON-MIB', 'RMON-MIB.my', 'MIB', NULL, NULL, NULL),	
	
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO-SMI.my', 'CISCO-SMI.my', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO-ENTITY-VENDORTYPE-OID-MIB.my', 'CISCO-ENTITY-VENDORTYPE-OID-MIB.my', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO-TC.my', 'CISCO-TC.my', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO-SMI', 'CISCO-SMI.my', 'MIB', NULL, NULL, NULL),
    ('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO-CEF-TC.my', 'CISCO-CEF-TC.my', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO-FIREWALL-TC.my', 'CISCO-FIREWALL-TC.my', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO-IMAGE-TC.my', 'CISCO-IMAGE-TC.my', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO-IPSEC-TC.my', 'CISCO-IPSEC-TC.my', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO-ST-TC.my', 'CISCO-ST-TC.my', 'MIB', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO-VIDEO-TC.my', 'CISCO-VIDEO-TC.my', 'MIB', NULL, NULL, NULL),
    ('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO-VTP-MIB', 'CISCO-VTP-MIB.my', 'MIB', NULL, NULL, NULL),	
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO TC MIB', 'CISCO-TC.my', 'MIB', NULL, NULL, NULL),	
    ('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO VLAN MIB', 'CISCO-PRIVATE-VLAN-MIB.my', 'MIB', NULL, NULL, NULL),
    ('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO VTP VLAN MIB', 'CISCO-VTP-MIB.my', 'MIB', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CDP MIB', 'CISCO-CDP-MIB.my', 'MIB', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO VLAN Membership', 'CISCO-VLAN-MEMBERSHIP-MIB.my', 'MIB', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Cisco', 'CISCO-PRODUCTS-MIB.my', 'ProductMIB', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Cisco Entity', 'CISCO-ENTITY-VENDORTYPE-OID-MIB.my', 'ProductMIB', NULL, NULL, NULL),
	
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'NetSnmp MIB', 'NET-SNMP-MIB', 'MIB', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'NetSnmp', 'NET-SNMP-TC', 'ProductMIB', NULL, NULL, NULL),
	
	
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Business Service', 'BusinessServices.yaml', 'Service', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'TechnologyTree', 'technology/TechnologyTree.yaml', 'TechnologyTreeYaml', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Category', 'technology/category.yaml', 'CategoryYaml', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Location Info', 'location.yaml', 'Location', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Organization Info', 'OrganizationTree.yaml', 'Organization', NULL, NULL, NULL),
 	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Environment Level', 'Environment.yaml', 'Environment', NULL, NULL, NULL),

	
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Interface Technology', 'common/technology.yaml', 'TechnologyYaml', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Interface ServiceElementType', 'common/serviceElementType.yaml', 'ServiceElementTypeYaml', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Ipv4 Technology', 'ipv4/technology.yaml', 'TechnologyYaml', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Ipv4 ServiceElementType', 'ipv4/serviceElementType.yaml', 'ServiceElementTypeYaml', NULL, NULL, NULL),
	
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Cisco Technology', 'cisco/technology.yaml', 'TechnologyYaml', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO Common ServiceElementType', 'cisco/commonSET.yaml', 'ServiceElementTypeYaml', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'CISCO VLAN ServiceElementType', 'cisco/vlanSET.yaml', 'ServiceElementTypeYaml', NULL, NULL, NULL),
 	
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'EntityMib Technology', 'entity_mib/technology.yaml', 'TechnologyYaml', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'EntityMib ServiceElementType', 'entity_mib/serviceElementType.yaml', 'ServiceElementTypeYaml', NULL, NULL, NULL),
	
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Host Resources Containment', 'vendorcontianment/HostResourcesContainment.yaml', 'VendorContainmentYaml', NULL, NULL, NULL),
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Host Resources Containment', 'vendorcontianment/CiscoEntityContainment.yaml', 'VendorContainmentYaml', NULL, NULL, NULL),
	
	('edu.harvard.integer.common.persistence.DataPreLoadFile', 'Table Index', 'CreateIndex.sql', 'SQL', NULL, NULL, NULL);
