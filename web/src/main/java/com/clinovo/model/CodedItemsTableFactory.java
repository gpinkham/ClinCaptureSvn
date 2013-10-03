/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Limit;
import org.jmesa.view.component.Row;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.editor.DroplistFilterEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Encapsulates all the functionality required to create tables for coded items depending on the handler called in the controller.
 *
 */
@SuppressWarnings("rawtypes")
public class CodedItemsTableFactory extends AbstractTableFactory {

	private int studyId = -1;
	private StudyDAO studyDAO;
	private DataSource datasource;
	private EventCRFDAO eventCRFDAO;
	private List<CodedItem> codedItems;
	private EventDefinitionCRFDAO eventDefCRFDAO;
	private StudySubjectDAO studySubjectDAO;
	private StudyEventDefinitionDAO studyEventDefDao;

    private String CODED_DIV_PREFIX = "";
    private final String CODED_DIV_MIDDLE = "\"/><div id=\"";
    private final String CODED_DIV_SUFIX = "\"></div>";
    private final String COLUMN_WIDTH_PREFIX = "<div style=\"width:";
	private final String COLUMN_WIDTH_SUFFIX = "px\"/>";
	private String AJAX_REQUEST_PREFIX = "";
	private final String AJAX_REQUEST_MIDDLE = "\"><img style=\"float:left;\" width=\"17\" border=\"0\" title=\"Code\" alt=\"Code\" src=\"../images/";
    private final String AJAX_REQUEST_SUFIX =  "\"/></a>";
    private final String GOTO_CRF_EVENTCRFID = "&nbsp;&nbsp;<a onmouseup=\"javascript:setImage('Complete','../images/icon_DEcomplete.gif');\" href=\"../ViewSectionDataEntry?eventCRFId=";
    private final String GOTO_CRF_EVENTDEFCRFID = "&eventDefinitionCRFId=";
    private final String GOTO_CRF_EVENTID = "&tabId=1&eventId=";
    private final String GOTO_CRF_SUFIX = "&amp;viewFull=yes\"><img border=\"0\" title=\"Open CRF\" alt=\"GoToCRF\" height=\"17px\" src=\"../images/icon_DEcomplete.gif\" name=\"GOTO\"/></a>";
    public final static String AJAX_UNCODE_ITEM_PREFIX = "&nbsp;&nbsp;<a onClick=\"uncodeCodeItem(this)\" name=\"unCode\" itemId=\"";
    public final static String AJAX_UNCODE_ITEM_SUFFIX = "\"><img width=\"17\" border=\"0\" title=\"UnCode\" src=\"../images/code_uncode.png\" name=\"codeBtn\"/></a>";
    public final static String AJAX_UNCODE_ITEM_SUFFIX_HIDDEN = "\" style=\"display:none\"><img width=\"17\" border=\"0\" alt=\"UnCode\" src=\"../images/code_uncode.png\" name=\"codeBtn\"/></a>";
    public final static String DIV_VERSION_PREFIX = "<div name=\"codedItemVersion\">";
    public final static String DIV_VERSION_SUFIX = "</div>";

    @Override
    protected String getTableName() {
        return "codedItems";
    }

    @Override
    protected void configureColumns(TableFacade tableFacade, Locale locale) {
    	
        tableFacade.setColumnProperties("codedItem.verbatimTerm", "dictionaryList",
                "codedItem.version", "codedItem.subjectName", "codedItem.eventName", "codedItem.isCoded", "codedColumn", "actionColumn");
        
        Row row = tableFacade.getTable().getRow();
        
        configureColumn(row.getColumn("codedItem.verbatimTerm"), "Verbatim Term", new VerbatimTermCellEditor(), null);
        configureColumn(row.getColumn("dictionaryList"), "Dictionary", new DictionaryCellEditor(), null, false, false);
        configureColumn(row.getColumn("codedItem.version"), "Version", new VersionCellEditor(), null, true, true);
        configureColumn(row.getColumn("codedItem.subjectName"), "Study Subject ID", new SubjectCellEditor(), null, true, true);
        configureColumn(row.getColumn("codedItem.eventName"), "Study Event", new EventCellEditor(), null, true, true);
        configureColumn(row.getColumn("codedItem.isCoded"), "Status", new StatusCellEditor(), new StatusDroplistFilterEditor());
        configureColumn(row.getColumn("codedColumn"), "Coded", new CodedCellEditor(), null, false, false);
        configureColumn(row.getColumn("actionColumn"), "Actions", new ActionCellEditor(), new DefaultActionsEditor(
                locale), true, false);
    }

