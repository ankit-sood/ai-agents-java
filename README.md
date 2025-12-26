# AI Agents Java

A Java framework for building AI-powered agents that can interact with tools, execute actions, and perform autonomous tasks using Large Language Models (LLMs).

## Overview

AI Agents Java is a lightweight framework that demonstrates how to build agentic AI systems in Java. The framework provides an abstraction layer over LLM providers (currently OpenAI) and includes example agents that can perform file operations, generate documentation, and interact with tools autonomously.

## Features

- **LLM Provider Abstraction**: Decoupled architecture that makes it easy to switch between different LLM providers
- **Tool-based Agent System**: Define custom tools that agents can use to perform actions
- **Conversation Management**: Built-in message handling and conversation state management
- **File Operations**: Example agents for file listing, reading, and manipulation
- **Documentation Generation**: Automated documentation generation for Java codebases
- **Action-Result Pattern**: Structured approach to tool execution and result handling

## Architecture

The framework is built around several key components:

- **LLM**: Abstraction layer for interacting with language models
- **Agent**: Base agent implementation with tool execution capabilities
- **Message**: Represents conversation messages with roles (system, user, assistant)
- **Action/ActionResult**: Structured approach to tool invocation and responses
- **Tool**: Define custom tools with JSON schema validation
- **Prompt**: Encapsulates messages, tools, and metadata for LLM requests

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- OpenAI API key (set as environment variable)

## Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/ai-agents-java.git
cd ai-agents-java
```

2. Set your OpenAI API key as an environment variable:
```bash
export OPENAI_API_KEY=your-api-key-here
```

3. Build the project:
```bash
mvn clean install
```

## Usage

### Running the File Agent

The File Agent can list and read files in a directory:

```java
public static void main(String[] args) {
    FileAgent.main(args);
}
```

The agent will prompt you for operations and can:
- List all files in the current directory
- Read the contents of specific files
- Respond to natural language queries about files

### Running the Project Documentation Agent

The Documentation Agent automatically generates markdown documentation for Java source files:

```java
public static void main(String[] args) {
    ProjectDocumentAgent.main(args);
}
```

This agent will:
1. List all Java files in the specified source directory
2. Read each file's contents
3. Generate comprehensive markdown documentation
4. Save the documentation to the `docs/` directory

### Creating Custom Agents

Here's a simple example of creating a custom agent:

```java
// Define your tools as JSON
String customToolJson = """
    {
        "toolName" : "myCustomTool",
        "description" : "Description of what this tool does",
        "parameters" : {
            "type" : "object",
            "properties" : {
                "param1" : {"type" : "string"}
            }
        },
        "required" : ["param1"]
    }
    """;

// Register the tool with a function
registerTool(customToolJson, args -> {
    String param = (String) args.get("param1");
    // Your tool logic here
    return "Result";
});

// Create messages and generate response
List<Message> messages = new ArrayList<>();
messages.add(new Message("system", "Your system prompt"));
messages.add(new Message("user", "User request"));

LLM llm = new LLM();
String response = llm.generateResponse(new Prompt(messages, tools));
```

## Project Structure

```
ai-agents-java/
├── src/main/java/dev/ankis/ai/
│   ├── complex/agents/models/
│   │   └── ProjectDocumentAgent.java    # Documentation generation agent
│   ├── intermediate/agents/
│   │   └── FileAgent.java               # File operations agent
│   ├── models/
│   │   ├── Action.java                  # Action representation
│   │   ├── ActionResult.java            # Action result wrapper
│   │   ├── Message.java                 # Conversation message
│   │   ├── Prompt.java                  # LLM prompt wrapper
│   │   └── Tool.java                    # Tool definition
│   └── util/
│       └── LLM.java                     # LLM abstraction layer
├── docs/                                # Generated documentation
├── pom.xml                              # Maven configuration
└── README.md                            # This file
```

## Key Concepts

### Tool Registration

Tools are defined using JSON schema and registered with executable functions:

```java
registerTool(toolJson, args -> {
    // Implementation
    return result;
});
```

### Action Parsing

Agent responses are parsed into structured Actions:

```java
Action action = parseAction(response);
String toolName = action.getTool();
Map<String, Object> args = action.getArgs();
```

### Conversation Loop

Agents operate in a loop, maintaining conversation context:

1. Get user input or system directive
2. Send messages to LLM with available tools
3. Parse LLM response into an action
4. Execute the action
5. Add result to conversation
6. Repeat until termination

## Dependencies

- **Spring Boot 4.0.1**: Application framework
- **OpenAI Java SDK 4.13.0**: OpenAI API client
- **Lombok**: Reduce boilerplate code
- **Jackson**: JSON processing

## Design Philosophy

### Why Use an Abstraction Layer?

The `LLM` class provides a critical abstraction that:

1. **Provider Independence**: Switch between OpenAI, Anthropic, or other providers without changing application code
2. **API Evolution Protection**: Isolates the codebase from provider API changes
3. **Testing & Mocking**: Simplifies unit testing with predictable mock responses
4. **Cost Control**: Enables fallback strategies and routing logic
5. **Observability**: Centralized logging, metrics, and monitoring
6. **Future-Proofing**: Easy integration of new providers or internal models

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is available for educational and demonstration purposes.

## Future Enhancements

- Support for additional LLM providers (Anthropic Claude, Google Gemini)
- Web-based agent interface
- Multi-agent collaboration patterns
- Enhanced error handling and retry logic
- Agent memory and context persistence
- Streaming responses support

## Contact

For questions or feedback, please open an issue on the GitHub repository.

---

Built with ☕ and 🤖