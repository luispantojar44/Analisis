package com.acculab.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Perfil implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private List<Prueba> pruebas;

    public Perfil(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.pruebas = new ArrayList<>();
    }

    public void addPrueba(Prueba prueba) {
        if (!pruebas.contains(prueba)) {
            pruebas.add(prueba);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Prueba> getPruebas() { return pruebas; }
    public void setPruebas(List<Prueba> pruebas) { this.pruebas = pruebas; }

    @Override
    public String toString() {
        return nombre;
    }
}
