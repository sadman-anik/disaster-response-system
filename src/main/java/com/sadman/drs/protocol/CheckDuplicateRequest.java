package com.sadman.drs.protocol;

import java.io.Serializable;

public class CheckDuplicateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String disasterType;
    private final String location;

    public CheckDuplicateRequest(String disasterType, String location) {
        this.disasterType = disasterType;
        this.location = location;
    }

    public String getDisasterType() {
        return disasterType;
    }

    public String getLocation() {
        return location;
    }
}
