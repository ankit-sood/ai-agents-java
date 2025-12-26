package dev.ankis.ai.complex.agents.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ankis.ai.models.Action;
import dev.ankis.ai.models.ActionResult;
import dev.ankis.ai.util.LLM;
import dev.ankis.ai.models.Message;
import dev.ankis.ai.models.Prompt;
import dev.ankis.ai.models.Tool;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Function;

@Slf4j
public class ProjectDocumentAgent {
    private static final Map<String, Function<Map<String, Object>, Object>> toolFunctions = new HashMap<>();
    private static List<Tool> tools = new ArrayList<>();

    public static void main(String[] args) {
        LLM llm = new LLM();
        ObjectMapper mapper = new ObjectMapper();

        try {
            registerAllTools();

            List<Message> messages = new ArrayList<>();
            messages.add(new Message("user", userMessage));
            messages.add(new Message("system", systemMessage));

            while(true) {
                Prompt prompt = new Prompt(messages, tools);

                // Get the response
                String response = llm.generateResponse(prompt);
                log.info(response);
                messages.add(new Message("assistant", response));

                Action action = parseAction(response);
                if("terminate".equalsIgnoreCase(action.getTool())) {
                    break;
                }
                ActionResult actionResult = executeAction(action);
                if(actionResult.getResult() != null) {
                    if(actionResult.getResult() instanceof List<?>) {
                        List<String> results = (List<String>) actionResult.getResult();
                        results.forEach(result -> {
                            messages.add(new Message("assistant", "file: "+result));
                        });
                    } else {
                        messages.add(new Message("assistant", (String) actionResult.getResult()));
                    }
                } else {
                    messages.add(new Message("assistant", actionResult.getError()));
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static void registerAllTools() {
        registerTool(listFileToolJson, args -> listJavaFiles((String) args.get("sourcePath")));
        registerTool(readFileToolJson, args -> readFile((String) args.get("filePath")));
        registerTool(writeDocFileToolJson, args -> writeFile((String) args.get("fileName"),
                (String) args.get("content")));
        registerTool(terminateToolJson, args -> terminate((String) args.get("message")));
    }

    private static Action parseAction(String response) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = mapper.readValue(response, Map.class);
        String toolName = (String) responseMap.get("tool");
        Map<String, Object> toolArgs = (Map<String, Object>) responseMap.get("args");
        return new Action(toolName, toolArgs);
    }

    private static ActionResult executeAction(Action action) {
        ActionResult actionResult = null;
        Function<Map<String, Object>, Object> toolFunction = toolFunctions.get(action.getTool());
        if(toolFunction != null) {
            if("listJavaFiles".equals(action.getTool())) {
                List<String> result  = (List<String>) toolFunction.apply(action.getArgs());
                actionResult = new ActionResult(result, null);
            } else {
                String result  = (String) toolFunction.apply(action.getArgs());
                actionResult = new ActionResult(result, null);
            }

        } else {
            log.info("No action found for tool {}", action.getTool());
            actionResult = new ActionResult(null, "Unknown tool: " + action.getTool());
        }
        return actionResult;
    }

    private static List<String> listJavaFiles(String path) {
        File srcDir = new File(path);
        return Arrays.stream(Objects.requireNonNull(srcDir.listFiles()))
                .map(File::getName)
                .filter(name -> name.endsWith(".java"))
                .toList();
    }

    private static String readFile(String filePath) {
        Path path = Path.of(filePath);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            log.error("File {} does not exist or is not a regular file", filePath);
        }
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String writeFile(String fileName, String content) {
        String filePath = "/Users/ankit_sood/Dev/Repositories/ai-agents-java/docs/" + fileName;
        Path path = Path.of(filePath);
        Path parent = path.getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return filePath;
    }

    private static String terminate(String message){
        log.info("Terminating with messgae: {}", message);
        return "Successfully terminated.";
    }

    private static void registerTool(String toolJson, Function<Map<String, Object>, Object> function) {
        Tool tool = Tool.fromJson(toolJson);
        tools.add(tool);
        toolFunctions.put(tool.getToolName(), function);
    }

    private static final String listFileToolJson = """
            {
                "toolName" : "listJavaFiles",
                "description" : "Tool to get the java files present in the source directory.",
                "parameters" : {
                    "type" : "object",
                    "properties" : {
                        "sourcePath" : {"type" : "string"}
                    }
                },
                "required" : ["sourcePath"]
            }
            """;

    private static final String readFileToolJson = """
            {
                "toolName" : "readFile",
                "description" : "Tool to read the contents of the file.",
                "parameters" : {
                    "type" : "object",
                    "properties" : {
                        "filePath" : {"type" : "string"}
                    }
                },
                "required" : ["filePath"]
            }
            """;

    private static final String writeDocFileToolJson = """
            {
                "toolName" : "writeDocFile",
                "description" : "Tool to write the documentation file to the docs/ directory.",
                "parameters" : {
                    "type" : "object",
                    "properties" : {
                        "fileName" : {"type" : "string"},
                        "content" : {"type" : "string"}
                    }
                },
                "required" : ["fileName", "content"]
            }
            """;

    private static final String terminateToolJson = """
            {
                "toolName" : "terminate",
                "description" : "Tool to terminate the documentation generation. No further action or interaction is possible after this. Prints the provided message to the user.",
                "parameters" : {
                    "type" : "object",
                    "properties" : {
                        "message" : {
                            "type" : "string",
                            "description" : "The final message to display to the user."
                        }
                    }
                }
            }
            """;

    private static final String systemMessage = """
            You are an AI agent that generates the documentation files in markdown format describing each and every 
            method of the class by using available tools. 
            
            If a user gives the source directory then follow the below steps:
            - Identify all the java files present in the source directory.
            - Extract the components of each file one by one.
            - Generate the documentation and write it into a file in markdown format.
            
            If no source directory is provided, you need to ask user to provide the source directory.
            
            When you are done, terminate the conversation.
            """;

    private static final String userMessage = """
            Please generate the documentation files in markdown format describing each and every method of the class. 
            Path of the source dir is `/Users/ankit_sood/Dev/Repositories/ai-agents-java/src/main/java/dev/ankis/ai`
            """;
}
