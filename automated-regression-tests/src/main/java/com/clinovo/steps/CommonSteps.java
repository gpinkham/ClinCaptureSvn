package com.clinovo.steps;

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebElement;

import com.clinovo.pages.AddSubjectPage;
import com.clinovo.pages.AdministerCRFsPage;
import com.clinovo.pages.AdministerSubjectsPage;
import com.clinovo.pages.AdministerUsersPage;
import com.clinovo.pages.BasePage;
import com.clinovo.pages.BuildStudyPage;
import com.clinovo.pages.CRFPage;
import com.clinovo.pages.ChangeStudyPage;
import com.clinovo.pages.ConfigureSystemPropertiesPage;
import com.clinovo.pages.ConfirmChangeStudyPage;
import com.clinovo.pages.ConfirmCreateSitePage;
import com.clinovo.pages.ConfirmEventDefinitionCreationPage;
import com.clinovo.pages.ConfirmSystemPropertiesPage;
import com.clinovo.pages.CreateCRFDataCommitedPage;
import com.clinovo.pages.CreateCRFVersionPage;
import com.clinovo.pages.CreateNewSitePage;
import com.clinovo.pages.CreateStudyEventDefinitionPage;
import com.clinovo.pages.CreateUserAccountPage;
import com.clinovo.pages.DNPage;
import com.clinovo.pages.DefineStudyEventSelectCRFsPage;
import com.clinovo.pages.DefineStudyEventSelectedCRFsPage;
import com.clinovo.pages.HomePage;
import com.clinovo.pages.ImportRuleDataPage;
import com.clinovo.pages.LoginPage;
import com.clinovo.pages.ManageEventDefinitionsPage;
import com.clinovo.pages.ManageRulesPage;
import com.clinovo.pages.ManageSitesPage;
import com.clinovo.pages.NotesAndDiscrepanciesPage;
import com.clinovo.pages.PreviewCRFPage;
import com.clinovo.pages.ResetPasswordPage;
import com.clinovo.pages.SDVPage;
import com.clinovo.pages.SignStudyEventPage;
import com.clinovo.pages.SubjectMatrixPage;
import com.clinovo.pages.UpdateStudyDetailsPage;
import com.clinovo.pages.UpdateSubjectDetailsPage;
import com.clinovo.pages.ViewEventPage;
import com.clinovo.pages.ViewSubjectRecordPage;
import com.clinovo.pages.ViewUserAccountPage;
import com.clinovo.pages.beans.CRF;
import com.clinovo.pages.beans.DNote;
import com.clinovo.pages.beans.Study;
import com.clinovo.pages.beans.StudyEventDefinition;
import com.clinovo.pages.beans.StudySubject;
import com.clinovo.pages.beans.SystemProperties;
import com.clinovo.pages.beans.User;
import com.clinovo.utils.Common;

import net.thucydides.core.Thucydides;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

/**
 * Created by Anton on 01.07.2014.
 */
public class CommonSteps extends ScenarioSteps {

	private static final long serialVersionUID = 1L;

	public CommonSteps(Pages pages) {
		super(pages);
	}

