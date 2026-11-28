package controller;

import java.util.List;
import java.util.stream.Collectors;

import entities.Participante;
import service.ParticipanteService;
import util.Moeda;
import view.ParticipantesPanel;

/**
 * Controller responsável pela gestão de participantes.
 * Lê o formulário, manda pro service e mostra o erro se o service recusar.
 */
public class ParticipanteController {

    private final ParticipanteService participanteService;
    private final ParticipantesPanel view;
    // Avisa o MainController pra atualizar as outras abas depois de uma mudança
    private final Runnable aoMudar;

    public ParticipanteController(ParticipanteService participanteService, ParticipantesPanel view, Runnable aoMudar) {
        this.participanteService = participanteService;
        this.view = view;
        this.aoMudar = aoMudar;

        // Conecta os botões da view
        view.onCadastrar(e -> cadastrar());
        view.onAlterar(e -> alterar());
        view.onExcluir(e -> excluir());
    }

    // Se o código ficar vazio, usa o próximo livre
    private int lerCodigo() {
        if (view.getCodigo().isEmpty()) {
            return participanteService.listar().stream().mapToInt(Participante::getCodigo).max().orElse(0) + 1;
        }
        return Integer.parseInt(view.getCodigo());
    }

    private void cadastrar() {
        try {
            if (view.getNome().isEmpty() || view.getOrcamento().isEmpty()) {
                view.mostrarErro("Nome e orçamento são obrigatórios.");
                return;
            }
            participanteService.cadastrar(lerCodigo(), view.getNome(), Moeda.paraCentavos(view.getOrcamento()));
            aoMudar.run();
        } catch (NumberFormatException ex) {
            view.mostrarErro("Código e orçamento devem ser números (ex.: 70,00).");
        } catch (RuntimeException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    // O código do participante selecionado já está no formulário (o campo fica travado na edição)
    private void alterar() {
        try {
            if (view.getNome().isEmpty() || view.getOrcamento().isEmpty()) {
                view.mostrarErro("Nome e orçamento são obrigatórios.");
                return;
            }
            participanteService.alterar(Integer.parseInt(view.getCodigo()), view.getNome(), Moeda.paraCentavos(view.getOrcamento()));
            aoMudar.run();
        } catch (NumberFormatException ex) {
            view.mostrarErro("Orçamento deve ser um número (ex.: 70,00).");
        } catch (RuntimeException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    private void excluir() {
        try {
            participanteService.remover(Integer.parseInt(view.getCodigo()));
            aoMudar.run();
        } catch (NumberFormatException ex) {
            view.mostrarErro("Selecione um participante da tabela.");
        } catch (RuntimeException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    /**
     * Atualiza a tabela da view com a lista de participantes
     */
    public void atualizarTabela() {
        List<Object[]> linhas = participanteService.listar().stream()
                .map(p -> new Object[]{p.getCodigo(), p.getNome(), Moeda.formatar(p.getOrcamentoCent())})
                .collect(Collectors.toList());
        view.setLinhas(linhas);
    }
}
