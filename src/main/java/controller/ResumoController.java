package controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import entities.DespesaFixa;
import entities.Participante;
import service.DespesaService;
import service.ResumoService;
import util.Moeda;
import view.ResumoPanel;

/**
 * Controller responsável pela aba Resumo.
 * As contas vêm prontas do ResumoService, aqui só formata pra tela.
 */
public class ResumoController {

    private final ResumoPanel view;
    private final DespesaService despesaService;
    private final ResumoService resumoService;

    public ResumoController(DespesaService despesaService, ResumoService resumoService, ResumoPanel view) {
        this.despesaService = despesaService;
        this.resumoService = resumoService;
        this.view = view;

        // Conecta os eventos da view
        view.onAtualizar(e -> atualizarResumo());
    }

    /**
     * Atualiza o resumo com os dados atuais
     */
    public void atualizarResumo() {
        view.setCustoTotal("R$ " + Moeda.formatar(resumoService.custoTotal()));

        // calcTotalCent é sobrescrito em cada tipo de despesa (polimorfismo)
        view.setDespesas(despesaService.listar().stream()
                .map(d -> new Object[]{
                        d.getDescricao(),
                        d instanceof DespesaFixa ? "Fixa" : "Por pessoa",
                        Moeda.formatar(d.calcTotalCent(d.getParticipantes().size()))
                })
                .collect(Collectors.toList()));

        Map<Participante, Integer> totais = resumoService.totalPorParticipante();
        List<Object[]> linhas = totais.entrySet().stream()
                .map(t -> new Object[]{
                        t.getKey().toString(),
                        Moeda.formatar(t.getKey().getOrcamentoCent()),
                        Moeda.formatar(t.getValue()),
                        situacao(t.getKey(), t.getValue())
                })
                .collect(Collectors.toList());
        view.setParticipantes(linhas);
    }

    private String situacao(Participante p, int totalCent) {
        int excedente = resumoService.excedente(p, totalCent);
        return excedente == 0 ? "Dentro do orçamento" : "Excede em R$ " + Moeda.formatar(excedente);
    }
}
