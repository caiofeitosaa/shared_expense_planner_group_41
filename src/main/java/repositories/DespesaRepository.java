package repositories;

import entities.Despesa;
import persistence.Persistencia;
import persistence.DadosSalvos;

import java.util.ArrayList;
import java.util.List;

public class DespesaRepository {

    private final DadosSalvos dados;
    private final Persistencia<DadosSalvos> persistencia;

    public DespesaRepository(
            DadosSalvos dados,
            Persistencia<DadosSalvos> persistencia) {

        this.dados = dados;
        this.persistencia = persistencia;
    }

    // Validar descrição
    private void validarDescricao(String descricao) {

        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "A descrição da despesa é inválida.");
        }
    }

    // Cadastrar despesa
    public void salvar(Despesa despesa) {

        if (despesa == null) {
            throw new IllegalArgumentException("Despesa inválida.");
        }

        validarDescricao(despesa.getDescricao());

        if (buscarPorDescricao(despesa.getDescricao()) != null) {
            throw new IllegalArgumentException(
                    "Já existe uma despesa com essa descrição.");
        }

        List<Despesa> lista =
                new ArrayList<>(dados.getDespesas());

        lista.add(despesa);

        dados.definirDespesas(lista);

        persistencia.salvar(dados);
    }

    // Buscar despesa pela descrição
    public Despesa buscarPorDescricao(String descricao) {

        validarDescricao(descricao);

        for (Despesa despesa : dados.getDespesas()) {

            if (despesa.getDescricao().equals(descricao)) {
                return despesa;
            }
        }

        return null;
    }

    // Listar todas as despesas
    public List<Despesa> listarTodas() {
        return dados.getDespesas();
    }

    // Atualizar despesa
    public void atualizar(
            String descricaoAtual,
            Despesa despesaAtualizada) {

        validarDescricao(descricaoAtual);

        if (despesaAtualizada == null) {
            throw new IllegalArgumentException("Despesa inválida.");
        }

        validarDescricao(despesaAtualizada.getDescricao());

        List<Despesa> lista =
                new ArrayList<>(dados.getDespesas());

        int indice = -1;

        // Localizar a despesa que será atualizada
        for (int i = 0; i < lista.size(); i++) {

            if (lista.get(i).getDescricao().equals(descricaoAtual)) {
                indice = i;
                break;
            }
        }

        if (indice == -1) {
            throw new IllegalArgumentException(
                    "Despesa não encontrada.");
        }

        // Verificar se a nova descrição já pertence a outra despesa
        for (int i = 0; i < lista.size(); i++) {

            if (i != indice &&
                    lista.get(i).getDescricao().equals(
                            despesaAtualizada.getDescricao())) {

                throw new IllegalArgumentException(
                        "Já existe outra despesa com essa descrição.");
            }
        }

        lista.set(indice, despesaAtualizada);

        dados.definirDespesas(lista);

        persistencia.salvar(dados);
    }

    // Remover despesa
    public void remover(String descricao) {

        validarDescricao(descricao);

        List<Despesa> lista =
                new ArrayList<>(dados.getDespesas());

        boolean removida = lista.removeIf(
                despesa -> despesa.getDescricao().equals(descricao)
        );

        if (!removida) {
            throw new IllegalArgumentException(
                    "Despesa não encontrada.");
        }

        dados.definirDespesas(lista);

        persistencia.salvar(dados);
    }
}