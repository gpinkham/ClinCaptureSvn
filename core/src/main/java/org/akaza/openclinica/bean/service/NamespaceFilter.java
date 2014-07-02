package org.akaza.openclinica.bean.service;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.XMLFilterImpl;

public class NamespaceFilter extends XMLFilterImpl {

	private String usedNamespaceUri;
	private boolean addNamespace;

	private boolean addedNamespace = false;

	public NamespaceFilter(String namespaceUri, boolean addNamespace) {
		super();

		if (addNamespace) {
			this.usedNamespaceUri = namespaceUri;
		} else {
			this.usedNamespaceUri = "";
			this.addNamespace = addNamespace;
		}
	}

	@Override
	public void startDocument() throws SAXException {

		super.startDocument();
		if (addNamespace) {
			startControlledPrefixMapping();
		}
	}

	@Override
	public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException {

		super.startElement(this.usedNamespaceUri, arg1, arg2, arg3);
	}

	@Override
	public void endElement(String arg0, String arg1, String arg2) throws SAXException {

		super.endElement(this.usedNamespaceUri, arg1, arg2);
	}

	@Override
	public void startPrefixMapping(String prefix, String url) throws SAXException {

		if (addNamespace) {
			this.startControlledPrefixMapping();
		}
	}

	private void startControlledPrefixMapping() throws SAXException {

		if (this.addNamespace && !this.addedNamespace) {

			super.startPrefixMapping("", this.usedNamespaceUri);
			this.addedNamespace = true;
		}
	}
}
