package com.beingbachelor.rule_engine;

import com.beingbachelor.rule_engine.engine.RuleEngine;
import com.beingbachelor.rule_engine.rule.BusinessRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusinessRulesTest {

    /*
     *   Test BRs
     *   1. Rule1: Input must be longer than 5 characters
     *   2. Rule2: Input must contain 7Eleven
     *   3. Rule3: Input must contain GSC
     *   4. Rule4: Input must contain 711
     */

    private RuleEngine<String> ruleEngine;
    private BusinessRules brEngine;

    @BeforeEach
    public void setUp() {
        ruleEngine = new RuleEngine<>(); // Initialize rule engine with 3 threads
        brEngine = new BusinessRules();   // Initialize business rules
        ruleEngine
                .addRule(brEngine.checkRule1)
                .addRule(brEngine.checkRule2)
                .addRule(brEngine.checkRule3)
                .addRule(brEngine.checkRule4);
    }

    @Test
    @DisplayName("1. Success: All rules passed (stopOnAnyFailure = false)")
    public void brSuccess() throws InterruptedException {
        String testString = "7Eleven GSC 711";
        Map<String, String> run = ruleEngine.runAndCollectFailures(testString);
        assertEquals(0, run.size());
    }

    @Test
    @DisplayName("2. Failure: All rules failed, continue execution (stopOnAnyFailure = false)")
    public void brFailure_stopOnFailure_False() {
        String testString = "TEST";
        Map<String, String> run = ruleEngine.runAndCollectFailures(testString);
        ruleEngine.stopOnAnyFailure(false);
        assertEquals(4, run.size());

        assertEquals("Input must be longer than 5 characters : false", run.get("CheckRule1"));
        assertEquals("Input must contain 7Eleven : false", run.get("CheckRule2"));
        assertEquals("Input must contain GSC : false", run.get("CheckRule3"));
        assertEquals("Input must contain 711 : false", run.get("CheckRule4"));
    }

    @Test
    @DisplayName("3. Partial Success: Rule 1 passed, others failed")
    public void brFailure_stopOnFailure_False_one_pass() {
        String testString = "TEST_RULES";
        Map<String, String> run = ruleEngine.runAndCollectFailures(testString);

        assertEquals(3, run.size());

        assertEquals("Input must contain 7Eleven : false", run.get("CheckRule2"));
        assertEquals("Input must contain GSC : false", run.get("CheckRule3"));
        assertEquals("Input must contain 711 : false", run.get("CheckRule4"));
    }

    @Test
    @DisplayName("4. Partial Success: Rule 1 passed, fail on Rule 2, stop execution")
    public void brFailure_stopOnFailure_True_one_pass() {
        String testString = "TEST_RULES";
        ruleEngine.stopOnAnyFailure(true);
        Map<String, String> run = ruleEngine.runAndCollectFailures(testString);

        assertEquals(1, run.size());
        assertTrue(run.containsKey("CheckRule2"));
        assertEquals("Input must contain 7Eleven : false", run.get("CheckRule2"));
    }

    @Test
    @DisplayName("5. Success (Compound - AND ): Rule 1 passed (len > 5), Rule 2 passed (contains 7Eleven)")
    public void brSuccess_compound_R1_AND_R2() throws InterruptedException {
        String testString = "This store has 7Eleven";
        ruleEngine = new RuleEngine<>(true);
        ruleEngine.addRule(brEngine.checkRule1.and(brEngine.checkRule2));
        Map<String, String> stringStringMap = ruleEngine.runAndCollectFailures(testString);
        assertEquals(0, stringStringMap.size());
    }

    @Test
    @DisplayName("6. Failure (Compound - AND ): Rule 1 passed, Rule 2 failed")
    public void brFailure_compound_R1_AND_R2() {
        String testString = "This store has Speedways";
        ruleEngine = new RuleEngine<>(true);
        ruleEngine.addRule(brEngine.checkRule1.and(brEngine.checkRule2));
        Map<String, String> run = ruleEngine.runAndCollectFailures(testString);

        assertEquals(1, run.size());
        assertTrue(run.values().iterator().next()
                .contains("Input must be longer than 5 characters : false and Input must contain 7Eleven : false"));
    }

    @Test
    @DisplayName("7. Success (Compound - OR ): Rule 1 passed (len > 5), Rule 2 passed (contains 7Eleven)")
    public void brSuccess_compound_R1_OR_R2() throws InterruptedException {
        String testString = "This store has 7Eleven";
        ruleEngine = new RuleEngine<>(true);
        ruleEngine.addRule(brEngine.checkRule1.or(brEngine.checkRule2));
        Map<String, String> stringStringMap = ruleEngine.runAndCollectFailures(testString);
        assertEquals(0, stringStringMap.size());
    }

    @Test
    @DisplayName("8. Success (Compound - OR ): Rule 1 passed (len > 5), Rule 2 Fail (not contains 7Eleven)")
    public void brSuccess_compound_R1_OR_R2Fail() throws InterruptedException {
        String testString = "This store has Speedways";
        ruleEngine = new RuleEngine<>(true);
        ruleEngine.addRule(brEngine.checkRule1.or(brEngine.checkRule2));
        Map<String, String> stringStringMap = ruleEngine.runAndCollectFailures(testString);
        assertEquals(0, stringStringMap.size());
    }

    @Test
    @DisplayName("9. Failure (Compound - OR ): Rule 2 passed (not contains 7Eleven), Rule 3 passed (contains GSC)")
    public void brSuccess_compound_R1Fail_OR_R2() throws InterruptedException {
        String testString = "This store has GSC";
        ruleEngine = new RuleEngine<>(true);
        ruleEngine.addRule(brEngine.checkRule2.or(brEngine.checkRule3));
        Map<String, String> stringStringMap = ruleEngine.runAndCollectFailures(testString);
        assertEquals(0, stringStringMap.size());
    }

    @Test
    @DisplayName("10. Failure (Compound - OR ): both fail")
    public void brFailure_compound_R1Fail_OR_R2Fail() {
        String testString = "This store has Fuels";
        ruleEngine = new RuleEngine<>(true);
        ruleEngine.addRule(brEngine.checkRule2.or(brEngine.checkRule3));
        Map<String, String> run = ruleEngine.runAndCollectFailures(testString);

        assertEquals(1, run.size());
        assertTrue(run.values().iterator().next()
                .contains("Input must contain 7Eleven : false OR Input must contain GSC : false"));
    }

    @Test
    @DisplayName("11. Success (Compound - negate ): Rule 2 failed (not contains 7Eleven) ")
    public void brSuccess_compound_R1Fail_Negate() throws InterruptedException {
        String testString = "This store has Fuels";
        ruleEngine = new RuleEngine<>();
        ruleEngine.addRule(brEngine.checkRule2.negate());
        Map<String, String> stringStringMap = ruleEngine.runAndCollectFailures(testString);
        assertEquals(0, stringStringMap.size());
    }
}
