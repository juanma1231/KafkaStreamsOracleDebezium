package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class ComunicacionesJoinEstado {
    @JsonProperty("data_comunicaciones")
    String dataComunicaciones;

    @JsonProperty("data_estado")
    String dataEstado;

    @JsonProperty("evento")
    Integer evento;

    @JsonProperty("create_date_comunicaciones")
    Date createDateComunicaciones;

    @JsonProperty("create_date_estado")
    Date createDateEstado;

    @JsonProperty("estado")
    String estado;

    @JsonProperty("UUID")
    String uuid;

    @JsonProperty("unique_id")
    String uniqueId;

    public ComunicacionesJoinEstado(String dataComunicaciones, String dataEstado, Integer evento, Date createDateComunicaciones, Date createDateEstado, String estado, String uuid, String uniqueId) {
        this.dataComunicaciones = dataComunicaciones;
        this.dataEstado = dataEstado;
        this.evento = evento;
        this.createDateComunicaciones = createDateComunicaciones;
        this.createDateEstado = createDateEstado;
        this.estado = estado;
        this.uuid = uuid;
        this.uniqueId = uniqueId;
    }

    public ComunicacionesJoinEstado() {
    }

    public String getDataComunicaciones() {
        return dataComunicaciones;
    }

    public void setDataComunicaciones(String dataComunicaciones) {
        this.dataComunicaciones = dataComunicaciones;
    }

    public String getDataEstado() {
        return dataEstado;
    }

    public void setDataEstado(String dataEstado) {
        this.dataEstado = dataEstado;
    }

    public Integer getEvento() {
        return evento;
    }

    public void setEvento(Integer evento) {
        this.evento = evento;
    }

    public Date getCreateDateComunicaciones() {
        return createDateComunicaciones;
    }

    public void setCreateDateComunicaciones(Date createDateComunicaciones) {
        this.createDateComunicaciones = createDateComunicaciones;
    }

    public Date getCreateDateEstado() {
        return createDateEstado;
    }

    public void setCreateDateEstado(Date createDateEstado) {
        this.createDateEstado = createDateEstado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
