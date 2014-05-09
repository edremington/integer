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

package edu.harvard.integer.service.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.slf4j.Logger;

import edu.harvard.integer.common.discovery.DiscoveryId;
import edu.harvard.integer.common.exception.IntegerException;
import edu.harvard.integer.common.exception.NetworkErrorCodes;
import edu.harvard.integer.common.properties.IntegerProperties;
import edu.harvard.integer.common.properties.IntegerPropertyNames;
import edu.harvard.integer.common.properties.LongPropertyNames;
import edu.harvard.integer.common.topology.DiscoveryRule;
import edu.harvard.integer.common.topology.IpTopologySeed;
import edu.harvard.integer.common.topology.ServiceElement;
import edu.harvard.integer.common.util.DisplayableInterface;
import edu.harvard.integer.service.BaseService;
import edu.harvard.integer.service.discovery.subnet.DiscoverNet;

/**
 * @author David Taylor
 * 
 */
@Singleton
@Startup
public class DiscoveryService extends BaseService implements
		DiscoveryServiceInterface {
	
	@Inject
	private Logger logger;

	@Inject
	private ServiceElementDiscoveryManagerInterface serviceElementDiscoveryManager;
	
    /**
     * Use to limit the number of discovery tasks.  The number should be small since
     * we are not suggest too many tasks for discovery.
     */
	private int discoveryTaskLimit = 5;
	
	private int subTaskLimit = 10;
	
	/**
	 * Use to limit the number of element discovery task.
	 */
	private int elementTaskLimit = 20;
	
	private ExecutorService pool = Executors.newFixedThreadPool(discoveryTaskLimit);
	
	private ExecutorService subPool = Executors.newFixedThreadPool(subTaskLimit);
		
	/**
	 * Discovery sequence id used for network discovery.  This id only valid within an integer server.
	 */
	private long discoverySeqId = 0;
		
	private Map<DiscoveryId, RunningDiscovery> runningDiscoveries = new ConcurrentHashMap<DiscoveryId, RunningDiscovery>();

	/**
	 * Called after service has been created. Initialize of the discovery
	 * service is done here.
	 */
	@PostConstruct
	private void init() {
		try {
			logger.info("Discovery service starting.... on server " 
					+ IntegerProperties.getInstance().getIntProperty(IntegerPropertyNames.ServerId));
		} catch (IntegerException e) {
			
			e.printStackTrace();
			logger.error("Error getting serverID " + e.toString());
		}

	}
	
	public ExecutorService getPool() {
		return pool;
	}

	public ExecutorService getSubPool() {
		return subPool;
	}
	
	
	/**
	 * Start a discovery with the given DiscoveryRule. The returned DiscoveryId can be used as a handle to 
	 * get status of the discovery as well as stop the discovery.
	 * 
	 * @param rule
	 * @return DiscoveryId of the discovery process started by this DiscoveryRule
	 * @throws IntegerException
	 */
	@Override
	public DiscoveryId startDiscovery(DiscoveryRule rule) throws IntegerException {
		DiscoveryId id = new DiscoveryId();
		id.setServerId(IntegerProperties.getInstance().getLongProperty(LongPropertyNames.ServerId));
		id.setDiscoveryId(discoverySeqId++);
				
		switch (rule.getDiscoveryType()) {
		case Both:
		case ServiceElement:
			startServiceElementDiscovery(id, rule.getTopologySeeds());
			break;
			
		case Topology:
			startTopologyDiscovery(rule.getTopologySeeds());
		}
		
		return id;
	}
	
	/**
	 * @param topologySeeds
	 */
	private void startTopologyDiscovery(List<IpTopologySeed> topologySeeds) {
		
	}

	/**
	 * @param topologySeeds
	 * @throws IntegerException 
	 */
	private void startServiceElementDiscovery(DiscoveryId id, List<IpTopologySeed> topologySeeds) throws IntegerException {
		
		for (IpTopologySeed ipTopologySeed : topologySeeds) {
			
			IpDiscoverySeed seed = createIpDiscoverySeed(ipTopologySeed);
		
			try {
				NetworkDiscovery discovery = serviceElementDiscoveryManager.startDiscovery(id, seed);
				RunningDiscovery runningDiscovery = runningDiscoveries.get(id);
				if (runningDiscovery == null) {
					runningDiscovery = new RunningDiscovery();
					runningDiscovery.setId(id);
					List<NetworkDiscovery> discoveries = new  ArrayList<NetworkDiscovery>();
					runningDiscovery.setRunningDiscoveries(discoveries);
		
					logger.info("Add to running queue ServiceElement discovery of " + seed.getSeedId());
					runningDiscoveries.put(id, runningDiscovery);
				}
				
				runningDiscovery.getRunningDiscoveries().add(discovery);
				
				
			} catch (IntegerException e) {
				logger.error("Error starting ServiceElementDiscovery " + e.toString());
				e.printStackTrace();
				throw e;
			}
		}
		
	}

	/**
	 * Create a IpDescoverySeed from the IpToplogySeed.
	 * 
	 * @param ipTopologySeed
	 * @return
	 */
	private IpDiscoverySeed createIpDiscoverySeed(IpTopologySeed ipTopologySeed) {
		
		DiscoverNet net = new DiscoverNet(ipTopologySeed.getSubnet().getAddress().getAddress(),
				ipTopologySeed.getSubnet().getMask().getAddress());
		
		IpDiscoverySeed seed = new IpDiscoverySeed(net, ipTopologySeed.getCredentials());
	
		return seed;
	}

	
	/**
	 * Called when discovery is complete.
	 * @param discoveryId
	 * @throws IntegerException
	 */
	@Override
	public void discoveryComplete(DiscoveryId discoveryId) throws IntegerException {
		RunningDiscovery runningDiscovery = runningDiscoveries.get(discoveryId);
		 
		
		if (runningDiscovery != null)
			logger.info("Discovery complete for " + discoveryId.getDiscoveryId());
		else
			logger.warn("Discovery " + discoveryId.getDiscoveryId() + " not running. Unable to mark as complete!");
	}
	
	/**
	 * Called when an error occurs during discovery. 
	 * @param id
	 * @param errorCode
	 * @param args
	 */
	@Override
	public void discoveryError(DiscoveryId id,  NetworkErrorCodes errorCode, DisplayableInterface[] args) {
		logger.error("Error during discovery " + id + " Error "  + errorCode);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.harvard.integer.service.discovery.DiscoveryServiceInterface#discoveredServiceElement(edu.harvard.integer.common.topology.ServiceElement)
	 */
	@Override
	public void discoveredServiceElement(ServiceElement accessElement) {
		logger.info("Found ServiceElemet " + accessElement);
	}
		
	
	/**
	 * Stop discovery based on id.
	 * 
	 * @param id
	 */
	@Override
	public void stopDiscovery( DiscoveryId id ) {
		
		RunningDiscovery runningDiscovery = runningDiscoveries.get(id);
		if ( runningDiscovery != null ) {
			runningDiscovery.stopDiscovery();	
		}
	}
	
}
