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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.servlet;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.ActionType;
import org.akaza.openclinica.domain.rule.action.DiscrepancyNoteActionBean;
import org.akaza.openclinica.domain.rule.action.EmailActionBean;
import org.akaza.openclinica.domain.rule.action.HideActionBean;
import org.akaza.openclinica.domain.rule.action.InsertActionBean;
import org.akaza.openclinica.domain.rule.action.PropertyBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.action.ShowActionBean;
import org.json.JSONArray;
import org.json.JSONException;
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

@SuppressWarnings({ "unchecked", "serial" })
public class RulesServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String action = request.getParameter("action");
		PrintWriter writer = response.getWriter();
		try {

			DataSource datasource = SpringServletAccess.getApplicationContext(this.getServletContext()).getBean(
					DataSource.class);
			if ("fetch".equals(action)) {

				JSONArray studies = new JSONArray();
				StudyDAO studyDAO = new StudyDAO(datasource);
				UserAccountBean userAccountBean = (UserAccountBean) request.getSession().getAttribute("userBean");
				List<StudyBean> availableStudies = studyDAO.findAllActiveStudiesWhereUserHasRole(userAccountBean
						.getName());
				for (StudyBean x : availableStudies) {
					if (!x.isSite(x.getParentStudyId())) {

						JSONObject study = new JSONObject();
						study.put("id", x.getId());
						study.put("oid", x.getOid());
						study.put("name", x.getName());
						study.put("description", x.getSummary());
						study.put("identifier", x.getIdentifier());

						JSONArray events = getStudyEvents(x, datasource);
						study.put("events", events);
						studies.put(study);
					}
				}

				writer.write(studies.toString());

			} else if ("validate".equals(action)) {

				JSONObject rule = new JSONObject(request.getParameter("rule"));

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
					// Content
					rTarget.setTextContent(rTargets.getJSONObject(x).getString("name"));
					// Rule target children
					rAssigment.appendChild(rTarget);
					for (int act = 0; act < actions.length(); act++) {
						// Rule Def
						Element ruleDef = document.createElement("RuleDef");
						// attributes
						ruleDef.setAttribute("Name", rule.getString("name"));
						ruleDef.setAttribute("OID", generateOID(rule.getString("expression"), currentCount));
						// Increment the count
						currentCount = currentCount + 1;
						Element expr = document.createElement("Expression");
						expr.setTextContent(rule.getString("expression"));
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

				JSONObject object = new JSONObject();
				RuleBean rule = dao.findById(Integer.parseInt(request.getParameter("id")));
				JSONArray ruleActions = new JSONArray();
				RuleSetRuleBean ruleSetRule = rDao.findById(Integer.parseInt(request.getParameter("rId")));
				try {
					JSONObject act = new JSONObject();
					act.put("type", ruleSetRule.getActions().get(0).getActionType());
					act.put("select", ruleSetRule.getActions().get(0).getExpressionEvaluatesTo());
					object.put("evaluates", ruleSetRule.getActions().get(0).getExpressionEvaluatesTo());
					if (ruleSetRule.getActions().get(0).getActionType().equals(ActionType.EMAIL)) {
						EmailActionBean emailAction = (EmailActionBean) ruleSetRule.getActions().get(0);
						act.put("to", emailAction.getTo());
						act.put("body", emailAction.getMessage());
					} else if (ruleSetRule.getActions().get(0).getActionType().equals(ActionType.FILE_DISCREPANCY_NOTE)) {
						DiscrepancyNoteActionBean discrepancyAction = (DiscrepancyNoteActionBean) ruleSetRule
								.getActions().get(0);
						act.put("type", "discrepancy");
						act.put("message", discrepancyAction.getMessage());
					} else if (ruleSetRule.getActions().get(0).getActionType().equals(ActionType.INSERT)) {
						InsertActionBean insertAction = (InsertActionBean) ruleSetRule.getActions().get(0);
						act.put("type", "insert");
						act.put("message", insertAction.getCuratedMessage());
						// Destinations
						JSONArray destinations = new JSONArray();
						for (int x = 0; x < insertAction.getProperties().size(); x++) {
							JSONObject dest = new JSONObject();
							PropertyBean bean = insertAction.getProperties().get(x);
							dest.put("id", bean.getId());
							dest.put("oid", bean.getOid());
							if (bean.getValue() == null && bean.getValueExpression() != null) {
								dest.put("item", true);
								dest.put("value", bean.getValueExpression().getValue());
							} else {
								dest.put("value", bean.getValue());
							}
							destinations.put(dest);
						}
						act.put("destinations", destinations);
					} else if (ruleSetRule.getActions().get(0).getActionType().equals(ActionType.SHOW)
							|| ruleSetRule.getActions().get(0).getActionType().equals(ActionType.HIDE)) {
						act.put("type", "showHide");
						for (int x = 0; x < ruleSetRule.getActions().size(); x++) {
							RuleActionBean vAction = ruleSetRule.getActions().get(x);
							if (vAction.getActionType().equals(ActionType.SHOW)) {
								ShowActionBean showAction = (ShowActionBean) ruleSetRule.getActions().get(x);
								act.put("type", "showHide");
								act.put("message", showAction.getMessage());
								act.put("show", showAction.getExpressionEvaluatesTo());
								object.put("evaluates", showAction.getExpressionEvaluatesTo());
							} else {
								HideActionBean hideAction = (HideActionBean) ruleSetRule.getActions().get(x);
								act.put("hide", hideAction.getExpressionEvaluatesTo());
								JSONArray destinations = new JSONArray();
								for (int d = 0; d < hideAction.getProperties().size(); d++) {
									destinations.put(hideAction.getProperties().get(d).getOid());
								}
								act.put("destinations", destinations);
							}
						}
					}

					ruleActions.put(act);

				} catch (JSONException e) {
					response.sendError(500, e.getMessage());
				}
				// Rule run
				object.put("di", rule.getRuleSetRules().get(0).getActions().get(0).getRuleActionRun()
						.getImportDataEntry());
				object.put("dde", rule.getRuleSetRules().get(0).getActions().get(0).getRuleActionRun()
						.getDoubleDataEntry());
				object.put("ide", rule.getRuleSetRules().get(0).getActions().get(0).getRuleActionRun()
						.getInitialDataEntry());
				object.put("ae", rule.getRuleSetRules().get(0).getActions().get(0).getRuleActionRun()
						.getAdministrativeDataEntry());
				// Targets
				JSONObject tar = new JSONObject();
				JSONArray targets = new JSONArray();

				String targetPath = rule.getRuleSetRules().get(0).getRuleSetBean().getOriginalTarget().getValue();
				String name = targetPath.replaceAll("\\[\\w+\\]", "").trim();
				tar.put("name", name);
				if (targetPath.split("\\.").length > 3) {
					tar.put("evt", getItemEvent(targetPath, tar));
				}
				if (isVersionified(targetPath)) {
					tar.put("versionify", true);
					tar.put("version", getItemVersion(targetPath));
				}
				if (getItemLine(targetPath, tar) != null) {
					tar.put("line", getItemLine(targetPath, tar));
				}
				tar.put("crf", getItemVersion(targetPath));
				tar.put("group", getItemGroup(targetPath));
				targets.put(tar);
				// Rule properties
				object.put("targets", targets);
				object.put("oid", rule.getOid());
				object.put("actions", ruleActions);

				if (!rule.getDescription().isEmpty()) {
					object.put("name", rule.getDescription());
				} else {
					object.put("name", rule.getName());
				}
				object.put("expression", rule.getExpression().getValue());
				response.getWriter().write(object.toString());
			}

		} catch (Exception ex) {
			response.sendError(500, ex.getMessage());
		} finally {
			writer.flush();
			writer.close();
		}
	}

	private JSONArray getStudyEvents(StudyBean study, DataSource datasource) throws Exception {

		JSONArray events = new JSONArray();

		StudyEventDefinitionDAO eventDAO = new StudyEventDefinitionDAO(datasource);
		List<StudyEventDefinitionBean> studyEvents = eventDAO.findAllByStudy(study);
		for (StudyEventDefinitionBean evt : studyEvents) {
			if (!evt.getStatus().equals(Status.DELETED)) {
				JSONObject obj = new JSONObject();
				obj.put("id", evt.getId());
				obj.put("oid", evt.getOid());
				obj.put("name", evt.getName());
				obj.put("ordinal", evt.getOrdinal());
				obj.put("description", evt.getDescription());
				JSONArray crfs = getStudyEventCRFs(evt, study, datasource);
				obj.put("crfs", crfs);
				events.put(obj);
			}
		}
		return events;
	}

	private JSONArray getStudyEventCRFs(StudyEventDefinitionBean evt, StudyBean study, DataSource datasource)
			throws Exception {
		JSONArray crfs = new JSONArray();
		CRFDAO crfDAO = new CRFDAO(datasource);
		EventDefinitionCRFDAO eCrfDAO = new EventDefinitionCRFDAO(datasource);
		List<EventDefinitionCRFBean> eventCRFs = (List<EventDefinitionCRFBean>) eCrfDAO
				.findAllActiveByEventDefinitionId(study, evt.getId());
		for (EventDefinitionCRFBean crf : eventCRFs) {
			JSONObject obj = new JSONObject();
			CRFBean cf = (CRFBean) crfDAO.findByPK(crf.getCrfId());
			if (!cf.getStatus().equals(Status.DELETED)) {
				obj.put("id", cf.getId());
				obj.put("oid", cf.getOid());
				obj.put("name", cf.getName());
				obj.put("version", cf.getVersionNumber());
				JSONArray versions = getCRFVersions(cf, datasource);
				if (versions.length() > 0) {
					obj.put("versions", versions);
					crfs.put(obj);
				}
			}
		}
		return crfs;
	}

	private JSONArray getCRFVersions(CRFBean cf, DataSource datasource) throws Exception {
		JSONArray versions = new JSONArray();
		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(datasource);
		List<CRFVersionBean> vers = (List<CRFVersionBean>) crfVersionDAO.findAllByCRFId(cf.getId());
		for (CRFVersionBean ver : vers) {
			if (!ver.getStatus().equals(Status.DELETED)) {
				JSONObject obj = new JSONObject();
				obj.put("id", ver.getId());
				obj.put("oid", ver.getOid());
				obj.put("name", ver.getName());
				JSONArray items = getCRFVersionItems(ver, datasource);
				obj.put("items", items);
				versions.put(obj);
			}
		}
		return versions;
	}

	private JSONArray getCRFVersionItems(CRFVersionBean crfVersion, DataSource datasource) throws Exception {
		JSONArray items = new JSONArray();
		ItemDAO crfDAO = new ItemDAO(datasource);
		ItemGroupDAO itemGroupDAO = new ItemGroupDAO(datasource);
		ItemGroupMetadataDAO itemGroupMetaDAO = new ItemGroupMetadataDAO(datasource);
		ItemFormMetadataDAO itemFormMetaDataDAO = new ItemFormMetadataDAO(datasource);
		List<ItemBean> crfItems = (List<ItemBean>) crfDAO.findAllItemsByVersionId(crfVersion.getId());
		for (ItemBean item : crfItems) {
			JSONObject obj = new JSONObject();
			// Item group
			ItemGroupBean itemGroup = itemGroupDAO.findByItemAndCRFVersion(item, crfVersion);
			// Form meta data
			ItemFormMetadataBean itemFormMetaData = itemFormMetaDataDAO.findByItemIdAndCRFVersionId(item.getId(),
					crfVersion.getId());
			obj.put("id", item.getId());
			obj.put("oid", item.getOid());
			obj.put("name", item.getName());
			obj.put("group", itemGroup.getOid());
			obj.put("type", item.getDataType().getName());
			obj.put("description", item.getDescription());
			obj.put("ordinal", itemFormMetaData.getOrdinal());
			// Repeat item?
			ItemGroupMetadataBean itemGroupMeta = (ItemGroupMetadataBean) itemGroupMetaDAO.findByItemAndCrfVersion(
					item.getId(), crfVersion.getId());
			if (itemGroupMeta.isRepeatingGroup()) {
				obj.put("repeat", true);
			}
			items.put(obj);
		}
		return items;
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
			oid = oid + "_" + new Random().nextInt(110 - 10 + 1) + 10;
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
				oid = oid.substring(0, oid.length() - 1) + (Integer.valueOf(matcher.group(0)) + 1);
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

	private String getItemEvent(String targetPath, JSONObject tar) throws JSONException {
		String[] preds = targetPath.split("\\.");
		if (preds.length == 4) {
			tar.put("eventify", true);
			return preds[0];
		} else {
			return targetPath;
		}
	}

	private String getItemLine(String targetPath, JSONObject tar) throws JSONException {
		Pattern pattern = Pattern.compile("(?<=\\[)(\\w+)(?=\\])");
		Matcher matcher = pattern.matcher(targetPath);
		if (matcher.find()) {
			tar.put("linefy", true);
			return matcher.group(0);
		}
		return null;
	}

	private boolean isVersionified(String targetPath) {
		String crf = "";
		String[] preds = targetPath.split("\\.");
		if (preds.length == 3) {
			crf = preds[0];
		} else if (preds.length > 3) {
			crf = preds[preds.length - 3];
		}
		Pattern pattern = Pattern.compile("V\\d+$");
		Matcher matcher = pattern.matcher(crf);
		return matcher.find();
	}

	private String getItemVersion(String targetPath) {
		String[] preds = targetPath.split("\\.");
		if (preds.length == 3) {
			return preds[0];
		} else if (preds.length > 3) {
			return preds[preds.length - 3];
		} else {
			return targetPath;
		}
	}

	private String getItemGroup(String targetPath) throws JSONException {
		targetPath = targetPath.replaceAll("\\[\\w+\\]", "").trim();
		String[] preds = targetPath.split("\\.");
		if (preds.length == 2) {
			return preds[0];
		} else if (preds.length > 2) {
			return preds[preds.length - 2];
		} else {
			return targetPath;
		}
	}
}
