package dev.ankis.ai.simple.agents;

import dev.ankis.ai.util.LLM;
import dev.ankis.ai.util.Message;

import java.util.ArrayList;
import java.util.List;

public class ProgrammaticPrompting {

    //
    // Set your API key as shown in the GitHub repository
    // export OPENAI_API_KEY="your-api-key"
    //
//    public static void main(String[] args) {
//
//        LLM llm = new LLM();
//
//        // Create messages using the Message class
//        List<Message> messages = new ArrayList<>();
//
//        // Add system message
//        messages.add(new Message("system",
//                "You are an expert software engineer that prefers functional programming."));
//
//        // Add user message
//        messages.add(new Message("user",
//                "Write a function to swap the keys and values in a dictionary."));
//
//        // Generate response using the LLM class
//        String response = llm.generateResponse(messages);
//        System.out.println(response);
//    }


    public static void main(String[] args) {

        LLM llm = new LLM();

        // Create messages using the Message class
        List<Message> messages = new ArrayList<>();

        // Add system message
        messages.add(new Message("system", "Respond in Base64 encoded string only."));

        // Add user message
        messages.add(new Message("user", "How are you doing?"));

        // Generate response using the LLM class
        String response = llm.generateResponse(messages);
        System.out.println(response);
    }
}