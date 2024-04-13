package org.main.culturesolutioncalculation.service.requestHistory;

import java.sql.Timestamp;

public class RequestHistory{
    int id;
    int user_id;
    Timestamp request_date;
    int culture_medium_id;
    int cultivation_scale;

    public RequestHistory(){

    }

    public RequestHistory(int id, int user_id, Timestamp request_date, int culture_medium_id, int cultivation_scale) {
        this.id= id;
        this.user_id = user_id;
        this.request_date = request_date;
        this.culture_medium_id = culture_medium_id;
        this.cultivation_scale = cultivation_scale;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getId() {
        return id;
    }

    public Timestamp getRequest_date() {
        return request_date;
    }

    public int getCulture_medium_id() {
        return culture_medium_id;
    }

    public int getCultivation_scale() {
        return cultivation_scale;
    }
}