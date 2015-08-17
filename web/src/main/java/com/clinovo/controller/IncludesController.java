package com.clinovo.controller;

import com.clinovo.controller.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller that was created in order to manage includes.
 */
@Controller
public class IncludesController extends BaseController {

	public static final Logger LOGGER = LoggerFactory.getLogger(CRFEvaluationController.class);

	/**
	 * Get content of the page.
	 * @param page String page name
	 * @return page content.
	 */
	@RequestMapping(value = "/includes/getPageContent", method = RequestMethod.POST)
	public String getPageContent(@RequestParam("page") String page) {
		if (!page.isEmpty()) {
			return page;
		} else {
			return "";
		}
	}
}
