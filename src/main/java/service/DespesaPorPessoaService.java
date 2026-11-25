package service;

import repositories.DespesaRepository;

// Regras da despesa por pessoa (cada participante paga o valor unitário).
// Base da classe: segue o mesmo modelo do DespesaFixaService, usando o DespesaService
// pras regras gerais. Falta fazer cadastrar, alterar e remover.
public class DespesaPorPessoaService {
    private final DespesaService despesaService;

    public DespesaPorPessoaService(DespesaRepository despesaRepository){
        this.despesaService = new DespesaService(despesaRepository);
    } // Final do método construtor
}
