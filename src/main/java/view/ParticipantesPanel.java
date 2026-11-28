package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

// Aba de participantes - sem código, só nome e orçamento
public class ParticipantesPanel extends JPanel {

    private final JTextField txtNome = new JTextField();
    private final JTextField txtOrcamento = new JTextField();

    private final JButton btnCadastrar = new JButton("Adicionar");
    private final JButton btnAlterar = new JButton("Alterar");
    private final JButton btnExcluir = new JButton("Excluir");

    // coluna 0 é o código (escondida). Antes tinha só 2 colunas e o nome caía na coluna do orçamento
    private final DefaultTableModel tabelaModel = new DefaultTableModel(new String[]{"Código", "Nome", "Orçamento (R$)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(tabelaModel);

    public ParticipantesPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Dados do participante"));
        form.add(new JLabel("Nome:"));
        form.add(txtNome);
        form.add(new JLabel("Orçamento (R$):"));
        form.add(txtOrcamento);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton b : new JButton[]{btnCadastrar, btnAlterar, btnExcluir}) botoes.add(b);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(form, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // esconde o código mas ele continua no model pro controller usar
        tabela.removeColumn(tabela.getColumnModel().getColumn(0));
        tabela.setFillsViewportHeight(true);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                txtNome.setText(String.valueOf(tabelaModel.getValueAt(linha, 1)));
                txtOrcamento.setText(String.valueOf(tabelaModel.getValueAt(linha, 2)).replace("R$", "").trim());
            }
            modoEdicao(linha >= 0);
        });
        // Clicar na área vazia da tabela tira a seleção e libera o Adicionar
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (tabela.rowAtPoint(e.getPoint()) < 0) tabela.clearSelection();
            }
        });
        modoEdicao(false);
    }

    private void modoEdicao(boolean editando) {
        btnCadastrar.setEnabled(true);
        btnAlterar.setEnabled(editando);
        btnExcluir.setEnabled(editando);
    }

    // ---- Métodos que o controller vai usar ----

    public String getNome() { return txtNome.getText().trim(); }
    public String getOrcamento() { return txtOrcamento.getText().trim(); }

    // Índice da linha no model (a coluna 0 do model é o código)
    public int getLinhaSelecionada() {
        int linha = tabela.getSelectedRow();
        return linha < 0 ? -1 : tabela.convertRowIndexToModel(linha);
    }

    public DefaultTableModel getTabelaModel() {
        return tabelaModel;
    }

    public void onCadastrar(ActionListener l) { btnCadastrar.addActionListener(l); }
    public void onAlterar(ActionListener l) { btnAlterar.addActionListener(l); }
    public void onExcluir(ActionListener l) { btnExcluir.addActionListener(l); }

    public void setLinhas(List<Object[]> linhas) {
        tabelaModel.setRowCount(0);
        linhas.forEach(tabelaModel::addRow);
        modoEdicao(false);
    }

    public void limparFormulario() {
        txtNome.setText("");
        txtOrcamento.setText("");
        tabela.clearSelection();
        modoEdicao(false);
    }

    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}