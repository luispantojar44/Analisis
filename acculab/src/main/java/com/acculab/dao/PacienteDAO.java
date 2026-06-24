package com.acculab.dao;

import com.acculab.models.Paciente;

import java.util.List;
import java.util.stream.Collectors;

public class PacienteDAO extends GenericBinaryDAO<Paciente, String> {

    public PacienteDAO() {
        super("data/pacientes.dat", Paciente::getId);
    }

    // Método para obtener solo los pacientes que NO están eliminados lógicamente
    public List<Paciente> findAllActivos() {
        return findAll().stream()
                .filter(p -> !p.isEliminado())
                .collect(Collectors.toList());
    }
}
