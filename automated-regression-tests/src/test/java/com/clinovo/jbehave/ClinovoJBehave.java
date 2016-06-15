package com.clinovo.jbehave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.clinovo.pages.AddSubjectPage;
import com.clinovo.pages.AdministerCRFsPage;
import com.clinovo.pages.BuildStudyPage;
import com.clinovo.pages.ChangeStudyPage;
import com.clinovo.pages.ConfirmChangeStudyPage;
import com.clinovo.pages.ConfirmEventDefinitionCreationPage;
import com.clinovo.pages.CreateCRFDataCommitedPage;
import com.clinovo.pages.CreateCRFVersionPage;
import com.clinovo.pages.CreateStudyEventDefinitionPage;
import com.clinovo.pages.DefineStudyEventSelectCRFsPage;
import com.clinovo.pages.DefineStudyEventSelectedCRFsPage;
import com.clinovo.pages.ManageEventDefinitionsPage;
import com.clinovo.pages.NotesAndDiscrepanciesPage;
import com.clinovo.pages.PreviewCRFPage;
import com.clinovo.pages.SubjectMatrixPage;
import com.clinovo.pages.UpdateSubjectDetailsPage;
import com.clinovo.pages.ViewSubjectRecordPage;
import com.clinovo.steps.CommonSteps;
import com.clinovo.pages.beans.CRF;
import com.clinovo.pages.beans.DNote;
import com.clinovo.pages.beans.Study;
import com.clinovo.pages.beans.StudyEventDefinition;
import com.clinovo.pages.beans.StudySubject;
import com.clinovo.pages.beans.SystemProperties;
import com.clinovo.pages.beans.User;

import net.thucydides.core.Thucydides;
import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Parameters;

/**
 * Created by Anton on 17.07.2014.
 */
public class ClinovoJBehave {
    
    @Steps
    CommonSteps commonSteps;
    
    @Given("User logs in as \"$user\"")
    public void userLogsInAsUser(String user) {
    	User currentUser = User.defineDefaultUser(user);
    	
   		Thucydides.getCurrentSession().put(User.CURRENT_USER, currentUser);
   		commonSteps.login_to_cc(currentUser);
    }
    
    @Given(value="User logs in as $userRoleName: $activityTable", priority=1)
    public void userLogsInAsUser(String userRoleName, ExamplesTable table) {
    	User currentUser = defineUserByData(userRoleName, table);
    	
   		Thucydides.getCurrentSession().put(User.CURRENT_USER, currentUser);
   		commonSteps.login_to_cc(currentUser);
    }

    private User defineUserByData(String userRoleName, ExamplesTable table) {
    	User currentUser;
		if (table.getRowCount() > 0) {
    		boolean replaceNamedParameters = true;
    		Parameters rowParams = table.getRowAsParameters(0, replaceNamedParameters);
    		currentUser = User.fillUserFromTableRow(userRoleName, rowParams.values());
    	} else {
    		currentUser = User.defineDefaultUser(userRoleName);
    	}
		return currentUser;
	}

    @Given("User logs in first time as \"$user\"")
    public void userLogsInAsUserFirstTime(String user) {
		User currentUser = User.defineDefaultUser(user);
		if (Thucydides.getCurrentSession().get(User.NEW_CREATED_USER) != null) {
	   		currentUser = (User) Thucydides.getCurrentSession().get(User.NEW_CREATED_USER);
		}
		
    	String pass = currentUser.getPassword();
   		Thucydides.getCurrentSession().put(User.CURRENT_USER, currentUser);
   		currentUser.setPassword(currentUser.getOldPassword());
   		commonSteps.login_to_cc_first_time(currentUser);
   		currentUser.setPassword(pass);
   		Thucydides.getCurrentSession().remove(User.NEW_CREATED_USER);
    }
    
	@Given(value="User logs in first time as \"$userRoleName\": $activityTable", priority=1)
    public void userLogsInAsUserFirstTime(String userRoleName, ExamplesTable table) {
		User currentUser = defineUserByData(userRoleName, table);
		if (Thucydides.getCurrentSession().get(User.NEW_CREATED_USER) != null) {
	   		currentUser = (User) Thucydides.getCurrentSession().get(User.NEW_CREATED_USER);
		}
		
    	String pass = currentUser.getPassword();
   		Thucydides.getCurrentSession().put(User.CURRENT_USER, currentUser);
   		currentUser.setPassword(currentUser.getOldPassword());
   		commonSteps.login_to_cc_first_time(currentUser);
   		currentUser.setPassword(pass);
   		Thucydides.getCurrentSession().remove(User.NEW_CREATED_USER);
    }
    
	@Given("User changes old password to new")
    @When("User changes old password to new")
    public void userChangesPasswordToNew() {
    	commonSteps.change_old_password_to_new(getCurrentUser());
    }
    
    @When("User clicks 'Create User' button")
    @Given("User clicks 'Create User' button")
	public void userClicksCreateUserButton() {
    	commonSteps.click_create_user_button(getCurrentUser());
    }
    
    @Given("User is on $page")
    @When("User is on $page")
    @Then("User is on $page")
	public void userIsOnPage(String page) {
    	commonSteps.user_is_on_page(page);
    }
    
    @Given("User logs out")
    @Then("User logs out")
    @When("User logs out")
	public void userLogsOut() {
    	commonSteps.log_out();
    }
    
