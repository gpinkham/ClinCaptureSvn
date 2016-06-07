/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
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
import net.sf.dynamicreports.report.constant.LineStyle;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.definition.ReportParameters;
import org.akaza.openclinica.dao.core.CoreResources;

/**
 * 
 * DynamicReports templates.
 * 
 */
@SuppressWarnings("deprecation")
public final class DRTemplates {
	public static final StyleBuilder ROOT_STYLE;
	public static final StyleBuilder BOLD_STYLE;
	public static final StyleBuilder ITALIC_STYLE;
	public static final StyleBuilder BOLD_CENTERED_STYLE;
	public static final StyleBuilder BOLD_14_CENTERED_STYLE;
	public static final StyleBuilder BOLD_18_CENTERED_STYLE;
	public static final StyleBuilder BOLD_22_CENTERED_STYLE;
	public static final StyleBuilder COLUMN_TITLE_STYLE;
	public static final StyleBuilder GROUP_COLUMN_STYLE;
	public static final StyleBuilder GROUP_COLUMN_CONDITIONAL_STYLE;
	public static final ReportTemplateBuilder REPORT_TEMPLATE;
	public static final ComponentBuilder<?, ?> FOOTER_COMPONENT;

	private static final int THREE = 3;
	private static final int SEVEN = 7;
	private static final int TEN = 10;
	private static final int TWELVE = 12;
	private static final int FOURTEEN = 14;
	private static final int EIGHTEEN = 18;
	private static final int TWENTY = 20;
	private static final int TWENTY_TWO = 22;
	private static final int FIFTY = 50;
	private static final int ONE_HUNDRED = 100;
	private static final int TWO_HUNDRED_SIX = 206;
	private static final int THREE_HUNDRED = 300;

