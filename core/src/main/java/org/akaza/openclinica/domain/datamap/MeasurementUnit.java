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
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */

package org.akaza.openclinica.domain.datamap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

/**
 * MeasurementUnit.
 */
@Entity
@Table(name = "measurement_unit", uniqueConstraints = {
        @UniqueConstraint(columnNames = "oc_oid"),
        @UniqueConstraint(columnNames = "name")})
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@org.hibernate.annotations.Parameter(name = "sequence", value = "measurement_unit_id_seq")})
public class MeasurementUnit extends AbstractMutableDomainObject {

    private String ocOid;
    private String name;
    private String description;

    @Column(name = "oc_oid", unique = true, nullable = false, length = 40)
    public String getOcOid() {
        return this.ocOid;
    }

    public void setOcOid(String ocOid) {
        this.ocOid = ocOid;
    }

    @Column(name = "name", unique = true, nullable = false, length = 100)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
