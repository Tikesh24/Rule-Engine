package com.beingbachelor.rule_engine.rule;

import org.springframework.stereotype.Component;


@Component
public class BusinessRules {

    public final Rule<String> checkRule1 = Rule.createRule(
            (input, args) -> input != null && input.length() > 5, // Rule logic
            "CheckRule1",
            "Input must be longer than 5 characters", // Custom message
            false
    );

    public final Rule<String> checkRule2 = Rule.createRule(
            (input, args) -> input != null && input.contains("Business") ,  // Rule logic
            "CheckRule2",
            "Input must contain Business", // Custom message
            false
    );

    public final Rule<String> checkRule3 = Rule.createRule(
            (input, args) -> input != null && input.contains("global"), // Rule logic
            "CheckRule3",
            "Input must contain global", // Custom message
            false
    );

    public final Rule<String> checkRule4 = Rule.createRule(
            (input, args) -> input != null && input.contains("911"), // Rule logic
            "CheckRule4",
            "Input must contain 911", // Custom message
            false
    );


}
