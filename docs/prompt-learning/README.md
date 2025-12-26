# LLM Evaluation & Prompt Optimization Framework

A structured framework for evaluating, judging, and optimizing coding agent outputs through systematic prompt engineering and meta-learning.

## Overview

This framework provides a three-tier evaluation system for assessing and improving AI coding agents:

1. **Evaluation Agent** - Analyzes question-answering outputs with precision/recall metrics
2. **Judge Agent** - Reviews coding agent solutions against test cases and correct implementations
3. **Meta Agent** - Optimizes prompts and rulesets based on evaluation feedback

## Components

### 1. LLM Evaluation Prompt (`llm-eval-prompt.md`)

Analyzes question-answering system outputs by comparing predicted answers against gold standard answers.

**Purpose**: Perform detailed error analysis on model predictions using standard NLP metrics.

**Metrics Analyzed**:
- **Exact Match**: Binary score (1.0 = perfect match, 0.0 = wrong)
- **F1 Score**: Harmonic mean of precision and recall
- **Precision**: Ratio of correct tokens in prediction
- **Recall**: Ratio of gold tokens captured in prediction

**Output Structure**:
1. Error description in plain language
2. Precision/Recall analysis and interpretation
3. Token-level breakdown (correct, extra, missing)
4. Root cause analysis
5. Improvement suggestions

**Use Case**: Understanding why an AI model's answer differs from the expected answer and identifying specific areas for improvement.

### 2. LLM Judge Prompt (`llm-judge-prompt.md`)

Reviews coding agent solutions by evaluating code quality, correctness, and approach.

**Purpose**: Provide expert-level code review of agent-generated solutions.

**Input Requirements**:
- Problem statement
- Coding agent's solution
- Test cases
- Test results (pass/fail)
- Actual correct code (reference implementation)

**Output Format** (JSON):
```json
{
    "result": "pass or fail",
    "explanation": "Detailed reasoning about correctness"
}
```

**Analysis Focus**:
- Solution correctness
- Agent's problem-solving approach
- Reasoning behind the agent's implementation choices
- Comparison with reference implementation
- Test case alignment

**Use Case**: Automated code review and understanding agent decision-making processes.

### 3. LLM Meta Prompt (`llm-meta-prompt.md`)

Optimizes coding agent prompts and rulesets through iterative improvement.

**Purpose**: Meta-learning layer that improves the overall guidance system for coding agents.

**Input Components**:
- `{original_prompt}`: Baseline prompt for the coding agent
- `{original_ruleset}`: Current rules guiding agent behavior
- `{results_from_judge_agent}`: Evaluation feedback and examples

**Optimization Process**:
1. Review baseline prompt and current ruleset
2. Analyze evaluation feedback and examples
3. Identify high-level shortcomings and gaps
4. Detect missing guidance or unclear constraints
5. Propose robust, broadly applicable improvements

**Output Format** (JSON):
```json
{
    "ruleset": [
        "rule1",
        "rule2",
        "..."
    ]
}
```

**Improvement Actions**:
- Add new rules for uncovered scenarios
- Remove redundant or ineffective rules
- Edit/strengthen existing rules
- Complete ruleset overhaul if necessary

**Use Case**: Continuous improvement of agent performance through systematic prompt engineering.

## Workflow

### End-to-End Evaluation Pipeline

```
┌─────────────────┐
│  Coding Agent   │
│   Generates     │
│   Solution      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Judge Agent    │◄─── Test Cases
│   Evaluates     │◄─── Reference Code
│   Solution      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Eval Agent     │◄─── Metrics
│   Analyzes      │◄─── Gold Answers
│   Errors        │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Meta Agent     │◄─── Baseline Prompt
│   Optimizes     │◄─── Current Ruleset
│   Ruleset       │
└────────┬────────┘
         │
         ▼
    Improved Prompt
    & Ruleset
```

## Usage Examples

### Using the Evaluation Agent

