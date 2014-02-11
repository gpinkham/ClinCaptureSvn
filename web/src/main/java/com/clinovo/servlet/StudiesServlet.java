package com.clinovo.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
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
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.ActionType;
import org.akaza.openclinica.domain.rule.action.DiscrepancyNoteActionBean;
import org.akaza.openclinica.domain.rule.action.EmailActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@SuppressWarnings("serial")
public class StudiesServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.sendRedirect(request.getContextPath() + "/designer/rule.html?action=edit&id="
				+ request.getParameter("ruleSetId"));
	}

	@Override
	@SuppressWarnings({ "unchecked" })
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
						ruleDef.setAttribute("Name", "RS_GEN_RULE");
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
						ref.appendChild(createAction(actions.getString(r), rule, document));

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

				RuleSetDao dao = SpringServletAccess.getApplicationContext(this.getServletContext()).getBean(
						RuleSetDao.class);

				JSONObject object = new JSONObject();
				RuleSetBean rsb = dao.findById(Integer.parseInt(request.getParameter("id")));

				// Targets
				JSONArray targets = new JSONArray();
				targets.put(rsb.getOriginalTarget().getValue());

				if (rsb != null) {

					JSONArray ruleActions = new JSONArray();
					for (RuleSetRuleBean rb : rsb.getRuleSetRules()) {

						RuleBean rule = rb.getRuleBean();
						RuleActionBean at = rb.getActions().get(0);

						try {

							JSONObject act = new JSONObject();

							act.put("select", at.getExpressionEvaluatesTo());
							act.put("type", at.getActionType());
							object.put("evaluateTo", at.getExpressionEvaluatesTo());

							if (at.getActionType().equals(ActionType.EMAIL)) {

								EmailActionBean emailAction = (EmailActionBean) at;
								
								act.put("to", emailAction.getTo());
								act.put("body", emailAction.getMessage());

							} else if (at.getActionType().equals(ActionType.FILE_DISCREPANCY_NOTE)) {

								DiscrepancyNoteActionBean discrepancyAction = (DiscrepancyNoteActionBean) at;
								
								act.put("type", "discrepancy");
								act.put("message", discrepancyAction.getMessage());
							}
							
							ruleActions.put(act);
							
							object.put("di", at.getRuleActionRun().getImportDataEntry());
							object.put("dde", at.getRuleActionRun().getDoubleDataEntry());
							object.put("ide", at.getRuleActionRun().getInitialDataEntry());
							object.put("ae", at.getRuleActionRun().getAdministrativeDataEntry());

							// Rule properties
							object.put("targets", targets);
							object.put("oid", rule.getOid());
							object.put("actions", ruleActions);
							object.put("name", rule.getDescription());
							object.put("expression", rule.getExpression().getValue());

						} catch (JSONException e) {
							response.sendError(500, e.getMessage());
						}
					}
				}

				response.getWriter().write(object.toString());
			}

		} catch (Exception ex) {

			response.sendError(500, ex.getMessage());

		} finally {

			writer.flush();
			writer.close();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JSONArray getStudyEvents(StudyBean study, DataSource datasource) throws Exception {

		JSONArray events = new JSONArray();

		StudyEventDefinitionDAO eventDAO = new StudyEventDefinitionDAO(datasource);

		List<StudyEventDefinitionBean> studyEvents = new ArrayList<StudyEventDefinitionBean>();

		studyEvents = eventDAO.findAllActiveByStudyId(study.getId());

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
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

			obj.put("id", cf.getId());
			obj.put("oid", cf.getOid());
			obj.put("name", cf.getName());
			obj.put("version", cf.getVersionNumber());

			JSONArray items = getCRFItems(cf, datasource);

			obj.put("items", items);

			crfs.put(obj);

		}

		return crfs;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JSONArray getCRFItems(CRFBean crf, DataSource datasource) throws Exception {

		JSONArray items = new JSONArray();
		ItemDAO crfDAO = new ItemDAO(datasource);
		ItemGroupDAO itemGroupDAO = new ItemGroupDAO(datasource);
		ItemFormMetadataDAO itemMetaDataDAO = new ItemFormMetadataDAO(datasource);

		List<ItemBean> crfItems = (List<ItemBean>) crfDAO.findAllActiveByCRF(crf);

		for (ItemBean item : crfItems) {

			JSONObject obj = new JSONObject();

			ItemFormMetadataBean itemMeta = itemMetaDataDAO.findByItemIdAndCRFVersionId(item.getId(), crf.getId());

			// Item group
			ItemGroupBean itemGroup = (ItemGroupBean) itemGroupDAO.findTopOneGroupBySectionId(itemMeta.getSectionId());

			obj.put("id", item.getId());
			obj.put("oid", item.getOid());
			obj.put("name", item.getName());
			obj.put("group", itemGroup.getOid());
			obj.put("ordinal", itemMeta.getOrdinal());
			obj.put("type", item.getDataType().getName());
			obj.put("description", item.getDescription());

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

	private Element createAction(String action, JSONObject rule, Document document) throws Exception {

		Element rAction = null;
		JSONObject act = new JSONObject(action);

		// Run
		Element run = document.createElement("Run");

		run.setAttribute("Batch", "true");
		run.setAttribute("ImportDataEntry", rule.getString("di"));
		run.setAttribute("InitialDataEntry", rule.getString("ide"));
		run.setAttribute("DoubleDataEntry", rule.getString("dde"));
		run.setAttribute("AdministrativeDataEntry", rule.getString("ae"));

		if (act.getString("type").equalsIgnoreCase("discrepancy")) {

			Element discrepancyText = document.createElement("Message");
			discrepancyText.setTextContent(act.getString("message"));

			rAction = document.createElement("DiscrepancyNoteAction");
			rAction.setAttribute("IfExpressionEvaluates", rule.getString("evaluatesTo"));

			rAction.appendChild(run);
			rAction.appendChild(discrepancyText);

		} else if (act.getString("type").equalsIgnoreCase("email")) {

			// Message element
			Element message = document.createElement("Message");
			message.setTextContent(act.getString("body"));

			rAction = document.createElement("EmailAction");
			rAction.setAttribute("IfExpressionEvaluates", rule.getString("evaluatesTo"));

			// To element
			Element to = document.createElement("To");
			to.setTextContent(act.getString("to"));

			rAction.appendChild(run);

			// Email props
			rAction.appendChild(message);
			rAction.appendChild(to);

		} else if (act.getString("type").equalsIgnoreCase("hide")) {

			// Message element
			Element message = document.createElement("Message");
			message.setTextContent(rule.getString("message"));

			rAction = document.createElement("HideAction");
			rAction.setAttribute("IfExpressionEvaluates", rule.getString("evaluatesTo"));

			JSONArray targets = rule.getJSONArray("destinationProperty");
			rAction.appendChild(run);

			for (int x = 0; x < targets.length(); x++) {

				// Target
				Element destProp = document.createElement("DestinationProperty");
				destProp.setAttribute("OID", targets.getJSONObject(x).getString("oid"));

				rAction.appendChild(destProp);
			}

			rAction.appendChild(message);

		} else if (act.getString("type").equalsIgnoreCase("show")) {

			// Message element
			Element message = document.createElement("Message");
			message.setTextContent(rule.getString("message"));

			rAction = document.createElement("ShowAction");
			rAction.setAttribute("IfExpressionEvaluates", rule.getString("evaluatesTo"));

			JSONArray targets = rule.getJSONArray("destinationProperty");
			rAction.appendChild(run);

			for (int x = 0; x < targets.length(); x++) {

				// Target
				Element destProp = document.createElement("DestinationProperty");
				destProp.setAttribute("OID", targets.getJSONObject(x).getString("oid"));

				rAction.appendChild(destProp);
			}

			rAction.appendChild(message);

		} else if (act.getString("type").equalsIgnoreCase("insert")) {

			rAction = document.createElement("InsertAction");
			rAction.setAttribute("IfExpressionEvaluates", rule.getString("evaluatesTo"));

			JSONArray targets = rule.getJSONArray("iDestinationProperty");
			rAction.appendChild(run);

			for (int x = 0; x < targets.length(); x++) {

				// Target
				Element destProp = document.createElement("DestinationProperty");

				destProp.setAttribute("OID", targets.getJSONObject(x).getString("oid"));
				destProp.setAttribute("Value", targets.getJSONObject(x).getString("value"));

				rAction.appendChild(destProp);
			}
		}

		return rAction;
	}
}