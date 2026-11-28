package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

// Aba de participantes. Aqui é só a tela (MVC): validação e cálculo ficam no controller/service.
public class ParticipantesPanel extends JPanel {

    private final JTextField txtCodigo = new JTextField();
    private final JTextField txtNome = new JTextField();
    private final JTextField txtOrcamento = new JTextField();
    // Na mesma ordem das colunas da tabela, assim dá pra usar um for pra preencher
    private final JTextField[] campos = {txtCodigo, txtNome, txtOrcamento};

    private final JButton btnCadastrar = new JButton("Cadastrar");
    private final JButton btnAlterar = new JButton("Alterar");
    private final JButton btnExcluir = new JButton("Excluir");
    private final JButton btnLimpar = new JButton("Limpar");

    // Tabela sem edição direta, pra mudar algo tem que usar o formulário (assim não pula a validação)
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

        // GridLayout com 2 colunas (texto e campo), é o mais fácil de alinhar
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Dados do participante"));
        form.add(new JLabel("Código:"));
        form.add(txtCodigo);
        form.add(new JLabel("Nome:"));
        form.add(txtNome);
        form.add(new JLabel("Orçamento (R$):"));
        form.add(txtOrcamento);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton b : new JButton[]{btnCadastrar, btnAlterar, btnExcluir, btnLimpar}) botoes.add(b);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(form, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Ao clicar numa linha, joga os dados dela no formulário e libera Alterar/Excluir
        tabela.getSelectionModel().addListSelectionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (e.getValueIsAdjusting() || linha < 0) return;
            for (int i = 0; i < campos.length; i++) campos[i].setText(String.valueOf(tabelaModel.getValueAt(linha, i)));
            modoEdicao(true);
        });
        btnLimpar.addActionListener(e -> limparFormulario());
        modoEdicao(false);
    }

    // Editando, o código trava (ele é único e não pode mudar) e só Alterar/Excluir funcionam.
    // Fora disso só o Cadastrar
    private void modoEdicao(boolean editando) {
        txtCodigo.setEnabled(!editando);
        btnCadastrar.setEnabled(!editando);
        btnAlterar.setEnabled(editando);
        btnExcluir.setEnabled(editando);
    }

    // ---- Métodos que o controller vai usar ----

    // Devolvo o texto do jeito que o usuário digitou, o controller que converte pra centavos e valida
    public String getCodigo() { return txtCodigo.getText().trim(); }
    public String getNome() { return txtNome.getText().trim(); }
    public String getOrcamento() { return txtOrcamento.getText().trim(); }

    public void onCadastrar(ActionListener l) { btnCadastrar.addActionListener(l); }
    public void onAlterar(ActionListener l) { btnAlterar.addActionListener(l); }
    public void onExcluir(ActionListener l) { btnExcluir.addActionListener(l); }

    // Refaz a tabela toda com o que veio do model, assim a tela nunca fica diferente dos dados
    public void setLinhas(List<Object[]> linhas) {
        tabelaModel.setRowCount(0);
        linhas.forEach(tabelaModel::addRow);
        limparFormulario();
    }

    public void limparFormulario() {
        for (JTextField c : campos) c.setText("");
        tabela.clearSelection();
        modoEdicao(false);
    }

    // Mostra a mensagem de erro que o controller mandar
    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
