package com.example.signpad.objetos;

public class Pessoa {

    private char foto;
    private char assinatura;


    public Pessoa(){

    }

    public Pessoa(char foto, char assinatura) {
        this.foto = foto;
        this.assinatura = assinatura;
    }

    public char getFoto() {
        return foto;
    }

    public void setFoto(char foto) {
        this.foto = foto;
    }

    public char getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(char assinatura) {
        this.assinatura = assinatura;
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "foto='" + foto + '\'' +
                ", assinatura=" + assinatura +
                '}';
    }
}
