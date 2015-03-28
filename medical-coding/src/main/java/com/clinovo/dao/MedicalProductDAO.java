package com.clinovo.dao;

import com.clinovo.coding.SearchException;
import com.clinovo.model.MedicalHierarchy;
import com.clinovo.model.MedicalProduct;
import com.clinovo.util.HibernateUtil;

import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * Medical product data access object.
 */
public class MedicalProductDAO {

    private static final String WHOD = "WHOD";
    private static final String MEDDRA = "MEDDRA";

    /**
     * Returns the list of medical products.
     *
     * @param mpn          the medical product name for search.
     * @param ontologyName the name of term's ontology.
     * @return the list of medical product information.
     */
    public List<Object> findByMedicalProductName(String mpn, String ontologyName) {
        String sql = "";
        if (ontologyName.contains(WHOD)) {
            sql = "from MedicalProduct p where p.drugName like :mpn or p.drugName = :mpnFull";
        } else if (ontologyName.contains(MEDDRA)) {
            sql = "from MedicalHierarchy mh where mh.ptName like :mpn or mh.ptName = :mpnFull";
        }
        Query q = getCurrentSession(ontologyName).createQuery(sql);
        q.setParameter("mpn", "%" + mpn + "%");
        q.setParameter("mpnFull", mpn);

        return q.list();
    }

    /**
     * Returns the list of medical products by keys.
     *
     * @param drugRecordNum the medical product drug record number.
     * @param seq1          the seq1 code.
     * @param seq2          the seq2 code.
     * @return the list of medical products.
     */
    public List<Object> findByMedicalProductUniqueKeys(String drugRecordNum, String seq1, String seq2) {
        Query q = getCurrentSession("WHOD").createQuery(
                "from MedicalProduct p where p.drugRecordNumber = :drn and p.sequenceNumber1 = :seq1 and p.sequenceNumber2 = :seq2");
        q.setParameter("drn", Integer.valueOf(drugRecordNum));
        q.setParameter("seq1", seq1);
        q.setParameter("seq2", seq2);
        return q.list();
    }

    /**
     * Returns medical product using medical product primary key.
     *
     * @param ontologyName the name of term's ontology.
     * @param termId       the medical product primary key.
     * @return the medical product bean.
     * @throws SearchException for invalid ontology name.
     */
    public Object findByPk(int termId, String ontologyName) throws SearchException {

        if (ontologyName.contains(WHOD)) {
            return getCurrentSession(ontologyName).get(MedicalProduct.class, termId);
        } else if (ontologyName.contains(MEDDRA)) {
            return getCurrentSession(ontologyName).get(MedicalHierarchy.class, Long.valueOf(termId));
        }
        throw new SearchException("Invalid ontology name");
    }

    private Session getCurrentSession(String ontologyName) {
        return HibernateUtil.getSession(ontologyName);
    }

}