    @Given("User fills in data on Create User Account page to create default \"$userRoleName\"")
	public void userFillsDataToCreateDefaultUser(String userRoleName) {
    	User createdUser = User.defineDefaultUser(userRoleName);
    	Thucydides.getCurrentSession().put(User.NEW_CREATED_USER, createdUser);
    	commonSteps.fill_data_on_create_user_page(createdUser);
    }
    
    @Given("User fills in data on Create User Account page to create $userRoleName: $activityTable")
	public void userFillsDataToCreateUser(String userRoleName, ExamplesTable table) {
    	User createdUser = defineUserByData(userRoleName, table);
    	Thucydides.getCurrentSession().put(User.NEW_CREATED_USER, createdUser);
    	commonSteps.fill_data_on_create_user_page(createdUser);
    }

    @Given("User clicks 'Submit' button")
    @When("User clicks 'Submit' button")
    public void userClicksSubmitButton() {
        commonSteps.click_submit_button();
    }
    
    @Given("User clicks 'Continue' button")
    @When("User clicks 'Continue' button")
    public void userClicksConfirmButton() {
        commonSteps.click_confirm_button();
    }
    
    @Given(value="User clicks 'Continue' button on $page", priority=1)
    @When("User clicks 'Continue' button on $page")
    public void userClicksContinueButton(String page) {
        commonSteps.click_continue_button(page);
    }
    
    @Given(value="User clicks 'Submit' button on $page", priority=1)
    @When("User clicks 'Submit' button on $page")
    public void userClicksSubmitButton(String page) {
        commonSteps.click_submit_button(page);
    }
    
    @Given("User remembers password of created user")
    public void userRemembersPasswordOfCreatedUser() {
        commonSteps.remember_pass_of_created_user();
    }
    
    @Given("User fills in data to create site: $activityTable")
    public void userFillsInDataToCreateSite(ExamplesTable table) {
    	List<Study> studies = new ArrayList<Study>();
    	for (int i = 0; i < table.getRowCount(); i++) {
    		boolean replaceNamedParameters = true;
    		Parameters rowParams = table.getRowAsParameters(i, replaceNamedParameters);
        	Study substudy = Study.fillSubStudyDetailsFromTableRow(rowParams.values());
        	studies.add(substudy);
        }
        commonSteps.fill_in_data_to_create_site(studies);
    }
    
    @Given("User fills in Configure System Properties page: $activityTable")
    public void userFillsInSystemProperties(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams = table.getRowAsParameters(0, replaceNamedParameters);
    	SystemProperties prop = SystemProperties.fillSystemPropertiesFromTableRow(rowParams.values());
    	commonSteps.fill_in_system_properties(prop);
    }
    
    @Given("User goes to $page")
    @When("User goes to $page")
    public void userGoesToPage(String pageName) {
        commonSteps.go_to_page(pageName);
    }
    
    @Given("User clicks $element on $page")
    @When("User clicks $element on $page")
    public void userClicksElementOnPage(String element, String pageName) {
        commonSteps.click_element_on_page(pageName, element);
    }
    
    @Then("User sets Study status to $status")
    @Given("User sets Study status to $status")
    public void userSetsStudyStatus(String status) {
        commonSteps.set_study_status(status);
    }
    
    @Given("User fills in Update Study Details page: $activityTable")
    public void userFillsInUpdateStudyDetailsPage(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams = table.getRowAsParameters(0, replaceNamedParameters);
    	Study study = Study.fillStudyDetailsFromTableRow(rowParams.values());
    	commonSteps.fill_in_study_details(study);
    }
    
    @When("User browses file on Create a New CRF page: <filepath>")
    @Given("User browses file on Create a New CRF page: <filepath>")
	public void userBrowsesCRFFile(@Named("filepath") String filepath) {
    	commonSteps.browse_file_with_crf(filepath);
    }
    
    @Given("User fills in data to create study event definition: $activityTable")
    public void userFillsInCreateStudyEventDefinitionPage(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams = table.getRowAsParameters(0, replaceNamedParameters);
    	StudyEventDefinition event = StudyEventDefinition.fillStudyEventDefinitionFromTableRow(rowParams.values());
    	Thucydides.getCurrentSession().put(StudyEventDefinition.NEW_CREATED_EVENT, event);
    	commonSteps.fill_in_study_event_definition(event);
    }
    
    @Given("User selects CRFs on Define Study Event page: $eCRFsTable")
    public void userSelectsCRFsOnDefineStudyEventPage(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams = table.getRowAsParameters(0, replaceNamedParameters);
    	StudyEventDefinition event = (StudyEventDefinition) Thucydides.getCurrentSession().get(StudyEventDefinition.NEW_CREATED_EVENT);
    	event.setCRFList(StudyEventDefinition.generateCRFList(rowParams.values().get("eCRFs")));    	
    	commonSteps.select_CRFs_for_study_event_definition(event);
    }
    
    @Given("User selects Study/Site \"$studyName\" on Change Study/Site page")
    public void userSelectsStudyOnChangeStudyPage(String studyName) {
    	commonSteps.select_study_on_change_study_page(studyName);
    }
    
    @When("User changes Study/Site to \"$studyName\"")
    @Given("User changes Study/Site to \"$studyName\"")
    public void userSelectsStudyOnStudyPage(String studyName) {
    	userGoesToPage(ChangeStudyPage.PAGE_NAME);
    	userSelectsStudyOnChangeStudyPage(studyName);
    	userClicksContinueButton(ChangeStudyPage.PAGE_NAME);
    	userIsOnPage(ConfirmChangeStudyPage.PAGE_NAME);
    	userClicksSubmitButton();
    }
    
