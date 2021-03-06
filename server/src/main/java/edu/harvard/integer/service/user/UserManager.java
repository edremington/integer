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

package edu.harvard.integer.service.user;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;

import edu.harvard.integer.common.ID;
import edu.harvard.integer.common.exception.IntegerException;
import edu.harvard.integer.common.user.AccessPolicy;
import edu.harvard.integer.common.user.Contact;
import edu.harvard.integer.common.user.Location;
import edu.harvard.integer.common.user.Organization;
import edu.harvard.integer.common.user.Role;
import edu.harvard.integer.common.user.User;
import edu.harvard.integer.common.user.authentication.AuthInfo;
import edu.harvard.integer.service.BaseManager;
import edu.harvard.integer.service.distribution.ManagerTypeEnum;
import edu.harvard.integer.service.persistance.PersistenceManagerInterface;
import edu.harvard.integer.service.persistance.dao.user.AccessPolicyDAO;
import edu.harvard.integer.service.persistance.dao.user.ContactDAO;
import edu.harvard.integer.service.persistance.dao.user.LocationDAO;
import edu.harvard.integer.service.persistance.dao.user.OrganizationDAO;
import edu.harvard.integer.service.persistance.dao.user.RoleDAO;
import edu.harvard.integer.service.persistance.dao.user.UserDAO;

/**
 * @see UserManagerInterface
 *  
 * @author David Taylor
 * 
 */
@Stateless
public class UserManager extends BaseManager implements UserManagerLocalInterface, UserManagerRemoteInterface {
	

	@Inject
	private Logger logger;

	@Inject
	private PersistenceManagerInterface dbm;
	