	private BasePage basePage = getPages().get(BasePage.class);
	private AdministerUsersPage administerUsersPage = getPages().get(AdministerUsersPage.class);
	private CreateUserAccountPage createUserAccountPage = getPages().get(CreateUserAccountPage.class);
	private ViewUserAccountPage viewUserAccountPage = getPages().get(ViewUserAccountPage.class);
	private BuildStudyPage buildStudyPage = getPages().get(BuildStudyPage.class);
	private CreateNewSitePage createNewSitePage = getPages().get(CreateNewSitePage.class);
	private ConfirmCreateSitePage confirmCreateSitePage = getPages().get(ConfirmCreateSitePage.class);
	private ManageSitesPage manageSitesPage = getPages().get(ManageSitesPage.class);
	private ConfigureSystemPropertiesPage configureSystemPropertiesPage = getPages()
			.get(ConfigureSystemPropertiesPage.class);
	private ConfirmSystemPropertiesPage confirmSystemPropertiesPage = getPages()
			.get(ConfirmSystemPropertiesPage.class);
	private UpdateStudyDetailsPage updateStudyDetailsPage = getPages().get(UpdateStudyDetailsPage.class);
	private CreateCRFVersionPage createCRFVersionPage = getPages().get(CreateCRFVersionPage.class);
	private PreviewCRFPage previewCRFPage = getPages().get(PreviewCRFPage.class);
	private CreateCRFDataCommitedPage createCRFDataCommitedPage = getPages().get(CreateCRFDataCommitedPage.class);
	private CreateStudyEventDefinitionPage createStudyEventDefinitionPage = getPages()
			.get(CreateStudyEventDefinitionPage.class);
	private DefineStudyEventSelectCRFsPage defineStudyEventSelectCRFsPage = getPages()
			.get(DefineStudyEventSelectCRFsPage.class);
	private DefineStudyEventSelectedCRFsPage defineStudyEventSelectedCRFsPage = getPages()
			.get(DefineStudyEventSelectedCRFsPage.class);
	private ConfirmEventDefinitionCreationPage confirmEventDefinitionCreationPage = getPages()
			.get(ConfirmEventDefinitionCreationPage.class);
	private ChangeStudyPage changeStudyPage = getPages().get(ChangeStudyPage.class);
	private ConfirmChangeStudyPage confirmChangeStudyPage = getPages().get(ConfirmChangeStudyPage.class);
	private AddSubjectPage addSubjectPage = getPages().get(AddSubjectPage.class);
	private SubjectMatrixPage subjectMatrixPage = getPages().get(SubjectMatrixPage.class);
	private ManageEventDefinitionsPage manageEventDefinitionsPage = getPages().get(ManageEventDefinitionsPage.class);
	private CRFPage crfPage = getPages().get(CRFPage.class);
	private SDVPage sdvPage = getPages().get(SDVPage.class);
	private SignStudyEventPage signStudyEventPage = getPages().get(SignStudyEventPage.class);
	private ViewSubjectRecordPage viewSubjectRecordPage = getPages().get(ViewSubjectRecordPage.class);
	private DNPage dnPage = getPages().get(DNPage.class);
	private NotesAndDiscrepanciesPage notesAndDiscrepanciesPage = getPages().get(NotesAndDiscrepanciesPage.class);
	private ManageRulesPage manageRulesPage = getPages().get(ManageRulesPage.class);
	private ImportRuleDataPage importRuleDataPage = getPages().get(ImportRuleDataPage.class);
	private AdministerCRFsPage administerCRFsPage = getPages().get(AdministerCRFsPage.class);
	private AdministerSubjectsPage administerSubjectsPage = getPages().get(AdministerSubjectsPage.class);
	private UpdateSubjectDetailsPage updateSubjectDetailsPage = getPages().get(UpdateSubjectDetailsPage.class);

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
		switch (page) {
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
		case ManageRulesPage.PAGE_NAME:
			return manageRulesPage;
		case ImportRuleDataPage.PAGE_NAME:
			return importRuleDataPage;
		case AdministerCRFsPage.PAGE_NAME:
			return administerCRFsPage;
		case AdministerSubjectsPage.PAGE_NAME:
			return administerSubjectsPage;
		case UpdateSubjectDetailsPage.PAGE_NAME:
			return updateSubjectDetailsPage;

		default:
			;
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

		switch (page) {
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
		case ManageRulesPage.PAGE_NAME:
			go_to_manage_rules_page();
			break;
		case AdministerCRFsPage.PAGE_NAME:
			go_to_administer_CRFs_page();
			break;
		case CreateStudyEventDefinitionPage.PAGE_NAME:
			go_to_create_study_event_definition_page();
			break;
		case ManageEventDefinitionsPage.PAGE_NAME:
			go_to_manage_event_definitions_page();
			break;
		case AdministerSubjectsPage.PAGE_NAME:
			go_to_administer_subjects_page();
			break;

		default:
			;
		}
	}

	private void go_to_administer_subjects_page() {
		basePage.goToAdministerSubjectsPage();
	}

	private void go_to_manage_event_definitions_page() {
		basePage.goToBuildStudyPage();
		buildStudyPage.clickViewStudyEventDefinitions();
	}

	private void go_to_create_study_event_definition_page() {
		basePage.goToBuildStudyPage();
		click_add_event_definition_button();
	}

	private void go_to_administer_CRFs_page() {
		basePage.goToAdministerCRFsPage();
	}

