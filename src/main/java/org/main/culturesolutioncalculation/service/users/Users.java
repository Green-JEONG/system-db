package org.main.culturesolutioncalculation.service.users;

import org.main.culturesolutioncalculation.service.requestHistory.RequestHistory;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Users {


    private int id;
    private String name;
    private String address;
    private String contact;



    public Users(){

    }

    public Users(int id, String name, String address, String contact) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contact = contact;
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
