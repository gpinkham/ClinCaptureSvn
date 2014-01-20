package com.clinovo.util;

import com.clinovo.model.CodedItem;
import com.clinovo.model.Term;
import com.clinovo.service.CodedItemService;
import com.clinovo.service.TermService;
import org.akaza.openclinica.job.OpenClinicaSchedulerFactoryBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class CodedItemAutoUpdater {

    @Autowired
    private OpenClinicaSchedulerFactoryBean scheduler;

    @Autowired
    private TermService termService;

    @Autowired
    private CodedItemService codedItemService;

    @RequestMapping(value = "/checkCodedItemsStatus")
    public void checkCodedItemsStatus(HttpServletRequest request, HttpServletResponse response) throws SchedulerException, IOException {

        String codedItemIdList = request.getParameter("arr");
        List<String> codedItemIdListString = new ArrayList<String>(Arrays.asList(codedItemIdList.split(",")));
        List<Integer> codedItemIdListInt = convertStringListToIntList(codedItemIdListString);
        List<Integer> codedItemsInProgress = new ArrayList<Integer>();

        for (JobExecutionContext jobExContext : getJobsList()) {

            JobDataMap dataMap = jobExContext.getMergedJobDataMap();

            for (int codedItemId : codedItemIdListInt) {

                if (dataMap.getIntegerFromString("codedItem") == codedItemId) {

                    codedItemsInProgress.add(dataMap.getIntegerFromString("codedItem"));
                }
            }
        }

        codedItemIdListInt.removeAll(codedItemsInProgress);

        response.getWriter().println(appendCodedItemTerm(codedItemIdListInt));
    }

    private List<String> appendCodedItemTerm(List<Integer> codedItemIdListInt) {

        List<String> codedItemIdWithTerm = new ArrayList<String>();

        for (int codedItemId : codedItemIdListInt) {

            CodedItem codedItem = codedItemService.findCodedItem(codedItemId);
            Term term = termService.findByTermAndExternalDictionary(codedItem.getPreferredTerm().toLowerCase(), codedItem.getDictionary());

            if (term != null) {

                codedItemIdWithTerm.add(String.valueOf(codedItemId) + "_" + term.getPreferredName());
            } else {

                codedItemIdWithTerm.add(String.valueOf(codedItemId));
            }
        }

        return codedItemIdWithTerm;
    }

    private List<JobExecutionContext> getJobsList() throws SchedulerException {

        return scheduler.getScheduler().getCurrentlyExecutingJobs();
    }

    private List<Integer> convertStringListToIntList(List<String> codedItemIdListString) {

        List<Integer> intList = new ArrayList<Integer>();

        for (String s : codedItemIdListString) {

            intList.add(Integer.valueOf(s));
        }

        return intList;
    }

}
