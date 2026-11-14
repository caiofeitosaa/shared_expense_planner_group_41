package view;

import javax.swing.*;
import java.awt.*;

// Janela principal: um JFrame com abas, uma por funcionalidade obrigatória.
// Abas permitem trabalhar com um planejamento por vez, como o enunciado pede.
public class MainFrame extends JFrame {

    private final ParticipantesPanel participantesPanel = new ParticipantesPanel();

    public MainFrame() {
        super("Grupo 41 - Planejador de despesas compartilhadas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Participantes", participantesPanel);
        // shortcut: abas abaixo são só reserva de lugar; serão trocadas na etapa 2 (Despesas) e 3 (Resumo/Arquivo)
        abas.addTab("Despesas", reserva("Etapa 2: cadastro de despesas"));
        abas.addTab("Resumo", reserva("Etapa 3: resumo, orçamento e persistência"));
        add(abas);

        pack();
        setLocationRelativeTo(null); // centraliza na tela
    }

    // Controller precisa alcançar o painel para ligar os eventos
    public ParticipantesPanel getParticipantesPanel() {
        return participantesPanel;
    }

    private JPanel reserva(String texto) {
        JPanel p = new JPanel(new GridBagLayout());
        p.add(new JLabel(texto));
        return p;
    }

    // Ponto de entrada temporário da etapa 1, para avaliar a tela sem controller.
    // invokeLater: Swing deve ser criado na Event Dispatch Thread
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
