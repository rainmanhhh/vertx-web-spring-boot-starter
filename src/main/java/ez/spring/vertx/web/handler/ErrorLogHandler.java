package ez.spring.vertx.web.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ErrorLogHandler implements Handler<RoutingContext> {
    private int errorCode = HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
    private int warnCode = HttpResponseStatus.BAD_REQUEST.code();
    private boolean showWarnStack = false;

    @Override
    public void handle(RoutingContext event) {
        event.addBodyEndHandler(v -> log(event));
        event.next();
    }

    protected void log(RoutingContext context) {
        Throwable failure = context.failure();
        if (failure != null) {
            String errClass = failure.getClass().getCanonicalName();
            if (failure instanceof HttpStatusException) {
                HttpStatusException se = (HttpStatusException) failure;
                int statusCode = se.getStatusCode();
                String payload = se.getPayload();
                Throwable cause = se.getCause();
                if (failure.getClass() == HttpStatusException.class) errClass = "";
                if (statusCode >= getErrorCode()) {
                    log.error("http-{} {}: {}", statusCode, errClass, payload, cause);
                } else if (statusCode >= getWarnCode()) {
                    if (isShowWarnStack())
                        log.warn("http-{} {}: {}", statusCode, errClass, payload, cause);
                    else log.warn("http-{} {}: {}", statusCode, errClass, payload);
                } // else LoggerHandler will do log
            } else
                log.error("{}: {}", errClass, failure.getMessage(), failure);
        } // else LoggerHandler will do log
    }
}