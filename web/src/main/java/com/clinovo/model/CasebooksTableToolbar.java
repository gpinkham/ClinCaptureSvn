package com.clinovo.model;

import org.akaza.openclinica.control.DefaultToolbar;
import org.jmesa.core.CoreContext;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.AbstractItem;
import org.jmesa.view.html.toolbar.AbstractItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItem;
import org.jmesa.view.html.toolbar.ToolbarItemRenderer;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Casebook table factory custom toolbar.
 */
public class CasebooksTableToolbar extends DefaultToolbar {

    private boolean secondaryIdRequired;
    private boolean dobRequired;
    private boolean genderRequired;
    private boolean personIdRequired;
    private boolean isSite;
    private MessageSource messageSource;
    private Locale locale;

    /**
     * Casebook table factory toolbar constructor.
     *
     * @param secondaryIdRequired the flag for secondary label.
     * @param dobRequired the flag for subject date of birth.
     * @param genderRequired the flag for study subject gender.
     * @param personIdRequired the flag for person id.
     * @param isSite the flag for study or site level.
     * @param messageSource the message source bean.
     * @param locale the locale.
     */
    public CasebooksTableToolbar(boolean secondaryIdRequired, boolean dobRequired, boolean genderRequired,
                                 boolean personIdRequired, boolean isSite, MessageSource messageSource, Locale locale) {
        super();
        this.secondaryIdRequired = secondaryIdRequired;
        this.genderRequired = genderRequired;
        this.dobRequired = dobRequired;
        this.personIdRequired = personIdRequired;
        this.isSite = isSite;
        this.messageSource = messageSource;
        this.locale = locale;
    }

    @Override
    protected void addToolbarItems() {
        addToolbarItem(createCustomItem(new DynamicShowHide()));
        addToolbarItem(createCustomItem(new SelectDeselectLinks()));
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    private class DynamicShowHide extends AbstractItem {

        @Override
        public String disabled() {
            return null;
        }

        @Override
        public String enabled() {
            HtmlBuilder html = new HtmlBuilder();
            html.script().type("text/javascript").close().append("$(document).ready(function(){ "
                    + "hideCols('" + CasebooksTableFactory.STUDY_CASEBOOKS_TABLE + "',["
                    + getIndexes() + "],false);});").scriptEnd();

            return html.toString();
        }

        private String getIndexes() {
            List<String> columns = new ArrayList<String>();
            columns.add(secondaryIdRequired ? "" : "6");
            columns.add(dobRequired ? "" : "7");
            columns.add(genderRequired ? "" : "5");
            columns.add(personIdRequired ? "" : "8");
            columns.add(isSite ? "1" : "");
            String result = "";
            for (String columnValue : columns) {
                if (!columnValue.isEmpty()) {
                    result = result.length() > 0 ? result.concat(", ").concat(columnValue) : result.concat(columnValue);
                }
            }
            return result;
        }
    }

    private class SelectDeselectLinks extends AbstractItem {

        @Override
        public String disabled() {
            return null;
        }

        @Override
        public String enabled() {
            HtmlBuilder html = new HtmlBuilder();
            html.tr(1).close().td(1).style("font-size: 12px;").colspan("100%").close();
            html.bold().append(getMessageSource().getMessage("table_sdv_select", null, locale)).nbsp().nbsp().boldEnd();
            html.a().name("checkAll").href("javascript:selectAll(true)").close()
                    .append(getMessageSource().getMessage("table_sdv_all", null, locale)).aEnd();
            html.nbsp().nbsp().nbsp();
            html.a().name("unCheckAll").href("javascript:selectAll(false)").close()
                    .append(getMessageSource().getMessage("table_sdv_none", null, locale)).aEnd();
            html.tdEnd().trEnd(1);
            return html.toString();
        }
    }

    private ToolbarItem createCustomItem(AbstractItem item) {

        ToolbarItemRenderer renderer = new CustomItemRenderer(item, getCoreContext());
        item.setToolbarItemRenderer(renderer);

        return item;
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
