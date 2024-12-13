package br.com.alura.codetickets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImportacaoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportacaoController.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @PostMapping("/api/v1/importacao")
    public ResponseEntity<String> novaImportacao() {
        JobParameters jobParameters = new JobParametersBuilder()
                .toJobParameters();

        try {
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            return ResponseEntity.ok(jobExecution.toString());
        } catch (JobExecutionException e) {
            LOGGER.error("Falha na execucao do job", e);
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

}
