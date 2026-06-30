package com.acculab.dao;

import com.acculab.models.Perfil;

public class PerfilDAO extends GenericBinaryDAO<Perfil, String> {

    public PerfilDAO() {
        super("data/perfiles.bin", Perfil::getId);
    }
}
