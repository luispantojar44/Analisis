package com.acculab.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Abono implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String ordenId;
    private double monto;
    private LocalDateTime fecha;
    
    public Abono(String id, String ordenId, double monto) {
        this.id = id;
        this.ordenId = ordenId;
        this.monto = monto;
        this.fecha = LocalDateTime.now();
    }
    
    public String getId() { return id; }
    public String getOrdenId() { return ordenId; }
    public double getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
}
