package br.com.alura.codetickets;

import org.hibernate.internal.TransactionManagement;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class ImportacaoJobConfiguration {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public Job job(Step passoInicial, Step moverArquivoStep, JobRepository jobRepository) {
        return new JobBuilder("geracao-tickets", jobRepository)
                .start(passoInicial)
                .next(moverArquivoStep)
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
    public Step moverArquivoStep(Tasklet moverArquivoTasklet,
            JobRepository jobRepository) {
        return new StepBuilder("mover-arquivo", jobRepository)
                .tasklet(moverArquivoTasklet, transactionManager)
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

    @Bean
    public Tasklet moverArquivoTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Path dirImportacao = Paths.get("files", "importado");

                if (Files.notExists(dirImportacao)) {
                    Files.createDirectory(dirImportacao);
                }

                String horaImportacao = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmSS"));

                Path arquivo = Paths.get("files", "dados.csv");
                Path arquivoDestino = Paths.get("files", "importado", "dados-" + horaImportacao + ".csv");

                Files.copy(arquivo, arquivoDestino);

                return RepeatStatus.FINISHED;
            }
        };
    }

}
