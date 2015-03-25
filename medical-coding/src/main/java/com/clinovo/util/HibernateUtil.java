package com.clinovo.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WHOD hibernate session util.
 */
public final class HibernateUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUtil.class);

	private static SessionFactory sessionFactory = buildSessionFactory();

	private static SessionFactory buildSessionFactory() {
		try {
			return new AnnotationConfiguration()
					.configure("whod_hibernate.xml").setNamingStrategy(new ImprovedNamingStrategy())
					.buildSessionFactory();
		} catch (Throwable ex) {
			LOGGER.error("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Returns WHOD connection session object.
	 *
	 * @return the Session for connection to the database.
	 */
	public static Session getSession() {
		try {
			return sessionFactory.openSession();
		} finally {
			sessionFactory.close();
		}
	}

}