package persistence;

import entities.Despesa;
import entities.Participante;

import java.nio.file.Path;


public class TesteCarregar {

    public static void main(String[] args) throws ArquivoNaoEncontradoException {

        Path caminho = Path.of("dados/viagem.dat");

        ArquivoPersistence<DadosSalvos> persistencia =
                new ArquivoPersistence<>(caminho, DadosSalvos.class);

        DadosSalvos dados = persistencia.carregar();

        System.out.println("Dados recuperados com sucesso!");

        System.out.println("\n--- PARTICIPANTES ---");

        for (Participante participante : dados.getParticipantes()) {
            System.out.println(participante);
        }

        System.out.println("\n--- DESPESAS ---");

        for (Despesa despesa : dados.getDespesas()) {
            System.out.println(despesa.getDescricao());

            System.out.println("Total: R$ " +
                    despesa.calcTotalCent(despesa.getParticipantes().size()) / 100.0);
        }

    }
}