/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/
package com.clinovo.servlet;

import com.clinovo.builder.RuleStudioJSONBuilder;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet that is used to handle all RulesStudio actions.
 */
@SuppressWarnings("serial")
public class RulesServlet extends HttpServlet {

	public static final int RANDOM_OID_RANGE = 101;
	public static final int RULE_OID_INCREMENT = 10;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String action = request.getParameter("action");
		PrintWriter writer = response.getWriter();
		try {
			DataSource datasource = SpringServletAccess.getApplicationContext(this.getServletContext()).getBean(DataSource.class);
			RuleStudioJSONBuilder rsJSONBuilder = new RuleStudioJSONBuilder(datasource);
			UserAccountBean userAccountBean = (UserAccountBean) request.getSession().getAttribute("userBean");

			if ("fetch".equals(action)) {
				JSONArray studies = rsJSONBuilder.buildStudiesArray(userAccountBean);
				writer.write(studies.toString());
			} else if ("events".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				StudyDAO studyDAO = new StudyDAO(datasource);
				StudyBean study = (StudyBean) studyDAO.findByPK(id);
				JSONArray events = rsJSONBuilder.buildEventsArray(study);
				writer.write(events.toString());
			} else if ("crfs".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(datasource);
				StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(id);
				StudyDAO studyDAO = new StudyDAO(datasource);
				StudyBean study = (StudyBean) studyDAO.findByPK(studyEventDefinitionBean.getStudyId());
				JSONArray crfs = rsJSONBuilder.buildCRFsArray(studyEventDefinitionBean, study);
				writer.write(crfs.toString());
			} else if ("versions".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				CRFDAO crfdao = new CRFDAO(datasource);
				CRFBean crf = (CRFBean) crfdao.findByPK(id);
				JSONArray versions = rsJSONBuilder.buildVersionsArray(crf);
				writer.write(versions.toString());
			} else if ("items".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				CRFVersionDAO crfVersionDAO = new CRFVersionDAO(datasource);
				CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDAO.findByPK(id);
				StudyDAO studyDAO = new StudyDAO(datasource);
				int studyId = Integer.parseInt(request.getParameter("studyId"));
				StudyBean study = (StudyBean) studyDAO.findByPK(studyId);
				JSONArray items = rsJSONBuilder.buildItemsArray(crfVersionBean, study);
				writer.write(items.toString());
			} else if ("validate".equals(action)) {
				JSONObject rule = new JSONObject(request.getParameter("rule"));
				Document document = createRuleDocument(rule);
				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				StringWriter xWriter = new StringWriter();
				transformer.transform(new DOMSource(document), new StreamResult(xWriter));
				writer.write(xWriter.getBuffer().toString());
			} else if ("edit".equals(action)) {
				RuleDao dao = SpringServletAccess.getApplicationContext(this.getServletContext())
						.getBean(RuleDao.class);
				RuleSetRuleDao rDao = SpringServletAccess.getApplicationContext(this.getServletContext()).getBean(
						RuleSetRuleDao.class);
				RuleBean rule = dao.findById(Integer.parseInt(request.getParameter("id")));
				RuleSetRuleBean ruleSetRule = rDao.findById(Integer.parseInt(request.getParameter("rId")));

				JSONObject wrapper = new JSONObject();
				JSONArray studies = rsJSONBuilder.getCascadeRuleData(userAccountBean, rule, ruleSetRule);
				JSONObject object = rsJSONBuilder.buildRule(rule, ruleSetRule);
				wrapper.put("studies", studies);
				wrapper.put("rule", object);
				response.getWriter().write(wrapper.toString());
			}
		} catch (Exception ex) {
			response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		} finally {
			writer.flush();
			writer.close();
		}
	}

	private Document createRuleDocument(JSONObject rule) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		List<Element> ruleDefs = new LinkedList<Element>();
		Element rootElement = document.createElement("RuleImport");
		// Rule Assignments
		JSONArray rTargets = rule.getJSONArray("targets");
		int currentCount = 1;

		for (int x = 0; x < rTargets.length(); x++) {
			JSONArray actions = rule.getJSONArray("actions");
			Element rAssigment = document.createElement("RuleAssignment");
			// Target
			Element rTarget = document.createElement("Target");
			rTarget.setAttribute("Context", "OC_RULES_V1");
			rTarget.setAttribute("EventOid", rTargets.getJSONObject(x).getString("evt"));
			rTarget.setAttribute("VersionOid", rTargets.getJSONObject(x).getString("version"));

			// Content
			rTarget.setTextContent(rTargets.getJSONObject(x).getString("name"));
			String expression = rTargets.getJSONObject(x).getString("expression");
			// Rule target children
			rAssigment.appendChild(rTarget);
			for (int act = 0; act < actions.length(); act++) {
				// Rule Def
				Element ruleDef = document.createElement("RuleDef");
				// attributes
				ruleDef.setAttribute("Name", rule.getString("name"));
				ruleDef.setAttribute("OID", generateOID(expression, currentCount));
				// Increment the count
				currentCount = currentCount + 1;
				Element expr = document.createElement("Expression");
				expr.setTextContent(expression);
				// Description - it comes before expression
				Element description = document.createElement("Description");
				description.setTextContent(rule.getString("name"));
				ruleDef.appendChild(description);
				ruleDef.appendChild(expr);
				// Rule Ref
				Element ref = document.createElement("RuleRef");
				ref.setAttribute("OID", ruleDef.getAttribute("OID"));

				List<Element> acts = createAction(actions.getString(act), rule, document);
				for (Element el : acts) {
					ref.appendChild(el);
				}
				// Append ref
				rAssigment.appendChild(ref);
				// Append to root
				ruleDefs.add(ruleDef);
			}
			rootElement.appendChild(rAssigment);
		}
		for (Element ele : ruleDefs) {
			rootElement.appendChild(ele);
		}
		document.appendChild(rootElement);
		return document;
	}

	private String generateOID(String expression, int x) {
		String oid = "";
		Pattern pattern = Pattern.compile("^\\w+?_(\\w{2})");
		Matcher matcher = pattern.matcher(expression.trim());
		RuleDao ruleDAO = SpringServletAccess.getApplicationContext(this.getServletContext()).getBean(RuleDao.class);
		if (matcher.find()) {
			oid = matcher.group(0);
			Pattern sPattern = Pattern.compile("(?<=_)([A-z]{2})");
			Matcher sMatcher = sPattern.matcher(oid.trim());
			if (sMatcher.find()) {
				oid = sMatcher.group(0);
			}
		}

		List<String> oids = ruleDAO.findRuleOIDs();
		if (oids != null && !oids.isEmpty()) {
			oid = oid + "_" + new Random().nextInt(RANDOM_OID_RANGE) + RULE_OID_INCREMENT;
			oid = checkOIDAvailability(oid, oids);
		} else {
			oid = oid + "_0" + x;
		}
		return oid;
	}

	private String checkOIDAvailability(String oid, List<String> oids) {
		if (oids.contains(oid)) {
			Pattern pattern = Pattern.compile("(\\d+)");
			Matcher matcher = pattern.matcher(oid);
			if (matcher.find()) {
				oid = oid.substring(0, oid.length() - matcher.group(0).length()) + (Integer.valueOf(matcher.group(0)) + 1);
				return checkOIDAvailability(oid, oids);
			}
		}
		return oid;
	}

	private List<Element> createAction(String actString, JSONObject rule, Document document) throws Exception {
		List<Element> actions = new LinkedList<Element>();
		JSONObject act = new JSONObject(actString);
		// Run
		Element run = document.createElement("Run");
		run.setAttribute("ImportDataEntry", rule.getString("di"));
		run.setAttribute("InitialDataEntry", rule.getString("ide"));
		run.setAttribute("DoubleDataEntry", rule.getString("dde"));
		run.setAttribute("AdministrativeDataEntry", rule.getString("ae"));
		if (act.getString("type").equalsIgnoreCase("discrepancy")) {
			run.setAttribute("Batch", "true");
			Element discrepancyText = document.createElement("Message");
			discrepancyText.setTextContent(act.getString("message"));
			Element action = document.createElement("DiscrepancyNoteAction");
			action.setAttribute("IfExpressionEvaluates", rule.getString("evaluates"));
			action.appendChild(run);
			action.appendChild(discrepancyText);
			actions.add(action);
		} else if (act.getString("type").equalsIgnoreCase("email")) {
			run.setAttribute("Batch", "true");
			// Message element
			Element message = document.createElement("Message");
			message.setTextContent(act.getString("body"));
			Element action = document.createElement("EmailAction");
			action.setAttribute("IfExpressionEvaluates", rule.getString("evaluates"));
			// To element
			Element to = document.createElement("To");
			to.setTextContent(act.getString("to"));
			action.appendChild(run);
			// Email props
			action.appendChild(message);
			action.appendChild(to);
			actions.add(action);
		} else if (act.getString("type").equalsIgnoreCase("showHide")) {
			run.setAttribute("Batch", "false");
			// Message element
			Element message = document.createElement("Message");
			message.setTextContent(act.getString("message"));
			// Show action
			Element showAction = document.createElement("ShowAction");
			// children
			showAction.setAttribute("IfExpressionEvaluates", act.getString("show"));
			showAction.appendChild(run);
			showAction.appendChild(message);
			JSONArray targets = act.getJSONArray("destinations");
			for (int x = 0; x < targets.length(); x++) {
				// Target
				Element destProp = document.createElement("DestinationProperty");
				destProp.setAttribute("OID", targets.getString(x));
				showAction.appendChild(destProp);
			}
			actions.add(showAction);
			// clone node
			Element hideAction = (Element) showAction.cloneNode(true);
			document.renameNode(hideAction, null, "HideAction");
			// Remove message node
			hideAction.getElementsByTagName("Message").item(0).setTextContent(null);
			hideAction.setAttribute("IfExpressionEvaluates", act.getString("hide"));
			actions.add(hideAction);
		} else if (act.getString("type").equalsIgnoreCase("insert")) {
			run.setAttribute("Batch", "false");
			Element action = document.createElement("InsertAction");
			action.setAttribute("IfExpressionEvaluates", rule.getString("evaluates"));
			Element msg = document.createElement("Message");
			msg.setTextContent(act.getString("message"));
			JSONArray destinations = act.getJSONArray("destinations");
			action.appendChild(run);
			for (int x = 0; x < destinations.length(); x++) {
				JSONObject dest = destinations.getJSONObject(x);
				// Target
				Element destProp = document.createElement("DestinationProperty");
				destProp.setAttribute("OID", dest.getString("oid"));
				if (dest.has("item") && dest.getBoolean("item")) {
					Element valueExpression = document.createElement("ValueExpression");
					valueExpression.setAttribute("Context", "OC_RULES_V1");
					valueExpression.setTextContent(dest.getString("value"));
					destProp.appendChild(valueExpression);
				} else {
					destProp.setAttribute("Value", dest.getString("value"));
				}
				action.appendChild(destProp);
			}
			actions.add(action);
		}
		return actions;
	}
}
