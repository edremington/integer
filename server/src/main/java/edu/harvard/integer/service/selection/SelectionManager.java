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

package edu.harvard.integer.service.selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import edu.harvard.integer.common.BaseEntity;
import edu.harvard.integer.common.ID;
import edu.harvard.integer.common.exception.IntegerException;
import edu.harvard.integer.common.selection.Filter;
import edu.harvard.integer.common.selection.FilterNode;
import edu.harvard.integer.common.selection.Layer;
import edu.harvard.integer.common.selection.Selection;
import edu.harvard.integer.common.technology.Service;
import edu.harvard.integer.common.technology.Technology;
import edu.harvard.integer.common.topology.Category;
import edu.harvard.integer.common.topology.CriticalityEnum;
import edu.harvard.integer.common.topology.EnvironmentLevel;
import edu.harvard.integer.common.topology.LayerTypeEnum;
import edu.harvard.integer.common.user.Location;
import edu.harvard.integer.common.user.Organization;
import edu.harvard.integer.service.BaseManager;
import edu.harvard.integer.service.distribution.DistributionManager;
import edu.harvard.integer.service.distribution.ManagerTypeEnum;
import edu.harvard.integer.service.persistance.PersistenceManagerInterface;
import edu.harvard.integer.service.persistance.dao.selection.FilterDAO;
import edu.harvard.integer.service.persistance.dao.selection.LayerDAO;
import edu.harvard.integer.service.persistance.dao.selection.SelectionDAO;
import edu.harvard.integer.service.persistance.dao.technology.ServiceDAO;
import edu.harvard.integer.service.persistance.dao.technology.TechnologyDAO;
import edu.harvard.integer.service.persistance.dao.topology.CategoryDAO;
import edu.harvard.integer.service.persistance.dao.user.OrganizationDAO;
import edu.harvard.integer.service.topology.device.ServiceElementAccessManagerInterface;
import edu.harvard.integer.service.topology.device.ServiceElementAccessManagerLocalInterface;

/**
 * 
 * @see SelectionManagerInterface
 * 
 * @author David Taylor
 * 
 */
