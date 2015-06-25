/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the Lesser GNU General Public License along with this program.
 * \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.view;

/**
 * @author jxu
 *
 *         Provides a type-safe enumeration for JSP Page.
 */
public final class Page {
	/**
	 * The filename of the JSP page.
	 */
	private String fileName;

	/**
	 * The title of the JSP page.
	 */
	private final String title;

	/**
	 * Page for logging in.
	 */
	public static final Page LOGIN = new Page("/WEB-INF/jsp/login/login.jsp", "OpenClinica Login");
	public static final Page LOGIN_USER_ACCOUNT_DELETED = new Page("/WEB-INF/jsp/login/login.jsp"
			+ "?action=userAccountDeleted", "Unsuccessful Login Due to Account Deletion");
	public static final Page ENTERPRISE = new Page("/WEB-INF/jsp/login/enterprise.jsp", "OpenClinica Enterprise");
	/**
	 * Page for logging out.
	 */
	public static final Page LOGOUT = new Page("/WEB-INF/jsp/login/logout.jsp", "OpenClinica Log Out");

	/**
	 * Page to show the main menu of openclinica.
	 */
	public static final Page MENU = new Page("/WEB-INF/jsp/menu.jsp", "Welcome to OpenClinica");
	public static final Page MENU_SERVLET = new Page("/MainMenu", "Welcome to OpenClinica Main Servlet");

	// YW 06-25-2007 <<
	/**
	 * Page for reset password when password is expired.
	 */
	public static final Page RESET_PASSWORD = new Page("/WEB-INF/jsp/login/resetPassword.jsp",
			"Reset your expired password");
	// YW >>

	/**
	 * Page for user to update profile.
	 */
	public static final Page UPDATE_PROFILE = new Page("/WEB-INF/jsp/login/updateProfile.jsp", "Update your profile");

	/**
	 * Page for user to confirm inputs of updating profile.
	 */
	public static final Page UPDATE_PROFILE_CONFIRM = new Page("/WEB-INF/jsp/login/updateProfileConfirm.jsp",
			"Confirm your profile");

	/**
	 * Page for user to request password.
	 */
	public static final Page CONTACT = new Page("/WEB-INF/jsp/login/contact.jsp", "Contact Form");

	/**
	 * Page for user to request password.
	 */
	public static final Page REQUEST_PWD = new Page("/WEB-INF/jsp/login/requestPassword.jsp", "Request passwod form");

	/**
	 * Page for changing study.
	 */
	public static final Page CHANGE_STUDY = new Page("/WEB-INF/jsp/login/changeStudy.jsp", "Change Study");
	public static final Page CHANGE_STUDY_CONFIRM = new Page("/WEB-INF/jsp/login/changeStudyConfirm.jsp",
			"Change Study Confirm");

	/**
	 * Page for user to confirm inputs of requesting password.
	 */
	public static final Page REQUEST_PWD_CONFIRM = new Page("/WEB-INF/jsp/login/requestPasswordConfirm.jsp",
			"Request passwod Confirm");

	/**
	 * Page for creating a user account.
	 */
	public static final Page CREATE_ACCOUNT = new Page("/WEB-INF/jsp/admin/createuseraccount.jsp", "Create an account");

	/**
	 * Page for creating a user account.
	 */
	public static final Page TEST_OBJECT = new Page("/WEB-INF/jsp/admin/createtestObject.jsp", "Create an account");

	/**
	 * Page for editing a user account, and confirmation page.
	 */
	public static final Page EDIT_ACCOUNT = new Page("/WEB-INF/jsp/admin/edituseraccount.jsp", "Edit an account");
	public static final Page EDIT_ACCOUNT_CONFIRM = new Page("/WEB-INF/jsp/admin/edituseraccountconfirm.jsp",
			"Edit an account");

	/**
	 * Page for viewing all user accounts (for admin).
	 */
	public static final Page LIST_USER_ACCOUNTS = new Page("/WEB-INF/jsp/admin/listuseraccounts.jsp",
			"List user accounts");
	public static final Page LIST_USER_ACCOUNTS_SERVLET = new Page("/ListUserAccounts", "List user accounts");

	/**
	 * Page for viewing a single user account (for admin).
	 */
	public static final Page VIEW_USER_ACCOUNT = new Page("/WEB-INF/jsp/admin/viewuseraccount.jsp", "View user account");
	public static final Page VIEW_USER_ACCOUNT_SERVLET = new Page("/ViewUserAccount", "View user account servlet");

	public static final Page CONFIGURATION = new Page("/WEB-INF/jsp/admin/configuration.jsp", "Configuration");

	public static final Page CONFIGURATION_PASSWORD_REQUIREMENTS = new Page(
			"/WEB-INF/jsp/admin/configurationPasswordRequirements.jsp", "Configuration");

	public static final Page SYSTEM_STATUS = new Page("/WEB-INF/jsp/admin/systemStatus.jsp", "System Status");

	/**
	 * Page for creating a study.
	 */
	public static final Page CREATE_STUDY1 = new Page("/WEB-INF/jsp/managestudy/createStudy1.jsp",
			"Create a new Study first section");
	public static final Page CREATE_STUDY2 = new Page("/WEB-INF/jsp/managestudy/createStudy2.jsp",
			"Create a new Study second section");
	public static final Page CREATE_STUDY3 = new Page("/WEB-INF/jsp/managestudy/createStudy3.jsp",
			"Create a new Study third section");
	public static final Page CREATE_STUDY4 = new Page("/WEB-INF/jsp/managestudy/createStudy4.jsp",
			"Create a new Study forth section");
	public static final Page CREATE_STUDY5 = new Page("/WEB-INF/jsp/managestudy/createStudy5.jsp",
			"Create a new Study fifth section");
	public static final Page CREATE_STUDY6 = new Page("/WEB-INF/jsp/managestudy/createStudy6.jsp",
			"Create a new Study sixth section");
	public static final Page CREATE_STUDY7 = new Page("/WEB-INF/jsp/managestudy/createStudy7.jsp",
			"Create a new Study seventh section");
	public static final Page CREATE_STUDY8 = new Page("/WEB-INF/jsp/managestudy/createStudy8.jsp",
			"Create a new Study last section");

	/**
	 * Page for confirming a new study.
	 */
	public static final Page STUDY_CREATE_CONFIRM = new Page("/WEB-INF/jsp/managestudy/studyCreateConfirm.jsp",
			"Confirm a new Study");

