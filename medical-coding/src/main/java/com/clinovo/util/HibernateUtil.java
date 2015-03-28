package com.clinovo.util;

import com.clinovo.coding.SearchException;
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

    private static SessionFactory sessionFactory;
    private static final String WHOD = "WHOD";
    private static final String MEDDRA = "MEDDRA";

    private static SessionFactory buildSessionFactory(String ontologyName) {
        try {
            if (ontologyName.contains(WHOD)) {
                return new AnnotationConfiguration()
                        .configure("whod_hibernate.xml").setNamingStrategy(new ImprovedNamingStrategy())
                        .buildSessionFactory();
            } else if (ontologyName.contains(MEDDRA)) {
                return new AnnotationConfiguration()
                        .configure("meddra_hibernate.xml").setNamingStrategy(new ImprovedNamingStrategy())
                        .buildSessionFactory();
            } else {
                throw new SearchException("invalid dictionary");
            }

        } catch (Throwable ex) {
            LOGGER.error("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Returns WHOD connection session object.
     *
     * @param ontologyName name of ontology.
     * @return the Session for connection to the database.
     */
    public static Session getSession(String ontologyName) {
        sessionFactory = buildSessionFactory(ontologyName);
        try {
            return sessionFactory.openSession();
        } finally {
            sessionFactory.close();
        }
    }

}