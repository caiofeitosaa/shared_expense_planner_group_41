package controller;

import java.util.List;

import entities.Participante;
import service.DespesaFixaService;

/**
 * Controller especializado para despesas do tipo Fixa.
 * O DespesaController chama aqui quando o tipo escolhido na tela é "Fixa".
 */
public class DespesaFixaController {

    private final DespesaFixaService despesaFixaService;

    public DespesaFixaController(DespesaFixaService despesaFixaService) {
        this.despesaFixaService = despesaFixaService;
    }

    public void cadastrar(String descricao, int valorTotalCent, List<Participante> participantes) {
        despesaFixaService.cadastrar(descricao, valorTotalCent, participantes);
    }

    public void alterar(String descricaoAtual, String novaDescricao, int valorTotalCent, List<Participante> participantes) {
        despesaFixaService.alterar(descricaoAtual, novaDescricao, valorTotalCent, participantes);
    }
}
