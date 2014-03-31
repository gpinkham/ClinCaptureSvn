/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOâ€™S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "widget")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "widget_id_seq") })
public class Widget extends AbstractMutableDomainObject {

	private String widgetName = "";
	private String description = "";
	private List<WidgetsLayout> widgetsLayout;
	private String haveAccess = "";
	private String displayAsDefault = "";
	private boolean studyMetrics = false;
	private boolean siteMetrics = false;
	private boolean twoColumnWidget = false;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	@JoinColumn(name = "widget_id", referencedColumnName = "id", nullable = false)
	public List<WidgetsLayout> getWidgetsLayout() {
		return widgetsLayout;
	}

	public void setWidgetsLayout(List<WidgetsLayout> widgetsLayout) {
		this.widgetsLayout = widgetsLayout;
	}

	public String getWidgetName() {
		return widgetName;
	}

	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHaveAccess() {
		return haveAccess;
	}

	public void setHaveAccess(String haveAccess) {
		this.haveAccess = haveAccess;
	}

	public String getDisplayAsDefault() {
		return displayAsDefault;
	}

	public void setDisplayAsDefault(String displayAsDefault) {
		this.displayAsDefault = displayAsDefault;
	}

	public boolean isStudyMetrics() {
		return studyMetrics;
	}

	public void setStudyMetrics(boolean studyMetrics) {
		this.studyMetrics = studyMetrics;
	}

	public boolean isSiteMetrics() {
		return siteMetrics;
	}

	public void setSiteMetrics(boolean siteMetrics) {
		this.siteMetrics = siteMetrics;
	}

	public boolean isTwoColumnWidget() {
		return twoColumnWidget;
	}

	public void setTwoColumnWidget(boolean twoColumnWidget) {
		this.twoColumnWidget = twoColumnWidget;
	}
}
