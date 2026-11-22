package persistence;

import entities.Participante;
import repositories.ParticipanteRepository;

import java.nio.file.Path;
import java.util.ArrayList;

public class TesteParticipanteRepository {

    public static void main(String[] args) {

        Path caminho = Path.of("dados/viagem.dat");

        ArquivoPersistence<DadosSalvos> persistencia = new ArquivoPersistence<>(caminho, DadosSalvos.class);

        DadosSalvos dados;

        try {
            dados = persistencia.carregar();
        } catch (ArquivoNaoEncontradoException e) {
            dados = new DadosSalvos(
                    new ArrayList<>(),
                    new ArrayList<>()
            );
        }

        ParticipanteRepository repositorio =
                new ParticipanteRepository(dados, persistencia);

        Participante encontrado = repositorio.buscarPorCodigo(4);

        repositorio.remover(4);


        System.out.println("Participantes cadastrados:");

        for (Participante p : repositorio.listarTodos()) {
            System.out.println(p);
        }
    }
}