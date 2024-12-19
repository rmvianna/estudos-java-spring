package br.com.alura.banco;

import java.math.BigDecimal;

public class AppBanco {

    public static void main(String[] args) {
        var cliente = new Cliente("Joao");
        var conta = new Conta(cliente, new BigDecimal("150"));

        var operacao = new OperacaoSaque(conta, new BigDecimal("150"));

        var saqueJoao = new Thread(operacao);
        var saqueMaria = new Thread(operacao);

        saqueJoao.start();
        saqueMaria.start();

        try {
            saqueJoao.join();
            saqueMaria.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Saldo final: " + conta.getSaldo());
    }

}
