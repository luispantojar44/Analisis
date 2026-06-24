package com.acculab.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Orden implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private Paciente paciente;
    private String medicoSolicitante;
    private LocalDateTime fechaCreacion;
    private EstadoOrden estado;
    
    // Pruebas solicitadas (mantiene el orden)
    private List<Prueba> pruebas;
    // Resultados de las pruebas
    private List<Resultado> resultados;
    
    // Financiero
    private double costoTotal;
    private double abono;

    public Orden(String id, Paciente paciente, String medicoSolicitante) {
        this.id = id;
        this.paciente = paciente;
        this.medicoSolicitante = medicoSolicitante;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoOrden.PENDIENTE;
        this.pruebas = new ArrayList<>();
        this.resultados = new ArrayList<>();
        this.costoTotal = 0.0;
        this.abono = 0.0;
    }

    public void addPrueba(Prueba prueba) {
        if (!pruebas.contains(prueba)) {
            pruebas.add(prueba);
        }
    }

    public void addResultado(Resultado resultado) {
        resultados.removeIf(r -> r.getPrueba().equals(resultado.getPrueba()));
        resultados.add(resultado);
    }

    public double getSaldoDeudor() {
        return costoTotal - abono;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public String getMedicoSolicitante() { return medicoSolicitante; }
    public void setMedicoSolicitante(String medicoSolicitante) { this.medicoSolicitante = medicoSolicitante; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }
    public List<Prueba> getPruebas() { return pruebas; }
    public void setPruebas(List<Prueba> pruebas) { this.pruebas = pruebas; }
    public List<Resultado> getResultados() { return resultados; }
    public void setResultados(List<Resultado> resultados) { this.resultados = resultados; }
    public double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(double costoTotal) { this.costoTotal = costoTotal; }
    public double getAbono() { return abono; }
    public void setAbono(double abono) { this.abono = abono; }
}
