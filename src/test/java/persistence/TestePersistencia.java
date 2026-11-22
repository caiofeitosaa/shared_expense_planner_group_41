package persistence;

import java.nio.file.Path;

public class TestePersistencia {

    public static void main(String[] args) {
        ArquivoPersistence<DadosSalvos> persistencia =
                new ArquivoPersistence<>(Path.of("dados/teste.dat"), DadosSalvos.class);

        try {
            DadosSalvos dados = persistencia.carregar();
            System.out.println("Carregou: " + dados.getParticipantes().size() + " participantes");
        } catch (ArquivoNaoEncontradoException e) {
            System.out.println("Primeira execução: " + e.getMessage());
        } catch (PersistenciaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
