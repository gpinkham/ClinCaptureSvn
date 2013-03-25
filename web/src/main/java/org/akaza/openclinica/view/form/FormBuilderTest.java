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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.view.form;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

@SuppressWarnings({ "rawtypes" })
public class FormBuilderTest {

	public void setFormContents(Map contentsMap) {

	}

	public String createTable() {
		Element root = new Element("table");
		root.setAttribute("border", "0");
		Document doc = new Document(root);
		Element thead = new Element("thead");
		Element th = new Element("th");
		th.addContent("A header");
		th.setAttribute("class", "aka_header_border");
		thead.addContent(th);
		Element th2 = new Element("th");
		th2.addContent("Another header");
		th2.setAttribute("class", "aka_header_border");
		thead.addContent(th2);
		root.addContent(thead);
		Element tr1 = new Element("tr");
		Element td1 = new Element("td");
		td1.setAttribute("valign", "top");
		td1.setAttribute("class", "cellBorders");
		td1.setText("cell contents");
		tr1.addContent(td1);
		root.addContent(tr1);
		XMLOutputter outp = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setOmitDeclaration(true);
		outp.setFormat(format);
		Writer writer = new StringWriter();
		try {
			outp.output(doc, writer);
		} catch (IOException e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		}
		return writer.toString();
	}

	public static void main(String[] args) {
		FormBuilderTest builder = new FormBuilderTest();
		System.out.println(builder.createTable());
	}
}
