package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

// Aba de despesas. Fiz igual ao ParticipantesPanel: aqui só tem tela,
// quem valida e calcula é o controller/service.
public class DespesasPanel extends JPanel {

    private final JTextField txtDescricao = new JTextField();
    private final JTextField txtValor = new JTextField();
    // Os dois tipos que o professor pediu (DespesaFixa e DespesaPorPessoa)
    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"Fixa", "Por pessoa"});
    // JList porque dá pra selecionar várias pessoas na mesma despesa
    private final DefaultListModel<String> participantesModel = new DefaultListModel<>();
    private final JList<String> listaParticipantes = new JList<>(participantesModel);

    private final JButton btnIncluir = new JButton("Incluir");
    private final JButton btnEditar = new JButton("Salvar alteração");
    private final JButton btnExcluir = new JButton("Excluir");
    private final JButton btnLimpar = new JButton("Limpar");

    // Tabela sem edição direta, pra mudar algo tem que usar o formulário (assim não pula a validação)
    private final DefaultTableModel tabelaModel = new DefaultTableModel(new String[]{"Descrição", "Tipo", "Valor (R$)", "Cada um paga", "Participantes"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(tabelaModel);

    public DespesasPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Deixei a lista fora do GridLayout porque ele deixa todas as linhas do mesmo tamanho
        // e a lista ficava espremida
        JPanel campos = new JPanel(new GridLayout(0, 2, 6, 6));
        campos.add(new JLabel("Descrição:"));
        campos.add(txtDescricao);
        campos.add(new JLabel("Valor (R$):"));
        campos.add(txtValor);
        campos.add(new JLabel("Tipo:"));
        campos.add(cmbTipo);

        // clique marca/desmarca a pessoa, assim não precisa ficar segurando Ctrl
        listaParticipantes.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (index0 == index1 && isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                } else {
                    super.addSelectionInterval(index0, index1);
                }
            }
        });
        listaParticipantes.setVisibleRowCount(4);
        JPanel participantes = new JPanel(new BorderLayout(0, 4));
        participantes.add(new JLabel("Participantes (clique para marcar/desmarcar):"), BorderLayout.NORTH);
        participantes.add(new JScrollPane(listaParticipantes), BorderLayout.CENTER);

        JPanel form = new JPanel(new BorderLayout(0, 6));
        form.setBorder(BorderFactory.createTitledBorder("Dados da despesa"));
        form.add(campos, BorderLayout.NORTH);
        form.add(participantes, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton b : new JButton[]{btnIncluir, btnEditar, btnExcluir, btnLimpar}) botoes.add(b);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(form, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);

        JScrollPane scrollTabela = new JScrollPane(tabela);
        tabela.setFillsViewportHeight(true);
        add(scrollTabela, BorderLayout.CENTER);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Ao clicar numa linha, joga os dados dela no formulário
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                txtDescricao.setText(String.valueOf(tabelaModel.getValueAt(linha, 0)));
                cmbTipo.setSelectedItem(tabelaModel.getValueAt(linha, 1));
                txtValor.setText(String.valueOf(tabelaModel.getValueAt(linha, 2)).replace("R$", "").trim());
                selecionarParticipantes(String.valueOf(tabelaModel.getValueAt(linha, 4)));
            }
            atualizarBotoes();
        });

        // clicou fora da linha -> só tira a seleção, não apaga os campos
        MouseAdapter cliqueFora = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getSource() == tabela && tabela.rowAtPoint(e.getPoint()) >= 0) return;
                tabela.clearSelection();
            }
        };
        tabela.addMouseListener(cliqueFora);
        scrollTabela.getViewport().addMouseListener(cliqueFora);
        addMouseListener(cliqueFora);
        form.addMouseListener(cliqueFora);
        botoes.addMouseListener(cliqueFora);

        btnLimpar.addActionListener(e -> limparFormulario());
        atualizarBotoes();
    }

    // Marca na lista quem está no texto da tabela (ex: "Ana (R$ 70,00), Bruno (R$ 60,00)")
    // compara só o nome pq se mudar o orçamento o texto muda
    private void selecionarParticipantes(String texto) {
        List<String> nomes = new ArrayList<>();
        for (String parte : texto.split("\\), ")) {
            nomes.add(extrairNome(parte));
        }
        listaParticipantes.clearSelection();
        for (int i = 0; i < participantesModel.size(); i++) {
            if (nomes.contains(extrairNome(participantesModel.get(i)))) {
                listaParticipantes.getSelectionModel().addSelectionInterval(i, i);
            }
        }
    }

    private static String extrairNome(String texto) {
        int idx = texto.lastIndexOf(" (");
        return (idx >= 0 ? texto.substring(0, idx) : texto).trim();
    }

    // Incluir sempre liberado, Salvar alteração/Excluir só com linha selecionada
    private void atualizarBotoes() {
        boolean linhaSelecionada = tabela.getSelectedRow() >= 0;
        btnIncluir.setEnabled(true);
        btnEditar.setEnabled(linhaSelecionada);
        btnExcluir.setEnabled(linhaSelecionada);
    }

    // ---- Métodos que o controller vai usar ----

    // Devolvo o texto do jeito que o usuário digitou, o controller que converte pra centavos e valida
    public String getDescricao() { return txtDescricao.getText().trim(); }
    public String getValor() { return txtValor.getText().trim(); }
    public String getTipo() { return (String) cmbTipo.getSelectedItem(); }
    public List<String> getParticipantesSelecionados() { return listaParticipantes.getSelectedValuesList(); }
    // Linha clicada na tabela (-1 se não tiver nenhuma), pro controller saber qual despesa editar/excluir
    public int getLinhaSelecionada() { return tabela.getSelectedRow(); }

    public void onIncluir(ActionListener l) { btnIncluir.addActionListener(l); }
    public void onEditar(ActionListener l) { btnEditar.addActionListener(l); }
    public void onExcluir(ActionListener l) { btnExcluir.addActionListener(l); }

    // Recebe os participantes cadastrados na aba 1 pra aparecerem na lista (sem perder quem já tava marcado)
    public void setParticipantes(List<String> participantes) {
        List<String> marcados = new ArrayList<>();
        for (String s : listaParticipantes.getSelectedValuesList()) marcados.add(extrairNome(s));
        participantesModel.clear();
        participantes.forEach(participantesModel::addElement);
        for (int i = 0; i < participantesModel.size(); i++) {
            if (marcados.contains(extrairNome(participantesModel.get(i)))) {
                listaParticipantes.getSelectionModel().addSelectionInterval(i, i);
            }
        }
    }

    // Refaz a tabela toda com o que veio do model, assim a tela nunca fica diferente dos dados
    // (não limpa o formulário mais, era isso que obrigava a apertar Limpar)
    public void setLinhas(List<Object[]> linhas) {
        tabelaModel.setRowCount(0);
        linhas.forEach(tabelaModel::addRow);
        tabela.clearSelection();
        atualizarBotoes();
    }

    // Pega o valor de uma célula específica (usado para editar/excluir)
    public Object getValorCelda(int linha, int coluna) {
        if (linha >= 0 && linha < tabelaModel.getRowCount()) {
            return tabelaModel.getValueAt(linha, coluna);
        }
        return null;
    }

    public void limparFormulario() {
        txtDescricao.setText("");
        txtValor.setText("");
        cmbTipo.setSelectedIndex(0);
        listaParticipantes.clearSelection();
        tabela.clearSelection();
        atualizarBotoes();
        txtDescricao.requestFocusInWindow();
    }

    public boolean confirmar(String mensagem) {
        return JOptionPane.showConfirmDialog(this, mensagem, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // janela com uma caixinha por despesa, devolve as posições que ficaram marcadas
    public List<Integer> escolherDespesas(String mensagem, List<String> despesas) {
        JPanel painel = new JPanel(new GridLayout(0, 1, 2, 2));
        for (String linha : mensagem.split("\n")) painel.add(new JLabel(linha));
        List<JCheckBox> caixas = new ArrayList<>();
        for (String d : despesas) {
            JCheckBox c = new JCheckBox(d, true);
            caixas.add(c);
            painel.add(c);
        }
        int r = JOptionPane.showConfirmDialog(this, painel, "Pessoa nova no grupo",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        List<Integer> marcadas = new ArrayList<>();
        if (r == JOptionPane.OK_OPTION) {
            for (int i = 0; i < caixas.size(); i++) if (caixas.get(i).isSelected()) marcadas.add(i);
        }
        return marcadas;
    }

    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
