package persistence;

import java.io.*;
import java.nio.file.*;

public class ArquivoPersistence<T extends Serializable> implements Persistencia<T> {

    private final Path caminhoArquivo;
    private final Class<T> tipo;


    public ArquivoPersistence(Path caminhoArquivo, Class<T> tipo) {
        if (caminhoArquivo == null || tipo == null) {
            throw new IllegalArgumentException("Caminho e tipo são obrigatórios.");
        }
        this.caminhoArquivo = caminhoArquivo;
        this.tipo = tipo;
    }

    // Salva um objeto no arquivo
    @Override
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
    @Override
    public T carregar() throws ArquivoNaoEncontradoException {


        if (!Files.exists(caminhoArquivo)) {
            throw new ArquivoNaoEncontradoException("Nenhum arquivo encontrado em: " + caminhoArquivo);
        }

        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(caminhoArquivo))) {

            // cast pelo tipo informado: arquivo com outro objeto dentro já falha aqui
            return tipo.cast(input.readObject());

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erro ao carregar os dados.", e);
        }
    }
}

