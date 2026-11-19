package persistence;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ArquivoPersistence<T extends Serializable> {

    private final String caminhoArquivo;

    public ArquivoPersistence(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    // Salva uma lista de objetos no arquivo
    public void salvar(T dados) {

        try {
            Path caminho = Paths.get(caminhoArquivo);

            Files.createDirectories(caminho.toAbsolutePath().getParent());

            try (ObjectOutputStream output = new ObjectOutputStream(
                    new FileOutputStream(caminhoArquivo))) {

                output.writeObject(dados);
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar os dados.", e);
        }
    }

    // Recupera os objetos armazenados
    @SuppressWarnings("unchecked")
    public T carregar() throws ArquivoNaoEncontradoException {

        File arquivo = new File(caminhoArquivo);

        if (!arquivo.exists()) {
            throw new ArquivoNaoEncontradoException("Nenhum arquivo encontrado em: " + caminhoArquivo);
        }

        try (ObjectInputStream input = new ObjectInputStream(
                new FileInputStream(arquivo))) {

            return (T) input.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erro ao carregar os dados.", e);
        }
    }
}

