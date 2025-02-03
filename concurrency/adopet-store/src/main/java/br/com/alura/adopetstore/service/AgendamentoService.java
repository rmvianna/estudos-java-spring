package br.com.alura.adopetstore.service;

import br.com.alura.adopetstore.email.EmailRelatorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class AgendamentoService {

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private EmailRelatorio enviadorEmail;

    @Scheduled(cron = "0 38 8 * * *") //Enviara as 8h17min0seg de todos os dias do ano
    public void enviarRelatorios() {
        var infoEstoque = relatorioService.infoEstoqueAsync();
        var faturamentoObtido = relatorioService.faturamentoObtidoAsync();

        try {
            CompletableFuture.allOf(infoEstoque, faturamentoObtido).join();
            enviadorEmail.enviar(faturamentoObtido.resultNow(), infoEstoque.getNow(null));
        } catch (CancellationException ex) {
            System.err.println("Tarefa de geracao de relatorios cancelada: " + ex.getMessage());
            ex.printStackTrace(System.err);
        } catch (CompletionException ex) {
            System.err.println("Falha ao obter informacoes de relatorios para disparo por email: " + ex.getMessage());
            ex.printStackTrace(System.err);
        }

        System.out.println("Thread Relatorio=" + Thread.currentThread().getName());
    }

}
