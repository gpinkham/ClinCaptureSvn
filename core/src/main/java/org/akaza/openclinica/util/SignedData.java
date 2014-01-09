package org.akaza.openclinica.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SignedData implements Serializable {
    public EventCrfInfo eventCrfInfo;
    public EventDefinitionInfo eventDefinitionInfo;
}
