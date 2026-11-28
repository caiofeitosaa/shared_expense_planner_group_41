package persistence;

// Contrato de salvar e carregar. Repositories e controller só conhecem esta interface,
// quem grava de verdade no arquivo é o ArquivoPersistence
public interface Persistencia<T> {

    void salvar(T dados);

    // Arquivo ausente lança ArquivoNaoEncontradoException; arquivo inválido lança RuntimeException
    T carregar() throws ArquivoNaoEncontradoException;
}