    @Override
    public void setDataAndLimitVariables(TableFacade tableFacade) {

        Limit limit = tableFacade.getLimit();

        if (!limit.isComplete()) {
            tableFacade.setTotalRows(codedItems.size());
        }

        Collection<HashMap<Object, Object>> codedItemsResult = new ArrayList<HashMap<Object, Object>>();
        
        for (CodedItem codedItem : codedItems) {
        	
            HashMap<Object, Object> h = new HashMap<Object, Object>();
            h.put("codedItem", codedItem);
            h.put("codedItem.itemId", codedItem.getItemDataId());
            h.put("codedItem.verbatimTerm", codedItem.getVerbatimTerm());
            h.put("codedItem.version", codedItem.getVersion());
            h.put("codedItem.subjectName", getSubjectBean(codedItem.getSubjectId()).getLabel());
            h.put("codedItem.eventName", getStudyEventDefinitionBean(codedItem.getEventCrfId(), codedItem.getCrfVersionId()).getName());
            h.put("codedItem.isCoded", codedItem.isCoded() ? "Completed" : "Available");

            codedItemsResult.add(h);
        }

        tableFacade.setItems(codedItemsResult);
    }
    
    @SuppressWarnings("unchecked")
    private class VerbatimTermCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {
            String value = "";
            String verbatimTerm = (String) ((HashMap<Object, Object>) item).get("codedItem.verbatimTerm");
            if (!verbatimTerm.isEmpty()) {
                StringBuilder url = new StringBuilder();
                url.append(verbatimTerm)
                        .append(COLUMN_WIDTH_PREFIX)
                        .append("160")
                        .append(COLUMN_WIDTH_SUFFIX);
                value = url.toString();
            }
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    private class DictionaryCellEditor implements CellEditor {
    	
        private StudyParameterValueDAO studyParameterDAO = null;

		public Object getValue(Object item, String property, int rowcount) {
            String value = "";
            CodedItem codedItem = (CodedItem) ((HashMap<Object, Object>) item).get("codedItem");
            StringBuilder url = new StringBuilder();
            url.append(selectItemDictionary(codedItem))
                    .append(COLUMN_WIDTH_PREFIX)
                    .append("100")
                    .append(COLUMN_WIDTH_SUFFIX);
            value = url.toString();
            return value;
        }

        private StringBuilder selectItemDictionary(CodedItem codedItem) {

            HashMap<String, String> dictionaries = new HashMap<String, String>();
            
            dictionaries.put("", "&nbsp;");
            dictionaries.put("MedDRA", "MedDRA");
            dictionaries.put("ICD9", "ICD9");
            dictionaries.put("ICD10", "ICD10");
            Iterator iterator = dictionaries.entrySet().iterator();

            StringBuilder dictionariesHtml = new StringBuilder("");
            if (isLoggedInUserMonitor()) {
                dictionariesHtml.append(codedItem.getDictionary());
                return dictionariesHtml;
            } else {
                dictionariesHtml.append("<select>");
                while (iterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) iterator.next();
                    dictionariesHtml.append("\"<option value=\"")
                            .append(mapEntry.getKey()).append("\"")
                            .append(isSelected(mapEntry.getKey(), codedItem.getDictionary()))
                            .append(">").append(mapEntry.getValue())
                            .append("</option>");
                }
                dictionariesHtml.append("</select>");
            }
            return dictionariesHtml;
        }

        private String isSelected(Object key, String codeditemDictionary) {
        	
        	StudyParameterValueBean defaultMedicalCodingDictionaryParam = getStudyParameterDAO().findByHandleAndStudy(studyId, "defaultMedicalCodingDictionary");
        	String defaultMedicalCodingDictionary = defaultMedicalCodingDictionaryParam.getValue();
        	
            String selected = "";
			if (key.toString().equalsIgnoreCase(codeditemDictionary)) {

				selected = " selected ";
			} else {

				// Not selected
				if (selected.isEmpty()) {
					if (codeditemDictionary != null && codeditemDictionary.isEmpty()
							&& key.equals(defaultMedicalCodingDictionary)) {
						selected = " selected ";
					}
				}
			}
            
            return selected;
        }

