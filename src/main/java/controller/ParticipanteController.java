package controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

import entities.Participante;
import service.ParticipanteService;
import view.ParticipantesPanel;

/**
 * Controller responsável pela gestão de participantes.
 * Código é gerado automaticamente, o usuário só preenche nome e orçamento.
 */
public class ParticipanteController {

    private final ParticipanteService participanteService;
    private final ParticipantesPanel view;
    private ResumoController resumoController;
    private DespesaController despesaController;

    public ParticipanteController(ParticipanteService participanteService, ParticipantesPanel view) {
        this.participanteService = participanteService;
        this.view = view;

        // Atualiza a tabela ao iniciar
        atualizarTabela();

        // Conecta os botões da view
        view.onCadastrar(this::acaoCadastrar);
        view.onAlterar(this::acaoAlterar);
        view.onExcluir(this::acaoExcluir);
    }

    /**
     * Define o controller de resumo para atualização automática
     */
    public void setResumoController(ResumoController resumoController) {
        this.resumoController = resumoController;
    }

    /**
     * Define o controller de despesas para sincronização
     */
    public void setDespesaController(DespesaController despesaController) {
        this.despesaController = despesaController;
    }

    /**
     * Obtém o serviço de participantes (necessário para o DespesaController)
     */
    public ParticipanteService getParticipanteService() {
        return participanteService;
    }

    /**
     * Converte reais para centavos (ex: "100,50" -> 10050)
     */
    private int converterParaCentavos(String valor) {
        return util.Moeda.paraCentavos(valor);
    }

    /**
     * Gera o próximo código automaticamente para o participante
     */
    private int gerarProximoCodigo() {
        List<Participante> participantes = participanteService.listar();
        if (participantes.isEmpty()) {
            return 1;
        }
        return participantes.stream()
                .mapToInt(Participante::getCodigo)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Ação do botão Adicionar
     */
    private void acaoCadastrar(ActionEvent e) {
        try {
            String nome = view.getNome();
            String orcamentoStr = view.getOrcamento();

            // Validação básica de campos preenchidos
            if (nome.isEmpty() || orcamentoStr.isEmpty()) {
                view.mostrarErro("Nome e orçamento são obrigatórios.");
                return;
            }

            // Gera código automaticamente
            int codigo = gerarProximoCodigo();
            int orcamentoCent = converterParaCentavos(orcamentoStr);

            participanteService.cadastrar(codigo, nome, orcamentoCent);
            atualizarTabela();
            view.limparFormulario();

            // Atualiza a lista de despesas com o novo participante
            if (despesaController != null) {
                despesaController.refreshParticipantes();
                // pergunta em quais despesas a pessoa nova entra
                despesaController.oferecerIncluirNasDespesas(participanteService.buscar(codigo));
            }

            // Atualiza o resumo automaticamente
            if (resumoController != null) {
                resumoController.atualizarResumo(new ActionEvent(this, 0, "PARTICIPANTE_ADICIONADO"));
            }

        } catch (NumberFormatException ex) {
            view.mostrarErro("Orçamento deve ser um número válido.");
        } catch (RuntimeException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    /**
     * Ação do botão Alterar
     */
    private void acaoAlterar(ActionEvent e) {
        try {
            String nome = view.getNome();
            String orcamentoStr = view.getOrcamento();

            if (nome.isEmpty() || orcamentoStr.isEmpty()) {
                view.mostrarErro("Nome e orçamento são obrigatórios.");
                return;
            }

            // Busca o participante selecionado na tabela
            int linha = getLinhaSelecionada();
            if (linha < 0) {
                view.mostrarErro("Selecione um participante da tabela.");
                return;
            }

            // Obtém o código do participante selecionado
            int codigo = (int) view.getTabelaModel().getValueAt(linha, 0);
            int orcamentoCent = converterParaCentavos(orcamentoStr);

            participanteService.alterar(codigo, nome, orcamentoCent);
            atualizarTabela();
            view.limparFormulario();

            // Nome/orçamento novo também aparece na aba Despesas
            if (despesaController != null) {
                despesaController.refreshParticipantes();
                despesaController.atualizarTabela();
            }

            // Atualiza o resumo automaticamente
            if (resumoController != null) {
                resumoController.atualizarResumo(new ActionEvent(this, 0, "PARTICIPANTE_ALTERADO"));
            }

        } catch (NumberFormatException ex) {
            view.mostrarErro("Orçamento deve ser um número válido.");
        } catch (RuntimeException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    /**
     * Ação do botão Excluir
     */
    private void acaoExcluir(ActionEvent e) {
        try {
            // Busca o participante selecionado na tabela
            int linha = getLinhaSelecionada();
            if (linha < 0) {
                view.mostrarErro("Selecione um participante da tabela.");
                return;
            }

            // Obtém o código do participante selecionado
            int codigo = (int) view.getTabelaModel().getValueAt(linha, 0);

            participanteService.remover(codigo);
            atualizarTabela();
            view.limparFormulario();
            if (despesaController != null) {
                despesaController.refreshParticipantes();
            }

            // Atualiza o resumo automaticamente
            if (resumoController != null) {
                resumoController.atualizarResumo(new ActionEvent(this, 0, "PARTICIPANTE_REMOVIDO"));
            }

        } catch (RuntimeException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    /**
     * Obtém a linha selecionada na tabela (retorna -1 se nenhuma)
     */
    private int getLinhaSelecionada() {
        return view.getLinhaSelecionada();
    }

    /**
     * Atualiza a tabela da view com a lista de participantes
     */
    public void atualizarTabela() {
        List<Participante> participantes = participanteService.listar();
        List<Object[]> linhas = participantes.stream()
                .map(p -> new Object[]{
                        p.getCodigo(), // Código escondido
                        p.getNome(),
                        util.Moeda.formatar(p.getOrcamentoCent())
                })
                .collect(Collectors.toList());
        view.setLinhas(linhas);
    }

    /**
     * Atualiza os participantes carregados de um arquivo
     */
    public void atualizarParticipantes(List<Participante> novosParticipantes) {
        // Atualiza a tabela sem limpar formulário
        List<Object[]> linhas = novosParticipantes.stream()
                .map(p -> new Object[]{
                        p.getCodigo(),
                        p.getNome(),
                        util.Moeda.formatar(p.getOrcamentoCent())
                })
                .collect(Collectors.toList());
        view.setLinhas(linhas);
    }
}