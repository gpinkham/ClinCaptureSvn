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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
@Scope("prototype")
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
	public String generateDcf(StudyBean study, Set<Integer> noteIds, String username) throws FileNotFoundException {
		ResourceBundle resword = ResourceBundleProvider.getWordsBundle(CoreResources.getSystemLocale());
		List<DiscrepancyCorrectionForm> dcfs = getDiscrepancyNoteDAO().getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, noteIds.toArray(new Integer[0]));
		String fileName = null;
		if (dcfs.size() > 0) {
			try {
				fileName = getFileName(dcfs, username, ".pdf");
				dcfReportBuilder.buildPdf(dcfs, fileName);
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage());
				throw e;
			} catch (IOException e) {
				logger.error(e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return fileName;
	}

	private String getFileName(List<DiscrepancyCorrectionForm> dcfs, String username, String extension)
			throws FileNotFoundException {
		String dcfDirName = "";
		String dcfUserDirName = "";
		String ccRepoPath = systemDAO.findByName("filePath").getValue().trim();
		File ccRepoDir = new File(ccRepoPath);
		ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle(CoreResources.getSystemLocale());
		if (ccRepoPath.length() > 0 && !ccRepoDir.exists()) {
			String exceptionMessage = MessageFormat.format(resexception.getString("cc_repo_directory_not_found"),
					ccRepoPath);
			throw new FileNotFoundException(exceptionMessage);
		}
		if (ccRepoPath.endsWith(File.separator) || ccRepoPath.trim().length() == 0) {
			dcfDirName = ccRepoPath.concat("dcf");
		} else {
			dcfDirName = ccRepoPath.concat(File.separator).concat("dcf").concat(File.separator).concat(username);
		}
		dcfUserDirName = dcfDirName.concat(File.separator).concat(username);
		File dcfDir = new File(dcfDirName);
		File dcfUserDir = new File(dcfUserDirName);
		if (!dcfDir.exists()) {
			dcfDir.mkdir();
		}
		if (!dcfUserDir.exists()) {
			dcfUserDir.mkdir();
		}
		String fileName = dcfs.get(0).getDcfFileName().concat(extension);
		fileName = dcfUserDirName.concat(File.separator).concat(fileName);
		logger.debug("Created file:" + fileName);
		return fileName;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean renderDcf() {
		boolean renderSuccessful = true;
		for (DcfRenderType renderType : renderTypes) {
			renderSuccessful = renderType.render();
			if (!renderSuccessful) {
				return renderSuccessful;
			}
		}
		return renderSuccessful;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addDcfRenderType(DcfRenderType renderType) {
		renderTypes.add(renderType);
	}

	/**
	 * {@inheritDoc}
	 */
	public void clearRenderTypes() {
		if (renderTypes != null) {
			renderTypes.clear();
		} else {
			renderTypes = new ArrayList<DcfRenderType>();
		}
	}

	private DiscrepancyNoteDAO getDiscrepancyNoteDAO() {
		if (discrepancyNoteDAO == null) {
			discrepancyNoteDAO = new DiscrepancyNoteDAO(datasource);
		}
		return discrepancyNoteDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateDiscrepancyNotes(Map<Integer, Map<Integer, String>> noteAndEntityIds, StudyBean currentStudy,
			UserAccountBean currentUser) {
		DiscrepancyNoteDAO dndao = getDiscrepancyNoteDAO();
		for (Integer noteId : noteAndEntityIds.keySet()) {
			DiscrepancyNoteBean note = (DiscrepancyNoteBean) getDiscrepancyNoteDAO().findByPK(noteId);
			for (Integer entityId : noteAndEntityIds.get(noteId).keySet()) {
				note.setEntityId(entityId);
				note.setColumn(noteAndEntityIds.get(noteId).get(entityId));
			}
			note.setResolutionStatusId(ResolutionStatus.UPDATED.getId());
			createChildDN(note, dndao, currentStudy, currentUser);
			note.setUpdater(currentUser);
			note.setUpdatedDate(new Date());
			dndao.update(note);
		}
	}

	private void createChildDN(DiscrepancyNoteBean parentNote, DiscrepancyNoteDAO dndao, StudyBean currentStudy,
			UserAccountBean currentUser) {
		DiscrepancyNoteBean childNote = new DiscrepancyNoteBean();
		String childNoteDescription = buildChildNoteDescription();
		childNote.setParentDnId(parentNote.getId());
		childNote.setDescription(childNoteDescription);
		childNote.setDiscrepancyNoteTypeId(parentNote.getDiscrepancyNoteTypeId());
		childNote.setResolutionStatusId(parentNote.getResolutionStatusId());
		childNote.setAssignedUserId(parentNote.getAssignedUserId());
		childNote.setOwner(currentUser);
		childNote.setStudyId(currentStudy.getId());
		childNote.setEntityId(parentNote.getEntityId());
		childNote.setEntityType(parentNote.getEntityType());
		childNote.setColumn(parentNote.getColumn());
		childNote = (DiscrepancyNoteBean) dndao.create(childNote);
		if (childNote.getId() > 0) {
			dndao.createMapping(childNote);
		}
	}

	private String buildChildNoteDescription() {
		ResourceBundle resword = ResourceBundleProvider.getWordsBundle(CoreResources.getSystemLocale());
		StringBuilder descriptionBuilder = new StringBuilder("");
		descriptionBuilder.append(resword.getString("dcf_generated_dcf")).append(" (");
		for (DcfRenderType renderType : renderTypes) {
			descriptionBuilder.append(resword.getString(renderType.getResourceBundleKeyForAction()));
			descriptionBuilder.append(", ");
		}
		String noteDescription = descriptionBuilder.toString().trim();
		return noteDescription.substring(0, noteDescription.length() - 1).concat(")");
	}
}
