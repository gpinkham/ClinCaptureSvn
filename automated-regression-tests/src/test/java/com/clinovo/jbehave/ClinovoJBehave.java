package com.clinovo.jbehave;

import java.util.ArrayList;
import java.util.List;

import com.clinovo.pages.ChangeStudyPage;
import com.clinovo.pages.ConfirmChangeStudyPage;
import com.clinovo.steps.CommonSteps;
import com.clinovo.utils.Common;
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
    
    @Given("User logs in as $user")
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

    @Given("User logs in first time as $user")
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
    
	@Given(value="User logs in first time as $userRoleName: $activityTable", priority=1)
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
    
    @Given("User fills in data on Create User Account page to create default $userRoleName")
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
    public void userClicksUpdateStudyutton() {
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
    
    @Given("User selects Study/Site $studyName on Change Study/Site page")
    public void userSelectsStudyOnChangeStudyPage(String studyName) {
    	commonSteps.select_study_on_change_study_page(Common.removeQuotes(studyName));
    }
    
    @When("User changes Study/Site to $studyName")
    @Given("User changes Study/Site to $studyName")
    public void userSelectsStudyOnStudyPage(String studyName) {
    	userGoesToPage(ChangeStudyPage.PAGE_NAME);
    	userSelectsStudyOnChangeStudyPage(Common.removeQuotes(studyName));
    	userClicksContinueButton(ChangeStudyPage.PAGE_NAME);
    	userIsOnPage(ConfirmChangeStudyPage.PAGE_NAME);
    	userClicksSubmitButton();
    }
    
    @Then("Current Study is $studyName")
    @Given("Current Study is $studyName")
    public void currentStudyIs(String studyName) {
    	commonSteps.current_study_is(Common.removeQuotes(studyName));
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
    
    @Then("User calls a popup for $studySubjectID, $eventName")
    @Given("User calls a popup for $studySubjectID, $eventName")
    public void userCallsPopupOnSM(String studySubjectID, String eventName) {
        commonSteps.call_popup_for_subject_and_event(studySubjectID, eventName);
    }
    
    private User getCurrentUser() {
    	User user = (User) Thucydides.getCurrentSession().get(User.CURRENT_USER);
		return user;
    }
}
