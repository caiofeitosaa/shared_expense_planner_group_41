package controller;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import persistence.ArquivoNaoEncontradoException;
import persistence.ArquivoPersistence;
import persistence.DadosSalvos;
import repositories.DespesaRepository;
import repositories.ParticipanteRepository;
import service.DespesaFixaService;
import service.DespesaPorPessoaService;
import service.DespesaService;
import service.ParticipanteService;
import service.ResumoService;
import view.MainFrame;
import view.ResumoPanel;

/**
 * Controller principal da aplicação.
 * Cria repositories, services e controllers, liga tudo na janela e cuida de salvar/carregar.
 */
public class MainController {

    // Arquivo onde os repositories salvam sozinhos a cada mudança
    private static final Path ARQUIVO_PADRAO = Path.of("dados/planejamento.dat");

    private final MainFrame view = new MainFrame();
    private final ParticipanteController participanteController;
    private final DespesaController despesaController;
    private final ResumoController resumoController;

    // Os dois repositories usam esse mesmo objeto. Pra carregar outro arquivo
    // a gente troca o conteúdo dele, não o objeto
    private final DadosSalvos dados;
    private final ArquivoPersistence<DadosSalvos> persistencia = new ArquivoPersistence<>(ARQUIVO_PADRAO, DadosSalvos.class);

    public MainController() {
        this.dados = carregarInicial();

        // Repositórios e serviços
        ParticipanteRepository participanteRepository = new ParticipanteRepository(dados, persistencia);
        DespesaRepository despesaRepository = new DespesaRepository(dados, persistencia);
        ParticipanteService participanteService = new ParticipanteService(participanteRepository, despesaRepository);
        DespesaService despesaService = new DespesaService(despesaRepository);

        // Controllers, cada aba avisa o recarregarTelas quando muda alguma coisa
        participanteController = new ParticipanteController(participanteService, view.getParticipantesPanel(), this::recarregarTelas);
        despesaController = new DespesaController(despesaService, participanteService,
                new DespesaFixaController(new DespesaFixaService(despesaRepository)),
                new DespesaPorPessoaController(new DespesaPorPessoaService(despesaRepository)),
                view.getDespesasPanel(), this::recarregarTelas);
        resumoController = new ResumoController(despesaService,
                new ResumoService(participanteRepository, despesaRepository), view.getResumoPanel());

        view.getResumoPanel().onSalvar(e -> salvar());
        view.getResumoPanel().onCarregar(e -> carregar());
        recarregarTelas();
    }

    // Arquivo ausente = primeira vez, começa vazio. Arquivo com problema também começa vazio, mas avisa
    private DadosSalvos carregarInicial() {
        try {
            return persistencia.carregar();
        } catch (ArquivoNaoEncontradoException e) {
            return new DadosSalvos(new ArrayList<>(), new ArrayList<>());
        } catch (RuntimeException e) {
            view.getResumoPanel().mostrarErro("Não foi possível abrir o último planejamento, começando um vazio.");
            return new DadosSalvos(new ArrayList<>(), new ArrayList<>());
        }
    }

    // Salva uma cópia do planejamento no arquivo que o usuário escolher
    private void salvar() {
        ResumoPanel resumo = view.getResumoPanel();
        File arquivo = resumo.escolherArquivo(true);
        if (arquivo == null) return;
        try {
            new ArquivoPersistence<>(arquivo.toPath(), DadosSalvos.class).salvar(dados);
            resumo.mostrarInfo("Planejamento salvo em " + arquivo.getName());
        } catch (RuntimeException e) {
            resumo.mostrarErro("Erro ao salvar o arquivo.");
        }
    }

    // Se der erro ao carregar, o planejamento em uso continua igual
    private void carregar() {
        ResumoPanel resumo = view.getResumoPanel();
        File arquivo = resumo.escolherArquivo(false);
        if (arquivo == null) return;
        try {
            DadosSalvos novos = new ArquivoPersistence<>(arquivo.toPath(), DadosSalvos.class).carregar();
            // Troca tudo de uma vez (não soma), assim não duplica registros
            dados.definirParticipantes(new ArrayList<>(novos.getParticipantes()));
            dados.definirDespesas(new ArrayList<>(novos.getDespesas()));
            persistencia.salvar(dados);
            recarregarTelas();
            resumo.mostrarInfo("Planejamento carregado de " + arquivo.getName());
        } catch (ArquivoNaoEncontradoException e) {
            resumo.mostrarErro("Arquivo não encontrado.");
        } catch (RuntimeException e) {
            resumo.mostrarErro("Arquivo inválido, o planejamento atual foi mantido.");
        }
    }

    // Atualiza todas as abas com os dados atuais
    private void recarregarTelas() {
        participanteController.atualizarTabela();
        despesaController.atualizarTabela();
        resumoController.atualizarResumo();
    }

    /**
     * Exibe a janela principal
     */
    public void iniciar() {
        view.setVisible(true);
    }

    // Ponto de entrada do programa. invokeLater porque o Swing tem que ser criado na thread da interface
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainController().iniciar());
    }
}
