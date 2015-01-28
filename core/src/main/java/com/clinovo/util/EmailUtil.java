package com.clinovo.util;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class was created to avoid repeating of the same parts,
 * that are present in emails, like footer and others.
 */
public class EmailUtil {

	private static ResourceBundle resword;
	private static ResourceBundle resnotes;

	/**
	 * This method will return an email footer, with
	 * social networks icons and privacy policy text.
	 *
	 * @param locale the locale of text.
	 * @return html formatted footer.
	 */
	public static String getEmailFooter(Locale locale) {
		ResourceBundleProvider.updateLocale(locale);
		resword = ResourceBundleProvider.getWordsBundle();
		resnotes = ResourceBundleProvider.getPageMessagesBundle();
		String text = "";
		int year = Calendar.getInstance().get(Calendar.YEAR);
		return text
				.concat("<table width='600px' align='center' cellpadding='0' cellspacing='0' style='font-family:helvetica,trebuchet ms,arial,times new roman; font-size:10pt'><tr><td style='height:10px'></td></tr><tr><td>")
				.concat("<table cellpadding='0' cellspacing='0'><tr><td style='border-right: 1px solid #CCCCCC;border-bottom: 1px solid #CCCCCC;border-left: 1px solid #CCCCCC;border-top: 4px solid #893BC9;'><table cellpadding='10' cellspacing='0'><tr><td>")
				.concat("<img src='https://clincapture.clinovo.com/builder/assets/images/email_template_header.jpg' /></td></tr><tr>")
				.concat("<td style='vertical-align:top; font-family:Arial, Helvetica, sans-serif; font-size:12px; font-weight:normal; color:#333; line-height:20px;'>")
				.concat(resnotes.getString("email_footer"))
				.concat("</td></tr><tr><td style='vertical-align:top;' align='center'>")
				.concat("<a href='http://www.linkedin.com/company/clinovo' target='_blank'><img src='https://clincapture.clinovo.com/builder/assets/images/email_template_social_link_linkedin.jpg' /></a>")
				.concat("<a href='https://www.facebook.com/Clinovo' target='_blank'><img src='https://clincapture.clinovo.com/builder/assets/images/email_template_social_link_facebook.jpg' /></a>")
				.concat("<a href='https://twitter.com/Clinovo' target='_blank'><img src='https://clincapture.clinovo.com/builder/assets/images/email_template_social_link_twitter.jpg' /></a>")
				.concat("<img src='https://clincapture.clinovo.com/builder/assets/images/email_template_social_link_call.jpg' /></td></tr></table></td></tr></table></td></tr>")
				.concat("<tr><td style='height:20px'></td></tr><tr><td style='height:60px; color:#333333;'> <font face='helvetica' color='#454545'>")
				.concat(resword.getString("mail.respect_privacy"))
				.concat("<a style='color:#0088CF' target'_blank' href='https://www.clinovo.com/Privacy-Policy'> ")
				.concat(resword.getString("mail.privacy_policy"))
				.concat(" </a>")
				.concat(resword.getString("mail.more_information"))
				.concat(" <br />Copyright&copy; ")
				.concat(Integer.toString(year))
				.concat(" Clinovo, 1208 E Arques Ave Suite 114, Sunnyvale, 94085 CA</font></td></tr><tr><td style='height:20px'></td></tr></table>");
	}

	/**
	 * This method will return an opening tags of the main table
	 * for email body.
	 *
	 * @return html formatted email body opening tags.
	 */
	public static String getEmailBodyStart() {
		String text = "";
		return text
				.concat("<table style='width:700px' align='center' cellpadding='0' cellspacing='0' style='font-family:helvetica,trebuchet ms,arial,times new roman; font-size:10pt'>")
				.concat("<tr><td><div style='width:700px;font-family: helvetica;'>");
	}

	/**
	 * This method will return closing tags of the main table
	 * for email body.
	 *
	 * @return html formatted email body closing tags.
	 */
	public static String getEmailBodyEnd() {
		String text = "";
		return text.concat("</td></tr></table>");
	}
}