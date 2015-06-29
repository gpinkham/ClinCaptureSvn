package com.clinovo.steps;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;

import com.clinovo.pages.*;
import com.clinovo.pages.beans.CRF;
import com.clinovo.pages.beans.DNote;
import com.clinovo.pages.beans.Study;
import com.clinovo.pages.beans.StudyEventDefinition;
import com.clinovo.pages.beans.StudySubject;
import com.clinovo.pages.beans.SystemProperties;
import com.clinovo.pages.beans.User;

import net.thucydides.core.Thucydides;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Anton on 01.07.2014.
 */
public class CommonSteps extends ScenarioSteps {

	private static final long serialVersionUID = 1L;

	public CommonSteps(Pages pages) {
        super(pages);
    }
    
	protected BasePage basePage = getPages().get(BasePage.class);
	protected AdministerUsersPage administerUsersPage = getPages().get(AdministerUsersPage.class);
	protected CreateUserAccountPage createUserAccountPage = getPages().get(CreateUserAccountPage.class);
	protected ViewUserAccountPage viewUserAccountPage = getPages().get(ViewUserAccountPage.class);
	protected BuildStudyPage buildStudyPage = getPages().get(BuildStudyPage.class);
	protected CreateNewSitePage createNewSitePage = getPages().get(CreateNewSitePage.class);
	protected ConfirmCreateSitePage confirmCreateSitePage = getPages().get(ConfirmCreateSitePage.class);
	protected ManageSitesPage manageSitesPage = getPages().get(ManageSitesPage.class);
	protected ConfigureSystemPropertiesPage configureSystemPropertiesPage = getPages().get(ConfigureSystemPropertiesPage.class);
	protected ConfirmSystemPropertiesPage confirmSystemPropertiesPage = getPages().get(ConfirmSystemPropertiesPage.class);
	protected UpdateStudyDetailsPage updateStudyDetailsPage = getPages().get(UpdateStudyDetailsPage.class);
	protected CreateCRFVersionPage createCRFVersionPage = getPages().get(CreateCRFVersionPage.class);
	protected PreviewCRFPage previewCRFPage = getPages().get(PreviewCRFPage.class);
	protected CreateCRFDataCommitedPage createCRFDataCommitedPage = getPages().get(CreateCRFDataCommitedPage.class);
	protected CreateStudyEventDefinitionPage createStudyEventDefinitionPage = getPages().get(CreateStudyEventDefinitionPage.class);
	protected DefineStudyEventSelectCRFsPage defineStudyEventSelectCRFsPage = getPages().get(DefineStudyEventSelectCRFsPage.class);
	protected DefineStudyEventSelectedCRFsPage defineStudyEventSelectedCRFsPage = getPages().get(DefineStudyEventSelectedCRFsPage.class);
	protected ConfirmEventDefinitionCreationPage confirmEventDefinitionCreationPage = getPages().get(ConfirmEventDefinitionCreationPage.class);
	protected ChangeStudyPage changeStudyPage = getPages().get(ChangeStudyPage.class);
	protected ConfirmChangeStudyPage confirmChangeStudyPage = getPages().get(ConfirmChangeStudyPage.class);
	protected AddSubjectPage addSubjectPage = getPages().get(AddSubjectPage.class);
	protected SubjectMatrixPage subjectMatrixPage = getPages().get(SubjectMatrixPage.class);
	protected ManageEventDefinitionsPage manageEventDefinitionsPage = getPages().get(ManageEventDefinitionsPage.class);
	protected CRFPage crfPage = getPages().get(CRFPage.class);
	protected SDVPage sdvPage = getPages().get(SDVPage.class);
	protected SignStudyEventPage signStudyEventPage = getPages().get(SignStudyEventPage.class);
	protected ViewSubjectRecordPage viewSubjectRecordPage = getPages().get(ViewSubjectRecordPage.class);
	protected DNPage dnPage = getPages().get(DNPage.class);
	protected NotesAndDiscrepanciesPage notesAndDiscrepanciesPage = getPages().get(NotesAndDiscrepanciesPage.class);
	
    private LoginPage loginPage = getPages().get(LoginPage.class);
    private HomePage homePage = getPages().get(HomePage.class);
    private ViewEventPage viewEventPage = getPages().get(ViewEventPage.class);
    private ResetPasswordPage resetPasswordPage = getPages().get(ResetPasswordPage.class);

    @Step
    public void enters_credentials(String login, String password) {
        loginPage.enterLoginName(login);
        loginPage.enterPassword(password);
    }

	@Step
    public void clicks_login_button() {
        loginPage.clickLoginBtn();
    }

