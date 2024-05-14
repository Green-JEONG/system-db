package org.main.culturesolutioncalculation.service.requestHistory;

import java.sql.Timestamp;

public class RequestHistory{
    int id;
    int userId;
    Timestamp requestDate;
    int cultureMediumId;
    int cultivation_scale; //재배 규모

    //===선택 사항
    String sampleType; //시료 종류
    String varietyName; //품종
    String substrateType; //배지 종류
    String reportIssuanceMethod; //교부 방법

    public RequestHistory(){

    }

    public RequestHistory(int id, int userId, Timestamp requestDate, int cultureMediumId, int cultivation_scale, String sampleType, String varietyName, String substrateType, String reportIssuanceMethod) {
        this.id = id;
        this.userId = userId;
        this.requestDate = requestDate;
        this.cultureMediumId = cultureMediumId;
        this.cultivation_scale = cultivation_scale;
        this.sampleType = sampleType;
        this.varietyName = varietyName;
        this.substrateType = substrateType;
        this.reportIssuanceMethod = reportIssuanceMethod;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public int getCultureMediumId() {
        return cultureMediumId;
    }

    public int getCultivation_scale() {
        return cultivation_scale;
    }

    public String getSampleType() {
        return sampleType;
    }

    public String getVarietyName() {
        return varietyName;
    }

    public String getSubstrateType() {
        return substrateType;
    }

    public String getReportIssuanceMethod() {
        return reportIssuanceMethod;
    }
}