	private void go_to_manage_rules_page() {
		basePage.goToManageRulesPage();
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
		Assert.assertEquals(studyName, basePage.getCurrentStudyName());
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
	public void check_data_in_crf(CRF crf) {
		assertThat(Common.checkAllTrue(crfPage.checkDataInCRF(crf))).isTrue();
		crfPage.clickExit();
	}

	@Step
	public void click_save_button_on_CRF_page() {
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
		Map<String, String> fromWinToWin = switch_to_another_window("");
		String oldWindowId = fromWinToWin.get("from");
		String newWindowId = fromWinToWin.get("to");
		switch (dn.getEntityType()) {
		case "CRF":
			dnPage.fillInAndSaveDN(dn, newWindowId);
			break;
		case "Event":
			dnPage.fillInAndSaveDNForEvent(dn, newWindowId);
			break;
		case "Study Subject":
			dnPage.fillInAndSaveDN(dn, newWindowId);
			break;
		case "Subject":
			dnPage.fillInAndSaveDN(dn, newWindowId);
			break;
		default:
			dnPage.fillInAndSaveDN(dn, newWindowId);
		}

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

	public Map<String, String> switch_to_another_window(String windowId) {
		// Store the current window handle
		Map<String, String> fromWinToWin = new HashMap<String, String>();

		if (windowId.isEmpty()) {
			// Switch to new window opened
			int i = 0;
			for (String winHandle : getDriver().getWindowHandles()) {
				if (i == 0)
					fromWinToWin.put("from", winHandle);
				if (i == getDriver().getWindowHandles().size() - 1) {
					fromWinToWin.put("to", winHandle);
					getDriver().switchTo().window(winHandle);
				}
				i++;
			}
		} else {
			try {
				fromWinToWin.put("from", getDriver().getWindowHandle());
			} catch (NoSuchWindowException e) {
				fromWinToWin.put("from", "");
			}

			for (String winHandle : getDriver().getWindowHandles()) {
				if (windowId.equals(winHandle)) {
					fromWinToWin.put("to", winHandle);
					getDriver().switchTo().window(winHandle);
				}
			}
		}

		// Close the new window, if that window no more required
		// webdriver.close();

		// Switch back to original browser (first window)
		// webdriver.switchTo().window(winHandleBefore);

		return fromWinToWin;
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
		String oldWindowId = switch_to_another_window("").get("from");
		dnPage.findAndFillInAndClickSubmit(dn);
		switch_to_another_window(oldWindowId);
	}

	@Step
	public void click_element_on_page(String page, String element) {
		switch (page) {
		case UpdateSubjectDetailsPage.PAGE_NAME:
			switch (element) {
			case "'Person ID' flag":
				updateSubjectDetailsPage.clickPersonIDFlag();
				break;
			case "'Gender' flag":
				updateSubjectDetailsPage.clickGenderFlag();
				break;
			case "'Date of Birth' flag":
				updateSubjectDetailsPage.clickDOBFlag();
				break;
			}
			break;
		case ViewSubjectRecordPage.PAGE_NAME:
			switch (element) {
			case "'Study Subject Record' link":
				viewSubjectRecordPage.clickStudySubjectRecordLink();
				break;
			case "'Date of Enrollment for Study' flag":
				viewSubjectRecordPage.clickEnrollmentDateFlag();
				break;
			}
			break;
		case AddSubjectPage.PAGE_NAME:
			switch (element) {
			case "'Add Next Subject' button":
				addSubjectPage.clickAddNextSubjectButton();
				break;
			}
			break;
		case CreateCRFDataCommitedPage.PAGE_NAME:
			switch (element) {
			case "'Exit' button":
				createCRFDataCommitedPage.clickExitButton();
				break;
			case "'View CRF' button":
				createCRFDataCommitedPage.clickViewCRFButton();
				break;
			case "'View CRF Metadata' button":
				createCRFDataCommitedPage.clickViewCRFMetadataButton();
				break;
			}
			break;
		case AdministerCRFsPage.PAGE_NAME:
			switch (element) {
			case "'Create CRF' button":
				administerCRFsPage.clickCreateCRFButton();
				break;
			}
			break;
		case ImportRuleDataPage.PAGE_NAME:
			switch (element) {
			case "'Yes' button in popup":
				importRuleDataPage.clickYesButtonInPopup();
				break;
			}
			break;
		case ManageRulesPage.PAGE_NAME:
			switch (element) {
			case "'Import Rules' button":
				manageRulesPage.clickImportRulesButton();
				break;
			}
			break;
		case SignStudyEventPage.PAGE_NAME:
			switch (element) {
			case "'Sign' button":
				click_sign_button_on_sign_study_event_page();
				break;
			}
			break;
		case BuildStudyPage.PAGE_NAME:
			switch (element) {
			case "'Add Site' button":
				click_add_site_button();
				break;
			case "'Update Study' button":
				click_update_study();
				break;
			case "'Add CRF' button":
				click_add_crf_button();
				break;
			case "'Add Event Definitions' button":
				click_add_event_definition_button();
				break;
			}
			break;
		case "popup":
			switch (element) {
			case "'Schedule Event' button":
				click_schedule_event_button_in_popup();
				break;
			case "'Sign Event' button":
				click_sign_event_button_in_popup();
				break;
			case "'Start Date' flag":
				click_start_date_flag_in_popup();
				break;
			case "'End Date' flag":
				click_end_date_flag_in_popup();
				break;
			case "'Location' flag":
				click_location_flag_in_popup();
				break;
			}
			break;

		default:
			;
		}
	}

	public void click_start_date_flag_in_popup() {
		subjectMatrixPage.clickStartDateFlagInPopup();
	}

	public void click_end_date_flag_in_popup() {
		subjectMatrixPage.clickEndDateFlagInPopup();
	}

	public void click_location_flag_in_popup() {
		subjectMatrixPage.clickLocationFlagInPopup();
	}

	@Step
	public void browse_file_with_rule(String filepath) {
		importRuleDataPage.browseRuleFile(filepath);
	}

	@Step
	public void see_message(String message) {
		basePage.message_is_shown(message);
	}

	@Step
	public void filter_administer_CRFs_page(CRF crf) {
		administerCRFsPage.filter(crf);
	}

	@Step
	public void check_CRF_row_is_present(CRF crf) {
		administerCRFsPage.checkCRFRowIsPresent(crf);
	}

	@Step
	public void set_CRF_parameters(CRF crf) {
		previewCRFPage.setCRFParameters(crf);
	}

	@Step
	public void filter_manage_event_definitions_page(StudyEventDefinition event) {
		manageEventDefinitionsPage.filter(event);
	}

	@Step
	public void check_event_row_is_present(StudyEventDefinition event) {
		manageEventDefinitionsPage.checkEventRowIsPresent(event);
	}

	@Step
	public void check_row_is_present_on_SM() {
		subjectMatrixPage.checkFirstRowIsPresent();
	}

	@Step
	public void click_view_icon_for_study_subject_on_SM(String studySubjectID) {
		subjectMatrixPage.clickViewIconForStudySubject(studySubjectID);
	}

	@Step
	public void click_edit_icon_for_subject_on_Administer_Subjects_page(String studyIDStSubjectID) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Protocol-Study Subject IDs", studyIDStSubjectID);
		administerSubjectsPage.filterAdministerSubjectsPage(map);
		administerSubjectsPage.clickEditIconForSubject(studyIDStSubjectID);
	}

	@Step
	public void view_DN_on_NDs_page(DNote dn) {
		notesAndDiscrepanciesPage.clickViewDNIcon();
	}

	@Step
	public void exit_from_DN() {
		String oldWindowId = switch_to_another_window("").get("from");
		dnPage.clickCloseWindowButton();
		switch_to_another_window(oldWindowId);
	}

	public void clear_filter_NDs_page(DNote dn) {
		notesAndDiscrepanciesPage.clickClearFilterLink();
	}

	@Step
	public void click_section_tab_in_crf(String sectionName) {
		crfPage.clickSectionTabInCRF(sectionName);
	}

	@Step
	public void click_more_info_button() {
		crfPage.clickMoreInfoButton();
	}

	@Step
	public void leave_CRF_without_saving() {
		crfPage.clickCancelButton();
		crfPage.clickYesAtDlg();
	}

	@Step
	public void verify_error_message_on_CRF(String errorMessage) {
		crfPage.verifyErrorMessage(errorMessage);
	}
}