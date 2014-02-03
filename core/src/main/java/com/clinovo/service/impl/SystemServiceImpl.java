package com.clinovo.service.impl;

import com.clinovo.command.SystemCommand;
import com.clinovo.command.SystemGroupHolder;
import com.clinovo.dao.SystemDAO;
import com.clinovo.model.PropertyType;
import com.clinovo.model.System;
import com.clinovo.model.SystemGroup;
import com.clinovo.service.SystemService;
import com.clinovo.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.job.OpenClinicaSchedulerFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SystemServiceImpl implements SystemService {

	@Autowired
	private SystemDAO systemDAO;

	@Autowired
	private OpenClinicaSchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private JavaMailSenderImpl mailSender;

	@Autowired
	private CoreResources coreResources;

	public List<System> findAll() {
		return systemDAO.findAll();
	}

	public List<SystemGroup> getAllMainGroups() {
		return systemDAO.getAllMainGroups();
	}

	public List<SystemGroup> getAllChildGroups(int parentId) {
		return systemDAO.getAllChildGroups(parentId);
	}

	public List<System> getAllProperties(int groupId, Role role) {
		return systemDAO.getAllProperties(groupId, role);
	}

	public List<SystemGroupHolder> getSystemPropertyGroups(Role role) {
		List<SystemGroupHolder> systemPropertyGroups = new ArrayList<SystemGroupHolder>();
		List<SystemGroup> groupList = systemDAO.getAllMainGroups();
		for (SystemGroup group : groupList) {
			SystemGroupHolder groupHolder = new SystemGroupHolder(group);
			groupHolder.setSystemProperties(systemDAO.getAllProperties(group.getId(), role));
			processDynamicProperties(groupHolder.getSystemProperties());
			boolean addGroup = groupHolder.getSystemProperties().size() > 0;
			for (SystemGroup subGroup : systemDAO.getAllChildGroups(group.getId())) {
				SystemGroupHolder subGroupHolder = new SystemGroupHolder(subGroup);
				subGroupHolder.setSystemProperties(systemDAO.getAllProperties(subGroup.getId(), role));
				processDynamicProperties(subGroupHolder.getSystemProperties());
				addGroup = !addGroup && subGroupHolder.getSystemProperties().size() > 0 || addGroup;
				groupHolder.getSubGroups().add(subGroupHolder);
			}
			if (addGroup) {
				systemPropertyGroups.add(groupHolder);
			}
		}
		return systemPropertyGroups;
	}

	private void processDynamicProperties(List<System> systemProperties) {
		for (System systemProperty : systemProperties) {
			if (systemProperty.getType() == PropertyType.DYNAMIC_INPUT
					|| systemProperty.getType() == PropertyType.DYNAMIC_RADIO
					|| systemProperty.getName().equalsIgnoreCase("logo")) {
				systemProperty.setValue(coreResources.getDataInfo().getProperty(systemProperty.getName()));
			}
		}
	}

	public void updateSystemProperties(SystemCommand systemCommand) throws Exception {
		FileUtil.changeLogo(systemCommand);
		for (SystemGroupHolder systemGroupHolder : systemCommand.getSystemPropertyGroups()) {
			for (SystemGroupHolder systemSubGroupHolder : systemGroupHolder.getSubGroups()) {
				for (com.clinovo.model.System systemProperty : systemSubGroupHolder.getSystemProperties()) {
					updateSystemProperty(systemCommand, systemProperty);
				}
			}
			for (com.clinovo.model.System systemProperty : systemGroupHolder.getSystemProperties()) {
				updateSystemProperty(systemCommand, systemProperty);
			}
		}
		CoreResources.prepareDataInfoProperties();
		Properties properties = new Properties();
		properties.setProperty("org.quartz.jobStore.misfireThreshold",
				CoreResources.getField("org.quartz.jobStore.misfireThreshold"));
		properties.setProperty("org.quartz.jobStore.class", CoreResources.getField("org.quartz.jobStore.class"));
		properties.setProperty("org.quartz.jobStore.driverDelegateClass",
				CoreResources.getField("org.quartz.jobStore.driverDelegateClass"));
		properties.setProperty("org.quartz.jobStore.useProperties",
				CoreResources.getField("org.quartz.jobStore.useProperties"));
		properties.setProperty("org.quartz.jobStore.tablePrefix",
				CoreResources.getField("org.quartz.jobStore.tablePrefix"));
		properties.setProperty("org.quartz.threadPool.class", CoreResources.getField("org.quartz.threadPool.class"));
		properties.setProperty("org.quartz.threadPool.threadCount",
				CoreResources.getField("org.quartz.threadPool.threadCount"));
		properties.setProperty("org.quartz.threadPool.threadPriority",
				CoreResources.getField("org.quartz.threadPool.threadPriority"));
		schedulerFactoryBean.setQuartzProperties(properties);
		schedulerFactoryBean.afterPropertiesSet();
		mailSender.setHost(CoreResources.getField("mail.host"));
		mailSender.setPort(Integer.parseInt(CoreResources.getField("mail.port")));
		mailSender.setUsername(CoreResources.getField("mail.username"));
		mailSender.setPassword(CoreResources.getField("mail.password"));
		mailSender.setProtocol(CoreResources.getField("mail.protocol"));
		mailSender.getJavaMailProperties().setProperty("mail.smtp.auth", CoreResources.getField("mail.smtp.auth"));
		mailSender.getJavaMailProperties().setProperty("mail.smtp.starttls.enable",
				CoreResources.getField("mail.smtp.starttls.enable"));
		mailSender.getJavaMailProperties().setProperty("mail.smtps.auth", CoreResources.getField("mail.smtps.auth"));
		mailSender.getJavaMailProperties().setProperty("mail.smtps.starttls.enable",
				CoreResources.getField("mail.smtps.starttls.enable"));
		mailSender.getJavaMailProperties().setProperty("mail.smtp.connectiontimeout",
				CoreResources.getField("mail.smtp.connectiontimeout"));
	}

	private void updateSystemProperty(SystemCommand systemCommand, com.clinovo.model.System systemProperty) {
		String value = systemProperty.getValue();
		if (value != null && systemProperty.getType() != PropertyType.DYNAMIC_INPUT
				&& systemProperty.getType() != PropertyType.DYNAMIC_RADIO) {
			if (systemProperty.getName().equalsIgnoreCase("logo") && systemCommand.getNewLogoUrl() != null
					&& !systemCommand.getNewLogoUrl().trim().isEmpty()) {
				systemProperty.setValue(systemCommand.getNewLogoUrl());
			}
			systemDAO.saveOrUpdate(systemProperty);
			CoreResources.setField(systemProperty.getName(), systemProperty.getValue());
			if (systemProperty.getName().equalsIgnoreCase("crfFileExtensions")) {
				systemProperty = systemDAO.findByName("crfFileExtensionSettings");
				systemProperty.setValue(!value.trim().isEmpty() ? "valid" : "");
				systemDAO.saveOrUpdate(systemProperty);
				CoreResources.setField(systemProperty.getName(), systemProperty.getValue());
			}
		}
	}
}