    @Step
    public void login_to_cc_first_time(User user) {
        cc_login(user);
        should_see_reset_password_page();
    }
    
    private void maximize() {
    	getDriver().manage().window().maximize();
	}

    public void cc_login(User user) {
        loginPage.open();
        maximize();
        enters_credentials(user.getUserName(), user.getPassword());
        clicks_login_button();
    }

	@Step
    public void login_to_cc(User user) {
		cc_login(user);
		should_see_task_menu();
    }

    @Step
    public void should_see_reset_password_page() {
        assertThat(resetPasswordPage.isOnPage()).isTrue();
    }
    
    @Step
    public void should_see_task_menu() {
        assertThat(homePage.taskMenuIsVisible()).isTrue();
    }

    @Step
    public void go_to_subject_matrix_page() {
        basePage.goToSubjectMatrix();
    }

    @Step
    public void log_out() {
        basePage.logOut();
    }

    @Step
    public void open_browser_window() {
        loginPage.open();
        getDriver().manage().window().maximize();
    }
    
    @Step
	public void change_old_password_to_new(User currentUser) {
		resetPasswordPage.fillInResetPasswordPage(currentUser);
		resetPasswordPage.clickSubmit();
		should_see_task_menu();
	}
    
    public BasePage getPageByPageName(String page) {
    	switch (page){
			case LoginPage.PAGE_NAME: 
				return loginPage;
			case HomePage.PAGE_NAME: 
				return homePage;
			case SubjectMatrixPage.PAGE_NAME: 
				return subjectMatrixPage;
			case ViewEventPage.PAGE_NAME: 
				return viewEventPage;
			case ResetPasswordPage.PAGE_NAME: 
				return resetPasswordPage;
			case AdministerUsersPage.PAGE_NAME: 
				return administerUsersPage;
			case ViewUserAccountPage.PAGE_NAME: 
				return viewUserAccountPage;
			case BuildStudyPage.PAGE_NAME: 
				return buildStudyPage;
			case CreateNewSitePage.PAGE_NAME: 
				return createNewSitePage;
			case ConfirmCreateSitePage.PAGE_NAME: 
				return confirmCreateSitePage;
			case ManageSitesPage.PAGE_NAME: 
				return manageSitesPage;
			case CreateUserAccountPage.PAGE_NAME: 
				return createUserAccountPage;
			case ConfigureSystemPropertiesPage.PAGE_NAME: 
				return configureSystemPropertiesPage;
			case ConfirmSystemPropertiesPage.PAGE_NAME: 
				return configureSystemPropertiesPage;
			case UpdateStudyDetailsPage.PAGE_NAME: 
				return updateStudyDetailsPage;
			case CreateCRFVersionPage.PAGE_NAME: 
				return createCRFVersionPage;
			case PreviewCRFPage.PAGE_NAME: 
				return previewCRFPage;	
			case CreateCRFDataCommitedPage.PAGE_NAME: 
				return createCRFDataCommitedPage;		
			case CreateStudyEventDefinitionPage.PAGE_NAME: 
				return createStudyEventDefinitionPage;	
			case DefineStudyEventSelectCRFsPage.PAGE_NAME: 
				return defineStudyEventSelectCRFsPage;	
			case DefineStudyEventSelectedCRFsPage.PAGE_NAME: 
				return defineStudyEventSelectedCRFsPage;	
			case ConfirmEventDefinitionCreationPage.PAGE_NAME: 
				return confirmEventDefinitionCreationPage;		
			case ChangeStudyPage.PAGE_NAME: 
				return changeStudyPage;	
			case ConfirmChangeStudyPage.PAGE_NAME: 
				return confirmChangeStudyPage;
			case AddSubjectPage.PAGE_NAME: 
				return addSubjectPage;	
			case ManageEventDefinitionsPage.PAGE_NAME: 
				return manageEventDefinitionsPage;
			case CRFPage.PAGE_NAME:
				return crfPage;
			case SDVPage.PAGE_NAME:
				return sdvPage;
			case SignStudyEventPage.PAGE_NAME:
				return signStudyEventPage;
			case ViewSubjectRecordPage.PAGE_NAME:
				return viewSubjectRecordPage;
			case DNPage.PAGE_NAME:
				return dnPage;	
			case NotesAndDiscrepanciesPage.PAGE_NAME:
				return notesAndDiscrepanciesPage;
			
			default: ;
    	}
    	
		return basePage;
	}
    
    @Step
	public void user_is_on_page(String page) {
		boolean isOnPage = false;
		isOnPage = getPageByPageName(page).isOnPage(getDriver());
		
		assertThat(isOnPage).isTrue();
	}

