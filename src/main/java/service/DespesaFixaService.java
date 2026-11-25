package service;

import entities.Despesa;
import entities.DespesaFixa;
import repositories.DespesaRepository;

public class DespesaFixaService {
    private final DespesaRepository despesaRepository;

    public DespesaFixaService(DespesaRepository despesaRepository){
        this.despesaRepository = despesaRepository;
    } // Final do método construtor

    public void cadastrar(Despesa despesa){
        try {
            despesaRepository.salvar(despesa);
        } catch (Exception e) {
            System.out.println("parceiro deu erro ai");
        }
    } // Final do método cadastrar

    public void remover(String descricao){
        despesaRepository.remover(descricao);
    } // método remover

    public void alterar(){
}
