package entities;

import java.io.Serial;
import java.io.Serializable;

public class Participante implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    //Atributos
    private final int codigo;
    private String nome;
    private int orcamentoCent;

    //Pede o código, o nome e o orçamento em centavos do usuário
    public Participante(int codigo, String nome, int orcamentoCent) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("Código deve ser positivo.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        if (orcamentoCent < 0) {
            throw new IllegalArgumentException("Orçamento não pode ser negativo.");
        }
        this.codigo = codigo;
        this.nome = nome;
        this.orcamentoCent = orcamentoCent;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        this.nome = nome;
    }

    public int getOrcamentoCent() {
        return orcamentoCent;
    }

    public void setOrcamentoCent(int orcamentoCent) {
        if (orcamentoCent < 0) {
            throw new IllegalArgumentException("Orçamento não pode ser negativo.");
        }
        this.orcamentoCent = orcamentoCent;
    }

    /* Verifica se o usuário pode pagar por uma despesa */
    public boolean podePagar(int custoCent){
        return custoCent <= orcamentoCent;
    }

    /* Aqui mostra o usuário e o orçamento */
    @Override 
    public String toString(){
        return codigo + " - " + nome; 
    }
}

/*
- Colocar os atributos, que são as características de cada classe
- Por os metódos de construção, que são as regras de cada classe

- set recbe um valor de fora e guarda
- get entrega esse valor guardado para quem pediu por fora
*/