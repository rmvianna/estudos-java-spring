package br.com.alura.adopetstore.email;

import br.com.alura.adopetstore.dto.RelatorioEstoque;
import br.com.alura.adopetstore.dto.RelatorioFaturamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailRelatorio {
    @Autowired
    private EnviadorEmail enviador;

    public void enviar(RelatorioFaturamento faturamento, RelatorioEstoque estoque){
        enviador.enviarEmail(
                "Relatorios da Adopet Store",
                "admin@email.com.br",
                """
                Olá, seguem abaixo os relatórios de faturamento e estoque do dia anterior
                
                %s
                
                %s
                """.formatted(faturamento, estoque));
    }
}
