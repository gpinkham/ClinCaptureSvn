package org.akaza.openclinica.domain;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import org.akaza.openclinica.domain.enumsupport.CodedEnum;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

public enum EventCRFStatus implements CodedEnum {

    INVALID(0, "invalid"), UNCOMPLETED(1, "not_started"), INITIAL_DATA_ENTRY(2, "initial_data_entry"), INITIAL_DATA_ENTRY_COMPLETE(3, "initial_data_entry_complete"), DOUBLE_DATA_ENTRY(4, "double_data_entry"), DOUBLE_DATA_ENTRY_COMPLETE(5, "data_entry_complete"), ADMINISTRATIVE_EDITING(
            6, "administrative_editing"), LOCKED(7, "locked");

    private int code;
    private String description;

    EventCRFStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        ResourceBundle resterm = ResourceBundleProvider.getTermsBundle();
        return resterm.getString(getDescription());
    }

    public static Status getByName(String name) {
        return Status.valueOf(Status.class, name);
    }

    public static EventCRFStatus getByCode(Integer code) {
        HashMap<Integer, EventCRFStatus> enumObjects = new HashMap<Integer, EventCRFStatus>();
        for (EventCRFStatus theEnum : EventCRFStatus.values()) {
            enumObjects.put(theEnum.getCode(), theEnum);
        }
        return enumObjects.get(Integer.valueOf(code));
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
