package persistence;

import entities.Despesa;
import entities.DespesaFixa;
import entities.DespesaPorPessoa;
import repositories.DespesaRepository;

import java.nio.file.Path;
import java.util.ArrayList;

public class TesteDespesaRepository {

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

        DespesaRepository repositorio = new DespesaRepository(dados, persistencia);

        // Cadastrar uma nova despesa


        repositorio.remover("Sobremesa");


        // Listar despesas
        System.out.println("--- DESPESAS CADASTRADAS ---");

        for (Despesa d : repositorio.listarTodas()) {
            System.out.println(d.getDescricao());
        }

    }
}