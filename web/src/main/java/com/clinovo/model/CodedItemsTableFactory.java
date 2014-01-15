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
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
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
    private ItemDataDAO itemDataDAO;
	private List<CodedItem> codedItems;
    private List<Term> terms;
	private EventDefinitionCRFDAO eventDefCRFDAO;
	private StudySubjectDAO studySubjectDAO;
	private StudyEventDefinitionDAO studyEventDefDao;
    private CRFDAO crfDAO;

    private final String medicalCodingContextNeeded;
    private final String showMoreLink;

    private String CODED_DIV_PREFIX = "";
    private final String CODED_DIV_MIDDLE = "\"/><div id=\"";
    private final String CODED_DIV_SUFIX = "\"></div>";
    private final String COLUMN_WIDTH_PREFIX = "<div style=\"width:";
	private final String COLUMN_WIDTH_SUFFIX = "px\"/>";
	private String AJAX_REQUEST_PREFIX = "";
	private final String AJAX_REQUEST_MIDDLE = "\"><img style=\"float:left;\" height=\"17\" border=\"0\" title=\"Code\" alt=\"Code\" src=\"../images/";
    private final String AJAX_REQUEST_SUFIX =  "\"/></a>";
    private final String GOTO_CRF_EVENTCRFID = "&nbsp;&nbsp;<a onmouseup=\"javascript:setImage('Complete','../images/icon_DEcomplete_long.gif');\" href=\"../ViewSectionDataEntry?eventCRFId=";
    private final String GOTO_CRF_EVENTDEFCRFID = "&eventDefinitionCRFId=";
    private final String GOTO_CRF_EVENTID = "&tabId=1&eventId=";
    private final String GOTO_CRF_SUFIX = "&amp;viewFull=yes\"><img border=\"0\" title=\"Open CRF\" alt=\"GoToCRF\" height=\"17px\" src=\"../images/icon_DEcomplete_long.gif\" name=\"GOTO\"/></a>";
    public final static String AJAX_UNCODE_ITEM_PREFIX = "&nbsp;&nbsp;<a onClick=\"uncodeCodeItem(this)\" name=\"unCode\" itemId=\"";
    public final static String AJAX_UNCODE_ITEM_SUFFIX = "\"><img height=\"17\" border=\"0\" title=\"UnCode\" src=\"../images/code_uncode.png\" name=\"codeBtn\"/></a>";
    public final static String AJAX_UNCODE_ITEM_SUFFIX_HIDDEN = "\" style=\"visibility:hidden\"><img height=\"17\" border=\"0\" alt=\"UnCode\" src=\"../images/code_uncode.png\" name=\"codeBtn\"/></a>";
    public final static String AJAX_DELETE_TERM_PREFIX = "&nbsp;&nbsp;<a onClick=\"deleteTerm(this)\" name=\"deleteTerm\" itemId=\"";
    public final static String AJAX_DELETE_TERM_MIDDLE ="\" term=\"";
    public final static String AJAX_DELETE_TERM_SUFFIX ="\"><img height=\"17\" border=\"0\" title=\"deleteTerm\" src=\"../images/bt_Delete.gif\" name=\"deleteTermBtn\"/></a>";
    public final static String AJAX_DELETE_TERM_SUFFIX_HIDDEN = "\" style=\"visibility:hidden\"><img height=\"17\" border=\"0\" src=\"../images/bt_Delete.gif\" name=\"deleteTermBtn\"/></a>";
    public final static String DIV_VERSION_PREFIX = "<div name=\"codedItemVersion\">";
    public final static String DIV_VERSION_SUFIX = "</div>";
    public final static String DIV_DICITIONARY_PREFIX = "<div name=\"termDictionary\">";
    public final static String DIV_DICTIONARY_SUFIX = "</div>";
    public final static String DIV_ITEMDATAVALUE_PREFIX = "<div name=\"itemDataValue\">";
    public final static String DIV_ITEMDATAVALUE_SUFIX = "</div>";

    public CodedItemsTableFactory(String medicalCodingContextNeeded, String showMoreLink) {

        this.medicalCodingContextNeeded = medicalCodingContextNeeded;
        this.showMoreLink = showMoreLink;
    }

    @Override
    protected String getTableName() {
        return "codedItems";
    }

    @Override
    protected void configureColumns(TableFacade tableFacade, Locale locale) {

        tableFacade.setColumnProperties("itemDataValue", "dictionaryList",
                "codedItem.version", "status", "subjectName", "eventName", "crfName", "codedColumn", "actionColumn");
        
        Row row = tableFacade.getTable().getRow();
        
        configureColumn(row.getColumn("itemDataValue"), "Verbatim Term", new ItemDataValueCellEditor(), null);
        configureColumn(row.getColumn("dictionaryList"), "Dictionary", new DictionaryCellEditor(), null, false, false);
        configureColumn(row.getColumn("codedItem.version"), "Version", new VersionCellEditor(), null, true, true);
        configureColumn(row.getColumn("status"), "Status", new StatusCellEditor(), new StatusDroplistFilterEditor());
        configureColumn(row.getColumn("subjectName"), "Study Subject ID", new SubjectCellEditor(), null, true, true);
        configureColumn(row.getColumn("eventName"), "Study Event", new EventCellEditor(), null, true, true);
        configureColumn(row.getColumn("crfName"), "CRF", new CrfCellEditor(), null, true, true);
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
            h.put("codedItem.itemId", codedItem.getItemId());
            h.put("itemDataValue", getItemDataValue(codedItem.getItemId()));
            h.put("codedItem.version", codedItem.getVersion());
            h.put("subjectName", getSubjectBean(codedItem.getSubjectId()).getLabel());
            h.put("eventName", getStudyEventDefinitionBean(codedItem.getEventCrfId(), codedItem.getCrfVersionId()).getName());
            h.put("status", getCodedItemStatus(codedItem));
            h.put("crfName", getCrfName(codedItem.getCrfVersionId()));

            codedItemsResult.add(h);
        }

        tableFacade.setItems(codedItemsResult);
    }

    private String getCodedItemStatus(CodedItem codedItem) {

        if (codedItem.getStatus().equals("NOT_CODED")) {

            return "To be Coded";
        } else if (codedItem.getStatus().equals("CODED")) {

            return "Completed";
        } else if ((codedItem.getStatus().equals("IN_PROGRESS"))) {

            return "In Progress";
        }

      return "Unknown";
    }

    @SuppressWarnings("unchecked")
    private class ItemDataValueCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {
            String value = "";
            String itemDataValue = (String) ((HashMap<Object, Object>) item).get("itemDataValue");
            if (!itemDataValue.isEmpty()) {
                StringBuilder url = new StringBuilder();
                url.append(DIV_ITEMDATAVALUE_PREFIX)
                        .append(itemDataValue)
                        .append(DIV_ITEMDATAVALUE_SUFIX)
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
    	
        public Object getValue(Object item, String property, int rowcount) {
            String value = "";
            CodedItem codedItem = (CodedItem) ((HashMap<Object, Object>) item).get("codedItem");
            StringBuilder url = new StringBuilder();
            url.append(DIV_DICITIONARY_PREFIX)
            .append(selectItemDictionary(codedItem.getDictionary()))
                    .append(DIV_DICTIONARY_SUFIX)
                    .append(COLUMN_WIDTH_PREFIX)
                    .append("100")
                    .append(COLUMN_WIDTH_SUFFIX);
            value = url.toString();
            return value;
        }

        private String selectItemDictionary(String codedItemDictionary) {

            if(codedItemDictionary.equalsIgnoreCase("ICD_9CM")) {
                return "ICD 9CM";
            } else if(codedItemDictionary.equalsIgnoreCase("ICD_10")) {
                return "ICD 10";
            } else if (codedItemDictionary.equalsIgnoreCase("MEDDRA")) {
                return "MedDRA";
            } else if (codedItemDictionary.equalsIgnoreCase("WHODRUG")) {
                return "WhoDRUG";
            }
            return "Dictionary Not Found";
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

            if (codedItem != null) {
            	
                StringBuilder url = new StringBuilder();
                
				if (isLoggedInUserMonitor()) {

                    url.append(codedItem.getPreferredTerm())
                        .append(COLUMN_WIDTH_PREFIX)
                        .append("250")
                        .append(COLUMN_WIDTH_SUFFIX);
					return url.toString();
				} else if (codedItem.getStatus().equals("CODED") || codedItem.getStatus().equals("IN_PROGRESS")) {

                    CODED_DIV_PREFIX = "Search: <input style=\"border:1px solid #a6a6a6;margin-bottom: 2px;background-color:#d9d9d9;color:#4D4D4D\" disabled=\"true\" type=\"text\" value=\"";
                } else {

                    CODED_DIV_PREFIX = "Search: <input style=\"border:1px solid #a6a6a6;margin-bottom: 2px;background-color:#d9d9d9;color:#4D4D4D\" type=\"text\" value=\"";
                }
				
                url.append(CODED_DIV_PREFIX).append(codedItem.getPreferredTerm())
                         .append(CODED_DIV_MIDDLE)
                         .append(codedItem.getItemId())
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
            String deleteTermButton = "";
            String uncodedItemButton = "";
            CodedItem codedItem = (CodedItem) ((HashMap<Object, Object>) item).get("codedItem");
            EventCRFBean eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(codedItem.getEventCrfId());
            StudyBean studyBean = (StudyBean) studyDAO.findByStudySubjectId(eventCRFBean.getStudySubjectId());
            EventDefinitionCRFBean eventDefCRFBean = (EventDefinitionCRFBean) eventDefCRFDAO.findByStudyEventIdAndCRFVersionId(studyBean, eventCRFBean.getStudyEventId(), codedItem.getCrfVersionId());
            String codedItemButtonColor = codedItem.isCoded() ? "code_confirm.png" : "code_blue.png";

            if (codedItem != null) {
            	
                StringBuilder url = new StringBuilder();
                
                // Monitor is readonly
 				if (isLoggedInUserMonitor()) {
 					
 					AJAX_REQUEST_PREFIX = "<a itemId=\"";
                    deleteTermButton = AJAX_DELETE_TERM_PREFIX + AJAX_DELETE_TERM_SUFFIX_HIDDEN;
 				} else {

                    AJAX_REQUEST_PREFIX = codedItem.getStatus().equals("IN_PROGRESS") ? "<a onClick=\"codeItem(this)\" style=\"visibility:hidden\" name=\"Code\" itemId=\"" : "<a onClick=\"codeItem(this)\" name=\"Code\" itemId=\"";
                    uncodedItemButton = (AJAX_UNCODE_ITEM_PREFIX) + codedItem.getItemId() + (codedItem.isCoded() ? (AJAX_UNCODE_ITEM_SUFFIX) : AJAX_UNCODE_ITEM_SUFFIX_HIDDEN);
                    deleteTermButton = (AJAX_DELETE_TERM_PREFIX) + codedItem.getItemId() + AJAX_DELETE_TERM_MIDDLE + codedItem.getPreferredTerm().toLowerCase() + (isDeleteable(codedItem) ? AJAX_DELETE_TERM_SUFFIX : AJAX_DELETE_TERM_SUFFIX_HIDDEN);
                }
 				
                url.append(AJAX_REQUEST_PREFIX)
                	.append(codedItem.getItemId())
                    .append(AJAX_REQUEST_MIDDLE)
                    .append(codedItemButtonColor)
                    .append(AJAX_REQUEST_SUFIX)
                    .append(uncodedItemButton)
                    .append(deleteTermButton)
                    .append(GOTO_CRF_EVENTCRFID)
                    .append(eventCRFBean.getId())
                    .append(GOTO_CRF_EVENTDEFCRFID)
                    .append(eventDefCRFBean.getId())
                    .append(GOTO_CRF_EVENTID)
                    .append(eventCRFBean.getStudyEventId()).append(GOTO_CRF_SUFIX)
                    .append(COLUMN_WIDTH_PREFIX)
                    .append("150")
                    .append(COLUMN_WIDTH_SUFFIX);
                value = url.toString();
            }
            return value;
        }
    }

	private boolean isDeleteable(CodedItem codedItem) {
			
			for (Term term : terms) {

				if (term.getPreferredName().equalsIgnoreCase(codedItem.getPreferredTerm())
						&& term.getExternalDictionaryName().equals(codedItem.getDictionary())) {

					return true;
				}
			}

		return false;
	}

    @SuppressWarnings("unchecked")
    private class StatusCellEditor implements CellEditor {
    	
        public Object getValue(Object item, String property, int cowcount) {
        	
            String value = "";
            String codedItemStatus = (String) ((HashMap<Object, Object>) item).get("status");
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
            String subjectLabel = (String) ((HashMap<Object, Object>) item).get("subjectName");
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
            String eventName = (String) ((HashMap<Object, Object>) item).get("eventName");
            if (!eventName.isEmpty()) {
                StringBuilder url = new StringBuilder();
                url.append(eventName);
                value = url.toString();
            }
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    private class CrfCellEditor implements CellEditor {
        public Object getValue(Object item, String property, int cowcount) {
            String value = "";
            String crfName = (String) ((HashMap<Object, Object>) item).get("crfName");
            if (!crfName.isEmpty()) {
                StringBuilder url = new StringBuilder();
                url.append(crfName)
                        .append(COLUMN_WIDTH_PREFIX)
                        .append("120")
                        .append(COLUMN_WIDTH_SUFFIX);
                value = url.toString();
            }
            return value;
        }
    }

    private class StatusDroplistFilterEditor extends DroplistFilterEditor {
    	
        protected List<Option> getOptions() {
        	
            List<Option> options = new ArrayList<Option>();
            options.add(new Option("To be Coded", "To be Coded"));
            options.add(new Option(" To be Approved", " To be Approved"));
            options.add(new Option("Completed", "Completed"));
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

    public void setTerms(List<Term> terms) {
        this.terms = terms;
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

		UserAccountBean loggedInUser = (UserAccountBean) userDAO.findByUserName(authentication.getName());

		return loggedInUser.getRoleByStudy(studyId).getName().equalsIgnoreCase("study monitor");
	}
	
	public void setStudySubjectDAO(StudySubjectDAO studySubjectDAO) {
		this.studySubjectDAO = studySubjectDAO;
	}

    public void setCrfDAO(CRFDAO crfDao) {
        this.crfDAO = crfDao;
    }

	public void setStudyEventDefinitionDAO(StudyEventDefinitionDAO studyEventDefDao) {
		this.studyEventDefDao = studyEventDefDao;
	}

    public void setItemDataDAO(ItemDataDAO itemDataDAO) {
        this.itemDataDAO = itemDataDAO;
    }

    private StudySubjectBean getSubjectBean(int subjectId) {
    	
        return  (StudySubjectBean) studySubjectDAO.findByPK(subjectId);
    }

    private StudyEventDefinitionBean getStudyEventDefinitionBean(int eventCrfId, int crfVersionId) {
    	
        EventCRFBean eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(eventCrfId);
        StudyBean studyBean = (StudyBean) studyDAO.findByStudySubjectId(eventCRFBean.getStudySubjectId());
        EventDefinitionCRFBean eventDefCRFBean = (EventDefinitionCRFBean) eventDefCRFDAO.findByStudyEventIdAndCRFVersionId(studyBean, eventCRFBean.getStudyEventId(), crfVersionId);
        
        return studyEventDefDao.findByEventDefinitionCRFId(eventDefCRFBean.getId());
    }

    private String getItemDataValue(int itemId) {
        ItemDataBean itemDataBean = (ItemDataBean) itemDataDAO.findByPK(itemId);
        return itemDataBean.getValue();
    }

    private String getCrfName(int eventCrfVersionId) {
            CRFBean crfBean = crfDAO.findByVersionId(eventCrfVersionId);
           return crfBean.getName();
    }

    @Override
    public void configureTableFacadePostColumnConfiguration(TableFacade tableFacade) {

        boolean showMore = false;
        boolean contextNeeded = false;

        if (showMoreLink.equalsIgnoreCase("true")) {
            showMore = true;
        }
        if (medicalCodingContextNeeded.equalsIgnoreCase("yes")) {
            contextNeeded = true;
        }

        CodedItemsTableToolbar toolbar = new CodedItemsTableToolbar(showMore, contextNeeded);
        tableFacade.setToolbar(toolbar);
    }

}