	/**
	 * Page for update a study.
	 */
	public static final Page UPDATE_STUDY1 = new Page("/WEB-INF/jsp/managestudy/updateStudy1.jsp",
			"Update a Study first section");
	public static final Page UPDATE_STUDY2 = new Page("/WEB-INF/jsp/managestudy/updateStudy2.jsp",
			"Update a Study second section");
	public static final Page UPDATE_STUDY3 = new Page("/WEB-INF/jsp/managestudy/updateStudy3.jsp",
			"Update a Study third section");
	public static final Page UPDATE_STUDY4 = new Page("/WEB-INF/jsp/managestudy/updateStudy4.jsp",
			"Update a Study forth section");
	public static final Page UPDATE_STUDY5 = new Page("/WEB-INF/jsp/managestudy/updateStudy5.jsp",
			"Update a Study fifth section");
	public static final Page UPDATE_STUDY6 = new Page("/WEB-INF/jsp/managestudy/updateStudy6.jsp",
			"Update a Study sixth section");
	public static final Page UPDATE_STUDY7 = new Page("/WEB-INF/jsp/managestudy/updateStudy7.jsp",
			"Update a Study seventh section");
	public static final Page UPDATE_STUDY8 = new Page("/WEB-INF/jsp/managestudy/updateStudy8.jsp",
			"Update a Study last section");

	public static final Page LIST_STUDY_SUBJECTS = new Page("/WEB-INF/jsp/managestudy/findSubjects.jsp",
			"List Study Subjects");
	public static final Page LIST_STUDY_SUBJECTS_SERVLET = new Page("/ListStudySubjects", "List Study Subjects");

	public static final Page AUTO_CLOSE_PAGE = new Page("/WEB-INF/jsp/autoClosePage.jsp", "Auto Close Page");

	public static final Page UPDATE_STUDY_SERVLET_NEW = new Page("/UpdateStudyNew", "Update a Study");
	public static final Page UPDATE_STUDY_NEW = new Page("/WEB-INF/jsp/managestudy/updateStudyNew.jsp",
			"Update a Study");

	public static final Page VIEW_STUDY = new Page("/WEB-INF/jsp/admin/viewStudy.jsp", "View study");
	public static final Page VIEW_FULL_STUDY = new Page("/WEB-INF/jsp/admin/viewFullStudy.jsp", "View full study");

	public static final Page REMOVE_CRF = new Page("/WEB-INF/jsp/admin/removeCRF.jsp", "Remove a CRF");
	public static final Page RESTORE_CRF = new Page("/WEB-INF/jsp/admin/restoreCRF.jsp", "Restore a CRF");
	/**
	 * Page for confirming an existing study.
	 */
	public static final Page STUDY_UPDATE_CONFIRM = new Page("/WEB-INF/jsp/managestudy/studyUpdateConfirm.jsp",
			"Confirm a Study");

	/**
	 * Page for creating a new sub study.
	 */
	public static final Page CREATE_SUB_STUDY = new Page("/WEB-INF/jsp/managestudy/createSubStudy.jsp",
			"Create a sub Study");

	/**
	 * Page for confirming a new sub study.
	 */
	public static final Page CONFIRM_CREATE_SUB_STUDY = new Page("/WEB-INF/jsp/managestudy/createSubStudyConfirm.jsp",
			"Confirm a new site");

	/**
	 * Page for confirming a new sub study.
	 */
	public static final Page CONFIRM_UPDATE_SUB_STUDY = new Page("/WEB-INF/jsp/managestudy/updateSubStudyConfirm.jsp",
			"Confirm a site");

	/**
	 * Page for updating a new sub study.
	 */
	public static final Page UPDATE_SUB_STUDY = new Page("/WEB-INF/jsp/managestudy/updateSubStudy.jsp",
			"Update a sub Study");

	/**
	 * Page for viewing a new sub study.
	 */
	public static final Page VIEW_SITE = new Page("/WEB-INF/jsp/managestudy/viewSite.jsp", "View a sub Study");
	public static final Page VIEW_SITE_SERVLET = new Page("/ViewSite", "View a sub Study");

	public static final Page REMOVE_STUDY = new Page("/WEB-INF/jsp/admin/removeStudy.jsp", "Remove a Study");

	/**
	 * Page for restoring a study.
	 */
	public static final Page RESTORE_STUDY = new Page("/WEB-INF/jsp/admin/restoreStudy.jsp", "Restore a Study");
	/**
	 * Page for removing a sub study.
	 */
	public static final Page REMOVE_SITE = new Page("/WEB-INF/jsp/managestudy/removeSite.jsp", "View a sub Study");

	/**
	 * Page for restoring a sub study.
	 */
	public static final Page RESTORE_SITE = new Page("/WEB-INF/jsp/managestudy/restoreSite.jsp", "Restore a sub Study");
	/**
	 * Page for editing a study user role.
	 */
	public static final Page EDIT_STUDY_USER_ROLE = new Page("/WEB-INF/jsp/admin/editstudyuserrole.jsp",
			"Edit Study User Role");

	/**
	 * Page for view all users of a study and its sites.
	 */
	public static final Page STUDY_USER_LIST = new Page("/WEB-INF/jsp/managestudy/studyUserList.jsp",
			"View Study Users");
	/**
	 * Page for view all studies.
	 */
	public static final Page STUDY_LIST = new Page("/WEB-INF/jsp/managestudy/studyList.jsp", "View All Studies");

	/**
	 * Page for view all studies.
	 */
	public static final Page STUDY_LIST_SERVLET = new Page("/ListStudy", "View All Studies");

	/**
	 * Page for view all sites.
	 */
	public static final Page SITE_LIST = new Page("/WEB-INF/jsp/managestudy/siteList.jsp", "View All Sites");
	public static final Page SITE_LIST_SERVLET = new Page("/ListSite", "View All Sites Servlet");

	/*
	 * Page for sign study subject
	 */

	public static final Page SIGN_STUDY_SUBJECT_SERVLET = new Page("/SignStudySubject", "Sign Study Subject");
	public static final Page SIGN_STUDY_SUBJECT = new Page("/WEB-INF/jsp/managestudy/signStudySubject.jsp",
			"Sign Study Subject");

