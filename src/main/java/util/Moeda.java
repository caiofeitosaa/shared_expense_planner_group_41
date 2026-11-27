package util;

import java.math.BigDecimal;

// Converte entre o texto da tela ("20,50") e centavos (2050). O programa todo guarda em centavos
public class Moeda {

    private Moeda() {
    }

    // 7500 -> "75,00"
    public static String formatar(int centavos) {
        return new BigDecimal(centavos).movePointLeft(2).toPlainString().replace('.', ',');
    }

    // "20,50", "20.50" ou "20" -> 2050. Texto inválido lança NumberFormatException
    // shortcut: não aceita separador de milhar ("1.000,00"), se precisar tratar aqui
    public static int paraCentavos(String texto) {
        try {
            return new BigDecimal(texto.trim().replace(',', '.')).movePointRight(2).intValueExact();
        } catch (ArithmeticException e) {
            throw new NumberFormatException("Use no máximo 2 casas decimais.");
        }
    }
}
