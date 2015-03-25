package com.clinovo.dao;

import com.clinovo.model.MedicalProduct;
import com.clinovo.util.HibernateUtil;

import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * Medical product data access object.
 */
public class MedicalProductDAO {

    /**
     * Returns the list of medical products.
     *
     * @param mpn the medical product name for search.
     * @return the list of medical product information.
     */
    @SuppressWarnings("unchecked")
	public List<MedicalProduct> findByMedicalProductName(String mpn) {

            Query q = getCurrentSession().createQuery(
                    "from MedicalProduct p where p.drugName like :mpn or p.drugName = :mpnFull");
            q.setParameter("mpn", "%" + mpn + "%");
            q.setParameter("mpnFull", mpn);
            return q.list();
    }

    /**
     * Returns medical product using medical product primary key.
     *
     * @param medicinalprodId the medical product primary key.
     * @return the medical product bean.
     */
    public MedicalProduct findByPk(int medicinalprodId) {
        return (MedicalProduct) getCurrentSession().get(MedicalProduct.class, medicinalprodId);
    }

    private Session getCurrentSession() {
        return HibernateUtil.getSession();
    }

}
