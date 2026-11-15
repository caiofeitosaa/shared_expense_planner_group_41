package view;

import javax.swing.*;
import java.awt.*;

// Janela principal, com uma aba pra cada funcionalidade do trabalho.
public class MainFrame extends JFrame {

    private final ParticipantesPanel participantesPanel = new ParticipantesPanel();
    private final DespesasPanel despesasPanel = new DespesasPanel();

    public MainFrame() {
        super("Grupo 41 - Planejador de despesas compartilhadas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Participantes", participantesPanel);
        abas.addTab("Despesas", despesasPanel);
        // shortcut: essa aba ainda é só um aviso, falta fazer na etapa 3 (Resumo/Arquivo)
        abas.addTab("Resumo", reserva("Etapa 3: resumo, orçamento e persistência"));
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

    private JPanel reserva(String texto) {
        JPanel p = new JPanel(new GridBagLayout());
        p.add(new JLabel(texto));
        return p;
    }

    // main temporário só pra ver a tela sem o controller.
    // invokeLater porque o Swing tem que ser criado na thread da interface
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}