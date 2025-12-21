package dev.ankis.ai.complex.agents.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Action {
    private String tool;
    private Map<String, Object> args;
}
