package com.beingbachelor.rule_engine.rule;

public class BusinessRules2 {

    // Rule 1: Greater than 10
    public static Rule<Integer> ruleGreaterThan10 = Rule.createRule(
            (input, args) -> {

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }, // Rule logic
            "ruleGreaterThan10",
            "Input must be greater than 10", // Custom message
            false
    );

    // Rule 2: Less than 50
    public static Rule<Integer> ruleLessThan50 = Rule.createRule(
            (input, args) -> {

                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }, // Rule logic
            "ruleLessThan50",
            "Input must be less than 50", // Custom message
            false
    );

    // Rule 3: Is Even
    public static Rule<Integer> ruleIsEven = Rule.createRule(
            (input, args) -> {
                return false;
            }, // Rule logic
            "ruleIsEven",
            "Input must even", // Custom message
            false
    );
}
