package com.clinovo.pages.beans;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.clinovo.utils.Common;

/**
 * CRF bean.
 */
public class CRF{
	
	public static final String CRFS_TO_CHECK_SDV_STATUS = "CRFs to check SDV tatus";
	public static final String CRFS_TO_CHECK_EXIST = "CRFs to check for existence";
	public static final String CRFS_TO_CHECK_SAVED_DATA = "CRFs to check saved data";

	public static ComparatorForItemOIDs comparatorForItemOIDs = new ComparatorForItemOIDs();
	public static final String[] ARRAY_OF_PARAMETERS_TO_SKIP = new String[] {"Mark Complete", "Study Subject ID", "Event Name", "CRF Name", "Section Name", "Add Rows"};
	
	private String version = "";
	private String name = "";
	private String studySubjectID = "";
	private String eventName = "";
	private String currentSectionName = "";
	private String addRows = "";
	private Map<String, String> fieldNameToValueMap = new HashMap<String, String>();
	private String markComplete = "no";
	private List<CRFSection> sections = new ArrayList<>();

	public Map<String, String> getFieldNameToValueMap() {
		return fieldNameToValueMap;
	}

	public void setFieldNameToValueMap(Map<String, String> fieldNameToValueMap) {
		this.fieldNameToValueMap = fieldNameToValueMap;
	}

	/**
	 * Method fill CRF from table row.
	 *
	 * @param values
	 *            Map<String, String>
	 * @return CRF
	 */
	public static CRF fillCRFFromTableRow(Map<String, String> values) {
		
		String sectionName = values.get("Section Name") == null? "" : values.get("Section Name");
		CRF oneSectionCRF = new CRF();
		
		if (values.containsKey("Mark Complete")) oneSectionCRF.setMarkComplete(values.get("Mark Complete"));
	
		if (values.containsKey("Study Subject ID")) oneSectionCRF.setStudySubjectID(values.get("Study Subject ID"));
		
		if (values.containsKey("Event Name")) oneSectionCRF.setEventName(values.get("Event Name"));
		
		if (values.containsKey("CRF Name")) oneSectionCRF.setName(values.get("CRF Name"));
		
		oneSectionCRF.setCurrentSectionName(sectionName);
		oneSectionCRF.getSections().add(new CRFSection(sectionName));
		
		if (values.containsKey("Add Rows")) {
			oneSectionCRF.setAddRows(values.get("Add Rows"));
			oneSectionCRF.getSections().get(0).setAddRows(values.get("Add Rows"));
		}
		
		Common.removeValuesFromMap(values, ARRAY_OF_PARAMETERS_TO_SKIP);
		
		oneSectionCRF.setFieldNameToValueMap(getFieldToValueMap(values));
		oneSectionCRF.getSections().get(0).setFieldNameToValueMap(oneSectionCRF.getFieldNameToValueMap());
		
		return oneSectionCRF;
	}
	
	public static Map<String, String> getFieldToValueMap(Map<String, String> values) {
		if (values.containsKey("item1")) {
			Map<String, String> parsedValues = new HashMap<String, String>();
			String parsedKey;
			String parsedValue;
			String value;
			String number;
			for (String key: values.keySet()) {
				value = values.get(key);
				if (value.isEmpty()) continue;
				number = key.replaceFirst(".*item", "");
				parsedValue = value.replaceFirst(".*input(\\d+)\\(\\w+\\):", "").trim();
				parsedKey = value.replaceFirst(": "+ parsedValue, "").trim();
				
				parsedValues.put("("+ number +")"+ parsedKey, parsedValue);
			}
			
			return parsedValues;
		}
		
		return values;
	}

	public String getMarkComplete() {
		return markComplete;
	}

