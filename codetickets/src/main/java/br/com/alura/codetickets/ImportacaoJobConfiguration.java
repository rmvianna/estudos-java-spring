package br.com.alura.codetickets;

import org.hibernate.internal.TransactionManagement;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class ImportacaoJobConfiguration {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public Job job(Step passoInicial, JobRepository jobRepository) {
        return new JobBuilder("geracao-tickets", jobRepository)
                .start(passoInicial)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step passoInicial(ItemReader<Importacao> reader,
                             ItemProcessor<Importacao, Importacao> processor,
                             ItemWriter<Importacao> writer,
                             JobRepository jobRepository) {
        return new StepBuilder("passo-inicial", jobRepository)
                .<Importacao, Importacao>chunk(2, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public ItemReader<Importacao> reader() {
        return new FlatFileItemReaderBuilder<Importacao>()
                .name("leitura-csv")
                .resource(new FileSystemResource("files/dados.csv"))
                .comments("--")
                .delimited()
                .delimiter(";")
                .names("cpfCliente", "nomeCliente", "dataNascimentoCliente", "nomeEvento", "dataEvento", "tipoIngresso", "valorEvento")
                .fieldSetMapper(new ImportacaoMapper())
                .build();
    }

    @Bean
    public ItemWriter<Importacao> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Importacao>()
                .dataSource(dataSource)
                .sql("INSERT INTO importacao (cpf_cliente, nome_cliente, data_nascimento_cliente, nome_evento, tipo_ingresso, valor_evento, data_evento, data_hora_importacao, taxa_adm) " +
                        "VALUES (:cpfCliente, :nomeCliente, :dataNascimentoCliente, :nomeEvento, :tipoIngresso, :valorEvento, :dataEvento, :dataHoraImportacao, :taxaAdm)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public ItemProcessor<Importacao, Importacao> processor() {
        return new ItemProcessor<Importacao, Importacao>() {

            @Override
            public Importacao process(Importacao item) {
                if (item.getTipoIngresso().equalsIgnoreCase("vip")) {
                    item.setTaxaAdm(130.0);
                } else if (item.getTipoIngresso().equalsIgnoreCase("camarote")) {
                    item.setTaxaAdm(80.0);
                } else {
                    item.setTaxaAdm(50.0);
                }

                return item;
            }
        };
    }

}
