package persistence;

import java.io.*;
import java.nio.file.*;

public class ArquivoPersistence<T extends Serializable> {

    // tirei o final pq quando troca de planejamento o arquivo muda
    private Path caminhoArquivo;
    private final Class<T> tipo;


    public ArquivoPersistence(Path caminhoArquivo, Class<T> tipo) {
        if (caminhoArquivo == null || tipo == null) {
            throw new IllegalArgumentException("Caminho e tipo são obrigatórios.");
        }
        this.caminhoArquivo = caminhoArquivo;
        this.tipo = tipo;
    }

    public Path getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(Path caminhoArquivo) {
        if (caminhoArquivo == null) {
            throw new IllegalArgumentException("Caminho é obrigatório.");
        }
        this.caminhoArquivo = caminhoArquivo;
    }

    // Salva um objeto no arquivo
    public void salvar(T dados) {

        try {

            Files.createDirectories(caminhoArquivo.toAbsolutePath().getParent());

            try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(caminhoArquivo))) {

                output.writeObject(dados);
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar os dados.", e);
        }
    }

    // Recupera os objetos armazenados
    @SuppressWarnings("unchecked")
    public T carregar() throws ArquivoNaoEncontradoException {


        if (!Files.exists(caminhoArquivo)) {
            throw new ArquivoNaoEncontradoException("Nenhum arquivo encontrado em: " + caminhoArquivo);
        }

        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(caminhoArquivo))) {

            return (T) input.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erro ao carregar os dados.", e);
        }
    }
}

