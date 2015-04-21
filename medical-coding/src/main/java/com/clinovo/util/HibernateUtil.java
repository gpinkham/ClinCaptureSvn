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
package com.clinovo.util;

import com.clinovo.model.AtcClassification;
import com.clinovo.model.CountryCode;
import com.clinovo.model.Ingredient;
import com.clinovo.model.LowLevelTerm;
import com.clinovo.model.MedicalHierarchy;
import com.clinovo.model.MedicalProduct;
import com.clinovo.model.Substance;
import com.clinovo.model.Therapgroup;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.AbstractSessionFactoryBean;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

import javax.sql.DataSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * WHOD/MEDDRA hibernate session util.
 */
@SuppressWarnings("rawtypes")
public final class HibernateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUtil.class);

    private static SessionFactory sessionFactory;

    private static final String WHOD_DEFAULT = "WHOD-0315";
    private static final String MEDDRA_DEFAULT = "MEDDRA-171";

    /**
     * Returns session for term search.
     * @param ontologyName the name of ontology.
     * @param bioontologyUrl the bioontology instance URL.
     * @param bioontologyUser the user for authterisation to the bioontology instance.
     * @return the Session bean.
     * @throws Exception for all exceptions.
     */
    public static Session getSession(String ontologyName, String bioontologyUrl, String bioontologyUser) throws Exception {
        sessionFactory = sessionFactory(ontologyName, bioontologyUrl, bioontologyUser).getObject();
        return sessionFactory.openSession();
    }

    private static AbstractSessionFactoryBean sessionFactory(String ontologyName, String bioontologyUrl, String bioontologyUser) throws Exception {
        AnnotationSessionFactoryBean lsfb = new AnnotationSessionFactoryBean();
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        lsfb.setDataSource(dataSource(ontologyName, bioontologyUrl, bioontologyUser));
        lsfb.setHibernateProperties(properties);
        Class[] annotatedClasses = {MedicalHierarchy.class, AtcClassification.class, CountryCode.class,
                Ingredient.class, MedicalProduct.class, Substance.class, Therapgroup.class, LowLevelTerm.class};
        lsfb.setAnnotatedClasses(annotatedClasses);
        lsfb.setNamingStrategy(new ImprovedNamingStrategy());
        lsfb.afterPropertiesSet();
        LOGGER.info("Session initialized");
        return lsfb;
    }

    private static String getHostName(String bioontologyUrl) throws MalformedURLException {
        URL myURL = new URL(bioontologyUrl);
        return myURL.getHost();
    }

    private static DataSource dataSource(String ontologyName, String bioontologyUrl, String bioontologyUser) throws MalformedURLException {
        ontologyName = ontologyName.equals("MEDDRA") ? MEDDRA_DEFAULT : ontologyName.equals("WHOD") ? WHOD_DEFAULT : ontologyName;
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://" + getHostName(bioontologyUrl) + ":5432/" + ontologyName);
        dataSource.setUsername(bioontologyUser);
        dataSource.setPassword("");
        return dataSource;    }
}