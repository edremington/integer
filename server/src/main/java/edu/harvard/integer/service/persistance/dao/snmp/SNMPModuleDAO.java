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

package edu.harvard.integer.service.persistance.dao.snmp;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import edu.harvard.integer.common.snmp.SNMPModule;
import edu.harvard.integer.service.persistance.dao.BaseDAO;

/**
 * The DAO is responsible for persisting the SNMPModule. All
 * queries will be done in this class. 
 * 
 * @author David Taylor
 *
 * 
 */
public class SNMPModuleDAO extends BaseDAO {
	
	/**
	 * Create SNMPModule with the supplied EntityManager. 
	 * 
	 * @param persistenceManger
	 */
	public SNMPModuleDAO(EntityManager persistenceManger, Logger logger) {
		super(persistenceManger, logger, SNMPModule.class);
		
	}

	/**
	 * Find the SNMPModule that has the given OID.
	 * @return
	 */
	public SNMPModule findByOid(String oidString) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		
		CriteriaQuery<SNMPModule> query = criteriaBuilder.createQuery(SNMPModule.class);

		Root<SNMPModule> from = query.from(SNMPModule.class);
		query.select(from);
		
		ParameterExpression<String> oid = criteriaBuilder.parameter(String.class);
		query.select(from).where(criteriaBuilder.equal(from.get("oid"), oid));
		
		TypedQuery<SNMPModule> typeQuery = getEntityManager().createQuery(query);
		typeQuery.setParameter(oid, oidString);
		
		List<SNMPModule> resultList = typeQuery.getResultList();
		
		if (resultList.size() > 0)
			return resultList.get(0);
		else
			return null;
		
	}
}
