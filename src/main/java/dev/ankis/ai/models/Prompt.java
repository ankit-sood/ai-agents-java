package dev.ankis.ai.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class Prompt {
    private List<Message> messages;
    private List<Tool> tools;
    private Map<String, Object> metadata;

    public Prompt(List<Message> messages) {
        this.messages = messages;
        this.tools = new ArrayList<>();
        this.metadata = new HashMap<>();
    }

    public Prompt(List<Message> messages, List<Tool> tools) {
        this.messages = messages;
        this.tools = tools;
        this.metadata = new HashMap<>();
    }
}
