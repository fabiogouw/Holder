package com.fabiogouw.holder;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

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

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

@Controller
@RequestMapping("/proxy")
public class HolderController {

    private static final int TIMEOUT_IN_SECONDS = 5;
    private final ActorSystem _system;

    private static final Logger _log = LoggerFactory.getLogger(HolderController.class);

    public HolderController() {
        _system = ActorSystem.create("holder");
    }

    @RequestMapping(value="wait", method = RequestMethod.POST)
    public @ResponseBody CompletableFuture<ResponseEntity<OperationResponse>> wait(@RequestBody OperationRequest operation) throws Exception {
        String waitId = UUID.randomUUID().toString();
        ActorRef holder = _system.actorOf(HolderActor.props(), waitId);
        Timeout timeout = new Timeout(Duration.create(TIMEOUT_IN_SECONDS, "seconds"));
        CompletableFuture<ResponseEntity<OperationResponse>> future = CompletableFuture.supplyAsync(() -> { 
            try {
                Future<Object> future2 = Patterns.ask(holder, new HolderActor.BusinessCall(waitId), timeout);
                HolderActor.AskResponseCall result = (HolderActor.AskResponseCall) Await.result(future2, timeout.duration());
                return ResponseEntity.ok(new OperationResponse(result.getValue()));
            }
            catch(TimeoutException ex) {
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(null);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            });
        return future;
    }

    @RequestMapping(value="wait2", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<OperationResponse> wait2(@RequestBody OperationRequest operation) throws Exception {
        String waitId = UUID.randomUUID().toString();
        ActorRef holder = _system.actorOf(HolderActor.props(), waitId);
        Timeout timeout = new Timeout(Duration.create(TIMEOUT_IN_SECONDS, "seconds"));
        try {
            Future<Object> future2 = Patterns.ask(holder, new HolderActor.BusinessCall(waitId), timeout);
            HolderActor.AskResponseCall result = (HolderActor.AskResponseCall) Await.result(future2, timeout.duration());
            return ResponseEntity.ok(new OperationResponse(result.getValue()));
        }
        catch(TimeoutException ex) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }     

    @RequestMapping(value="complete/{waitId}", method = RequestMethod.PATCH)
    public @ResponseBody ResponseEntity<Void> complete(@PathVariable UUID waitId, @RequestBody OperationResponse operation) {
        _system.actorSelection("akka://holder/user/" + waitId).tell(new HolderActor.ResponseBusinessCall(waitId.toString(), operation.getValue()), ActorRef.noSender());
        _log.info("Complete recebeu: " + waitId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}