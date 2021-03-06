
/*
 * 
 */
package edu.harvard.integer.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.harvard.integer.common.GWTWhitelist;
import edu.harvard.integer.common.ID;
import edu.harvard.integer.common.selection.Selection;
import edu.harvard.integer.common.snmp.MIBInfo;
import edu.harvard.integer.common.snmp.SnmpGlobalReadCredential;
import edu.harvard.integer.common.topology.Capability;
import edu.harvard.integer.common.topology.DeviceDetails;
import edu.harvard.integer.common.topology.DiscoveryRule;
import edu.harvard.integer.common.topology.IpTopologySeed;
import edu.harvard.integer.common.topology.MapItemPosition;
import edu.harvard.integer.common.topology.Network;
import edu.harvard.integer.common.topology.NetworkInformation;
import edu.harvard.integer.common.topology.ServiceElement;
import edu.harvard.integer.common.topology.ServiceElementType;

/**
 * The async counterpart of <code>IntegerService</code>.
 */
public interface IntegerServiceAsync {

	/**
	 * Mib import.
	 *
	 * @param fileName the file name
	 * @param mib the mib
	 * @param standardMib the standard mib
	 * @param callback the callback
	 */
	void mibImport(String fileName, String mib, boolean standardMib, AsyncCallback<String> callback);

	/**
	 * Gets the imported mibs.
	 *
	 * @param callback the callback
	 * @return the imported mibs
	 */
	void getImportedMibs(AsyncCallback<MIBInfo[]> callback);

	/**
	 * Fake class used to force GWT to add classes to the whitelist. The white list is used to 
	 * say what classes can be serialized and sent to the client. The inherited abstract classes
	 * do not get added to the whitelist. 
	 *
	 * @param be the be
	 * @param calllback the calllback
	 * @return GWTThitelist
	 */
	void getGWTWhitelist(GWTWhitelist be, AsyncCallback<GWTWhitelist> calllback);

	/**
	 * Gets the capabilities.
	 *
	 * @param callback the callback
	 * @return the capabilities
	 */
	void getCapabilities(AsyncCallback<List<Capability>> callback);

	/**
	 * Gets the events.
	 *
	 * @param callback the callback
	 * @return the events
	 */
	void getEvents(AsyncCallback<List<Object>> callback);

	/**
	 * Gets the top level elements.
	 *
	 * @param callback the callback
	 * @return the top level elements
	 */
	void getTopLevelElements(AsyncCallback<ServiceElement[]> callback);

	/**
	 * Gets the service element by parent id.
	 *
	 * @param id the id
	 * @param callback the callback
	 * @return the service element by parent id
	 */
	void getServiceElementByParentId(ID id, AsyncCallback<ServiceElement[]> callback);

	/**
	 * Start discovery.
	 *
	 * @param callback the callback
	 */
	void startDiscovery(AsyncCallback<Void> callback);

	/**
	 * Start discovery.
	 *
	 * @param address the address
	 * @param mask the mask
	 * @param callback the callback
	 */
	void startDiscovery(String address, String mask, AsyncCallback<Void> callback);

	/**
	 * Gets the device details.
	 *
	 * @param id the id
	 * @param callback the callback
	 * @return the device details
	 */
	void getDeviceDetails(ID id, AsyncCallback<DeviceDetails> callback);

	/**
	 * Gets the blank selection.
	 *
	 * @param callback the callback
	 * @return the blank selection
	 */
	void getBlankSelection(AsyncCallback<Selection> callback);

	/**
	 * Gets the service element type by id.
	 *
	 * @param serviceElementTypeId the service element type id
	 * @param callback the callback
	 * @return the service element type by id
	 */
	void getServiceElementTypeById(ID serviceElementTypeId, AsyncCallback<ServiceElementType> callback);

	/**
	 * Gets the all networks.
	 *
	 * @param callback the callback
	 * @return the all networks
	 */
	void getAllNetworks(AsyncCallback<Network[]> callback);

	/**
	 * Gets the network information.
	 *
	 * @param callback the callback
	 * @return the network information
	 */
	void getNetworkInformation(AsyncCallback<NetworkInformation> callback);

	/**
	 * Gets the all discovery rules.
	 *
	 * @param callback the callback
	 * @return the all discovery rules
	 */
	void getAllDiscoveryRules(AsyncCallback<DiscoveryRule[]> callback);

	/**
	 * Gets the all global credentails.
	 *
	 * @param callback the callback
	 * @return the all global credentails
	 */
	void getAllGlobalCredentails(AsyncCallback<SnmpGlobalReadCredential[]> callback);

	/**
	 * Gets the all ip topology seeds.
	 *
	 * @param callback the callback
	 * @return the all ip topology seeds
	 */
	void getAllIpTopologySeeds(AsyncCallback<IpTopologySeed[]> callback);

	/**
	 * Gets the positions by network.
	 *
	 * @param networkId the network id
	 * @param callback the callback
	 * @return the positions by network
	 */
	void getPositionsByNetwork(ID networkId, AsyncCallback<MapItemPosition[]> callback);

	/**
	 * Update map item position.
	 *
	 * @param position the position
	 * @param callback the callback
	 */
	void updateMapItemPosition(MapItemPosition position, AsyncCallback<Void> callback);
	
}