		private StudyParameterValueDAO getStudyParameterDAO() {
			
			if(studyParameterDAO == null) {
				studyParameterDAO = new StudyParameterValueDAO(datasource);
			}
			
			return studyParameterDAO;
		}
    }
    
	
    @SuppressWarnings({ "unchecked"})
    private class VersionCellEditor implements CellEditor {

		public Object getValue(Object item, String property, int rowcount) {
            String value = "";
            CodedItem codedItem = (CodedItem) ((HashMap<Object, Object>) item).get("codedItem");
            if (codedItem != null) {
                StringBuilder url = new StringBuilder();
                url.append(DIV_VERSION_PREFIX)
                        .append(codedItem.getVersion())
                        .append(DIV_VERSION_SUFIX);
                value = url.toString();
            }
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    private class CodedCellEditor implements CellEditor {
    	
        public Object getValue(Object item, String property, int rowcount) {
        	
            String value = "";
            CodedItem codedItem = (CodedItem) ((HashMap<Object, Object>) item).get("codedItem");
            String inputTerm = codedItem.isCoded() ? codedItem.getCodedTerm() : codedItem.getVerbatimTerm();
            
            if (codedItem != null) {
            	
                StringBuilder url = new StringBuilder();
                
				if (isLoggedInUserMonitor()) {

                    url.append(inputTerm)
                        .append(COLUMN_WIDTH_PREFIX)
                        .append("250")
                        .append(COLUMN_WIDTH_SUFFIX);
					return url.toString();
				} else if (codedItem.isCoded()) {

                    CODED_DIV_PREFIX = "Search: <input style=\"border:1px solid #a6a6a6;margin-bottom: 2px;background-color:#d9d9d9;color:#4D4D4D\" disabled=\"true\" type=\"text\" value=\"";
                } else {

                    CODED_DIV_PREFIX = "Search: <input style=\"border:1px solid #a6a6a6;margin-bottom: 2px;background-color:#d9d9d9;color:#4D4D4D\" type=\"text\" value=\"";
                }
				
                url.append(CODED_DIV_PREFIX).append(inputTerm)
                        .append(CODED_DIV_MIDDLE)
                        .append(codedItem.getItemDataId())
                        .append(CODED_DIV_SUFIX)
                        .append(COLUMN_WIDTH_PREFIX)
                        .append("420")
                        .append(COLUMN_WIDTH_SUFFIX);
                value = url.toString();
            }
            return value;
        }
    }
    
    @SuppressWarnings("unchecked")
    private class ActionCellEditor implements CellEditor {
    	
		public Object getValue(Object item, String property, int rowcount) {
			
            String value = "";
            CodedItem codedItem = (CodedItem) ((HashMap<Object, Object>) item).get("codedItem");
            EventCRFBean eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(codedItem.getEventCrfId());
            StudyBean studyBean = (StudyBean) studyDAO.findByStudySubjectId(eventCRFBean.getStudySubjectId());
            EventDefinitionCRFBean eventDefCRFBean = (EventDefinitionCRFBean) eventDefCRFDAO.findByStudyEventIdAndCRFVersionId(studyBean, eventCRFBean.getStudyEventId(), codedItem.getCrfVersionId());
            String codedItemButton = codedItem.isCoded() ? "code_confirm.png" : "code_blue.png";
            String uncodedItemButton = "";

            if (codedItem != null) {
            	
                StringBuilder url = new StringBuilder();
                
                // Monitor is readonly
 				if (isLoggedInUserMonitor()) {
 					
 					AJAX_REQUEST_PREFIX = "<a itemId=\"";
 				} else {

                    AJAX_REQUEST_PREFIX = "<a onClick=\"codeItem(this)\" name=\"Code\" itemId=\"";
                    uncodedItemButton = (AJAX_UNCODE_ITEM_PREFIX) + codedItem.getItemDataId() + (codedItem.isCoded() ? (AJAX_UNCODE_ITEM_SUFFIX) : AJAX_UNCODE_ITEM_SUFFIX_HIDDEN);
                }
 				
                url.append(AJAX_REQUEST_PREFIX)
                	.append(codedItem.getItemDataId())
                    .append(AJAX_REQUEST_MIDDLE)
                    .append(codedItemButton)
                    .append(AJAX_REQUEST_SUFIX)
                    .append(GOTO_CRF_EVENTCRFID)
                    .append(eventCRFBean.getId())
                    .append(GOTO_CRF_EVENTDEFCRFID)
                    .append(eventDefCRFBean.getId())
                    .append(GOTO_CRF_EVENTID)
                    .append(eventCRFBean.getStudyEventId()).append(GOTO_CRF_SUFIX)
                    .append(uncodedItemButton)
                    .append(COLUMN_WIDTH_PREFIX)
                    .append("150")
                    .append(COLUMN_WIDTH_SUFFIX);
                value = url.toString();
            }
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    private class StatusCellEditor implements CellEditor {
    	
        public Object getValue(Object item, String property, int cowcount) {
        	
            String value = "";
            String codedItemStatus = (String) ((HashMap<Object, Object>) item).get("codedItem.isCoded");
            if (!codedItemStatus.isEmpty()) {
            	
                StringBuilder url = new StringBuilder();
                url.append(codedItemStatus)
                    .append(COLUMN_WIDTH_PREFIX)
                    .append("80")
                    .append(COLUMN_WIDTH_SUFFIX);
                value = url.toString();
            }
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    private class SubjectCellEditor implements CellEditor {
        public Object getValue(Object item, String property, int cowcount) {
            String value = "";
            String subjectLabel = (String) ((HashMap<Object, Object>) item).get("codedItem.subjectName");
            if (!subjectLabel.isEmpty()) {
                StringBuilder url = new StringBuilder();
                url.append(subjectLabel);
                value = url.toString();
            }
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    private class EventCellEditor implements CellEditor {
        public Object getValue(Object item, String property, int cowcount) {
            String value = "";
            String eventName = (String) ((HashMap<Object, Object>) item).get("codedItem.eventName");
            if (!eventName.isEmpty()) {
                StringBuilder url = new StringBuilder();
                url.append(eventName);
                value = url.toString();
            }
            return value;
        }
    }

    private class StatusDroplistFilterEditor extends DroplistFilterEditor {
    	
        protected List<Option> getOptions() {
        	
            List<Option> options = new ArrayList<Option>();
            options.add(new Option("Completed", "Completed"));
            options.add(new Option("Available", "Available"));
            return options;
        }
    }

    public void setStudyDAO(StudyDAO studyDAO) {
        this.studyDAO = studyDAO;
    }
    
    public void setCodedItems(List<CodedItem> codedItems) {
        this.codedItems = codedItems;
    }

    public void setEventCRFDAO(EventCRFDAO eventCRFDAO) {
        this.eventCRFDAO = eventCRFDAO;
    }

    public void setEventDefinitionCRFDAO(EventDefinitionCRFDAO eventDefenitionCRFDAO) {
        this.eventDefCRFDAO = eventDefenitionCRFDAO;
    }

	public void setStudyId(String studyId) {

		try {
			this.studyId = Integer.parseInt(studyId);
		} catch (Exception ex) {
			this.studyId = 1;
		}
	}

	public void setDataSource(DataSource datasource) {
		this.datasource = datasource;
	}
	
	public boolean isLoggedInUserMonitor() {

		UserAccountDAO userDAO = new UserAccountDAO(datasource);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// Really type coding for root
		UserAccountBean loggedInUser = (UserAccountBean) userDAO.findByUserName(authentication.getName());

		return loggedInUser.getRoleByStudy(studyId).getName().equalsIgnoreCase("study monitor");
	}
	
	public void setStudySubjectDAO(StudySubjectDAO studySubjectDAO) {
		this.studySubjectDAO = studySubjectDAO;
	}

	public void setStudyEventDefinitionDAO(StudyEventDefinitionDAO studyEventDefDao) {
		this.studyEventDefDao = studyEventDefDao;
	}
		
    private StudySubjectBean getSubjectBean(int subjectId) {
        StudySubjectBean subjectBean = (StudySubjectBean) studySubjectDAO.findByPK(subjectId);
        return subjectBean;
    }

    private StudyEventDefinitionBean getStudyEventDefinitionBean(int eventCrfId, int crfVersionId) {
        EventCRFBean eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(eventCrfId);
        StudyBean studyBean = (StudyBean) studyDAO.findByStudySubjectId(eventCRFBean.getStudySubjectId());
        EventDefinitionCRFBean eventDefCRFBean = (EventDefinitionCRFBean) eventDefCRFDAO.findByStudyEventIdAndCRFVersionId(studyBean, eventCRFBean.getStudyEventId(), crfVersionId);
        StudyEventDefinitionBean studyEventDefBean = studyEventDefDao.findByEventDefinitionCRFId(eventDefCRFBean.getId());
        return studyEventDefBean;
    }
}
