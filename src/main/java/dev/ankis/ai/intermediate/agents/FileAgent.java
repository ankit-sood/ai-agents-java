package dev.ankis.ai.intermediate.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ankis.ai.models.Action;
import dev.ankis.ai.models.ActionResult;
import dev.ankis.ai.util.LLM;
import dev.ankis.ai.models.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Slf4j
public class FileAgent {
    private static List<Message> conversation = new ArrayList<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String systemPrompt = """
            You are an AI Agent that can perform tasks by using the available tools.
            
            Available Tools:
            - listFiles() -> List<String>: List all the files in the current directory.
            - readFile(fileName: String) -> String: Read the content of the file.
            - terminate(message: String) -> End the agent loop and print the summary to the user.
            
            If a user asks about files, list them before reading.
            
            Every response MUST have an action.
            Respond in this format:
            
            ```action
            {
                "tool" : "insert toolName",
                "args" : {...fill in any required arguments here...}
            }
            ```
            """;

    public static void main(String[] args) throws JsonProcessingException {
        int maxIterations = 10;
        LLM llm = new LLM();
        // Agent loop
        while(true) {
            // Include the system message and context so far
            List<Message> messages = new ArrayList<>();
            messages.add(getSystemPrompt());
            messages.addAll(conversation);

            // take the input
            log.info("Provide the operation, you want to perform.");
            Scanner sc = new Scanner(System.in);
            String userInput = sc.nextLine();
            messages.add(new Message("user", userInput));

            log.info("Agent thinking....");
            String response = llm.generateResponse(messages);
            log.info("Response: {}", response);
            conversation.add(new Message("assistant", response));

            Action action = parseAction(response);
            ActionResult result = null;
            if(action != null) {
                if("listFiles".equalsIgnoreCase(action.getTool())) {
                    result = new ActionResult(listFiles(), null);
                } else if ("readFile".equalsIgnoreCase(action.getTool())) {
                    String fileName = (String) action.getArgs().getOrDefault("fileName", null);
                    result = new ActionResult(readFile(fileName), null);
                } else if ("error".equalsIgnoreCase(action.getTool())) {
                    String errorMessage = (String) action.getArgs().getOrDefault("errorMessage", null);
                    result = new ActionResult(null, errorMessage);
                } else if ("terminate".equalsIgnoreCase(action.getTool())) {
                    log.info("Terminating agent ...");
                    break;
                } else {
                    log.error("Unknown Action: {}", action);
                }

                log.info("Action Result: {}", result.toMap());
                conversation.add(new Message("user", mapper.writeValueAsString(result.toMap())));
            }
        }
    }

    private static Message getSystemPrompt() {
        return new Message("system", systemPrompt);
    }

    // Tool to list the files in the current directory
    private static List<String> listFiles() {
        File currentDir = new File(".");
        File[] files = currentDir.listFiles();
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }

    // Tool to list the contents of the file
    private static String readFile(String fileName) {
        try {
            return Files.readString(new File(fileName).toPath());
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    private static Action parseAction(String response) {
        try {
            String actionBlock = extractMarkdownBlock(response, "action");
            Action action = mapper.readValue(actionBlock, Action.class);
            if (action.getTool() != null && action.getArgs() != null) {
                return action;
            } else {
                return errorAction("You must respond with a JSON tool invocation.");
            }
        } catch (Exception e) {
            return errorAction("Invalid JSON response. You must respond with a JSON tool invocation.");
        }
    }

    private static String extractMarkdownBlock(String text, String label) {
        String startTag = "```" + label;
        String endTag = "```";
        int start = text.indexOf(startTag);
        int end = text.indexOf(endTag, startTag.length());
        if (start != -1 && end != -1) {
            return text.substring(start + startTag.length(), end).trim();
        }
        throw new IllegalArgumentException("No markdown block found for label: " + label);
    }

    private static Action errorAction(String message) {
        Map<String, Object> args = new HashMap<>();
        args.put("message", message);
        return new Action("error", args);
    }
}