	public void setMarkComplete(String markComplete) {
		this.markComplete = markComplete;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStudySubjectID() {
		return studySubjectID;
	}

	public void setStudySubjectID(String studySubjectID) {
		this.studySubjectID = studySubjectID;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getCurrentSectionName() {
		return currentSectionName;
	}

	public void setCurrentSectionName(String currentSectionName) {
		this.currentSectionName = currentSectionName;
	}

	public String getAddRows() {
		return addRows;
	}

	public void setAddRows(String addRows) {
		this.addRows = addRows;
	}

	/**
	 * Method changes keys for CRF items.
	 *
	 * @param map
	 * 			Map<String, String>
	 * @param values
	 * 			Map<String, String>
	 * @return
	 */
	public static Map<String, String> changeKeysForCRFItems(Map<String, String> map, Map<String, String> values) {
    	Map<String, String> result = new HashMap<String, String>();
    	int max = 0;
    	Pattern p = Pattern.compile(".*item(\\d+)");
    	for (String key: map.keySet()) {
    		Matcher m = p.matcher(key);
    		if (m.matches()) {
    			int itemNumber = Integer.parseInt(m.group(1));
    			max = max > itemNumber ? max : itemNumber;
    		}
    	}
    	for (String key: values.keySet()) {
    		Matcher m = p.matcher(key);
    		if (m.matches()) {
    			int itemNumber = max + Integer.parseInt(m.group(1));
    			result.put("item" + itemNumber, values.get(key));
    		}
    	}
		return result;
	}

	public List<CRFSection> getSections() {
		return sections;
	}

	public void setSections(List<CRFSection> sections) {
		this.sections = sections;
	}

	public static void setMarkCRFCompleteStatus(CRF crf) {
		if (crf.getSections() == null || crf.getSections().isEmpty()) return;
		boolean shouldCRFBeMarkAsCompleted = "yes".equals(crf.getMarkComplete()) || 
				"yes".equals(crf.getSections().get(crf.getSections().size() - 1).getMarkComplete());
		
		crf.getSections().get(crf.getSections().size() - 1).setMarkComplete(shouldCRFBeMarkAsCompleted? "yes" : "no");
		crf.setMarkComplete(shouldCRFBeMarkAsCompleted? "yes" : "no");
	}

	public static CRF createCRFFromSection(CRFSection section) {
		CRF oneSectionCRF = new CRF();
		oneSectionCRF.setFieldNameToValueMap(section.getFieldNameToValueMap());
		oneSectionCRF.setMarkComplete(section.getMarkComplete());
		oneSectionCRF.setAddRows(section.getAddRows());
		return oneSectionCRF;
	}
}

class ComparatorForItemOIDs implements Comparator<String> {
	public int compare(String str1, String str2) {
		
		//str = ({a})GROUP_NAME_{b}input{c}(T)
		int a1, b1, c1, d1, a2, b2, c2, d2;
		
		Pattern p = Pattern.compile("\\((\\d+)\\).+");
		Matcher m1 = p.matcher(str1);
		Matcher m2 = p.matcher(str2);
		a1 = m1.matches()? Integer.parseInt(m1.group(1)) : 0;
		a2 = m2.matches()? Integer.parseInt(m2.group(1)) : 0;
		
		p = Pattern.compile(".+_(\\d+)input.+");
		m1 = p.matcher(str1);
		m2 = p.matcher(str2);
		b1 = m1.matches()? Integer.parseInt(m1.group(1)) : 0;
		b2 = m2.matches()? Integer.parseInt(m2.group(1)) : 0;
		
		p = Pattern.compile(".+_(\\d+)input.+");
		m1 = p.matcher(str1);
		m2 = p.matcher(str2);
		c1 = m1.matches()? Integer.parseInt(m1.group(1)) : 0;		
		c2 = m2.matches()? Integer.parseInt(m2.group(1)) : 0;
		
		p = Pattern.compile(".*input(\\d+).*");
		m1 = p.matcher(str1);
		m2 = p.matcher(str2);
		d1 = m1.matches()? Integer.parseInt(m1.group(1)) : 0;
		d2 = m2.matches()? Integer.parseInt(m2.group(1)) : 0;
		
		if (a1 > a2) return 1;
		if (a2 > a1) return -1;
		if (b1 > b2) return 1;
		if (b2 > b1) return -1;
		if (c1 > c2) return 1;
		if (c2 > c1) return -1;
		if (d1 > d2) return 1;
		if (d2 > d1) return -1;
		
		return 0;
	}
}
