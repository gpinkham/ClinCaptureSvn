package com.clinovo.tag;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.CRFMaskingService;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Locale;

/**
 * Tag that will generate chose CRF version icon.
 */
@SuppressWarnings("serial")
public class ChoseCRFVersionIconTag extends TagSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChoseCRFVersionIconTag.class);

	private DisplayEventCRFBean dec;
	private StudySubjectBean subjectBean;
	private String hspace = "";
	private String onClick = "";

	private void reset() {
		dec = null;
		subjectBean = null;
		hspace = "";
		onClick = "";
	}

	@Override
	public int doStartTag() throws JspException {
		WebApplicationContext appContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(((PageContext) pageContext.getAttribute(PageContext.PAGECONTEXT))
						.getServletContext());
		CRFMaskingService crfMaskingService = (CRFMaskingService) appContext.getBean("crfMaskingService");
		MessageSource messageSource = (MessageSource) appContext.getBean("messageSource");
		Locale locale = LocaleResolver.getLocale((HttpServletRequest) pageContext.getRequest());
		UserAccountBean user = (UserAccountBean) pageContext.getSession().getAttribute("userBean");
		EventDefinitionCRFBean edcBean;
		hspace = hspace.isEmpty() ? "6" : hspace;
		String link = "<img src=\"images/bt_Transparent.gif\" border=\"0\" align=\"left\" hspace=\"" + hspace + "\">";

		if (dec != null && subjectBean != null) {
			edcBean = dec.getEventDefinitionCRF();
			Status eventCRFStatus = dec.getEventCRF().getStatus();
			if (!crfMaskingService.isEventDefinitionCRFMasked(edcBean.getId(), user.getId(), edcBean.getStudyId())
					&& !eventCRFStatus.isPartialDataEntry()) {
				link = "<a href=\"pages/managestudy/chooseCRFVersion?crfId="
						+ dec.getEventCRF().getCrf().getId()
						+ "&crfName="
						+ dec.getEventCRF().getCrf().getName()
						+ "&crfversionId="
						+ dec.getEventCRF().getCrfVersion().getId()
						+ "&crfVersionName="
						+ dec.getEventCRF().getCrfVersion().getName()
						+ "&studySubjectLabel="
						+ subjectBean.getLabel()
						+ "&studySubjectId="
						+ subjectBean.getId()
						+ "&eventCRFId="
						+ dec.getEventCRF().getId()
						+ "&eventDefinitionCRFId="
						+ edcBean.getId() + "\" "
						+ "onMouseDown=\"javascript:setImage('bt_Reassign','images/bt_Reassign_d.gif');\" "
						+ "onMouseUp=\"javascript:setImage('bt_Reassign','images/bt_Reassign.gif');\""
						+ "onclick=\"" + onClick + "\">"
						+ "<img name=\"Reassign\" src=\"images/bt_Reassign.gif\" border=\"0\" "
						+ "alt=\"" + messageSource.getMessage("reassign_crf_version", null, locale) + "\" "
						+ "title=\"" + messageSource.getMessage("reassign_crf_version", null, locale) + "\" "
						+ "align=\"left\" hspace=\"" + hspace + "\"></a>";
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

	public DisplayEventCRFBean getDec() {
		return dec;
	}

	public void setDec(DisplayEventCRFBean dec) {
		this.dec = dec;
	}

	public StudySubjectBean getSubjectBean() {
		return subjectBean;
	}

	public void setSubjectBean(StudySubjectBean subjectBean) {
		this.subjectBean = subjectBean;
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
