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

package org.akaza.openclinica.view.form;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class DefaultFormBuilder implements FormBuilder {
	public DefaultFormBuilder() {
		super();
		new ArrayList<Object>();
	}

	public DefaultFormBuilder(List<Object> displayItems) {
	}

	public String createMarkup() {
		Element root = createTable();
		Document doc = new Document(root);
		Element thead = this.createThead();
		Element th = this.createThCell("Col 1");
		Element th2 = this.createThCell("Col 2");
		thead.addContent(th);
		thead.addContent(th2);
		root.addContent(thead);
		Element tfoot = this.createTfoot();
		Element trFoot = this.createRow();
		Element tdFoot = this.createCell("Some content in the footer");
		tdFoot.setAttribute("colspan", "2");
		trFoot.addContent(tdFoot);
		tfoot.addContent(trFoot);
		root.addContent(tfoot);
		Element tbody = this.createTbody();
		Element tr1 = this.createRow();
		Element td1 = this.createCell("Some content");
		td1.setAttribute("valign", "top");
		tr1.addContent(td1);
		Element td2 = this.createCell("Some more content");
		td1.setAttribute("valign", "top");
		tr1.addContent(td2);
		tbody.addContent(tr1);
		root.addContent(tbody);

		XMLOutputter outp = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setOmitDeclaration(true);
		outp.setFormat(format);
		Writer writer = new StringWriter();
		try {
			outp.output(doc, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}

	public Element createTable() {
		Element root = new Element("table");
		root.setAttribute("border", "0");
		root.setAttribute("cellspacing", "0");
		root.setAttribute("cellpadding", "0");
		return root;
	}

	public Element createThead() {
		return new Element("thead");

	}

	public Element createThCell() {
		return new Element("th");
	}

	public Element createThCell(String content) {
		return new Element("th").addContent(content);
	}

	public Element createColGroup() {
		return new Element("colgroup");
	}

	public Element createTbody() {
		return new Element("tbody");
	}

	public Element createTfoot() {
		return new Element("tfoot");
	}

	public Element createRow() {
		return new Element("tr");
	}

	public Element createCell() {
		return new Element("td");
	}

	public Element createCell(String content) {
		return new Element("td").addContent(content);
	}

	public Element setClassNames(Element styledElement) {
		// Just returns the Element unstyled
		return styledElement;
	}

	public static void main(String[] args) {
		DefaultFormBuilder dfb = new DefaultFormBuilder();
		System.out.println(dfb.createMarkup());
	}
}
