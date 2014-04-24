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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.util;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.hyperLink;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.template;

import java.awt.Color;
import java.util.Locale;

import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.HyperLinkBuilder;
import net.sf.dynamicreports.report.builder.ReportTemplateBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.style.BorderBuilder;
import net.sf.dynamicreports.report.builder.style.ReportStyleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.definition.ReportParameters;

public class DRTemplates {
	public static final StyleBuilder rootStyle;
	public static final StyleBuilder boldStyle;
	public static final StyleBuilder italicStyle;
	public static final StyleBuilder boldCenteredStyle;
	public static final StyleBuilder bold18CenteredStyle;
	public static final StyleBuilder bold22CenteredStyle;
	public static final StyleBuilder columnTitleStyle;
	public static final StyleBuilder groupColumnStyle;
	public static final StyleBuilder groupColumnConditionalStyle; 
	public static final ReportTemplateBuilder reportTemplate;
	public static final ComponentBuilder<?, ?> footerComponent;
	
	private static final String imagePath = "images/Logo.gif";

	static {
		rootStyle = stl.style().setPadding(2);
		boldStyle = stl.style(rootStyle).bold();
		italicStyle = stl.style(rootStyle).italic();
		boldCenteredStyle = stl.style(boldStyle).setAlignment(HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
		bold18CenteredStyle = stl.style(boldCenteredStyle).setFontSize(18);
		bold22CenteredStyle = stl.style(boldCenteredStyle).setFontSize(22);
		columnTitleStyle = stl.style(boldCenteredStyle).setBorder(stl.pen1Point()).setBackgroundColor(Color.LIGHT_GRAY);
		groupColumnStyle = stl.style(boldStyle).italic().setFontSize(12).setBackgroundColor(new Color(206, 206, 206));
		groupColumnConditionalStyle = stl.style().conditionalStyles(stl.conditionalStyle(new GroupConditionExpression())
				.boldItalic().setFontSize(12).setBackgroundColor(new Color(206, 206, 206)).setPadding(3));
		
		reportTemplate = template().setLocale(Locale.ENGLISH).highlightDetailEvenRows().crosstabHighlightEvenRows();
		footerComponent = cmp.pageXofY().setStyle(stl.style(boldCenteredStyle).setTopBorder(stl.pen1Point()));
	}

	/**
	 * Creates custom component which is possible to add to report band component
	 */
	public static HorizontalListBuilder getTitleComponent(String label, ComponentBuilder<?, ?> dynamicReportsComponent) {
		return cmp
				.horizontalList()
				.add(dynamicReportsComponent,
						cmp.text(label).setStyle(DRTemplates.bold18CenteredStyle)
								.setHorizontalAlignment(HorizontalAlignment.RIGHT)).newRow().add(cmp.line()).newRow()
				.add(cmp.verticalGap(20));
	}

	public static ComponentBuilder<?, ?> getGapComponent() {
		// to separate header and data tables
		return cmp.horizontalList().newRow().add(cmp.verticalGap(10));
	}

	public static ReportStyleBuilder getHeaderColumnStyle() {
		BorderBuilder border = stl.border().setBottomPen(stl.penThin()).setTopPen(stl.penThin())
				.setLeftPen(stl.penThin()).setRightPen(stl.penThin());
		ReportStyleBuilder columnStyle = stl.style().setBorder(border).setPadding(3).setLeftIndent(7).bold();

		return columnStyle;
	}

	public static ComponentBuilder<?, ?> getDynamicReportsComponent(String urlPath, String sysPath) {
		HyperLinkBuilder link = hyperLink(urlPath);
		ComponentBuilder<?, ?> dynamicReportsComponent = cmp.horizontalList(
				cmp.image(sysPath + "/" + imagePath).setFixedDimension(100, 50),
				cmp.verticalList(cmp.text("CRF Report").setStyle(bold22CenteredStyle.setLeftPadding(20))
						.setHorizontalAlignment(HorizontalAlignment.LEFT), cmp.text(urlPath).setStyle(italicStyle)
						.setHyperLink(link))).setFixedWidth(300);
		return dynamicReportsComponent;
	}
	
	private static class GroupConditionExpression extends AbstractSimpleExpression<Boolean> {
		private static final long serialVersionUID = 1L;
		
		public Boolean evaluate(ReportParameters reportParameters) {
			String groupColumnHeader = reportParameters.getValue("group_column");
			
			return !"".equals(groupColumnHeader.trim());
		}
	}
}