    @Then("Current Study is \"$studyName\"")
    @Given("Current Study is \"$studyName\"")
    public void currentStudyIs(String studyName) {
    	commonSteps.current_study_is(studyName);
    }
    
    @When("User changes scope to Study")
    @Given("User changes scope to Study")
    public void userChangesScopeToStudy() {
    	userGoesToPage(ChangeStudyPage.PAGE_NAME);
    	userSelectsStudyOnChangeStudyPage(commonSteps.get_study_name_from_page());
    	userClicksContinueButton(ChangeStudyPage.PAGE_NAME);
    	userIsOnPage(ConfirmChangeStudyPage.PAGE_NAME);
    	userClicksSubmitButton();
    }
    
    @Then("User is on Study level")
    public void isOnStudyLevel() {
    	commonSteps.is_on_study_level();
    }
    
    @Given("User fills in data on Add Subject page to create subject: $activityTable")
    public void userFillsInAddSubjectPage(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams = table.getRowAsParameters(0, replaceNamedParameters);
    	StudySubject ssubj = StudySubject.fillStudySubjectFromTableRow(rowParams.values());
    	commonSteps.fill_in_study_subject_page(ssubj);
    }
    
    @Given("User calls a popup for \"$studySubjectID\", \"$eventName\"")
    @When("User calls a popup for \"$studySubjectID\", \"$eventName\"")
    public void userCallsPopupOnSM(String studySubjectID, String eventName) {
    	StudyEventDefinition event = new StudyEventDefinition();
    	event.setName(eventName);
    	event.setStudySubjectID(studySubjectID);
    	Thucydides.getCurrentSession().put(StudyEventDefinition.EVENT_TO_SCHEDULE, event);
        commonSteps.call_popup_for_subject_and_event(studySubjectID, eventName);
    }
    
    @Given("User fills in popup to schedule event: $activityTable")
    public void userFillInPopupToScheduleEvent(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams = table.getRowAsParameters(0, replaceNamedParameters);
    	StudyEventDefinition event = StudyEventDefinition.fillStudyEventDefinitionFromTableRow(rowParams.values());
    	commonSteps.fill_in_popup_to_schedule_event(event);
    }
    
    @Given("User schedules event using popup: $activityTable")
    @When("User schedules event using popup: $activityTable")
    public void userSchedulesEventUsingPopup(ExamplesTable table) {
    	userFillInPopupToScheduleEvent(table);
    	commonSteps.click_schedule_event_button_in_popup();
    }
    
    @Given("Event is scheduled")
    @Then("Event is scheduled")
    public void eventIsScheduled() {
    	StudyEventDefinition event = (StudyEventDefinition) Thucydides.getCurrentSession().get(StudyEventDefinition.EVENT_TO_SCHEDULE);
    	Thucydides.getCurrentSession().remove(StudyEventDefinition.EVENT_TO_SCHEDULE);
    	commonSteps.event_is_scheduled(event);
    }
    
    @Given("User schedules events on SM: $activityTable")
    @When("User schedules events on SM: $activityTable")
    public void userSchedulesEventsOnSM(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	List<StudyEventDefinition> events = new ArrayList<StudyEventDefinition>();
    	Parameters rowParams;
    	for (int i = 0; i < table.getRowCount(); i++) {
    		rowParams = table.getRowAsParameters(i, replaceNamedParameters);
    		Map<String, String> values = rowParams.values();
    		for (String eventName: rowParams.values().get("Event Name").split(", ")) {
    			values.put("Event Name", eventName.trim());
    			StudyEventDefinition event = StudyEventDefinition.fillStudyEventDefinitionFromTableRow(values);
    			userCallsPopupOnSM(event.getStudySubjectID(), event.getName());
    			commonSteps.fill_in_popup_to_schedule_event(event);
    			commonSteps.click_schedule_event_button_in_popup();
    			events.add(event);
    		}
    	}
    	
    	Thucydides.getCurrentSession().put(StudyEventDefinition.EVENTS_TO_SCHEDULE, events);
    	commonSteps.clear_filter_on_SM();
    }
    
    @SuppressWarnings("unchecked")
	@Given("Events are scheduled")
    @Then("Events are scheduled")
    public void eventsAreScheduled() {
    	List<StudyEventDefinition> events = (List<StudyEventDefinition>) Thucydides.getCurrentSession().get(StudyEventDefinition.EVENTS_TO_SCHEDULE);
    	Thucydides.getCurrentSession().remove(StudyEventDefinition.EVENTS_TO_SCHEDULE);
    	Map<String, List<StudyEventDefinition>> studySubjectIDToEvents = new HashMap<String, List<StudyEventDefinition>>();
    	for (StudyEventDefinition event: events) {
    		if (studySubjectIDToEvents.get(event.getStudySubjectID()) != null) {
    			studySubjectIDToEvents.get(event.getStudySubjectID()).add(event);
    		} else {
    			List<StudyEventDefinition> seds = new ArrayList<StudyEventDefinition>();
    			seds.add(event);
    			studySubjectIDToEvents.put(event.getStudySubjectID(), seds);
    		}
    	}
    	
    	for (String studySubjectID: studySubjectIDToEvents.keySet()) {
    		commonSteps.filter_SM_by_study_subject_id(studySubjectID);
    		for (StudyEventDefinition event: studySubjectIDToEvents.get(studySubjectID)) {
    			commonSteps.event_is_scheduled(event);
    		}
    	}
    	
    	commonSteps.clear_filter_on_SM();
    }
    
