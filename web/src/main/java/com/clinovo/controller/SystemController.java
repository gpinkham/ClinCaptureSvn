package com.clinovo.controller;

import com.clinovo.command.SystemCommand;
import com.clinovo.service.SystemService;
import com.clinovo.util.FileUtil;
import com.clinovo.util.MayProceedUtil;
import com.clinovo.util.PageMessagesUtil;
import com.clinovo.util.SessionUtil;
import com.clinovo.validation.SystemValidator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.web.SQLInitServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/system")
public class SystemController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemController.class);

	public static final String SYSTEM_COMMAND_RESULT = "systemCommandResult";
	public static final String SYSTEM_COMMAND_ERROR = "systemCommandError";
	public static final String SYSTEM_COMMAND = "systemCommand";

	@Autowired
	private SystemValidator validator;

	@Autowired
	private SystemService systemService;

	@Autowired
	private CoreResources coreResources;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping(method = RequestMethod.GET)
	public String mainGet(HttpServletRequest request, Model model) throws Exception {
		String page = "system/system";
		if (!MayProceedUtil.mayProceed(request, Role.SYSTEM_ADMINISTRATOR, Role.STUDY_ADMINISTRATOR)) {
			page = "redirect:/MainMenu?message=system_no_permission";
		} else {
			SystemCommand systemCommand = (SystemCommand) request.getSession().getAttribute(SYSTEM_COMMAND);
			request.getSession().removeAttribute(SYSTEM_COMMAND);
			if (systemCommand == null || !systemCommand.isBackMode()) {
				systemCommand = new SystemCommand();
				Role role = ((StudyUserRoleBean) request.getSession().getAttribute(BaseController.USER_ROLE)).getRole();
				systemCommand.setSystemPropertyGroups(systemService.getSystemPropertyGroups(role));
			}
			model.addAttribute(SYSTEM_COMMAND, systemCommand);

			if (systemCommand.getSystemPropertyGroups().size() == 0) {
				model.addAttribute(SYSTEM_COMMAND_ERROR, "system_no_permission");
			} else {
				String systemCommandError = (String) request.getSession().getAttribute(SYSTEM_COMMAND_ERROR);
				request.getSession().removeAttribute(SYSTEM_COMMAND_ERROR);
				model.addAttribute(SYSTEM_COMMAND_ERROR, systemCommandError);
			}

			String messageCode = (String) request.getSession().getAttribute(SYSTEM_COMMAND_RESULT);
			request.getSession().removeAttribute(SYSTEM_COMMAND_RESULT);
			if (messageCode != null) {
				PageMessagesUtil.addPageMessage(request,
						messageSource.getMessage(messageCode, null, request.getLocale()));
			}
		}
		return page;
	}

	@RequestMapping(method = RequestMethod.POST, params = "back")
	public String back(HttpServletRequest request) throws Exception {
		SystemCommand systemCommand = (SystemCommand) request.getSession().getAttribute(SYSTEM_COMMAND);
		systemCommand.setBackMode(true);
		return "redirect:system";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(HttpServletRequest request, @ModelAttribute(SYSTEM_COMMAND) SystemCommand command,
			BindingResult result) throws Exception {
		String page = "system/systemConfirm";
		if (!MayProceedUtil.mayProceed(request, Role.SYSTEM_ADMINISTRATOR, Role.STUDY_ADMINISTRATOR)) {
			page = "redirect:/MainMenu?message=system_no_permission";
		} else {
			validator.validate(command, result, request.getLocale());
			if (result.hasErrors()) {
				page = "system/system";
			} else {
				try {
					FileUtil.saveLogo(command);
					request.getSession().setAttribute(SYSTEM_COMMAND, command);
				} catch (Exception ex) {
					page = "system/system";
					request.setAttribute(SYSTEM_COMMAND_ERROR, "error.systemCommand.dataWasNotSaved");
					LOGGER.error("Error has occurred.", ex);
				}
			}
		}
		return page;
	}

	@RequestMapping(method = RequestMethod.POST, params = "confirm")
	public String confirm(HttpServletRequest request) throws Exception {
		String page = "redirect:system";
		SystemCommand systemCommand = (SystemCommand) request.getSession().getAttribute(SYSTEM_COMMAND);
		request.getSession().removeAttribute(SYSTEM_COMMAND);
		request.setAttribute(SYSTEM_COMMAND, systemCommand);
		if (!MayProceedUtil.mayProceed(request, Role.SYSTEM_ADMINISTRATOR, Role.STUDY_ADMINISTRATOR)) {
			page = "redirect:/MainMenu?message=system_no_permission";
		} else {
			try {
				systemService.updateSystemProperties(systemCommand);
				SessionUtil.updateSession(coreResources, request.getSession());
				SQLInitServlet.updateParams(coreResources.getDataInfo());
				request.getSession().setAttribute(SYSTEM_COMMAND_RESULT, "systemCommand.dataWasSuccessfullySaved");
			} catch (Exception ex) {
				request.getSession().setAttribute(SYSTEM_COMMAND_ERROR, "error.systemCommand.dataWasNotSaved");
				LOGGER.error("Error has occurred.", ex);
			}
		}
		return page;
	}
}
