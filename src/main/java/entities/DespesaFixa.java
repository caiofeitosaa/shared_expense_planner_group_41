package entities;

public class DespesaFixa extends Despesa {
    
    private int valorTotalCent;

    /* Aqui ele vai pedir descrição da classe mãe (Despesa) e o valor total em centavos */
    public DespesaFixa(String descricao, int valorTotalCent) {
        super(descricao);
        this.valorTotalCent = valorTotalCent;
    }

    @Override 
    public int calcTotalCent(int qtdUser) {
        return valorTotalCent;
    }
}

/* 
- Override é um metódo que vai sobrescrever o metódo da classe mãe (Despesa);
- 
*/