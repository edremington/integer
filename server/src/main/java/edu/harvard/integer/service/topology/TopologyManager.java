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

package edu.harvard.integer.service.topology;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.deser.std.NumberDeserializers.IntegerDeserializer;

import edu.harvard.integer.common.Address;
import edu.harvard.integer.common.ID;
import edu.harvard.integer.common.exception.IntegerException;
import edu.harvard.integer.common.properties.IntegerProperties;
import edu.harvard.integer.common.topology.InterDeviceLink;
import edu.harvard.integer.common.topology.InterNetworkLink;
import edu.harvard.integer.common.topology.LayerTypeEnum;
import edu.harvard.integer.common.topology.MapItemPosition;
import edu.harvard.integer.common.topology.Network;
import edu.harvard.integer.common.topology.NetworkInformation;
import edu.harvard.integer.common.topology.Path;
import edu.harvard.integer.common.topology.ServiceElement;
import edu.harvard.integer.common.topology.TopologyElement;
import edu.harvard.integer.common.user.Location;
import edu.harvard.integer.service.BaseManager;
import edu.harvard.integer.service.distribution.ManagerTypeEnum;
import edu.harvard.integer.service.persistance.PersistenceManagerInterface;
import edu.harvard.integer.service.persistance.dao.topology.InterDeviceLinkDAO;
import edu.harvard.integer.service.persistance.dao.topology.InterNetworkLinkDAO;
import edu.harvard.integer.service.persistance.dao.topology.MapItemPositionDAO;
import edu.harvard.integer.service.persistance.dao.topology.NetworkDAO;
import edu.harvard.integer.service.persistance.dao.topology.PathDAO;
import edu.harvard.integer.service.persistance.dao.topology.ServiceElementDAO;
import edu.harvard.integer.service.persistance.dao.topology.TopologyElementDAO;
import edu.harvard.integer.service.persistance.dao.user.LocationDAO;
import edu.harvard.integer.service.topology.layout.LayoutTypeEnum;

/**
 * 
 * @author David Taylor
 * 
 */
