
package com.acculab.dao;

import com.acculab.models.Medico;

import java.util.List;
import java.util.stream.Collectors;

public class MedicoDAO extends GenericBinaryDAO<Medico, String> {

    public MedicoDAO() {
        super("data/medicos.bin", Medico::getId);
    }

    public List<Medico> findAllActivos() {
        return findAll().stream()
                .filter(m -> !m.isEliminado())
                .collect(Collectors.toList());
    }
}
