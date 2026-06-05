package com.sadman.drs.protocol;

import java.io.Serializable;

/**
 * Request for searching audit records.
 */
public class AuditSearchRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String keyword;

    public AuditSearchRequest(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}
