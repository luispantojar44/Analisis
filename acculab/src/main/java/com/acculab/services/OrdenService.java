package com.acculab.services;

import com.acculab.dao.OrdenDAO;
import com.acculab.models.*;

public class OrdenService {
    private final OrdenDAO ordenDAO;

    public OrdenService(OrdenDAO ordenDAO) {
        this.ordenDAO = ordenDAO;
    }

    public void registrarAbono(Orden orden, double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El abono debe ser mayor a 0");
        }
        orden.setAbono(orden.getAbono() + monto);
        ordenDAO.update(orden);
    }

    public void agregarPerfilAOrden(Orden orden, Perfil perfil) {
        // Al seleccionar un perfil, se agregan sus pruebas de manera individual
        // respetando el orden.
        for (Prueba prueba : perfil.getPruebas()) {
            orden.addPrueba(prueba);
        }
    }

    public void capturarResultado(Orden orden, Prueba prueba, double valor, Paciente paciente) {
        boolean fueraDeRango = false;
        if (paciente.getSexo() == Paciente.Sexo.MASCULINO) {
            if (valor < prueba.getRefMinMasculino() || valor > prueba.getRefMaxMasculino()) {
                fueraDeRango = true;
            }
        } else {
            if (valor < prueba.getRefMinFemenino() || valor > prueba.getRefMaxFemenino()) {
                fueraDeRango = true;
            }
        }

        Resultado resultado = new Resultado(prueba, valor, fueraDeRango);
        orden.addResultado(resultado);

        // Si todas las pruebas tienen resultados, se podría cambiar el estado automáticamente
        if (orden.getResultados().size() == orden.getPruebas().size()) {
            orden.setEstado(EstadoOrden.FINALIZADA);
        }
        
        ordenDAO.update(orden);
    }

    public void marcarComoEliminada(Orden orden) {
        orden.setEstado(EstadoOrden.ELIMINADA);
        ordenDAO.update(orden);
    }
}
