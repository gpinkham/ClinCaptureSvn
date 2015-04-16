package com.clinovo.tag;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.CRFMaskingService;

/**
 * Custom tag for building the delete event CRF link.
 */
@SuppressWarnings("serial")
public class DeleteEventCRFLinkTag extends TagSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeleteEventCRFLinkTag.class);

	private Object object;
	private String subjectId = "";
	private String hspace = "";
	private String onClick = "";

	private void reset() {
		object = null;
		subjectId = "";
		hspace = "";
		onClick = "";
	}

	@Override
	public int doStartTag() throws JspException {
		hspace = hspace.isEmpty() ? "6" : hspace;
		String link = "<img src=\"images/bt_Transparent.gif\" border=\"0\" align=\"left\" hspace=\"" + hspace + "\">";
		WebApplicationContext appContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(((PageContext) pageContext.getAttribute(PageContext.PAGECONTEXT))
						.getServletContext());
		CRFMaskingService maskingService = (CRFMaskingService) appContext.getBean("crfMaskingService");
		MessageSource messageSource = (MessageSource) appContext.getBean("messageSource");
		Locale locale = LocaleResolver.getLocale((HttpServletRequest) pageContext.getRequest());
		UserAccountBean user = (UserAccountBean) pageContext.getSession().getAttribute("userBean");

		if (object != null && object instanceof DisplayEventCRFBean) {
			DisplayEventCRFBean decBean = (DisplayEventCRFBean) object;
			EventDefinitionCRFBean edcBean = decBean.getEventDefinitionCRF();
			if (!maskingService.isEventDefinitionCRFMasked(edcBean.getId(), user.getId(), edcBean.getStudyId())) {
				link = "<a href=\"DeleteEventCRF?action=confirm&ssId=" + subjectId + "&ecId="
						+ decBean.getEventCRF().getId() + "\" "
						+ "onMouseDown=\"javascript:setImage('bt_Delete1','images/bt_Delete_d.gif');\" "
						+ "onMouseUp=\"javascript:setImage('bt_Delete1','images/bt_Delete.gif');\" " + "onclick=\""
						+ onClick + "\">" + "<img name=\"bt_Delete1\" src=\"images/bt_Delete.gif\" border=\"0\" "
						+ "alt=\"" + messageSource.getMessage("delete", null, locale) + "\" " + "title=\""
						+ messageSource.getMessage("delete", null, locale) + "\" " + " align=\"left\" hspace=\""
						+ hspace + "\"/></a>";
			}
		}

		JspWriter writer = pageContext.getOut();
		try {
			writer.write(link);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		reset();
		return SKIP_BODY;
	}

	@Override
	public void release() {
		reset();
		super.release();
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getHspace() {
		return hspace;
	}

	public void setHspace(String hspace) {
		this.hspace = hspace;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}
}
