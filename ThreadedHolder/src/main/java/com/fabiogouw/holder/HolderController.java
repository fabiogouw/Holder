package com.fabiogouw.holder;

import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

@Controller
@RequestMapping("/proxy")
public class HolderController {

    private final long _timeout = 5000;
    private final Map<UUID, DeferredResult<OperationResponse>> _map = new ConcurrentHashMap<>();

    private static final Logger _log = LoggerFactory.getLogger(HolderController.class);

    @RequestMapping(value="wait", method = RequestMethod.POST)
    public @ResponseBody DeferredResult<OperationResponse> wait(@RequestBody OperationRequest operation) {
        UUID waitId = UUID.randomUUID();
        DeferredResult<OperationResponse> result = new DeferredResult<>(_timeout);
        result.onTimeout(() -> {
            _map.remove(waitId);
            _log.info("Limpou por timeout: " + waitId);
        });
        result.onCompletion(() -> {
            _map.remove(waitId);
            _log.info("Limpou: " + waitId);
        });
        _log.info("Add lista: " + waitId);
        _map.put(waitId, result);
        callDoSomething(waitId.toString());
        return result;
    }

    /*
    @RequestMapping(value="wait2", method = RequestMethod.POST)
    public @ResponseBody OperationResponse wait2(@RequestBody OperationRequest operation) {
        UUID waitId = UUID.randomUUID();
        callDoSomething(waitId.toString());            
        int i = _loops;
        OperationResponse waiter = null;
        do {
            try {
                Thread.sleep(_waitTimeout);
            } catch (InterruptedException e) {
            }
            waiter = _map.remove(waitId);
            if(waiter != null) {
                _log.info("Limpou: " + waitId);
            }
            i--;
        }
        while(i > 0 && waiter == null);
        return waiter;
    }
    */

    private void callDoSomething(String waitId) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            RequestConfig config = RequestConfig
            .custom()
            .setConnectTimeout(500)
            .setConnectionRequestTimeout(500)
            .setSocketTimeout(500)
            .build();            
            try {
                HttpPost httpPost = new HttpPost("http://localhost:8001/business/doSomething/" + waitId);
                String json = "{}";
                StringEntity entity = new StringEntity(json);
                httpPost.setConfig(config);
                httpPost.setEntity(entity);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");     
                client.execute(httpPost);
            }
            finally {
                client.close();    
            }
        }
        catch (Exception ex) {

        }
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void scheduleFixedRateWithInitialDelayTask() {
        _log.info("====> Tamanho da lista: " + _map.size());
    }

    @RequestMapping(value="complete/{waitId}", method = RequestMethod.PATCH)
    public @ResponseBody ResponseEntity<Void> complete(@PathVariable UUID waitId, @RequestBody OperationResponse operation) {
        _log.info("Complete recebeu: " + waitId);
        DeferredResult<OperationResponse> waiter = _map.get(waitId);
        if(waiter != null) {
            waiter.setResult(new OperationResponse(UUID.randomUUID().toString()));
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}