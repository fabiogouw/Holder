package com.fabiogouw.holder;

import java.io.Serializable;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class HolderActor extends AbstractActor {

    static class BusinessCall implements Serializable {

        private static final long serialVersionUID = 4766278085642796988L;
		private final String _id;
        
        public BusinessCall(String id) {
            _id = id;
        }
    
        public String getId() {
            return _id;
        }
    }

    static class ResponseBusinessCall implements Serializable {

        private static final long serialVersionUID = 4766278085642796988L;
        private final String _id;
        private final String _value;
        
        public ResponseBusinessCall(String id, String value) {
            _id = id;
            _value = value;
        }
    
        public String getId() {
            return _id;
        }

        public String getValue() {
        return _value;
        }
    }

    static class AskResponseCall implements Serializable {

        private static final long serialVersionUID = 4766278085642796988L;
        private final String _id;
        private final String _value;
        
        public AskResponseCall(String id, String value) {
            _id = id;
            _value = value;
        }
    
        public String getId() {
            return _id;
        }

        public String getValue() {
        return _value;
        }
    }

    private final LoggingAdapter _log = Logging.getLogger(getContext().getSystem(), this);
    private ActorRef _respondTo;

    public static Props props() {
        return Props.create(HolderActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(BusinessCall.class, req -> {
                _respondTo = getSender();
                _log.info("Calling business... " + req.getId());
                callDoSomething(req.getId());
            })
            .match(ResponseBusinessCall.class, req -> {
                _log.info("Getting back from business... " + req.getId());
                _respondTo.tell(new AskResponseCall(req.getId(), req.getValue()) , getSelf());
                getSelf().tell(PoisonPill.getInstance(), getSelf());
            })            
            .build();
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
}