	/**
	 * Page for view all study group classes.
	 */
	public static final Page SUBJECT_GROUP_CLASS_LIST = new Page("/WEB-INF/jsp/managestudy/subjectGroupClassList.jsp",
			"View All Group Class");
	public static final Page SUBJECT_GROUP_CLASS_LIST_SERVLET = new Page("/ListSubjectGroupClass",
			"View Subject Group Class Servlet");
	public static final Page CREATE_SUBJECT_GROUP_CLASS = new Page(
			"/WEB-INF/jsp/managestudy/createSubjectGroupClass.jsp", "Create Subject Group Class");
	public static final Page CREATE_SUBJECT_GROUP_CLASS_CONFIRM = new Page(
			"/WEB-INF/jsp/managestudy/createSubjectGroupClassConfirm.jsp", "Create Subject Group Class Confirm");
	public static final Page UPDATE_SUBJECT_GROUP_CLASS = new Page(
			"/WEB-INF/jsp/managestudy/updateSubjectGroupClass.jsp", "Update Subject Group Class");
	public static final Page UPDATE_SUBJECT_GROUP_CLASS_CONFIRM = new Page(
			"/WEB-INF/jsp/managestudy/updateSubjectGroupClassConfirm.jsp", "Update Subject Group Class Confirm");
	public static final Page VIEW_SUBJECT_GROUP_CLASS = new Page("/WEB-INF/jsp/managestudy/viewSubjectGroupClass.jsp",
			"View Subject Group Class");
	public static final Page REMOVE_SUBJECT_GROUP_CLASS = new Page(
			"/WEB-INF/jsp/managestudy/removeSubjectGroupClass.jsp", "Remove Subject Group Class");
	public static final Page RESTORE_SUBJECT_GROUP_CLASS = new Page(
			"/WEB-INF/jsp/managestudy/restoreSubjectGroupClass.jsp", "Restore Subject Group Class");

	/**
	 * Page for defining a study event.
	 */
	public static final Page DEFINE_STUDY_EVENT1 = new Page("/WEB-INF/jsp/managestudy/defineStudyEvent1.jsp",
			"Define Study Event");
	public static final Page DEFINE_STUDY_EVENT2 = new Page("/WEB-INF/jsp/managestudy/defineStudyEvent2.jsp",
			"Define Study Event");
	public static final Page DEFINE_STUDY_EVENT3 = new Page("/WEB-INF/jsp/managestudy/defineStudyEvent3.jsp",
			"Define Study Event");
	public static final Page DEFINE_STUDY_EVENT_CONFIRM = new Page(
			"/WEB-INF/jsp/managestudy/defineStudyEventConfirm.jsp", "Define Study Event Confirm");

	/**
	 * Page for updating a study event definition.
	 */
	public static final Page UPDATE_EVENT_DEFINITION1 = new Page("/WEB-INF/jsp/managestudy/updateEventDefinition1.jsp",
			"Update Event Definition");
	public static final Page UPDATE_EVENT_DEFINITION2 = new Page("/WEB-INF/jsp/managestudy/updateEventDefinition2.jsp",
			"Update Event Definition");
	public static final Page UPDATE_EVENT_DEFINITION_CONFIRM = new Page(
			"/WEB-INF/jsp/managestudy/updateEventDefinitionConfirm.jsp", "Update Event Definition Confirm");

	/**
	 * Page for viewing definition.
	 */
	public static final Page VIEW_EVENT_DEFINITION = new Page("/WEB-INF/jsp/managestudy/viewEventDefinition.jsp",
			"View Event Definition");
	public static final Page VIEW_EVENT_DEFINITION_READONLY = new Page(
			"/WEB-INF/jsp/managestudy/viewEventDefinitionReadOnly.jsp", "View Event Definition");
	public static final Page VIEW_EVENT_DEFINITION_SERVLET = new Page("/ViewEventDefinition",
			"View Event Definition Servlet");

	/**
	 * Page for removing definition.
	 */
	public static final Page REMOVE_DEFINITION = new Page("/WEB-INF/jsp/managestudy/removeDefinition.jsp",
			"Remove Event Definition");
	/**
	 * Page for removing definition.
	 */
	public static final Page RESTORE_DEFINITION = new Page("/WEB-INF/jsp/managestudy/restoreDefinition.jsp",
			"Restore Event Definition");

	/**
	 * Page for locking definition.
	 */
	public static final Page LOCK_DEFINITION = new Page("/WEB-INF/jsp/managestudy/lockDefinition.jsp",
			"Lock Event Definition");
	/**
	 * Page for unlocking definition.
	 */
	public static final Page UNLOCK_DEFINITION = new Page("/WEB-INF/jsp/managestudy/unlockDefinition.jsp",
			"Unlock Event Definition");
	/**
	 * Page for listing seds.
	 */
	public static final Page STUDY_EVENT_DEFINITION_LIST = new Page(
			"/WEB-INF/jsp/managestudy/studyEventDefinitionList.jsp", "List all Event Definitions");

	/**
	 * Page to add CRFs to seds.
	 */
	public static final Page ADD_CRFTO_DEFINITION_SERVLET = new Page("/AddCRFToDefinition",
			"Add CRFs To Study Event Definition");

	/**
	 * Page for view all seds.
	 */
	public static final Page LIST_DEFINITION_SERVLET = new Page("/ListEventDefinition", "View All Definitions");

	/**
	 * Page for listing crfs.
	 */
	public static final Page CRF_LIST = new Page("/WEB-INF/jsp/admin/listCRF.jsp", "List all CRFs");
	public static final Page CRF_LIST_SERVLET = new Page("/ListCRF", "List all CRFs servlet");

	/**
	 * Page for creating crf.
	 */
	public static final Page VIEW_CRF = new Page("/WEB-INF/jsp/admin/viewCRF.jsp", "View a CRF");

	/**
	 * Page for viewing audit user activity.
	 */
	public static final Page AUDIT_USER_ACTIVITY = new Page("/WEB-INF/jsp/admin/auditUserActivity.jsp",
			"Audit User Activity");

	/**
	 * Page for viewing audit database.
	 */
	public static final Page AUDIT_DATABASE = new Page("/WEB-INF/jsp/admin/auditDatabase.jsp", "Audit Database");

	/**
	 * Page for creating crf.
	 */
	public static final Page CREATE_CRF = new Page("/WEB-INF/jsp/admin/createCRF.jsp", "Create a new CRF");