    @Step
	public void click_submit_button() {
		basePage.clickSubmit();
	}

	@Step
	public void remember_pass_of_created_user() {
		User createdUser = (User) Thucydides.getCurrentSession().get(User.NEW_CREATED_USER);
		String pass = viewUserAccountPage.getPasswordFromAlertsAndMessages();
		createdUser.setOldPassword(pass);
	}

	@Step
	public void go_to_build_study_page() {
		basePage.goToBuildStudyPage();
	}

	@Step
	public void click_add_site_button() {
		buildStudyPage.clickAddSite();
	}

	@Step
	public void fill_in_data_to_create_site(List<Study> studies) {
		if (!studies.isEmpty()) {
			createNewSitePage.fillInCreateNewSitePage(studies.get(0));
		}
	}

	@Step
	public void click_confirm_button() {
		basePage.clickSubmit();
	}

	@Step
	public void fill_in_system_properties(SystemProperties prop) {
		configureSystemPropertiesPage.fillInConfigureSystemPropertiesPage(prop);
	}

	@Step
	public void go_to_administer_users_page() {
		basePage.goToAdministerUsersPage();
	}
	
	@Step
	public void go_to_configure_system_properties_page() {
		basePage.goToConfigureSystemPropertiesPage();
	}
	
	@Step
	public void go_to_page(String page) {
		
		switch (page){
			case AdministerUsersPage.PAGE_NAME: 
				go_to_administer_users_page();
				break;
			case BuildStudyPage.PAGE_NAME: 
				go_to_build_study_page();
				break;
			case ConfigureSystemPropertiesPage.PAGE_NAME: 
				go_to_configure_system_properties_page();
				break;
			case UpdateStudyDetailsPage.PAGE_NAME: 
				go_to_update_study_details_page();
				break;
			case ChangeStudyPage.PAGE_NAME: 
				go_to_change_study_page();
				break;
			case AddSubjectPage.PAGE_NAME: 
				go_to_add_subject_page();
				break;
			case SubjectMatrixPage.PAGE_NAME: 
				go_to_subject_matrix_page();
				break;	
			case SDVPage.PAGE_NAME: 
				go_to_source_data_verification_page();
				break;		
			case NotesAndDiscrepanciesPage.PAGE_NAME: 
				go_to_notes_and_discrepancies_page();
				break;
			
			default: ;
		}		
	}

	private void go_to_notes_and_discrepancies_page() {
		basePage.goToNDsPage();
	}

	private void go_to_source_data_verification_page() {
		basePage.goToSDVPage();
	}

	private void go_to_add_subject_page() {
		basePage.clickAddSubjectLink();
	}

	private void go_to_change_study_page() {
		basePage.clickChangeStudyLink();
	}
	
	private void go_to_update_study_details_page() {
		buildStudyPage.clickUpdateStudy();
	}

	@Step
	public void set_study_status(String status) {
		buildStudyPage.setStudyStatus(BuildStudyPage.convertStatusNameToStatusValue(status));
		buildStudyPage.clickSaveStudyStatusButton();
	}

	@Step
	public void click_update_study() {
		buildStudyPage.clickUpdateStudy();
	}

	@Step
	public void fill_in_study_details(Study study) {
		updateStudyDetailsPage.fillInStudyDetailsPage(study);
	}

	@Step
	public void click_add_crf_button() {
		buildStudyPage.clickAddCRF();
	}

	@Step
	public void browse_file_with_crf(String filepath) {
		createCRFVersionPage.browseCRFFile(filepath);
	}

	@Step
	public void click_continue_button(String page) {
		getPageByPageName(page).clickContinue();
	}

	@Step
	public void click_submit_button(String page) {
		getPageByPageName(page).clickSubmit();
	}
	
	@Step
	public void fill_in_study_event_definition(StudyEventDefinition event) {
		createStudyEventDefinitionPage.fillInStudyEventDefinitionPage(event);
	}

	@Step
	public void select_CRFs_for_study_event_definition(StudyEventDefinition event) {
		defineStudyEventSelectCRFsPage.selectCRFs(event.getCRFList());
	}
	
    @Step
	public void click_create_user_button(User currentUser) {
    	administerUsersPage.clickCreateUserButton();
	}

    @Step
	public void fill_data_on_create_user_page(User createdUser) {
		createUserAccountPage.fillInCreateUserAccountPage(createdUser);
	}

    @Step
	public void click_add_event_definition_button() {
		buildStudyPage.clickAddStudyEventDefinition();
	}

    @Step
	public void select_study_on_change_study_page(String studyName) {
		changeStudyPage.selectStudy(studyName);
	}
    
