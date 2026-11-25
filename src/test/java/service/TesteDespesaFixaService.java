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
import java.util.Map;

// Teste simples com main (o projeto não tem JUnit). Rodar com: java -ea
public class TesteDespesaFixaService {

    public static void main(String[] args) throws Exception {
        // Arquivo temporário pra não mexer nos dados de verdade
        Path temp = Files.createTempDirectory("teste_fixa").resolve("dados.dat");
        ArquivoPersistence<DadosSalvos> persistencia = new ArquivoPersistence<>(temp, DadosSalvos.class);
        DadosSalvos dados = new DadosSalvos(new ArrayList<>(), new ArrayList<>());
        ParticipanteService participantes = new ParticipanteService(new ParticipanteRepository(dados, persistencia), new DespesaRepository(dados, persistencia));
        DespesaRepository despesas = new DespesaRepository(dados, persistencia);
        DespesaFixaService service = new DespesaFixaService(despesas);

        participantes.cadastrar(1, "Ana", 7000);
        participantes.cadastrar(2, "Bruno", 6000);
        participantes.cadastrar(3, "Carla", 7000);
        Participante ana = participantes.buscar(1), bruno = participantes.buscar(2), carla = participantes.buscar(3);

        // Centavos no rateio: mesmo escolhendo fora de ordem, o centavo extra vai pro menor código
        service.cadastrar("Teste centavos", 10000, List.of(carla, ana, bruno));
        Map<Participante, Integer> rateio = despesas.buscarPorDescricao("Teste centavos").calcularRateio();
        assert rateio.get(ana) == 3334 && rateio.get(bruno) == 3333 && rateio.get(carla) == 3333;

        // Entradas inválidas são recusadas sem mudar nada
        recusa(() -> service.cadastrar("Sem ninguém", 1000, List.of()));
        recusa(() -> service.cadastrar("Negativa", -100, List.of(ana)));
        recusa(() -> service.cadastrar("Zero", 0, List.of(ana)));
        recusa(() -> service.cadastrar("", 1000, List.of(ana)));
        recusa(() -> service.cadastrar("Repetido", 1000, List.of(ana, ana)));
        recusa(() -> service.cadastrar("Teste centavos", 1000, List.of(ana)));
        assert despesas.listarTodas().size() == 1;

        // Alterar troca valor e participantes; alterar algo que não existe é recusado
        service.alterar("Teste centavos", "Atividade recreativa", 3000, List.of(ana, carla));
        Despesa atividade = despesas.buscarPorDescricao("Atividade recreativa");
        assert atividade.calcularRateio().get(ana) == 1500 && atividade.calcularRateio().get(bruno) == null;
        recusa(() -> service.alterar("Não existe", "X", 1000, List.of(ana)));
        recusa(() -> service.alterar("Atividade recreativa", "Atividade recreativa", -1, List.of(ana)));
        assert despesas.buscarPorDescricao("Atividade recreativa").calcTotalCent(2) == 3000;

        // Remover
        recusa(() -> service.remover("Não existe"));
        service.remover("Atividade recreativa");
        assert despesas.listarTodas().isEmpty();

        System.out.println("TesteDespesaFixaService: tudo certo");
    }

    private static void recusa(Runnable acao) {
        try {
            acao.run();
        } catch (IllegalArgumentException e) {
            System.out.println("Recusado: " + e.getMessage());
            return;
        }
        throw new AssertionError("era pra ter sido recusado");
    }
}
