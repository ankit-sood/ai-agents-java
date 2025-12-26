package dev.ankis.ai.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonValue;
import com.openai.models.ChatModel;
import com.openai.models.FunctionDefinition;
import com.openai.models.chat.completions.*;
import dev.ankis.ai.models.Message;
import dev.ankis.ai.models.Prompt;
import dev.ankis.ai.models.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class LLM {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String model = ChatModel.GPT_4_1_MINI.asString();

    /**
     * Generates an LLM response based on the provided messages.
     *
     * Why do it this way instead of using OpenAI directly?
     * ------------------------------------------------------
     * This method implements a critical abstraction layer that decouples the application's
     * business logic from any specific LLM provider. This architectural decision offers
     * several significant advantages:
     *
     * 1. Provider Independence: By using our own Message abstraction, we can easily switch
     *    between different LLM providers (OpenAI, Anthropic, Google, etc.) without changing
     *    any code in the rest of the application. Only this method needs modification.
     *
     * 2. API Evolution Protection: LLM provider APIs frequently change. This abstraction
     *    insulates the rest of the codebase from these changes. If OpenAI deprecates an
     *    API or changes its data structures, we only need to update this single method.
     *
     * 3. Testing and Mocking: This approach makes testing significantly easier. We can
     *    mock this method with predictable responses for unit tests without needing to
     *    stub complex provider-specific APIs.
     *
     * 4. Cost Control: We can easily implement fallback strategies, rate limiting, or
     *    routing logic to different models/providers based on cost or performance needs
     *    without affecting the consumer code.
     *
     * 5. Observability: This centralized method provides a single point for adding logging,
     *    metrics, error handling, and monitoring for all LLM interactions.
     *
     * 6. Future-Proofing: As new LLM providers emerge or as we develop internal models,
     *    we can integrate them seamlessly by just adapting this method.
     *
     * Example future extension:
     * ```java
     * if (useAnthropic) {
     *     return AnthropicAdapter.generateResponse(messages);
     * } else if (useInternalModel) {
     *     return InternalModelAdapter.generateResponse(messages);
     * } else {
     *     // Current OpenAI implementation
     * }
     * ```
     *
     * @param messages List of Message objects containing role and content.
     * @return The generated response as a String.
     */
    public String generateResponse(List<Message> messages) {
        // Initialize OpenAI client using environment variables
        OpenAIClient client = OpenAIOkHttpClient.fromEnv();

        // Transform custom Message objects to OpenAI's ChatCompletionMessageParam objects
        ChatCompletionCreateParams.Builder paramsBuilder = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4_1_MINI)
                .maxTokens(1024);

        // Add messages individually to the builder
        for (Message message : messages) {
            if (message.getRole().equals("system")) {
                ChatCompletionSystemMessageParam systemMsg = ChatCompletionSystemMessageParam.builder()
                        .content(message.getContent())
                        .build();
                paramsBuilder.addMessage(systemMsg);
            } else if (message.getRole().equals("user")) {
                ChatCompletionUserMessageParam userMsg = ChatCompletionUserMessageParam.builder()
                        .content(message.getContent())
                        .build();
                paramsBuilder.addMessage(userMsg);
            } else {
                // For assistant or other roles, use ChatCompletionAssistantMessageParam
                ChatCompletionAssistantMessageParam assistantMsg = ChatCompletionAssistantMessageParam.builder()
                        .content(message.getContent())
                        .build();
                paramsBuilder.addMessage(assistantMsg);
            }
        }

        // Get completion response
        ChatCompletion completion = client.chat().completions().create(paramsBuilder.build());

        // Return content from first choice
        return completion.choices().getFirst().message().content().orElse("");
    }

    public String generateResponse(Prompt prompt) {
        try {
            // Initialize OpenAI client using environment variables
            OpenAIClient client = OpenAIOkHttpClient.fromEnv();

            List<Message> messages = prompt.getMessages();

            ChatCompletionCreateParams.Builder paramsBuilder = ChatCompletionCreateParams.builder()
                    .model(this.model)
                    .maxCompletionTokens(2048*4);

            // Add messages to the request
            for (Message message : messages) {
                if (message.getRole().equals("system")) {
                    ChatCompletionSystemMessageParam systemMsg = ChatCompletionSystemMessageParam.builder()
                            .content(message.getContent())
                            .build();
                    paramsBuilder.addMessage(systemMsg);
                } else if (message.getRole().equals("user")) {
                    ChatCompletionUserMessageParam userMsg = ChatCompletionUserMessageParam.builder()
                            .content(message.getContent())
                            .build();
                    paramsBuilder.addMessage(userMsg);
                } else {
                    ChatCompletionAssistantMessageParam assistantMsg = ChatCompletionAssistantMessageParam.builder()
                            .content(message.getContent())
                            .build();
                    paramsBuilder.addMessage(assistantMsg);
                }
            }

            String result = null;
            List<Tool> tools = prompt.getTools();
            if(CollectionUtils.isEmpty(tools)) {
                ChatCompletion chatCompletion = client.chat().completions().create(paramsBuilder.build());
                result = chatCompletion.choices().getFirst().message().content().orElse("");
            } else {
                // Add the tools
                List<ChatCompletionTool> chatCompletionTools = convertToolsToOpenAIFormat(tools);
                paramsBuilder.tools(chatCompletionTools);

                // Get completion with tools
                ChatCompletion completion = client.chat().completions().create(paramsBuilder.build());

                // Check if model used a tool
                if (completion.choices().getFirst().message().toolCalls().isPresent()) {
                    // Extract the tool call
                    ChatCompletionMessageToolCall toolCall = completion.choices().getFirst().message().toolCalls().get().getFirst();

                    // Format the response as a JSON string
                    Map<String, Object> toolResponse = new HashMap<>();
                    if(toolCall.function().isPresent()) {
                        toolResponse.put("tool", toolCall.function().get().function().name());
                        toolResponse.put("args", objectMapper.readValue(toolCall.function().get().function().arguments(), Map.class));
                    } else {
                        log.error("Tool Call Function Not Found");
                    }
                    result = objectMapper.writeValueAsString(toolResponse);
                } else {
                    result = completion.choices().getFirst().message().content().orElse("");
                }
            }
            return result;
        } catch (Exception exp) {
            log.error("Error generating response: " + exp.getMessage());
            log.debug("{}",exp);

            log.error("Prompt details:");
            for (Message message : prompt.getMessages()) {
                log.error("Message: " + message.getRole() + " - " + message.getContent());
            }

            if (!prompt.getTools().isEmpty()) {
                log.error("Tools:");
                for (Tool tool : prompt.getTools()) {
                    log.error("Tool: " + tool.getToolName() + " - " + tool.getDescription());
                }
            }

            log.error("Model: " + this.model);

            throw new RuntimeException("Failed to generate response", exp);
        }
    }

    private List<ChatCompletionTool> convertToolsToOpenAIFormat(List<Tool> tools) {
        List<ChatCompletionTool> chatCompletionTools = new ArrayList<>();

        for (Tool tool : tools) {
            FunctionDefinition functionDefinition =
                    FunctionDefinition.builder()
                            .name(tool.getToolName())
                            .description(tool.getDescription())
                            .parameters(JsonValue.from(tool.getParameters()))
                            .build();

            ChatCompletionFunctionTool functionTool =
                    ChatCompletionFunctionTool.builder()
                            .function(functionDefinition)
                            .build();

            chatCompletionTools.add(
                    ChatCompletionTool.ofFunction(functionTool)
            );
        }

        return chatCompletionTools;
    }

}
