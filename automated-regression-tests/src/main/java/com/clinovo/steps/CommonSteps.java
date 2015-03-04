package com.clinovo.steps;

import java.util.List;

import com.clinovo.pages.*;
import com.clinovo.utils.Study;
import com.clinovo.utils.StudyEventDefinition;
import com.clinovo.utils.SystemProperties;
import com.clinovo.utils.User;

import net.thucydides.core.Thucydides;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import static org.fest.assertions.Assertions.assertThat;

public class CommonSteps extends ScenarioSteps {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CommonSteps(Pages pages) {
        super(pages);
    }
    
	protected BasePage aBasePage = getPages().get(BasePage.class);
	protected AdministerUsersPage anAdministerUsersPage = getPages().get(AdministerUsersPage.class);
	protected CreateUserAccountPage aCreateUserAccountPage = getPages().get(CreateUserAccountPage.class);
	protected ViewUserAccountPage aViewUserAccountPage = getPages().get(ViewUserAccountPage.class);
	protected BuildStudyPage aBuildStudyPage = getPages().get(BuildStudyPage.class);
	protected CreateNewSitePage aCreateNewSitePage = getPages().get(CreateNewSitePage.class);
	protected ConfirmCreateSitePage aConfirmCreateSitePage = getPages().get(ConfirmCreateSitePage.class);
	protected ManageSitesPage aManageSitesPage = getPages().get(ManageSitesPage.class);
	protected ConfigureSystemPropertiesPage aConfigureSystemPropertiesPage = getPages().get(ConfigureSystemPropertiesPage.class);
	protected ConfirmSystemPropertiesPage aConfirmSystemPropertiesPage = getPages().get(ConfirmSystemPropertiesPage.class);
	protected UpdateStudyDetailsPage anUpdateStudyDetailsPage = getPages().get(UpdateStudyDetailsPage.class);
	protected CreateCRFVersionPage aCreateCRFVersionPage = getPages().get(CreateCRFVersionPage.class);
	protected PreviewCRFPage aPreviewCRFPage = getPages().get(PreviewCRFPage.class);
	protected CreateCRFDataCommitedPage aCreateCRFDataCommitedPage = getPages().get(CreateCRFDataCommitedPage.class);
	protected CreateStudyEventDefinitionPage aCreateStudyEventDefinitionPage = getPages().get(CreateStudyEventDefinitionPage.class);
	protected DefineStudyEventSelectCRFsPage aDefineStudyEventSelectCRFsPage = getPages().get(DefineStudyEventSelectCRFsPage.class);
	protected DefineStudyEventSelectedCRFsPage aDefineStudyEventSelectedCRFsPage = getPages().get(DefineStudyEventSelectedCRFsPage.class);
	protected ConfirmEventDefinitionCreationPage aConfirmEventDefinitionCreationPage = getPages().get(ConfirmEventDefinitionCreationPage.class);

	
    private LoginPage aLoginPage = getPages().get(LoginPage.class);
    private HomePage aHomePage = getPages().get(HomePage.class);
    private SubjectMatrixPage aSubjectMatrixPage = getPages().get(SubjectMatrixPage.class);
    private ViewEventPage aViewEventPage = getPages().get(ViewEventPage.class);
    private ResetPasswordPage aResetPasswordPage = getPages().get(ResetPasswordPage.class);

    @Step
    public void enters_credentials(String login, String password) {
        aLoginPage.enterLoginName(login);
        aLoginPage.enterPassword(password);
    }

	@Step
    public void clicks_login_button() {
        aLoginPage.clickLoginBtn();
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
        aLoginPage.open();
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
        assertThat(aResetPasswordPage.isOnPage()).isTrue();
    }
    
    @Step
    public void should_see_task_menu() {
        assertThat(aHomePage.taskMenuIsVisible()).isTrue();
    }

    @Step
    public void go_to_subject_matrix_page() {
        aHomePage.goToSubjectMatrix();
    }

    @Step
    public void log_out() {
        aBasePage.logOut();
    }

    @Step
    public void open_browser_window() {
        aLoginPage.open();
        getDriver().manage().window().maximize();
    }
    
    @Step
	public void change_old_password_to_new(User currentUser) {
		aResetPasswordPage.fillInResetPasswordPage(currentUser);
		aResetPasswordPage.clickSubmit();
		should_see_task_menu();
	}
    
