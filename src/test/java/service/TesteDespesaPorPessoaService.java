package service;

import entities.Despesa;
import entities.Participante;
import persistence.ArquivoPersistence;
import persistence.DadosSalvos;
import repositories.DespesaRepository;
import repositories.ParticipanteRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Teste simples com main (o projeto não tem JUnit). Rodar com: java -ea
public class TesteDespesaPorPessoaService {

    public static void main(String[] args) throws Exception {
        // Arquivo temporário pra não mexer nos dados de verdade
        Path temp = Files.createTempDirectory("teste_por_pessoa").resolve("dados.dat");
        ArquivoPersistence<DadosSalvos> persistencia = new ArquivoPersistence<>(temp, DadosSalvos.class);
        DadosSalvos dados = new DadosSalvos(new ArrayList<>(), new ArrayList<>());
        DespesaRepository despesas = new DespesaRepository(dados, persistencia);
        ParticipanteService participantes = new ParticipanteService(new ParticipanteRepository(dados, persistencia), despesas);
        DespesaPorPessoaService service = new DespesaPorPessoaService(despesas);

        participantes.cadastrar(1, "Ana", 7000);
        participantes.cadastrar(2, "Bruno", 6000);
        participantes.cadastrar(3, "Carla", 7000);
        Participante ana = participantes.buscar(1), bruno = participantes.buscar(2), carla = participantes.buscar(3);

        // Almoço do caso principal: R$ 20,00 pra cada um, R$ 60,00 no total
        service.cadastrar("Almoço", 2000, List.of(ana, bruno, carla));
        Despesa almoco = despesas.buscarPorDescricao("Almoço");
        assert almoco.calcularRateio().get(bruno) == 2000;
        assert almoco.calcTotalCent(3) == 6000;

        // Entradas inválidas são recusadas sem mudar nada
        recusa(() -> service.cadastrar("Sem ninguém", 1000, List.of()));
        recusa(() -> service.cadastrar("Negativa", -100, List.of(ana)));
        recusa(() -> service.cadastrar("", 1000, List.of(ana)));
        recusa(() -> service.cadastrar("Repetido", 1000, List.of(ana, ana)));
        recusa(() -> service.cadastrar("Almoço", 1000, List.of(ana)));
        assert despesas.listarTodas().size() == 1;

        // Alterar: Bruno sai da sobremesa e ela passa a custar zero pra ele
        service.alterar("Almoço", "Sobremesa", 1000, List.of(ana, carla));
        Despesa sobremesa = despesas.buscarPorDescricao("Sobremesa");
        assert sobremesa.calcularRateio().get(ana) == 1000 && sobremesa.calcularRateio().get(bruno) == null;
        recusa(() -> service.alterar("Não existe", "X", 1000, List.of(ana)));

        // Remover
        recusa(() -> service.remover("Não existe"));
        service.remover("Sobremesa");
        assert despesas.listarTodas().isEmpty();

        System.out.println("TesteDespesaPorPessoaService: tudo certo");
    }

    private static void recusa(Runnable acao) {
        try {
            acao.run();
        } catch (IllegalArgumentException e) {
            return;
        }
        throw new AssertionError("era pra ter sido recusado");
    }
}
