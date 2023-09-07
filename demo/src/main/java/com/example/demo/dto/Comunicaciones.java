package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Comunicaciones {

    @JsonProperty("DATO")
    String data;

    @JsonProperty("EVENTO")
    Integer evento;

    @JsonProperty("UUID")
    String uuid;

    @JsonProperty("CREATE_DATE")
    @JsonFormat(pattern = "YYYY-MM-DD")
    Date createDate;

    public Comunicaciones(String data, Integer evento, String uuid, Date createDate) {
        this.data = data;
        this.evento = evento;
        this.uuid = uuid;
        this.createDate = createDate;
    }

    public Comunicaciones() {
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getEvento() {
        return evento;
    }

    public void setEvento(Integer evento) {
        this.evento = evento;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
