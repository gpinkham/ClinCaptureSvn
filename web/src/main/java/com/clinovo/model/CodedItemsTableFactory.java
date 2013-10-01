package com.clinovo.model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Limit;
import org.jmesa.view.component.Row;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.editor.DroplistFilterEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

	private final String CODED_DIV_SUFIX = "\"></div>";
	private final String GOTO_CRF_CRFID = "&amp;ecId=";
	private final String COLUMN_WIDTH_SUFFIX = "px\"/>";
	private final String CODED_DIV_MIDDLE = "\"/><div id=\"";
	private final String GOTO_CRF_EVENTID = "&amp;tabId=1&eventId=";
	private final String COLUMN_WIDTH_PREFIX = "<div style=\"width:";
	private final String AJAX_REQUEST_SUFIX =  "\" name=\"codeBtn\"/></a>";
	private String AJAX_REQUEST_PREFIX = "<a onClick=\"codeItem(this)\" itemId=\"";
	private final String AJAX_REQUEST_MIDDLE = "\"><img style=\"float:left;\" width=\"17\" border=\"0\" title=\"Code\" alt=\"Code\" src=\"../images/";
    private String CODED_DIV_PREFIX = "Search: <input style=\"border:1px solid #a6a6a6;margin-bottom: 2px;background-color:#d9d9d9;color:#4D4D4D\" type=\"text\" value=\"";
    private final String GOTO_CRF_SUFIX = "&amp;viewFull=yes\"><img border=\"0\" title=\"Open CRF\" alt=\"GoToCRF\" src=\"../images/icon_DEcomplete.gif\" name=\"GOTO\"/></a>";
    private final String GOTO_CRF_DEFID = "&nbsp;&nbsp;<a onmouseup=\"javascript:setImage('Complete','../images/icon_DEcomplete.gif');\" href=\"../ViewSectionDataEntry?eventDefinitionCRFId=";

    @Override
    protected String getTableName() {
        return "codedItems";
    }

    @Override
    protected void configureColumns(TableFacade tableFacade, Locale locale) {
    	
        tableFacade.setColumnProperties("codedItem.verbatimTerm", "codedItem.dictionary",
                "version", "codedItem.subjectName", "codedItem.eventName", "codedItem.isCoded", "codedColumn", "actionColumn");
        
        Row row = tableFacade.getTable().getRow();
        
        configureColumn(row.getColumn("codedItem.verbatimTerm"), "Verbatim Term", new VerbatimTermCellEditor(), null);
        configureColumn(row.getColumn("codedItem.dictionary"), "Dictionary", null, null);
        configureColumn(row.getColumn("version"), "Version", new VersionCellEditor(), null, true, true);
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
            h.put("codedItem.dictionary", codedItem.getDictionary());
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

    private class VersionCellEditor implements CellEditor {
    	
        public Object getValue(Object item, String property, int rowcount) {
            String value = "";
            String version = "";
            if (!version.isEmpty()) {
                StringBuilder url = new StringBuilder();
                url.append(version);
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

					CODED_DIV_PREFIX = "<input style=\"border:1px solid #a6a6a6;margin-bottom: 2px;background-color:#d9d9d9;color:#4D4D4D\" disabled=\"true\" type=\"text\" value=\"";
				}
				
                url.append(CODED_DIV_PREFIX).append(inputTerm)
                        .append(CODED_DIV_MIDDLE)
                        .append(codedItem.getItemDataId())
                        .append(CODED_DIV_SUFIX);
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
            EventDefinitionCRFBean eventDefCRFBean = (EventDefinitionCRFBean) eventDefCRFDAO.findByStudyEventIdAndCRFVersionId(studyBean, codedItem.getEventCrfId(), codedItem.getCrfVersionId());
            String codedItemButton = codedItem.isCoded() ? "code_confirm.png" : "code_blue.png";

            if (codedItem != null) {
            	
                StringBuilder url = new StringBuilder();
                
                // Monitor is readonly
 				if (isLoggedInUserMonitor()) {
 					
 					AJAX_REQUEST_PREFIX = "<a itemId=\"";
 				}
 				
                url.append(AJAX_REQUEST_PREFIX)
                	.append(codedItem.getItemDataId())
                    .append(AJAX_REQUEST_MIDDLE)
                    .append(codedItemButton)
                    .append(AJAX_REQUEST_SUFIX)
                    .append(GOTO_CRF_DEFID)
                    .append(eventDefCRFBean.getStudyEventDefinitionId())
                    .append(GOTO_CRF_CRFID)
                    .append(codedItem.getEventCrfId())
                    .append(GOTO_CRF_EVENTID)
                    .append(codedItem.getEventCrfId()).append(GOTO_CRF_SUFIX);
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
                url.append(codedItemStatus);
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

    private StudyEventDefinitionBean getStudyEventDefinitionBean(int eventCrdId, int crfVersionId) {
        EventCRFBean eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(crfVersionId);
        StudyBean studyBean = (StudyBean) studyDAO.findByStudySubjectId(eventCRFBean.getStudySubjectId());
        EventDefinitionCRFBean eventDefCRFBean = (EventDefinitionCRFBean) eventDefCRFDAO.findByStudyEventIdAndCRFVersionId(studyBean, eventCrdId, crfVersionId);
        StudyEventDefinitionBean studyEventDefBean = studyEventDefDao.findByEventDefinitionCRFId(eventDefCRFBean.getId());
        return studyEventDefBean;
    }
}
