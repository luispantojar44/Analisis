package com.acculab.services;

import com.acculab.dao.UsuarioDAO;
import com.acculab.models.Usuario;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private final UsuarioDAO usuarioDAO;

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Autentica a un usuario verificando su username y contraseña.
     * @param username El nombre de usuario.
     * @param plainPassword La contraseña en texto plano.
     * @return El objeto Usuario si la autenticación es exitosa, null en caso contrario.
     */
    public Usuario login(String username, String plainPassword) {
        Usuario usuario = usuarioDAO.findByUsername(username);
        
        if (usuario != null) {
            // Verificar el hash de la contraseña
            if (BCrypt.checkpw(plainPassword, usuario.getHashPassword())) {
                return usuario; // Login exitoso
            }
        }
        return null; // Login fallido
    }

    /**
     * Registra un nuevo usuario hasheando su contraseña antes de guardarlo.
     */
    public void registrarUsuario(Usuario nuevoUsuario, String plainPassword) {
        // Generar el hash usando BCrypt
        String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        nuevoUsuario.setHashPassword(hashed);
        
        usuarioDAO.save(nuevoUsuario);
    }
}
