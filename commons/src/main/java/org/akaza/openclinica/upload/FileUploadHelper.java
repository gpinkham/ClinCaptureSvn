/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2010 Akaza Research
 */
package org.akaza.openclinica.upload;

import org.akaza.openclinica.bean.rule.FileProperties;
import org.akaza.openclinica.bean.rule.FileRenamePolicy;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * File Upload Helper.
 */
public class FileUploadHelper {

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private FileProperties fileProperties;
	private FileRenamePolicy fileRenamePolicy;

	/**
	 * Default constructor.
	 */
	public FileUploadHelper() {
		fileProperties = new FileProperties();
	}

	/**
	 * Constructor with FileProperties.
	 * @param fileProperties FileProperties
	 */
	public FileUploadHelper(FileProperties fileProperties) {
		super();
		this.fileProperties = fileProperties;
	}

	/**
	 * Get files from request.
	 * @param request HttpServletRequest
	 * @return List<File>
	 */
	public List<File> returnFiles(HttpServletRequest request) {
		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		return isMultipart ? getFiles(request, null) : new ArrayList<File>();
	}

	/**
	 * Get files with custom fileRenamePolicy.
	 * @param request HttpServletRequest
	 * @param fileRenamePolicy FileRenamePolicy
	 * @return List<File>
	 */
	public List<File> returnFiles(HttpServletRequest request, FileRenamePolicy fileRenamePolicy) {
		// Check that we have a file upload request
		this.fileRenamePolicy = fileRenamePolicy;
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		return isMultipart ? getFiles(request, null) : new ArrayList<File>();
	}

	/**
	 * Get files with custom fileRenamePolicy and dir for temp files.
	 * @param request HttpServletRequest
	 * @param dirToSaveUploadedFileIn String
	 * @param fileRenamePolicy FileRenamePolicy
	 * @return List<File>
	 */
	public List<File> returnFiles(HttpServletRequest request, String dirToSaveUploadedFileIn,
			FileRenamePolicy fileRenamePolicy) {
		// Check that we have a file upload request
		this.fileRenamePolicy = fileRenamePolicy;
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		return isMultipart ? getFiles(request, createDirectoryIfDoesNotExist(dirToSaveUploadedFileIn))
				: new ArrayList<File>();
	}

	/**
	 * Get files with custom dir for temp files.
	 * @param request HttpServletRequest
	 * @param dirToSaveUploadedFileIn String
	 * @return List<File>
	 */
	public List<File> returnFiles(HttpServletRequest request, String dirToSaveUploadedFileIn) {
		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		return isMultipart ? getFiles(request, createDirectoryIfDoesNotExist(dirToSaveUploadedFileIn))
				: new ArrayList<File>();
	}

	private List<File> getFiles(HttpServletRequest request, String dirToSaveUploadedFileIn) {
		List<File> files = new ArrayList<File>();
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			// Parse the request
			List<FileItem> items = upload.parseRequest(request);
			List<FileItem> filesList = new ArrayList<FileItem>();
			// First we should process all form fields and set all variables to request.
			for (FileItem item : items) {
				if (item.isFormField()) {
					request.setAttribute(item.getFieldName(), item.getString());
				} else {
					request.setAttribute("displayedFileName", getFileName(item.getName()));
					filesList.add(item);
				}
			}
			// Only then all files should be processed
			for (FileItem item : filesList) {
				getFileProperties().isValidExtension(item.getName());
				files.add(processUploadedFile(item, dirToSaveUploadedFileIn));
			}
			return files;
		} catch (FileSizeLimitExceededException slee) {
			throw new OpenClinicaSystemException("exceeds_permitted_file_size",
					new Object[] { String.valueOf(getFileProperties().getFileSizeMaxInMb()) }, slee.getMessage());
		} catch (FileUploadException fue) {
			throw new OpenClinicaSystemException("file_upload_error_occured", new Object[] { fue.getMessage() },
					fue.getMessage());
		} catch (Exception e) {
			throw new OpenClinicaSystemException("file_upload_error_occured", new Object[] { e.getMessage() },
				e.getMessage());
		}
	}

	private File processUploadedFile(FileItem item, String dirToSaveUploadedFileIn) throws Exception {
		dirToSaveUploadedFileIn = dirToSaveUploadedFileIn == null ? System.getProperty("java.io.tmpdir")
				: dirToSaveUploadedFileIn;
		String fileName = getFileName(item.getName());

		File uploadedFile = new File(dirToSaveUploadedFileIn + File.separator + fileName);
		if (fileRenamePolicy != null) {
			uploadedFile = fileRenamePolicy.rename(uploadedFile);
		}
		item.write(uploadedFile);

		if (uploadedFile.length() > getFileProperties().getFileSizeMax()) {
			if (uploadedFile.delete()) {
				logger.info("Temp file deleted (file size is too big): " + fileName);
			} else {
				logger.error("Unable to delete temp file: " + fileName);
			}
			throw new FileSizeLimitExceededException("", 0, 0);
		}
		return uploadedFile;
	}

	private String createDirectoryIfDoesNotExist(String theDir) {
		if (!new File(theDir).isDirectory()) {
			new File(theDir).mkdirs();
		}
		return new File(theDir).toString();
	}

	public FileProperties getFileProperties() {
		return fileProperties;
	}

	public void setFileProperties(FileProperties fileProperties) {
		this.fileProperties = fileProperties;
	}

	private String getFileName(String fileName) {
		int startIndex = fileName.lastIndexOf('\\');
		if (startIndex != -1) {
			fileName = fileName.substring(startIndex + 1, fileName.length());
		}
		return fileName;
	}
}
