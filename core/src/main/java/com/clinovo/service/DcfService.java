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
package com.clinovo.service;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;

import com.clinovo.util.DcfRenderType;

/**
 * Discrepancy Correction From service.
 * 
 * @author Frank
 * 
 */
public interface DcfService {

	/**
	 * Generates DCFs for list of DNs.
	 * 
	 * @param study
	 *            current study
	 * @param noteIds
	 *            Set of IDs of DNs for which to generate DCFs
	 * @param username
	 *            Current user's username
	 * @return path to generated DCF file; null if generation was not successful.
	 * @throws FileNotFoundException
	 *             in case CC repository path doesn't exits on the server
	 */
	String generateDcf(StudyBean study, Set<Integer> noteIds, String username) throws FileNotFoundException;

	/**
	 * Adds DCF render type.
	 * 
	 * @param renderType
	 *            DcfRenderer
	 */
	void addDcfRenderType(DcfRenderType renderType);

	/**
	 * Clears render types.
	 */
	void clearRenderTypes();

	/**
	 * Renders generated DCFs.
	 * 
	 * @return true if all render types are successful, false otherwise
	 */
	boolean renderDcf();

	/**
	 * Updates DiscrepancyNotes after DCFs have been generated and rendered.
	 * 
	 * @param noteAndEntityIds
	 *            Map of DN Ids and their Entity Ids
	 * @param currentStudy
	 *            StudyBean
	 * @param currentUser
	 *            UserAccountBean
	 */
	void updateDiscrepancyNotes(Map<Integer, Map<Integer, String>> noteAndEntityIds, StudyBean currentStudy,
			UserAccountBean currentUser);
}
