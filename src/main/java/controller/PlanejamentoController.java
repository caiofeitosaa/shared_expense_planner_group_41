package controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import persistence.ArquivoNaoEncontradoException;
import persistence.ArquivoPersistence;
import persistence.DadosSalvos;

/**
 * Controller para gerenciamento de arquivos de planejamento.
 * Cada planejamento (praia, cinema, chácara...) é um arquivo dados/nome.dat
 * com as pessoas e despesas dele.
 */
public class PlanejamentoController {

    public static final String PADRAO = "planejamento";
    private static final Path DIRETORIO = Path.of("dados");
    private static final Path ULTIMO = DIRETORIO.resolve("ultimo.txt");

    private String nomeAtual;

    // Tira caracteres que o Windows não aceita em nome de arquivo
    public static String limparNome(String nome) {
        return nome == null ? "" : nome.trim().replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    public static Path arquivoDe(String nome) {
        return DIRETORIO.resolve(limparNome(nome) + ".dat");
    }

    public static boolean existe(String nome) {
        return Files.exists(arquivoDe(nome));
    }

    public static List<String> listar() {
        if (!Files.isDirectory(DIRETORIO)) {
            return new ArrayList<>();
        }
        try (Stream<Path> arquivos = Files.list(DIRETORIO)) {
            return arquivos
                    .map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(".dat"))
                    .map(n -> n.substring(0, n.length() - 4))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static DadosSalvos ler(String nome) throws ArquivoNaoEncontradoException {
        return new ArquivoPersistence<>(arquivoDe(nome), DadosSalvos.class).carregar();
    }

    public static void apagar(String nome) throws IOException {
        Files.deleteIfExists(arquivoDe(nome));
    }

    // Guarda o último aberto pra abrir ele de novo quando iniciar o programa
    public static String lerUltimo() {
        try {
            if (Files.exists(ULTIMO)) {
                String nome = Files.readString(ULTIMO, StandardCharsets.UTF_8).trim();
                if (!nome.isEmpty() && existe(nome)) {
                    return nome;
                }
            }
        } catch (IOException ignored) {
            // se não conseguir ler, abre o padrão
        }
        return PADRAO;
    }

    private static void gravarUltimo(String nome) {
        try {
            Files.createDirectories(DIRETORIO);
            Files.writeString(ULTIMO, nome, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // não é crítico
        }
    }

    public String getNomeAtual() {
        return nomeAtual;
    }

    public void setNomeAtual(String nome) {
        this.nomeAtual = limparNome(nome);
        gravarUltimo(this.nomeAtual);
    }
}
