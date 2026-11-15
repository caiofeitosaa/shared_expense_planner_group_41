package entities;

public class DespesaPorPessoa extends Despesa{
    private int valorUnitarioCent;

    public DespesaPorPessoa(String descricao, int valorUnitarioCent){
        super(descricao);
        this.valorUnitarioCent = valorUnitarioCent;
    }

    @Override 
    /* Calcula o valor total da despesa por pessoa */
    public int calcTotalCent(int qtdUser){
        return valorUnitarioCent * qtdUser;
    }
}
