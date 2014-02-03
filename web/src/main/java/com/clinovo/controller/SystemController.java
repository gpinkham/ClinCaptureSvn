package com.clinovo.controller;

import com.clinovo.command.SystemCommand;
import com.clinovo.service.SystemService;
import com.clinovo.util.FileUtil;
import com.clinovo.util.MayProceedUtil;
import com.clinovo.util.PageMessagesUtil;
import com.clinovo.validation.SystemValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
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

@Controller
@RequestMapping("/system")
public class SystemController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemController.class);

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
			SystemCommand systemCommand = (SystemCommand) request.getSession().getAttribute("systemCommand");
			if (systemCommand != null) {
				request.getSession().removeAttribute("systemCommand");
			} else {
				systemCommand = new SystemCommand();
			}
			model.addAttribute("systemCommand", systemCommand);

			String systemCommandError = (String) request.getSession().getAttribute("systemCommandError");
			request.getSession().removeAttribute("systemCommandError");
			model.addAttribute("systemCommandError", systemCommandError);

			Role role = ((StudyUserRoleBean) request.getSession().getAttribute("userRole")).getRole();
			systemCommand.setSystemPropertyGroups(systemService.getSystemPropertyGroups(role));

			if (systemCommand.getSystemPropertyGroups().size() == 0) {
				model.addAttribute("systemCommandError", "system_no_permission");
			}

			String messageCode = (String) request.getSession().getAttribute("systemCommandResult");
			if (messageCode != null) {
				PageMessagesUtil.addPageMessage(request,
						messageSource.getMessage(messageCode, null, request.getLocale()));
				request.getSession().removeAttribute("systemCommandResult");
			}
		}
		return page;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(HttpServletRequest request, @ModelAttribute("systemCommand") SystemCommand command,
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
					request.getSession().setAttribute("systemCommand", command);
				} catch (Exception ex) {
					page = "system/system";
					request.setAttribute("systemCommandError", "error.systemCommand.dataWasNotSaved");
					LOGGER.error("Error has occurred.", ex);
				}
			}
		}
		return page;
	}

	@RequestMapping(method = RequestMethod.POST, params = "confirm")
	public String confirm(HttpServletRequest request) throws Exception {
		String page = "system/system";
		request.setAttribute("systemCommandError", "error.systemCommand.dataWasNotSaved");
		SystemCommand systemCommand = (SystemCommand) request.getSession().getAttribute("systemCommand");
		if (!MayProceedUtil.mayProceed(request, Role.SYSTEM_ADMINISTRATOR, Role.STUDY_ADMINISTRATOR)) {
			page = "redirect:/MainMenu?message=system_no_permission";
		} else {
			if (systemCommand != null) {
				try {
					request.setAttribute("systemCommand", systemCommand);
					request.getSession().removeAttribute("systemCommand");
					systemService.updateSystemProperties(systemCommand);
					updateSession(request.getSession());
					SQLInitServlet.updateParams(coreResources.getDataInfo());
					request.removeAttribute("systemCommandError");
					request.getSession().removeAttribute("systemCommand");
					request.getSession().setAttribute("systemCommandResult", "systemCommand.dataWasSuccessfullySaved");
					page = "redirect:system";
				} catch (Exception ex) {
					LOGGER.error("Error has occurred.", ex);
				}
			}
		}
		return page;
	}

	private void updateSession(HttpSession session) throws Exception {
		for (Object key : coreResources.getDataInfo().keySet()) {
			if (key instanceof String) {
				Object value = session.getAttribute((String) key);
				if (value != null) {
					session.setAttribute((String) key, CoreResources.getField((String) key));
				}
			}
		}
		session.setAttribute("newThemeColor", CoreResources.getField("themeColor"));
	}
}
