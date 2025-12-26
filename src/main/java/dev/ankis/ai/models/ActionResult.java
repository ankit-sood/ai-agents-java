package dev.ankis.ai.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class ActionResult {
    private Object result;
    private String error;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (error == null) {
            map.put("result", result);
        } else {
            map.put("error", error);
        }
        return map;
    }
}