    public BasePage getPageByPageName(String page) {
    	switch (page){
		case LoginPage.PAGE_NAME: 
			return aLoginPage;
		case HomePage.PAGE_NAME: 
			return aHomePage;
		case SubjectMatrixPage.PAGE_NAME: 
			return aSubjectMatrixPage;
		case ViewEventPage.PAGE_NAME: 
			return aViewEventPage;
		case ResetPasswordPage.PAGE_NAME: 
			return aResetPasswordPage;
		case AdministerUsersPage.PAGE_NAME: 
			return anAdministerUsersPage;
		case ViewUserAccountPage.PAGE_NAME: 
			return aViewUserAccountPage;
		case BuildStudyPage.PAGE_NAME: 
			return aBuildStudyPage;
		case CreateNewSitePage.PAGE_NAME: 
			return aCreateNewSitePage;
		case ConfirmCreateSitePage.PAGE_NAME: 
			return aConfirmCreateSitePage;
		case ManageSitesPage.PAGE_NAME: 
			return aManageSitesPage;
		case CreateUserAccountPage.PAGE_NAME: 
			return aCreateUserAccountPage;
		case ConfigureSystemPropertiesPage.PAGE_NAME: 
			return aConfigureSystemPropertiesPage;
		case ConfirmSystemPropertiesPage.PAGE_NAME: 
			return aConfirmSystemPropertiesPage;
		case UpdateStudyDetailsPage.PAGE_NAME: 
			return anUpdateStudyDetailsPage;
		case CreateCRFVersionPage.PAGE_NAME: 
			return aCreateCRFVersionPage;
		case PreviewCRFPage.PAGE_NAME: 
			return aPreviewCRFPage;	
		case CreateCRFDataCommitedPage.PAGE_NAME: 
			return aCreateCRFDataCommitedPage;		
		case CreateStudyEventDefinitionPage.PAGE_NAME: 
			return aCreateStudyEventDefinitionPage;	
		case DefineStudyEventSelectCRFsPage.PAGE_NAME: 
			return aDefineStudyEventSelectCRFsPage;	
		case DefineStudyEventSelectedCRFsPage.PAGE_NAME: 
			return aDefineStudyEventSelectedCRFsPage;	
		case ConfirmEventDefinitionCreationPage.PAGE_NAME: 
			return aConfirmEventDefinitionCreationPage;		
			
		default: ;
    	}
    	
		return aBasePage;
	}
    
    @Step
	public void user_is_on_page(String page) {
		boolean isOnPage = false;
		isOnPage = getPageByPageName(page).isOnPage(getDriver());
		
		assertThat(isOnPage).isTrue();
	}

    @Step
	public void click_submit_button() {
		aBasePage.clickSubmit();
	}

	@Step
	public void remember_pass_of_created_user() {
		User createdUser = (User) Thucydides.getCurrentSession().get(User.NEW_CREATED_USER);
		String pass = aViewUserAccountPage.getPasswordFromAlertsAndMessages();
		createdUser.setOldPassword(pass);
	}

	@Step
	public void go_to_build_study_page() {
		aBasePage.goToBuildStudyPage();
	}

	@Step
	public void click_add_site_button() {
		aBuildStudyPage.clickAddSite();
	}

	@Step
	public void fill_in_data_to_create_site(List<Study> studies) {
		if (!studies.isEmpty()) {
			aCreateNewSitePage.fillInCreateNewSitePage(studies.get(0));
		}
	}

	@Step
	public void click_confirm_button() {
		aBasePage.clickSubmit();
	}

	@Step
	public void fill_in_system_properties(SystemProperties prop) {
		aConfigureSystemPropertiesPage.fillInConfigureSystemPropertiesPage(prop);
	}

	@Step
	public void go_to_administer_users_page() {
		aBasePage.goToAdministerUsersPage();
	}
	
	@Step
	public void go_to_configure_system_properties_page() {
		aBasePage.goToConfigureSystemPropertiesPage();
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
				go_to_update_study_details();
				break;
			case ConfirmSystemPropertiesPage.PAGE_NAME: 
				break;
			
			default: ;
		}		
	}

	private void go_to_update_study_details() {
		aBuildStudyPage.clickUpdateStudy();
	}

	@Step
	public void set_study_status(String status) {
		aBuildStudyPage.setStudyStatus(BuildStudyPage.convertStatusNameToStatusValue(status));
		aBuildStudyPage.clickSaveStudyStatusButton();
	}

	@Step
	public void click_update_study() {
		aBuildStudyPage.clickUpdateStudy();
	}

	@Step
	public void fill_in_study_details(Study study) {
		anUpdateStudyDetailsPage.fillInStudyDetailsPage(study);
	}

	@Step
	public void click_add_crf_button() {
		aBuildStudyPage.clickAddCRF();
	}

	@Step
	public void browse_file_with_crf(String filepath) {
		aCreateCRFVersionPage.browseCRFFile(filepath);
	}

	@Step
	public void click_continue_button(String page) {
		getPageByPageName(page).clickContinue();
	}

	@Step
	public void fill_in_study_event_definition(StudyEventDefinition event) {
		aCreateStudyEventDefinitionPage.fillInStudyEventDefinitionPage(event);
		
	}

	@Step
	public void select_CRFs_for_study_event_definition(
			StudyEventDefinition event) {
		aDefineStudyEventSelectCRFsPage.selectCRFs(event.getCRFList());
	}
	
    @Step
	public void click_create_user_button(User currentUser) {
    	anAdministerUsersPage.clickCreateUserButton();
	}

    @Step
	public void fill_data_on_create_user_page(User createdUser) {
		aCreateUserAccountPage.fillInCreateUserAccountPage(createdUser);
	}
}
