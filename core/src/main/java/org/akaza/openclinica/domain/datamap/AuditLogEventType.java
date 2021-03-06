package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * AuditLogEventType.
 */
@Entity
@Table(name = "audit_log_event_type")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence_name", value = "audit_log_event_type_audit_log_event_type_id_seq")})
public class AuditLogEventType extends DataMapDomainObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private int auditLogEventTypeId;
    private String name;
    private List<AuditLogEvent> auditLogEvent;

    public String getI18nName(Locale locale) {
        if (!"".equals(this.name)) {
            ResourceBundle resWords = ResourceBundleProvider.getWordsBundle(locale);
            String des = resWords.getString(this.name);
            if (des != null) {
                return des.trim();
            } else {
                return "";
            }
        } else {
            return this.name;
        }
    }


    @Id
    @Column(name = "audit_log_event_type_id", unique = true, nullable = false)
    public int getAuditLogEventTypeId() {
        return this.auditLogEventTypeId;
    }

    public void setAuditLogEventTypeId(int auditLogEventTypeId) {
        this.auditLogEventTypeId = auditLogEventTypeId;
    }

    @Column(name = "name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "auditLogEventType")
    public List<AuditLogEvent> getAuditLogEvent() {
        return auditLogEvent;
    }

    public void setAuditLogEvent(List<AuditLogEvent> auditLogEvent) {
        this.auditLogEvent = auditLogEvent;
    }

}