	/**
	 * Page for updating crf.
	 */
	public static final Page UPDATE_CRF = new Page("/WEB-INF/jsp/admin/updateCRF.jsp", "Update a CRF");
	public static final Page UPDATE_CRF_CONFIRM = new Page("/WEB-INF/jsp/admin/updateCRFConfirm.jsp",
			"Update a CRF Confirm");

	/**
	 * Page for creating crf confirm.
	 */
	public static final Page CREATE_CRF_CONFIRM = new Page("/WEB-INF/jsp/admin/createCRFConfirm.jsp",
			"Create a new CRF Confirm");

	/**
	 * Page for creating crf version.
	 */
	public static final Page CREATE_CRF_VERSION = new Page("/WEB-INF/jsp/admin/createCRFVersion.jsp",
			"Create a new CRF Version");
	public static final Page UPLOAD_CRF_VERSION = new Page("/WEB-INF/jsp/admin/uploadCRFVersionFile.jsp",
			"Upload a new CRF Version");

	public static final Page REMOVE_CRF_VERSION = new Page("/WEB-INF/jsp/admin/removeCRFVersion.jsp",
			"Remove CRF Version");
	public static final Page RESTORE_CRF_VERSION = new Page("/WEB-INF/jsp/admin/restoreCRFVersion.jsp",
			"Restore CRF Version");

	/**
	 * Page for creating crf data imports.
	 */
	public static final Page IMPORT_CRF_DATA = new Page("/WEB-INF/jsp/submit/import.jsp", "Import CRF Data");
	public static final Page VERIFY_IMPORT_SERVLET = new Page("/VerifyImportedCRFData",
			"Verify Imported CRF Data Servlet");
	public static final Page VERIFY_IMPORT_CRF_DATA = new Page("/WEB-INF/jsp/submit/verifyImport.jsp",
			"Verify Imported CRF Data");

	/**
	 * Page for creating rule imports.
	 */
	public static final Page IMPORT_RULES = new Page("/WEB-INF/jsp/submit/importRules.jsp", "Import Rules");
	public static final Page VIEW_RULE_SETS = new Page("/WEB-INF/jsp/submit/listRuleSets.jsp", "List Rule Assignments");
	public static final Page VIEW_RULE_SETS2 = new Page("/WEB-INF/jsp/submit/listRuleSets2.jsp",
			"List Rule Assignments");
	public static final Page VIEW_RULE_SETS_DESIGNER = new Page("/WEB-INF/jsp/submit/listRuleSetsDesigner.jsp",
			"List Rule Assignments");
	public static final Page VERIFY_RULES_IMPORT_SERVLET = new Page("/VerifyImportedRule",
			"Verify Imported Rule Servlet");
	public static final Page VERIFY_RULES_IMPORT = new Page("/WEB-INF/jsp/submit/verifyImportRule.jsp",
			"Verify Imported Rules");
	public static final Page VIEW_RULES = new Page("/WEB-INF/jsp/submit/viewRules.jsp", "View Rules");
	public static final Page VIEW_EXECUTED_RULES = new Page("/WEB-INF/jsp/submit/viewExecutedRules.jsp", "View Rules");
	public static final Page LIST_RULE_SETS_SERVLET = new Page("/ViewRuleAssignment", "List Rule Assignments");
	public static final Page REMOVE_RULE_SET = new Page("/WEB-INF/jsp/submit/removeRuleSet.jsp", "Remove RuleSet");
	public static final Page RESTORE_RULE_SET = new Page("/WEB-INF/jsp/submit/restoreRuleSet.jsp", "Remove RuleSet");
	public static final Page VIEW_EXECUTED_RULES_FROM_CRF = new Page(
			"/WEB-INF/jsp/submit/viewExecutedRulesFromCrf.jsp", "View Results");
	public static final Page VIEW_RULESET_AUDITS = new Page("/WEB-INF/jsp/submit/viewRuleSetAudits.jsp",
			"View Rule Audits");
	public static final Page TEST_RULES = new Page("/WEB-INF/jsp/submit/testRules.jsp", "Test Rules");

	/**
	 * Page for creating crf version.
	 */
	public static final Page CREATE_CRF_VERSION_SERVLET = new Page("/CreateCRFVersion",
			"Create a new CRF Version Servlet");
	/**
	 * Page for confirming crf version.
	 */
	public static final Page CREATE_CRF_VERSION_CONFIRM = new Page("/WEB-INF/jsp/admin/createCRFVersionConfirm.jsp",
			"Create a new CRF Version Confirm");
	public static final Page CREATE_CRF_VERSION_CONFIRMSQL = new Page(
			"/WEB-INF/jsp/admin/createCRFVersionConfirmSQL.jsp", "Create a new CRF Version Confirm SQL");
	public static final Page CREATE_CRF_VERSION_DONE = new Page("/WEB-INF/jsp/admin/createCRFVersionDone.jsp",
			"Create a new CRF Version Done");
	public static final Page REMOVE_CRF_VERSION_CONFIRM = new Page("/WEB-INF/jsp/admin/removeCRFVersionConfirm.jsp",
			"Remove CRF Version Confirm");
	public static final Page CREATE_CRF_VERSION_NODELETE = new Page("/WEB-INF/jsp/admin/createCRFVersionNoDelete.jsp",
			"Create a new CRF cannot delete version");
	public static final Page CREATE_CRF_VERSION_ERROR = new Page("/WEB-INF/jsp/admin/createCRFVersionError.jsp",
			"Create a new CRF error");
	public static final Page REMOVE_CRF_VERSION_DEF = new Page("/WEB-INF/jsp/admin/removeCRFVersionDef.jsp",
			"Remove CRF Version From Definition");

	public static final Page AUDIT_LOG_USER = new Page("/WEB-INF/jsp/admin/auditLogUser.jsp",
			"Audit Log display by User");
	public static final Page AUDIT_LOG_STUDY = new Page("/WEB-INF/jsp/admin/auditLogStudy.jsp",
			"Audit Log display by Study");
	public static final Page AUDIT_LOGS_STUDY = new Page("/WEB-INF/jsp/admin/studyAuditLog.jsp",
			"Audit Log display by Study");

	/**
	 * Page for extract datasets main, tbh.
	 */
	public static final Page EXTRACT_DATASETS_MAIN = new Page("/WEB-INF/jsp/extract/extractDatasetsMain.jsp",
			"Extract Datasets Main Page");