    @Given("User changes Study properties: $activityTable")
   	public void userChangesStudyProperties(ExamplesTable table) {
    	userGoesToPage(BuildStudyPage.PAGE_NAME);
    	userSetsStudyStatus("Design");
    	commonSteps.click_update_study();
    	userFillsInUpdateStudyDetailsPage(table);
    }
    
    @Given("User clicks 'Enter Data' button in popup for \"$CRF\"")
    @When("User clicks 'Enter Data' button in popup for \"$CRF\"")
	public void userClicksEnterDataButtonInPopup(String aCRF) {
    	commonSteps.click_enter_data_button_in_popup(aCRF);
    }
    
    @Given("User fills in data into CRF: $activityTable")
   	public void userFillsInDataIntoCRF(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams = table.getRowAsParameters(0, replaceNamedParameters);
    	CRF crf = CRF.fillCRFFromTableRow(rowParams.values());
    	commonSteps.fill_in_crf(crf);
    }
    
    
    @Given("User fills in{ and|, completes and} saves CRF: $activityTable")
    @When("User fills in{ and|, completes and} saves CRF: $activityTable")
   	public void userFillsInAndSaveCRF(ExamplesTable table) {
    	userFillsInCRFAndSavesIfNeed(table, true);
    }
    
    @Given("User filters SDV table and performs SDV: $activityTable")
    @When("User filters SDV table and performs SDV: $activityTable")
   	public void userFiltersTableAndPerformsSDV(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Map<String, String> values;
    	for (int i = 0; i < table.getRowCount(); i++) {
    		values = new HashMap<String, String>(table.getRowAsParameters(i, replaceNamedParameters).values());
    		for (String crfName: values.get("CRF Name").split(", ")) {
    			values.put("CRF Name", crfName.trim());
    			userFiltersSDVPage(values);
    			userClicksPerformSDVButtonForFilteredTable();
    		}
    	}
    	
    	Thucydides.getCurrentSession().put(CRF.CRFS_TO_CHECK_SDV_STATUS, table);
    }
    
    private void userClicksPerformSDVButtonForFilteredTable() {
    	commonSteps.click_perform_SDV_button_for_filtered_table();
	}

	private void userFiltersSDVPage(Map<String, String> values) {
		commonSteps.filter_SDV_page(values);
	}

	@Given(value = "User clicks 'Save' button on CRF page", priority=1)
    @When(value = "User clicks 'Save' button on CRF page", priority=1)
	public void userClicksSaveButton() {
    	commonSteps.click_save_button_on_CRF_page();
    }
	
	@Then("CRFs are SDVed")
	public void crfsAreSDVed() {
		ExamplesTable table = (ExamplesTable) Thucydides.getCurrentSession().get(CRF.CRFS_TO_CHECK_SDV_STATUS);
    	Thucydides.getCurrentSession().remove(CRF.CRFS_TO_CHECK_SDV_STATUS);
    	boolean replaceNamedParameters = true;
    	Map<String, String> values;
    	for (int i = 0; i < table.getRowCount(); i++) {
    		values = new HashMap<String, String>(table.getRowAsParameters(i, replaceNamedParameters).values());
    		for (String crfName: values.get("CRF Name").split(", ")) {
    			values.put("CRF Name", crfName.trim());
    			userFiltersSDVPage(values);
    			userCheckCRFSDVed();
    		}
    	}
    }
    
    @Given("User enters credentials on Sign Study Event page")
    @When("User enters credentials on Sign Study Event page")
	public void userEntersCredentialsOnSignStudyEventPage() {
    	commonSteps.enter_credentials_on_sign_study_event_page(getCurrentUser());
    }
    
    private void userCheckCRFSDVed() {
    	commonSteps.user_check_CRF_SDVed();		
	}

    @Given("User filters SM table and signs events: $activityTable")
    @When("User filters SM table and signs events: $activityTable")
   	public void userFiltersTableAndSignsEvents(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Map<String, String> values;
    	for (int i = 0; i < table.getRowCount(); i++) {
    		values = new HashMap<String, String>(table.getRowAsParameters(i, replaceNamedParameters).values());
    		for (String eventName: values.get("Event Name").split(", ")) {
    			values.put("Event Name", eventName.trim());
    			userFiltersSMPage(values);
    			userCallsPopupOnSM(values);
    			commonSteps.click_sign_event_button_in_popup();
    			userEntersCredentialsOnSignStudyEventPage();
    			commonSteps.click_sign_button_on_sign_study_event_page();
    			userGoesToPage(SubjectMatrixPage.PAGE_NAME);
    		}
    	}
    	
    	Thucydides.getCurrentSession().put(StudyEventDefinition.EVENTS_TO_CHECK_SIGN_STATUS, table);
    	commonSteps.clear_filter_on_SM();
    }
    
    @Then("Events are signed")
	public void eventsAreSigned() {
		ExamplesTable table = (ExamplesTable) Thucydides.getCurrentSession().get(StudyEventDefinition.EVENTS_TO_CHECK_SIGN_STATUS);
    	boolean replaceNamedParameters = true;
    	Map<String, String> values;
    	for (int i = 0; i < table.getRowCount(); i++) {
    		values = new HashMap<String, String>(table.getRowAsParameters(i, replaceNamedParameters).values());
    		for (String eventName: values.get("Event Name").split(", ")) {
    			values.put("Event Name", eventName.trim());
    			userFiltersSMPage(values);
    			userChecksSignEventStatus(values);
    		}
    	}
    	
    	Thucydides.getCurrentSession().remove(StudyEventDefinition.EVENTS_TO_CHECK_SIGN_STATUS);
    }
    
