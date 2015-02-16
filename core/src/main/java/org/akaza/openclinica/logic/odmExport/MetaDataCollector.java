/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2015 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */

package org.akaza.openclinica.logic.odmExport;

import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.odmbeans.MetaDataVersionProtocolBean;
import org.akaza.openclinica.bean.odmbeans.OdmStudyBean;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.job.JobTerminationMonitor;

import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.sql.DataSource;

public class MetaDataCollector extends OdmDataCollector {
    private LinkedHashMap<String, OdmStudyBean> odmStudyMap;
    private static int textLength = 4000;
    private RuleSetRuleDao ruleSetRuleDao;

    public MetaDataCollector(DataSource ds, StudyBean study, RuleSetRuleDao ruleSetRuleDao) {
        super(ds, study);
        this.ruleSetRuleDao = ruleSetRuleDao;
        odmStudyMap = new LinkedHashMap<String, OdmStudyBean>();
    }

    public MetaDataCollector(DataSource ds, DatasetBean dataset, StudyBean currentStudy, RuleSetRuleDao ruleSetRuleDao) {
        super(ds, dataset, currentStudy);
        this.ruleSetRuleDao = ruleSetRuleDao;
        odmStudyMap = new LinkedHashMap<String, OdmStudyBean>();
    }

    @Override
    public void collectFileData() {
        this.collectOdmRoot();
        this.collectMetadataUnitMap();
    }

    public void collectFileData(String formVersionOID) {
        this.collectOdmRoot();
        this.collectMetadataUnitMap(formVersionOID);

    }

    public void collectMetadataUnitMap() {
        Iterator<OdmStudyBase> it = this.getStudyBaseMap().values().iterator();
        MetaDataVersionProtocolBean protocol = new MetaDataVersionProtocolBean();
        while (it.hasNext()) {
            OdmStudyBase u = it.next();
            StudyBean study = u.getStudy();
            MetadataUnit meta = new MetadataUnit(this.ds, this.dataset, this.getOdmbean(), study, this.getCategory(),
                    getRuleSetRuleDao());
            meta.collectOdmStudy();
            if (this.getCategory() == 1) {
                if (study.isSite(study.getParentStudyId())) {
                    meta.getOdmStudy().setParentStudyOID(meta.getParentOdmStudyOid());
                    MetaDataVersionProtocolBean p = meta.getOdmStudy().getMetaDataVersion().getProtocol();
                    if (p != null && p.getStudyEventRefs().size() > 0) {
                    } else {
                        logger.error("site " + study.getName() + " will be assigned protocol with StudyEventRefs size="
                                + protocol.getStudyEventRefs().size());
                        meta.getOdmStudy().getMetaDataVersion().setProtocol(protocol);
                    }
                } else {
                    protocol = meta.getOdmStudy().getMetaDataVersion().getProtocol();

                }
            }
            odmStudyMap.put(u.getStudy().getOid(), meta.getOdmStudy());
        }
    }

    public void collectMetadataUnitMap(String formVersionOID) {
        Iterator<OdmStudyBase> it = this.getStudyBaseMap().values().iterator();
        MetaDataVersionProtocolBean protocol = new MetaDataVersionProtocolBean();
        while (it.hasNext()) {
            JobTerminationMonitor.check();
            OdmStudyBase u = it.next();
            StudyBean study = u.getStudy();
            MetadataUnit meta = new MetadataUnit(this.ds);
            meta.setStudyBase(u);
            meta.setOdmStudy(new OdmStudyBean());
            meta.setParentStudy(new StudyBean());

            meta.collectOdmStudy(formVersionOID);
            if (this.getCategory() == 1) {
                if (study.isSite(study.getParentStudyId())) {
                    meta.getOdmStudy().setParentStudyOID(meta.getParentOdmStudyOid());
                    MetaDataVersionProtocolBean p = meta.getOdmStudy().getMetaDataVersion().getProtocol();
                    if (p != null && p.getStudyEventRefs().size() > 0) {
                    } else {
                        logger.error("site " + study.getName() + " will be assigned protocol with StudyEventRefs size=" + protocol.getStudyEventRefs().size());
                        meta.getOdmStudy().getMetaDataVersion().setProtocol(protocol);
                    }
                } else {
                    protocol = meta.getOdmStudy().getMetaDataVersion().getProtocol();


                }
            }
            odmStudyMap.put(u.getStudy().getOid(), meta.getOdmStudy());
        }
    }

    public LinkedHashMap<String, OdmStudyBean> getOdmStudyMap() {
        return odmStudyMap;
    }

    public void setOdmStudyMap(LinkedHashMap<String, OdmStudyBean> odmStudyMap) {
        this.odmStudyMap = odmStudyMap;
    }

    public static void setTextLength(int len) {
        textLength = len;
    }

    public static int getTextLength() {
        return textLength;
    }

    public RuleSetRuleDao getRuleSetRuleDao() {
        return ruleSetRuleDao;
    }

    public void setRuleSetRuleDao(RuleSetRuleDao ruleSetRuleDao) {
        this.ruleSetRuleDao = ruleSetRuleDao;
    }
}
