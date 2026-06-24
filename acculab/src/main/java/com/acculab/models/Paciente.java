package com.acculab.models;

import java.io.Serializable;
import java.time.LocalDate;

public class Paciente implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Sexo {
        MASCULINO,
        FEMENINO
    }

    private String id;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private LocalDate fechaNacimiento;
    private Sexo sexo;
    private boolean eliminado; // Para "Soft Delete"

    public Paciente(String id, String nombres, String apellidos, LocalDate fechaNacimiento, Sexo sexo, String telefono, String correo) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.telefono = telefono;
        this.correo = correo;
        this.eliminado = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public Sexo getSexo() { return sexo; }
    public void setSexo(Sexo sexo) { this.sexo = sexo; }
    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }
    
    @Override
    public String toString() {
        return nombres + " " + apellidos;
    }
}