    @Given("User creates DNs for the items from CRF: $activityTable")
    @When("User creates DNs for the items from CRF: $activityTable")
   	public void userCreatesDNsForItemsFromCRF(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams;
    	List<DNote> dns = new ArrayList<DNote>();
    	DNote dn;
    	for (int i = 0; i < table.getRowCount(); i++) {
    		rowParams = table.getRowAsParameters(i, replaceNamedParameters);
    		userCallsPopupOnSM(rowParams.values().get("Study Subject ID"), rowParams.values().get("Event Name"));
    		userClicksEnterDataButtonInPopup(rowParams.values().get("CRF Name"));
    		commonSteps.click_dn_flag_icon(commonSteps.get_flag_icon_element_by_CRF_item(rowParams.values().get("Item")));
    		dn = DNote.fillDNoteFromTableRow(rowParams.values());
    		commonSteps.create_DN(dn);
    		userClicksSaveButton();
    		dns.add(dn);
    	}
    	
    	Thucydides.getCurrentSession().put(DNote.DNS_TO_CHECK_EXIST, dns);
    }
    
    @Given("User creates DNs in CRF: $activityTable")
    @When("User creates DNs in CRF: $activityTable")
   	public void userCreatesDNsInCRF(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams;
    	List<DNote> dns = new ArrayList<DNote>();
    	DNote dn;
    	for (int i = 0; i < table.getRowCount(); i++) {
    		rowParams = table.getRowAsParameters(i, replaceNamedParameters);
    		commonSteps.click_dn_flag_icon(commonSteps.get_flag_icon_element_by_CRF_item(rowParams.values().get("Item")));
    		dn = DNote.fillDNoteFromTableRow(rowParams.values());
    		commonSteps.create_DN(dn);
    		dns.add(dn);
    	}
    	
    	Thucydides.getCurrentSession().put(DNote.DNS_TO_CHECK_EXIST, dns);
    }
    
    @Given("User saves CRF")
    @When("User saves CRF")
	public void userSavesCRF() {
    	commonSteps.save_crf();
    }
    
    @SuppressWarnings("unchecked")
	@Then("DNs are $status")
	public void dnsAreCreatedUpdatedClosed(String status) {
    	commonSteps.go_to_page(NotesAndDiscrepanciesPage.PAGE_NAME);
    	List<DNote> dns = (List<DNote>) Thucydides.getCurrentSession().get(DNote.DNS_TO_CHECK_EXIST);
    	for (DNote dn: dns) {
    		commonSteps.filter_NDs_page(dn);
    		commonSteps.check_DN_row_is_present(dn);
    		commonSteps.clear_filter_NDs_page(dn);
    	}
    
    	Thucydides.getCurrentSession().remove(DNote.DNS_TO_CHECK_EXIST);
    }
    
    @SuppressWarnings("unchecked")
	@Then("CRFs are uploaded")
	public void crfsUploaded() {
    	if (!commonSteps.getPageByPageName(AdministerCRFsPage.PAGE_NAME).isOnPage(commonSteps.getDriver())) {
    		commonSteps.go_to_page(AdministerCRFsPage.PAGE_NAME);
    	}
    	
    	List<CRF> crfs = (List<CRF>) Thucydides.getCurrentSession().get(CRF.CRFS_TO_CHECK_EXIST);
    	for (CRF crf: crfs) {
    		commonSteps.filter_administer_CRFs_page(crf);
    		commonSteps.check_CRF_row_is_present(crf);
    	}
    
    	Thucydides.getCurrentSession().remove(CRF.CRFS_TO_CHECK_EXIST);
    }
    
    @Then("DN is $status: $activityTable")
	public void dnIsCreatedUpdatedClosed(String status, ExamplesTable table) {
    	commonSteps.go_to_page(NotesAndDiscrepanciesPage.PAGE_NAME);
    	DNote dn = DNote.fillDNoteFromTableRow(table.getRow(0));
    	commonSteps.filter_NDs_page(dn);
    	commonSteps.check_DN_row_is_present(dn);    
    }
    
    @Given("User $changes Query DNs for the items from CRF: $activityTable")
    @When("User $changes Query DNs for the items from CRF: $activityTable")
   	public void userUpdatesOrClosesDNsForItemsFromCRF(String action, ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams;
    	List<DNote> dns = new ArrayList<DNote>();
    	DNote dn;
    	for (int i = 0; i < table.getRowCount(); i++) {
    		rowParams = table.getRowAsParameters(i, replaceNamedParameters);
    		userCallsPopupOnSM(rowParams.values().get("Study Subject ID"), rowParams.values().get("Event Name"));
    		userClicksEnterDataButtonInPopup(rowParams.values().get("CRF Name"));
    		commonSteps.click_dn_flag_icon(commonSteps.get_flag_icon_element_by_CRF_item(rowParams.values().get("Item")));
    		dn = DNote.fillDNoteFromTableRow(rowParams.values());
    		commonSteps.update_or_close_DN(dn);
    		userClicksSaveButton();
    		dns.add(dn.getParentDN());
    	}
    	
    	Thucydides.getCurrentSession().put(DNote.DNS_TO_CHECK_EXIST, dns);
    }
    
    @Given("User fills in CRF: $activityTable")
    @When("User fills in CRF: $activityTable")
   	public void userFillsInCRF(ExamplesTable table) {
    	userFillsInCRFAndSavesIfNeed(table, false);
    }
    
