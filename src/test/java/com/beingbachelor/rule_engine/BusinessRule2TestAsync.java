package com.beingbachelor.rule_engine;


import com.beingbachelor.rule_engine.engine.RuleEngine;
import com.beingbachelor.rule_engine.rule.BusinessRules2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BusinessRule2TestAsync {

    /*
     *   Added Sleeps to test Async behaviour.
     *   Result should have sequence -
     *      Input must even:false
     *      Input must be greater than 10:false
     *      Input must be less than 50:false
     */

    private RuleEngine<String> ruleEngine;
    private BusinessRules2 brEngine;

    @BeforeEach
    public void setUp() {
        ruleEngine = new RuleEngine<>(); // Initialize rule engine with 3 threads
    }

    @Test
    @DisplayName("1. Async output - All pass in sequence of thread sleep.")
    public void brSuccess() {

        // Run the rule engine asynchronously and get the results
        int testInput = 7;
        System.out.println("Running Rule Engine for input: " + testInput);

        RuleEngine<Integer> ruleEngine = new RuleEngine<>(true);
        ruleEngine.addRule(BusinessRules2.ruleGreaterThan10)
                .addRule(BusinessRules2.ruleLessThan50)
                .addRule(BusinessRules2.ruleIsEven);

        Map<String, String> failedRules = ruleEngine.runAndCollectFailures(testInput);
        ruleEngine.shutdown();
        assertEquals(3, failedRules.size());

        // ✅ Assert by rule name (you must ensure these names are available via getRuleName())
        assertEquals("Input must be greater than 10 : false", failedRules.get("ruleGreaterThan10"));
        assertEquals("Input must be less than 50 : false", failedRules.get("ruleLessThan50"));
        assertEquals("Input must even : false", failedRules.get("ruleIsEven"));
    }
}
