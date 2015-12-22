package org.akaza.openclinica.i18n.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import com.clinovo.i18n.ClinCaptureResourceBundle;
import com.clinovo.i18n.ClinCaptureResourceBundleLoader;

/**
 * ResourceBundleProvider.
 */
public final class ResourceBundleProvider {

	public static final String UTF_8 = "UTF-8";

	private static HashMap<Thread, Locale> localeMap = new HashMap<Thread, Locale>();

	private static ClinCaptureResourceBundleLoader clinCaptureResourceBundleLoader;

	static {
		clinCaptureResourceBundleLoader = new ClinCaptureResourceBundleLoader();
		clinCaptureResourceBundleLoader.setDefaultEncoding(UTF_8);
	}

	private ResourceBundleProvider() {
	}

	/**
	 * Updates current thread locale.
	 *
	 * @param locale
	 *            Locale
	 */
	public static synchronized void updateLocale(Locale locale) {
		localeMap.put(Thread.currentThread(), locale);
	}

	/**
	 * Returns current thread locale.
	 *
	 * @return Locale
	 */
	public static Locale getLocale() {
		return localeMap.get(Thread.currentThread());
	}

	/**
	 * Returns admin ResourceBundle.
	 *
	 * @return ResourceBundle
	 */
	public static ResourceBundle getAdminBundle() {
		return getResBundle("org.akaza.openclinica.i18n.admin");
	}

	/**
	 * Returns admin ResourceBundle.
	 *
	 * @param locale
	 *            Locale
	 * @return ResourceBundle
	 */
	public static ResourceBundle getAdminBundle(Locale locale) {
		return getResBundle("org.akaza.openclinica.i18n.admin", locale);
	}

	/**
	 * Returns audit_events ResourceBundle.
	 *
	 * @return ResourceBundle
	 */
	public static ResourceBundle getAuditEventsBundle() {
		return getResBundle("org.akaza.openclinica.i18n.audit_events");
	}

	/**
	 * Returns audit_events ResourceBundle.
	 *
	 * @param locale
	 *            Locale
	 * @return ResourceBundle
	 */
	public static ResourceBundle getAuditEventsBundle(Locale locale) {
		return getResBundle("org.akaza.openclinica.i18n.audit_events", locale);
	}

	/**
	 * Returns exceptions ResourceBundle.
	 *
	 * @return ResourceBundle
	 */
	public static ResourceBundle getExceptionsBundle() {
		return getResBundle("org.akaza.openclinica.i18n.exceptions");
	}

	/**
	 * Returns exceptions ResourceBundle.
	 *
	 * @param locale
	 *            Locale
	 * @return ResourceBundle
	 */
	public static ResourceBundle getExceptionsBundle(Locale locale) {
		return getResBundle("org.akaza.openclinica.i18n.exceptions", locale);
	}

	/**
	 * Returns format ResourceBundle.
	 *
	 * @return ResourceBundle
	 */
	public static ResourceBundle getFormatBundle() {
		return getResBundle("org.akaza.openclinica.i18n.format");
	}

	/**
	 * Returns format ResourceBundle.
	 *
	 * @param locale
	 *            Locale
	 * @return ResourceBundle
	 */
	public static ResourceBundle getFormatBundle(Locale locale) {
		return getResBundle("org.akaza.openclinica.i18n.format", locale);
	}

	/**
	 * Returns page_messages ResourceBundle.
	 *
	 * @return ResourceBundle
	 */
	public static ResourceBundle getPageMessagesBundle() {
		return getResBundle("org.akaza.openclinica.i18n.page_messages");
	}

	/**
	 * Returns page_messages ResourceBundle.
	 *
	 * @param locale
	 *            Locale
	 * @return ResourceBundle
	 */
	public static ResourceBundle getPageMessagesBundle(Locale locale) {
		return getResBundle("org.akaza.openclinica.i18n.page_messages", locale);
	}

	/**
	 * Returns terms ResourceBundle.
	 *
	 * @return ResourceBundle
	 */
	public static ResourceBundle getTermsBundle() {
		return getResBundle("org.akaza.openclinica.i18n.terms");
	}

	/**
	 * Returns terms ResourceBundle.
	 *
	 * @param locale
	 *            Locale
	 * @return ResourceBundle
	 */
	public static ResourceBundle getTermsBundle(Locale locale) {
		return getResBundle("org.akaza.openclinica.i18n.terms", locale);
	}

