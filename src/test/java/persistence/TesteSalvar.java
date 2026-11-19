package persistence;


import entities.Participante;
import entities.Despesa;
import entities.DespesaFixa;
import entities.DespesaPorPessoa;
import java.nio.file.Path;
import java.util.List;

public class TesteSalvar {

    public static void main(String[] args) {
        Participante ana = new Participante(1, "Ana", 7000);
        Participante bruno = new Participante(2, "Bruno", 6000);
        Participante carla = new Participante(3, "Carla", 7000);


        Despesa transporte = new DespesaFixa("Transporte", 9000);
        transporte.definirParticipantes(List.of(ana, bruno, carla));

        Despesa almoco = new DespesaPorPessoa("Almoço", 2000);
        almoco.definirParticipantes(List.of(ana, bruno, carla));

        Despesa atividade = new DespesaFixa("Atividade recreativa", 3000);
        atividade.definirParticipantes(List.of(ana, carla));

        Despesa sobremesa = new DespesaPorPessoa("Sobremesa", 1000);
        sobremesa.definirParticipantes(List.of(ana, bruno, carla));

        DadosSalvos dados = new DadosSalvos(
                List.of(ana, bruno, carla),
                List.of(transporte, almoco, atividade, sobremesa));

        Path caminho = Path.of("dados/viagem.dat");
        ArquivoPersistence<DadosSalvos> persistencia = new ArquivoPersistence<>(caminho, DadosSalvos.class);
        persistencia.salvar(dados);
    }
}