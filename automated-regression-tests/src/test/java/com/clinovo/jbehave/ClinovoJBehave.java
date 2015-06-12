package com.clinovo.jbehave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clinovo.pages.BuildStudyPage;
import com.clinovo.pages.ChangeStudyPage;
import com.clinovo.pages.ConfirmChangeStudyPage;
import com.clinovo.pages.NotesAndDiscrepanciesPage;
import com.clinovo.pages.SubjectMatrixPage;
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
public class ClinovoJBehave extends BaseJBehave {
    
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
    
    @Given("User clicks 'Continue' button on $page")
    @When("User clicks 'Continue' button on $page")
    public void userClicksContinueButton(String page) {
        commonSteps.click_continue_button(page);
    }
    
    @Given("User clicks 'Submit' button on $page")
    @When("User clicks 'Submit' button on $page")
    public void userClicksSubmitButton(String page) {
        commonSteps.click_submit_button(page);
    }
    
    @Given("User remembers password of created user")
    public void userRemembersPasswordOfCreatedUser() {
        commonSteps.remember_pass_of_created_user();
    }
    
    @Given("User clicks 'Add Site' button")
    public void userClicksAddSiteButton() {
        commonSteps.click_add_site_button();
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
    
    @Given("User clicks 'Update Study' button")
    public void userClicksUpdateStudyButton() {
        commonSteps.click_update_study();
    }
    
    @When("User clicks 'Add CRF' button")
    @Given("User clicks 'Add CRF' button")
	public void userClicksCreateCRFButton() {
    	commonSteps.click_add_crf_button();
    }
    
    @When("User browses file on Create a New CRF page: <filepath>")
    @Given("User browses file on Create a New CRF page: <filepath>")
	public void userBrowsesCRFFile(@Named("filepath") String filepath) {
    	commonSteps.browse_file_with_crf(filepath);
    }
    
    @When("User clicks 'Add Event Definitions' button")
    @Given("User clicks 'Add Event Definitions' button")
	public void userClicksAddEventDefenitionButton() {
    	commonSteps.click_add_event_definition_button();
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
    public void userSelectsCRFsPage(ExamplesTable table) {
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
    
    @Given("User clicks 'Schedule Event' button on popup")
    @When("User clicks 'Schedule Event' button on popup")
	public void userClicksScheduleEventButtonInPopup() {
    	commonSteps.click_schedule_event_button_in_popup();
    }
    
    @Given("User schedules event using popup: $activityTable")
    @When("User schedules event using popup: $activityTable")
    public void userSchedulesEventUsingPopup(ExamplesTable table) {
    	userFillInPopupToScheduleEvent(table);
    	userClicksScheduleEventButtonInPopup();
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
    			userClicksScheduleEventButtonInPopup();
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
    	userClicksUpdateStudyButton();
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
    	CRF crf = CRF.fillStudyDetailsFromTableRow(rowParams.values());
    	commonSteps.fill_in_crf(crf);
    }
    
    
    @Given("User fills in{ and|, completes and} saves CRF: $activityTable")
    @When("User fills in{ and|, completes and} saves CRF: $activityTable")
   	public void userFillsInAndSaveCRF(ExamplesTable table) {
    	boolean replaceNamedParameters = true;
    	Parameters rowParams;
    	for (int i = 0; i < table.getRowCount(); i++) {
    		rowParams = table.getRowAsParameters(i, replaceNamedParameters);
    		userCallsPopupOnSM(rowParams.values().get("Study Subject ID"), rowParams.values().get("Event Name"));
    		userClicksEnterDataButtonInPopup(rowParams.values().get("CRF Name"));
    		rowParams.values().remove("Study Subject ID");
    		rowParams.values().remove("Event Name");
    		rowParams.values().remove("CRF Name");
        	commonSteps.fill_in_crf(CRF.fillStudyDetailsFromTableRow(rowParams.values()));
    		userClicksSaveButton();
    	}
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

	@Given("User clicks 'Save' button")
    @When("User clicks 'Save' button")
	public void userClicksSaveButton() {
    	commonSteps.click_save_button();
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
	
    @Given("User clicks 'Sign Event' button in popup")
    @When("User clicks 'Sign Event' button in popup")
	public void userClicksSignEventButtonInPopup() {
    	commonSteps.click_sign_event_button_in_popup();
    }
    
    @Given("User enters credentials on Sign Study Event page")
    @When("User enters credentials on Sign Study Event page")
	public void userEntersCredentialsOnSignStudyEventPage() {
    	commonSteps.enter_credentials_on_sign_study_event_page(getCurrentUser());
    }
    
    @Given("User clicks 'Sign' button on Sign Study Event page")
    @When("User clicks 'Sign' button on Sign Study Event page")
	public void userClicksSignButtonOnSignStudyEventPage() {
    	commonSteps.click_sign_button_on_sign_study_event_page();
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
    			userClicksSignEventButtonInPopup();
    			userEntersCredentialsOnSignStudyEventPage();
    			userClicksSignButtonOnSignStudyEventPage();
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
    
    @Given("User saves CRF")
    @When("User saves CRF")
	public void userSavesCRF() {
    	commonSteps.save_crf();
    }
    
    @SuppressWarnings("unchecked")
	@Then("DNs are created")
	public void dnsAreCreated() {
    	commonSteps.go_to_page(NotesAndDiscrepanciesPage.PAGE_NAME);
    	List<DNote> dns = (List<DNote>) Thucydides.getCurrentSession().get(DNote.DNS_TO_CHECK_EXIST);
    	for (DNote dn: dns) {
    		commonSteps.filter_NDs_page(dn);
    		commonSteps.check_DN_row_is_present(dn);
    	}
    
    	Thucydides.getCurrentSession().remove(DNote.DNS_TO_CHECK_EXIST);
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
