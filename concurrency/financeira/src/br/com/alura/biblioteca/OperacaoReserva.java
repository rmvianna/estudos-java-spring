package br.com.alura.biblioteca;

public class OperacaoReserva implements Runnable {

    private Leitor leitor;
    private Livro livro;

    public OperacaoReserva(Leitor leitor, Livro livro) {
        this.leitor = leitor;
        this.livro = livro;
    }

    @Override
    public void run() {
        System.out.println("Iniciando reserva");

        synchronized (livro) {
            if (livro.isReservado()) {
                System.out.println("O livro já foi reservado, " + leitor.getNome());
            } else {
                livro.setReservado(true);
                System.out.println("Reserva efetuada com sucesso! Aproveite a leitura, " + leitor.getNome());
            }
        }
    }
}
