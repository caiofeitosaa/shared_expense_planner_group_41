package service;

import entities.Despesa;
import entities.Participante;
import repositories.DespesaRepository;

import java.util.List;

// Regras das despesas. O controller chama aqui, e aqui a gente confere as regras
// antes de mandar pro repository guardar.
public class DespesaService {

    private final DespesaRepository despesaRepository;

    public DespesaService(DespesaRepository despesaRepository) {
        this.despesaRepository = despesaRepository;
    }

    // Recebe a despesa já montada (fixa ou por pessoa). O repository confere
    // se a descrição é válida e se não está repetida
    public void cadastrar(Despesa despesa) {
        conferirDespesa(despesa);
        despesaRepository.salvar(despesa);
    }

    // Troca a despesa antiga pela nova. O repository avisa se a nova descrição
    // já é de outra despesa
    public void alterar(String descricaoAtual, Despesa despesaAtualizada) {
        conferirExiste(descricaoAtual);
        conferirDespesa(despesaAtualizada);
        despesaRepository.atualizar(descricaoAtual, despesaAtualizada);
    }

    public void remover(String descricao) {
        conferirExiste(descricao);
        despesaRepository.remover(descricao);
    }

    // Despesa com essa descrição, ou null se não existir
    public Despesa buscar(String descricao) {
        return despesaRepository.buscarPorDescricao(descricao);
    }

    public List<Despesa> listar() {
        return despesaRepository.listarTodas();
    }

    /**
     * Adiciona um participante a uma despesa existente
     */
    public void adicionarParticipante(String descricaoDespesa, Participante participante) {
        Despesa despesa = buscar(descricaoDespesa);
        if (despesa == null) {
            throw new RuntimeException("Despesa não encontrada.");
        }
        if (participante == null) {
            throw new RuntimeException("Participante inválido.");
        }

        List<Participante> participantes = despesa.getParticipantes();
        if (participantes.stream().anyMatch(p -> p.getCodigo() == participante.getCodigo())) {
            throw new RuntimeException("Este participante já está na despesa.");
        }

        despesa.adicionarParticipante(participante);
        despesaRepository.atualizar(descricaoDespesa, despesa);
    }

    /**
     * Remove um participante de uma despesa existente
     */
    public void removerParticipante(String descricaoDespesa, Participante participante) {
        Despesa despesa = buscar(descricaoDespesa);
        if (despesa == null) {
            throw new RuntimeException("Despesa não encontrada.");
        }
        if (participante == null) {
            throw new RuntimeException("Participante inválido.");
        }

        if (despesa.getParticipantes().stream().noneMatch(p -> p.getCodigo() == participante.getCodigo())) {
            throw new RuntimeException("Este participante não está na despesa.");
        }
        if (despesa.getParticipantes().size() == 1) {
            throw new RuntimeException("A despesa precisa ter pelo menos um participante.");
        }
        despesa.removerParticipante(participante.getCodigo());

        despesaRepository.atualizar(descricaoDespesa, despesa);
    }

    // Não deixa passar despesa vazia nem sem participantes (sem ninguém não dá pra dividir)
    private void conferirDespesa(Despesa despesa) {
        if (despesa == null) {
            throw new RuntimeException("Despesa inválida.");
        }
        if (despesa.getParticipantes().isEmpty()) {
            throw new RuntimeException("A despesa precisa ter pelo menos um participante.");
        }
    }

    // Mesma mensagem do ParticipanteService quando não acha o item
    private void conferirExiste(String descricao) {
        if (descricao == null || buscar(descricao) == null) {
            throw new RuntimeException("Despesa não encontrada.");
        }
    }
}