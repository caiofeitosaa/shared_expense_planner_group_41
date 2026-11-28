package controller;

import java.util.ArrayList;
import java.util.List;

import entities.Despesa;
import entities.Participante;
import persistence.ArquivoNaoEncontradoException;
import persistence.ArquivoPersistence;
import persistence.DadosSalvos;
import repositories.DespesaRepository;
import repositories.ParticipanteRepository;
import service.DespesaService;
import service.ParticipanteService;
import view.MainFrame;

/**
 * Controller principal da aplicação.
 * Cria tudo, liga os controllers e cuida da troca de planejamento (praia, cinema...).
 */
public class MainController {

    private final MainFrame view;
    private final ParticipanteController participanteController;
    private final DespesaController despesaController;
    private final ResumoController resumoController;
    private final PlanejamentoController planejamentos = new PlanejamentoController();

    // Camada de serviços
    private final ParticipanteService participanteService;
    private final DespesaService despesaService;

    // Os dois repositories usam esse mesmo objeto. Pra trocar de planejamento
    // a gente troca o conteúdo dele e o arquivo onde salva
    private final DadosSalvos dados;
    private final ArquivoPersistence<DadosSalvos> persistencia;

    public MainController() {
        String nomeInicial = PlanejamentoController.lerUltimo();
        this.dados = carregarDados(nomeInicial);
        this.persistencia = new ArquivoPersistence<>(PlanejamentoController.arquivoDe(nomeInicial), DadosSalvos.class);
        planejamentos.setNomeAtual(nomeInicial);

        // Repositórios e serviços
        ParticipanteRepository participanteRepository = new ParticipanteRepository(dados, persistencia);
        DespesaRepository despesaRepository = new DespesaRepository(dados, persistencia);
        this.participanteService = new ParticipanteService(participanteRepository, despesaRepository);
        this.despesaService = new DespesaService(despesaRepository);

        // salva logo pra ele aparecer na lista
        persistencia.salvar(dados);

        this.view = new MainFrame();

        // Controllers de apoio
        DespesaPorPessoaController porPessoaController = new DespesaPorPessoaController(despesaService, null);
        DespesaFixaController fixaController = new DespesaFixaController(despesaService, porPessoaController, null);

        // Controllers principais
        this.participanteController = new ParticipanteController(participanteService, view.getParticipantesPanel());
        this.despesaController = new DespesaController(despesaService, porPessoaController,
                fixaController, view.getDespesasPanel());
        this.resumoController = new ResumoController(despesaService, participanteService, view.getResumoPanel());

        // Conecta os controllers entre si
        despesaController.setParticipanteController(participanteController);
        despesaController.setResumoController(resumoController);
        participanteController.setDespesaController(despesaController);
        participanteController.setResumoController(resumoController);

        configurarEventos();
        recarregarTelas();
    }

    private DadosSalvos carregarDados(String nome) {
        try {
            return PlanejamentoController.ler(nome);
        } catch (ArquivoNaoEncontradoException e) {
            return new DadosSalvos(new ArrayList<>(), new ArrayList<>());
        } catch (RuntimeException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Erro ao carregar \"" + nome + "\": " + e.getMessage(),
                    "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
            return new DadosSalvos(new ArrayList<>(), new ArrayList<>());
        }
    }

    private void configurarEventos() {
        // Barra de planejamentos no topo
        view.onTrocarPlanejamento(this::abrir);
        view.onNovoPlanejamento(e -> novo());
        view.onSalvarComo(e -> salvarComo());
        view.onRenomear(e -> renomear());
        view.onExcluirPlanejamento(e -> excluir());

        // Botões da aba Resumo
        view.getResumoPanel().onSalvar(e -> salvar());
        view.getResumoPanel().onCarregar(e -> {
            String nome = view.escolherPlanejamento(PlanejamentoController.listar(), planejamentos.getNomeAtual());
            if (nome != null) abrir(nome);
        });
    }

    // Ações dos planejamentos

    private void salvar() {
        try {
            persistencia.salvar(dados);
            view.mostrarInfo("Planejamento \"" + planejamentos.getNomeAtual() + "\" salvo com sucesso!");
        } catch (RuntimeException e) {
            view.mostrarErro("Erro ao salvar: " + e.getMessage());
        }
    }

