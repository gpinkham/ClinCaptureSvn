package com.clinovo.tag;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.CRFMaskingService;

/**
 * Custom tag for building view data entry link.
 */
@SuppressWarnings("serial")
public class ViewDataEntryLinkTag extends TagSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(ViewDataEntryLinkTag.class);

	private Object object;
	private String onMouseUp = "";
	private String onMouseDown = "";
	private String name = "";
	private String image = "";
	private String border;
	private String align = "";
	private String hspace = "";
	private String onClick = "";
	private String queryTail = "";

	private void reset() {
		object = null;
		onMouseUp = "";
		onMouseDown = "";
		name = "";
		image = "";
		border = "";
		align = "";
		hspace = "";
		onClick = "";
		queryTail = "";
	}

	@Override
	public int doStartTag() throws JspException {
		if (object != null) {
			WebApplicationContext appContext = WebApplicationContextUtils
					.getRequiredWebApplicationContext(((PageContext) pageContext.getAttribute(PageContext.PAGECONTEXT))
							.getServletContext());
			CRFMaskingService crfMaskingService = (CRFMaskingService) appContext.getBean("crfMaskingService");
			MessageSource messageSource = (MessageSource) appContext.getBean("messageSource");
			Locale locale = LocaleResolver.getLocale((HttpServletRequest) pageContext.getRequest());
			UserAccountBean user = (UserAccountBean) pageContext.getSession().getAttribute("userBean");
			String link = "";
			EventCRFBean eventCRF = null;
			EventDefinitionCRFBean edcBean = null;

			if (object instanceof DisplayEventCRFBean) {
				DisplayEventCRFBean dec = (DisplayEventCRFBean) object;
				eventCRF = dec.getEventCRF();
				edcBean = dec.getEventDefinitionCRF();
			} else if (object instanceof DisplayEventDefinitionCRFBean) {
				DisplayEventDefinitionCRFBean dedc = (DisplayEventDefinitionCRFBean) object;
				eventCRF = dedc.getEventCRF();
				edcBean = dedc.getEdc();
			}

			if (eventCRF != null && edcBean != null) {
				hspace = hspace.isEmpty() ? "4" : hspace;

				if (!crfMaskingService.isEventDefinitionCRFMasked(edcBean.getId(), user.getId(), edcBean.getStudyId())) {
					if (!onClick.contains("viewCrfByVersion")) {
						link = link.concat("<a href='ViewSectionDataEntry?eventCRFId=" + eventCRF.getId())
								.concat("&eventDefinitionCRFId=" + edcBean.getId())
								.concat("&tabId=1" + queryTail + "' ");
					} else {
						link = link.concat("<a href='#' ");
					}
					align = align.isEmpty() ? "left" : align;
					name = name.isEmpty() ? "bt_View1" : name;
					onMouseDown = onMouseDown.isEmpty()
							? "setImage(\"bt_View1\",\"images/bt_View_d.gif\");"
							: onMouseDown;
					onMouseUp = onMouseUp.isEmpty() ? "setImage('bt_View1','images/bt_View.gif');" : onMouseUp;

					link = link.concat("onclick=\"" + onClick.replace("\"", "'") + "\"")
							.concat("onmousedown='" + onMouseDown + "' ").concat("onmouseup=\"" + onMouseUp + "\" >")
							.concat("<img name='" + name + "' ")
							.concat("src='images/bt_View.gif' border='" + border + "'")
							.concat("alt='" + messageSource.getMessage("view", null, locale) + "' ")
							.concat("title='" + messageSource.getMessage("view", null, locale) + "' ")
							.concat("align='" + align + "' " + "hspace='" + hspace + "'/>").concat("</a>");
				} else {
					link = link.concat("<img src=\"images/bt_Transparent.gif\" border=\"0\" align=\"left\" hspace=\""
							+ hspace + "\">");
				}
			}
			JspWriter writer = pageContext.getOut();
			try {
				writer.write(link);
			} catch (Exception ex) {
				LOGGER.error("Error has occurred.", ex);
			}

		}
		reset();
		return SKIP_BODY;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getOnMouseUp() {
		return onMouseUp;
	}

	public void setOnMouseUp(String onMouseUp) {
		this.onMouseUp = onMouseUp;
	}

	public String getOnMouseDown() {
		return onMouseDown;
	}

	public void setOnMouseDown(String onMouseDown) {
		this.onMouseDown = onMouseDown;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getBorder() {
		return border;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
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

	public String getQueryTail() {
		return queryTail;
	}

	public void setQueryTail(String queryTail) {
		this.queryTail = queryTail;
	}

	@Override
	public void release() {
		reset();
		super.release();
	}
}
