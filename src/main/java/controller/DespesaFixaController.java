package controller;

import java.util.List;
import java.util.stream.Collectors;

import entities.DespesaFixa;
import entities.Participante;
import service.DespesaService;

/**
 * Controller especializado para despesas do tipo Fixa.
 */
public class DespesaFixaController {

    private final DespesaService despesaService;
    private final DespesaPorPessoaController despesaPorPessoaController;
    private final DespesaController despesaController;

    public DespesaFixaController(DespesaService despesaService,
                                DespesaPorPessoaController despesaPorPessoaController,
                                DespesaController despesaController) {
        this.despesaService = despesaService;
        this.despesaPorPessoaController = despesaPorPessoaController;
        this.despesaController = despesaController;
    }

    public void cadastrarDespesaFixa(String descricao, int valorCent, List<Participante> participantes) {
        if (participantes.isEmpty()) {
            throw new RuntimeException("A despesa precisa ter pelo menos um participante.");
        }
        DespesaFixa despesa = new DespesaFixa(descricao, valorCent);
        despesa.definirParticipantes(participantes);
        despesaService.cadastrar(despesa);
        if (despesaController != null) {
            despesaController.atualizarTabela();
        }
    }

    public List<String> calcularRateioDespesaFixa(String descricao) {
        var despesa = despesaService.buscar(descricao);
        var rateio = despesa.calcularRateio();
        return rateio.entrySet().stream()
                .map(e -> e.getKey().getNome() + ": R$ " + String.format("%.2f", e.getValue() / 100.0))
                .collect(Collectors.toList());
    }
}