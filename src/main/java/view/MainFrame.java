package view;

import javax.swing.*;

// Janela principal, com uma aba pra cada funcionalidade do trabalho.
public class MainFrame extends JFrame {

    private final ParticipantesPanel participantesPanel = new ParticipantesPanel();
    private final DespesasPanel despesasPanel = new DespesasPanel();
    private final ResumoPanel resumoPanel = new ResumoPanel();

    public MainFrame() {
        super("Grupo 41 - Planejador de despesas compartilhadas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Participantes", participantesPanel);
        abas.addTab("Despesas", despesasPanel);
        abas.addTab("Resumo", resumoPanel);
        add(abas);

        pack();
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

    // main temporário só pra ver a tela sem o controller.
    // invokeLater porque o Swing tem que ser criado na thread da interface
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}