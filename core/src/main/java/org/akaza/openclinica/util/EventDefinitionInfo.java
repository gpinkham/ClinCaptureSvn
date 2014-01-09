package org.akaza.openclinica.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class EventDefinitionInfo implements Serializable {
    public int id;
    public boolean required;
    public int defaultVersionId;
}
