package org.main.culturesolutioncalculation;

import java.time.LocalDate;


public class UserInfo {

    private int id;
    // 고객 이름
    private String name;
    // 주소
    private String address;
    // 연락처
    private String contact;

    private String email;
    // 배양액 종류


    public UserInfo() {
    }

    public UserInfo(int id, String name, String address, String contact, String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(int id) {
        this.id = id;
    }
}
