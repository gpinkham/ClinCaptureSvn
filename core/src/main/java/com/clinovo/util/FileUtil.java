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

package com.clinovo.util;

import com.clinovo.command.SystemCommand;
import org.akaza.openclinica.dao.core.CoreResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public final class FileUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	private FileUtil() {
	}

	public static void saveLogo(SystemCommand command) throws Exception {
		if (command.getLogoFile() != null && command.getLogoFile().getSize() > 0) {
			DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
			String fileName = "logo_" + new Date().getTime() + ".gif";
			String newLogoPath = resourceLoader.getResource(".." + File.separator + ".." + File.separator + "images")
					.getFile().getAbsolutePath()
					+ File.separator + fileName;
			FileOutputStream fos = new FileOutputStream(newLogoPath);
			fos.write(command.getLogoFile().getBytes());
			fos.close();
			command.setNewLogoPath(newLogoPath);
			command.setNewLogoUrl("/images/" + fileName);
		}
	}

	public static void changeLogo(SystemCommand command) {
		try {
			if (command.getNewLogoPath() != null && !command.getNewLogoPath().isEmpty()) {
				DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
				File newLogoPathFile = new File(command.getNewLogoPath());
				File filePathUploads = new File(new File(CoreResources.getField("filePath")).getAbsolutePath()
						+ File.separator + "uploads");
				if (!filePathUploads.exists()) {
					filePathUploads.mkdirs();
				}
				FileCopyUtils.copy(newLogoPathFile, new File(filePathUploads.getAbsolutePath() + File.separator
						+ newLogoPathFile.getName()));
				File prevLogoFile = new File(filePathUploads.getAbsolutePath() + File.separator
						+ new File(CoreResources.getField("logo")).getName());
				if (prevLogoFile.exists()) {
					prevLogoFile.delete();
				}
				prevLogoFile = new File(resourceLoader
						.getResource(".." + File.separator + ".." + File.separator + "images").getFile()
						.getAbsolutePath()
						+ File.separator + new File(CoreResources.getField("logo")).getName());
				if (prevLogoFile.exists()) {
					prevLogoFile.delete();
				}
			}
		} catch (Exception ex) {
			command.setNewLogoUrl("");
			LOGGER.error("Error has occurred.", ex);
		}
	}

}
