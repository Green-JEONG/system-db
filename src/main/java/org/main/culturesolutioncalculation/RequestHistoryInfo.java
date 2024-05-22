package org.main.culturesolutioncalculation;

import java.sql.Timestamp;

public class RequestHistoryInfo {

    private int id;

    private Timestamp requestDate;
    private UserInfo userInfo;
    private int mediumTypeId; //배양액 재배작물 아이디
    private String sampleType;
    private String varietyName; //품종명

    private String cropType;
    private String substrateType; //배지 종류( 코이어, 암면, 피트모스, 기타)
    private String deliveryMethod; //교부 방법

    private String selectedCropName;

    public RequestHistoryInfo() {

    }

    public RequestHistoryInfo(int id, Timestamp requestDate, int mediumTypeId, String sampleType, String varietyName, String cropType, String substrateType, String deliveryMethod, String selectedCropName) {
        this.id = id;
        this.requestDate = requestDate;
        this.mediumTypeId = mediumTypeId;
        this.sampleType = sampleType;
        this.varietyName = varietyName;
        this.cropType = cropType;
        this.substrateType = substrateType;
        this.deliveryMethod = deliveryMethod;
        this.selectedCropName = selectedCropName;
    }

    public String getCropType() {
        return cropType;
    }


    public String getSelectedCropName() {
        return selectedCropName;
    }

    public void setSelectedCropName(String selectedCropName) {
        this.selectedCropName = selectedCropName;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getSubstrateType() {
        return substrateType;
    }

    public void setSubstrateType(String substrateType) {
        this.substrateType = substrateType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public void setVarietyName(String varietyName) {
        this.varietyName = varietyName;
    }



    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public int getId() {
        return id;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public int getMediumTypeId() {
        return mediumTypeId;
    }

    public void setMediumTypeId(int mediumTypeId) {
        this.mediumTypeId = mediumTypeId;
    }

    public String getSampleType() {
        return sampleType;
    }

    public String getVarietyName() {
        return varietyName;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }
}
