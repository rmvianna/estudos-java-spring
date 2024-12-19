package br.com.alura.banco;

import java.math.BigDecimal;

public class OperacaoSaque implements Runnable {

    private Conta conta;
    private BigDecimal valor;

    public OperacaoSaque(Conta conta, BigDecimal valor) {
        this.conta = conta;
        this.valor = valor;
    }

    @Override
    public void run() {
        System.out.println("Iniciando operação de saque.");

        synchronized (this) {
            var saldoAtual = conta.getSaldo();

            if (saldoAtual.compareTo(valor) >= 0) {
                System.out.println("Debitando valor da conta");
                conta.debitaSaldo(valor);
                System.out.println("Saldo atual: " + conta.getSaldo());
            }
        }

        System.out.println("Finalizando operação de saque.");
    }
}
