package edu.harvard.integer.common.topology;

import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import edu.harvard.integer.common.BaseEntity;

/*
 * A capability represents the lowest layer of abstraction in the system before it becomes device and protocol 
 * specific. For example, a firewall would have the ability to permit or deny access based on source subnet,
 *  port, and potentially other factors. This would be the canonical form for the system. In our case, the 
 *  it may take several device/protocol specific management objects to represent the capability. 
 *  For this reason, there may be several parameters that comprise a single capability to use our firewall
 *   example. For this reason, there is a list of the parameters with a name and description
 *
 * Another way of saying this is that a capability is also the lowest layer of abstraction that does a unit 
 * of work. In the above example port number would be one of the parameters in a CLI command to a router, but 
 * if you were to just type a port number on the command line, it would not make any sense (ignoring for 
 * the moment where you are in a mode where sometimes a single attribute is entered in a series of commands). 
 *
 * Note that the instance of the Capabilities object must come from a Capabilities template when they 
 * are initially created.
 */
@Entity
public class Capability extends BaseEntity {

	/*
	 * Listing of all the FCAPS categories an instance of this capability
	 * applies. Note that there is an extra 's' in this attribute to convey that
	 * this can also be used in the management of services.
	 * 
	 * Some types of objects lend themselves to use in several areas such as
	 * performance and fault, whereas a configuration object may be useful to
	 * configuration and security.
	 * 
	 * Also note that there is no restriction that enforces a single access type
	 * across a single capability for a service element instance. For example,
	 * CLI might be used for configuration while, SNMP might be used to monitor
	 * the configured function. While this is true, multiple access methods for
	 * configuration is not a good idea.
	 */
	@ElementCollection(targetClass = FCAPSEnum.class)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "Capability_fcaps")
	private List<FCAPSEnum> fcaps = null;

	private String description = null;

	/*
	 * List of individual parameters and descriptions for each capability
	 * associated with the service element. These are mapped to one or more
	 * ServiceElementProtocolSpecificManagementObjects depending on the
	 * protocol.
	 */
	@OneToMany
	private List<ServiceElementManagementObject> parameters = null;
	
	/**
	 * @return the fcaps
	 */
	public List<FCAPSEnum> getFcaps() {
		return fcaps;
	}

	/**
	 * @param fcaps
	 *            the fcaps to set
	 */
	public void setFcaps(List<FCAPSEnum> fcaps) {
		this.fcaps = fcaps;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

//	/**
//	 * @return the parameters
//	 */
//	public List<ServiceElementManagementObject> getParameters() {
//		return parameters;
//	}
//
//	/**
//	 * @param parameters
//	 *            the parameters to set
//	 */
//	public void setParameters(List<ServiceElementManagementObject> parameters) {
//		this.parameters = parameters;
//	}

}