	/**
	 * Page for view all datasets, tbh.
	 */
	public static final Page VIEW_DATASETS_SERVLET = new Page("/ViewDatasets", "View Datasets Servlet");
	public static final Page VIEW_DATASETS = new Page("/WEB-INF/jsp/extract/viewDatasets.jsp", "View Datasets");
	public static final Page VIEW_EMPTY_DATASETS = new Page("/WEB-INF/jsp/extract/viewEmptyDatasets.jsp",
			"View Datasets");
	public static final Page VIEW_DATASET_DETAILS = new Page("/WEB-INF/jsp/extract/viewDatasetDetails.jsp",
			"View Dataset Details");

	public static final Page EXPORT_DATASETS = new Page("/WEB-INF/jsp/extract/exportDatasets.jsp", "Export Dataset");
	public static final Page GENERATE_DATASET_HTML = new Page("/WEB-INF/jsp/extract/generatedDatasetHtml.jsp",
			"Generate Dataset");
	public static final Page GENERATE_EXCEL_DATASET = new Page("/WEB-INF/jsp/extract/generatedExcelDataset.jsp",
			"Generate Excel Dataset");

	public static final Page CREATE_DATASET_1 = new Page("/WEB-INF/jsp/extract/createDatasetBegin.jsp",
			"Create Dataset Begin");
	public static final Page CREATE_DATASET_2 = new Page("/WEB-INF/jsp/extract/createDatasetStep2.jsp",
			"Create Dataset Step Two");
	public static final Page CREATE_DATASET_3 = new Page("/WEB-INF/jsp/extract/createDatasetStep3.jsp",
			"Create Dataset Step Three");
	public static final Page CREATE_DATASET_4 = new Page("/WEB-INF/jsp/extract/createDatasetStep4.jsp",
			"Create Dataset Step Four");
	public static final Page CONFIRM_DATASET = new Page("/WEB-INF/jsp/extract/createDatasetConfirmMetadata.jsp",
			"Create Dataset Step Four");

	public static final Page CREATE_DATASET_EVENT_ATTR = new Page("/WEB-INF/jsp/extract/selectEventAttribute.jsp",
			"Create Dataset and select event Attribute");
	public static final Page CREATE_DATASET_SUB_ATTR = new Page("/WEB-INF/jsp/extract/selectSubAttribute.jsp",
			"Create Dataset and select subject Attribute");
	public static final Page CREATE_DATASET_GROUP_ATTR = new Page("/WEB-INF/jsp/extract/selectGroupAttribute.jsp",
			"Create Dataset and select group Attribute");
	public static final Page CREATE_DATASET_CRF_ATTR = new Page("/WEB-INF/jsp/extract/selectCRFAttributes.jsp",
			"Create Dataset and select CRF Attribute");
	public static final Page CREATE_DATASET_SELECT_ITEMS = new Page("/WEB-INF/jsp/extract/selectItems.jsp",
			"Create Dataset and select Items");

	public static final Page CREATE_DATASET_APPLY_FILTER_SERVLET = new Page("CreateDatasetApplyFilter",
			"Create Dataset Apply Filter");
	public static final Page CREATE_DATASET_APPLY_FILTER = new Page(
			"/WEB-INF/jsp/extract/createDatasetApplyFilter.jsp", "Create Dataset Apply Filter");

	public static final Page CREATE_DATASET_VIEW_SELECTED = new Page("/WEB-INF/jsp/extract/viewSelected.jsp",
			"View Selected Items");
	public static final Page CREATE_DATASET_VIEW_SELECTED_HTML = new Page("/WEB-INF/jsp/extract/viewSelectedHtml.jsp",
			"View Selected Items in a static way");
	public static final Page REMOVE_DATASET = new Page("/WEB-INF/jsp/extract/removeDataset.jsp", "Remove Dataset");
	public static final Page RESTORE_DATASET = new Page("/WEB-INF/jsp/extract/restoreDataset.jsp", "Restore Dataset");

	public static final Page ITEM_DETAIL = new Page("/WEB-INF/jsp/extract/itemDetail.jsp", "Remove Dataset");

	public static final Page EDIT_DATASET = new Page("/WEB-INF/jsp/extract/editDataset.jsp", "Edit Dataset");

	public static final Page ERROR = new Page("/WEB-INF/jsp/error.jsp", "Error Page of OpenClinica");

	private static final String PATH = "/WEB-INF/jsp/";
	public static final String SERVLET_PATH = "/OpenClinica";

	public static final Page ADMIN_SYSTEM = new Page(PATH + "admin/index.jsp", "Administer System Menu");

	public static final Page CREATE_JOB_EXPORT = new Page(PATH + "admin/createExportJob.jsp", "Create Export Job");
	public static final Page UPDATE_JOB_EXPORT = new Page(PATH + "admin/updateExportJob.jsp", "Update Export Job");
	public static final Page CREATE_JOB_IMPORT = new Page(PATH + "admin/createImportJob.jsp", "Create Import Job");
	public static final Page UPDATE_JOB_IMPORT = new Page(PATH + "admin/updateImportJob.jsp", "Update Import Job");
	public static final Page VIEW_JOB = new Page(PATH + "admin/viewJobs.jsp", "View Jobs");
	public static final Page VIEW_ALL_JOBS = new Page(PATH + "admin/viewAllJobs.jsp", "View Jobs");
	public static final Page VIEW_IMPORT_JOB = new Page(PATH + "admin/viewImportJobs.jsp", "View Import Jobs");
	public static final Page VIEW_IMPORT_JOB_SERVLET = new Page("/ViewImportJob", "View Import Jobs");
	public static final Page VIEW_LOG_MESSAGE = new Page(PATH + "admin/viewLogMessage.jsp", "View Log Message");
	// below line for redirect without having to generate the table, tbh
	public static final Page VIEW_JOB_SERVLET = new Page("/ViewJob", "View Jobs");
	public static final Page VIEW_SINGLE_JOB = new Page(PATH + "admin/viewSingleJob.jsp", "View Jobs");
	// job creation and viewing pages, all under admin
	public static final Page TECH_ADMIN_SYSTEM = new Page(PATH + "techadmin/index.jsp", "Technical Administrator Menu");
	public static final Page VIEW_SCHEDULER = new Page(PATH + "admin/viewScheduler.jsp", "View System Scheduler");
	public static final Page ADMIN_SYSTEM_SERVLET = new Page("/AdminSystem", "Administer System Servlet");

