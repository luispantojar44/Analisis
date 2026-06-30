package com.acculab.models;

import java.io.Serializable;

public class Medico implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private String telefono;
    private String correo;
    private boolean eliminado;

    public Medico(String id, String nombres, String apellidos, String especialidad, String telefono, String correo) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.especialidad = especialidad;
        this.telefono = telefono;
        this.correo = correo;
        this.eliminado = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }

    @Override
    public String toString() {
        return nombres + " " + apellidos;
    }
}