	/**
	 * Returns words ResourceBundle.
	 *
	 * @return ResourceBundle
	 */
	public static ResourceBundle getWordsBundle() {
		return getResBundle("org.akaza.openclinica.i18n.words");
	}

	/**
	 * Returns words ResourceBundle.
	 *
	 * @param locale
	 *            Locale
	 * @return ResourceBundle
	 */
	public static ResourceBundle getWordsBundle(Locale locale) {
		return getResBundle("org.akaza.openclinica.i18n.words", locale);
	}

	/**
	 * Returns notes ResourceBundle.
	 *
	 * @return ResourceBundle
	 */
	public static ResourceBundle getTextsBundle() {
		return getResBundle("org.akaza.openclinica.i18n.notes");
	}

	/**
	 * Returns notes ResourceBundle.
	 *
	 * @param locale
	 *            Locale
	 * @return ResourceBundle
	 */
	public static ResourceBundle getTextsBundle(Locale locale) {
		return getResBundle("org.akaza.openclinica.i18n.notes", locale);
	}

	/**
	 * Returns workflow ResourceBundle.
	 *
	 * @return ResourceBundle
	 */
	public static ResourceBundle getWorkflowBundle() {
		return getResBundle("org.akaza.openclinica.i18n.workflow");
	}

	/**
	 * Returns workflow ResourceBundle.
	 *
	 * @param locale
	 *            Locale
	 * @return ResourceBundle
	 */
	public static ResourceBundle getWorkflowBundle(Locale locale) {
		return getResBundle("org.akaza.openclinica.i18n.workflow", locale);
	}

	/**
	 * Returns buildNumber ResourceBundle.
	 *
	 * @return ResourceBundle
	 */
	public static ResourceBundle getBuildNumberBundle() {
		return getResBundle("org.akaza.openclinica.i18n.buildNumber");
	}

	/**
	 * Returns buildNumber ResourceBundle.
	 *
	 * @param locale
	 *            Locale
	 * @return ResourceBundle
	 */
	public static ResourceBundle getBuildNumberBundle(Locale locale) {
		return getResBundle("org.akaza.openclinica.i18n.buildNumber", locale);
	}

	/**
	 * Returns the required bundle, using the current thread to determine the appropiate locale.
	 *
	 * @param name
	 *            requested bundle name.
	 * @return ResourceBundle
	 */
	private static ResourceBundle getResBundle(String name) {
		return new ClinCaptureResourceBundle(clinCaptureResourceBundleLoader, name,
				localeMap.get(Thread.currentThread()));
	}

	/**
	 *
	 * @param name
	 *            Required bundle name
	 * @param locale
	 *            Required locale
	 * @return The corresponding ResourceBundle
	 */
	public static ResourceBundle getResBundle(String name, Locale locale) {
		return new ClinCaptureResourceBundle(clinCaptureResourceBundleLoader, name, locale);
	}

	/**
	 *
	 * @param name
	 *            Required bundle name
	 * @param locale
	 *            Required locale
	 * @return The corresponding ResourceBundle
	 */
	public static ResourceBundle getResBundleForMessageSource(String name, Locale locale) {
		return new ClinCaptureResourceBundle(clinCaptureResourceBundleLoader, true, name, locale);
	}

	/**
	 * Method returns property value from admin bundle.
	 *
	 * @param key
	 *            String
	 * @return String
	 */
	public static String getResAdmin(String key) {
		return getAdminBundle().containsKey(key) ? getAdminBundle().getString(key) : key;
	}

	/**
	 * Method returns property value from terms bundle.
	 *
	 * @param key
	 *            String
	 * @return String
	 */
	public static String getResTerm(String key) {
		return getTermsBundle().containsKey(key) ? getTermsBundle().getString(key) : key;
	}

	/**
	 * Method returns property value from words bundle.
	 *
	 * @param key
	 *            String
	 * @return String
	 */
	public static String getResWord(String key) {
		return getWordsBundle().containsKey(key) ? getWordsBundle().getString(key) : key;
	}

	/**
	 * Method returns property value from notes bundle.
	 *
	 * @param key
	 *            String
	 * @return String
	 */
	public static String getResNotes(String key) {
		return getTextsBundle().containsKey(key) ? getTextsBundle().getString(key) : key;
	}

	/**
	 * Method returns property value from format bundle.
	 *
	 * @param key
	 *            String
	 * @return String
	 */
	public static String getResFormat(String key) {
		return getFormatBundle().containsKey(key) ? getFormatBundle().getString(key) : key;
	}
}
