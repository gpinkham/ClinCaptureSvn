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

package com.clinovo.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.job.OpenClinicaSchedulerFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinovo.command.SystemCommand;
import com.clinovo.command.SystemGroupHolder;
import com.clinovo.dao.SystemDAO;
import com.clinovo.model.PropertyType;
import com.clinovo.model.System;
import com.clinovo.model.SystemGroup;
import com.clinovo.service.SystemService;
import com.clinovo.util.FileUtil;

@Service
@Transactional
public class SystemServiceImpl implements SystemService {

	@Autowired
	private SystemDAO systemDAO;

	@Autowired
	private DataSource datasource;

	@Autowired
	private CoreResources coreResources;

	@Autowired
	private JavaMailSenderImpl mailSender;

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

		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(datasource);
		List<SystemGroupHolder> systemPropertyGroups = new ArrayList<SystemGroupHolder>();
		List<SystemGroup> groupList = systemDAO.getAllMainGroups();
		boolean addGroup = false;

		for (SystemGroup group : groupList) {
			addGroup = false;
			SystemGroupHolder groupHolder = new SystemGroupHolder(group);
			addGroup = processGroupProperties(role, groupHolder, addGroup, spvdao);

			for (SystemGroup subGroup : systemDAO.getAllChildGroups(group.getId())) {
				SystemGroupHolder subGroupHolder = new SystemGroupHolder(subGroup);
				addGroup = processGroupProperties(role, subGroupHolder, addGroup, spvdao);
				groupHolder.getSubGroups().add(subGroupHolder);
			}
			if (addGroup) {
				systemPropertyGroups.add(groupHolder);
			}
		}
		return systemPropertyGroups;
	}

	private boolean processGroupProperties(Role role, SystemGroupHolder groupHolder, boolean addGroup,
			StudyParameterValueDAO spvdao) {

		groupHolder.setSystemProperties(systemDAO.getAllProperties(groupHolder.getGroup().getId(), role));
		processDynamicProperties(groupHolder.getSystemProperties());
		addGroup = !addGroup && groupHolder.getSystemProperties().size() > 0 || addGroup;

		return addGroup;
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

	public void updateSystemProperties(SystemCommand systemCommand,
			OpenClinicaSchedulerFactoryBean schedulerFactoryBean) throws Exception {
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
