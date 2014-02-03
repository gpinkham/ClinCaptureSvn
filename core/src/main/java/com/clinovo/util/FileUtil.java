package com.clinovo.util;

import com.clinovo.command.SystemCommand;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import org.akaza.openclinica.dao.core.CoreResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.FileCopyUtils;

public final class FileUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	private FileUtil() {
	}

	public static void saveLogo(SystemCommand command) throws Exception {
		if (command.getLogoFile() != null && command.getLogoFile().getSize() > 0) {
			DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
			File file = new File(resourceLoader.getResource(".." + File.separator + ".." + File.separator + "images")
					.getFile().getAbsolutePath()
					+ File.separator + "uploads");
			if (!file.exists()) {
				file.mkdirs();
			}
			String fileName = "logo_" + new Date().getTime() + ".gif";
			String newLogoPath = resourceLoader
					.getResource(".." + File.separator + ".." + File.separator + "images" + File.separator + "uploads")
					.getFile().getAbsolutePath()
					+ File.separator + fileName;
			FileOutputStream fos = new FileOutputStream(newLogoPath);
			fos.write(command.getLogoFile().getBytes());
			fos.close();
			command.setNewLogoPath(newLogoPath);
			command.setNewLogoUrl("/images/uploads/" + fileName);
		}
	}

	public static void changeLogo(SystemCommand command) {
		try {
			if (command.getNewLogoPath() != null && !command.getNewLogoPath().isEmpty()) {
				String logoName = "logo_" + new Date().getTime() + ".gif";
				DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
				FileCopyUtils.copy(new File(command.getNewLogoPath()),
						new File(resourceLoader.getResource(".." + File.separator + ".." + File.separator + "images")
								.getFile().getAbsolutePath()
								+ File.separator + logoName));
				new File(command.getNewLogoPath()).delete();
				new File(resourceLoader.getResource(".." + File.separator + "..").getFile().getAbsolutePath()
						+ CoreResources.getField("logo")).delete();
				command.setNewLogoUrl("/images/" + logoName);
			}
		} catch (Exception ex) {
			command.setNewLogoUrl("");
			LOGGER.error("Error has occurred.", ex);
		}
	}

}
