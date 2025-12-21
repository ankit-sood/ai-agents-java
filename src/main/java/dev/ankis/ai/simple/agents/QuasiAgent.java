package dev.ankis.ai.simple.agents;

import dev.ankis.ai.util.LLM;
import dev.ankis.ai.util.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class QuasiAgent {
    private static final String systemPrompt = """
            You are an expert Java Engineer who prefers functional programming. You write clean, efficient and well 
            documented code. While writting code you follow best practices and provide comprehensive documentation and
            test cases.
            
            You don't have to return any explanation just return what is asked for.
            """;

    private static List<Message> conversationHistory = new ArrayList<>();

    public static void main(String[] args) {
        log.info("Starting QuasiAgent");
        log.info("Please enter the function you want to create");
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();

        String functionCode = generateFunction(userInput);
        log.info("1. Function Code: {}", functionCode);

        String functionWithDocumentation = addDocumentation(functionCode);
        log.info("2. Function With Documentation: {}", functionWithDocumentation);

        String completeCode = addTestCases(functionWithDocumentation);
        log.info("3. Complete Code: {}", completeCode);
    }

    private static String generateFunction(String userPrompt) {
        LLM llm = new LLM();
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", systemPrompt));
        messages.add(new Message("user", userPrompt));
        return llm.generateResponse(messages);
    }

    private static String addDocumentation(String generatedFunction) {
        LLM llm = new LLM();
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", systemPrompt));
        messages.add(new Message("assistant", generatedFunction));

        String userMessage = """
                Add comprehensive java docs. to the function. Include:
                1. Function Description
                2. Parameter Description
                3. Reture Value Description
                4. Example Usage
                5. Edge Cases
                
                Here's the function to document: \n
                """ + generatedFunction;

        messages.add(new Message("user", userMessage));
        return llm.generateResponse(messages);
    }

    private static String addTestCases(String functionWithJavaDocs) {
        LLM llm = new LLM();
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", systemPrompt));
        messages.add(new Message("assistant", functionWithJavaDocs));
        messages.add(new Message("user", "Generate all the test cases and Include a function to all generated test cases."));
        return llm.generateResponse(messages);
    }
}
