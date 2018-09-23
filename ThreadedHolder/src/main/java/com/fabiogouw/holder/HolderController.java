package com.fabiogouw.holder;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/proxy")
public class HolderController {

    private final int _timeout = 5000;
    private final int _waitTimeout = 100;
    private final int _loops = _timeout / _waitTimeout;
    private final Map<UUID, OperationResponse> _map = new ConcurrentHashMap<>();

    private static final Logger _log = LoggerFactory.getLogger(HolderController.class);

    @RequestMapping(value="wait", method = RequestMethod.POST)
    public @ResponseBody CompletableFuture<OperationResponse> wait(@RequestBody OperationRequest operation) {
        UUID waitId = UUID.randomUUID();
        CompletableFuture<OperationResponse> future = CompletableFuture.supplyAsync(() -> { 
            // call other service and pass the waitId...
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
        });
        return future;
    }

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

    private void callDoSomething(String waitId) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            try {
                HttpPost httpPost = new HttpPost("http://localhost:8001/business/doSomething/" + waitId);
                String json = "{}";
                StringEntity entity = new StringEntity(json);
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

    @RequestMapping(value="complete/{waitId}", method = RequestMethod.PATCH)
    public @ResponseBody ResponseEntity<Void> complete(@PathVariable UUID waitId, @RequestBody OperationResponse operation) {
        _log.info("Complete recebeu: " + waitId);
        OperationResponse waiter = new OperationResponse(waitId.toString());
        _map.putIfAbsent(waitId, waiter);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}