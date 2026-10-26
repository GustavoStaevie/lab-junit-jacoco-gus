package br.ufrgs.inf.lab.nivel3;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AnalisadorEmprestimoTest {

    private static final double DELTA = 0.01;

    private AnalisadorEmprestimo analisador;

    @BeforeEach
    void preparar() {
        analisador = new AnalisadorEmprestimo();
    }

    // Regra 1

    @ParameterizedTest
    @CsvSource({
        "-1,   5000, 10000, 12",
        "1001, 5000, 10000, 12",
        "800,  0,    10000, 12",
        "800,  -100, 10000, 12",
        "800,  5000, 0,     12",
        "800,  5000, -500,  12",
        "800,  5000, 10000, 0",
        "800,  5000, 10000, 361"
    })
    void dadosInvalidosDevemSerRecusados(int score, double renda, double valor, int parcelas) {
        DecisaoEmprestimo decisao = analisador.analisar(score, renda, valor, parcelas);

        assertEquals(DecisaoEmprestimo.DADOS_INVALIDOS, decisao);
    }

    @Test
    void limitesValidosDeScoreEParcelasSaoAceitos() {
        assertNotEquals(DecisaoEmprestimo.DADOS_INVALIDOS, analisador.analisar(0, 5000, 10000, 12));
        assertNotEquals(DecisaoEmprestimo.DADOS_INVALIDOS, analisador.analisar(1000, 10000, 1000, 1));
        assertNotEquals(DecisaoEmprestimo.DADOS_INVALIDOS, analisador.analisar(1000, 10000, 1000, 360));
    }

    // Regra 2

    @Test
    void scoreAbaixoDe300DeveSerNegado() {
        assertEquals(DecisaoEmprestimo.NEGADO, analisador.analisar(299, 10000, 1000, 12));
    }

    @Test
    void scoreExatamente300NaoENegadoPelaRegra2() {
        assertEquals(DecisaoEmprestimo.ANALISE_MANUAL, analisador.analisar(300, 10000, 1000, 12));
    }

    // Regra 3

    @Test
    void taxaAnualDependeDaFaixaDeScore() {
        assertEquals(0.12, analisador.taxaAnual(800), 0.0001);
        assertEquals(0.24, analisador.taxaAnual(799), 0.0001);
        assertEquals(0.24, analisador.taxaAnual(600), 0.0001);
        assertEquals(0.36, analisador.taxaAnual(599), 0.0001);
    }

    // Regra 4

    @Test
    void parcelaSegueATabelaPrice() {
        double parcela = analisador.calcularParcela(10000, 0.01, 12);

        assertEquals(888.49, parcela, DELTA);
    }

    // Regra 5

    @Test
    void comprometimentoAcimaDe50PorcentoDeveSerNegado() {
        assertEquals(DecisaoEmprestimo.NEGADO, analisador.analisar(800, 1000, 10000, 12));
    }

    @Test
    void comprometimentoEntre30E50PorcentoVaiParaAnaliseManual() {
        assertEquals(DecisaoEmprestimo.ANALISE_MANUAL, analisador.analisar(800, 2000, 10000, 12));
    }

    // Regra 6

    @Test
    void comprometimentoBaixoComScoreAltoDeveSerAprovado() {
        assertEquals(DecisaoEmprestimo.APROVADO, analisador.analisar(800, 3000, 10000, 12));
    }

    @Test
    void scoreExatamente600ComComprometimentoBaixoDeveSerAprovado() {
        assertEquals(DecisaoEmprestimo.APROVADO, analisador.analisar(600, 4000, 10000, 12));
    }

    @Test
    void score599ComComprometimentoBaixoVaiParaAnaliseManual() {
        assertEquals(DecisaoEmprestimo.ANALISE_MANUAL, analisador.analisar(599, 4000, 10000, 12));
    }
}
