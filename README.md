# Cabe no Bolso: planejador de despesas compartilhadas

Projeto final de Programação Orientada a Objetos (Grupo 41).

Sabe aquele passeio em que todo mundo racha o transporte, só metade do grupo vai na atividade e cada um paga o próprio almoço? No fim ninguém sabe direito quanto deve, nem se cabe no bolso. Este programa resolve isso: você cadastra quem vai e o que vai ser gasto, e ele mostra quanto cada pessoa paga e quem passou do orçamento.

O programa é uma janela feita em Java Swing, com três abas: **Participantes**, **Despesas** e **Resumo**.

---

## O que você precisa

- **JDK 25 ou mais novo** (o `pom.xml` compila para Java 25). Para conferir, rode `java -version`.
- Nenhuma biblioteca externa. É só Java puro, então não precisa baixar dependências.
- O Maven é opcional. Os comandos abaixo usam só `javac` e `java`.

## Como baixar

```bash
git clone https://github.com/caiofeitosaa/shared_expense_planner_group_41.git
cd shared_expense_planner_group_41
```

## Como compilar

Rode o comando na pasta raiz do projeto. Ele compila o programa e os testes para a pasta `out`.

**Windows (PowerShell):**
```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse -Filter *.java src).FullName
```

**Git Bash, Linux ou macOS:**
```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
```

O `-encoding UTF-8` é importante, porque sem ele os acentos das mensagens saem quebrados no Windows.

## Como executar

```bash
java -cp out controller.MainController
```

Pelo VS Code ou IntelliJ, também dá para abrir `src/main/java/controller/MainController.java` e clicar em **Run** no método `main`. O `MainController` monta tudo e abre a janela já com os botões funcionando.

### Usando o programa

1. **Participantes:** preencha código, nome e orçamento (ex.: `70,00`) e clique em **Cadastrar**. Se deixar o código vazio, o programa usa o próximo número livre. Para mudar nome ou orçamento, clique na pessoa na tabela, edite e clique em **Alterar**.
2. **Despesas:** escreva a descrição e o valor e escolha o tipo:
   - **Fixa:** o valor total é dividido entre quem participa (ex.: transporte de R$ 90,00 para 3 pessoas).
   - **Por pessoa:** cada participante paga o valor informado (ex.: almoço de R$ 20,00 cada).

   Depois selecione os participantes na lista (segure **Ctrl** para marcar mais de um) e clique em **Incluir**. Para editar ou excluir, clique na despesa na tabela.
3. **Resumo:** mostra cada despesa, o custo total e quanto cada pessoa vai pagar, com a situação ("Dentro do orçamento" ou "Excede em R$ X").

### Onde os dados ficam salvos

- Toda mudança é gravada sozinha em `dados/planejamento.dat`. Quando você fecha e abre o programa de novo, tudo volta como estava.
- Na aba Resumo, **Salvar planejamento** grava uma cópia num arquivo que você escolhe, e **Carregar planejamento** abre um arquivo salvo.
- Se o arquivo escolhido não existir ou estiver corrompido, aparece uma mensagem e o planejamento que estava aberto continua igual.
- Para começar um planejamento vazio, é só apagar `dados/planejamento.dat` com o programa fechado.

---

## Organização das pastas

O projeto segue o padrão MVC: a tela só mostra e recebe dados, o controller encaminha as ações e o service confere as regras e faz as contas.

```
src/main/java/
├── view/          Telas em Swing (janela principal e as três abas)
├── controller/    Liga os botões da tela aos services e formata os valores. Ponto de entrada: MainController
├── service/       Regras do trabalho: validações, rateio e resumo
├── entities/      Participante, Despesa (abstrata), DespesaFixa e DespesaPorPessoa
├── repositories/  Guardam e buscam participantes e despesas na memória
├── persistence/   Interface Persistencia e a classe ArquivoPersistence (serialização)
└── util/          Moeda: converte "20,50" em centavos e vice-versa

src/test/java/
├── service/       Testes das regras e do caso de referência
└── persistence/   Testes de salvar e carregar arquivo
```

Algumas decisões que valem explicar:

- **Os valores são guardados em centavos (`int`)**, nunca em `double`, para não aparecer R$ 33,330000001. Na despesa fixa, os centavos que sobram vão para os menores códigos: R$ 100,00 para três pessoas fica 33,34 / 33,33 / 33,33.
- **`Despesa` é abstrata** e cada tipo sobrescreve `calcularRateio()`. O resumo trata todas como `Despesa` e cada uma calcula do seu jeito (polimorfismo).
- **Os repositories dependem da interface `Persistencia`**, não da classe de arquivo. Se um dia quisermos salvar em outro lugar, basta criar outra implementação.

---

## Como reproduzir os testes

Os testes são classes com `main` e usam `assert` (o projeto não usa JUnit). Por isso **o `-ea` é obrigatório**: sem ele os `assert` são ignorados e o teste passa sem conferir nada.

Depois de compilar:

```bash
java -ea -cp out service.TesteResumoService
java -ea -cp out service.TesteDespesaFixaService
java -ea -cp out service.TesteDespesaPorPessoaService
java -ea -cp out service.TesteParticipanteService
```

Cada um termina com `tudo certo` quando passa. Se algo der errado, aparece um `AssertionError` com a linha do problema.

### Roteiro manual pela interface (caso de referência)

Se preferir ver funcionando na tela, abra o programa com `dados/planejamento.dat` apagado e cadastre:

**Participantes**

| Código | Nome | Orçamento |
|---|---|---|
| 1 | Ana | 70,00 |
| 2 | Bruno | 60,00 |
| 3 | Carla | 70,00 |

**Despesas**

| Descrição | Valor | Tipo | Participantes |
|---|---|---|---|
| Transporte | 90,00 | Fixa | Ana, Bruno, Carla |
| Almoço | 20,00 | Por pessoa | Ana, Bruno, Carla |
| Atividade recreativa | 30,00 | Fixa | Ana, Carla |
| Sobremesa | 10,00 | Por pessoa | Ana, Bruno, Carla |

Na aba **Resumo** deve aparecer custo total de **R$ 210,00**, Ana e Carla com "Excede em R$ 5,00" e Bruno "Dentro do orçamento" (60,00 de 60,00, total igual ao limite conta como dentro).

Depois disso:

- **Alteração de orçamento:** na aba Participantes, mude o orçamento da Ana para `80,00`. No Resumo ela continua pagando 75,00, agora dentro do orçamento.
- **Exclusão de despesa:** na aba Despesas, exclua "Atividade recreativa". O total cai para R$ 180,00, com 60,00 para cada um.
- **Salvar e reiniciar:** feche o programa e abra de novo. Tudo volta igual, sem nada repetido.
- **Exclusão bloqueada:** tente excluir um participante que está em alguma despesa. O programa recusa e diz em qual despesa ele está.
