package br.com.alura.adopetstore.service;

import br.com.alura.adopetstore.dto.ProdutoDTO;
import br.com.alura.adopetstore.dto.RelatorioEstoque;
import br.com.alura.adopetstore.dto.RelatorioFaturamento;
import br.com.alura.adopetstore.repository.EstoqueRepository;
import br.com.alura.adopetstore.repository.PedidoRepository;
import br.com.alura.adopetstore.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class RelatorioService {
    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    public RelatorioEstoque infoEstoque(){
        var produtosSemEstoque = estoqueRepository.produtosComEstoqueZerado()
                .stream().map(ProdutoDTO::new)
                .collect(Collectors.toList());
        return new RelatorioEstoque(produtosSemEstoque);
    }

    @Async("geradorRelatorio")
    public CompletableFuture<RelatorioEstoque> infoEstoqueAsync(){
        System.out.println("Thread GeradorRelatorio=" + Thread.currentThread().getName());
        var infoEstoque = infoEstoque();
        return CompletableFuture.completedFuture(infoEstoque);
    }

    public RelatorioFaturamento faturamentoObtido() {
        var dataOntem = LocalDate.now().minusDays(1);
        var faturamentoTotal = pedidoRepository.faturamentoTotalDoDia(dataOntem);

        var estatisticas = pedidoRepository.faturamentoTotalDoDiaPorCategoria(dataOntem);

        return new RelatorioFaturamento(faturamentoTotal, estatisticas);
    }

    @Async("geradorRelatorio")
    public CompletableFuture<RelatorioFaturamento> faturamentoObtidoAsync() {
        System.out.println("Thread GeradorRelatorio=" + Thread.currentThread().getName());
        var faturamentoObtido = faturamentoObtido();
        faturamentoObtido = null;
        faturamentoObtido.estatisticas();

        return CompletableFuture.completedFuture(faturamentoObtido);
    }
}