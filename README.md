# Estudos SpringBatch
Este repositório tem por objetivo armazenar os estudos do framework SpringBatch, utilizado para processamentos em lote na plataforma Java.

Para executar algum projeto, primeiro é necessário acessar a pasta do mesmo e rodar o comando abaixo, que será o responsável por subir os containers necessários. Depois ver como rodar cada aplicativo conforme seção específica

```powershell
docker-compose up -d
```

## CodeTickets
Este aplicativo foi criado à partir dos estudos na Alura. Ele usa um banco PostgreSQL.
Sua ideia é abordar conceitos básicos do SpringBatch, como Job, JobLauncher, Step, Tasklet, ItemReader, ItemProcessor e ItemWriter

Depois de subir o container através do comando acima, pode rodar por linha de comando conforme abaixo. 

```bash
./scripts/start.sh
```

Ou rodar diretamente pelo seu IDE informando as três variáveis de ambiente que o app precisa, conforme exemplo abaixo:

```plaintext
POSTGRES_DB=codetickets_db; POSTGRES_USER=codeticketsowner; POSTGRES_PASSWORD=q1w2e3
```
