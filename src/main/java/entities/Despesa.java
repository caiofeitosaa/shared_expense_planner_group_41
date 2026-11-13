package entities;

public abstract class Expense {
    private String descricao;

    public Expense(String descricao) {
        this.descricao = descricao;
    }

    /* Metódo da Classe
    As depesas filhas vão receber o atributo descricao
    */

    // Descricao - Get e Set
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao){
        this.descricao = descricao;
    }
    
    /* Metódo da Classe
    Vai funcionar diferente pra cada despesa filha
    */
    public abstract int calcTotalCent(int qtdUser);

}

/*
- (metódo construtor, tem que ser public pra poder acessar fora)
No public expense, ele diz que toda expense tem que receber uma descricao
- this descricao significa que o atributo da classe expense é o mesmo que o paramêtro do metódo
- 
*/