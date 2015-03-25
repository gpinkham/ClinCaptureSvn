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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EscapeTag.
 */
@SuppressWarnings("serial")
public class EscapeTag extends TagSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(EscapeTag.class);

	public static final String COMMA = ",";

	private String tagNames;

	private String content;

	@Override
	public int doStartTag() throws JspException {
		try {
			String newContent = content;
			for (String tagName : tagNames.split(COMMA)) {
				tagName = tagName.trim();
				newContent = newContent.replaceAll("<".concat(tagName).concat(".*/>"),
						"<!-- ".concat(tagName).concat(" -->")).replaceAll(
						"<".concat(tagName).concat(".*>[\\s|.|\\S]*</".concat(tagName).concat(">")),
						"<!-- ".concat(tagName).concat(" -->"));
			}
			pageContext.getOut().write(newContent);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return SKIP_BODY;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTagNames() {
		return tagNames;
	}

	public void setTagNames(String tagNames) {
		this.tagNames = tagNames;
	}
}
