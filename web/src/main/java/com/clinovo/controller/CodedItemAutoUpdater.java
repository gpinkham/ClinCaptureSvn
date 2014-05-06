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

package com.clinovo.controller;

import com.clinovo.model.CodedItem;
import com.clinovo.model.CodedItemElement;
import com.clinovo.model.Term;
import com.clinovo.service.CodedItemService;
import com.clinovo.service.TermService;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.job.OpenClinicaSchedulerFactoryBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jmesa.view.html.HtmlBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.*;

@Controller
public class CodedItemAutoUpdater {

    @Autowired
    private OpenClinicaSchedulerFactoryBean scheduler;

    @Autowired
    private TermService termService;

    @Autowired
    private CodedItemService codedItemService;

    @Autowired
    private DataSource datasource;

    private String themeColor;

    @RequestMapping(value = "/checkCodedItemsStatus")
    public void checkCodedItemsStatus(HttpServletRequest request, HttpServletResponse response) throws SchedulerException, IOException {

        String codedItemIdList = request.getParameter("arr");
        List<String> codedItemIdListString = new ArrayList<String>(Arrays.asList(codedItemIdList.split(",")));
        List<Integer> codedItemIdListInt = convertStringListToIntList(codedItemIdListString);
        List<Integer> codedItemsInProgress = new ArrayList<Integer>();

        themeColor = (String) request.getSession().getAttribute("newThemeColor");
        String showContext = request.getParameter("showContext");
        themeColor = themeColor == null ? "blue" : themeColor;
        showContext = showContext == null ? "false" : showContext;

        for (JobExecutionContext jobExContext : getJobsList()) {

            JobDataMap dataMap = jobExContext.getMergedJobDataMap();

            for (int codedItemId : codedItemIdListInt) {

                if (dataMap.getIntegerFromString("codedItem") == codedItemId) {

                    codedItemsInProgress.add(dataMap.getIntegerFromString("codedItem"));
                }
            }
        }

        codedItemIdListInt.removeAll(codedItemsInProgress);

        response.getWriter().println(buildResponseBox(codedItemIdListInt, showContext));
    }

    private List<String> buildResponseBox(List<Integer> codedItemIdListInt, String showContext) {

        List<String> codedItemToAppend = new ArrayList<String>();

        for (int codedItemId : codedItemIdListInt) {

            CodedItem codedItem = codedItemService.findCodedItem(codedItemId);

            if (codedItem.isCoded()) {

                ItemDataDAO itemDataDAO = new ItemDataDAO(datasource);
                ItemDataBean data = (ItemDataBean) itemDataDAO.findByPK(codedItem.getItemId());
                Term term = termService.findByAliasAndExternalDictionary(data.getValue().toLowerCase(), codedItem.getDictionary());

                if (term != null) {

                    codedItemToAppend.add(contextBoxBuilder(codedItem, term.getLocalAlias(), term.getPreferredName(), showContext));
                } else {

                    codedItemToAppend.add(contextBoxBuilder(codedItem, "", "", showContext));
                }
            }
        }

        return codedItemToAppend;
    }

    private String contextBoxBuilder(CodedItem codedItem, String alise, String prefTerm, String showContext) {

        String termToAppend = "";
        String prefToAppend = "";
        String displayStyle = "display:none;";
		String httpPathDisplay = codedItem.getDictionary().equals("WHOD") ? "display:none;" : "";

        if(!alise.isEmpty() && !prefTerm.isEmpty()) {

            termToAppend = alise;
            prefToAppend = prefTerm;
        }

        if(showContext.equals("true")) {

            displayStyle = "";
        }

        HtmlBuilder builder = new HtmlBuilder();
            builder.table(1).id("tablepaging").styleClass("itemsTable")
                    .append(" idToAppend=\"" + codedItem.getItemId() + "\" ")
                    .style(displayStyle)
                    .append(" termToAppend=\"" + termToAppend + "\" ")
                    .append(" prefToAppend=\"" + prefToAppend + "\" ")
                    .close()
                    .tr(1).style(httpPathDisplay).close()
                    .td(1).close().append("HTTP: ").tdEnd()
					.td(2).close().a().style("color:" + getThemeColor() + "").append(" target=\"_blank\" ").href("http://bioportal.bioontology.org/ontologies/"
					+ codedItem.getDictionary().replace("_", "") + "?p=classes&conceptid=" + codedItem.getHttpPath()).close().append(codedItem.getHttpPath()).aEnd().tdEnd()
					.td(3).width("360px").colspan("2").close().tdEnd()
                    .td(4).close().tdEnd().trEnd(1);

            for (CodedItemElement codedItemElement : codedItemElementsFilter(codedItem).getCodedItemElements()) {

                builder.tr(1).close().td(1).close().append(" " + codedItemElement.getItemName() + ": ").tdEnd()
                        .td(2).close().append(codedItemElement.getItemCode()).tdEnd().tdEnd()
                        .td(3).width("360px").colspan("2").close().tdEnd()
                        .td(4).close().tdEnd().trEnd(1).trEnd(1);

            }

            builder.tableEnd(1);
            builder.append("separatorMark");

        return builder.toString();
    }

    private CodedItem codedItemElementsFilter(CodedItem codedItem) {

        CodedItem codedItemWithFilterFields = new CodedItem();

		for (CodedItemElement codedItemElement : codedItem.getCodedItemElements()) {

			for (CodedItemElement codedItemIteration : codedItem.getCodedItemElements()) {

				if ((codedItemElement.getItemName() + "C").equals(codedItemIteration.getItemName())) {

					codedItemWithFilterFields.addCodedItemElements(codedItemElement);
					break;
				} else if (codedItemElement.getItemName().equals("CMP")) {

					codedItemWithFilterFields.addCodedItemElements(codedItemElement);
					break;
				}
			}
		}

        Collections.sort(codedItemWithFilterFields.getCodedItemElements(), new codedElementSortByItemDataId());

        return codedItemWithFilterFields;
    }

    private class codedElementSortByItemDataId implements Comparator<Object> {

        public int compare(Object o1, Object o2) {
            CodedItemElement p1 = (CodedItemElement) o1;
            CodedItemElement p2 = (CodedItemElement) o2;
            return p1.getItemDataId() - p2.getItemDataId();
        }
    }

    private List<Integer> convertStringListToIntList(List<String> codedItemIdListString) {

        List<Integer> intList = new ArrayList<Integer>();

        for (String s : codedItemIdListString) {

            intList.add(Integer.valueOf(s));
        }

        return intList;
    }

    private List<JobExecutionContext> getJobsList() throws SchedulerException {

        return scheduler.getScheduler().getCurrentlyExecutingJobs();
    }

    public String getThemeColor() {

        if (themeColor.equalsIgnoreCase("violet")) {

            return "#aa62c6";

        } else if (themeColor.equalsIgnoreCase("green")) {

            return "#75b894";
        }

        return "#729fcf";
    }

}
