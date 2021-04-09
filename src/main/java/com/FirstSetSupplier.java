package com;

import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;


import static java.util.stream.Collectors.*;

/** Small first-set-calculator for tiny grammar, see {}*/
public class FirstSetSupplier {

    public static final char EPSILON = '$';

    final public class Rule {
        @RequiredArgsConstructor
        final class RuleEdgePointerMapping {
            @Getter
            private final Character[] rule;
            @Getter
            @Setter
            private int edge = 1;
            @Getter
            private final Set<Character> firstSet = new HashSet<>();

            public void addToFirstSet(final Character token) {
                hasChanged |= firstSet.add(token);
            }

            public void addToFirstSet(final Set<Character> tokens) {
                hasChanged |= firstSet.addAll(tokens);
            }
        }

        @Getter
        private final Character name;
        @Getter
        private final List<RuleEdgePointerMapping> rules;

        public Rule(final char aName, final Collection<String> aRules) {
            name = aName;
            rules = new ArrayList<>(aRules.size());
            for (final String ruleStr : aRules) {
                char[] rule = ruleStr.toCharArray();
                Character[] tmpRule = new Character[rule.length];
                System.arraycopy(ArrayUtils.toObject(rule), 0, tmpRule, 0, rule.length);
                rules.add(new RuleEdgePointerMapping(tmpRule));
            }
        }
    }

    public Set<Character> getFirstSetForRule(final Character aCharacter) {
        return grammar.get(aCharacter).getRules().stream()
                                                 .map(Rule.RuleEdgePointerMapping::getFirstSet)
                                                 .flatMap(Collection::stream)
                                                 .collect(toSet());
    }

    private final Map<Character, Rule> grammar = new HashMap<>();
    private boolean hasChanged = true;

    public FirstSetSupplier(final Multimap<Character, String> aRules) {
        for (Map.Entry<Character, Collection<String>> rule : aRules.asMap().entrySet()) {
            grammar.put(rule.getKey() , new Rule(rule.getKey(), rule.getValue()));
        }
    }

    boolean hasChanged() {
        boolean result = hasChanged;
        hasChanged = false;
        return result;
    }

    boolean isTerminal(final Character token) {
        return !Character.isUpperCase(token);
    }

    boolean isEpsilon(final Character s) {
        return s.equals(EPSILON);
    }

    public void determineFollow() {
        while (hasChanged()) {
            for (final Map.Entry<Character, Rule> rule : grammar.entrySet()) {
                for (final Rule.RuleEdgePointerMapping ruleEdgePointerMapping : rule.getValue().getRules()) {
                    determineFollowSetFromRule(rule.getValue(), ruleEdgePointerMapping);
                }
            }
        }
    }

    void determineFollowSetFromRule(final Rule rule, final Rule.RuleEdgePointerMapping currentRuleEdgePointerMapping) {
        if (isEpsilon(currentRuleEdgePointerMapping.getRule()[0])) {
            currentRuleEdgePointerMapping.addToFirstSet(currentRuleEdgePointerMapping.getRule()[0]);
        } else {
            for (int i = 0; i < currentRuleEdgePointerMapping.getEdge() && i < currentRuleEdgePointerMapping.getRule().length; i++) {
                if (isTerminal(currentRuleEdgePointerMapping.getRule()[i])) {
                    currentRuleEdgePointerMapping.addToFirstSet(currentRuleEdgePointerMapping.getRule()[i]);
                    return;
                } else {
                    if (rule.getName().equals(currentRuleEdgePointerMapping.getRule()[i])) {
                        for (Rule.RuleEdgePointerMapping altRuleEdgesPointerMapping : rule.getRules().stream()
                                                                         .filter(rE -> !rE.equals(currentRuleEdgePointerMapping))
                                                                         .collect(toList())) {
                            if (i == (currentRuleEdgePointerMapping.getEdge() - 1)) {
                                if (altRuleEdgesPointerMapping.getFirstSet().contains(EPSILON)) {
                                    currentRuleEdgePointerMapping.setEdge(currentRuleEdgePointerMapping.getEdge() + 1);
                                    continue;
                                }
                            }
                            currentRuleEdgePointerMapping.addToFirstSet(altRuleEdgesPointerMapping.getFirstSet().stream()
                                    .filter(token -> !isEpsilon(token))
                                    .collect(toSet()));
                        }
                    } else {
                        Set<Character> first = grammar.get(currentRuleEdgePointerMapping.getRule()[i]).getRules().stream()
                                                                                                   .map(Rule.RuleEdgePointerMapping::getFirstSet)
                                                                                                   .flatMap(Collection::stream)
                                                                                                   .collect(toSet());
                        if (i == (currentRuleEdgePointerMapping.getEdge() - 1)) {
                            if (first.contains(EPSILON)) {
                                currentRuleEdgePointerMapping.setEdge(currentRuleEdgePointerMapping.getEdge() + 1);
                            }
                        }

                        currentRuleEdgePointerMapping.addToFirstSet(first.stream()
                                                           .filter(token -> !isEpsilon(token))
                                                           .collect(toSet()));
                    }
                    //No more tokens available & all firstsets til here containing $
                    if (currentRuleEdgePointerMapping.getEdge() == currentRuleEdgePointerMapping.getRule().length + 1) {
                        currentRuleEdgePointerMapping.addToFirstSet(EPSILON);
                    }
                }
            }
        }
    }
}

