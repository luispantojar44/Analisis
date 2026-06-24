package com.acculab.models;

import java.io.Serializable;
import java.util.Objects;

public class Prueba implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String unidad;
    private double refMinMasculino;
    private double refMaxMasculino;
    private double refMinFemenino;
    private double refMaxFemenino;
    private double precio;

    public Prueba(String id, String nombre, String unidad, 
                  double refMinMasculino, double refMaxMasculino,
                  double refMinFemenino, double refMaxFemenino, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.unidad = unidad;
        this.refMinMasculino = refMinMasculino;
        this.refMaxMasculino = refMaxMasculino;
        this.refMinFemenino = refMinFemenino;
        this.refMaxFemenino = refMaxFemenino;
        this.precio = precio;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public double getRefMinMasculino() { return refMinMasculino; }
    public void setRefMinMasculino(double refMinMasculino) { this.refMinMasculino = refMinMasculino; }
    public double getRefMaxMasculino() { return refMaxMasculino; }
    public void setRefMaxMasculino(double refMaxMasculino) { this.refMaxMasculino = refMaxMasculino; }
    public double getRefMinFemenino() { return refMinFemenino; }
    public void setRefMinFemenino(double refMinFemenino) { this.refMinFemenino = refMinFemenino; }
    public double getRefMaxFemenino() { return refMaxFemenino; }
    public void setRefMaxFemenino(double refMaxFemenino) { this.refMaxFemenino = refMaxFemenino; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prueba prueba = (Prueba) o;
        return Objects.equals(id, prueba.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
