package com.acculab.models;

import java.io.Serializable;

public class Resultado implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Prueba prueba;
    private double valor;
    private String alerta;
    
    public Resultado(Prueba prueba, double valor) {
        this.prueba = prueba;
        this.valor = valor;
        this.alerta = "PENDIENTE";
    }
    
    public void validarRango(Paciente paciente) {
        boolean fueraDeRango = false;
        if (paciente.getSexo() == Paciente.Sexo.MASCULINO) {
            if (valor < prueba.getRefMinMasculino() || valor > prueba.getRefMaxMasculino()) fueraDeRango = true;
        } else {
            if (valor < prueba.getRefMinFemenino() || valor > prueba.getRefMaxFemenino()) fueraDeRango = true;
        }
        
        if (fueraDeRango) {
            this.alerta = "FUERA DE RANGO";
        } else {
            this.alerta = "NORMAL";
        }
    }

    public Prueba getPrueba() { return prueba; }
    public double getValor() { return valor; }
    public String getAlerta() { return alerta; }
    public boolean isFueraDeRango() { return "FUERA DE RANGO".equals(alerta); }
}
