/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 *
 * Created on Feb 23, 2005
 */
package org.akaza.openclinica.bean.extract;

import org.akaza.openclinica.bean.core.EntityBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class which facilitates holding many unique child beans.
 * 
 * @author ssachs
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class OrderedEntityBeansSet {

	/**
	 * Key is entity's id, value is Integer whose value is index into entities. If key is present, entity was added to
	 * entities; otherwise it was not.
	 */
	private HashMap entityIsAdded = new HashMap();

	/**
	 * Array of EntityBeans, ordered as they should be retrieved.
	 */
	private ArrayList entities = new ArrayList();

	/**
	 * Returned by add in case of error.
	 */
	private EntityBean defaultEntity;

	public OrderedEntityBeansSet(EntityBean defaultEntity) {
		this.defaultEntity = defaultEntity;
	}

	/**
	 * Adds the entity to the set of entities, if it has not yet been added.
	 * 
	 * @param entity
	 *            The entity to add.
	 * @return The entity which was already in the set, if it had previously been added, or the entity which was passed
	 *         as an argument, if it had not been added.
	 */
	public EntityBean add(EntityBean entity) {
		Integer key = new Integer(entity.getId());

		if (entityIsAdded.containsKey(key)) {
			Integer ind = (Integer) entityIsAdded.get(key);

			if (ind == null) {
				return defaultEntity;
			}

			int i = ind.intValue();
			if (i < 0 || i >= entities.size()) {
				return defaultEntity;
			}

			return (EntityBean) entities.get(ind.intValue());
		} else {
			entities.add(entity);
			Integer ind = new Integer(entities.size() - 1);
			entityIsAdded.put(key, ind);
			return entity;
		}
	}

	/**
	 * Write the Entity on top of any entity that may already be in the set that has the same id. If no such entity
	 * exists, take no action.
	 * 
	 * @param entity
	 *            The entity to update.
	 */
	public void update(EntityBean entity) {
		Integer key = new Integer(entity.getId());

		if (entityIsAdded.containsKey(key)) {
			Integer ind = (Integer) entityIsAdded.get(key);

			if (ind == null) {
				return;
			}

			int i = ind.intValue();
			if (i < 0 || i >= entities.size()) {
				return;
			}

			entities.set(ind.intValue(), entity);
		}
	}

	/**
	 * @return Returns the entities.
	 */
	public ArrayList getEntities() {
		return entities;
	}

	public boolean contains(EntityBean eb) {
		Integer key = new Integer(eb.getId());
		return entityIsAdded.containsKey(key);
	}
}
