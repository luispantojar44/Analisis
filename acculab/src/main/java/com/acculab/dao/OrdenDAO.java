package com.acculab.dao;

import com.acculab.models.Orden;
import com.acculab.models.Paciente;
import java.util.List;
import java.util.stream.Collectors;

public class OrdenDAO extends GenericBinaryDAO<Orden, String> {

    public OrdenDAO() {
        super("data/ordenes.bin", Orden::getId);
    }

    public List<Orden> findByPaciente(Paciente paciente) {
        return findAll().stream()
                .filter(o -> o.getPaciente().getId().equals(paciente.getId()))
                .collect(Collectors.toList());
    }
}
