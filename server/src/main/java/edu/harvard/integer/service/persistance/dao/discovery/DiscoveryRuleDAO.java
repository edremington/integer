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

package edu.harvard.integer.service.persistance.dao.discovery;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;

import edu.harvard.integer.common.BaseEntity;
import edu.harvard.integer.common.exception.IntegerException;
import edu.harvard.integer.common.schedule.CalendarPolicy;
import edu.harvard.integer.common.topology.DiscoveryRule;
import edu.harvard.integer.common.topology.IpTopologySeed;
import edu.harvard.integer.service.persistance.dao.BaseDAO;

/**
 * @author David Taylor
 *
 */
public class DiscoveryRuleDAO extends BaseDAO {

	/**
	 * @param entityManger
	 * @param logger
	 * @param clazz
	 */
	public DiscoveryRuleDAO(EntityManager entityManger, Logger logger) {
		super(entityManger, logger, DiscoveryRule.class);

	}

	/* (non-Javadoc)
	 * @see edu.harvard.integer.service.persistance.dao.BaseDAO#preSave(edu.harvard.integer.common.BaseEntity)
	 */
	@Override
	public <T extends BaseEntity> void preSave(T entity)
			throws IntegerException {
		
		DiscoveryRule rule = (DiscoveryRule) entity;
		
		if (rule.getCalendars() != null) {
			CalendarPolicyDAO policyDAO = new CalendarPolicyDAO(getEntityManager(), getLogger());
			List<CalendarPolicy> dbCalendars = new ArrayList<CalendarPolicy>();
			for (CalendarPolicy policy : rule.getCalendars()) {
				dbCalendars.add(policyDAO.update(policy));
			}
			rule.setCalendars(dbCalendars);
		}
		
		if (rule.getTopologySeeds() != null) {
			IpTopologySeedDAO dao = new IpTopologySeedDAO(getEntityManager(), getLogger());
			List<IpTopologySeed> seeds = new ArrayList<IpTopologySeed>();
			for (IpTopologySeed policy : rule.getTopologySeeds()) {
				seeds.add(dao.update(policy));
			}
			rule.setTopologySeeds(seeds);
		}
	
		
		super.preSave(entity);
	}

	/**
	 * Find the discovery rule with the given name. If more than one rule 
	 * has the same name then the first one will be returned.
	 * @param name
	 * @return DiscoveryRule with the given name
	 */
	public DiscoveryRule findByName(String name) {

		return findByStringField(name, "name", DiscoveryRule.class);
	}

}
