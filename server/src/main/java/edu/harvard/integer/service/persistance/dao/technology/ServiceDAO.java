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

package edu.harvard.integer.service.persistance.dao.technology;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;

import edu.harvard.integer.common.exception.IntegerException;
import edu.harvard.integer.common.technology.Service;
import edu.harvard.integer.service.persistance.dao.BaseDAO;

/**
 *  The DAO is responsible for persisting the Service. All
 * queries will be done in this class. 
 *
 * @author David Taylor
 *
 */
public class ServiceDAO extends BaseDAO {

	/**
	 * @param entityManger
	 * @param logger
	 * @param clazz
	 */
	public ServiceDAO(EntityManager entityManger, Logger logger) {
		super(entityManger, logger, Service.class);
		
	}

	/**
	 * @param name
	 * @return
	 */
	public Service findByName(String name) {
		
		return findByStringField(name, "name", Service.class);
	}

	public Service[] getTopLevelServices() throws IntegerException {
		StringBuffer b = new StringBuffer();

		String tableName = getTableName();
		
		b.append("select bs.* ").append('\n');
		b.append("from ").append(tableName).append(" bs ").append('\n');
		b.append(
				" where not exists (select * from  ").append(tableName).append("_childServices sep ")
				.append('\n');
		b.append("where sep.identifier = bs.identifier)");

		Query query = getEntityManager().createNativeQuery(b.toString(),
				getPersistentClass());

		@SuppressWarnings("unchecked")
		List<Service> resultList = query.getResultList();

		return (Service[]) resultList
				.toArray(new Service[resultList.size()]);

	}
}
