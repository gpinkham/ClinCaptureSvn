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
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package com.clinovo.tag;

import com.clinovo.dao.SystemDAO;
import com.clinovo.util.StudyParameterPriorityUtil;
import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import javax.sql.DataSource;

/**
 * Custom tag for building the data entry link.
 */
@SuppressWarnings({ "serial" })
public class DataEntryLinkTag extends TagSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataEntryLinkTag.class);

	private int border;
	private Object object;
	private String alt = "";
	private String title = "";
	private int rowCount = 1;
	private String imgHSpace = "4";
	private String actionQuery = "";
	private String imgAlign = "left";
	private String imgPostfix = "_d";
	private String actionQueryTail = "";
	private String imgExtension = ".gif";
	private String imgSrcPrefix = "images/";
	private String imgPrefix = "bt_EnterData";
	private String onClickFunction = "checkCRFLocked";

	@Override
	public int doStartTag() throws JspException {
		if (object != null) {
			WebApplicationContext webApplicationContext = WebApplicationContextUtils
					.getRequiredWebApplicationContext(((PageContext) pageContext
							.getAttribute("javax.servlet.jsp.jspPageContext")).getServletContext());
			SystemDAO systemDAO = (SystemDAO) webApplicationContext.getBean("systemDAO");
			DataSource dataSource = (DataSource) webApplicationContext.getBean("dataSource");
			MessageSource messageSource = (MessageSource) webApplicationContext.getBean("messageSource");
			StudyBean currentStudy = (StudyBean) pageContext.getSession().getAttribute("study");
			int currentStudyId = currentStudy.getParentStudyId() > 0 ? currentStudy.getParentStudyId() : currentStudy
					.getId();
			boolean allowCrfEvaluation = StudyParameterPriorityUtil.isParameterEnabled("allowCrfEvaluation",
					currentStudyId, systemDAO, new StudyParameterValueDAO(dataSource), new StudyDAO(dataSource));
			String link = "<img src=\"".concat(imgSrcPrefix)
					.concat("bt_Transparent.gif\" class=\"crfBlankCellImg\" border=\"")
					.concat(Integer.toString(border)).concat("\" align=\"").concat(imgAlign).concat("\" hspace=\"")
					.concat(imgHSpace).concat("\"/>");
			String dynamicAlt = !alt.isEmpty() ? alt : "";
			String dynamicActionQuery = !actionQuery.isEmpty() ? actionQuery : "";
			EventCRFBean eventCrfBean = null;
			if (object instanceof DisplayEventCRFBean) {
				DisplayEventCRFBean dec = (DisplayEventCRFBean) object;
				if (dynamicAlt.isEmpty()) {
					if (dec.isContinueInitialDataEntryPermitted()) {
						eventCrfBean = dec.getEventCRF();
						dynamicAlt = messageSource.getMessage("continue_entering_data", null, pageContext.getRequest()
								.getLocale());
						if (dynamicActionQuery.isEmpty()) {
							dynamicActionQuery = "'InitialDataEntry".concat(actionQueryTail).concat("'");
						}
					} else if (dec.isStartDoubleDataEntryPermitted() || dec.isContinueDoubleDataEntryPermitted()) {
						eventCrfBean = dec.getEventCRF();
						dynamicAlt = messageSource.getMessage(eventCrfBean.getValidatorId() == 0 ? (allowCrfEvaluation
								&& dec.getEventDefinitionCRF().isEvaluatedCRF()
								&& !dec.getEventDefinitionCRF().isDoubleEntry() ? "begin_crf_evaluation"
								: "begin_double_data_entry") : "continue_entering_data", null, pageContext.getRequest()
								.getLocale());
						if (dynamicActionQuery.isEmpty()) {
							dynamicActionQuery = "'DoubleDataEntry".concat(actionQueryTail).concat("'");
						}
					} else if (dec.isPerformAdministrativeEditingPermitted()) {
						eventCrfBean = dec.getEventCRF();
						dynamicAlt = messageSource.getMessage("administrative_editing", null, pageContext
								.getRequest().getLocale());
						if (dynamicActionQuery.isEmpty()) {
							dynamicActionQuery = "'AdministrativeEditing".concat(actionQueryTail).concat("'");
						}
					}
				}
			} else if (object instanceof DisplayEventDefinitionCRFBean) {
				DisplayEventDefinitionCRFBean dedc = (DisplayEventDefinitionCRFBean) object;
				eventCrfBean = dedc.getEventCRF();
				if (dynamicAlt.isEmpty()) {
					dynamicAlt = messageSource.getMessage("enter_data", null, pageContext.getRequest().getLocale());
				}
				if (dynamicActionQuery.isEmpty()) {
					dynamicActionQuery = "document.startForm".concat(actionQueryTail);
				}
			}
			String dynamicTitle = !title.isEmpty() ? title : dynamicAlt;
			if (eventCrfBean != null) {
				link = "<a href=\"#\" onclick=\"".concat(onClickFunction).concat("('")
						.concat(Integer.toString(eventCrfBean.getId())).concat("',").concat(dynamicActionQuery)
						.concat(");\"").concat("onMouseDown=\"setImage('").concat(imgPrefix)
						.concat(Integer.toString(rowCount)).concat("','").concat(imgSrcPrefix).concat(imgPrefix)
						.concat(imgPostfix).concat(imgExtension).concat("');\"").concat("onMouseUp=\"setImage('")
						.concat(imgPrefix).concat(Integer.toString(rowCount)).concat("','").concat(imgSrcPrefix)
						.concat(imgPrefix).concat(imgExtension).concat("');\">").concat("<img name=\"")
						.concat(imgPrefix).concat(Integer.toString(rowCount)).concat("\" src=\"").concat(imgSrcPrefix)
						.concat(imgPrefix).concat(imgExtension).concat("\" ").concat("border=\"")
						.concat(Integer.toString(border)).concat("\" alt=\"").concat(dynamicAlt).concat("\" title=\"")
						.concat(dynamicTitle).concat("\" align=\"").concat(imgAlign).concat("\" hspace=\"")
						.concat(imgHSpace).concat("\"/></a>");
			}
			JspWriter writer = pageContext.getOut();
			try {
				writer.write(link);
			} catch (Exception ex) {
				LOGGER.error("Error has occurred.", ex);
			}
		}

		return SKIP_BODY;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public String getActionQuery() {
		return actionQuery;
	}

	public void setActionQuery(String actionQuery) {
		this.actionQuery = actionQuery;
	}

	public String getImgAlign() {
		return imgAlign;
	}

	public void setImgAlign(String imgAlign) {
		this.imgAlign = imgAlign;
	}

	public String getImgHSpace() {
		return imgHSpace;
	}

	public void setImgHSpace(String imgHSpace) {
		this.imgHSpace = imgHSpace;
	}

	public String getImgPrefix() {
		return imgPrefix;
	}

	public void setImgPrefix(String imgPrefix) {
		this.imgPrefix = imgPrefix;
	}

	public String getImgExtension() {
		return imgExtension;
	}

	public void setImgExtension(String imgExtension) {
		this.imgExtension = imgExtension;
	}

	public String getImgPostfix() {
		return imgPostfix;
	}

	public void setImgPostfix(String imgPostfix) {
		this.imgPostfix = imgPostfix;
	}

	public String getOnClickFunction() {
		return onClickFunction;
	}

	public void setOnClickFunction(String onClickFunction) {
		this.onClickFunction = onClickFunction;
	}

	public String getImgSrcPrefix() {
		return imgSrcPrefix;
	}

	public void setImgSrcPrefix(String imgSrcPrefix) {
		this.imgSrcPrefix = imgSrcPrefix;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getBorder() {
		return border;
	}

	public void setBorder(int border) {
		this.border = border;
	}

	public String getActionQueryTail() {
		return actionQueryTail;
	}

	public void setActionQueryTail(String actionQueryTail) {
		this.actionQueryTail = actionQueryTail;
	}
}
