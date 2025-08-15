package com.beingbachelor.rule_engine.engine;


import com.beingbachelor.rule_engine.rule.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class RuleEngine<T> {

    private static final Logger logger = Logger.getLogger(RuleEngine.class.getName());

    private final List<Rule<T>> rules = new ArrayList<>();
    private final ExecutorService executorService;
    private boolean stopOnAnyFailure = false;
    private final AtomicBoolean stopFurtherExecution = new AtomicBoolean(false);

    public RuleEngine() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public RuleEngine(boolean doParallelExecution) {
        this.executorService = doParallelExecution
                ? Executors.newFixedThreadPool(3)
                : Executors.newSingleThreadExecutor();
    }

    public RuleEngine<T> stopOnAnyFailure(boolean stopOnAnyFailure) {
        this.stopOnAnyFailure = stopOnAnyFailure;
        return this;
    }

    public RuleEngine<T> addRule(Rule<T> rule) {
        rules.add(rule);
        return this;
    }

    /**
     * Run only failed rules and return map of ruleName -> failure message
     */
    public Map<String, String> runAndCollectFailures(T input, Object... args) {
        return runInternal(input, true, args);
    }

    /**
     * Run all rules and return map of ruleName -> result message
     */
    public Map<String, String> runAndCollectAllResults(T input, Object... args) {
        return runInternal(input, false, args);
    }

    private Map<String, String> runInternal(T input, boolean onlyFailures, Object... args) {
        Map<String, String> resultMap = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Rule<T> rule : rules) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                if (stopFurtherExecution.get()) {
                    logger.info("Skipping rule due to stop flag.");
                    return;
                }

                String ruleName = rule.getRuleName();
                logger.info("Evaluating rule: " + ruleName);

                boolean passed = rule.apply(input, args);
                String message = rule.getMessage(passed);

                if (!passed) {
                    resultMap.put(ruleName, message);
                    logger.warning("Rule failed: " + ruleName + " -> " + message);

                    if (rule.stopOnFailure() || stopOnAnyFailure) {
                        logger.warning("Execution stopped due to failure in rule: " + ruleName);
                        stopFurtherExecution.set(true);
                    }
                } else {
                    if (!onlyFailures) {
                        resultMap.put(ruleName, message);
                    }
                    logger.info("Rule passed: " + ruleName + " -> " + message);
                }
            }, executorService);
            futures.add(future);
        }

        futures.forEach(CompletableFuture::join);
        logger.info("RuleEngine run completed. Total rules evaluated: " + resultMap.size());

        // Print the result map
        resultMap.forEach((ruleName, msg) -> logger.info(ruleName + " => " + msg));

        return resultMap;
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
