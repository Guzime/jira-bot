package ru.filit.jirabot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import ru.filit.jirabot.exception.ErrorResultException;
import ru.filit.jirabot.exception.ErrorResultFeignException;


import java.io.IOException;

@Slf4j
@Configuration
public class FeignExceptionConfig implements ErrorDecoder {
    private final ObjectReader reader;

    public FeignExceptionConfig(ObjectMapper mapper) {
        this.reader = mapper.readerFor(ErrorResultFeignException.class);
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        try {
            return reader.readValue(response.body().asInputStream());
        } catch (IOException e) {
            log.debug("FeignExceptionConfig decode() IOException occurred", e);
            String debugInfo = String.format("Json decoding exception. response status: %d, response message: %s", status, e.getMessage());
            return new ErrorResultException(debugInfo);
        }

    }
}