    @Step
	public void current_study_is(String studyName) {
		assert(studyName.equals(basePage.getCurrentStudyName()));
	}
    
    @Step
	public String get_study_name_from_page() {
		return basePage.getCurrentParentStudyName();
	}
    
    @Step
	public void is_on_study_level() {
		basePage.isOnStudyLevel();
	}

    @Step
	public void fill_in_study_subject_page(StudySubject ssubj) {
		addSubjectPage.fillInAddSubjectPage(ssubj);
	}
    
    @Step
	public void call_popup_for_subject_and_event(String studySubjectID, String eventName) {
		subjectMatrixPage.callPopupForSubjectAndEvent(studySubjectID, eventName);
	}
    
    @Step
	public void fill_in_popup_to_schedule_event(StudyEventDefinition event) {
		subjectMatrixPage.fillInPopupToScheduleEvent(event);
	}

    @Step
	public void click_schedule_event_button_in_popup() {
		subjectMatrixPage.clickScheduleEventButtonInPopup();
	}

	@Step
	public void event_is_scheduled(StudyEventDefinition event) {
		subjectMatrixPage.eventIsScheduled(event);
	}
	
	@Step
	public void clear_filter_on_SM() {
		subjectMatrixPage.clickClearFilterLink();
	}
	
	@Step
	public void filter_SM_by_study_subject_id(String studySubjectID) {
		subjectMatrixPage.filterSMByStudySubjectID(studySubjectID);
	}
	
	@Step
	public void click_enter_data_button_in_popup(String aCRFName) {
		subjectMatrixPage.clickEnterDataButtonInPopup(aCRFName);
	}

	@Step
	public void fill_in_crf(CRF crf) {
		crfPage.fillInCRF(crf);
	}

	@Step
	public void click_save_button() {
		crfPage.clickSaveButton();
	}
	
	@Step
	public void click_perform_SDV_button_for_filtered_table() {
		sdvPage.clickPerformSDVButtonForFilteredTable();
	}

	@Step
	public void filter_SDV_page(Map<String, String> map) {
		sdvPage.fillFiltersOnSDVPage(map);
	}

	@Step
	public void user_check_CRF_SDVed() {
		sdvPage.checkSDVIcon();
	}
	
	@Step
	public void click_sign_event_button_in_popup() {
		subjectMatrixPage.clickSignEventButton();
	}
	
	@Step
	public void enter_credentials_on_sign_study_event_page(User user) {
		signStudyEventPage.enterCredentials(user);
	}
	
	@Step
	public void click_sign_button_on_sign_study_event_page() {
		signStudyEventPage.clickSignButton();
	}
	
	@Step
	public void user_filters_SM_page(Map<String, String> map) {
		subjectMatrixPage.filterSMPage(map);
	}
	
	@Step
	public void check_sign_event_status(Map<String, String> values) {
		subjectMatrixPage.checkSignEventStatus(values);
	}

	@Step
	public void create_DN(DNote dn) {
		String oldWindowId = switch_to_another_window("");
		dnPage.fillInAndSaveDN(dn);
		switch_to_another_window(oldWindowId);
	}
	
	@Step
	public void save_crf() {
		crfPage.clickSaveButton();
	}

	public WebElement get_flag_icon_element_by_CRF_item(String itemName) {
		return crfPage.findFlagIconElementByCRFItem(itemName);
	}

	@Step
	public void click_dn_flag_icon(WebElement flagIcon) {
		flagIcon.click();
	}
	
	public String switch_to_another_window(String windowId) {
		//Store the current window handle
		String winHandleBefore = getDriver().getWindowHandle();
		if (windowId.isEmpty()) {
			//Switch to new window opened
			for(String winHandle : getDriver().getWindowHandles()){
				getDriver().switchTo().window(winHandle);
			}
		} else {
			getDriver().switchTo().window(windowId);			
		}

		//Close the new window, if that window no more required
		//webdriver.close();

		//Switch back to original browser (first window)
		//webdriver.switchTo().window(winHandleBefore);
		
		return winHandleBefore;
	}

	@Step
	public void filter_NDs_page(DNote dn) {
		notesAndDiscrepanciesPage.fillFiltersFromDNOnNDPage(dn);
	}

	@Step
	public void check_DN_row_is_present(DNote dn) {
		notesAndDiscrepanciesPage.checkDNPresent(dn);
		
	}
	
	@Step
	public void update_or_close_DN(DNote dn) {
		String oldWindowId = switch_to_another_window("");
		dnPage.findAndFillInAndClickSubmit(dn);
		switch_to_another_window(oldWindowId);
	}
}