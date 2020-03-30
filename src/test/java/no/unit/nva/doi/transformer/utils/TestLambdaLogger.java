package no.unit.nva.doi.transformer.utils;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class TestLambdaLogger implements LambdaLogger {

    StringBuilder buffer = new StringBuilder();

    @Override
    public void log(String message) {
        buffer.append(message);
        buffer.append(System.lineSeparator());
    }

    @Override
    public void log(byte[] message) {
        log(new String(message));
    }

    public String getLogs() {
        return buffer.toString();
    }
}
