package service;

import entities.Despesa;
import entities.Participante;
import repositories.DespesaRepository;
import repositories.ParticipanteRepository;

import java.util.List;

// Regras dos participantes. O controller chama aqui, e aqui a gente confere as regras
// antes de mandar pro repository guardar.
public class ParticipanteService {

    private final ParticipanteRepository participanteRepository;
    // Precisa também do repository de despesas pra saber se o participante já está em alguma
    private final DespesaRepository despesaRepository;

    public ParticipanteService(ParticipanteRepository participanteRepository, DespesaRepository despesaRepository) {
        this.participanteRepository = participanteRepository;
        this.despesaRepository = despesaRepository;
    }

    // O próprio construtor do Participante já valida código, nome e orçamento,
    // e o repository recusa código repetido
    public void cadastrar(int codigo, String nome, int orcamentoCent) {
        participanteRepository.salvar(new Participante(codigo, nome, orcamentoCent));
    }

    public void alterar(int codigo, String novoNome, int novoOrcamentoCent) {
        Participante p = participanteRepository.buscarPorCodigo(codigo);
        if (p == null) {
            throw new IllegalArgumentException("Participante não encontrado.");
        }

        // Cria um Participante só pra validar os dois valores. Assim, se o orçamento for inválido,
        // o nome não chega a ser trocado (não fica alterado pela metade)
        new Participante(codigo, novoNome, novoOrcamentoCent);

        // Muda o objeto que já existe (e não troca por um novo), porque as despesas
        // apontam pra ele e precisam enxergar o nome e o orçamento novos
        p.setNome(novoNome);
        p.setOrcamentoCent(novoOrcamentoCent);
        participanteRepository.atualizar(p);
    }

    public void remover(int codigo) {
        Participante p = participanteRepository.buscarPorCodigo(codigo);
        if (p == null) {
            throw new IllegalArgumentException("Participante não encontrado.");
        }

        // Regra do trabalho: não pode excluir quem ainda está em alguma despesa
        for (Despesa d : despesaRepository.listarTodas()) {
            if (d.getParticipantes().contains(p)) {
                throw new IllegalArgumentException("Não dá pra excluir " + p.getNome() + ": ele está na despesa \"" + d.getDescricao() + "\".");
            }
        }
        participanteRepository.remover(codigo);
    }

    // Participante com esse código, ou null se não existir
    public Participante buscar(int codigo) {
        return participanteRepository.buscarPorCodigo(codigo);
    }

    public List<Participante> listar() {
        return participanteRepository.listarTodos();
    }
}
