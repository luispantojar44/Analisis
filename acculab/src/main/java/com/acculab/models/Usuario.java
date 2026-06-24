package com.acculab.models;

import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Rol {
        RECEPCIONISTA,
        LABORATORISTA,
        ADMINISTRADOR
    }

    private String id;
    private String nombre;
    private String username;
    private String hashPassword;
    private Rol rol;
    private boolean activo;

    public Usuario(String id, String nombre, String username, String hashPassword, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.hashPassword = hashPassword;
        this.rol = rol;
        this.activo = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getHashPassword() { return hashPassword; }
    public void setHashPassword(String hashPassword) { this.hashPassword = hashPassword; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
