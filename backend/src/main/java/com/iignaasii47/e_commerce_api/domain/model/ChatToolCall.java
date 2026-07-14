package com.iignaasii47.e_commerce_api.domain.model;

import java.util.Map;
import java.util.Objects;

public class ChatToolCall {

    private final String id;
    private final String functionName;
    private final Map<String, Object> arguments;

    public ChatToolCall(String id, String functionName, Map<String, Object> arguments) {
        this.id = id;
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public String getId() {
        return id;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatToolCall that = (ChatToolCall) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
