package entities;

public class User {

    //Atributos
    private String nome;
    private int orcamentoCent;
    //Pede o nome e o orçamento em centavos do usuário
    public User (String nome, int orcamentoCent){
        this.nome = nome;
        this.orcamentoCent = orcamentoCent;
    }

    public String getNome() {
        return nome;
    }

    public int getOrcamentoCent() {
        return orcamentoCent;
    }

    /* Verifica se o usuário pode pagar por uma despesa */
    public boolean podePagar(int custoCent){
        return custoCent <= orcamentoCent;
    }

    /* Aqui mostra o usuário e o orçamento */
    @Override 
    public String toString(){
        return nome + " " + String.valueOf(orcamentoCent); 
    }
}

/*
- Colocar os atributos, que são as características de cada classe
- Por os metódos de construção, que são as regras de cada classe

- set recbe um valor de fora e guarda
- get entrega esse valor guardado para quem pediu por fora
*/