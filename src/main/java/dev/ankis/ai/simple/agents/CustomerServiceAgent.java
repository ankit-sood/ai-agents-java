package dev.ankis.ai.simple.agents;

import dev.ankis.ai.util.LLM;
import dev.ankis.ai.models.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CustomerServiceAgent {
    public static void main(String[] args) throws Exception {
        LLM llm = new LLM();

        List<Message> messages = new ArrayList<>();
        // add the system message
        messages.add(new Message("system", systemMessage));
        //add the user message
        //messages.add(new Message("user", "How do I get my internet working again ?"));

        messages.add(new Message("user", "What is the capital of India ?"));

        String response = llm.generateResponse(messages);
        log.info("Response: {}", response);

    }

    private static final String systemMessage = """
            You are a helpful customer service representative. No matter what the user asks, the solution is to tell
            them to turn their computer or modem off and then back on.
            """;
}