@Stateless
public class TopologyManager extends BaseManager implements
		TopologyManagerLocalInterface, TopologyManagerRemoteInterface {

	@Inject
	private Logger logger;

	@Inject
	private PersistenceManagerInterface persistenceManager;

	/**
	 * @param managerType
	 */
	public TopologyManager() {
		super(ManagerTypeEnum.TopologyManager);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.harvard.integer.service.topology.TopologyManagerInterface#getAllNetworks
	 * ()
	 */
	@Override
	public Network[] getAllNetworks() throws IntegerException {
		NetworkDAO dao = persistenceManager.getNetworkDAO();

		Network[] networks = dao.findAll();

		networks = dao.copyArray(networks);

		return networks;
	}

	@Override
	public Network getNetworkByAddress(Address networkAddress)
			throws IntegerException {

		NetworkDAO dao = persistenceManager.getNetworkDAO();

		Network networks = dao.findByAddress(networkAddress);

		return networks;
	}

	@Override
	public Network getNetworkByName(String name) throws IntegerException {
		NetworkDAO dao = persistenceManager.getNetworkDAO();
		
		return dao.findByName(name);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.harvard.integer.service.topology.TopologyManagerInterface#
	 * getNetworkInformation()
	 */
	@Override
	public NetworkInformation getNetworkInformation() throws IntegerException {
		NetworkInformation networkInfo = new NetworkInformation();

		networkInfo.setNetworks(getAllNetworks());

		InterNetworkLinkDAO linkDao = persistenceManager
				.getInterNetworkLinkDAO();
		networkInfo.setLinks(linkDao.copyArray((InterNetworkLink[]) linkDao
				.findAll()));

		logger.info("Found " + networkInfo.getNetworks().length + " networks "
				+ networkInfo.getLinks().length + " Links");
		if (logger.isDebugEnabled()) {
			for (Network network : networkInfo.getNetworks()) {

				logger.debug("Network: " + network.getName());

				for (InterNetworkLink link : networkInfo.getLinks()) {
					if (link.getSourceNetworkId() != null
							&& link.getSourceNetworkId().equals(network.getID()))
						logger.debug("    Link: " + link.getName());
				}
			}
		}
		
		LayoutGenerator generator = new LayoutGenerator(IntegerProperties.getInstance().getEnumProperty(LayoutTypeEnum.CircleLayout));
		networkInfo.setPositions(generator.generatePositions(networkInfo));
		
		for (ID id : networkInfo.getPositions().keySet()) {
			MapItemPosition mapItemPosition = networkInfo.getPositions().get(id);
			if (mapItemPosition != null)
				logger.info("MapItem " + mapItemPosition.getItemId() + " Xpos " + mapItemPosition.getXposition() + " Ypos " + mapItemPosition.getYposition());
		}
		
		return networkInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.harvard.integer.service.topology.TopologyManagerInterface#updateNetwork
	 * (edu.harvard.integer.common.topology.Network)
	 */
	@Override
	public Network updateNetwork(Network network) throws IntegerException {
		NetworkDAO dao = persistenceManager.getNetworkDAO();

		return dao.update(network);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.harvard.integer.service.topology.TopologyManagerInterface#
	 * getAllInterDeviceLinks()
	 */
	@Override
	public InterDeviceLink[] getAllInterDeviceLinks() throws IntegerException {
		InterDeviceLinkDAO dao = persistenceManager.getInterDeviceLinkDAO();

		return dao.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.harvard.integer.service.topology.TopologyManagerInterface#
	 * getLinksBySourceDestAddress(edu.harvard.integer.common.Address,
	 * edu.harvard.integer.common.Address)
	 */
	@Override
	public InterDeviceLink[] getInterDeviceLinksBySourceDestAddress(
			Address sourceAddress, Address destAddress) throws IntegerException {
		InterDeviceLinkDAO dao = persistenceManager.getInterDeviceLinkDAO();

		return dao.findBySourceDestAddress(sourceAddress, destAddress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.harvard.integer.service.topology.TopologyManagerInterface#
	 * getInterDeviceLinksBySourceDestNetworkIDs(edu.harvard.integer.common.ID,
	 * edu.harvard.integer.common.ID)
	 */
	@Override
	public InterDeviceLink[] getInterDeviceLinksBySourceDestServiceElementIDs(
			ID sourceId, ID destId) throws IntegerException {
		InterDeviceLinkDAO dao = persistenceManager.getInterDeviceLinkDAO();

		return dao.findBySourceDestServiceElementIDs(sourceId, destId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.harvard.integer.service.topology.TopologyManagerInterface#
	 * getInterNetworkLinksBySourceDestNetworkIDs(edu.harvard.integer.common.ID,
	 * edu.harvard.integer.common.ID)
	 */
	@Override
	public InterNetworkLink[] getInterNetworkLinksBySourceDestNetworkIDs(
			ID sourceId, ID destId) throws IntegerException {
		InterNetworkLinkDAO dao = persistenceManager.getInterNetworkLinkDAO();

		return dao.findBySourceDestID(sourceId, destId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.harvard.integer.service.topology.TopologyManagerInterface#
	 * updateInterDeviceLink
	 * (edu.harvard.integer.common.topology.InterDeviceLink)
	 */
	@Override
	public InterDeviceLink updateInterDeviceLink(InterDeviceLink interDeviceLink)
			throws IntegerException {
		InterDeviceLinkDAO dao = persistenceManager.getInterDeviceLinkDAO();

		interDeviceLink = dao.update(interDeviceLink);

		checkNetworks(interDeviceLink);

		return interDeviceLink;
	}

	/**
	 * @param interDeviceLink
	 * @throws IntegerException
	 */
	private void checkNetworks(InterDeviceLink interDeviceLink)
			throws IntegerException {

		NetworkDAO networkDao = persistenceManager.getNetworkDAO();

		if (logger.isDebugEnabled() && interDeviceLink.getSourceAddress() != null)
			logger.debug("Create network name from "
					+ interDeviceLink.getSourceAddress().getAddress() + " and "
					+ interDeviceLink.getSourceAddress().getMask());

		String sourceNetworkName = interDeviceLink.getSourceServiceElementId().getName(); // Network.createName(interDeviceLink
				// .getSourceAddress());
		

		String destNetworkName = interDeviceLink.getDestinationServiceElementId().getName(); // Network.createName(interDeviceLink
				// .getDestinationAddress());

		//if (logger.isDebugEnabled())
			logger.info("Create network name from "
					+ sourceNetworkName + " and "
					+ destNetworkName);

		
		if (sourceNetworkName == null || sourceNetworkName.equals("N/A")) {
			if (destNetworkName != null && !destNetworkName.equals("N/A"))
				sourceNetworkName = destNetworkName;
			else
				return;
		}
		
		if (destNetworkName == null || destNetworkName.equals("N/A")) {
			if (sourceNetworkName != null && !sourceNetworkName.equals("N/A"))
				destNetworkName = sourceNetworkName;
			else
				return;
		}

		Network sourceNetwork = networkDao.findByName(sourceNetworkName);
		if (sourceNetwork == null)
			sourceNetwork = networkDao.update(createNetwork(sourceNetworkName, interDeviceLink
					.getSourceAddress()));

		addInterDeviceLinkToNetwork(interDeviceLink, sourceNetwork);
		addServiceElementToNetwork(interDeviceLink.getSourceServiceElementId(),
				sourceNetwork);

		Network destNetwork = networkDao.findByName(destNetworkName);
		if (destNetwork == null)
			destNetwork = networkDao.update(createNetwork(destNetworkName, interDeviceLink
					.getDestinationAddress()));

		addServiceElementToNetwork(
				interDeviceLink.getDestinationServiceElementId(), destNetwork);

		interDeviceLink.setSourceNetworkId(sourceNetwork.getID());
		interDeviceLink.setDestinationNetworkId(destNetwork.getID());

		addInterNetworkLink(interDeviceLink, sourceNetworkName, destNetworkName);

	}

	private void addInterNetworkLink(InterDeviceLink interDeviceLink,
			String sourceNetworkName, String destNetworkName)
			throws IntegerException {
		InterNetworkLinkDAO linkDao = persistenceManager
				.getInterNetworkLinkDAO();

		InterNetworkLink[] networkLinks = linkDao.findBySourceDestID(
				interDeviceLink.getSourceNetworkId(),
				interDeviceLink.getDestinationNetworkId());

		if (networkLinks == null || networkLinks.length == 0) {
			InterNetworkLink link = new InterNetworkLink();
			link.setCreated(new Date());
			link.setModified(new Date());

			if (interDeviceLink.getSourceAddress() != null) 
				link.setSourceAddress(new Address(Address.getSubNet(interDeviceLink
						.getSourceAddress()), interDeviceLink.getSourceAddress()
						.getMask()));
			
			link.setSourceNetworkId(interDeviceLink.getSourceNetworkId());

			if (interDeviceLink.getDestinationAddress() != null)
				link.setDestinationAddress(new Address(Address
						.getSubNet(interDeviceLink.getDestinationAddress()),
						interDeviceLink.getDestinationAddress().getMask()));

			link.setDestinationNetworkId(interDeviceLink
					.getDestinationNetworkId());

			link.setLayer(LayerTypeEnum.Two);
			link.setName(sourceNetworkName + " - " + destNetworkName);

			linkDao.update(link);
		}
	}

	/**
	 * @param sourceServiceElementId
	 * @param sourceNetwork
	 * @throws IntegerException
	 */
	private void addServiceElementToNetwork(ID sourceServiceElementId,
			Network sourceNetwork) throws IntegerException {

		if (sourceServiceElementId == null)
			return;

		if (sourceNetwork == null)
			return;

		if (sourceNetwork.getServiceElements() == null)
			sourceNetwork.setServiceElements(new ArrayList<ServiceElement>());

		ServiceElementDAO dao = persistenceManager.getServiceElementDAO();
		ServiceElement serviceElement = dao.findById(sourceServiceElementId);

		addServiceElementToNetwork(serviceElement, sourceNetwork);
	}

	private void addServiceElementToNetwork(ServiceElement serviceElement,
			Network sourceNetwork) throws IntegerException {

		if (serviceElement == null)
			return;

		if (sourceNetwork == null)
			return;

		if (sourceNetwork.getServiceElements() == null)
			sourceNetwork.setServiceElements(new ArrayList<ServiceElement>());

		boolean foundIt = false;
		for (ServiceElement netServieElement : sourceNetwork
				.getServiceElements()) {
			if (netServieElement.getID().equals(serviceElement.getID())) {
				foundIt = true;
				break;
			}
		}
		if (!foundIt)
			sourceNetwork.getServiceElements().add(serviceElement);
	}

	/**
	 * @param interDeviceLink
	 * @param sourceNetwork
	 */
	private void addInterDeviceLinkToNetwork(InterDeviceLink interDeviceLink,
			Network sourceNetwork) {

		if (sourceNetwork.getInterDeviceLinks() == null)
			sourceNetwork.setInterDeviceLinks(new ArrayList<InterDeviceLink>());

		boolean foundLink = false;
		for (InterDeviceLink link : sourceNetwork.getInterDeviceLinks()) {

			if (link.getSourceServiceElementId() != null
					&& link.getSourceServiceElementId().equals(
							interDeviceLink.getSourceServiceElementId())
					&& link.getDestinationServiceElementId().equals(
							interDeviceLink.getDestinationServiceElementId()))
				foundLink = true;
		}

		if (!foundLink)
			sourceNetwork.getInterDeviceLinks().add(interDeviceLink);

	}

	private Network createNetwork(String name, Address address) {
		Network network = new Network();
		network.setCreated(new Date());
		network.setModified(new Date());
		network.setName(name);
		network.setAddress(address);
		network.setServiceElements(new ArrayList<ServiceElement>());
		network.setInterDeviceLinks(new ArrayList<InterDeviceLink>());

		return network;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.harvard.integer.service.topology.TopologyManagerInterface#
	 * getAllTopologyElements()
	 */
	@Override
	public TopologyElement[] getAllTopologyElements() throws IntegerException {
		TopologyElementDAO dao = persistenceManager.getTopologyElementDAO();

		return dao.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.harvard.integer.service.topology.TopologyManagerInterface#
	 * getTopologyElementsByServiceElement(edu.harvard.integer.common.ID)
	 */
	@Override
	public TopologyElement[] getTopologyElementsByServiceElement(
			ID serviceElementId) throws IntegerException {
		TopologyElementDAO dao = persistenceManager.getTopologyElementDAO();

		return dao.findByServiceElementID(serviceElementId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.harvard.integer.service.topology.TopologyManagerInterface#
	 * updateTopologyElement
	 * (edu.harvard.integer.common.topology.TopologyElement)
	 */
	@Override
	public TopologyElement updateTopologyElement(TopologyElement topologyElement)
			throws IntegerException {
		TopologyElementDAO dao = persistenceManager.getTopologyElementDAO();

		return dao.update(topologyElement);
	}

	@Override
	public TopologyElement updateTopologyElementForHost(TopologyElement topologyElement)
			throws IntegerException {

		NetworkDAO networkDao = persistenceManager.getNetworkDAO();
		ServiceElementDAO serviceElementDao = persistenceManager
				.getServiceElementDAO();

		if (topologyElement.getAddress() != null) {
			for (Address address : topologyElement.getAddress()) {
				String sourceNetworkName = Network.createName(address);
				Network network = networkDao.findByName(sourceNetworkName);

				if (network == null) {
					network = createNetwork(topologyElement.getServiceElementId().getName(), address);
					network = networkDao.update(network);
				}

				ServiceElement topLevelServiceElement = getTopLevelServiceElementFor(
						topologyElement.getServiceElementId(),
						serviceElementDao);
				addServiceElementToNetwork(topLevelServiceElement, network);
				networkDao.update(network);
			}
		}

		return updateTopologyElement(topologyElement);
	}
	
	private ServiceElement getTopLevelServiceElementFor(ID childId,
			ServiceElementDAO serviceElementDao) throws IntegerException {
		if (childId == null)
			return null;
		
		ServiceElement parent = (ServiceElement) serviceElementDao.findParent(childId);
		
		while (parent != null) {
	

			if (parent.getParentIds() != null
					&& parent.getParentIds().size() > 0) {
				ServiceElement nextParent = getTopLevelServiceElementFor(
						parent.getID(), serviceElementDao);
				if (nextParent != null)
					parent = nextParent;
				else
					return parent;
			}
			return parent;
		}

		return parent;
	}

	@Override
	public Path updatePath(Path path) throws IntegerException {
		PathDAO dao = persistenceManager.getPathDAO();

		return dao.update(path);
	}

	@Override
	public Path[] getAllPaths() throws IntegerException {
		PathDAO dao = persistenceManager.getPathDAO();

		return dao.findAll();
	}

	@Override
	public Path getPathBySourceDestAddress(Address sourceAddress,
			Address destAddress) throws IntegerException {
		PathDAO dao = persistenceManager.getPathDAO();

		return dao.findBySourceDestAddress(sourceAddress, destAddress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.harvard.integer.service.topology.TopologyManagerInterface#getAllLocations
	 * ()
	 */
	@Override
	public Location[] getAllLocations() throws IntegerException {
		LocationDAO dao = persistenceManager.getLocationDAO();

		return dao.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.harvard.integer.service.topology.TopologyManagerInterface#updateLocation
	 * (edu.harvard.integer.common.user.Location)
	 */
	@Override
	public Location updateLocation(Location location) throws IntegerException {
		LocationDAO dao = persistenceManager.getLocationDAO();

		return dao.update(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.harvard.integer.service.topology.TopologyManagerInterface#getLocationById
	 * (edu.harvard.integer.common.ID)
	 */
	@Override
	public Location getLocationById(ID locationId) throws IntegerException {
		LocationDAO dao = persistenceManager.getLocationDAO();

		return dao.findById(locationId);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see edu.harvard.integer.service.topology.TopologyManagerInterface#getAllNetworkPositions()
	 */
	@Override
	public MapItemPosition[] getAllNetworkPositions() throws IntegerException {
		MapItemPositionDAO dao = persistenceManager.getMapItemPositionDAO();
		
		return dao.findAll();
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.harvard.integer.service.topology.TopologyManagerInterface#getPositionsByMap(edu.harvard.integer.common.ID)
	 */
	@Override
	public MapItemPosition[] getPositionsByMap(ID mapId) throws IntegerException {
		HashMap<ID, MapItemPosition> mapPositions = null;
		
		MapItemPositionDAO dao = persistenceManager.getMapItemPositionDAO();
		MapItemPosition[] positions = dao.findByMapId(mapId);
		logger.info("Found " + positions.length + " for MapId " + mapId.toDebugString());
		
		if (mapId.getIdType().getClassType().equals(Network.class.getName())) {
			mapPositions = getSubnetPostitions(mapId);
		}
		
		if (mapPositions == null) {
			return positions;
		}
		
		for (MapItemPosition pos : positions) {
			mapPositions.put(pos.getItemId(), pos);
		}
		
		List<MapItemPosition> positionList = new ArrayList<MapItemPosition>();
		
		for (ID id : mapPositions.keySet()) {
			MapItemPosition position = mapPositions.get(id);
			if (position != null) {
				positionList.add(position);
			
				logger.info("MapId " + mapId.toDebugString() 
						+ " Item " + position.getItemId().toDebugString()
						+ " X: " + position.getXposition()
						+ " Y: " + position.getYposition());
			}
		}

		return (MapItemPosition[]) positionList
				.toArray(new MapItemPosition[positionList.size()]);
	}
	
	/**
	 * @param mapPositions
	 * @param mapId
	 * @throws IntegerException 
	 */
	private HashMap<ID, MapItemPosition> getSubnetPostitions(ID mapId) throws IntegerException {
		NetworkDAO dao = persistenceManager.getNetworkDAO();
		Network network = dao.findById(mapId);
		
		HashMap<ID,MapItemPosition> generatePositions = null;
		
		if (network.getServiceElements() != null && network.getServiceElements().size() > 0) {
		
			LayoutGenerator layoutGenerator = new LayoutGenerator(IntegerProperties.getInstance().getEnumProperty(LayoutTypeEnum.CircleLayout));
			generatePositions = layoutGenerator.generatePositions(network);
		} else 
			generatePositions = new HashMap<ID, MapItemPosition>();
		
		return generatePositions;
		
	}

	/*
	 * (non-Javadoc)
	 * @see edu.harvard.integer.service.topology.TopologyManagerInterface#updateMapItemPosition(edu.harvard.integer.common.topology.MapItemPosition)
	 */
	@Override
	public MapItemPosition updateMapItemPosition(MapItemPosition position) throws IntegerException {
		MapItemPositionDAO dao = persistenceManager.getMapItemPositionDAO();
		
		MapItemPosition dbPosition = dao.findByMapIdAndItemId(position.getMapId(), position.getItemId());
		if (dbPosition == null) 
			dbPosition = position;
		else
			dao.copyFields(dbPosition, position);
		
		return dao.update(dbPosition);
	}
}
