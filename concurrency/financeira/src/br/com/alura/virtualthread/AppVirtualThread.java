package br.com.alura.virtualthread;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppVirtualThread {

    private static final int FIXED_THREAD_POOL_SIZE = 10000;
    private static final int NUMERO_TAREFAS = FIXED_THREAD_POOL_SIZE;

    private enum TipoExecutor {
        VIRTUAL_THREAD_PER_TASK,
        FIXED_THREAD_POOL
    }

    public static void main(String[] args) {
        TipoExecutor tipoExecutor = TipoExecutor.VIRTUAL_THREAD_PER_TASK;
        Instant inicio = Instant.now();

        try (ExecutorService e = newExecutorService(tipoExecutor)) {
            for (int i = 0; i < NUMERO_TAREFAS; i++) {
                e.submit(() -> {
                    System.out.println("Executando tarefa com a thread: " + Thread.currentThread());
                });
            }
        }

        Instant fim = Instant.now();

        Duration duracao = Duration.between(inicio, fim);

        System.out.printf("Duração com %s: %dms%n", tipoExecutor, duracao.toMillis());
    }

    private static ExecutorService newExecutorService(TipoExecutor tipoExecutor) {
        switch (tipoExecutor) {
            case VIRTUAL_THREAD_PER_TASK:
                return Executors.newVirtualThreadPerTaskExecutor();
            case FIXED_THREAD_POOL:
                return Executors.newFixedThreadPool(FIXED_THREAD_POOL_SIZE);
        }

        throw new RuntimeException("Tipo de executor não suportado");
    }

}
