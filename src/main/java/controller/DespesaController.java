package controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

import entities.Despesa;
import entities.DespesaFixa;
import entities.DespesaPorPessoa;
import entities.Participante;
import service.DespesaService;
import util.Moeda;
import view.DespesasPanel;

/**
 * Controller responsável pela gestão de despesas.
 */
public class DespesaController {

    private final DespesaService despesaService;
    private final DespesaPorPessoaController despesaPorPessoaController;
    private final DespesaFixaController despesaFixaController;
    private final DespesasPanel view;
    private ParticipanteController participanteController;
    private ResumoController resumoController;

    // Lista de participantes - sempre atualizada pelo MainController
    private List<Participante> participantes;

    public DespesaController(DespesaService despesaService,
                            DespesaPorPessoaController despesaPorPessoaController,
                            DespesaFixaController despesaFixaController,
                            DespesasPanel view) {
        this.despesaService = despesaService;
        this.despesaPorPessoaController = despesaPorPessoaController;
        this.despesaFixaController = despesaFixaController;
        this.view = view;

        // Atualiza a tabela ao iniciar
        atualizarTabela();

        // Conecta os botões da view
        view.onIncluir(this::acaoIncluir);
        view.onEditar(this::acaoEditar);
        view.onExcluir(this::acaoExcluir);
    }

    public void setParticipanteController(ParticipanteController participanteController) {
        this.participanteController = participanteController;
    }

    public void setResumoController(ResumoController resumoController) {
        this.resumoController = resumoController;
    }

    // Texto que aparece na lista e na tabela: "Ana (R$ 70,00)"
    private static String rotulo(Participante p) {
        return p.getNome() + " (" + Moeda.formatar(p.getOrcamentoCent()) + ")";
    }

    // "Fixa" ou "Por pessoa" - igual ao texto do combo da tela
    private static String tipo(Despesa d) {
        return d instanceof DespesaFixa ? "Fixa" : "Por pessoa";
    }

    /**
     * Atualiza a lista de participantes (chamado pelo aplicativo principal)
     */
    public void setParticipantes(List<Participante> participantes) {
        this.participantes = participantes;
        view.setParticipantes(participantes == null ? List.of()
                : participantes.stream().map(DespesaController::rotulo).collect(Collectors.toList()));
    }

    /**
     * Força atualização da lista de participantes (ex: depois de cadastrar alguém)
     */
    public void refreshParticipantes() {
        if (participanteController != null) {
            setParticipantes(participanteController.getParticipanteService().listar());
        }
    }

