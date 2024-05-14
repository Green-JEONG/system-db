package org.main.culturesolutioncalculation;

import java.time.LocalDate;


public class UserInfo {

    private int id;
    // 고객 이름
    private String customerName;
    // 주소
    private String address;
    // 연락처
    private String contact;
    // 의뢰 일자
    private LocalDate processingDate;
    // 재배규모
    private String scale;

    private String email;
    // 배양액 종류
    private String selectedCulture;

    // 재배 작물
    private String selectedCrop;

    //선택한 culture_medum의 id
    private int cultureMediumId;

    public void setCultureMediumId(int id){
        this.cultureMediumId = id;
    }
    public int getCultureMediumId(){
        return cultureMediumId;
    }

    public UserInfo() {
    }

    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public LocalDate getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(LocalDate processingDate) {
        this.processingDate = processingDate;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getSelectedCrop() {
        return selectedCrop;
    }

    public void setSelectedCrop(String crop) {
        this.selectedCrop = crop;
    }

    public String getSelectedCulture() {
        return selectedCulture;
    }

    public void setSelectedCulture(String culture) {
        this.selectedCulture = culture;
    }
}
