package com.clinovo.jbehave;

import net.thucydides.jbehave.ThucydidesJUnitStories;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.jbehave.core.annotations.BeforeStory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class AcceptanceTestSuite extends ThucydidesJUnitStories {

	public static final String STORY_ROOT_DIR = "com.clinovo.stories/";
	public static final String RESOURCE_FOLDER = "./src/test/resources/";
	public static final String FEATURES_FOLDER = "features/";
	public static final String SMOKE_FOLDER = "basic_wf/";
	public static final String FEATURES = "features";
	public static final String FILE_SEPARATOR = "/";
	public static final List<String> EXCLUDED_FILES = Arrays.asList("Preconditions.story");

	private DataSource dataSource;

	private Logger logger = LoggerFactory.getLogger(AcceptanceTestSuite.class);
	private Properties properties = new Properties();

	@BeforeStory
	public void prepare() {
		try {
			setupDatabaseConnection();
			ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
			rdp.addScript(new ClassPathResource("sql/reset_root_password.sql"));
			rdp.populate(dataSource.getConnection());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (dataSource.getConnection() != null) {
					dataSource.getConnection().close();
				}
			} catch (Exception sqlEx) {
				sqlEx.printStackTrace();
			}
		}
	}

	@Override
	public List<String> storyPaths() {
		String testSuite = System.getProperty("testSuite");
		if (testSuite == null || testSuite.isEmpty()) {
			return getStoriesList(RESOURCE_FOLDER + STORY_ROOT_DIR);
		} else if (testSuite.equals(FEATURES)) {
			return getStoriesList(RESOURCE_FOLDER + STORY_ROOT_DIR + FEATURES_FOLDER);
		} else {
			return getStoriesList(RESOURCE_FOLDER + STORY_ROOT_DIR + SMOKE_FOLDER);
		}
	}

	private ArrayList<String> getStoriesList(String rootFolder) {
		ArrayList<String> storiesToRun = new ArrayList<>();
		File folder = new File(rootFolder);
		File[] filesList = folder.listFiles();

		if (filesList == null) {
			return storiesToRun;
		}
		for (File file : filesList) {
			if (file.isFile() && fileNotExcluded(file)) {
				storiesToRun.add(rootFolder.replace(RESOURCE_FOLDER, "") + file.getName());
			} else if (file.isDirectory() && fileNotExcluded(file)) {
				storiesToRun.addAll(getStoriesList(rootFolder + file.getName() + FILE_SEPARATOR));
			}
		}
		return storiesToRun;
	}

	private boolean fileNotExcluded(File file) {
		return !EXCLUDED_FILES.contains(file.getName());
	}

	private void setupDatabaseConnection() {
		loadProperties();

		BasicDataSource ds = new BasicDataSource();
		ds.setAccessToUnderlyingConnectionAllowed(true);
		ds.setDriverClassName(properties.getProperty("driver"));
		ds.setUsername(properties.getProperty("dbUser"));
		ds.setPassword(properties.getProperty("dbPass"));
		ds.setUrl(properties.getProperty("url") + properties.getProperty("db"));
		dataSource = ds;
	}

	private void loadProperties() {
		try {
			properties.load(AcceptanceTestSuite.class.getResourceAsStream(getPropertiesFilePath()));
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}

	private String getPropertiesFilePath() {
		return "/datainfo.properties";
	}
}