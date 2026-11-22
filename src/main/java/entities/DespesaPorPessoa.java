package entities;

import java.io.Serial;
import java.util.LinkedHashMap;
import java.util.Map;

public class DespesaPorPessoa extends Despesa{

    @Serial
    private static final long serialVersionUID = 1L;

    private int valorUnitarioCent;

    public DespesaPorPessoa(String descricao, int valorUnitarioCent){
        super(descricao);
        this.valorUnitarioCent = valorUnitarioCent;
    }

    public int getValorUnitarioCent() {
        return valorUnitarioCent;
    }

    @Override 
    /* Calcula o valor total da despesa por pessoa */
    public int calcTotalCent(int qtdUser){
        return valorUnitarioCent * qtdUser;
    }

    @Override
    public Map<Participante, Integer> calcularRateio() {
        if (getParticipantes().isEmpty()) {
            throw new IllegalStateException("A despesa precisa de pelo menos um participante.");
        }
        Map<Participante, Integer> rateio = new LinkedHashMap<>();
        for (Participante participante : getParticipantes()) {
            rateio.put(participante, valorUnitarioCent);
        }
        return rateio;
    }
}