	/**
	 * @param managerType
	 */
	public UserManager() {
		super(ManagerTypeEnum.UserManager);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.harvard.integer.service.user.UserManagerInterface#addUser(edu.harvard.integer.common.user.User)
	 */
	@Override
	public User addUser(User user) throws IntegerException {
		logger.info("Add User (" + user.getAlias() + ") " + user.getFirstName()
				+ " " + user.getLastName());
		UserDAO userDAO = dbm.getUserDAO();
		userDAO.update(user);
		return user;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.harvard.integer.service.user.UserManagerInterface#modifyUser(edu.harvard.integer.common.user.User)
	 */
	@Override
	public User modifyUser(User user) throws IntegerException {
		logger.info("Modify User (" + user.getAlias() + ") "
				+ user.getFirstName() + " " + user.getLastName());

		UserDAO userDAO = dbm.getUserDAO();
		userDAO.update(user);

		return user;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.harvard.integer.service.user.UserManagerInterface#deleteUser(edu.harvard.integer.common.user.User)
	 */
	@Override
	public void deleteUser(User user) throws IntegerException {
		logger.info("Delete User (" + user.getAlias() + ") "
				+ user.getFirstName() + " " + user.getLastName());
		UserDAO userDAO = dbm.getUserDAO();
		userDAO.delete(user);
	}

	/*
	 * 	(non-Javadoc)
	 * @see edu.harvard.integer.service.user.UserManagerInterface#getAllUsers()
	 */
	@Override
	public User[] getAllUsers() throws IntegerException {

		UserDAO userDAO = dbm.getUserDAO();

		return userDAO.findAll();
	}

	/**
	 * Add a Contact to the system.
	 * 
	 * @param contact
	 * @return Contact. The newly created user.
	 * @throws IntegerException
	 */
	@Override
	public Contact updateContact(Contact contact) throws IntegerException {
		ContactDAO dao = dbm.getContactDAO();
		dao.update(contact);

		return contact;
	}

	/**
	 * Delete the Contact. The contact must not be associated with any user,
	 * service element or organization for the delete to succeed. If the contact
	 * is being used an IntegerException will be thrown with a error code of
	 * ContactInUseCanNotDelete.
	 * 
	 * 
	 * @param contact
	 * @throws IntegerException. If
	 *             the Contact is in Use with an error code of
	 *             ContactInUseCanNotDelete.
	 */
	@Override
	public void deleteContact(Contact contact) throws IntegerException {
		ContactDAO dao = dbm.getContactDAO();

		dao.delete(contact);
	}

	/**
	 * Show the users VIA rest interface. This will list all users in the
	 * system.
	 */
	@GET
	@Path("/Users")
	@Produces(value = MediaType.TEXT_HTML)
	@Override
	public String showUsers() throws IntegerException {

		UserDAO userDAO = dbm.getUserDAO();
		User[] resultList = userDAO.findAll();

		StringBuffer b = new StringBuffer();
		b.append("<ul>");
		for (User obj : resultList) {
			if (obj != null)
				b.append("<li>User " + obj.getIdentifier() + " "
						+ obj.getFirstName() + " " + obj.getLastName()
						+ " UUID: " + obj.getUuid() + "</li>");
			else
				b.append("<li>User is NULL!!</li>");

		}
		b.append("</ul>");
		return b.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see edu.harvard.integer.service.user.UserManagerInterface#getAllOrganizations()
	 */
	@Override
	public Organization[] getAllOrganizations() throws IntegerException {
		OrganizationDAO dao = dbm.getOrganizationDAO();
		
		return dao.findAll();
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.harvard.integer.service.user.UserManagerInterface#updateOrganization(edu.harvard.integer.common.user.Organization)
	 */
	@Override
	public Organization updateOrganization(Organization organization)
			throws IntegerException {
		OrganizationDAO dao = dbm.getOrganizationDAO();

		try {
			organization = dao.update(organization);
		} catch (IntegerException e) {
			e.printStackTrace();
		}

		return organization;
	}

	/**
	 * Delete the Organization the ID identifies
	 * 
	 * @param id
	 * @throws IntegerException
	 */
	@Override
	public void deleteOrganization(ID id) throws IntegerException {
		OrganizationDAO dao = dbm.getOrganizationDAO();

		dao.delete(id);
	}

	/**
	 * Delete the Organization
	 * 
	 * @param organization
	 * @throws IntegerException
	 */
	@Override
	public void deleteOrganization(Organization organization)
			throws IntegerException {
		OrganizationDAO dao = dbm.getOrganizationDAO();

		dao.delete(organization);
	}

	/**
	 * Update the AccessPolicy.
	 * 
	 * @param policy
	 * @return
	 * @throws IntegerException
	 */
	@Override
	public AccessPolicy updateAccessPolicy(AccessPolicy policy)
			throws IntegerException {
		AccessPolicyDAO dao = dbm.getAccessPolicyDAO();

		policy = dao.update(policy);

		return policy;
	}

	/**
	 * Delete the AccessPolicy the ID specifies.
	 * 
	 * @param id
	 * @throws IntegerException
	 */
	@Override
	public void deleteAccessPolicy(ID id) throws IntegerException {
		AccessPolicyDAO dao = dbm.getAccessPolicyDAO();
		dao.delete(id);
	}

	/**
	 * Delete the AccessPolicy.
	 * 
	 * @param policy
	 * @throws IntegerException
	 */
	@Override
	public void deleteAccessPolicy(AccessPolicy policy) throws IntegerException {
		AccessPolicyDAO dao = dbm.getAccessPolicyDAO();
		dao.delete(policy);
	}

	/**
	 * Update the AuthInfo.
	 * 
	 * @param authInfo
	 * @return
	 * @throws IntegerException
	 */
	@Override
	public AuthInfo updateAuthInfo(AuthInfo authInfo) throws IntegerException {
		AccessPolicyDAO dao = dbm.getAccessPolicyDAO();

		return dao.update(authInfo);
	}

	/**
	 * Delete the AuthInfo
	 * 
	 * @param id
	 * @throws IntegerException
	 */
	public void deleteAuthInfo(ID id) throws IntegerException {
		AccessPolicyDAO dao = dbm.getAccessPolicyDAO();
		dao.delete(id);
	}

	/**
	 * Update the location.
	 * 
	 * @param location
	 * @return
	 * @throws IntegerException
	 */
	@Override
	public Location updateLocation(Location location) throws IntegerException {
		LocationDAO dao = dbm.getLocationDAO();
		location = dao.update(location);

		return location;
	}

	/**
	 * Delete the Location.
	 * 
	 * @param id
	 * @throws IntegerException
	 */
	@Override
	public void deleteLocation(ID id) throws IntegerException {
		LocationDAO dao = dbm.getLocationDAO();
		dao.delete(id);
	}

	/**
	 * Update the Role.
	 * 
	 * @param role
	 * @return
	 * @throws IntegerException
	 */
	@Override
	public Role updateRole(Role role) throws IntegerException {

		RoleDAO dao = dbm.getRoleDAO();
		role = dao.update(role);

		return role;
	}

	/**
	 * Delete the Role specified by the ID.
	 * 
	 * @param id
	 * @throws IntegerException
	 */
	public void deleteRole(ID id) throws IntegerException {
		RoleDAO dao = dbm.getRoleDAO();
		dao.delete(id);
	}

}
