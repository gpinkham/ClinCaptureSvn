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

package org.akaza.openclinica.dao.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@SuppressWarnings({"unchecked"})
public class EhCacheWrapper<K, V> implements CacheWrapper<K, V> {
	private final String cacheName;
	private final CacheManager cacheManager;
	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public EhCacheWrapper(final String cacheName, final CacheManager cacheManager) {
		this.cacheName = cacheName;
		this.cacheManager = cacheManager;
	}

	public void put(final K key, final V value) {
		getCache().put(new Element(key, value));
	}

	public V get(final K key) {
		Element element = null;
		Ehcache ehCache = getCache();
		if (ehCache != null) {
			element = getCache().get(key);
			logMe("element  null" + element);
		}
		if (element != null) {
			logMe("element not null" + element);
			return (V) element.getObjectValue();
		}
		return null;
	}

	public Ehcache getCache() {
		return cacheManager.getEhcache(cacheName);
	}

	private void logMe(String message) {

		logger.info(message);
		// System.out.println(message);
	}
}
