package service;

import entities.DespesaFixa;
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
public class TesteParticipanteService {

    public static void main(String[] args) throws Exception {
        // Arquivo temporário pra não mexer nos dados de verdade
        Path temp = Files.createTempDirectory("teste_service").resolve("dados.dat");
        ArquivoPersistence<DadosSalvos> persistencia = new ArquivoPersistence<>(temp, DadosSalvos.class);
        DadosSalvos dados = new DadosSalvos(new ArrayList<>(), new ArrayList<>());
        ParticipanteRepository participantes = new ParticipanteRepository(dados, persistencia);
        DespesaRepository despesas = new DespesaRepository(dados, persistencia);
        ParticipanteService service = new ParticipanteService(participantes, despesas);

        service.cadastrar(1, "Ana", 7000);
        service.cadastrar(2, "Bruno", 6000);
        assert service.listar().size() == 2;

        // código repetido e dados inválidos precisam ser recusados sem mudar nada
        recusa(() -> service.cadastrar(1, "Outra", 100));
        recusa(() -> service.cadastrar(3, "", 100));
        recusa(() -> service.cadastrar(3, "Carla", -1));
        assert service.listar().size() == 2;

        // alterar com orçamento inválido não pode trocar o nome
        recusa(() -> service.alterar(1, "Ana Maria", -5));
        assert service.buscar(1).getNome().equals("Ana");

        // o objeto alterado é o mesmo que a despesa enxerga
        Participante ana = service.buscar(1);
        DespesaFixa transporte = new DespesaFixa("Transporte", 9000);
        transporte.definirParticipantes(List.of(ana));
        despesas.salvar(transporte);
        service.alterar(1, "Ana Maria", 8000);
        assert transporte.getParticipantes().get(0).getNome().equals("Ana Maria");
        assert transporte.getParticipantes().get(0).getOrcamentoCent() == 8000;

        // quem está numa despesa não pode ser excluído, quem não está pode
        recusa(() -> service.remover(1));
        service.remover(2);
        assert service.listar().size() == 1;

        System.out.println("TesteParticipanteService: tudo certo");
    }

    private static void recusa(Runnable acao) {
        try {
            acao.run();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return;
        }
        throw new AssertionError("era pra ter sido recusado");
    }
}
