package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

// Janela principal, com uma aba pra cada funcionalidade do trabalho.
// Em cima fica a barra pra escolher o planejamento (praia, cinema...)
public class MainFrame extends JFrame {

    private static final String TITULO = "Grupo 41 - Planejador de despesas compartilhadas";

    private final ParticipantesPanel participantesPanel = new ParticipantesPanel();
    private final DespesasPanel despesasPanel = new DespesasPanel();
    private final ResumoPanel resumoPanel = new ResumoPanel();

    private final JComboBox<String> cmbPlanejamentos = new JComboBox<>();
    private final JButton btnNovo = new JButton("Novo");
    private final JButton btnSalvarComo = new JButton("Salvar como...");
    private final JButton btnRenomear = new JButton("Renomear");
    private final JButton btnExcluirPlanejamento = new JButton("Excluir");
    // sem isso o combo disparava a troca sozinho quando a gente atualizava a lista
    private boolean atualizandoCombo = false;

    public MainFrame() {
        super(TITULO);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        JLabel lbl = new JLabel("Planejamento:");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        cmbPlanejamentos.setPrototypeDisplayValue("Viagem para a praia em janeiro");
        cmbPlanejamentos.setToolTipText("Escolha a lista que quer abrir (ex.: Praia, Cinema)");
        btnNovo.setToolTipText("Começa uma lista nova, vazia");
        btnSalvarComo.setToolTipText("Salva uma cópia da lista atual com outro nome");
        barra.add(lbl);
        barra.add(cmbPlanejamentos);
        barra.add(btnNovo);
        barra.add(btnSalvarComo);
        barra.add(btnRenomear);
        barra.add(btnExcluirPlanejamento);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Participantes", participantesPanel);
        abas.addTab("Despesas", despesasPanel);
        abas.addTab("Resumo", resumoPanel);

        setLayout(new BorderLayout());
        add(barra, BorderLayout.NORTH);
        add(abas, BorderLayout.CENTER);

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null); // centraliza na tela
    }

    // O controller vai precisar disso pra ligar os botões
    public ParticipantesPanel getParticipantesPanel() {
        return participantesPanel;
    }

    public DespesasPanel getDespesasPanel() {
        return despesasPanel;
    }

    public ResumoPanel getResumoPanel() {
        return resumoPanel;
    }

    // ---- Barra de planejamentos ----

    public void setPlanejamentos(List<String> nomes, String atual) {
        atualizandoCombo = true;
        cmbPlanejamentos.removeAllItems();
        nomes.forEach(cmbPlanejamentos::addItem);
        cmbPlanejamentos.setSelectedItem(atual);
        atualizandoCombo = false;
        setTitle(TITULO + " - " + atual);
    }

    public void onTrocarPlanejamento(java.util.function.Consumer<String> acao) {
        cmbPlanejamentos.addActionListener(e -> {
            if (atualizandoCombo) return;
            Object item = cmbPlanejamentos.getSelectedItem();
            if (item != null) acao.accept(item.toString());
        });
    }

    public void onNovoPlanejamento(ActionListener l) { btnNovo.addActionListener(l); }
    public void onSalvarComo(ActionListener l) { btnSalvarComo.addActionListener(l); }
    public void onRenomear(ActionListener l) { btnRenomear.addActionListener(l); }
    public void onExcluirPlanejamento(ActionListener l) { btnExcluirPlanejamento.addActionListener(l); }

    public String pedirNome(String mensagem, String valorInicial) {
        Object r = JOptionPane.showInputDialog(this, mensagem, "Planejamento",
                JOptionPane.QUESTION_MESSAGE, null, null, valorInicial);
        return r == null ? null : r.toString().trim();
    }

    public String escolherPlanejamento(List<String> nomes, String atual) {
        if (nomes.isEmpty()) return null;
        Object r = JOptionPane.showInputDialog(this, "Qual planejamento deseja abrir?", "Abrir planejamento",
                JOptionPane.QUESTION_MESSAGE, null, nomes.toArray(), atual);
        return r == null ? null : r.toString();
    }

    public boolean confirmar(String mensagem) {
        return JOptionPane.showConfirmDialog(this, mensagem, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarInfo(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }
}
