package com.acculab.models;

import java.io.Serializable;

public class Resultado implements Serializable {
    private static final long serialVersionUID = 1L;

    private Prueba prueba;
    private double valor;
    private boolean fueraDeRango;

    public Resultado(Prueba prueba, double valor, boolean fueraDeRango) {
        this.prueba = prueba;
        this.valor = valor;
        this.fueraDeRango = fueraDeRango;
    }

    public Prueba getPrueba() { return prueba; }
    public void setPrueba(Prueba prueba) { this.prueba = prueba; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public boolean isFueraDeRango() { return fueraDeRango; }
    public void setFueraDeRango(boolean fueraDeRango) { this.fueraDeRango = fueraDeRango; }
}
