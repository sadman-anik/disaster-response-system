package com.sadman.drs.protocol;

import com.sadman.drs.model.DisasterReport;

import java.io.Serializable;

public class AssessmentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final DisasterReport report;
    private final String damageLevel;
    private final int peopleAffected;
    private final boolean infrastructureDamage;

    public AssessmentRequest(DisasterReport report, String damageLevel,
                             int peopleAffected, boolean infrastructureDamage) {
        this.report = report;
        this.damageLevel = damageLevel;
        this.peopleAffected = peopleAffected;
        this.infrastructureDamage = infrastructureDamage;
    }

    public DisasterReport getReport() {
        return report;
    }

    public String getDamageLevel() {
        return damageLevel;
    }

    public int getPeopleAffected() {
        return peopleAffected;
    }

    public boolean isInfrastructureDamage() {
        return infrastructureDamage;
    }
}
