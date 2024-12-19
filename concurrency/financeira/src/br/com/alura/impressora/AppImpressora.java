package br.com.alura.impressora;

public class AppImpressora {

    public static void main(String[] args) {
        var impressora1 = new Impressora(1);
        var impressora2 = new Impressora(2);

        var thread1 = new Thread(impressora1);
        var thread2 = new Thread(impressora2);

        thread1.setPriority(Thread.MIN_PRIORITY);
        thread2.setPriority(Thread.MAX_PRIORITY);

        thread1.start();
        thread2.start();
    }

}