@Stateless
public class SelectionManager extends BaseManager implements
		SelectionManagerLocalInterface, SelectionManagerRemoteInterface {

	@Inject
	private PersistenceManagerInterface persistenceManager;

	@Inject
	private Logger logger;

	public SelectionManager() {
		super(ManagerTypeEnum.SelectionManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.harvard.integer.service.selection.SelectionManagerInterface#
	 * updateSelection(edu.harvard.integer.common.selection.Selection)
	 */
	@Override
	public Selection updateSelection(Selection selection)
			throws IntegerException {
		SelectionDAO selectionDAO = persistenceManager.getSelectionDAO();

		return selectionDAO.update(selection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.harvard.integer.service.selection.SelectionManagerInterface#
	 * getBlankSelection()
	 */
	@Override
	public Selection getBlankSelection() throws IntegerException {
		TechnologyDAO dao = persistenceManager.getTechnologyDAO();
		Technology[] technologies = dao.findTopLevel();

		List<FilterNode> nodes = new ArrayList<FilterNode>();
		List<FilterNode> routing = new ArrayList<FilterNode>();

		if (technologies != null && technologies.length > 0) {
			Technology[] children = dao.findByParentId(technologies[0].getID());

			for (Technology technology : children) {
				FilterNode node = new FilterNode();
				node.setItemId(technology.getID());
				node.setName(technology.getName());
				node.setChildren(findChildren(dao, technology.getID()));

				if (LayerTypeEnum.Unknown.equals(technology.getLayer()))
					nodes.add(node);
				else
					routing.add(node);
			}

		}

		Filter filter = new Filter();
		filter.setCreated(new Date());

		ServiceDAO serviceDao = persistenceManager.getServiceDAO();

		Service[] topLevelServices = serviceDao.getTopLevelServices();
		
		List<FilterNode> children = new ArrayList<FilterNode>();
		
		for (Service service : topLevelServices) {
			FilterNode node = new FilterNode();
			
			node.setIdentifier(service.getIdentifier());
			node.setName(service.getName());
			node.setItemId(service.getID());
			node.setChildren(createChildService(service.getChildServices(), serviceDao));
			
			children.add(node);
		}
		
		
		filter.setServices( children );
		if (logger.isDebugEnabled())
			logger.debug("Buisness Services: "
					+ printFilterNode(new StringBuffer(), "  ",
							filter.getServices()));
		
		filter.setTechnologies(nodes);
		if (logger.isDebugEnabled())
			logger.debug("Technologies: "
					+ printFilterNode(new StringBuffer(), "  ",
							filter.getTechnologies()));

		CategoryDAO categoryDAO = persistenceManager.getCategoryDAO();
		Category[] categories = categoryDAO.findAllTopLevel();

		filter.setCategories(createCategoryList(categories));
		filter.setCriticalities(Arrays.asList(CriticalityEnum.values()));
		filter.setLinkTechnologies(routing);

		filter.setLocations(createLocationList((Location[]) persistenceManager
				.getLocationDAO().getAllByStateCityZip()));

		OrganizationDAO organizationDAO = persistenceManager
				.getOrganizationDAO();
		Organization[] allOrganizations = organizationDAO
				.getTopLevelOrganizations();
		filter.setOrginizations(createOrganizationList(organizationDAO,
				allOrganizations));
		
		if (logger.isDebugEnabled())
			logger.debug("Added "
					+ filter.getOrginizations().size()
					+ " Organizations \n"
					+ printFilterNode(new StringBuffer(), "  ",
							filter.getOrginizations()));

		filter.setProviders(createProviderList(allOrganizations, new ArrayList<ID>(), organizationDAO));
		
		ServiceElementAccessManagerInterface serviceElementManaeger = DistributionManager.getManager(ManagerTypeEnum.ServiceElementAccessManager);
		EnvironmentLevel[] allEnvironmentLevels = serviceElementManaeger.getAllEnvironmentLevels();
		
		filter.setEnvironmentLevel(createIDList(allEnvironmentLevels));
		
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(filter);

		Selection selection = new Selection();
		selection.setFilters(filters);

		return selection;
	}
	
	/**
	 * @param childServices
	 * @return
	 * @throws IntegerException 
	 */
	private List<FilterNode> createChildService(List<ID> childServices, ServiceDAO dao) throws IntegerException {
		List<FilterNode> children = new ArrayList<FilterNode>();
		
		for (ID id : childServices) {
			Service service = dao.findById(id);
		
			FilterNode node = new FilterNode();

			node.setIdentifier(service.getIdentifier());
			node.setName(service.getName());
			node.setItemId(service.getID());
			node.setChildren(createChildService(service.getChildServices(), dao));
			
			children.add(node);
		}
		
		return children;
	}

	private List<ID> createIDList(BaseEntity[] entities) {
		List<ID> ids = new ArrayList<ID>();
		
		if (entities == null)
			return ids;
		
		for (BaseEntity baseEntity : entities) {
			
			ids.add(baseEntity.getID());
		}
		
		return ids;
	}

	private List<FilterNode> createBusinessServiceNodes(List<ID> ids) {
		List<FilterNode> nodes = new ArrayList<FilterNode>();
		
		for (ID id : ids) {
			FilterNode node = new FilterNode();
			node.setIdentifier(id.getIdentifier());
			node.setName(id.getName());
			node.setItemId(id);
			
			nodes.add(node);
		}
		
		return nodes;
	}
	
	/**
	 * @param allOrganizations
	 * @return
	 * @throws IntegerException 
	 */
	private List<ID> createProviderList(Organization[] allOrganizations, List<ID> providerIds, OrganizationDAO dao) throws IntegerException {
		
		for (Organization organization : allOrganizations) {
			
			if (logger.isDebugEnabled())
				logger.debug("Organization " + organization.getName()
						+ " has " + organization.getBusinessServices().size()
						+ " Bus and " + organization.getTechnologies().size()
						+ " techs");
			
			if (organization.getBusinessServices() != null && organization.getBusinessServices().size() > 0)
				providerIds.add(organization.getID());
			
			if (organization.getTechnologies() != null && organization.getTechnologies().size() > 0)
				providerIds.add(organization.getID());
			
			if (organization.getChildOrginizations() != null)
				createProviderList(organization.getChildOrginizations(),  providerIds, dao); 
		}
		
		return providerIds;
	}

	/**
	 * @param childOrginizations
	 * @param providerIds
	 * @throws IntegerException 
	 */
	private void createProviderList(List<ID> childOrginizations,
			List<ID> providerIds, OrganizationDAO dao) throws IntegerException {
		
		for (ID id : childOrginizations) {
			Organization organization = dao.findById(id);
			if (organization.getBusinessServices() != null && organization.getBusinessServices().size() > 0)
				providerIds.add(organization.getID());
			
			if (organization.getTechnologies() != null && organization.getTechnologies().size() > 0)
				providerIds.add(organization.getID());
			
			if (organization.getChildOrginizations() != null)
				createProviderList(organization.getChildOrginizations(),  providerIds, dao); 
		}
		
	}

	
	static StringBuffer printFilterNode(StringBuffer b, String indent,
			List<FilterNode> nodes) {

		if (nodes != null) {
			for (FilterNode filterNode : nodes) {
				b.append(indent).append("Node: ").append(filterNode.getName())
						.append('\n');
				if (filterNode.getChildren() != null
						&& filterNode.getChildren().size() > 0)
					printFilterNode(b, indent + "  ", filterNode.getChildren());
			}
		}

		return b;
	}

	private List<FilterNode> createOrganizationList(
			OrganizationDAO organizationDAO, Organization[] organizations)
			throws IntegerException {
		List<FilterNode> nodes = new ArrayList<FilterNode>();

		if (organizations != null && organizations.length > 0) {
			for (Organization organization : organizations) {
				FilterNode node = new FilterNode();
				node.setIdentifier(organization.getIdentifier());
				node.setName(organization.getName());
				node.setItemId(organization.getID());

				
				node.setChildren(createOrganizationList(organizationDAO,
						organization.getChildOrginizations()));

				nodes.add(node);
			}
		}

		return nodes;
	}

	/**
	 * @param childOrginizations
	 * @return
	 * @throws IntegerException
	 */
	private List<FilterNode> createOrganizationList(
			OrganizationDAO organizationDAO, List<ID> childOrginizations)
			throws IntegerException {
		List<FilterNode> nodes = new ArrayList<FilterNode>();

		if (childOrginizations != null && childOrginizations.size() > 0) {
			for (ID organization : childOrginizations) {
				FilterNode node = new FilterNode();
				node.setName(organization.getName());
				node.setItemId(organization);

				Organization dbOrganization = organizationDAO
						.findById(organization);

			
				node.setChildren(createOrganizationList(organizationDAO,
						dbOrganization.getChildOrginizations()));

				nodes.add(node);
			}
		}

		return nodes;
	}

	/**
	 * @param findAll
	 * @return
	 */
	private List<FilterNode> createLocationList(Location[] locations) {
		List<FilterNode> locationTree = createLocationTree(0, locations, 0, new String[] { "State", "City", "PostalCode"} );
		
		if (logger.isDebugEnabled()) {
			StringBuffer b = printFilterNode(new StringBuffer(), "", locationTree);
		
			logger.debug("Location Tree " + b.toString());
		}
		
		return locationTree;
	}

	private List<FilterNode> createLocationTree(int index, Location[] locations, int level, String[] levelNames) {
		LocationTreeBuilder builder = new LocationTreeBuilder(locations, levelNames, logger);
		return builder.createLocationTree();
	}
	
	private List<FilterNode> createCategoryList(Category[] categories)
			throws IntegerException {
		List<FilterNode> categoryNodes = new ArrayList<FilterNode>();

		CategoryDAO dao = persistenceManager.getCategoryDAO();

		for (Category category : categories) {
			FilterNode node = new FilterNode();
			node.setIdentifier(category.getIdentifier());
			node.setItemId(category.getID());
			node.setName(category.getName());

			node.setChildren(createChildCategoryList(dao,
					category.getChildIds()));

			categoryNodes.add(node);
		}

		return categoryNodes;
	}

	/**
	 * @param childIds
	 * @return
	 * @throws IntegerException
	 */
	private List<FilterNode> createChildCategoryList(CategoryDAO dao,
			List<ID> childIds) throws IntegerException {
		List<FilterNode> categoryNodes = new ArrayList<FilterNode>();

		for (ID id : childIds) {
			FilterNode node = new FilterNode();
			node.setIdentifier(id.getIdentifier());
			node.setItemId(id);
			node.setName(id.getName());

			if (logger.isDebugEnabled())
				logger.debug("Find category " + id.toDebugString());

			Category category = dao.findById(id);

			if (category.getChildIds() != null)
				node.setChildren(createChildCategoryList(dao,
						category.getChildIds()));

			categoryNodes.add(node);
		}

		return categoryNodes;
	}

	private List<FilterNode> findChildren(TechnologyDAO dao, ID parentId)
			throws IntegerException {
		Technology[] children = dao.findByParentId(parentId);

		List<FilterNode> nodes = new ArrayList<FilterNode>();
		for (Technology technology : children) {
			FilterNode node = new FilterNode();
			node.setItemId(technology.getID());
			node.setName(technology.getName());
			node.setChildren(findChildren(dao, technology.getID()));

			nodes.add(node);
		}

		return nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.harvard.integer.service.selection.SelectionManagerInterface#
	 * getAllSeletions()
	 */
	@Override
	public Selection[] getAllSeletions() throws IntegerException {
		SelectionDAO selectionDAO = persistenceManager.getSelectionDAO();

		return selectionDAO.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.harvard.integer.service.selection.SelectionManagerInterface#
	 * getSelectionById(edu.harvard.integer.common.ID)
	 */
	@Override
	public Selection getSelectionById(ID selectionId) throws IntegerException {
		SelectionDAO selectionDAO = persistenceManager.getSelectionDAO();

		return selectionDAO.findById(selectionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.harvard.integer.service.selection.SelectionManagerInterface#updateFilter
	 * (edu.harvard.integer.common.selection.Filter)
	 */
	@Override
	public Filter updateFilter(Filter filter) throws IntegerException {
		FilterDAO dao = persistenceManager.getFilterDAO();

		return dao.update(filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.harvard.integer.service.selection.SelectionManagerInterface#getAllFilters
	 * ()
	 */
	@Override
	public Filter[] getAllFilters() throws IntegerException {
		FilterDAO dao = persistenceManager.getFilterDAO();

		return dao.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.harvard.integer.service.selection.SelectionManagerInterface#getFilterById
	 * (edu.harvard.integer.common.ID)
	 */
	@Override
	public Filter getFilterById(ID filterId) throws IntegerException {
		FilterDAO dao = persistenceManager.getFilterDAO();

		return dao.findById(filterId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.harvard.integer.service.selection.SelectionManagerInterface#updateLayer
	 * (edu.harvard.integer.common.selection.Layer)
	 */
	@Override
	public Layer updateLayer(Layer layer) throws IntegerException {
		LayerDAO dao = persistenceManager.getLayerDAO();

		return dao.update(layer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.harvard.integer.service.selection.SelectionManagerInterface#getAllLayers
	 * ()
	 */
	@Override
	public Layer[] getAllLayers() throws IntegerException {
		LayerDAO dao = persistenceManager.getLayerDAO();
		return dao.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.harvard.integer.service.selection.SelectionManagerInterface#getLayerById
	 * (edu.harvard.integer.common.ID)
	 */
	@Override
	public Layer getLayerById(ID layerId) throws IntegerException {
		LayerDAO dao = persistenceManager.getLayerDAO();

		return dao.findById(layerId);
	}
}
