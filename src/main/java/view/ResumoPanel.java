package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

// Aba de resumo do planejamento + salvar/carregar o arquivo.
// Igual às outras abas: aqui só mostra, os números vêm calculados do controller/model.
public class ResumoPanel extends JPanel {

    private final JLabel lblCustoTotal = new JLabel("Custo total: R$ 0,00");

    private final JButton btnAtualizar = new JButton("Atualizar resumo");
    private final JButton btnSalvar = new JButton("Salvar planejamento");
    private final JButton btnCarregar = new JButton("Carregar planejamento");

    // Duas tabelas sem edição: uma com as despesas e outra com a situação de cada participante
    private final DefaultTableModel despesasModel = criarModelo("Descrição", "Tipo", "Valor (R$)");
    private final DefaultTableModel participantesModel = criarModelo("Participante", "Orçamento (R$)", "Total (R$)", "Situação");

    public ResumoPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnAtualizar);
        botoes.add(btnSalvar);
        botoes.add(btnCarregar);
        add(botoes, BorderLayout.NORTH);

        // GridLayout de 1 coluna pra as duas tabelas dividirem a altura igual
        JPanel tabelas = new JPanel(new GridLayout(2, 1, 8, 8));
        tabelas.add(comTitulo("Despesas", despesasModel));
        tabelas.add(comTitulo("Situação por participante", participantesModel));
        add(tabelas, BorderLayout.CENTER);

        lblCustoTotal.setFont(lblCustoTotal.getFont().deriveFont(Font.BOLD, 14f));
        add(lblCustoTotal, BorderLayout.SOUTH);
    }

    private DefaultTableModel criarModelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JScrollPane comTitulo(String titulo, DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        // Todo JTable pede 400 de altura por padrão, e com duas tabelas a janela esticava demais
        tabela.setPreferredScrollableViewportSize(new Dimension(450, 90));
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder(titulo));
        return scroll;
    }

    // ---- Métodos que o controller vai usar ----

    public void onAtualizar(ActionListener l) { btnAtualizar.addActionListener(l); }
    public void onSalvar(ActionListener l) { btnSalvar.addActionListener(l); }
    public void onCarregar(ActionListener l) { btnCarregar.addActionListener(l); }

    // O controller manda tudo já formatado (ex: "R$ 75,00", "Excede em R$ 5,00"), a view só exibe
    public void setDespesas(List<Object[]> linhas) {
        despesasModel.setRowCount(0);
        linhas.forEach(despesasModel::addRow);
    }

    public void setParticipantes(List<Object[]> linhas) {
        participantesModel.setRowCount(0);
        linhas.forEach(participantesModel::addRow);
    }

    public void setCustoTotal(String texto) {
        lblCustoTotal.setText("Custo total: " + texto);
    }

    // Abre a janela de escolher arquivo. Devolve null se o usuário cancelar.
    // O arquivo em si quem lê/grava é a classe de persistência, não a view
    public File escolherArquivo(boolean salvar) {
        JFileChooser chooser = new JFileChooser();
        int opcao = salvar ? chooser.showSaveDialog(this) : chooser.showOpenDialog(this);
        return opcao == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // Aviso de sucesso (ex: "Planejamento salvo")
    public void mostrarInfo(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }
}
