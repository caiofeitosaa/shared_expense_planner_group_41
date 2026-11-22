package entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Despesa implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String descricao;
    private final List<Participante> participantes = new ArrayList<>();

    public Despesa(String descricao) {
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

    public List<Participante> getParticipantes() {
        return List.copyOf(participantes);
    }

    public void definirParticipantes(List<Participante> participantes) {
        this.participantes.clear();
        if (participantes != null) {
            this.participantes.addAll(participantes);
        }
    }
    
    /* Metódo da Classe
    Vai funcionar diferente pra cada despesa filha
    */
    public abstract int calcTotalCent(int qtdUser);

    public abstract Map<Participante, Integer> calcularRateio();

}

/*
- (metódo construtor, tem que ser public pra poder acessar fora)
No public expense, ele diz que toda expense tem que receber uma descricao
- this descricao significa que o atributo da classe expense é o mesmo que o paramêtro do metódo
- 
*/