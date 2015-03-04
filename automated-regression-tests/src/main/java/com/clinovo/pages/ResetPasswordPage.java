package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

import com.clinovo.utils.User;

/**
 * Created by Igor.
 */
public class ResetPasswordPage extends BasePage {

	public static final String PAGE_NAME = "Reset Password page";
	public static final String PAGE_URL = "ResetPassword";
	
	@FindBy(name="oldPasswd")
    private WebElementFacade iOldPassword;

    @FindBy(name="passwd")
    private WebElementFacade iNewPassword;
    
    @FindBy(name="passwd1")
    private WebElementFacade iConfirmPassword;
    
    @FindBy(name="passwdChallengeQ")
    private WebElementFacade sChallengeQuestion;

    @FindBy(name="passwdChallengeA")
    private WebElementFacade iChallengeAnswer;
    
    @FindBy(name="exit")
    private WebElementFacade bExit;
    
    public ResetPasswordPage (WebDriver driver) {
        super(driver);
    }

    public void fillOldPasswordField(String oldPassword) {
    	iOldPassword.type(oldPassword);
    }

    public void fillNewPasswordField(String newPassword) {
    	iNewPassword.type(newPassword);
    }

    public void fillConfirmPasswordField(String confirmPassword) {
    	iConfirmPassword.type(confirmPassword);
    }
    
    public void fillChallengeQuestionSelect(User user) {
    	if (user.getChallengeQuestion() == null || 
    			user.getChallengeQuestion().trim().isEmpty()) {
    		sChallengeQuestion.selectByIndex(user.getChallengeQuestionIndex());
		} else {
			sChallengeQuestion.selectByVisibleText(user.getChallengeQuestion());
		}
    }
    
    public void fillChallengeAnswerField(String challengeAnswer) {
    	iChallengeAnswer.type(challengeAnswer);
    }

	public boolean isOnPage() {
		return iOldPassword.isDisplayed() && iNewPassword.isDisplayed() && iConfirmPassword.isDisplayed();
	}

	public void fillInResetPasswordPage(User user) {
		fillOldPasswordField(user.getOldPassword());
		fillNewPasswordField(user.getPassword());
		fillConfirmPasswordField(user.getPassword());
		fillChallengeQuestionSelect(user);
	    fillChallengeAnswerField(user.getChallengeAnswer());
	}
}
