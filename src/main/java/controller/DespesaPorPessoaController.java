package controller;

import java.util.List;
import java.util.stream.Collectors;

import entities.DespesaPorPessoa;
import entities.Participante;
import service.DespesaService;

/**
 * Controller especializado para despesas do tipo Por Pessoa.
 */
public class DespesaPorPessoaController {

    private final DespesaService despesaService;
    private final DespesaController despesaController;

    public DespesaPorPessoaController(DespesaService despesaService,
                                       DespesaController despesaController) {
        this.despesaService = despesaService;
        this.despesaController = despesaController;
    }

    public void cadastrarDespesaPorPessoa(String descricao, int valorCent, List<Participante> participantes) {
        if (participantes.isEmpty()) {
            throw new RuntimeException("A despesa precisa ter pelo menos um participante.");
        }
        DespesaPorPessoa despesa = new DespesaPorPessoa(descricao, valorCent);
        despesa.definirParticipantes(participantes);
        despesaService.cadastrar(despesa);
        if (despesaController != null) {
            despesaController.atualizarTabela();
        }
    }

    public List<String> calcularRateioDespesaPorPessoa(String descricao) {
        var despesa = despesaService.buscar(descricao);
        var rateio = despesa.calcularRateio();
        return rateio.entrySet().stream()
                .map(e -> e.getKey().getNome() + ": R$ " + String.format("%.2f", e.getValue() / 100.0))
                .collect(Collectors.toList());
    }
}