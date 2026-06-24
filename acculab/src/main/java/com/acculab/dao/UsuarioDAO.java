package com.acculab.dao;

import com.acculab.models.Usuario;

public class UsuarioDAO extends GenericBinaryDAO<Usuario, String> {
    
    // Asume que los archivos de datos se guardarán en una carpeta "data"
    public UsuarioDAO() {
        super("data/usuarios.dat", Usuario::getId);
    }

    public Usuario findByUsername(String username) {
        return findAll().stream()
                .filter(u -> u.getUsername().equals(username) && u.isActivo())
                .findFirst()
                .orElse(null);
    }
}
