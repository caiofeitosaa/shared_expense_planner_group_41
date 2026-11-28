package controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import entities.Despesa;
import entities.DespesaFixa;
import entities.DespesaPorPessoa;
import entities.Participante;
import service.DespesaService;
import service.ParticipanteService;
import view.ResumoPanel;

/**
 * Controller responsável pelo resumo e estatísticas.
 * Calcula o custo total, o rateio de cada participante e exibe o status.
 */
public class ResumoController {

    private final ResumoPanel view;
    private final DespesaService despesaService;
    private final ParticipanteService participanteService;

    public ResumoController(DespesaService despesaService,
                            ParticipanteService participanteService,
                            ResumoPanel view) {
        this.despesaService = despesaService;
        this.participanteService = participanteService;
        this.view = view;

        // Conecta os eventos da view
        view.onAtualizar(this::atualizarResumo);
    }

    /**
     * Atualiza o resumo com os dados atuais
     */
    public void atualizarResumo(ActionEvent e) {
        List<Despesa> despesas = despesaService.listar();
        List<Participante> participantes = participanteService.listar();

        // Calcula o custo total
        int custoTotalCent = despesas.stream()
                .mapToInt(d -> calcularValorTotalDespesa(d))
                .sum();

        view.setCustoTotal(formatarMoeda(custoTotalCent));

        // Atualiza a tabela de despesas
        List<Object[]> linhasDespesas = despesas.stream()
                .map(d -> new Object[]{
                        d.getDescricao(),
                        d instanceof DespesaFixa ? "Fixa" : "Por pessoa",
                        formatarMoeda(calcularValorTotalDespesa(d))
                })
                .collect(Collectors.toList());
        view.setDespesas(linhasDespesas);

        // Calcula o rateio por participante
        Map<Participante, Integer> rateioTotal = despesas.stream()
                .flatMap(d -> d.calcularRateio().entrySet().stream())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingInt(Map.Entry::getValue)
                ));

        // Atualiza a tabela de participantes com status
        List<Object[]> linhasParticipantes = participantes.stream()
                .map(p -> {
                    Integer totalCent = rateioTotal.getOrDefault(p, 0);
                    String situacao = calcularSituacao(p, totalCent);
                    return new Object[]{
                            p.getNome(),
                            formatarMoeda(p.getOrcamentoCent()),
                            formatarMoeda(totalCent),
                            situacao
                    };
                })
                .collect(Collectors.toList());
        view.setParticipantes(linhasParticipantes);
    }

    /**
     * Calcula o valor total de uma despesa
     */
    private int calcularValorTotalDespesa(Despesa d) {
        if (d instanceof DespesaFixa) {
            return ((DespesaFixa) d).getValorTotalCent();
        } else if (d instanceof DespesaPorPessoa) {
            // Para despesa por pessoa, multiplicar pelo número de participantes
            return ((DespesaPorPessoa) d).getValorUnitarioCent() * d.getParticipantes().size();
        }
        return 0;
    }

    /**
     * Calcula a situação do participante (dentro/estourou)
     */
    private String calcularSituacao(Participante p, int totalCent) {
        if (totalCent <= p.getOrcamentoCent()) {
            return "Dentro do orçamento (sobra " + formatarMoeda(p.getOrcamentoCent() - totalCent) + ")";
        }
        return "Excede em " + formatarMoeda(totalCent - p.getOrcamentoCent());
    }

    /**
     * Formata um valor em centavos para moeda brasileira
     */
    private String formatarMoeda(int valorCent) {
        return util.Moeda.formatar(valorCent);
    }
}