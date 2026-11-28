package service;

import entities.Despesa;
import entities.Participante;
import repositories.DespesaRepository;
import repositories.ParticipanteRepository;

import java.util.LinkedHashMap;
import java.util.Map;

// Contas da aba Resumo. Só lê os dados, não altera nada. Tudo em centavos,
// quem transforma em "R$ 75,00" é o controller/view.
public class ResumoService {
    private final ParticipanteRepository participanteRepository;
    private final DespesaRepository despesaRepository;

    public ResumoService(ParticipanteRepository participanteRepository, DespesaRepository despesaRepository){
        this.participanteRepository = participanteRepository;
        this.despesaRepository = despesaRepository;
    } // Final do método construtor

    // Quanto cada participante paga no total, somando a parte dele em cada despesa.
    // Começa todo mundo com zero, assim quem não está em nenhuma despesa aparece com 0
    public Map<Participante, Integer> totalPorParticipante(){
        Map<Participante, Integer> totais = new LinkedHashMap<>();
        for (Participante p : participanteRepository.listarTodos()) {
            totais.put(p, 0);
        }
        for (Despesa d : despesaRepository.listarTodas()) {
            d.calcularRateio().forEach((p, valorCent) -> totais.merge(p, valorCent, Integer::sum));
        }
        return totais;
    } // Final do método totalPorParticipante

    // Soma de tudo que o grupo vai gastar
    public int custoTotal(){
        return totalPorParticipante().values().stream().mapToInt(Integer::intValue).sum();
    } // Final do método custoTotal

    // Quanto passou do orçamento. 0 = dentro do orçamento (total igual ao limite também está dentro)
    public int excedente(Participante participante, int totalCent){
        return Math.max(0, totalCent - participante.getOrcamentoCent());
    } // Final do método excedente
}
