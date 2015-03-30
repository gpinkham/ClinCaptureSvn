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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.rest.exception;

import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.dao.core.CoreResources;
import org.springframework.context.MessageSource;

/**
 * RestException.
 */
@SuppressWarnings("serial")
public class RestException extends Exception {

	private int code;

	/**
	 * RestException constructor.
	 *
	 * @param messageSource
	 *            MessageSource
	 * @param messageCode
	 *            String
	 */
	public RestException(MessageSource messageSource, String messageCode) {
		super(messageSource.getMessage(messageCode, null, CoreResources.getSystemLocale()));
		this.code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	}

	/**
	 * RestException constructor.
	 *
	 * @param messageSource
	 *            MessageSource
	 * @param messageCode
	 *            String
	 * @param code
	 *            integer
	 */
	public RestException(MessageSource messageSource, String messageCode, int code) {
		super(messageSource.getMessage(messageCode, null, CoreResources.getSystemLocale()));
		this.code = code;
	}

	/**
	 * RestException constructor.
	 *
	 * @param messageSource
	 *            MessageSource
	 * @param messageCode
	 *            String
	 * @param args
	 *            Object[]
	 * @param code
	 *            integer
	 */
	public RestException(MessageSource messageSource, String messageCode, Object[] args, int code) {
		super(messageSource.getMessage(messageCode, args, CoreResources.getSystemLocale()));
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
