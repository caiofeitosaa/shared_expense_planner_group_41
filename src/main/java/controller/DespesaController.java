package controller;

import java.util.List;
import java.util.stream.Collectors;

import entities.Despesa;
import entities.DespesaFixa;
import entities.DespesaPorPessoa;
import entities.Participante;
import service.DespesaService;
import service.ParticipanteService;
import util.Moeda;
import view.DespesasPanel;

/**
 * Controller responsável pela gestão de despesas.
 * Lê o formulário e manda pro controller do tipo escolhido (Fixa ou Por pessoa).
 */
public class DespesaController {

    private final DespesaService despesaService;
    private final ParticipanteService participanteService;
    private final DespesaFixaController despesaFixaController;
    private final DespesaPorPessoaController despesaPorPessoaController;
    private final DespesasPanel view;
    // Avisa o MainController pra atualizar as outras abas depois de uma mudança
    private final Runnable aoMudar;

    public DespesaController(DespesaService despesaService,
                             ParticipanteService participanteService,
                             DespesaFixaController despesaFixaController,
                             DespesaPorPessoaController despesaPorPessoaController,
                             DespesasPanel view,
                             Runnable aoMudar) {
        this.despesaService = despesaService;
        this.participanteService = participanteService;
        this.despesaFixaController = despesaFixaController;
        this.despesaPorPessoaController = despesaPorPessoaController;
        this.view = view;
        this.aoMudar = aoMudar;

        // Conecta os botões da view
        view.onIncluir(e -> salvar(null));
        view.onEditar(e -> salvar(descricaoSelecionada()));
        view.onExcluir(e -> excluir());
    }

    // "Fixa" ou "Por pessoa" - igual ao texto do combo da tela
    private static String tipo(Despesa d) {
        return d instanceof DespesaFixa ? "Fixa" : "Por pessoa";
    }

    // Valor que o usuário digitou: total na fixa, unitário na por pessoa
    private static int valorInformado(Despesa d) {
        return d instanceof DespesaFixa ? ((DespesaFixa) d).getValorTotalCent() : ((DespesaPorPessoa) d).getValorUnitarioCent();
    }

    // A tabela tem a mesma ordem da lista do service, então a linha diz qual despesa é
    private String descricaoSelecionada() {
        int linha = view.getLinhaSelecionada();
        return linha < 0 ? null : despesaService.listar().get(linha).getDescricao();
    }

    // A lista da tela mostra "1 - Ana" (toString do Participante). Pega o código antes do " - "
    private List<Participante> participantesSelecionados() {
        return view.getParticipantesSelecionados().stream()
                .map(texto -> participanteService.buscar(Integer.parseInt(texto.substring(0, texto.indexOf(" - ")))))
                .collect(Collectors.toList());
    }

    // Serve pro Incluir (descricaoAtual null) e pro Editar (descrição da linha selecionada)
    private void salvar(String descricaoAtual) {
        try {
            if (view.getValor().isEmpty()) {
                view.mostrarErro("Valor é obrigatório.");
                return;
            }
            int valorCent = Moeda.paraCentavos(view.getValor());
            List<Participante> participantes = participantesSelecionados();
            boolean fixa = "Fixa".equals(view.getTipo());

            if (descricaoAtual == null && fixa) {
                despesaFixaController.cadastrar(view.getDescricao(), valorCent, participantes);
            } else if (descricaoAtual == null) {
                despesaPorPessoaController.cadastrar(view.getDescricao(), valorCent, participantes);
            } else if (fixa) {
                despesaFixaController.alterar(descricaoAtual, view.getDescricao(), valorCent, participantes);
            } else {
                despesaPorPessoaController.alterar(descricaoAtual, view.getDescricao(), valorCent, participantes);
            }
            aoMudar.run();
        } catch (NumberFormatException ex) {
            view.mostrarErro("Valor deve ser um número válido (ex.: 25,50).");
        } catch (RuntimeException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    private void excluir() {
        try {
            String descricao = descricaoSelecionada();
            if (descricao == null) {
                view.mostrarErro("Selecione uma despesa para excluir.");
                return;
            }
            despesaService.remover(descricao);
            aoMudar.run();
        } catch (RuntimeException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    /**
     * Atualiza a lista de participantes e a tabela de despesas da view
     */
    public void atualizarTabela() {
        view.setParticipantes(participanteService.listar().stream().map(Participante::toString).collect(Collectors.toList()));
        // A coluna de participantes usa o mesmo texto da lista, assim clicar na linha marca as pessoas certas
        List<Object[]> linhas = despesaService.listar().stream()
                .map(d -> new Object[]{
                        d.getDescricao(),
                        tipo(d),
                        Moeda.formatar(valorInformado(d)),
                        d.getParticipantes().stream().map(Participante::toString).collect(Collectors.joining(", "))
                })
                .collect(Collectors.toList());
        view.setLinhas(linhas);
    }
}
