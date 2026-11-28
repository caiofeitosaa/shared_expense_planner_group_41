package controller;

import java.util.List;

import entities.Participante;
import service.DespesaPorPessoaService;

/**
 * Controller especializado para despesas do tipo Por Pessoa.
 * O DespesaController chama aqui quando o tipo escolhido na tela é "Por pessoa".
 */
public class DespesaPorPessoaController {

    private final DespesaPorPessoaService despesaPorPessoaService;

    public DespesaPorPessoaController(DespesaPorPessoaService despesaPorPessoaService) {
        this.despesaPorPessoaService = despesaPorPessoaService;
    }

    public void cadastrar(String descricao, int valorUnitarioCent, List<Participante> participantes) {
        despesaPorPessoaService.cadastrar(descricao, valorUnitarioCent, participantes);
    }

    public void alterar(String descricaoAtual, String novaDescricao, int valorUnitarioCent, List<Participante> participantes) {
        despesaPorPessoaService.alterar(descricaoAtual, novaDescricao, valorUnitarioCent, participantes);
    }
}
