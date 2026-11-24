package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
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
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnExcluir = new JButton("Excluir");
    private final JButton btnLimpar = new JButton("Limpar");

    // Tabela sem edição direta, pra mudar algo tem que usar o formulário (assim não pula a validação)
    private final DefaultTableModel tabelaModel = new DefaultTableModel(new String[]{"Descrição", "Tipo", "Valor (R$)", "Participantes"}, 0) {
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

        listaParticipantes.setVisibleRowCount(4);
        JPanel participantes = new JPanel(new BorderLayout(0, 4));
        participantes.add(new JLabel("Participantes (Ctrl para escolher vários):"), BorderLayout.NORTH);
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
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Ao clicar numa linha, joga os dados dela no formulário
        tabela.getSelectionModel().addListSelectionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (e.getValueIsAdjusting() || linha < 0) return;
            txtDescricao.setText(String.valueOf(tabelaModel.getValueAt(linha, 0)));
            cmbTipo.setSelectedItem(tabelaModel.getValueAt(linha, 1));
            txtValor.setText(String.valueOf(tabelaModel.getValueAt(linha, 2)));
            selecionarParticipantes(String.valueOf(tabelaModel.getValueAt(linha, 3)));
            modoEdicao(true);
        });
        btnLimpar.addActionListener(e -> limparFormulario());
        modoEdicao(false);
    }

    // Marca na lista quem está no texto da tabela (ex: "1 - Ana, 2 - Bruno")
    // shortcut: separo por ", " então nome com vírgula dá problema, se precisar usar o código no lugar
    private void selecionarParticipantes(String texto) {
        List<String> nomes = List.of(texto.split(", "));
        listaParticipantes.clearSelection();
        for (int i = 0; i < participantesModel.size(); i++) {
            if (nomes.contains(participantesModel.get(i))) listaParticipantes.addSelectionInterval(i, i);
        }
    }

    // Com linha selecionada só Editar/Excluir funcionam, senão só Incluir
    private void modoEdicao(boolean editando) {
        btnIncluir.setEnabled(!editando);
        btnEditar.setEnabled(editando);
        btnExcluir.setEnabled(editando);
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

    // Recebe os participantes cadastrados na aba 1 pra aparecerem na lista
    public void setParticipantes(List<String> participantes) {
        participantesModel.clear();
        participantes.forEach(participantesModel::addElement);
    }

    // Refaz a tabela toda com o que veio do model, assim a tela nunca fica diferente dos dados
    public void setLinhas(List<Object[]> linhas) {
        tabelaModel.setRowCount(0);
        linhas.forEach(tabelaModel::addRow);
        limparFormulario();
    }

    public void limparFormulario() {
        txtDescricao.setText("");
        txtValor.setText("");
        cmbTipo.setSelectedIndex(0);
        listaParticipantes.clearSelection();
        tabela.clearSelection();
        modoEdicao(false);
    }

    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
