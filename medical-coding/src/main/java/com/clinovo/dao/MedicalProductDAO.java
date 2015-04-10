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
@SuppressWarnings("unchecked")
public class MedicalProductDAO {

    private static final String WHOD = "WHOD";
    private static final String MEDDRA = "MEDDRA";

    /**
     * Returns the list of medical products.
     *
     * @param mpn             the medical product name for search.
     * @param ontologyName    the name of term's ontology.
     * @param bioontologyUrl  the bioontology URL path.
     * @param bioontologyUser the bioontologyUser user for auth.
     * @return the list of medical product information.
     */
    public List<Object> findByMedicalProductName(String mpn, String ontologyName, String bioontologyUrl, String bioontologyUser) throws Exception {
        String sql = "";
        if (ontologyName.contains(WHOD)) {
            sql = "from MedicalProduct p where p.drugName like :mpn or p.drugName = :mpnFull";
        } else if (ontologyName.contains(MEDDRA)) {
            sql = "from MedicalHierarchy mh where mh.ptName like :mpn or mh.ptName = :mpnFull";
        }
        Query q = getCurrentSession(ontologyName, bioontologyUrl, bioontologyUser).createQuery(sql);
        q.setParameter("mpn", "%" + mpn + "%");
        q.setParameter("mpnFull", mpn);

        return q.list();
    }

    /**
     * Returns the list of medical products by keys.
     *
     * @param drugRecordNum   the medical product drug record number.
     * @param seq1            the seq1 code.
     * @param seq2            the seq2 code.
     * @param bioontologyUrl  the bioontology URL path.
     * @param bioontologyUser the bioontologyUser user for auth.
     * @return the list of medical products.
     */
    public List<Object> findByMedicalProductUniqueKeys(String drugRecordNum, String seq1, String seq2, String bioontologyUrl, String bioontologyUser) throws Exception {
        Query q = getCurrentSession("WHOD", bioontologyUrl, bioontologyUser).createQuery(
                "from MedicalProduct p where p.drugRecordNumber = :drn and p.sequenceNumber1 = :seq1 and p.sequenceNumber2 = :seq2");
        q.setParameter("drn", Integer.valueOf(drugRecordNum));
        q.setParameter("seq1", seq1);
        q.setParameter("seq2", seq2);
        return q.list();
    }

    /**
     * Returns medical product using medical product primary key.
     *
     * @param ontologyName    the name of term's ontology.
     * @param termId          the medical product primary key.
     * @param bioontologyUrl  the bioontology URL path.
     * @param bioontologyUser the bioontologyUser user for auth.
     * @return the medical product bean.
     * @throws SearchException for invalid ontology name.
     */
    public Object findByPk(int termId, String ontologyName, String bioontologyUrl, String bioontologyUser) throws Exception {

        if (ontologyName.contains(WHOD)) {
            return getCurrentSession(ontologyName, bioontologyUrl, bioontologyUser).get(MedicalProduct.class, termId);
        } else if (ontologyName.contains(MEDDRA)) {
            return getCurrentSession(ontologyName, bioontologyUrl, bioontologyUser).get(MedicalHierarchy.class, Long.valueOf(termId));
        }
        throw new SearchException("Invalid ontology name");
    }

    private Session getCurrentSession(String ontologyName, String bioontologyUrl, String bioontologyUser) throws Exception {
        return HibernateUtil.getSession(ontologyName, bioontologyUrl, bioontologyUser);
    }
}
