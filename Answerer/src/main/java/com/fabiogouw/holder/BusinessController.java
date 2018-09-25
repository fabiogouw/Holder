package com.fabiogouw.holder;

import java.util.Random;

import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/business")
public class BusinessController {

    @Value("${completeUrl}")
    private String _completeUrl;

    @Value("${sleep}")
    private int _sleep;    

    private static final Logger _log = LoggerFactory.getLogger(BusinessController.class);

    @RequestMapping(value="doSomething/{waitId}", method = RequestMethod.POST)
    public @ResponseBody void doSomething(@PathVariable String waitId) throws InterruptedException {
        _log.info("Business recebeu: " + waitId);
        Thread.sleep(_sleep);
        returnComplete(waitId);
    }

    private void returnComplete(String waitId) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            try {
                HttpPatch httpPatch = new HttpPatch("http://" + _completeUrl + "/complete/" + waitId);
                String json = "{\"value\":\"1\"}";
                StringEntity entity = new StringEntity(json);
                httpPatch.setEntity(entity);
                httpPatch.setHeader("Accept", "application/json");
                httpPatch.setHeader("Content-type", "application/json");     
                client.execute(httpPatch);
            }
            finally {
                client.close();    
            }
        }
        catch (Exception ex) {
            
        }
    }
}