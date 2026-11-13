package entities;

public class ExpensePerPerson extends Expense{
    private int valorUnitarioCent;

    public ExpensePerPerson(String descricao, int valorUnitarioCent){
        super(descricao);
        this.valorUnitarioCent = valorUnitarioCent;
    }

    @Override 
    /* Calcula o valor total da despesa por pessoa */
    public int calcTotalCent(int qtdUser){
        return valorUnitarioCent * qtdUser;
    }
}
