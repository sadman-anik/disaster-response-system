package com.sadman.drs.protocol;

import java.io.Serializable;

public class SearchReportsRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String keyword;

    public SearchReportsRequest(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}
