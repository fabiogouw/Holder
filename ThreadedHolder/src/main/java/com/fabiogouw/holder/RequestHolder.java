package com.fabiogouw.holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHolder {

    private final Map<UUID, DeferredResult<OperationResponse>> _map = new ConcurrentHashMap<>();
    private static final Logger _log = LoggerFactory.getLogger(RequestHolder.class);

    public UUID add(DeferredResult<OperationResponse> hold) {
        UUID waitId = UUID.randomUUID();
        hold.onTimeout(() -> {
            _map.remove(waitId);
            _log.info("Removed by timeout: " + waitId);
        });
        hold.onCompletion(() -> {
            _map.remove(waitId);
            _log.info("Removed: " + waitId);
        });
        _log.info("Added: " + waitId);
        _map.put(waitId, hold);
        return waitId;
    }

    public void release(UUID id, OperationResponse response) {
        DeferredResult<OperationResponse> hold = _map.get(id);
        if(hold != null) {
            hold.setResult(response);
            _log.info("Completed: " + id);
        }
    }
}