    private void userFillsInCRFAndSavesIfNeed(ExamplesTable table, boolean saveCRF) {
    	List<CRF> listWithCRFs = new ArrayList<CRF>();
    	for (Map<String, String> map : getCorrectMapWithCRFItems(table)) {
    		CRF crf = CRF.fillCRFFromTableRow(map);
    		userCallsPopupOnSM(crf.getStudySubjectID(), crf.getEventName());
    		userClicksEnterDataButtonInPopup(crf.getCrfName());
        	commonSteps.fill_in_crf(crf);
    		if (saveCRF) {
    			userClicksSaveButton();
    		}
    		listWithCRFs.add(crf);
    	}
    	Thucydides.getCurrentSession().put(CRF.CRFS_TO_CHECK_SAVED_DATA, listWithCRFs);
    }
    
    @SuppressWarnings("unchecked")
	@Then("CRF data is saved correctly")
	public void crfDataIsSavedCorrectly() {
    	List<CRF> crfs = (List<CRF>) Thucydides.getCurrentSession().get(CRF.CRFS_TO_CHECK_SAVED_DATA);
    	for (CRF crf : crfs) {
    		userCallsPopupOnSM(crf.getStudySubjectID(), crf.getEventName());
    		userClicksEnterDataButtonInPopup(crf.getCrfName());
    		commonSteps.check_data_in_crf(crf);
    	}
    	Thucydides.getCurrentSession().remove(CRF.CRFS_TO_CHECK_SAVED_DATA);
    }
    
    private List<Map<String, String>> getCorrectMapWithCRFItems(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams;
    	List<Map<String, String>> result = new ArrayList<Map<String, String>>();
    	Map<String, String> map = new HashMap<String, String>();
    	String studySubjectId = "", eventName = "", crfName = "", sectionName = "";
    	for (int i = 0; i < table.getRowCount(); i++) {
    		rowParams = table.getRowAsParameters(i, replaceNamedParameters);
    		Map<String, String> values = rowParams.values();
    		boolean next = false;
    		if (values.containsKey("Study Subject ID") && !values.get("Study Subject ID").isEmpty()) {
    			studySubjectId = values.get("Study Subject ID");
    			next = true;
    		} else {
    			values.put("Study Subject ID", studySubjectId);
    		}
    		if (values.containsKey("Event Name") && !values.get("Event Name").isEmpty()) {
    			eventName = values.get("Event Name");
    			next = true;
    		} else {
    			values.put("Event Name", eventName);
    		}
    		if (values.containsKey("CRF Name") && !values.get("CRF Name").isEmpty()) {
    			crfName = values.get("CRF Name");
    			next = true;
    		} else {
    			values.put("CRF Name", crfName);
    		}
    		if (values.containsKey("Section Name") && !values.get("Section Name").isEmpty()) {
    			sectionName = values.get("Section Name");
    			next = true;
    		} else {
    			values.put("Section Name", sectionName);
    		}
    		if (next) {
    			if (!map.isEmpty()) {
    				result.add(map);
    				map = new HashMap<String, String>(values);
    			} else {
    				map.putAll(values);
    			}
    		} else {
    			CRF.removeValuesFromMap(values, CRF.ARRAY_OF_PARAMETERS_TO_SKIP);
    			map.putAll(CRF.changeKeysForCRFItems(map, values));
    		}
    		if (i == table.getRowCount() - 1) {
    			result.add(map);
    		}
    	}
    	
		return result;
    }

	@When("User browses file on Import Rule Data page: <filepath>")
    @Given("User browses file on Import Rule Data page: <filepath>")
	public void userBrowsesRuleFile(@Named("filepath") String filepath) {
    	commonSteps.browse_file_with_rule(filepath);
    }
    
    @When("User sees '$message' message in 'Alerts&Messages' section")
    @Given("User sees '$message' message in 'Alerts&Messages' section")
    @Then("User sees '$message' message in 'Alerts&Messages' section")
	public void userSeesMessage(String message) {
    	commonSteps.see_message(message);
    }
    
    @Given("User uploads CRFs: $activityTable")
    @When("User uploads CRFs: $activityTable")
   	public void userUploadsCRFs(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams;
    	List<CRF> crfs = new ArrayList<CRF>();
    	for (int i = 0; i < table.getRowCount(); i++) {
    		rowParams = table.getRowAsParameters(i, replaceNamedParameters);
    		CRF crf = new CRF();
    		commonSteps.click_element_on_page(AdministerCRFsPage.PAGE_NAME, "'Create CRF' button");
    		commonSteps.browse_file_with_crf(rowParams.values().get("filepath"));
    		commonSteps.click_continue_button(CreateCRFVersionPage.PAGE_NAME);
    		commonSteps.user_is_on_page(PreviewCRFPage.PAGE_NAME);
    		commonSteps.set_CRF_parameters(crf);
    		crfs.add(crf);
    		commonSteps.click_submit_button();
    		commonSteps.user_is_on_page(CreateCRFDataCommitedPage.PAGE_NAME);
    		commonSteps.click_element_on_page(CreateCRFDataCommitedPage.PAGE_NAME, "'Exit' button");
    	}
    	
    	Thucydides.getCurrentSession().put(CRF.CRFS_TO_CHECK_EXIST, crfs);
    }
    
