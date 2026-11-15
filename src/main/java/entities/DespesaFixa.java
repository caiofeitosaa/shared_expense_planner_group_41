package entities;

public class FixedExpense extends Expense {
    
    private int valorTotalCent;

    /* Aqui ele vai pedir descrição da classe mãe (Expense) e o valor total em centavos */
    public FixedExpense(String descricao, int valorTotalCent) {
        super(descricao);
        this.valorTotalCent = valorTotalCent;
    }

    @Override 
    public int calcTotalCent(int qtdUser) {
        return valorTotalCent;
    }
}

/* 
- Override é um metódo que vai sobrescrever o metódo da classe mãe (Expense);
- 
*/