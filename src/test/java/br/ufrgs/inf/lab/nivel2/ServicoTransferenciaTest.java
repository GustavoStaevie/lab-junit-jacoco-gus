package br.ufrgs.inf.lab.nivel2;

import static org.junit.jupiter.api.Assertions.*;

import br.ufrgs.inf.lab.nivel1.Conta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * NÍVEL 2 (semi-guiado): ServicoTransferencia.
 *
 * Aqui NÃO há os asserts: cada teste abaixo diz o CENÁRIO e o que deve
 * ser verificado. Você decide como escrever (assertEquals, assertTrue...).
 * Leia as regras R1..R5 em ServicoTransferencia.java.
 *
 * Remova o @Disabled e o fail(...) de cada teste à medida que o implementar.
 * Sinta-se livre para ACRESCENTAR testes (e usar @ParameterizedTest).
 */
class ServicoTransferenciaTest {

    private static final double DELTA = 0.001;

    private ServicoTransferencia servico;
    private Conta origem;
    private Conta destino;

    // Roda ANTES de cada teste: garante contas "limpas" (testes independentes)
    @BeforeEach
    void preparar() {
        servico = new ServicoTransferencia();
        origem = new Conta("Origem", 10_000.0);
        destino = new Conta("Destino", 0.0);
    }

    // ---------------------------------------------------------------
    // PRONTO (exemplo)
    // ---------------------------------------------------------------

    @Test
    void transferenciaPequenaDeveSerAceitaSemTarifa() {
        StatusTransferencia status = servico.transferir(origem, destino, 200.0);

        assertEquals(StatusTransferencia.OK, status);
        assertEquals(9_800.0, origem.getSaldo(), DELTA);
        assertEquals(200.0, destino.getSaldo(), DELTA);
    }

    // ---------------------------------------------------------------
    // PARA VOCÊ ESCREVER
    // ---------------------------------------------------------------

    @Test
    void transferenciaAcimaDe1000PagaTarifaDe1Porcento() {
        // DADO   origem com 10.000 e destino com 0
        StatusTransferencia status = servico.transferir(origem, destino, 2000.0);
        // QUANDO transfere 2.000
        // ENTÃO  status = ?  | calcularTarifa(2000) = ?  | saldo da origem = ?  | saldo do destino = ?
        //        (calcule os valores esperados na mão ANTES de rodar!)
        assertEquals(StatusTransferencia.OK, status);
        assertEquals(7980.0, origem.getSaldo(), DELTA);
        assertEquals(2000.0, destino.getSaldo(), DELTA);
    }

    @Test

    void transferenciaDeExatamente1000NaoPagaTarifa() {
        // Valor-limite da regra R4: "ACIMA de 1.000" ... e 1.000 exatos?
        // Verifique o status, a tarifa e os saldos.
        StatusTransferencia status = servico.transferir(origem, destino, 1000.0);   
        assertEquals(StatusTransferencia.OK, status);
        assertEquals(9000.0, origem.getSaldo(),DELTA);
        assertEquals(1000.0, destino.getSaldo(),DELTA);

    }

    @Test

    void transferenciaAcimaDoLimiteDeveSerRecusada() {
        // Qual status? Qual o valor mínimo que ultrapassa o limite (R3)?
        // E um teste com EXATAMENTE o limite: o que deveria acontecer?
        // Verifique também que nenhuma conta foi alterada
        //cenario acima do limite
        StatusTransferencia status = servico.transferir(origem, destino, 5000.01);
        assertEquals(StatusTransferencia.ACIMA_DO_LIMITE, status);
        assertEquals(10_000.0, origem.getSaldo(), DELTA);
        assertEquals(0.0, destino.getSaldo(), DELTA);

        // Cenário 2: exatamente no limite (5000.00) -> permitido
        Conta origem2 = new Conta("Origem2", 10_000.0);
        Conta destino2 = new Conta("Destino2", 0.0);

        StatusTransferencia status2 = servico.transferir(origem2, destino2, 5000.00);

        assertEquals(StatusTransferencia.OK, status2);
        assertEquals(4_950.0, origem2.getSaldo(), DELTA);
        assertEquals(5_000.0, destino2.getSaldo(), DELTA);
    }

    @Test
    void saldoInsuficienteParaValorMaisTarifaNaoAlteraNenhumaConta() {
        // DICA: crie uma conta de origem com saldo suficiente para o VALOR,
        //       mas NÃO suficiente para VALOR + TARIFA (ex.: saldo 2.000, valor 2.000).
        // Verifique o status e que origem E destino mantêm os saldos originais.
        Conta origemPobre = new Conta("Origem", 2000.0);

        StatusTransferencia status = servico.transferir(origemPobre, destino, 2000.0);

        assertEquals(StatusTransferencia.SALDO_INSUFICIENTE, status);
        assertEquals(2000.0, origemPobre.getSaldo(), DELTA);
        assertEquals(0.0, destino.getSaldo(), DELTA);
    }

    @Test
    void mesmaContaEValorInvalidoDevemSerRecusados() {
        // R1: mesma conta
        StatusTransferencia status1 = servico.transferir(origem, origem, 100.0);
        assertEquals(StatusTransferencia.MESMA_CONTA, status1);
        assertEquals(10_000.0, origem.getSaldo(), DELTA);

        // R2: valor zero
        StatusTransferencia status2 = servico.transferir(origem, destino, 0.0);
        assertEquals(StatusTransferencia.VALOR_INVALIDO, status2);
        assertEquals(10_000.0, origem.getSaldo(), DELTA);
        assertEquals(0.0, destino.getSaldo(), DELTA);

        // R2: valor negativo
        StatusTransferencia status3 = servico.transferir(origem, destino, -50.0);
        assertEquals(StatusTransferencia.VALOR_INVALIDO, status3);
        assertEquals(10_000.0, origem.getSaldo(), DELTA);
        assertEquals(0.0, destino.getSaldo(), DELTA);
    }
}