```python
evaluation_prompt = load_prompt("llm-eval-prompt.md")

# Prepare metrics
metrics = {
    "exact_match": 0.0,
    "f1_score": 0.67,
    "precision": 0.80,
    "recall": 0.57
}

# Format prompt with actual data
formatted_prompt = evaluation_prompt.format(
    question="What is the capital of France?",
    gold_answer="Paris",
    predicted_answer="Paris is the capital city",
    **metrics
)

# Get analysis
analysis = llm.generate(formatted_prompt)
```

### Using the Judge Agent

```python
judge_prompt = load_prompt("llm-judge-prompt.md")

# Format with coding problem data
formatted_prompt = judge_prompt.format(
    problem_statement="Implement a function to reverse a string",
    coding_agent_solution=agent_code,
    test_cases=test_cases,
    result="fail",
    actual_correct_code=reference_implementation
)

# Get judgment
judgment = llm.generate(formatted_prompt)
result = json.loads(judgment)
```

### Using the Meta Agent

```python
meta_prompt = load_prompt("llm-meta-prompt.md")

# Collect evaluation results
evaluation_results = aggregate_judge_results()

# Format for optimization
formatted_prompt = meta_prompt.format(
    original_prompt=baseline_prompt,
    original_ruleset=current_rules,
    results_from_judge_agent=evaluation_results
)

# Get optimized ruleset
optimization = llm.generate(formatted_prompt)
new_ruleset = json.loads(optimization)["ruleset"]
```

## Metrics Explained

### Precision
- Measures correctness of the prediction
- **High precision**: Few false positives (extra/wrong tokens)
- **Low precision**: Many irrelevant or incorrect tokens

### Recall
- Measures completeness of the prediction
- **High recall**: Few false negatives (missing tokens)
- **Low recall**: Many missing required tokens

### F1 Score
- Harmonic mean balancing precision and recall
- **High F1**: Good balance of correctness and completeness
- **Low F1**: Poor overall performance

### Exact Match
- Binary metric for perfect answers
- Useful for factual questions with single correct answers

## Best Practices

### For Evaluation Agent
- Always provide context about what was expected
- Focus on actionable feedback
- Consider both syntactic and semantic differences

### For Judge Agent
- Include comprehensive test cases
- Provide reference implementation for comparison
- Analyze not just correctness but approach quality

### For Meta Agent
- Collect diverse examples before optimization
- Focus on general patterns, not specific cases
- Test new rulesets before deployment
- Iterate incrementally for stability

## Output Formats

### Evaluation Agent Output (Markdown)
```markdown
## What went wrong
The prediction included extra descriptive text...

## Precision/Recall Analysis
- Precision: 0.80 (some extra tokens)
- Recall: 0.57 (missing key information)

## Token-level Breakdown
...
```

### Judge Agent Output (JSON)
```json
{
    "result": "fail",
    "explanation": "The solution fails edge case X because..."
}
```

### Meta Agent Output (JSON)
```json
{
    "ruleset": [
        "Always validate input parameters before processing",
        "Handle edge cases explicitly",
        "Write clear variable names"
    ]
}
```

## Common Use Cases

1. **QA System Evaluation**: Assess answer quality for chatbots and question-answering systems
2. **Code Generation Assessment**: Evaluate AI-generated code quality
3. **Prompt Engineering**: Systematically improve agent prompts
4. **Automated Testing**: Create self-improving test feedback loops
5. **Agent Performance Monitoring**: Track and optimize agent behavior over time

## Extension Ideas

- Add domain-specific evaluation criteria
- Implement multi-agent debate for complex decisions
- Create visualization dashboards for metrics
- Build automated A/B testing for prompt variants
- Integrate with CI/CD pipelines for continuous evaluation

## Contributing

When adding new prompt templates:
1. Follow the established format (clear sections, JSON output)
2. Include usage examples
3. Document expected inputs and outputs
4. Test with diverse scenarios

## Reference
- [Prompt Learning Talk](https://www.youtube.com/watch?v=pP_dSNz_EdQ)