	static {
		ROOT_STYLE = stl.style().setPadding(2);
		BOLD_STYLE = stl.style(ROOT_STYLE).bold();
		ITALIC_STYLE = stl.style(ROOT_STYLE).italic();
		BOLD_CENTERED_STYLE = stl.style(BOLD_STYLE).setAlignment(HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
		BOLD_14_CENTERED_STYLE = stl.style(BOLD_CENTERED_STYLE).setFontSize(FOURTEEN);
		BOLD_18_CENTERED_STYLE = stl.style(BOLD_CENTERED_STYLE).setFontSize(EIGHTEEN);
		BOLD_22_CENTERED_STYLE = stl.style(BOLD_CENTERED_STYLE).setFontSize(TWENTY_TWO);
		COLUMN_TITLE_STYLE = stl.style(BOLD_CENTERED_STYLE).setBorder(stl.pen1Point())
				.setBackgroundColor(Color.LIGHT_GRAY);
		GROUP_COLUMN_STYLE = stl.style(BOLD_STYLE).italic().setFontSize(TWELVE)
				.setBackgroundColor(new Color(TWO_HUNDRED_SIX, TWO_HUNDRED_SIX, TWO_HUNDRED_SIX));
		GROUP_COLUMN_CONDITIONAL_STYLE = stl.style().conditionalStyles(
				stl.conditionalStyle(new GroupConditionExpression()).boldItalic().setFontSize(TWELVE)
						.setBackgroundColor(new Color(TWO_HUNDRED_SIX, TWO_HUNDRED_SIX, TWO_HUNDRED_SIX))
						.setPadding(THREE));
		REPORT_TEMPLATE = template().setLocale(Locale.ENGLISH).highlightDetailEvenRows().crosstabHighlightEvenRows();
		FOOTER_COMPONENT = cmp.pageXofY().setStyle(stl.style(BOLD_CENTERED_STYLE).setTopBorder(stl.pen1Point()));
	}

	private DRTemplates() {
	}

	/**
	 * Creates custom component which is possible to add to report band component.
	 * 
	 * @param label
	 *            String
	 * @param dynamicReportsComponent
	 *            ComponentBuilder
	 * @return HorizontalListBuilder
	 */
	public static HorizontalListBuilder getTitleComponent(String label, ComponentBuilder<?, ?> dynamicReportsComponent) {
		return cmp
				.horizontalList()
				.add(dynamicReportsComponent,
						cmp.text(label).setStyle(DRTemplates.BOLD_18_CENTERED_STYLE)
								.setHorizontalAlignment(HorizontalAlignment.RIGHT)).newRow().add(cmp.line()).newRow()
				.add(cmp.verticalGap(TWENTY));
	}

	/**
	 * Creates custom component which is possible to add to any report band component.
	 * 
	 * @param label
	 *            String
	 * @return ComponentBuilder
	 */
	public static ComponentBuilder<?, ?> createTitleComponent(String label) {
		return cmp.horizontalList().add(
				cmp.text(label).setStyle(BOLD_14_CENTERED_STYLE).setHorizontalAlignment(HorizontalAlignment.CENTER));
	}

	/**
	 * Gets gap component.
	 * 
	 * @return ComponentBuilder
	 */
	public static ComponentBuilder<?, ?> getGapComponent() {
		return cmp.horizontalList().newRow().add(cmp.verticalGap(TEN));
	}

	/**
	 * Gets header column style.
	 * 
	 * @return ReportStyleBuilder
	 */
	public static ReportStyleBuilder getHeaderColumnStyle() {
		BorderBuilder border = stl.border().setBottomPen(stl.penThin()).setTopPen(stl.penThin())
				.setLeftPen(stl.penThin()).setRightPen(stl.penThin());
		ReportStyleBuilder columnStyle = stl.style().setBorder(border).setPadding(THREE).setLeftIndent(SEVEN).bold();
		return columnStyle;
	}

	/**
	 * Gets header column style with specific left and right border width.
	 * 
	 * @param leftBorder
	 *            left border width
	 * @param rightBorder
	 *            right border width
	 * @return ReportStyleBuilder
	 */
	public static ReportStyleBuilder getHeaderColumnStyle(float leftBorder, float rightBorder) {
		BorderBuilder border = stl.border().setBottomPen(stl.penThin()).setTopPen(stl.penThin())
				.setLeftPen(stl.pen(leftBorder, LineStyle.SOLID)).setRightPen(stl.pen(rightBorder, LineStyle.SOLID));
		ReportStyleBuilder columnStyle = stl.style().setBorder(border).setPadding(THREE).setLeftIndent(SEVEN).bold();
		return columnStyle;
	}

	/**
	 * Gets normal column style.
	 * 
	 * @return ReportStyleBuilder
	 */
	public static ReportStyleBuilder getNormalColumnStyle() {
		BorderBuilder border = stl.border().setBottomPen(stl.penThin()).setTopPen(stl.penThin())
				.setLeftPen(stl.penThin()).setRightPen(stl.penThin());
		ReportStyleBuilder columnStyle = stl.style().setBorder(border).setPadding(THREE).setLeftIndent(SEVEN);
		return columnStyle;
	}

	/**
	 * Gets normal column style with specific left and right border width.
	 * 
	 * @param leftBorder
	 *            left border width
	 * @param rightBorder
	 *            right border width
	 * @return ReportStyleBuilder
	 */
	public static ReportStyleBuilder getNormalColumnStyle(float leftBorder, float rightBorder) {
		BorderBuilder border = stl.border().setBottomPen(stl.penThin()).setTopPen(stl.penThin())
				.setLeftPen(stl.pen(leftBorder, LineStyle.SOLID)).setRightPen(stl.pen(rightBorder, LineStyle.SOLID));
		ReportStyleBuilder columnStyle = stl.style().setBorder(border).setPadding(THREE);
		return columnStyle;
	}

	/**
	 * Gets header column style for Question to Site in DCF.
	 * 
	 * @return ReportStyleBuilder
	 */
	public static ReportStyleBuilder getQuestionToSiteHeaderStyle() {
		BorderBuilder border = stl.border().setBottomPen(stl.pen(Float.valueOf(0), LineStyle.SOLID))
				.setTopPen(stl.penThin()).setLeftPen(stl.penThin()).setRightPen(stl.penThin());
		ReportStyleBuilder columnStyle = stl.style().setBorder(border).setPadding(THREE).setLeftIndent(SEVEN).bold();
		return columnStyle;
	}

	/**
	 * Gets normal column style for Question to Site in DCF.
	 * 
	 * @return ReportStyleBuilder
	 */
	public static ReportStyleBuilder getQuestionToSiteNormalStyle() {
		BorderBuilder border = stl.border().setBottomPen(stl.penThin())
				.setTopPen(stl.pen(Float.valueOf(0), LineStyle.SOLID)).setLeftPen(stl.penThin())
				.setRightPen(stl.penThin());
		ReportStyleBuilder columnStyle = stl.style().setBorder(border).setPadding(THREE).setLeftIndent(SEVEN);
		return columnStyle;
	}

	/**
	 * Gets DynamicReports component.
	 * 
	 * @param urlPath
	 *            URL path of CC instance
	 * @param sysPath
	 *            File system path to repository
	 * @return ComponentBuilder
	 */
	public static ComponentBuilder<?, ?> getDynamicReportsComponent(String urlPath, String sysPath) {
		HyperLinkBuilder link = hyperLink(urlPath);
		ComponentBuilder<?, ?> dynamicReportsComponent = cmp.horizontalList(
				cmp.image(sysPath + CoreResources.getField(CoreResources.PROP_CLIENT_LOGO))
						.setFixedDimension(ONE_HUNDRED, FIFTY),
				cmp.verticalList(cmp.text("CRF Report").setStyle(BOLD_22_CENTERED_STYLE.setLeftPadding(TWENTY))
						.setHorizontalAlignment(HorizontalAlignment.LEFT), cmp.text(urlPath).setStyle(ITALIC_STYLE)
						.setHyperLink(link))).setFixedWidth(THREE_HUNDRED);
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
