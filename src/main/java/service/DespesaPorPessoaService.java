package service;

import entities.DespesaPorPessoa;
import entities.Participante;
import repositories.DespesaRepository;

import java.util.List;

// Regras da despesa por pessoa (cada participante paga o valor unitário).
// Segue o mesmo modelo do DespesaFixaService: as regras gerais ficam no DespesaService,
// aqui só monta a despesa por pessoa e trata os erros com uma mensagem simples pra tela mostrar.
public class DespesaPorPessoaService {
    private final DespesaService despesaService;

    public DespesaPorPessoaService(DespesaRepository despesaRepository){
        this.despesaService = new DespesaService(despesaRepository);
    } // Final do método construtor

    public void cadastrar(String descricao, int valorUnitarioCent, List<Participante> participantes){
        try {
            despesaService.cadastrar(montar(descricao, valorUnitarioCent, participantes));
        } catch (IllegalArgumentException e) {
            // Dado inválido: repassa o motivo pra tela mostrar
            throw new IllegalArgumentException("Despesa por pessoa não cadastrada: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            // Qualquer outro erro aqui é falha ao gravar o arquivo
            throw new RuntimeException("Despesa por pessoa não cadastrada: erro ao salvar no arquivo.", e);
        }
    } // Final do método cadastrar

    // Troca a despesa que tem a descricaoAtual por uma nova com os dados informados
    public void alterar(String descricaoAtual, String novaDescricao, int novoValorUnitarioCent, List<Participante> participantes){
        try {
            despesaService.alterar(descricaoAtual, montar(novaDescricao, novoValorUnitarioCent, participantes));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Despesa por pessoa não alterada: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new RuntimeException("Despesa por pessoa não alterada: erro ao salvar no arquivo.", e);
        }
    } // Final do método alterar

    public void remover(String descricao){
        try {
            despesaService.remover(descricao);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Despesa por pessoa não removida: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new RuntimeException("Despesa por pessoa não removida: erro ao salvar no arquivo.", e);
        }
    } // Final do método remover

    // Cria a despesa por pessoa. Só confere o valor, o resto o DespesaService e o repository conferem
    private DespesaPorPessoa montar(String descricao, int valorUnitarioCent, List<Participante> participantes){
        if (valorUnitarioCent <= 0) {
            throw new IllegalArgumentException("O valor precisa ser maior que zero.");
        }
        DespesaPorPessoa despesa = new DespesaPorPessoa(descricao, valorUnitarioCent);
        despesa.definirParticipantes(participantes);
        return despesa;
    } // Final do método montar
}
