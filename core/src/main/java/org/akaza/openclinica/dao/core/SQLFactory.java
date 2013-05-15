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
 */
package org.akaza.openclinica.dao.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

import org.akaza.openclinica.dao.cache.EhCacheWrapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.SAXException;

/**
 * Provides a singleton SQLFactory instance
 * 
 * @author thickerson
 * @author Jun Xu
 * 
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class SQLFactory {

	// DAO KEYS TO USE FOR RETRIEVING DIGESTER
	public final String DAO_USERACCOUNT = "useraccount";
	public final String DAO_STUDY = "study";
	public final String DAO_STUDYEVENTDEFNITION = "studyeventdefintion";
	public final String DAO_SUBJECT = "subject";
	public final String DAO_STUDYSUBJECT = "study_subject";
	public final String DAO_STUDYGROUP = "study_group";
	public final String DAO_STUDYGROUPCLASS = "study_group_class";
	public final String DAO_SUBJECTGROUPMAP = "subject_group_map";
	public final String DAO_STUDYEVENT = "study_event";
	public final String DAO_EVENTDEFINITIONCRF = "event_definition_crf";
	public final String DAO_AUDITEVENT = "audit_event";
	public final String DAO_AUDIT = "audit";
	public final String DAO_DATAVIEW = "dataview_dao";
	public final String DAO_DYNAMIC_EVENT = "dynamic_event";
	public final String DAO_ITEM = "item";
	public final String DAO_ITEMDATA = "item_data";
	public final String DAO_ITEMFORMMETADATA = "item_form_metadata";
	public final String DAO_CRF = "crf";
	public final String DAO_CRFVERSION = "crfversion";
	public final String DAO_DATASET = "dataset";
	public final String DAO_SECTION = "section";
	
	public final String DAO_EVENTCRF = "eventcrf";
	public final String DAO_ARCHIVED_DATASET_FILE = "archived_dataset_file";
	public final String DAO_DISCREPANCY_NOTE = "discrepancy_note";
	public final String DAO_STUDY_PARAMETER = "study_parameter";
	public final String DAO_ITEM_GROUP = "item_group";
	public final String DAO_ITEM_GROUP_METADATA = "item_group_metadata";
	public final String DAO_RULESET = "ruleset";
	public final String DAO_RULE = "rule";
	public final String DAO_RULE_ACTION = "action";
	public final String DAO_EXPRESSION = "expression";
	public final String DAO_RULESET_RULE = "rulesetrule";
	public final String DAO_RULESET_AUDIT = "rulesetaudit";
	public final String DAO_RULESETRULE_AUDIT = "rulesetruleaudit";
	public final String DAO_SUBJECTTRANSFER = "subjecttransfer";
	public final String DAO_ODM_EXTRACT = "odm_extract";

	public EhCacheWrapper ehCacheWrapper;

	public EhCacheWrapper getEhCacheWrapper() {
		return ehCacheWrapper;
	}

	public void setEhCacheWrapper(EhCacheWrapper ehCacheWrapper) {
		this.ehCacheWrapper = ehCacheWrapper;
	}

	public static String JUNIT_XML_DIR = System.getProperty("user.dir")
			+ (System.getProperty("user.dir").indexOf(File.separator + "core") < 0 ? (File.separator + "core") : "")
			+ File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator
			+ "properties" + File.separator;

	public static void setXMLDir(String path) {
		JUNIT_XML_DIR = path;
	}

	private static Hashtable digesters = new Hashtable();

	/**
	 * A handle to the unique SQLFactory instance.
	 */
	static private SQLFactory facInstance = null;

	/**
	 * @return The unique instance of this class. <b>WARNING this directory will need to be changed to run unit tests on
	 *         other systems!!!</b>
	 */
	static public SQLFactory getInstance() {
		// set so that we could test an xml file in a unit test, tbh
		if (facInstance == null) {

			facInstance = new SQLFactory();
		}
		return facInstance;
	}

	// name should be one of the public static final Strings above
	public void addDigester(String name, DAODigester dig) {
		digesters.put(name, dig);
	}

	// name should be one of the public static final Strings above
	public DAODigester getDigester(String name) {
		return (DAODigester) digesters.get(name);
	}

	public void run(String dbName, ResourceLoader resourceLoader) {
		HashMap fileList = new HashMap();
		CacheManager cacheManager = new CacheManager();

		try {
			Resource resource = resourceLoader.getResource("classpath:org/akaza/openclinica/ehcache.xml");
			cacheManager = CacheManager.create(resource.getInputStream());
		} catch (CacheException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EhCacheWrapper ehCache = new EhCacheWrapper("com.akaza.openclinica.dao.core.DAOCache", cacheManager);

		setEhCacheWrapper(ehCache);

		if ("oracle".equals(dbName)) {
			fileList.put(this.DAO_USERACCOUNT, "oracle_useraccount_dao.xml");
			fileList.put(this.DAO_ARCHIVED_DATASET_FILE, "oracle_archived_dataset_file_dao.xml");
			fileList.put(this.DAO_STUDY, "oracle_study_dao.xml");
			fileList.put(this.DAO_STUDYEVENTDEFNITION, "oracle_studyeventdefinition_dao.xml");
			fileList.put(this.DAO_STUDYEVENT, "oracle_study_event_dao.xml");
			fileList.put(this.DAO_STUDYGROUP, "oracle_study_group_dao.xml");
			fileList.put(this.DAO_STUDYGROUPCLASS, "oracle_study_group_class_dao.xml");
			fileList.put(this.DAO_STUDYSUBJECT, "oracle_study_subject_dao.xml");
			fileList.put(this.DAO_SUBJECT, "oracle_subject_dao.xml");
			fileList.put(this.DAO_SUBJECTGROUPMAP, "oracle_subject_group_map_dao.xml");
			fileList.put(this.DAO_EVENTDEFINITIONCRF, "oracle_event_definition_crf_dao.xml");
			fileList.put(this.DAO_AUDITEVENT, "oracle_audit_event_dao.xml");
			fileList.put(this.DAO_AUDIT, "oracle_audit_dao.xml");
            fileList.put(this.DAO_DYNAMIC_EVENT, "oracle_dynamic_event_dao.xml");
			fileList.put(this.DAO_ITEM, "oracle_item_dao.xml");
			fileList.put(this.DAO_ITEMDATA, "oracle_itemdata_dao.xml");
			fileList.put(this.DAO_CRF, "oracle_crf_dao.xml");
			fileList.put(this.DAO_CRFVERSION, "oracle_crfversion_dao.xml");
			fileList.put(this.DAO_DATASET, "oracle_dataset_dao.xml");
			fileList.put(this.DAO_SECTION, "oracle_section_dao.xml");
			
			fileList.put(this.DAO_EVENTCRF, "oracle_eventcrf_dao.xml");
			fileList.put(this.DAO_ITEMFORMMETADATA, "oracle_item_form_metadata_dao.xml");
			fileList.put(this.DAO_DISCREPANCY_NOTE, "oracle_discrepancy_note_dao.xml");
			fileList.put(this.DAO_STUDY_PARAMETER, "oracle_study_parameter_value_dao.xml");
			fileList.put(this.DAO_ITEM_GROUP, "oracle_item_group_dao.xml");
			fileList.put(this.DAO_ITEM_GROUP_METADATA, "oracle_item_group_metadata_dao.xml");
			fileList.put(this.DAO_RULESET, "oracle_ruleset_dao.xml");
			fileList.put(this.DAO_RULE, "oracle_rule_dao.xml");
			fileList.put(this.DAO_RULE_ACTION, "oracle_action_dao.xml");
			fileList.put(this.DAO_EXPRESSION, "oracle_expression_dao.xml");
			fileList.put(this.DAO_RULESET_RULE, "oracle_rulesetrule_dao.xml");
			fileList.put(this.DAO_RULESET_AUDIT, "oracle_ruleset_audit_dao.xml");
			fileList.put(this.DAO_RULESETRULE_AUDIT, "oracle_rulesetrule_audit_dao.xml");
			fileList.put(this.DAO_ODM_EXTRACT, "oracle_odm_extract_dao.xml");
		} else if ("postgres".equals(dbName)) {
			fileList.put(this.DAO_USERACCOUNT, "useraccount_dao.xml");
			fileList.put(this.DAO_ARCHIVED_DATASET_FILE, "archived_dataset_file_dao.xml");
			fileList.put(this.DAO_STUDY, "study_dao.xml");
			fileList.put(this.DAO_STUDYEVENTDEFNITION, "studyeventdefinition_dao.xml");
			fileList.put(this.DAO_STUDYEVENT, "study_event_dao.xml");
			fileList.put(this.DAO_STUDYGROUP, "study_group_dao.xml");
			fileList.put(this.DAO_STUDYGROUPCLASS, "study_group_class_dao.xml");
			fileList.put(this.DAO_STUDYSUBJECT, "study_subject_dao.xml");
			fileList.put(this.DAO_SUBJECT, "subject_dao.xml");
			fileList.put(this.DAO_SUBJECTGROUPMAP, "subject_group_map_dao.xml");
			fileList.put(this.DAO_EVENTDEFINITIONCRF, "event_definition_crf_dao.xml");
			fileList.put(this.DAO_AUDITEVENT, "audit_event_dao.xml");
			fileList.put(this.DAO_AUDIT, "audit_dao.xml");
            fileList.put(this.DAO_DYNAMIC_EVENT, "dynamic_event_dao.xml");
			fileList.put(this.DAO_ITEM, "item_dao.xml");
			fileList.put(this.DAO_ITEMDATA, "itemdata_dao.xml");
			fileList.put(this.DAO_CRF, "crf_dao.xml");
			fileList.put(this.DAO_CRFVERSION, "crfversion_dao.xml");
			fileList.put(this.DAO_DATASET, "dataset_dao.xml");
			fileList.put(this.DAO_SECTION, "section_dao.xml");
			
			fileList.put(this.DAO_EVENTCRF, "eventcrf_dao.xml");
			fileList.put(this.DAO_ITEMFORMMETADATA, "item_form_metadata_dao.xml");
			fileList.put(this.DAO_DISCREPANCY_NOTE, "discrepancy_note_dao.xml");
			fileList.put(this.DAO_STUDY_PARAMETER, "study_parameter_value_dao.xml");
			fileList.put(this.DAO_ITEM_GROUP, "item_group_dao.xml");
			fileList.put(this.DAO_ITEM_GROUP_METADATA, "item_group_metadata_dao.xml");

			fileList.put(this.DAO_RULESET, "ruleset_dao.xml");
			fileList.put(this.DAO_RULE, "rule_dao.xml");
			fileList.put(this.DAO_RULE_ACTION, "action_dao.xml");
			fileList.put(this.DAO_EXPRESSION, "expression_dao.xml");
			fileList.put(this.DAO_RULESET_RULE, "rulesetrule_dao.xml");
			fileList.put(this.DAO_RULESET_AUDIT, "ruleset_audit_dao.xml");
			fileList.put(this.DAO_RULESETRULE_AUDIT, "rulesetrule_audit_dao.xml");
			fileList.put(this.DAO_SUBJECTTRANSFER, "subjecttransfer_dao.xml");

			fileList.put(this.DAO_ODM_EXTRACT, "odm_extract_dao.xml");

			// add files here as we port over to postgres, tbh
		}// should be either oracle or postgres, but what if the file is
			// gone?
		else {
			// throw an exception here, ssachs
		}

		Set DAONames = fileList.keySet();
		Iterator DAONamesIt = DAONames.iterator();

		while (DAONamesIt.hasNext()) {
			String DAOName = (String) DAONamesIt.next();
			String DAOFileName = (String) fileList.get(DAOName);

			DAODigester newDaoDigester = new DAODigester();

			try {
				if (System.getProperty("catalina.home") == null) {
					newDaoDigester.setInputStream(new FileInputStream(JUNIT_XML_DIR + DAOFileName));
				} else {
					newDaoDigester.setInputStream(resourceLoader.getResource("classpath:properties/" + DAOFileName)
							.getInputStream());
				}
				try {
					newDaoDigester.run();
					digesters.put(DAOName, newDaoDigester);
				} catch (SAXException saxe) {
					saxe.printStackTrace();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
