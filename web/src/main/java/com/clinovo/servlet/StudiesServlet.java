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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
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

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class StudiesServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String action = request.getParameter("action");
		PrintWriter writer = response.getWriter();

		try {

			if ("fetch".equals(action)) {

				DataSource datasource = SpringServletAccess.getApplicationContext(this.getServletContext()).getBean(
						DataSource.class);

				JSONArray studies = new JSONArray();

				StudyDAO studyDAO = new StudyDAO(datasource);

				// Get studies DESIGN and AVAILABLE
				List<StudyBean> pendingStudies = (List<StudyBean>) studyDAO.findAllByStatus(Status.PENDING);
				List<StudyBean> availableStudies = (List<StudyBean>) studyDAO.findAllByStatus(Status.AVAILABLE);

				// Merge
				availableStudies.addAll(pendingStudies);

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
					rTarget.setTextContent(rTargets.getString(x));

					// Rule target children
					rAssigment.appendChild(rTarget);

					for (int r = 0; r < actions.length(); r++) {

						// Rule Def
						Element ruleDef = document.createElement("RuleDef");

						// attributes
						ruleDef.setAttribute("Name", rule.getString("name"));
						ruleDef.setAttribute("OID", generateOID(rule.getString("expression"), currentCount));

						// Increment the count
						currentCount = currentCount + 1;

						Element expr = document.createElement("Expression");
						expr.setTextContent(rule.getString("expression"));

						Element description = document.createElement("Description");
						description.setTextContent(rule.getString("name"));

						// desc comes first
						ruleDef.appendChild(description);
						ruleDef.appendChild(expr);

						// Rule Ref
						Element ref = document.createElement("RuleRef");
						ref.setAttribute("OID", ruleDef.getAttribute("OID"));
						
						List<Element> acts = createAction(actions.getString(r), rule, document);
						
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

				RuleDao dao = SpringServletAccess.getApplicationContext(this.getServletContext()).getBean(RuleDao.class);
				RuleSetRuleDao rDao = SpringServletAccess.getApplicationContext(this.getServletContext()).getBean(RuleSetRuleDao.class);

				JSONObject object = new JSONObject();
				RuleBean rule = dao.findById(Integer.parseInt(request.getParameter("id")));

				JSONArray ruleActions = new JSONArray();
				
				RuleSetRuleBean ruleSetRule = rDao.findById(Integer.parseInt(request.getParameter("rId")));

				try {

					JSONObject act = new JSONObject();

					act.put("type", ruleSetRule.getActions().get(0).getActionType());
					act.put("select", ruleSetRule.getActions().get(0).getExpressionEvaluatesTo());

					object.put("evaluatesTo", ruleSetRule.getActions().get(0).getExpressionEvaluatesTo());

					if (ruleSetRule.getActions().get(0).getActionType().equals(ActionType.EMAIL)) {

						EmailActionBean emailAction = (EmailActionBean) ruleSetRule.getActions().get(0);

						act.put("to", emailAction.getTo());
						act.put("body", emailAction.getMessage());

					} else if (ruleSetRule.getActions().get(0).getActionType().equals(ActionType.FILE_DISCREPANCY_NOTE)) {

						DiscrepancyNoteActionBean discrepancyAction = (DiscrepancyNoteActionBean) ruleSetRule.getActions().get(0);

						act.put("type", "discrepancy");
						act.put("message", discrepancyAction.getMessage());
						
					} else if (ruleSetRule.getActions().get(0).getActionType().equals(ActionType.INSERT)) {
						
						InsertActionBean insertAction = (InsertActionBean) ruleSetRule.getActions().get(0);
						
						act.put("type","insert");
						act.put("message", insertAction.getCuratedMessage());
						
						// Destinations
						JSONArray destinations = new JSONArray();
						for (int x = 0; x < insertAction.getProperties().size(); x++) {
							
							JSONObject dest = new JSONObject();
							PropertyBean bean = insertAction.getProperties().get(x);
							
							dest.put("id", bean.getId());
							dest.put("oid", bean.getOid());
							dest.put("value", bean.getValue());
							
							destinations.put(dest);
						}
						
						act.put("destinations", destinations);
						
					} else if (ruleSetRule.getActions().get(0).getActionType().equals(ActionType.SHOW) || ruleSetRule.getActions().get(0).getActionType().equals(ActionType.HIDE)) {
						
						act.put("type", "showHide");
						
						for (int x = 0; x < ruleSetRule.getActions().size(); x++) {
							
							RuleActionBean vAction = ruleSetRule.getActions().get(x);
							
							if (vAction.getActionType().equals(ActionType.SHOW)) {
								
								ShowActionBean showAction = (ShowActionBean) ruleSetRule.getActions().get(x);
								
								act.put("type", "showHide");
								act.put("message", showAction.getMessage());
								act.put("show", showAction.getExpressionEvaluatesTo());
								
								object.put("evaluatesTo", showAction.getExpressionEvaluatesTo());
								
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
				object.put("di", rule.getRuleSetRules().get(0).getActions().get(0).getRuleActionRun().getImportDataEntry());
				object.put("dde", rule.getRuleSetRules().get(0).getActions().get(0).getRuleActionRun().getDoubleDataEntry());
				object.put("ide", rule.getRuleSetRules().get(0).getActions().get(0).getRuleActionRun().getInitialDataEntry());
				object.put("ae", rule.getRuleSetRules().get(0).getActions().get(0).getRuleActionRun().getAdministrativeDataEntry());
				
				// Targets
				JSONArray targets = new JSONArray();
				targets.put(rule.getRuleSetRules().get(0).getRuleSetBean().getOriginalTarget().getValue());
				
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

		List<StudyEventDefinitionBean> studyEvents = eventDAO.findAllActiveByStudyId(study.getId());

		for (StudyEventDefinitionBean evt : studyEvents) {

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

		return events;

	}

	private JSONArray getStudyEventCRFs(StudyEventDefinitionBean evt, StudyBean study, DataSource datasource) throws Exception {

		JSONArray crfs = new JSONArray();

		CRFDAO crfDAO = new CRFDAO(datasource);
		EventDefinitionCRFDAO eCrfDAO = new EventDefinitionCRFDAO(datasource);

		List<EventDefinitionCRFBean> eventCRFs = (List<EventDefinitionCRFBean>) eCrfDAO.findAllByDefinition(study,
				evt.getId());

		for (EventDefinitionCRFBean crf : eventCRFs) {

			JSONObject obj = new JSONObject();

			CRFBean cf = (CRFBean) crfDAO.findByPK(crf.getCrfId());
			
			if (!cf.getStatus().equals(Status.DELETED)) {
				
				obj.put("id", cf.getId());
				obj.put("oid", cf.getOid());
				obj.put("name", cf.getName());
				obj.put("version", cf.getVersionNumber());

				JSONArray versions = getCRFVersions(cf, datasource);

				obj.put("versions", versions);

				crfs.put(obj);
			}
		}

		return crfs;
	}

	private JSONArray getCRFVersions(CRFBean cf, DataSource datasource) throws Exception {

		JSONArray versions = new JSONArray();

		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(datasource);
		List<CRFVersionBean> vers = (List<CRFVersionBean>) crfVersionDAO.findAllActiveByCRF(cf.getId());

		for (CRFVersionBean ver : vers) {

			JSONObject obj = new JSONObject();

			obj.put("id", ver.getId());
			obj.put("oid", ver.getOid());
			obj.put("name", ver.getName());

			JSONArray items = getCRFVersionItems(ver, datasource);

			obj.put("items", items);

			versions.put(obj);

		}

		return versions;
	}

	private JSONArray getCRFVersionItems(CRFVersionBean cf, DataSource datasource) throws Exception {

		JSONArray items = new JSONArray();
		ItemDAO crfDAO = new ItemDAO(datasource);
		ItemGroupDAO itemGroupDAO = new ItemGroupDAO(datasource);
		ItemFormMetadataDAO itemFormMetaDataDAO = new ItemFormMetadataDAO(datasource);

		List<ItemBean> crfItems = (List<ItemBean>) crfDAO.findAllItemsByVersionId(cf.getId());

		for (ItemBean item : crfItems) {

			JSONObject obj = new JSONObject();

			// Item group
			ItemGroupBean itemGroup = itemGroupDAO.findByItemAndCRFVersion(item, cf);

			// Form meta data
			ItemFormMetadataBean itemFormMetaData = itemFormMetaDataDAO.findByItemIdAndCRFVersionId(item.getId(),
					cf.getId());

			obj.put("id", item.getId());
			obj.put("oid", item.getOid());
			obj.put("name", item.getName());
			obj.put("group", itemGroup.getOid());
			obj.put("type", item.getDataType().getName());
			obj.put("description", item.getDescription());
			obj.put("ordinal", itemFormMetaData.getOrdinal());

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

		if (oid != null && !oid.isEmpty()) {

			List<String> oids = ruleDAO.findRuleOIDs();

			if (oids != null && !oids.isEmpty()) {
				Collections.sort(oids, new Comparator<String>() {

					// Note that this comparison does not strictly adhere to the Object.equals protocol
					public int compare(String predicate1, String predicate2) {

						if (predicate1.length() > predicate2.length()) {
							return 1;
						} else if (predicate1.length() < predicate2.length()) {
							return -1;
						} else {
							return predicate1.compareTo(predicate2);
						}
					}
				});

				Pattern dPattern = Pattern.compile("(\\d+)");
				Matcher dMatcher = dPattern.matcher(oids.get(oids.size() - 1));

				if (dMatcher.find()) {

					String _num = dMatcher.group(0);
					if (_num.contains("0")) {
						oid = oid + "_0" + (Integer.valueOf(_num) + x);
					} else {
						oid = oid + "_" + (Integer.valueOf(_num) + x);
					}
				}
			} else {

				oid = oid + "_0" + x;
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
			action.setAttribute("IfExpressionEvaluates", rule.getString("evaluatesTo"));

			action.appendChild(run);
			action.appendChild(discrepancyText);
			
			actions.add(action);

		} else if (act.getString("type").equalsIgnoreCase("email")) {
			
			run.setAttribute("Batch", "true");
			
			// Message element
			Element message = document.createElement("Message");
			message.setTextContent(act.getString("body"));

			Element action = document.createElement("EmailAction");
			action.setAttribute("IfExpressionEvaluates", rule.getString("evaluatesTo"));

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
			showAction.setAttribute("IfExpressionEvaluates", rule.getString("evaluatesTo"));
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
			
			Boolean eval = Boolean.valueOf(rule.getString("evaluatesTo"));
			
			// Hide action opposite of show
			if (eval) {
				hideAction.setAttribute("IfExpressionEvaluates", Boolean.FALSE.toString());
			} else {
				hideAction.setAttribute("IfExpressionEvaluates", Boolean.TRUE.toString());
			}
			
			actions.add(hideAction);

		} else if (act.getString("type").equalsIgnoreCase("insert")) {
			
			run.setAttribute("Batch", "false");
			
			Element action = document.createElement("InsertAction");
			action.setAttribute("IfExpressionEvaluates", rule.getString("evaluatesTo"));
			
			Element msg = document.createElement("Message");
			msg.setTextContent(act.getString("message"));
			
			JSONArray destinations = act.getJSONArray("destinations");
			action.appendChild(run);

			for (int x = 0; x < destinations.length(); x++) {
				
				JSONObject dest = destinations.getJSONObject(x);
				// Target
				Element destProp = document.createElement("DestinationProperty");

				destProp.setAttribute("OID", dest.getString("oid"));
				destProp.setAttribute("Value", dest.getString("value"));

				action.appendChild(destProp);
			}
			
			actions.add(action);
		}

		return actions;
	}
}