	public static final Page SUBMIT_DATA = new Page(PATH + "submit/index.jsp", "Submit Data Menu");
	// public static final Page SUBMIT_DATA_SERVLET = new Page("/SubmitData",
	// "Submit Data Menu");
	public static final Page SUBMIT_DATA_SERVLET = new Page("/ListStudySubjectsSubmit", "Submit Data Menu");

	public static final Page PAGE_TO_CREATE_NEW_STUDY_EVENT = new Page(PATH + "submit/pageToCreateNewStudyEvent.jsp",
			"Create a New Study Event");
	public static final Page CREATE_NEW_STUDY_EVENT = new Page(PATH + "submit/createNewStudyEvent.jsp",
			"Create a New Study Event");
	public static final Page CREATE_NEW_STUDY_EVENT_SERVLET = new Page("/CreateNewStudyEvent",
			"Create a New Study Event");

	public static final Page INSTRUCTIONS_ENROLL_SUBJECT = new Page(PATH + "submit/instructionsEnrollSubject.jsp",
			"Enroll New Subject - Instructions");
	public static final Page ADD_NEW_SUBJECT = new Page(PATH + "submit/addNewSubject.jsp", "Enroll New Subject");
	public static final Page ADD_EXISTING_SUBJECT = new Page(PATH + "submit/addExistingSubject.jsp",
			"Enroll An Existing Subject");

	public static final Page INSTRUCTIONS_ENROLL_SUBJECT_SERVLET = new Page("/AddNewSubject?instr=1",
			"Enroll New Subject Servlet");

	public static final Page FIND_STUDY_EVENTS_SERVLET = new Page("/FindStudyEvents", "Find Study Events");
	public static final Page FIND_STUDY_EVENTS_STEP1 = new Page(PATH + "submit/findStudyEventsStep1.jsp",
			"Find Study Events - Step 1");
	public static final Page FIND_STUDY_EVENTS_STEP2 = new Page(PATH + "submit/findStudyEventsStep2.jsp",
			"Find Study Events - Step 2");
	public static final Page FIND_STUDY_EVENTS_STEP3 = new Page(PATH + "submit/findStudyEventsStep3.jsp",
			"Find Study Events - Step 3");

	public static final Page SHUFFLE_DNS_PAGE = new Page(PATH + "submit/shuffleDNsPage.jsp", "Shuffle DiscrepancyNotes");
	public static final Page CRF_LIST_FOR_STUDY_EVENT = new Page(PATH + "submit/crfListForStudyEvent.jsp",
			"Enter Data for a Study Event");
	public static final Page ENTER_DATA_FOR_STUDY_EVENT = new Page(PATH + "submit/enterDataForStudyEvent.jsp",
			"Enter Data for a Study Event");
	public static final Page ENTER_DATA_FOR_STUDY_EVENT_SERVLET = new Page("/EnterDataForStudyEvent",
			"Enter Data for a Study Event");
	public static final Page INTERVIEWER = new Page(PATH + "submit/interviewer.jsp",
			"Event CRF Interview Info Submission");

	public static final Page INITIAL_DATA_ENTRY = new Page(PATH + "submit/initialDataEntry.jsp", "Initial Data Entry");
	public static final Page INITIAL_DATA_ENTRY_SERVLET = new Page("/InitialDataEntry", "Initial Data Entry");

	public static final Page DOUBLE_DATA_ENTRY = new Page(PATH + "submit/doubleDataEntry.jsp", "Double Data Entry");
	public static final Page DOUBLE_DATA_ENTRY_SERVLET = new Page("/DoubleDataEntry", "Double Data Entry");

	public static final Page ADMIN_EDIT = new Page(PATH + "submit/administrativeEditing.jsp", "Administrative Editing");
	public static final Page ADMIN_EDIT_SERVLET = new Page("/AdministrativeEditing", "Administrative Editing Servlet");

	public static final Page LIST_USER_IN_STUDY = new Page(PATH + "managestudy/listUserInStudy.jsp",
			"list users in a study");
	public static final Page VIEW_USER_IN_STUDY = new Page(PATH + "managestudy/viewUserInStudy.jsp",
			"view a user in a study");
	public static final Page SET_USER_ROLE_IN_STUDY = new Page(PATH + "managestudy/setUserRoleInStudy.jsp",
			"set a user role in a study");
	public static final Page LIST_USER_IN_STUDY_SERVLET = new Page("/ListStudyUser", "list users in a study");

	public static final Page LIST_SUBJECT = new Page(PATH + "managestudy/listSubject.jsp", "list subjects in a study");
	public static final Page LIST_SUBJECT_SERVLET = new Page("/ListSubject", "list subjects in a study");
	public static final Page VIEW_SUBJECT = new Page(PATH + "admin/viewSubject.jsp", "View Subject");

	public static final Page VIEW_CRF_VERSION = new Page(PATH + "managestudy/viewCRFVersion.jsp", "View a CRF Version");

	// TODO do we need both versions here??? tbh
	public static final Page LIST_STUDY_SUBJECT_SERVLET = new Page("/ListStudySubject", "list subjects in a study");
	public static final Page VIEW_STUDY_SUBJECT = new Page(PATH + "managestudy/viewStudySubject.jsp",
			"View Subject in a study");
	public static final Page VIEW_STUDY_SUBJECT_AUDIT = new Page(PATH + "managestudy/viewStudySubjectAudit.jsp",
			"View Subject in a study Audit");
	public static final Page VIEW_STUDY_SUBJECT_SERVLET = new Page("/ViewStudySubject",
			"View Subject in a study Servlet");

	public static final Page UPDATE_STUDY_SUBJECT = new Page(PATH + "managestudy/updateStudySubject.jsp",
			"update Subject in a study");
	public static final Page UPDATE_STUDY_SUBJECT_SERVLET = new Page("/UpdateStudySubject", "update Subject in a study");
	public static final Page UPDATE_STUDY_SUBJECT_CONFIRM = new Page(
			PATH + "managestudy/updateStudySubjectConfirm.jsp", "update Subject in a study Confirm");

