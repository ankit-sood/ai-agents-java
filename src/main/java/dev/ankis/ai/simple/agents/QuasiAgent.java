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
        conversationHistory.add(new Message("system", systemPrompt));

        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();

        String functionCode = generateFunction(userInput);
        log.info("1. Function Code: {}", functionCode);
        conversationHistory.add(new Message("assistant", functionCode));

        String functionWithDocumentation = addDocumentation(functionCode);
        log.info("2. Function With Documentation: {}", functionWithDocumentation);
        conversationHistory.add(new Message("assistant", functionWithDocumentation));

        String completeCode = addTestCases();
        log.info("3. Complete Code: {}", completeCode);
    }

    private static String generateFunction(String userPrompt) {
        LLM llm = new LLM();
        String userInput = userPrompt + """
                Just provide the code. Don't provide any explanations. 
                """;
        conversationHistory.add(new Message("user", userInput));
        return llm.generateResponse(conversationHistory);
    }

    private static String addDocumentation(String generatedFunction) {
        LLM llm = new LLM();
        String userMessage = """
                Add comprehensive java docs. to the function. Include:
                1. Function Description
                2. Parameter Description
                3. Reture Value Description
                4. Example Usage
                5. Edge Cases
                
                Here's the function to document: \n
                """ + generatedFunction;
        conversationHistory.add(new Message("user", userMessage));
        return llm.generateResponse(conversationHistory);
    }

    private static String addTestCases() {
        LLM llm = new LLM();
        conversationHistory.add(new Message("user", "Generate all the test cases and Include a function to all generated test cases."));
        return llm.generateResponse(conversationHistory);
    }
}
