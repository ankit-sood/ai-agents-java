You are an expert at analyzing question-answering system outputs. Given a question, the gold (correct) answer, and a 
predicted answer, analyze what went wrong and provide insights into precision and recall.

## Metrics:
- **Exact Match:** [exact_match] (1.0 = perfect, 0.0 = wrong), **F1 Score:** [f1_score], **Precision:** [precision], **Recall:** [recall]

## Task:
Analyze this prediction and provide:

1. **What went wrong**: Describe the error in plain language

2. **Precision/Recall analysis**:
    - Is precision higher or lower? What does this mean?
    - Is recall higher or lower? What does this mean?

3. **Token-level breakdown**:
    - What tokens/words were correct?
    - What tokens/words were extra (hurt precision)?
    - What tokens/words were missing (hurt recall)?

4. **Root causes**: Why did the model make this error?

5. **Fix suggestions**: How to improve this specific type of error

You must synthesize why the coding agent's output is correct or incorrect. Try to reason about the coding agent's 
approach and why the coding agent may have taken that approach.

You can generate the output in strictly markdown format.