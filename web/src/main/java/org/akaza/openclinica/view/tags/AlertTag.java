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

package org.akaza.openclinica.view.tags;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

@SuppressWarnings({"rawtypes", "unchecked"})
public class AlertTag extends SimpleTagSupport {
	private String message;

	@Override
	public void doTag() throws JspException, IOException {
		JspContext context = getJspContext();
		JspWriter tagWriter = context.getOut();
		StringBuilder builder = new StringBuilder("");

		List<String> messages = (ArrayList) context.findAttribute("pageMessages");
		if (messages != null) {
			for (String message : messages) {
				builder.append(message);
				builder.append("<br />");
			}

		}
		tagWriter.println(builder.toString());

	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String messages) {
		this.message = messages;
	}
}
