Teste comparativo pra "segurar" uma requisição HTTP enquanto processos assíncronos são executados

Considerando 200 threads no JMeter, 5 segundos de timeout, sem delay proposital no serviço assíncrono, retornando um CompletableFuture

- Akka: 267,2/sec
- Thread na unha com sleep de 10 ms: 174,6/sec
- Thread na unha com sleep de 5 ms: 225,4/sec
- Thread na unha com sleep de 1 ms: 248,8/sec

Conclusão até o momento: se o processo assíncrono for muito, muito rápido, a solução até que atende. Mas testes com um delay proposital deixaram a solução inviável, aumentando muito o tempo de resposta e dando timeout em quase todas as requisições.
