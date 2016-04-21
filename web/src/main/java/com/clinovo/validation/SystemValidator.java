/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.validation;

import com.clinovo.command.SystemCommand;
import com.clinovo.command.SystemGroupHolder;
import com.clinovo.model.PropertyType;
import com.clinovo.model.PropertyValueType;
import org.akaza.openclinica.util.StringValidator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Locale;

/**
 * System page validator class.
 *
 */
@Component
@SuppressWarnings("unused")
public class SystemValidator {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SystemValidator.class);
	public static final int LOGO_MAX_SIZE = 20000;

	@Autowired
	@Qualifier("validator")
	private Validator validator;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Do validate.
	 * 
	 * @param command
	 *            command
	 * @param errors
	 *            errors
	 * @param locale
	 *            Locale to be used
	 */
	public void validate(SystemCommand command, Errors errors, Locale locale) {
		validator.validate(command, errors);
		if (!errors.hasErrors() && command.getLogoFile() != null && command.getLogoFile().getSize() != 0) {
			if (command.getLogoFile().getSize() > LOGO_MAX_SIZE) {
				errors.rejectValue("logoFile", "error.systemProperty.logoFile.maxFileSizeExceeded");
			} else if (!command.getLogoFile().getOriginalFilename().toLowerCase().endsWith(".jpg")
					&& !command.getLogoFile().getOriginalFilename().toLowerCase().endsWith(".jpeg")) {
				errors.rejectValue("logoFile", "error.systemProperty.logoFile.forbiddenExtention");
			}
		}
		if (!errors.hasErrors()) {
			for (SystemGroupHolder sgh : command.getSystemPropertyGroups()) {
				boolean opened = sgh.isOpened();
				String path = "systemPropertyGroups['" + command.getSystemPropertyGroups().indexOf(sgh) + "']";
				for (SystemGroupHolder subSgh : sgh.getSubGroups()) {
					validate(subSgh, path + ".subGroups['" + sgh.getSubGroups().indexOf(subSgh) + "']", errors, locale);
					opened = !opened && subSgh.isOpened() || opened;
				}
				sgh.setOpened(opened);
				validate(sgh, path, errors, locale);
			}
			// Validate Medical Coding
			validateMedicalCoding(command, errors);
		}
	}

	private void validate(SystemGroupHolder systemGroupHolder, String path, Errors errors, Locale locale) {
		for (com.clinovo.model.System systemProperty : systemGroupHolder.getSystemProperties()) {
			if (systemProperty.getType() != PropertyType.DYNAMIC_INPUT
					&& systemProperty.getType() != PropertyType.DYNAMIC_RADIO) {
				String label = messageSource.getMessage("systemProperty." + systemProperty.getName() + ".label", null,
						locale);
				String name = path + ".systemProperties['"
						+ systemGroupHolder.getSystemProperties().indexOf(systemProperty) + "'].value";
				if (systemProperty.getValueType() == PropertyValueType.INTEGER) {
					try {
						Integer.parseInt(systemProperty.getValue());
					} catch (Exception ex) {
						systemGroupHolder.setOpened(true);
						errors.rejectValue(name, "error.systemProperty.shouldBeInteger", new Object[] { label }, "");
					}
				} else if (systemProperty.getValueType() == PropertyValueType.FLOAT) {
					try {
						Float.parseFloat(systemProperty.getValue());
					} catch (Exception ex) {
						systemGroupHolder.setOpened(true);
						errors.rejectValue(name, "error.systemProperty.shouldBeFloat", new Object[] { label }, "");
					}
				} else if (systemProperty.isRequired()
						&& (systemProperty.getValue() == null || systemProperty.getValue().trim().isEmpty())) {
					systemGroupHolder.setOpened(true);
					errors.rejectValue(name, "error.systemProperty.isRequired", new Object[] { label }, "");
				}
			}
		}
	}

	private void validateMedicalCoding(SystemCommand command, Errors errors) {

		String defaultBioontologyURL = "";
		String medicalCodingApiKey = "";
		String path = "";
		String pathForBioontologyURL = "";
		String pathForCodingApiKey = "";
		boolean opened = false;
		// Set defaultBioontologyURL and medicalCodingApiKey
		for (SystemGroupHolder sgh : command.getSystemPropertyGroups()) {

			if (sgh.getGroup().getName().equals("medical_coding")) {
				opened = sgh.isOpened();
				path = "systemPropertyGroups['" + command.getSystemPropertyGroups().indexOf(sgh) + "']";
				List<com.clinovo.model.System> properties = sgh.getSystemProperties();
				for (com.clinovo.model.System property : properties) {

					if (property.getName().equals("defaultBioontologyURL")) {
						property.setValue(property.getValue().trim());
						defaultBioontologyURL = property.getValue();
						pathForBioontologyURL = path + ".systemProperties['" + properties.indexOf(property)
								+ "'].value";
					} else if (property.getName().equals("medicalCodingApiKey")) {
						property.setValue(property.getValue().trim());
						medicalCodingApiKey = property.getValue();
						pathForCodingApiKey = path + ".systemProperties['" + properties.indexOf(property) + "'].value";
					}
				}
				opened = !performValidation(errors, defaultBioontologyURL, medicalCodingApiKey, pathForBioontologyURL,
						pathForCodingApiKey) || opened;
				sgh.setOpened(opened);
				break;
			}
		}
	}

	private boolean performValidation(Errors errors, String defaultBioontologyURL, String medicalCodingApiKey,
			String pathForBioontologyURL, String pathForCodingApiKey) {
		boolean valid = true;
		if (!defaultBioontologyURL.isEmpty()) {
			if (!StringValidator.isValidURL(defaultBioontologyURL)) {
				errors.rejectValue(pathForBioontologyURL, "please_provide_a_valid_url");
				valid = false;
			}
			if (defaultBioontologyURL.endsWith("/")) {
				errors.rejectValue(pathForBioontologyURL, "please_remove_the_final_slash");
				valid = false;
			}
			if (medicalCodingApiKey.isEmpty()) {
				errors.rejectValue(pathForCodingApiKey, "please_supply_the_biootology_api_key");
				valid = false;
			}
		}
		return valid;
	}
}
