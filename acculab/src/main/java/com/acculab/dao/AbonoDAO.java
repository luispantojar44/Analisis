package com.acculab.dao;

import com.acculab.models.Abono;
import java.util.List;
import java.util.stream.Collectors;

public class AbonoDAO extends GenericBinaryDAO<Abono, String> {
    
    public AbonoDAO() {
        super("data/abonos.bin", Abono::getId);
    }
    
    public List<Abono> buscarPorOrdenId(String ordenId) {
        return findAll().stream()
                .filter(a -> a.getOrdenId().equals(ordenId))
                .collect(Collectors.toList());
    }
}
