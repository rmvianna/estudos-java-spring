# Estudos SpringBatch
Este repositório tem por objetivo armazenar os estudos do framework SpringBatch, utilizado para processamentos em lote na plataforma Java.

Para executar, basta rodar o comando abaixo, que será o responsável por subir os containers necessários

```powershell
docker-compose up -d
```

## CodeTickets
Este aplicativo foi criado à partir dos estudos na Alura. Ele usa um banco PostgreSQL.
Depois que rodou a primeira vez o comando acima, para subir os containers, basta rodar o programa diretamente pelo seu IDE informando as três variáveis de ambiente que o app precisa, conforme exemplo abaixo:

```plaintext
DB_URL=jdbc:postgresql://localhost/codetickets_db; DB_USER=codeticketsowner; DB_PASS=q1w2e3
```