    private void abrir(String nome) {
        if (nome == null || nome.equals(planejamentos.getNomeAtual())) return;
        try {
            persistencia.salvar(dados); // garante que o atual está gravado antes de trocar
            DadosSalvos novos = PlanejamentoController.ler(nome);
            trocarPara(nome, novos);
        } catch (ArquivoNaoEncontradoException e) {
            view.mostrarErro("Planejamento \"" + nome + "\" não encontrado.");
            recarregarTelas();
        } catch (RuntimeException e) {
            view.mostrarErro("Erro ao abrir: " + e.getMessage());
            recarregarTelas();
        }
    }

    private void novo() {
        String nome = pedirNomeNovo("Nome do novo planejamento (ex.: Praia, Cinema):", "");
        if (nome == null) return;
        persistencia.salvar(dados);
        trocarPara(nome, new DadosSalvos(new ArrayList<>(), new ArrayList<>()));
    }

    // Copia o planejamento atual com outro nome (ex: usar a praia de base pra chácara)
    private void salvarComo() {
        String nome = pedirNomeNovo("Salvar uma cópia deste planejamento com o nome:", planejamentos.getNomeAtual() + " (cópia)");
        if (nome == null) return;
        persistencia.salvar(dados);
        trocarPara(nome, dados);
    }

    private void renomear() {
        String antigo = planejamentos.getNomeAtual();
        String nome = pedirNomeNovo("Novo nome para \"" + antigo + "\":", antigo);
        if (nome == null) return;
        try {
            trocarPara(nome, dados);
            PlanejamentoController.apagar(antigo);
            recarregarTelas();
        } catch (Exception e) {
            view.mostrarErro("Erro ao renomear: " + e.getMessage());
        }
    }

    private void excluir() {
        String atual = planejamentos.getNomeAtual();
        if (!view.confirmar("Excluir o planejamento \"" + atual + "\" com todas as pessoas e despesas?")) return;
        try {
            PlanejamentoController.apagar(atual);
            List<String> restantes = PlanejamentoController.listar();
            if (restantes.isEmpty()) {
                trocarPara(PlanejamentoController.PADRAO, new DadosSalvos(new ArrayList<>(), new ArrayList<>()));
            } else {
                trocarPara(restantes.get(0), PlanejamentoController.ler(restantes.get(0)));
            }
        } catch (Exception e) {
            view.mostrarErro("Erro ao excluir: " + e.getMessage());
        }
    }

    // Fica pedindo o nome até ser um que não existe. null = cancelou
    private String pedirNomeNovo(String mensagem, String sugestao) {
        String nome = view.pedirNome(mensagem, sugestao);
        while (nome != null) {
            nome = PlanejamentoController.limparNome(nome);
            if (nome.isEmpty()) {
                view.mostrarErro("Informe um nome.");
            } else if (PlanejamentoController.existe(nome)) {
                view.mostrarErro("Já existe um planejamento chamado \"" + nome + "\".");
            } else {
                return nome;
            }
            nome = view.pedirNome(mensagem, nome);
        }
        return null;
    }

    // Coloca os dados do planejamento escolhido e passa a salvar no arquivo dele
    private void trocarPara(String nome, DadosSalvos novos) {
        List<Participante> participantes = new ArrayList<>(novos.getParticipantes());
        List<Despesa> despesas = new ArrayList<>(novos.getDespesas());
        dados.definirParticipantes(participantes);
        dados.definirDespesas(despesas);
        persistencia.setCaminhoArquivo(PlanejamentoController.arquivoDe(nome));
        persistencia.salvar(dados);
        planejamentos.setNomeAtual(nome);
        view.getDespesasPanel().limparFormulario();
        view.getParticipantesPanel().limparFormulario();
        recarregarTelas();
    }

    // Atualiza todas as abas e a barra de planejamentos
    private void recarregarTelas() {
        view.setPlanejamentos(PlanejamentoController.listar(), planejamentos.getNomeAtual());
        participanteController.atualizarTabela();
        despesaController.refreshParticipantes();
        despesaController.atualizarTabela();
        resumoController.atualizarResumo(null);
    }

    /**
     * Exibe a janela principal
     */
    public void iniciar() {
        view.setVisible(true);
    }

    public ParticipanteService getParticipanteService() {
        return participanteService;
    }

    public DespesaService getDespesaService() {
        return despesaService;
    }

    public DadosSalvos getDadosSalvos() {
        return dados;
    }

    public ArquivoPersistence<DadosSalvos> getPersistencia() {
        return persistencia;
    }
}
