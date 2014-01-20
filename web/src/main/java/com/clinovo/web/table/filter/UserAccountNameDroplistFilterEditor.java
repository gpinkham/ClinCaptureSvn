/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.web.table.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.view.html.editor.DroplistFilterEditor;

public class UserAccountNameDroplistFilterEditor extends DroplistFilterEditor {

	private DataSource dataSource;

	/**
	 * Constructs an instance of the filter. It assumes that the supplied data source and study are valid and
	 * functional.
	 * 
	 * @param dataSource
	 *            Valid DataBase connection object
	 */
	public UserAccountNameDroplistFilterEditor(DataSource dataSource) {

		this.dataSource = dataSource;
	}

	@Override
	protected List<Option> getOptions() {

		List<Option> options = new ArrayList<Option>();
		List<UserAccountBean> userAccounts = getUserAccounts();
		ResourceBundle reterm = ResourceBundleProvider.getTermsBundle();

		options.add(new Option(reterm.getString("not_assigned"), reterm.getString("not_assigned")));
		
		for (UserAccountBean userAccount : userAccounts) {
			options.add(new Option(userAccount.getName(), userAccount.getFirstName() + " " + userAccount.getLastName() 
					+ " (" + userAccount.getName() + ")"));
		}

		return options;
	}

	@SuppressWarnings({ "unchecked" })
	private List<UserAccountBean> getUserAccounts() {

		UserAccountDAO userAccountDAO = new UserAccountDAO(dataSource);
		List<UserAccountBean> userAccounts = (List<UserAccountBean>) userAccountDAO.findAll();

		return userAccounts;
	}
}
