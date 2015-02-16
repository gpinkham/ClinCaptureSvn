package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.enumsupport.CodedEnum;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public enum SubjectEventStatus implements CodedEnum {

    INVALID(0, "invalid"), SCHEDULED(1, "scheduled"), NOT_SCHEDULED(2, "not_scheduled"),
    DATA_ENTRY_STARTED(3, "data_entry_started"), COMPLETED(4, "completed"),
    STOPPED(5, "stopped"), SKIPPED(6, "skipped"), LOCKED(7, "locked"),
    SIGNED(8, "signed"), SOURCE_DATA_VERIFIED(9, "source_data_verified"),
    REMOVED(10, "removed"), UNLOCK(11, "unlock");


    private int code;
    private String description;

    SubjectEventStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static SubjectEventStatus getByCode(Integer code) {
        HashMap<Integer, SubjectEventStatus> enumObjects = new HashMap<Integer, SubjectEventStatus>();
        for (SubjectEventStatus theEnum : SubjectEventStatus.values()) {
            enumObjects.put(theEnum.getCode(), theEnum);
        }
        return enumObjects.get(Integer.valueOf(code));
    }

    public static SubjectEventStatus getByName(String name) {
        return SubjectEventStatus.valueOf(SubjectEventStatus.class, name);
    }

    public String getI18nDescription(Locale locale) {
        if (!"".equals(this.description)) {
            ResourceBundle resterm = ResourceBundleProvider.getTermsBundle(locale);
            String des = resterm.getString(this.description);
            if (des != null) {
                return des.trim();
            } else {
                return "";
            }
        } else {
            return this.description;
        }
    }

    @Override
    public String toString() {
        ResourceBundle resterm = ResourceBundleProvider.getTermsBundle();
        return resterm.getString(getDescription());
    }

    public String getName() {
        return this.name();
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
