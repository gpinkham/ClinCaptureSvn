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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.model;

import org.akaza.openclinica.control.DefaultToolbar;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.core.CoreContext;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.*;

import java.util.ResourceBundle;

public class CodedItemsTableToolbar extends DefaultToolbar {

    private ResourceBundle reswords = ResourceBundleProvider.getWordsBundle();
    private boolean contextNeeded;
    private boolean showCodedItemsContext;

    private final static String INDEXES_OF_COLUMNS_TO_BE_HIDDEN = "3, 4, 5";

    public CodedItemsTableToolbar(boolean showMoreLink, boolean contextNeeded, boolean showCodedItemsContext) {
        this.showMoreLink = showMoreLink;
        this.contextNeeded = contextNeeded;
        this.showCodedItemsContext = showCodedItemsContext;
    }

    @Override
    protected void addToolbarItems() {
        addToolbarItem(ToolbarItemType.SEPARATOR);
        addToolbarItem(createCustomItem(new displayCodedItemContextInput()));
        addToolbarItem(createCustomItem(new showHideCodedItemsContext()));
        addToolbarItem(ToolbarItemType.SEPARATOR);
        addToolbarItem(createCustomItem(new ShowMoreItem()));
        addToolbarItem(createCustomItem(new NewHiddenItem()));
    }

    private ToolbarItem createCustomItem(AbstractItem item) {

        ToolbarItemRenderer renderer = new CustomItemRenderer(item, getCoreContext());
        renderer.setOnInvokeAction("onInvokeAction");
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

            if (contextNeeded) {

                if(showMoreLink) {

                    html.a().id("showMore").href("javascript:hideCols('codedItemsId',["
                            + getIndexes() + "],true); javascript:onInvokeAction('codedItemsId','filter')").close();
                    html.div().close().nbsp().append(reswords.getString("show_more")).nbsp().divEnd().aEnd();

                    html.a().id("hide").style("display: none;") .href("javascript:hideCols('codedItemsId',["
                            + getIndexes() + "],false); javascript:onInvokeAction('codedItemsId','filter')").close();
                    html.div().close().nbsp().append(reswords.getString("hide")).nbsp().divEnd().aEnd();

                    html.script().type("text/javascript").close().append("$(document).ready(function(){ "
                            + "hideCols('codedItemsId',["
                            + getIndexes() + "],false);});").scriptEnd();

                } else {

                    html.a().id("showMore").style("display: none;").href("javascript:hideCols('codedItemsId',["
                            + getIndexes() + "],true); javascript:onInvokeAction('codedItemsId','filter')").close();
                    html.div().close().nbsp().append(reswords.getString("show_more")).nbsp().divEnd().aEnd();

                    html.a().id("hide").href("javascript:hideCols('codedItemsId',["
                            + getIndexes() + "],false); javascript:onInvokeAction('codedItemsId','filter')").close();
                    html.div().close().nbsp().append(reswords.getString("hide")).nbsp().divEnd().aEnd();

                    html.script().type("text/javascript").close().append("$(document).ready(function(){ "
                            + "hideCols('codedItemsId',["
                            + getIndexes() + "],true);});").scriptEnd();

                }

            } else {

                html.script().type("text/javascript").close().append("$(document).ready(function(){ "
                        + "hideCols('codedItemsId',["
                        + getIndexes() + "],false);});").scriptEnd();
            }

            return html.toString();
        }

        String getIndexes() {
            return INDEXES_OF_COLUMNS_TO_BE_HIDDEN;
        }
    }

   private class showHideCodedItemsContext extends AbstractItem {

       @Override
       public String disabled() {
           return null;
       }

       @Override
       public String enabled() {

           HtmlBuilder html = new HtmlBuilder();
           if (showCodedItemsContext) {

               html.a().id("showContext").href("javascript:updateExpandCollapseCodedItemsInput(" + showCodedItemsContext + "); javascript:onInvokeAction('codedItemsId','filter')").close();
               html.div().close().nbsp().append(reswords.getString("collapse")).nbsp().divEnd().aEnd();
           } else {

               html.a().id("showContext").href("javascript:updateExpandCollapseCodedItemsInput(" + showCodedItemsContext + "); javascript:onInvokeAction('codedItemsId','filter')").close();
               html.div().close().nbsp().append(reswords.getString("expand")).nbsp().divEnd().aEnd();
           }

           return html.toString();
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

    public class displayCodedItemContextInput extends AbstractItem {

        @Override
        public String disabled() {
            return null;
        }

        @Override
        public String enabled() {
            HtmlBuilder html = new HtmlBuilder();
            html.input().id("showContext").type("hidden").name("showContext").value(showCodedItemsContext + "").end();
            return html.toString();
        }

    }

}