	public static final Page REMOVE_STUDY_SUBJECT = new Page(PATH + "managestudy/removeStudySubject.jsp",
			"Remove Subject from a study");
	public static final Page RESTORE_STUDY_SUBJECT = new Page(PATH + "managestudy/restoreStudySubject.jsp",
			"Restore Subject to a study");

	public static final Page REMOVE_STUDY_EVENT = new Page(PATH + "managestudy/removeStudyEvent.jsp",
			"Remove Event from a study");
	public static final Page RESTORE_STUDY_EVENT = new Page(PATH + "managestudy/restoreStudyEvent.jsp",
			"Restore Event to a study");
	public static final Page DELETE_STUDY_EVENT = new Page(PATH + "managestudy/deleteStudyEvent.jsp",
			"Delete Event from a study");
	public static final Page DELETE_STUDY_EVENT_SERVLET = new Page("/DeleteStudyEvent",
			"Delete Event from a study servlet");

	public static final Page REMOVE_EVENT_CRF = new Page(PATH + "managestudy/removeEventCRF.jsp",
			"Remove CRF from event");
	public static final Page RESTORE_EVENT_CRF = new Page(PATH + "managestudy/restoreEventCRF.jsp",
			"Restore CRF to event");
	public static final Page VIEW_EVENT_CRF = new Page(PATH + "managestudy/viewEventCRF.jsp", "View Event CRF Data");
	public static final Page DELETE_EVENT_CRF = new Page(PATH + "admin/deleteEventCRF.jsp", "Delete CRF from event");

	public static final Page UPDATE_SUBJECT = new Page(PATH + "admin/updateSubject.jsp", "update a subject");
	public static final Page UPDATE_SUBJECT_SERVLET = new Page("/UpdateSubject", "update a subject");
	public static final Page UPDATE_SUBJECT_CONFIRM = new Page(PATH + "admin/updateSubjectConfirm.jsp",
			"confirm update a subject");
	public static final Page REASSIGN_STUDY_SUBJECT = new Page(PATH + "managestudy/reassignStudySubject.jsp",
			"reassign a subject");
	public static final Page REASSIGN_STUDY_SUBJECT_CONFIRM = new Page(PATH
			+ "managestudy/reassignStudySubjectConfirm.jsp", "confirm reassign a subject");

	public static final Page REMOVE_SUBJECT = new Page(PATH + "admin/removeSubject.jsp", "remove a subject");
	public static final Page RESTORE_SUBJECT = new Page(PATH + "admin/restoreSubject.jsp", "restore a subject");

	public static final Page SET_USER_ROLE = new Page(PATH + "admin/setUserRole.jsp",
			"set a study user role for a user");
	/**
	 * Page for listing subjects.
	 */
	public static final Page SUBJECT_LIST = new Page("/WEB-INF/jsp/admin/listSubject.jsp", "List all Subjects");
	public static final Page SUBJECT_LIST_SERVLET = new Page("/ListSubject", "List all subjects servlet");

	public static final Page VIEW_TABLE_OF_CONTENT = new Page("/WEB-INF/jsp/managestudy/viewTableOfContents.jsp",
			"View Table Of Contents");
	public static final Page VIEW_TABLE_OF_CONTENT_SERVLET = new Page("/ViewTableOfContent",
			"View Table Of Contents Servlet");
	public static final Page VIEW_SECTION_DATA_ENTRY = new Page("/WEB-INF/jsp/managestudy/viewSectionDataEntry.jsp",
			"View Section Data Entry");
	public static final Page VIEW_ANNOTATED_SECTION_DATA_ENTRY = new Page(
			"/WEB-INF/jsp/managestudy/viewAnnotatedSectionDataEntry.jsp", "View Section Data Entry");

	public static final Page VIEW_SECTION_DATA_PREVIEW = new Page(
			"/WEB-INF/jsp/managestudy/viewSectionDataPreview.jsp", "View Section Data Preview");
	// YW 07-23-2007 << for preview in the issue000937
	public static final Page VIEW_SECTION_DATA_ENTRY_PREVIEW = new Page("/SectionPreview", "Preview CRF Servlet");
	// YW >>

	public static final Page VIEW_SECTION_DATA_ENTRY_PRINT = new Page(
			"/WEB-INF/jsp/managestudy/viewSectionDataEntryHtml.jsp", "View Section Data Entry Html");

	public static final Page VIEW_SECTION_DATA_ENTRY_SERVLET = new Page("/ViewSectionDataEntry",
			"View Section Data Entry Servlet");

	public static final Page EXPORT_DATA_CUSTOM = new Page("", "Dataset Export");
	public static final Page VIEW_EVENT_CRF_CONTENT = new Page("/WEB-INF/jsp/managestudy/viewEventCRFContent.jsp",
			"View Event CRF Content");

	public static final Page UPDATE_STUDY_EVENT = new Page("/WEB-INF/jsp/managestudy/updateStudyEvent.jsp",
			"Upate Study Event");
	public static final Page UPDATE_STUDY_EVENT_SERVLET = new Page("/UpdateStudyEvent", "Upate Study Event");
	public static final Page UPDATE_STUDY_EVENT_SIGNED = new Page(
			"/WEB-INF/jsp/managestudy/updateStudyEventSigned.jsp", "Upate Study Event");
	public static final Page VIEW_STUDY_EVENTS = new Page("/WEB-INF/jsp/managestudy/viewStudyEvents.jsp",
			"View Study Events");
	public static final Page VIEW_STUDY_EVENTS_SERVLET = new Page("/ViewStudyEvents", "View Study Events");
	public static final Page VIEW_STUDY_EVENTS_PRINT = new Page("/WEB-INF/jsp/managestudy/viewStudyEventsPrint.jsp",
			"View Study Events");

	public static final Page DELETE_CRF_VERSION = new Page("/WEB-INF/jsp/admin/deleteCRFVersion.jsp",
			"delete CRF Version");

	public static final Page ADD_DISCREPANCY_NOTE = new Page("/WEB-INF/jsp/submit/addDiscrepancyNote.jsp",
			"Add Discrepancy Note");
	public static final Page ADD_DISCREPANCY_NOTE_DIV = new Page("/WEB-INF/jsp/submit/addDiscrepancyNoteDiv.jsp",
			"Add Discrepancy Note");
	public static final Page ADD_ONE_DISCREPANCY_NOTE_DIV = new Page(
			"/WEB-INF/jsp/submit/addOneDiscrepancyNoteDiv.jsp", "Add Discrepancy Note");
	public static final Page ADD_DISCREPANCY_NOTE_SERVLET = new Page("/CreateDiscrepancyNote", "Add Discrepancy Note");

