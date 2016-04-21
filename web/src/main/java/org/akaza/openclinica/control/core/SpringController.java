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
package org.akaza.openclinica.control.core;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.RequestUtil;

/**
 * SpringController.
 * 
 * Here we can keep common methods for Servlets and for Controllers.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class SpringController extends BaseSpringController {

	/**
	 * Method that adds a message to the request.
	 *
	 * @param message
	 *            String
	 * @param request
	 *            HttpServletRequest
	 * @param aLogger
	 *            Logger
	 */
	public static void addPageMessage(String message, HttpServletRequest request, Logger aLogger) {
		ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);

		if (pageMessages == null) {
			pageMessages = new ArrayList();
		}

		if (!pageMessages.contains(message) && !message.isEmpty()) {
			pageMessages.add(message);
		}
		aLogger.debug(message);
		request.setAttribute(PAGE_MESSAGE, pageMessages);
	}

	/**
	 * Method that moves messages from request to the session.
	 *
	 * @param request
	 *            HttpServletRequest
	 */
	public static void storePageMessages(HttpServletRequest request) {
		Map storedAttributes = new HashMap();
		storedAttributes.put(PAGE_MESSAGE, request.getAttribute(PAGE_MESSAGE));
		request.getSession().setAttribute(STORED_ATTRIBUTES, storedAttributes);
	}

	/**
	 * Method that restores messages from the session.
	 *
	 * @param request
	 *            HttpServletRequest
	 */
	public static void restorePageMessages(HttpServletRequest request) {
		Map storedAttributes = (Map) request.getSession().getAttribute(STORED_ATTRIBUTES);
		if (storedAttributes != null) {
			request.getSession().removeAttribute(STORED_ATTRIBUTES);
			ArrayList pageMessages = (ArrayList) storedAttributes.get(PAGE_MESSAGE);
			if (pageMessages != null) {
				request.setAttribute(PAGE_MESSAGE, pageMessages);
			}
		}
	}

	/**
	 * This method returns context path.
	 *
	 * @param request
	 *            the HttpServletRequest from which path will be take.
	 * @return context path.
	 */
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath().replaceAll("/", "");
	}

	/**
	 * Get UserDetails from Context.
	 *
	 * @return UserDetails.
	 */
	protected UserDetails getUserDetails() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			return (UserDetails) principal;
		} else {
			return null;
		}
	}

	/**
	 * Sets input message.
	 *
	 * @param messages
	 *            HashMap
	 * @param request
	 *            HttpServletRequest
	 */
	public void setInputMessages(HashMap messages, HttpServletRequest request) {
		request.setAttribute(INPUT_MESSAGES, messages);
	}

	/**
	 * Download file.
	 *
	 * @param file
	 *            File.
	 * @param contentType
	 *            String.
	 * @param response
	 *            HttpServletResponse.
	 * @throws IOException
	 *             if there was some error.
	 */
	public void downloadFile(File file, String contentType, HttpServletResponse response) throws IOException {
		downloadFile(file, file.getName(), contentType, response);
	}

	/**
	 * Download file.
	 *
	 * @param file
	 *            File.
	 * @param fileName
	 *            String.
	 * @param contentType
	 *            String.
	 * @param response
	 *            HttpServletResponse.
	 * @throws IOException
	 *             if there was some error.
	 */
	public void downloadFile(File file, String fileName, String contentType, HttpServletResponse response)
			throws IOException {
		response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\";");
		response.setContentType(contentType);
		response.setHeader("Pragma", "public");
		ServletOutputStream op = response.getOutputStream();
		DataInputStream in = null;
		try {
			response.setContentLength((int) file.length());
			byte[] bbuf = new byte[(int) file.length()];
			in = new DataInputStream(new FileInputStream(file));
			int length;
			while ((length = in.read(bbuf)) != -1) {
				op.write(bbuf, 0, length);
			}
			in.close();
			op.flush();
			op.close();
		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
			if (op != null) {
				op.close();
			}
		}
	}

	/**
	 * Process Multiple Email Addresses.
	 *
	 * @param to
	 *            list of the recipients.
	 * @return InternetAddress[].
	 * @throws javax.mail.MessagingException
	 *             if there was an error.
	 */
	public InternetAddress[] processMultipleImailAddresses(String to) throws MessagingException {
		ArrayList<String> recipientsArray = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(to, ",");

		while (st.hasMoreTokens()) {
			recipientsArray.add(st.nextToken());
		}
		int sizeTo = recipientsArray.size();
		InternetAddress[] addressTo = new InternetAddress[sizeTo];

		for (int i = 0; i < sizeTo; i++) {
			addressTo[i] = new InternetAddress(recipientsArray.get(i));
		}
		return addressTo;
	}

	/**
	 * Updates calendar email job.
	 *
	 * @param uaBean
	 *            UserAccountBean
	 * @param logger
	 *            Logger
	 */
	public void updateCalendarEmailJob(UserAccountBean uaBean, Logger logger) {
		String triggerGroup = "CALENDAR";
		StdScheduler scheduler = getStdScheduler();
		try {
			Set<TriggerKey> legacyTriggers = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroup));
			if (legacyTriggers != null && legacyTriggers.size() > 0) {
				for (TriggerKey triggerKey : legacyTriggers) {
					Trigger trigger = scheduler.getTrigger(triggerKey);
					JobDataMap dataMap = trigger.getJobDataMap();
					String contactEmail = dataMap.getString(CONTACT_EMAIL);
					int userId = dataMap.getInt("user_id");
					logger.info("contact email from calendared " + contactEmail + " for user userId " + userId);
					logger.info("Old email " + dataMap.getString(CONTACT_EMAIL));
					if (uaBean.getId() == userId) {
						dataMap.put(CONTACT_EMAIL, uaBean.getEmail());
						JobDetailImpl jobDetailBean = new JobDetailImpl();
						jobDetailBean.setKey(trigger.getJobKey());
						jobDetailBean.setDescription(trigger.getDescription());
						jobDetailBean.setGroup(triggerGroup);
						jobDetailBean.setName(triggerKey.getName());
						jobDetailBean.setJobClass(org.akaza.openclinica.service.calendar.EmailStatefulJob.class);
						jobDetailBean.setJobDataMap(dataMap);
						logger.info("New email " + dataMap.getString(CONTACT_EMAIL));
						jobDetailBean.setDurability(true);
						scheduler.addJob(jobDetailBean, true);
					}
				}
			}
		} catch (SchedulerException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Returns theme color.
	 *
	 * @return String
	 */
	public String getThemeColor() {
		String themeColor = (String) RequestUtil.getRequest().getSession().getAttribute("newThemeColor");
		themeColor = themeColor == null ? "blue" : themeColor;
		if (themeColor.equalsIgnoreCase("violet")) {
			return "#aa62c6";
		} else if (themeColor.equalsIgnoreCase("green")) {
			return "#75b894";
		} else if (themeColor.equalsIgnoreCase("darkBlue")) {
			return "#2c6caf";
		}
		return "#729fcf";
	}

	/**
	 * Returns all studies.
	 *
	 * @return ArrayList
	 */
	public ArrayList getAllStudies() {
		return (ArrayList) getStudyDAO().findAll();
	}

	/**
	 * Returns current locale.
	 * 
	 * @return Locale
	 */
	public Locale getLocale() {
		return LocaleResolver.getLocale();
	}
}
