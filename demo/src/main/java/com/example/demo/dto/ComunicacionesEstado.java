package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class ComunicacionesEstado {

    @JsonProperty("UUID")
    String uuid;


    @JsonProperty("UNIQUE_ID")
    String uniqueId;

    @JsonProperty("DATO")
    String data;

    @JsonProperty("ESTADO")
    String estado;

    @JsonProperty("CREATE_DATE")
    String createDate;

    public ComunicacionesEstado(String uuid, String uniqueId, String data, String estado, String createDate) {
        this.uuid = uuid;
        this.uniqueId = uniqueId;
        this.data = data;
        this.estado = estado;
        this.createDate = createDate;
    }

    public ComunicacionesEstado() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUnique_id(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
