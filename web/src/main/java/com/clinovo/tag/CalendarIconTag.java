package com.clinovo.tag;

import com.clinovo.util.SessionUtil;
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
 * Tag that will generate icon for calendar.
 */
public class CalendarIconTag extends TagSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(CalendarTag.class);

	private String alt = "";
	private String title = "";
	private String dateFormat = "";
	private String linkName = "";
	private String linkId = "";
	private String onClickSelector;
	private String imageId = "";

	@Override
	public int doStartTag() throws JspException {

		if (onClickSelector != null) {
			WebApplicationContext webApplicationContext = WebApplicationContextUtils
					.getRequiredWebApplicationContext(((PageContext) pageContext
							.getAttribute("javax.servlet.jsp.jspPageContext")).getServletContext());
			MessageSource messageSource = (MessageSource) webApplicationContext.getBean("messageSource");
			Locale locale = SessionUtil.getLocale((HttpServletRequest) pageContext.getRequest());

			alt = alt.isEmpty() ? messageSource.getMessage("show_calendar", null, locale) : alt;
			title = title.isEmpty() ? messageSource.getMessage("show_calendar", null, locale) : title;
			dateFormat = dateFormat.isEmpty() ? messageSource.getMessage("date_format_calender", null, locale) : dateFormat;

			String html = "";

			html = html.concat("<a href='#!' onclick=\"$(" + onClickSelector + ")")
					.concat(".datepicker({ dateFormat: '" + dateFormat + "', ")
					.concat("showOn: 'none', changeYear: true,changeMonth : true,\n")
					.concat("onChangeMonthYear: function (year, month, inst) {\n"
							+ "\t var date = $(this).val();\n"
							+ "\t if ($.trim(date) != '') {\n"
							+ "\t\t var newDate = month + '/' + inst.currentDay + '/' + year;\n"
							+ "\t\t $(this).val($.datepicker.formatDate('" + dateFormat + "', new Date(newDate)));\n"
							+ "\t}\n}")
					.concat(",yearRange: 'c-20:c+10'}).datepicker('show');\" ");
			if (!linkId.isEmpty()) {
				html = html.concat("id='" + linkId + "' ");
			}
			if (!linkName.isEmpty()) {
				html = html.concat("name='" + linkName + "'");
			}
			html = html.concat(">\n \t<img src='images/bt_Calendar.gif' alt='" + alt + "' ")
					.concat("title='" + title + "' border='0'");
			if (!imageId.isEmpty()) {
				html = html.concat("id='" + imageId + "'");
			}
			html = html.concat("/>\n" + "</a>");

			JspWriter writer = pageContext.getOut();
			try {
				writer.write(html);
			} catch (Exception ex) {
				LOGGER.error("Error has occurred.", ex);
			}
		}
		return SKIP_BODY;
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

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}

	public String getOnClickSelector() {
		return onClickSelector;
	}

	public void setOnClickSelector(String onClickSelector) {
		this.onClickSelector = onClickSelector;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
}
