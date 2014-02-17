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
