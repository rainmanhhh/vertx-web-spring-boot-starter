package ez.spring.vertx.web.handler.props;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Set;

import ez.spring.vertx.web.VertxWebConfiguration;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.handler.CorsHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Lazy
@Data
@Configuration
@ConfigurationProperties(VertxWebConfiguration.PREFIX + ".cors-handler")
public class CorsHandlerProps extends AbstractHandlerProps {
    private String handler = CorsHandler.class.getCanonicalName();
    private int order = -900;

    /**
     * if set to `*`, {@link #allowCredentials} cannot be true
     */
    private String allowedOriginPattern = "*";
    private Set<HttpMethod> allowedMethods = null;
    private Set<String> allowedHeaders = null;
    private Set<String> exposedHeaders = null;
    /**
     * if set to true, {@link #allowedOriginPattern} cannot be `*`
     */
    private boolean allowCredentials = false;
    /**
     * default value is -1(means no max age limit)
     */
    private int maxAgeSeconds = -1;

    @Lazy
    @ConditionalOnMissingBean(CorsHandler.class)
    @Bean
    public CorsHandler corsHandler() {
        CorsHandler handler = CorsHandler.create(allowedOriginPattern);
        if (allowedMethods != null) handler.allowedMethods(allowedMethods);
        if (allowedHeaders != null) handler.allowedHeaders(allowedHeaders);
        if (exposedHeaders != null) handler.exposedHeaders(exposedHeaders);
        return handler.allowCredentials(allowCredentials);
    }
}