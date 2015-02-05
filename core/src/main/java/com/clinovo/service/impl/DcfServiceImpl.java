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
package com.clinovo.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.dao.SystemDAO;
import com.clinovo.model.DiscrepancyCorrectionForm;
import com.clinovo.reporting.DcfReportBuilder;
import com.clinovo.service.DcfService;
import com.clinovo.util.DcfRenderType;

/**
 * Implements DcfService.
 * 
 * @author Frank
 * 
 */
@Service("dcfService")
public class DcfServiceImpl implements DcfService {

	@Autowired
	private SystemDAO systemDAO;
	@Autowired
	private DataSource datasource;

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	private List<DcfRenderType> renderTypes;
	private DcfReportBuilder dcfReportBuilder;
	private DiscrepancyNoteDAO discrepancyNoteDAO;

	/**
	 * Constructor.
	 */
	public DcfServiceImpl() {
		dcfReportBuilder = new DcfReportBuilder();
		renderTypes = new ArrayList<DcfRenderType>();
	}

	/**
	 * {@inheritDoc}
	 */
	public String generateDcf(StudyBean study, ResourceBundle resword, List<Integer> noteIds) {
		List<DiscrepancyCorrectionForm> dcfs = getDiscrepancyNoteDAO().getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, noteIds.toArray(new Integer[0]));
		String fileName = null;
		if (dcfs.size() > 0) {
			try {
				fileName = getFileName(dcfs, ".pdf");
				dcfReportBuilder.buildPdf(dcfs, fileName);
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage());
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
		return fileName;
	}

	private String getFileName(List<DiscrepancyCorrectionForm> dcfs, String extension) throws IOException {
		String dcfDirName = "";
		String ccRepoPath = systemDAO.findByName("filePath").getValue();
		if (ccRepoPath.endsWith(File.separator) || ccRepoPath.trim().length() == 0) {
			dcfDirName = ccRepoPath.concat("dcf");
		} else {
			dcfDirName = ccRepoPath.concat(File.separator).concat("dcf");
		}
		File dcfDir = new File(dcfDirName);
		if (!dcfDir.exists()) {
			dcfDir.mkdir();
		}
		String fileName = dcfs.get(0).getDcfFileName().concat(extension);
		fileName = dcfDirName.concat(File.separator).concat(fileName);
		logger.debug("Created file:" + fileName);
		return fileName;
	}

	/**
	 * {@inheritDoc}
	 */
	public void renderDcf() {
		// TODO To be implemented in #1997
	}

	/**
	 * {@inheritDoc}
	 */
	public void addDcfRenderType(DcfRenderType renderType) {
		renderTypes.add(renderType);
	}

	private DiscrepancyNoteDAO getDiscrepancyNoteDAO() {
		if (discrepancyNoteDAO == null) {
			discrepancyNoteDAO = new DiscrepancyNoteDAO(datasource);
		}
		return discrepancyNoteDAO;
	}
}
