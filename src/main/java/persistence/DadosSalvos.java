package persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import entities.Despesa;
import entities.Participante;

public class DadosSalvos implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Participante> participantes;
    private final List<Despesa> despesas;

    public DadosSalvos(List<Participante> participantes, List<Despesa> despesas) {
        this.participantes = new ArrayList<>(participantes);
        this.despesas = new ArrayList<>(despesas);
    }

    public List<Participante> getParticipantes() {
        return List.copyOf(participantes);
    }

    public List<Despesa> getDespesas() {
        return List.copyOf(despesas);
    }
}