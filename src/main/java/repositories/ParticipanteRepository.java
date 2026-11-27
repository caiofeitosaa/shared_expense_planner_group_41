package repositories;

import entities.Participante;
import persistence.Persistencia;
import persistence.DadosSalvos;

import java.util.List;
import java.util.ArrayList;

public class ParticipanteRepository {


    private final DadosSalvos dados;
    private final Persistencia<DadosSalvos> persistencia;

    public ParticipanteRepository(
            DadosSalvos dados,
            Persistencia<DadosSalvos> persistencia) {

        this.dados = dados;
        this.persistencia = persistencia;
    }

    // Cadastrar participante
    public void salvar(Participante participante) {

        if (participante == null) {
            throw new IllegalArgumentException("Participante inválido.");
        }

        if (buscarPorCodigo(participante.getCodigo()) != null) {
            throw new IllegalArgumentException(
                    "Já existe um participante com esse código.");
        }

        List<Participante> lista =
                new ArrayList<>(dados.getParticipantes());

        lista.add(participante);

        dados.definirParticipantes(lista);

        persistencia.salvar(dados);
    }

    // Buscar participante pelo código
    public Participante buscarPorCodigo(int codigo) {

        for (Participante participante : dados.getParticipantes()) {

            if (participante.getCodigo() == codigo) {
                return participante;
            }
        }

        return null;
    }

    // Listar todos os participantes
    public List<Participante> listarTodos() {
        return dados.getParticipantes();
    }

    // Atualizar participante
    public void atualizar(Participante participante) {

        if (participante == null) {
            throw new IllegalArgumentException("Participante inválido.");
        }

        List<Participante> lista =
                new ArrayList<>(dados.getParticipantes());

        boolean encontrado = false;

        for (int i = 0; i < lista.size(); i++) {

            if (lista.get(i).getCodigo() == participante.getCodigo()) {
                lista.set(i, participante);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            throw new IllegalArgumentException(
                    "Participante não encontrado.");
        }

        dados.definirParticipantes(lista);

        persistencia.salvar(dados);
    }

    // Remover participante
    public void remover(int codigo) {

        List<Participante> lista =
                new ArrayList<>(dados.getParticipantes());

        boolean removido = lista.removeIf(
                participante -> participante.getCodigo() == codigo
        );

        if (!removido) {
            throw new IllegalArgumentException(
                    "Participante não encontrado.");
        }

        dados.definirParticipantes(lista);

        persistencia.salvar(dados);
    }
}