	public static final Page UPDATE_CRF_HEADER_PAGE = new Page(PATH + "submit/updateCRFHeader.jsp", "Update CRF Header");

	public static final Page VIEW_DISCREPANCY_NOTE = new Page("/WEB-INF/jsp/submit/viewDiscrepancyNote.jsp",
			"View Discrepancy Note");
	public static final Page VIEW_DISCREPANCY_NOTES_IN_STUDY = new Page("/WEB-INF/jsp/managestudy/viewNotes.jsp",
			"View Discrepancy Notes in Study");
	public static final Page VIEW_DISCREPANCY_NOTES_IN_STUDY_PRINT = new Page(
			"/WEB-INF/jsp/managestudy/viewNotesPrint.jsp", "View Discrepancy Notes in Study Print");
	public static final Page VIEW_DISCREPANCY_NOTES_IN_STUDY_SERVLET = new Page("/ViewNotes",
			"View Discrepancy Notes in Study");

	public static final Page LIST_EVENTS_FOR_SUBJECTS_SERVLET = new Page("/ListEventsForSubjects",
			"List Selected Event For Subjects");
	public static final Page LIST_EVENTS_FOR_SUBJECTS = new Page("/WEB-INF/jsp/managestudy/listEventsForSubjects.jsp",
			"List Selected Event For Subjects");
	public static final Page INITIAL_DATA_ENTRY_NW = new Page("/WEB-INF/jsp/submit/initialDataEntryNw.jsp",
			"Data Entry");
	public static final Page VIEW_SECTION_DATA_ENTRY_PRINT_GROUPS = new Page(
			"/WEB-INF/jsp/managestudy/viewGroupSectionsPrint.jsp", "Print View for Group Tables");

	public static final Page CHOOSE_DOWNLOAD_FORMAT = new Page("/WEB-INF/jsp/submit/chooseDownloadFormat.jsp",
			"Choose download format");

	public static final Page FILE_UPLOAD = new Page("/WEB-INF/jsp/submit/uploadFile.jsp", "Form For File Uploading");
	public static final Page UPLOAD_FILE_SERVLET = new Page("/UploadFile", "Upload File");
	public static final Page DOWNLOAD_ATTACHED_FILE = new Page("/WEB-INF/jsp/submit/downloadAttachedFile.jsp",
			"Download Attached File");

	public static final Page CONFIRM_LOCKING_CRF_VERSION = new Page(
			"/WEB-INF/jsp/managestudy/confirmLockingCRFVersion.jsp", "confirm locking crf version");
	public static final Page CONFIRM_UNLOCKING_CRF_VERSION = new Page(
			"/WEB-INF/jsp/managestudy/confirmUnlockingCRFVersion.jsp", "confirm unlocking crf version");

	// public static final Page MANAGE_STUDY_MODULE = new
	// Page("pages/studymodule", "Manage study");
	public static final String MANAGE_STUDY_MODULE = "/pages/studymodule";
	public static final Page VIEW_SECTION_DATA_ENTRY_SERVLET_REST_URL = new Page("/ViewSectionDataEntryRESTUrlServlet",
			"View Section Data Entry Servlet for REST Url call");

	public static final Page VIEW_ALL_SITE_DEFAULT_CRF_VERSIONS_PRINT = new Page(
			"/WEB-INF/jsp/managestudy/defaultAllSiteCrfVersionPrint.jsp", "View default crf versions print");
	public static final Page VIEW_DEFAULT_CRF_VERSIONS_PRINT = new Page(
			"/WEB-INF/jsp/managestudy/defaultCrfVersionPrint.jsp", "View default crf versions print");
	public static final Page VIEW_ALL_DEFAULT_CRF_VERSIONS_PRINT = new Page(
			"/WEB-INF/jsp/managestudy/defaultAllCrfVersionPrint.jsp", "View default crf versions print");
	public static final Page VIEW_SUBJECT_CASE_BOOK_PRINT = new Page(
			"/WEB-INF/jsp/managestudy/printSubjectCaseBook.jsp", "View subject's case book print");
	public static final Page SHOW_CALENDAR_FUNC = new Page("/WEB-INF/jsp/managestudy/showCalendarFunc.jsp",
			"View caledar events properties");
	public static final Page SHOW_CALENDAR_FUNC_PER_SUBJ = new Page(
			"/WEB-INF/jsp/managestudy/viewCalendaredEventsForSubject.jsp", "View caledar events properties");

	public static final Page LOCK_SITE = new Page("/WEB-INF/jsp/submit/lockSite.jsp", "Lock / Unlock Site");
	public static final Page LOCK_STUDY_SUBJECT = new Page("/WEB-INF/jsp/submit/lockStudySubject.jsp",
			"Lock / Unlock Study Subject");

	/**
	 * Constructs the JSP Page instance.
	 *
	 * @param fileName
	 *            The filename of the JSP page
	 * @param title
	 *            The title of the JSP page
	 */
	private Page(String fileName, String title) {
		this.fileName = fileName;
		this.title = title;
	}

	/**
	 * Gets the title attribute of the Page object.
	 *
	 * @return The title value
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Gets the fileName attribute of the Page object.
	 *
	 * @return The fileName value
	 */
	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String newFileName) {
		this.fileName = newFileName;
	}

	/**
	 * Method sets new page.
	 *
	 * @param fileName
	 *            String
	 * @param title
	 *            String
	 * @return Page
	 */
	public static Page setNewPage(String fileName, String title) {
		return new Page(fileName, title);
	}

	/**
	 * Method checks that page is the DDE page.
	 *
	 * @param page
	 *            Page
	 * @return boolean
	 */
	public static boolean isDDEServletPage(Page page) {
		return page == null ? false : Page.DOUBLE_DATA_ENTRY_SERVLET.equals(page);
	}

	/**
	 * Method checks that page is the AE page.
	 *
	 * @param page
	 *            Page
	 * @return boolean
	 */
	public static boolean isAdminEditServletPage(Page page) {
		return page == null ? false : Page.DOUBLE_DATA_ENTRY_SERVLET.equals(page);
	}
}
