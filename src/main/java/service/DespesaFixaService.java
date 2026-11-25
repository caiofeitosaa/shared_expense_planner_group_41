package service;

import entities.DespesaFixa;
import entities.Participante;
import repositories.DespesaRepository;

import java.util.List;

// Regras da despesa fixa (valor total dividido entre os participantes).
// As regras que valem pra qualquer despesa ficam no DespesaService, aqui só monta a fixa
// e trata os erros com uma mensagem simples pra tela mostrar.
public class DespesaFixaService {
    private final DespesaService despesaService;

    public DespesaFixaService(DespesaRepository despesaRepository){
        this.despesaService = new DespesaService(despesaRepository);
    } // Final do método construtor

    public void cadastrar(String descricao, int valorTotalCent, List<Participante> participantes){
        try {
            despesaService.cadastrar(montar(descricao, valorTotalCent, participantes));
        } catch (IllegalArgumentException e) {
            // Dado inválido: repassa o motivo pra tela mostrar
            throw new IllegalArgumentException("Despesa fixa não cadastrada: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            // Qualquer outro erro aqui é falha ao gravar o arquivo
            throw new RuntimeException("Despesa fixa não cadastrada: erro ao salvar no arquivo.", e);
        }
    } // Final do método cadastrar

    // Troca a despesa que tem a descricaoAtual por uma nova com os dados informados
    public void alterar(String descricaoAtual, String novaDescricao, int novoValorTotalCent, List<Participante> participantes){
        try {
            despesaService.alterar(descricaoAtual, montar(novaDescricao, novoValorTotalCent, participantes));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Despesa fixa não alterada: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new RuntimeException("Despesa fixa não alterada: erro ao salvar no arquivo.", e);
        }
    } // Final do método alterar

    public void remover(String descricao){
        try {
            despesaService.remover(descricao);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Despesa fixa não removida: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new RuntimeException("Despesa fixa não removida: erro ao salvar no arquivo.", e);
        }
    } // Final do método remover

    // Cria a despesa fixa. Só confere o valor, o resto o DespesaService e o repository conferem
    private DespesaFixa montar(String descricao, int valorTotalCent, List<Participante> participantes){
        if (valorTotalCent <= 0) {
            throw new IllegalArgumentException("O valor precisa ser maior que zero.");
        }
        DespesaFixa despesa = new DespesaFixa(descricao, valorTotalCent);
        despesa.definirParticipantes(participantes);
        return despesa;
    } // Final do método montar
}
