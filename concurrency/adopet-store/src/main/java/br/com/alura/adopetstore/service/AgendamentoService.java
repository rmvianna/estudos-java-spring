package br.com.alura.adopetstore.service;

import br.com.alura.adopetstore.dto.RelatorioEstoque;
import br.com.alura.adopetstore.dto.RelatorioFaturamento;
import br.com.alura.adopetstore.email.EmailRelatorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AgendamentoService {

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private EmailRelatorio enviadorEmail;

    @Scheduled(cron = "0 55 8 * * *") //Enviara as 8h17min0seg de todos os dias do ano
    public void enviarRelatorios() {
        var infoEstoque = relatorioService.infoEstoque();
        var faturamentoObtido = relatorioService.faturamentoObtido();

        enviadorEmail.enviar(faturamentoObtido, infoEstoque);
        System.out.println("Thread Relatorio=" + Thread.currentThread().getName());
    }

    @Async("geradorRelatorio")
    public CompletableFuture<RelatorioEstoque> getInfoEstoque() {
        return CompletableFuture.completedFuture(relatorioService.infoEstoque());
    }

    @Async("geradorRelatorio")
    public CompletableFuture<RelatorioFaturamento> getFaturamentoObtido() {
        return CompletableFuture.completedFuture(relatorioService.faturamentoObtido());
    }

}
