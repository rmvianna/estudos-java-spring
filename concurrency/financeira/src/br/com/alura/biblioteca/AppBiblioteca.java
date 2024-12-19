package br.com.alura.biblioteca;

public class AppBiblioteca {

    public static void main(String[] args) {
        var livro = new Livro();

        var leitorJoao = new Leitor("João");
        var leitorMaria = new Leitor("Maria");

        var reservaJoao = new OperacaoReserva(leitorJoao, livro);
        var reservaMaria = new OperacaoReserva(leitorMaria, livro);

        var threadJoao = new Thread(reservaJoao);
        var threadMaria = new Thread(reservaMaria);

        threadJoao.start();
        threadMaria.start();

        try {
            threadJoao.join();
            threadMaria.join();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        System.out.println("Reservas finalizadas");
    }

}
