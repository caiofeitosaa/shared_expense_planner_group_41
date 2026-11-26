package service;

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

// Caso de referência da seção 4 do trabalho. Rodar com: java -ea
public class TesteResumoService {

    public static void main(String[] args) throws Exception {
        // Arquivo temporário pra não mexer nos dados de verdade
        Path temp = Files.createTempDirectory("teste_resumo").resolve("dados.dat");
        ArquivoPersistence<DadosSalvos> persistencia = new ArquivoPersistence<>(temp, DadosSalvos.class);
        DadosSalvos dados = new DadosSalvos(new ArrayList<>(), new ArrayList<>());
        ParticipanteRepository participanteRepo = new ParticipanteRepository(dados, persistencia);
        DespesaRepository despesaRepo = new DespesaRepository(dados, persistencia);
        ParticipanteService participantes = new ParticipanteService(participanteRepo, despesaRepo);
        DespesaFixaService fixa = new DespesaFixaService(despesaRepo);
        DespesaPorPessoaService porPessoa = new DespesaPorPessoaService(despesaRepo);
        ResumoService resumo = new ResumoService(participanteRepo, despesaRepo);

        participantes.cadastrar(1, "Ana", 7000);
        participantes.cadastrar(2, "Bruno", 6000);
        participantes.cadastrar(3, "Carla", 7000);
        Participante ana = participantes.buscar(1), bruno = participantes.buscar(2), carla = participantes.buscar(3);

        fixa.cadastrar("Transporte", 9000, List.of(ana, bruno, carla));
        porPessoa.cadastrar("Almoço", 2000, List.of(ana, bruno, carla));
        fixa.cadastrar("Atividade recreativa", 3000, List.of(ana, carla));
        porPessoa.cadastrar("Sobremesa", 1000, List.of(ana, bruno, carla));

        // Ana 75, Bruno 60, Carla 75, total 210. Ana e Carla excedem 5, Bruno no limite
        Map<Participante, Integer> totais = resumo.totalPorParticipante();
        assert totais.get(ana) == 7500 && totais.get(bruno) == 6000 && totais.get(carla) == 7500;
        assert resumo.custoTotal() == 21000;
        assert resumo.excedente(ana, totais.get(ana)) == 500;
        assert resumo.excedente(bruno, totais.get(bruno)) == 0;

        // Alteração de orçamento: Ana com R$ 80,00 continua pagando 75, agora dentro do limite
        participantes.alterar(1, "Ana", 8000);
        totais = resumo.totalPorParticipante();
        assert totais.get(ana) == 7500 && resumo.excedente(ana, totais.get(ana)) == 0;

        // Exclusão de despesa: sem a atividade recreativa, 60 por pessoa e 180 no total
        fixa.remover("Atividade recreativa");
        totais = resumo.totalPorParticipante();
        assert totais.get(ana) == 6000 && totais.get(bruno) == 6000 && totais.get(carla) == 6000;
        assert resumo.custoTotal() == 18000;

        System.out.println("TesteResumoService: tudo certo");
    }
}
