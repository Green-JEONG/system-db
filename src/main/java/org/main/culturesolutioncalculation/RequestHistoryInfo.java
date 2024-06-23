package org.main.culturesolutioncalculation;

import java.sql.Timestamp;

public class RequestHistoryInfo {

    private int id;

    private Timestamp requestDate;
    private UserInfo userInfo;
    private int cultureMediumId; //배양액 재배작물 아이디

    private String mediumTypeName; //배양액 종류 이름
    private String sampleType;
    private String varietyName; //품종명

    private String cropType;
    private String substrateType; //배지 종류( 코이어, 암면, 피트모스, 기타)
    private String deliveryMethod; //교부 방법

    private String selectedCropName;
    private double ph; //산도
    private double ec; //농도
    private double hco3; //중탄산

    public double getPh() {
        return ph;
    }

    public double getEc() {
        return ec;
    }

    public double getHco3() {
        return hco3;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPh(double ph) {
        this.ph = ph;
    }

    public void setEc(double ec) {
        this.ec = ec;
    }

    public void setHco3(double hco3) {
        this.hco3 = hco3;
    }

    public String getMediumTypeName() {
        return mediumTypeName;
    }

    public void setMediumTypeName(String mediumTypeName) {
        this.mediumTypeName = mediumTypeName;
    }

    public RequestHistoryInfo() {

    }

    public RequestHistoryInfo(int id, Timestamp requestDate, UserInfo userInfo, int cultureMediumId, String sampleType, String varietyName, String cropType, String substrateType, String deliveryMethod, String selectedCropName, double ph, double ec, double hco3) {
        this.id = id;
        this.requestDate = requestDate;
        this.userInfo = userInfo;
        this.cultureMediumId = cultureMediumId;
        this.sampleType = sampleType;
        this.varietyName = varietyName;
        this.cropType = cropType;
        this.substrateType = substrateType;
        this.deliveryMethod = deliveryMethod;
        this.selectedCropName = selectedCropName;
        this.ph = ph;
        this.ec = ec;
        this.hco3 = hco3;
    }

    // 유저 정보를 통해 조회할 때는 ph, ec, hco3
    public RequestHistoryInfo(int id, Timestamp requestDate, int cultureMediumId, String sampleType, String varietyName, String cropType, String substrateType, String deliveryMethod, String selectedCropName) {
        this.id = id;
        this.requestDate = requestDate;
        this.cultureMediumId = cultureMediumId;
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

    public int getCultureMediumId() {
        return cultureMediumId;
    }

    public void setCultureMediumId(int cultureMediumId) {
        this.cultureMediumId = cultureMediumId;
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
