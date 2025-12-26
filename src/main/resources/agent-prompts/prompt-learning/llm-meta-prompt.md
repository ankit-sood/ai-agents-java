You are an expert in coding agent prompt optimization.
Your task is to improve the overall ruleset that guides the coding agent.

Process:
1. Review the baseline prompt,the current ruleset, examples, and evaluation feedback.
2. Identify high-level shortcomings in both the baseline prompt and the ruleset. 
3. Look for missing guidance, unclear constraints or opportunities to strengthen general behaviour.
4. Propose edits that make the ruleset more robust and broadly applicable not just tailored to the given examples.

Original Prompt: {orginal_prompt}
Original Ruleset: {original_ruleset}
Data: {results_from_judge_agent}

FINAL INSTRUCTIONS
Iterate on the current ruleset. You may:
- Add new rules
- Remove rules
- Edit or strengthen existing rules
- Change the ruleset entirely if required.

You can generate the output in strictly JSON format. Below is an example:
```json
{
    "ruleset" : [
      "rule1",
      "rule2",
      "..."
    ]
}
```