    @Given("User creates study event definitions: $activityTable")
    @When("User creates study event definitions: $activityTable")
    public void userCreatesStudyEventDefinitions(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	List<StudyEventDefinition> events = new ArrayList<StudyEventDefinition>();
    	for (int i = 0; i < table.getRowCount(); i++) {
    		Parameters rowParams = table.getRowAsParameters(i, replaceNamedParameters);
    		StudyEventDefinition event = StudyEventDefinition.fillStudyEventDefinitionFromTableRow(rowParams.values());
    		commonSteps.user_is_on_page(CreateStudyEventDefinitionPage.PAGE_NAME);
    		commonSteps.fill_in_study_event_definition(event);
    		userClicksConfirmButton();
    		commonSteps.user_is_on_page(DefineStudyEventSelectCRFsPage.PAGE_NAME);
    		commonSteps.select_CRFs_for_study_event_definition(event);
    		userClicksConfirmButton();
    		commonSteps.user_is_on_page(DefineStudyEventSelectedCRFsPage.PAGE_NAME);
    		userClicksConfirmButton();
    		commonSteps.user_is_on_page(ConfirmEventDefinitionCreationPage.PAGE_NAME);
    		userClicksSubmitButton();
    		events.add(event);
    	}
    	
    	Thucydides.getCurrentSession().put(StudyEventDefinition.NEW_CREATED_EVENTS, events);
    }
    
    @SuppressWarnings("unchecked")
   	@Then("Study event definitions are created")
   	public void studyEventDefinitionsAreCreated() {
       	List<StudyEventDefinition> events = (List<StudyEventDefinition>) Thucydides.getCurrentSession().get(StudyEventDefinition.NEW_CREATED_EVENTS);
       	commonSteps.go_to_page(ManageEventDefinitionsPage.PAGE_NAME);
       	for (StudyEventDefinition event: events) {
       		commonSteps.filter_manage_event_definitions_page(event);
       		commonSteps.check_event_row_is_present(event);
       	}
       	
       	Thucydides.getCurrentSession().remove(StudyEventDefinition.NEW_CREATED_EVENTS);
    }
    
    @Given("User creates subjects: $activityTable")
    @When("User creates subjects: $activityTable")
   	public void userCreatesSubjects(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams;
    	List<StudySubject> sSubjects = new ArrayList<StudySubject>();
    	for (int i = 0; i < table.getRowCount(); i++) {
    		rowParams = table.getRowAsParameters(i, replaceNamedParameters);
    		StudySubject ssubj = StudySubject.fillStudySubjectFromTableRow(rowParams.values());
        	commonSteps.fill_in_study_subject_page(ssubj);
        	commonSteps.click_element_on_page(AddSubjectPage.PAGE_NAME, "'Add Next Subject' button");
        	sSubjects.add(ssubj);
    	}
    	
    	Thucydides.getCurrentSession().put(StudySubject.STUDY_SUBJECTS_TO_CHECK_EXIST, sSubjects);
    }
    
    @Given("User creates DNs for Events using popup: $activityTable")
    @When("User creates DNs for Events using popup: $activityTable")
   	public void userCreatesDNsForEventUsingPopup(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams;
    	String ssID = "", eventName = "", ssIDFromRow = "", eventNameFromRow = "";
    	List<DNote> dns = new ArrayList<DNote>();
    	for (int i = 0; i < table.getRowCount(); i++) {
    		rowParams = table.getRowAsParameters(i, replaceNamedParameters);
    		ssIDFromRow = rowParams.values().get("Study Subject ID");
    		eventNameFromRow = rowParams.values().get("Event Name");
    		
    		//if "Study Subject ID" and "Event Name" are empty, fill them from previous row
    		
    		if (ssIDFromRow.isEmpty()) {
    			rowParams.values().put("Study Subject ID", ssID);
    		} 
    		if (eventNameFromRow.isEmpty()) {
    			rowParams.values().put("Event Name", eventName);
    		} 
    		
    		if ((!ssIDFromRow.isEmpty() && !ssID.equals(ssIDFromRow)) || (!eventNameFromRow.isEmpty() && !ssID.equals(eventNameFromRow))) {
    			ssID = ssIDFromRow.isEmpty()? ssID : ssIDFromRow;
    			eventName = eventNameFromRow.isEmpty()? eventName : eventNameFromRow;
    			if (i > 0) commonSteps.click_schedule_event_button_in_popup();
    			commonSteps.call_popup_for_subject_and_event(ssID, eventName);
    		}
    		
    		DNote dn = DNote.fillDNoteFromTableRow(rowParams.values());
        	dn.setEntityType("Event");
    		switch (dn.getEntityName()) {
    		case "Start Date":
    			commonSteps.click_element_on_page("popup", "'Start Date' flag");
    			break;
    		case "End Date":
    			commonSteps.click_element_on_page("popup", "'End Date' flag");
    			break;
    		case "Location":
    			commonSteps.click_element_on_page("popup", "'Location' flag");
    			break;
    		}
        	commonSteps.create_DN(dn);
        	dns.add(dn);
        	if (i == table.getRowCount() - 1) commonSteps.click_schedule_event_button_in_popup();
    	}
    	
    	Thucydides.getCurrentSession().put(DNote.DNS_TO_CHECK_EXIST, dns);
    }
    
    @SuppressWarnings("unchecked")
	@Then("Study subjects are created")
	public void studySubjectsAreCreated() {
    	List<StudySubject> sSubjects= (List<StudySubject>) Thucydides.getCurrentSession().get(StudySubject.STUDY_SUBJECTS_TO_CHECK_EXIST);
    	commonSteps.go_to_page(SubjectMatrixPage.PAGE_NAME);
    	for (StudySubject ssubj: sSubjects) {
    		commonSteps.filter_SM_by_study_subject_id(ssubj.getStudySubjectID());
    		commonSteps.check_row_is_present_on_SM();
    	}
    	
    	Thucydides.getCurrentSession().remove(StudySubject.STUDY_SUBJECTS_TO_CHECK_EXIST);
    }
        
