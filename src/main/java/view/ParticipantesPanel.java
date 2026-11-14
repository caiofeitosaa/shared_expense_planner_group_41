package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

// Painel da aba "Participantes". A view só recebe entradas e mostra resultados:
// validar e calcular fica para o controller e o service.
public class ParticipantesPanel extends JPanel {

    private final JTextField txtCodigo = new JTextField();
    private final JTextField txtNome = new JTextField();
    private final JTextField txtOrcamento = new JTextField();
    // Mesma ordem das colunas da tabela, para preencher o formulário em um laço
    private final JTextField[] campos = {txtCodigo, txtNome, txtOrcamento};

    private final JButton btnCadastrar = new JButton("Cadastrar");
    private final JButton btnAlterar = new JButton("Alterar");
    private final JButton btnExcluir = new JButton("Excluir");
    private final JButton btnLimpar = new JButton("Limpar");

    // Células não editáveis: toda alteração passa pelo formulário e, portanto, pelo controller
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

        // GridLayout de 2 colunas: rótulo à esquerda, campo à direita, sem restrições a configurar
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
        // Clicar numa linha preenche o formulário e entra no modo de edição
        tabela.getSelectionModel().addListSelectionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (e.getValueIsAdjusting() || linha < 0) return;
            for (int i = 0; i < campos.length; i++) campos[i].setText(String.valueOf(tabelaModel.getValueAt(linha, i)));
            modoEdicao(true);
        });
        btnLimpar.addActionListener(e -> limparFormulario());
        modoEdicao(false);
    }

    // Em edição o código fica bloqueado (é a identidade única); fora dela só "Cadastrar" está ativo
    private void modoEdicao(boolean editando) {
        txtCodigo.setEnabled(!editando);
        btnCadastrar.setEnabled(!editando);
        btnAlterar.setEnabled(editando);
        btnExcluir.setEnabled(editando);
    }

    // ---- API para o controller ----

    // Texto cru: converter para centavos e validar é papel do controller/model
    public String getCodigo() { return txtCodigo.getText().trim(); }
    public String getNome() { return txtNome.getText().trim(); }
    public String getOrcamento() { return txtOrcamento.getText().trim(); }

    public void onCadastrar(ActionListener l) { btnCadastrar.addActionListener(l); }
    public void onAlterar(ActionListener l) { btnAlterar.addActionListener(l); }
    public void onExcluir(ActionListener l) { btnExcluir.addActionListener(l); }

    // Recarrega a tabela do model, assim a tela reflete o estado real mesmo após uma operação recusada
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

    // Mensagens de validação vindas do controller
    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