    public void adicionarParticipante(String descricaoDespesa, Participante participante) {
        try {
            despesaService.adicionarParticipante(descricaoDespesa, participante);
            atualizarTabela();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(view, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void removerParticipante(String descricaoDespesa, Participante participante) {
        try {
            despesaService.removerParticipante(descricaoDespesa, participante);
            atualizarTabela();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(view, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Lê o formulário e monta a despesa. Se faltar alguma coisa mostra o erro e devolve null
    // (usado no incluir e no editar, antes era o mesmo código copiado nos dois)
    private Despesa montarDespesaDoFormulario() {
        String descricao = view.getDescricao();
        String valorStr = view.getValor();

        if (descricao.isEmpty()) {
            view.mostrarErro("Descrição é obrigatória.");
            return null;
        }
        if (valorStr.isEmpty()) {
            view.mostrarErro("Valor é obrigatório.");
            return null;
        }
        // Pega a lista mais nova (pode ter cadastrado alguém agora na aba Participantes)
        refreshParticipantesSilencioso();
        if (participantes == null || participantes.isEmpty()) {
            view.mostrarErro("Nenhum participante cadastrado.");
            return null;
        }

        int valorCent;
        try {
            valorCent = Moeda.paraCentavos(valorStr);
        } catch (NumberFormatException ex) {
            view.mostrarErro("Valor deve ser um número válido (ex.: 25,50).");
            return null;
        }

        List<String> nomesSelecionados = view.getParticipantesSelecionados().stream()
                .map(sel -> sel.substring(0, Math.max(0, sel.lastIndexOf(" ("))).trim())
                .collect(Collectors.toList());
        List<Participante> selecionados = participantes.stream()
                .filter(p -> nomesSelecionados.contains(p.getNome()))
                .collect(Collectors.toList());

        if (selecionados.isEmpty()) {
            view.mostrarErro("Selecione pelo menos um participante.");
            return null;
        }

        Despesa despesa = "Fixa".equals(view.getTipo())
                ? new DespesaFixa(descricao, valorCent)
                : new DespesaPorPessoa(descricao, valorCent);
        despesa.definirParticipantes(selecionados);
        return despesa;
    }

    private void refreshParticipantesSilencioso() {
        if (participanteController != null) {
            this.participantes = participanteController.getParticipanteService().listar();
        }
    }

    private void acaoIncluir(ActionEvent e) {
        try {
            Despesa despesa = montarDespesaDoFormulario();
            if (despesa == null) return;

            // se já existe (ex: Transporte) pergunta se quer atualizar em vez de dar erro
            Despesa existente = despesaService.listar().stream()
                    .filter(d -> d.getDescricao().equalsIgnoreCase(despesa.getDescricao()))
                    .findFirst().orElse(null);
            if (existente != null) {
                if (!view.confirmar("Já existe a despesa \"" + existente.getDescricao() + "\".\n"
                        + "Atualizar ela com as " + despesa.getParticipantes().size()
                        + " pessoas marcadas e redividir o valor?")) {
                    return;
                }
                despesaService.alterar(existente.getDescricao(), despesa);
            } else {
                despesaService.cadastrar(despesa);
            }
            atualizarTabela();
            atualizarResumo();
        } catch (RuntimeException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    private void acaoEditar(ActionEvent e) {
        try {
            int linhaSelecionada = view.getLinhaSelecionada();
            if (linhaSelecionada < 0) {
                view.mostrarErro("Selecione uma despesa para alterar.");
                return;
            }
            String descricaoAtual = (String) view.getValorCelda(linhaSelecionada, 0);

            Despesa despesa = montarDespesaDoFormulario();
            if (despesa == null) return;

            despesaService.alterar(descricaoAtual, despesa);
            atualizarTabela();
            atualizarResumo();
        } catch (RuntimeException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    private void acaoExcluir(ActionEvent e) {
        try {
            int linhaSelecionada = view.getLinhaSelecionada();
            if (linhaSelecionada < 0) {
                view.mostrarErro("Selecione uma despesa para excluir.");
                return;
            }

            String descricao = (String) view.getValorCelda(linhaSelecionada, 0);
            despesaService.remover(descricao);
            atualizarTabela();
            view.limparFormulario();
            atualizarResumo();
        } catch (RuntimeException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    // Quando entra alguém novo no grupo pergunta em quais despesas ele vai participar.
    // Na fixa o valor é dividido de novo entre todo mundo
    public void oferecerIncluirNasDespesas(Participante novo) {
        List<Despesa> despesas = despesaService.listar();
        if (novo == null || despesas.isEmpty()) return;

        List<String> opcoes = despesas.stream()
                .map(d -> d.getDescricao() + " (" + tipo(d) + ", " + d.getParticipantes().size() + " pessoas)")
                .collect(Collectors.toList());
        List<Integer> escolhidas = view.escolherDespesas(
                novo.getNome() + " entrou no grupo. Incluir em quais despesas?\n"
                        + "Nas despesas fixas o valor é redividido entre todos.", opcoes);
        for (int i : escolhidas) {
            despesaService.adicionarParticipante(despesas.get(i).getDescricao(), novo);
        }
        if (!escolhidas.isEmpty()) {
            atualizarTabela();
            atualizarResumo();
        }
    }

    // texto da coluna "Cada um paga". Se a divisão não for exata mostra de quanto a quanto
    private static String cadaUmPaga(Despesa d) {
        if (d.getParticipantes().isEmpty()) return "-";
        java.util.IntSummaryStatistics st = d.calcularRateio().values().stream()
                .mapToInt(Integer::intValue).summaryStatistics();
        return st.getMin() == st.getMax()
                ? Moeda.formatar(st.getMin()) + " cada"
                : Moeda.formatar(st.getMin()) + " a " + Moeda.formatar(st.getMax());
    }

    private void atualizarResumo() {
        if (resumoController != null) {
            resumoController.atualizarResumo(null);
        }
    }

    /**
     * Atualiza a tabela da view com a lista de despesas
     */
    public void atualizarTabela() {
        List<Object[]> linhas = despesaService.listar().stream()
                .map(d -> new Object[]{
                        d.getDescricao(),
                        tipo(d),
                        Moeda.formatar(d instanceof DespesaFixa
                                ? ((DespesaFixa) d).getValorTotalCent()
                                : ((DespesaPorPessoa) d).getValorUnitarioCent()),
                        cadaUmPaga(d),
                        d.getParticipantes().stream()
                                .map(DespesaController::rotulo)
                                .collect(Collectors.joining(", "))
                })
                .collect(Collectors.toList());
        view.setLinhas(linhas);
    }
}
