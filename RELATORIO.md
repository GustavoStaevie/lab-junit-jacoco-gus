# Relatório do Laboratório: JUnit 5

**Dupla:** Gustavo Zanotto Staevie
**Link do fork:** [_https://github.com/..._](https://github.com/GustavoStaevie/lab-junit-jacoco-gus)

## 1. Resultado final

Cole aqui o resumo que aparece no final de `java scripts/Rodar.java`:

```
Test run finished after 143 ms
[         7 containers found      ]
[         0 containers skipped    ]
[         7 containers started    ]
[         0 containers aborted    ]
[         7 containers successful ]
[         0 containers failed     ]
[        32 tests found           ]
[         0 tests skipped         ]
[        32 tests started         ]
[         0 tests aborted         ]
[        32 tests successful      ]
[         0 tests failed          ]
```

## 2. Nível 1: `if`s de `Conta`

Para cada `if` de `Conta.java`, diga qual teste faz o `if` ser **verdadeiro** e qual faz ser **falso**
(escreva o nome do método de teste).

| `if` (condição) | Teste que torna **verdadeiro** | Teste que torna **falso** |
|---|---|---|
| saldo inicial negativo (construtor) | contaComSaldoInicialNegativoDeveComecarComSaldoZero | contaNovaDeveGuardarTitularESaldoInicial |
| valor do depósito `<= 0` | depositoComValorNegativoDeveSerRecusado | depositoValidoDeveAumentarOSaldo |
| valor do saque `<= 0` | saqueComValorNegativoDeveSerRecusado|saqueValidoDeveDiminuirOSaldo |
| saque maior que o saldo |saqueMaiorQueOSaldoDeveSerRecusado | saqueValidoDeveDiminuirOSaldo,saqueDoSaldoExatoDeveSerPermitidoEZerarAConta|

## 3. Nível 2: regras de `ServicoTransferencia`

| Regra | Teste(s) que a exercita(m) |
|---|---|
| R1 mesma conta |mesmaContaEValorInvalidoDevemSerRecusados |
| R2 valor <= 0 |mesmaContaEValorInvalidoDevemSerRecusados |
| R3 acima do limite de 5.000 (e 5.000 exatos permitido) | transferenciaAcimaDoLimiteDeveSerRecusada|
| R4 saldo insuficiente para valor + tarifa (nada é alterado); tarifa de 1% acima de 1.000 (1.000 exatos sem tarifa) | saldoInsuficienteParaValorMaisTarifaNaoAlteraNenhumaConta, transferenciaAcimaDe1000PagaTarifaDe1Porcento, transferenciaDeExatamente1000NaoPagaTarifa|
| R5 transferência OK: saldos da origem e do destino |transferenciaPequenaDeveSerAceitaSemTarifa, transferenciaAcimaDe1000PagaTarifaDe1Porcento, transferenciaDeExatamente1000NaoPagaTarifa |

## 4. Nível 3: regras de `AnalisadorEmprestimo`

| Regra | Cenário escolhido (entradas → decisão esperada) | Método de teste |
|---|---|---|
| 1. entradas inválidas → DADOS_INVALIDOS (cada uma das 6 condições) | score -1 e 1001; renda 0 e -100; valor 0 e -500; parcelas 0 e 361 → DADOS_INVALIDOS. Limites válidos: score 0 e 1000, parcelas 1 e 360 → não são recusados | dadosInvalidosDevemSerRecusados (parametrizado, 8 casos), limitesValidosDeScoreEParcelasSaoAceitos |
| 2. score < 300 → NEGADO | score 299 → NEGADO; score 300 → ANALISE_MANUAL | scoreAbaixoDe300DeveSerNegado, scoreExatamente300NaoENegadoPelaRegra2 |
| 3. taxa por faixa de score (800 / 600) | 800 → 12%; 799 → 24%; 600 → 24%; 599 → 36% | taxaAnualDependeDaFaixaDeScore |
| 4. parcela (Price) | valor 10.000, taxa 1% a.m., 12x → parcela 888,49 | parcelaSegueATabelaPrice |
| 5. comprometimento > 50% → NEGADO; > 30% → ANÁLISE MANUAL | score 800, valor 10.000, 12x: renda 1.000 (88,8%) → NEGADO; renda 2.000 (44,4%) → ANALISE_MANUAL | comprometimentoAcimaDe50PorcentoDeveSerNegado, comprometimentoEntre30E50PorcentoVaiParaAnaliseManual |
| 6. comprometimento <= 30%: score >= 600 aprovado; abaixo, análise manual | score 800, renda 3.000 (29,6%) → APROVADO; score 600, renda 4.000 (23,6%) → APROVADO; score 599, renda 4.000 (25,1%) → ANALISE_MANUAL | comprometimentoBaixoComScoreAltoDeveSerAprovado, scoreExatamente600ComComprometimentoBaixoDeveSerAprovado, score599ComComprometimentoBaixoVaiParaAnaliseManual |

## 5. Reflexão (2 a 4 linhas cada)

1. **Os dois lados do `if`:** No começo ficaram sem o lado verdadeiro o `if (saldoInicial < 0)` do construtor e o `if (valor <= 0)` do `sacar`. Sem o teste do saque negativo, se removesse essa verificação, `sacar(-50)` faria `saldo -= -50` e aumentaria o saldo. Fiz dois testes extras pra isso.

2. **Valor-limite:** Eh mais importante o teste de 1.000,00 exatos. Se o código usa `valor >= FAIXA_ISENTA`, uma transferência de 1.000 cobraria 10 de tarifa e a origem ficaria com 8.990 em vez de 9.000.

3. **Nível 3:** escolhi um valor de cada lado da fronteira (299/300, 599/600, 799/800, 0/1000, 1/360). Para o comprometimento usei classes de equivalência (abaixo de 30%, entre 30% e 50%, acima de 50%), calculando a parcela na mão e variando só a renda para isolar a regra. As entradas inválidas ficaram num `@ParameterizedTest`, cada linha quebrando uma única condição. Os limites exatos de 30% e 50% não foram testados, porque é difícil achar entradas que deem exatamente esses valores com a fórmula Price.

4. **Teste que passa mas não protege:** Um teste que cria `new Conta("Ana", 100.0)`, chama `conta.sacar(30.0)` e termina sem nenhum `assert`. Ele sempre passa, mesmo que `sacar` subtraia o valor errado ou nem altere o saldo, porque nada é verificado. Só falharia se o método lançasse uma exceção.

5. **Divisão do trabalho:** Trabalho feito individualmente por mim.
