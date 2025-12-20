package dev.ankis.ai.simple.agents;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ankis.ai.util.LLM;
import dev.ankis.ai.util.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CustomerServiceAgentWIthJson {
    public static void main(String[] args) throws Exception {
        LLM llm = new LLM();

        Map<String, Object> codeSpec = new HashMap<>();
        codeSpec.put("name", "odd_even_number");
        codeSpec.put("description", "Identifies if the number is even or odd.");

        Map<String, String> params = new HashMap<>();
        params.put("d", "A dictionary with unique values.");
        codeSpec.put("params", params);

        ObjectMapper mapper = new ObjectMapper();
        String codeSpecString = mapper.writeValueAsString(codeSpec);

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", systemMessage));
        messages.add(new Message("user", codeSpecString));

        String response = llm.generateResponse(messages);
        log.info("Response: {}", response);

    }

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
}
