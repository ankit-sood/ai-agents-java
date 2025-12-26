package dev.ankis.ai.simple.agents;

import dev.ankis.ai.util.LLM;
import dev.ankis.ai.models.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MemoizedProgrammaticPrompting {
    private static final String systemMessage = """
            You are an expert java software engineer that prefers functional programming.
            You need to return the response as JSON in the format:
            ```json
            {
                "title" : <String Value>,
                "code" : <String Value>, 
            }
            ```
    """;

    public static void main(String[] args) {

        LLM llm = new LLM();

        // Iteration 1

        // Create messages using the Message class
        List<Message> messages = new ArrayList<>();
        // Add system message
        messages.add(new Message("system", systemMessage));
        // Add user message
        messages.add(new Message("user", "Write a Java Program to identify is a number is odd or even ?"));
        // Generate response using the LLM class
        String response = llm.generateResponse(messages);
        log.info("First Response: {}",response);
        messages.clear();

        // Iteration 2
        // Add the system message
        messages.add(new Message("system", systemMessage));
        // Add the response from previous iteration
        messages.add(new Message("assistant", response));
        // Add the new user message
        messages.add(new Message("user", "Update the function to include the java docs."));

        response = llm.generateResponse(messages);
        log.info("Second Response: {}",response);
    }
}