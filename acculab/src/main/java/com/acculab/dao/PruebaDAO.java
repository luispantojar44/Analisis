package com.acculab.dao;

import com.acculab.models.Prueba;

public class PruebaDAO extends GenericBinaryDAO<Prueba, String> {

    public PruebaDAO() {
        super("data/pruebas.bin", Prueba::getId);
    }
}
