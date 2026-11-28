package util;

import java.util.Locale;

// Formata e converte os valores em reais. Deixei fixo no padrão BR (1.234,56)
// porque num pc em inglês o String.format trocava ponto e vírgula
public final class Moeda {

    private static final Locale BR = Locale.forLanguageTag("pt-BR");

    private Moeda() {
    }

    // 7050 -> "R$ 70,50"
    public static String formatar(int valorCent) {
        return String.format(BR, "R$ %,.2f", valorCent / 100.0);
    }

    // aceita "70", "70,5", "1.234,56" e até "R$ 70,50" (quando vem da tabela)
    public static int paraCentavos(String valor) {
        if (valor == null) {
            throw new NumberFormatException("Valor vazio");
        }
        String limpo = valor.replace("R$", "").replace(" ", "").replace(" ", "").trim();
        if (limpo.isEmpty()) {
            throw new NumberFormatException("Valor vazio");
        }
        if (limpo.contains(",")) {
            // ponto é milhar e vírgula é decimal
            limpo = limpo.replace(".", "").replace(",", ".");
        }
        double numero = Double.parseDouble(limpo);
        if (numero < 0) {
            throw new NumberFormatException("Valor negativo");
        }
        return (int) Math.round(numero * 100);
    }
}
