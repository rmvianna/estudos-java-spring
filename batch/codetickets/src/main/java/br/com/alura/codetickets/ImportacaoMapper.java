package br.com.alura.codetickets;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImportacaoMapper implements FieldSetMapper<Importacao> {

    private static final DateTimeFormatter ANO_MES_DIA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Importacao mapFieldSet(FieldSet fieldSet) throws BindException {
        Importacao importacao = new Importacao();
        importacao.setCpfCliente(fieldSet.readString("cpfCliente"));
        importacao.setNomeCliente(fieldSet.readString("nomeCliente"));
        importacao.setDataNascimentoCliente(LocalDate.parse(fieldSet.readString("dataNascimentoCliente"), ANO_MES_DIA));
        importacao.setTipoIngresso(fieldSet.readString("tipoIngresso"));
        importacao.setNomeEvento(fieldSet.readString("nomeEvento"));
        importacao.setDataEvento(LocalDate.parse(fieldSet.readString("dataEvento"), ANO_MES_DIA));
        importacao.setValorEvento(fieldSet.readDouble("valorEvento"));
        importacao.setDataHoraImportacao(LocalDateTime.now());

        return importacao;
    }
}
