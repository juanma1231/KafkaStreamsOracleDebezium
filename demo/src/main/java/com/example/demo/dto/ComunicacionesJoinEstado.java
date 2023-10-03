package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class ComunicacionesJoinEstado {


    @JsonProperty(value = "data_comunicaciones",required = false)
    String dataComunicaciones;



    @JsonProperty(value = "data_estado",required = false)
    String dataEstado;


    @JsonProperty(value = "evento",required = false)
    Integer evento;


    @JsonProperty(value = "create_date_comunicaciones",required = false)
    String createDateComunicaciones;

    @JsonProperty(value = "create_date_estado",required = false)
    String createDateEstado;


    @JsonProperty(value = "estado",required = false)
    String estado;

    @JsonProperty(value = "uuid",required = false)
    String uuid;

    @JsonProperty(value = "unique_id",required = false)
    String uniqueId;

    public ComunicacionesJoinEstado(String dataComunicaciones, String dataEstado, Integer evento, String createDateComunicaciones, String createDateEstado, String estado, String uuid, String uniqueId) {
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

    public String getCreateDateComunicaciones() {
        return createDateComunicaciones;
    }

    public void setCreateDateComunicaciones(String createDateComunicaciones) {
        this.createDateComunicaciones = createDateComunicaciones;
    }

    public String getCreateDateEstado() {
        return createDateEstado;
    }

    public void setCreateDateEstado(String createDateEstado) {
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
