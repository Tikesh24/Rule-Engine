# Rule Engine

## Overview

This Rule Engine is a flexible Java-based framework designed to validate inputs against a set of business rules. It supports both sequential and parallel execution of rules, rule composition (e.g., `and`, `or`, `negate`), and configurable stop-on-failure behavior at both the rule and engine levels.

## Features

- **Sequential and Parallel Execution**: Run rules either in a single-threaded or multi-threaded mode.
- **Stop-on-Failure**: Configure rules and the engine to stop execution when a rule fails.
- **Customizable Rule Messages**: Define custom failure messages for each rule.
- **Compositional Rules**: Combine rules using logical `and`, `or`, or `negate` operations.
- **JUnit Testing**: Easily test rule logic using JUnit integration.

## Table of Contents

- [Usage](#usage)
    - [Creating Rules](#creating-rules)
    - [Composing Rules](#composing-rules)
    - [Rule Engine Configuration](#rule-engine-configuration)
    - [Parallel Execution](#parallel-execution)
    - [Stop-on-Failure](#stop-on-failure)
- [JUnit Testing](#junit-testing)

## Usage

### Creating Rules

Rules are created using a lambda expression that defines the validation logic. The createRule method takes the rule logic, a custom error message, and a flag to specify whether to stop on failure.

```java
Rule<String> rule1 = Rule.createRule(
    input -> input.length() > 5,
    "Input must be longer than 5 characters",
    false // Continue on failure
);

Rule<String> rule2 = Rule.createRule(
    input -> input.contains("7Eleven"),
    "Input must contain '7Eleven'",
    true // Stop on failure
);
```


### Composing Rules

You can combine rules with logical operations such as and, or, and negate.

```java
Rule<String> combinedRule = rule1.and(rule2);
```

### Rule Engine Configuration

To run the rules on a specific input, initialize a RuleEngine, add the desired rules, and execute them.

```java
RuleEngine<String> engine = new RuleEngine<>();
engine.addRule(rule1).addRule(rule2);

List<String> results = engine.run("7Eleven Store");

if (results.isEmpty()) {
    System.out.println("All rules passed.");
} else {
    results.forEach(System.out::println);
}
```

### Parallel Execution

For performance improvements, the Rule Engine supports parallel execution. Specify the number of threads when creating the engine:

```java
RuleEngine<String> engine = new RuleEngine<>(3);
```

### Stop-on-Failure

By default, the engine can run all rules even if one fails. However, you can configure it to stop at the first failure:

```java
engine.stopOnAnyFailure(true);
```

### JUnit Testing

JUnit can be used to validate rule logic and rule engine behavior. Here's a simple test case to check if all rules pass:

```java
@Test
@DisplayName("Success: All rules pass")
public void testRulesSuccess() throws InterruptedException {
    RuleEngine<String> engine = new RuleEngine<>(2);
    engine.addRule(rule1).addRule(rule2);

    List<String> results = engine.run("7Eleven Store");
    assertTrue(results.isEmpty());
}
```