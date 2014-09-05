package com.clinovo.model;

import org.akaza.openclinica.control.DefaultToolbar;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.core.CoreContext;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.AbstractItem;
import org.jmesa.view.html.toolbar.AbstractItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItem;
import org.jmesa.view.html.toolbar.ToolbarItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItemType;

import java.util.ResourceBundle;

/**
 * CRFEvaluationTableToolbar class.
 */
public class CRFEvaluationTableToolbar extends DefaultToolbar {

	public static final String ON_INVOKE_ACTION = "onInvokeAction";

	private ResourceBundle reswords = ResourceBundleProvider.getWordsBundle();

	private boolean evaluateWithContext;

	/**
	 * CRFEvaluationTableToolbar constructor.
	 * 
	 * @param evaluateWithContext
	 *            boolean
	 * @param showMoreLink
	 *            boolean
	 */
	public CRFEvaluationTableToolbar(boolean evaluateWithContext, boolean showMoreLink) {
		super();
		this.showMoreLink = showMoreLink;
		this.evaluateWithContext = evaluateWithContext;
	}

	@Override
	protected void addToolbarItems() {
		addToolbarItem(ToolbarItemType.SEPARATOR);
		addToolbarItem(createCustomItem(new ShowMoreItem()));
		addToolbarItem(createCustomItem(new NewHiddenItem()));
	}

	private ToolbarItem createCustomItem(AbstractItem item) {

		ToolbarItemRenderer renderer = new CustomItemRenderer(item, getCoreContext());
		renderer.setOnInvokeAction(ON_INVOKE_ACTION);
		item.setToolbarItemRenderer(renderer);

		return item;
	}

	private class ShowMoreItem extends AbstractItem {

		@Override
		public String disabled() {
			return null;
		}

		@Override
		public String enabled() {
			HtmlBuilder html = new HtmlBuilder();
			if (evaluateWithContext) {
				if (showMoreLink) {
					html.a()
							.id("showMore")
							.href("javascript:hideCols('crfEvaluationTable',[".concat(getIndexes()).concat(
									"],true);onInvokeAction('crfEvaluationTable','filter');")).close();
					html.div().close().nbsp().append(reswords.getString("show_more")).nbsp().divEnd().aEnd();
					html.a()
							.id("hide")
							.style("display: none;")
							.href("javascript:hideCols('crfEvaluationTable',[".concat(getIndexes()).concat(
									"],false);onInvokeAction('crfEvaluationTable','filter');")).close();
					html.div().close().nbsp().append(reswords.getString("hide")).nbsp().divEnd().aEnd();
					html.script()
							.type("text/javascript")
							.close()
							.append("$(document).ready(function(){ ".concat("hideCols('crfEvaluationTable',[")
									.concat(getIndexes()).concat("],false);});")).scriptEnd();
				} else {
					html.a()
							.id("hide")
							.href("javascript:hideCols('crfEvaluationTable',[".concat(getIndexes()).concat(
									"],false);onInvokeAction('crfEvaluationTable','filter');")).close();
					html.div().close().nbsp().append(reswords.getString("hide")).nbsp().divEnd().aEnd();
					html.a()
							.id("showMore")
							.style("display: none;")
							.href("javascript:hideCols('crfEvaluationTable',[".concat(getIndexes()).concat(
									"],true);onInvokeAction('crfEvaluationTable','filter');")).close();
					html.div().close().nbsp().append(reswords.getString("show_more")).nbsp().divEnd().aEnd();
				}
			}
			return html.toString();
		}

		String getIndexes() {
			return "1,2";
		}

	}

	private static class CustomItemRenderer extends AbstractItemRenderer {
		public CustomItemRenderer(ToolbarItem item, CoreContext coreContext) {
			setToolbarItem(item);
			setCoreContext(coreContext);
		}

		public String render() {
			ToolbarItem item = getToolbarItem();
			return item.enabled();
		}
	}

}
