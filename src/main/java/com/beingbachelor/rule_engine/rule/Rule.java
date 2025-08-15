package com.beingbachelor.rule_engine.rule;

@FunctionalInterface
public interface Rule<T> {

    boolean apply(T input, Object... args);

    default String getMessage(boolean passed) {
        return passed ? "Rule passed" : "Rule failed";
    }

    default String getRuleName() {
        return this.getClass().getSimpleName(); // fallback
    }

    default boolean stopOnFailure() {
        return false; // Default is not to stop on failure
    }

    static <T> Rule<T> createRule(RuleLogic<T> ruleLogic,String ruleName, String message, boolean stopOnFailure) {
        return new Rule<T>() {
            @Override
            public boolean apply(T input, Object... args) {
                return ruleLogic.apply(input, args);
            }

            @Override
            public String getMessage(boolean passed) {
                return message +" : "+ passed;
            }

            @Override
            public boolean stopOnFailure() {
                return stopOnFailure;
            }

            @Override
            public String getRuleName() {
                return ruleName;
            }
        };
    }

    @FunctionalInterface
    interface RuleLogic<T> {
        boolean apply(T input, Object... args);
    }


    // Chaining methods - Anonymous Implementation of Rule
    default Rule<T> and(Rule<? super T> other) {
        return new Rule<T>() {
            @Override
            public boolean apply(T input, Object... args) {
                boolean thisResult = Rule.this.apply(input, args);
                boolean otherResult = other.apply(input, args);
                return thisResult && otherResult;
            }

            @Override
            public String getMessage(boolean passed) {
                if (passed) return Rule.this.getMessage(true) + " and " + other.getMessage(true);
                return Rule.this.getMessage(false) + " and " + other.getMessage(false);
            }
        };
    }

    default Rule<T> or(Rule<? super T> other) {
        return new Rule<T>() {
            @Override
            public boolean apply(T input, Object... args) {
                boolean thisResult = Rule.this.apply(input, args);
                boolean otherResult = other.apply(input, args);
                return thisResult || otherResult;
            }

            @Override
            public String getMessage(boolean passed) {
                if (passed) return Rule.this.getMessage(true) + " OR " + other.getMessage(true); // If the OR condition passed, return nothing

                // If both rules failed, combine their failure messages
                String thisMessage = Rule.this.getMessage(false);
                String otherMessage = other.getMessage(false);

                // Return both failure messages concatenated
                return thisMessage + " OR " + otherMessage;
            }
        };
    }

    default Rule<T> negate() {
        return new Rule<T>() {
            @Override
            public boolean apply(T input, Object... args) {
                return !Rule.this.apply(input, args);
            }

            @Override
            public String getMessage(boolean passed) {
                return Rule.this.getMessage(!passed);
            }
        };
    }
}
