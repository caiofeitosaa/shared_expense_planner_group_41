package entities;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DespesaFixa extends Despesa {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private int valorTotalCent;

    /* Aqui ele vai pedir descrição da classe mãe (Despesa) e o valor total em centavos */
    public DespesaFixa(String descricao, int valorTotalCent) {
        super(descricao);
        this.valorTotalCent = valorTotalCent;
    }

    public int getValorTotalCent() {
        return valorTotalCent;
    }

    @Override 
    public int calcTotalCent(int qtdUser) {
        return valorTotalCent;
    }

    @Override
    public Map<Participante, Integer> calcularRateio() {
        List<Participante> envolvidos = new ArrayList<>(getParticipantes());
        // Regra do trabalho: os centavos que sobram vão pros menores códigos primeiro
        envolvidos.sort(Comparator.comparingInt(Participante::getCodigo));
        if (envolvidos.isEmpty()) {
            throw new IllegalStateException("A despesa precisa de pelo menos um participante.");
        }
        int quantidade = envolvidos.size();
        int cota = valorTotalCent / quantidade;
        int resto = valorTotalCent % quantidade;
        Map<Participante, Integer> rateio = new LinkedHashMap<>();
        for (int i = 0; i < quantidade; i++) {
            rateio.put(envolvidos.get(i), cota + (i < resto ? 1 : 0));
        }
        return rateio;
    }
}

/* 
- Override é um metódo que vai sobrescrever o metódo da classe mãe (Despesa);
- 
*/