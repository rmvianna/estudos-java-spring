package br.com.alura.tarefa;

public class AppTarefa {

    public static void main(String[] args) throws InterruptedException {
        var tarefa = new RealizaTarefa();
        var thread = new Thread(tarefa);

        System.out.println(thread.isAlive());
        thread.start();
        System.out.println(thread.isAlive());
        thread.join();
        System.out.println(thread.isAlive());
    }

}
