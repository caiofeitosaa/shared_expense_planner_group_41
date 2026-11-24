package persistence;

import java.io.Serial;

public class ArquivoNaoEncontradoException extends PersistenciaException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ArquivoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}