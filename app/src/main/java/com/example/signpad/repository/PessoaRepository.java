package com.example.signpad.repository;

import com.example.signpad.objetos.Pessoa;

import java.util.ArrayList;

import java.util.List;

public class PessoaRepository {

    private static List<Pessoa> pessoas = new ArrayList<>();

    public static void save(Pessoa pessoa){ pessoas.add(pessoa);
    }

    public static List<Pessoa> findAll() { return pessoas;
    }

    public static void update( Pessoa pessoa){
        //update Objeto
    }
    public static void delete (Pessoa pessoa){
        //update do Objeto
    }

}
