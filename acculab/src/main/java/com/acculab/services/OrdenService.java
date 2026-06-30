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
        Resultado resultado = new Resultado(prueba, valor);
        resultado.validarRango(paciente);
        orden.getResultados().add(resultado);

        // Si todas las pruebas tienen resultados, se podría cambiar el estado automáticamente
        if (orden.getResultados().size() == orden.getPruebas().size()) {
            orden.setEstado(EstadoOrden.FINALIZADA);
        }
        
        ordenDAO.update(orden);
    }
    
    public void actualizarOrden(Orden orden) {
        ordenDAO.update(orden);
    }

    public void marcarComoEliminada(Orden orden) {
        orden.setEstado(EstadoOrden.ELIMINADA);
        ordenDAO.update(orden);
    }
}