    @Given(value = "User clicks 'View' icon for $studySubjectID on SM page", priority=1)
    @When(value = "User clicks 'View' icon for $studySubjectID on SM page", priority=1)
	public void userClicksViewIconForStSubjectOnSMPage(String studySubjectID) {
    	commonSteps.click_view_icon_for_study_subject_on_SM(studySubjectID);
    }
    
    @Given(value = "User clicks 'Edit' icon for $studyIDStSubjectID on Administer Subjects page", priority=1)
    @When(value = "User clicks 'Edit' icon for $studyIDStSubjectID on Administer Subjects page", priority=1)
	public void userClicksEditIconForSubjectOnAdministerSubjectsPage(String studyIDStSubjectID) {
    	commonSteps.click_edit_icon_for_subject_on_Administer_Subjects_page(studyIDStSubjectID);
    }
    
    @Given("User creates DNs for Study Subject: $activityTable")
    @When("User creates DNs for Study Subject: $activityTable")
   	public void userCreatesDNsForStudySubject(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams;
    	List<DNote> dns = new ArrayList<DNote>();
    	for (int i = 0; i < table.getRowCount(); i++) {
    		rowParams = table.getRowAsParameters(i, replaceNamedParameters);
    		DNote dn = DNote.fillDNoteFromTableRow(rowParams.values());
        	dn.setEntityType("Study Subject");
    		commonSteps.click_element_on_page(ViewSubjectRecordPage.PAGE_NAME, "'Date of Enrollment for Study' flag");
        	commonSteps.create_DN(dn);
        	dns.add(dn);
    	}
    	
    	Thucydides.getCurrentSession().put(DNote.DNS_TO_CHECK_EXIST, dns);
    }
    
    @Given("User creates DNs for Subject: $activityTable")
    @When("User creates DNs for Subject: $activityTable")
   	public void userCreatesDNsForSubject(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams;
    	List<DNote> dns = new ArrayList<DNote>();
    	for (int i = 0; i < table.getRowCount(); i++) {
    		rowParams = table.getRowAsParameters(i, replaceNamedParameters);
    		DNote dn = DNote.fillDNoteFromTableRow(rowParams.values());
        	dn.setEntityType("Subject");
        	switch (dn.getEntityName()) {
    		case "Unique Identifier": //Person ID
    			commonSteps.click_element_on_page(UpdateSubjectDetailsPage.PAGE_NAME, "'Person ID' flag");
    			break;
    		case "Sex": //Gender
    			commonSteps.click_element_on_page(UpdateSubjectDetailsPage.PAGE_NAME, "'Gender' flag");
    			break;
    		case "Date of Birth": //Date of Birth
    			commonSteps.click_element_on_page(UpdateSubjectDetailsPage.PAGE_NAME, "'Date of Birth' flag");
    			break;
    		}
        	commonSteps.create_DN(dn);
        	dns.add(dn);
    	}
    	
    	Thucydides.getCurrentSession().put(DNote.DNS_TO_CHECK_EXIST, dns);
    }
    
    @Given("User works with DNs on NDs page: $activityTable")
    @When("User works with DNs on NDs page: $activityTable")
   	public void userWorksWithDNs(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams;
    	List<DNote> dns = new ArrayList<DNote>();
    	DNote dn;
    	for (int i = 0; i < table.getRowCount(); i++) {
    		rowParams = table.getRowAsParameters(i, replaceNamedParameters);
    		dn = DNote.fillDNoteFromTableRow(rowParams.values());
    		
    		switch (rowParams.values().get("Action")) {
    		case "View": 
    			commonSteps.filter_NDs_page(dn);
    			commonSteps.view_DN_on_NDs_page(dn);
    			commonSteps.exit_from_DN();
    			dns.add(dn);
    			break;
    		default:
    			commonSteps.filter_NDs_page(dn.getParentDN());
    			commonSteps.view_DN_on_NDs_page(dn);
    			commonSteps.update_or_close_DN(dn);
    			dns.add(dn.getParentDN());
    			break;
    		}
    		commonSteps.clear_filter_NDs_page(dn);
    	}
    	
    	Thucydides.getCurrentSession().put(DNote.DNS_TO_CHECK_EXIST, dns);
    }
    
    @When("User leaves CRF without saving")
    @Given("User leaves CRF without saving")
    @Then("User leaves CRF without saving")
	public void userLeavesCRFWithoutSaving() {
    	commonSteps.leave_CRF_without_saving();
    }    
    
	@Then("Verify error message \"$errorMessage\" on CRF page")
	public void verifyErrorMessage(String errorMessage) {
		commonSteps.verify_error_message_on_CRF(errorMessage);
	}

	private void userChecksSignEventStatus(Map<String, String> values) {
		commonSteps.check_sign_event_status(values);
	}

	private void userCallsPopupOnSM(Map<String, String> values) {
		userCallsPopupOnSM(values.get("Study Subject ID"), values.get("Event Name"));
	}

	private void userFiltersSMPage(Map<String, String> map) {
		commonSteps.user_filters_SM_page(map);
	}

	private User getCurrentUser() {
    	User user = (User) Thucydides.getCurrentSession().get(User.CURRENT_USER);
		return user;
    }
}
