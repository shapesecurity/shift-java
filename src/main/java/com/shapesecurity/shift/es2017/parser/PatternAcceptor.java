/**
 * Copyright 2018 Shape Security, Inc. <p> Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at <p> http://www.apache.org/licenses/LICENSE-2.0 <p> Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package com.shapesecurity.shift.es2017.parser;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.data.ImmutableSet;
import com.shapesecurity.functional.data.Maybe;

import javax.annotation.Nonnull;

import java.util.*;

public class PatternAcceptor {

    public final String pattern;
    public final boolean unicode;

    private static final String[] decimalDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static final String[] octalDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7"};
    private static final String[] hexDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "A", "B", "C", "D", "E", "F"};
    private static final String syntaxCharacters = "^$\\.*+?()[]{}|";
    private static final String[] syntaxCharacterArray = syntaxCharacters.split("");
    private static final String extendedSyntaxCharacters = "^$.*+?()[|";
    private static final String[] controlCharacters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private static final HashMap<String, Integer> controlEscapeCharacterValues = new HashMap<>();

    static {
        controlEscapeCharacterValues.put("f", (int) '\f');
        controlEscapeCharacterValues.put("n", (int) '\n');
        controlEscapeCharacterValues.put("r", (int) '\r');
        controlEscapeCharacterValues.put("t", (int) '\t');
        controlEscapeCharacterValues.put("v", 0x11); // \v in javascript
    }

    private static final String[] controlEscapeCharacters = controlEscapeCharacterValues.keySet().toArray(new String[0]);
    
    private class State {
        private int index;
        private ImmutableSet<String> backreferenceNames;
        private ImmutableSet<String> groupingNames;
        private int largestBackreference;
        private int capturingGroups;

        private State(@Nonnull State state) {
            this.index = state.index;
            this.backreferenceNames = state.backreferenceNames;
            this.groupingNames = state.groupingNames;
            this.largestBackreference = state.largestBackreference;
            this.capturingGroups = state.capturingGroups;
        }

        public State() {
            this.index = 0;
            this.backreferenceNames = ImmutableSet.emptyUsingEquality();
            this.groupingNames = ImmutableSet.emptyUsingEquality();
            this.largestBackreference = 0;
            this.capturingGroups = 0;
        }

        public void backreference(int num) {
            if (num > this.largestBackreference) {
                this.largestBackreference = num;
            }
        }

        public boolean verifyBackreferences() {
            if (PatternAcceptor.this.unicode) {
                if (this.largestBackreference > this.capturingGroups) {
                    return false;
                }
            }
            for (String backreferenceName : this.backreferenceNames) {
                if (!groupingNames.contains(backreferenceName)) {
                    return false;
                }
            }
            return true;
        }

        public State backtrackOnFailure() {
            return new State(this);
        }

        public boolean backtrackOnFailure(F<State, Boolean> predicate) {
            State state = this.backtrackOnFailure();
            boolean accepted = predicate.apply(state);
            if (accepted) {
                this.absorb(state);
            }
            return accepted;
        }

        public <B> Maybe<B> backtrackOnFailureMaybe(F<State, Maybe<B>> predicate) {
            State state = this.backtrackOnFailure();
            Maybe<B> accepted = predicate.apply(state);
            if (accepted.isJust()) {
                this.absorb(state);
            }
            return accepted;
        }

        private void absorb(State otherState) {
            this.index = otherState.index;
            this.backreferenceNames = otherState.backreferenceNames;
            this.largestBackreference = otherState.largestBackreference;
            this.groupingNames = otherState.groupingNames;
            this.capturingGroups = otherState.capturingGroups;
        }

        public Maybe<String> nextCodePoint() {
            if (this.index >= pattern.length()) {
                return Maybe.empty();
            }
            if (!PatternAcceptor.this.unicode) {
                return Maybe.of(new String(Character.toChars(pattern.charAt(this.index))));
            }
            return Maybe.of(new String(Character.toChars(pattern.codePointAt(this.index))));
        }

        public void skipCodePoint() {
            this.nextCodePoint().foreach(cp -> this.index += cp.length());
        }

        public boolean eat(String str) {
            if (this.index + str.length() > pattern.length() || !pattern.startsWith(str, this.index)) {
                return false;
            }
            this.index += str.length();
            return true;
        }

        public Maybe<String> eatAny(@Nonnull String... strings) {
            for (String string : strings) {
                if (this.eat(string)) {
                    return Maybe.of(string);
                }
            }
            return Maybe.empty();
        }

        public Maybe<String> eatAny(@Nonnull String[]... stringArrays) {
            for (String[] strings : stringArrays) {
                for (String string : strings) {
                    if (this.eat(string)) {
                        return Maybe.of(string);
                    }
                }
            }
            return Maybe.empty();
        }

        public String collect(@Nonnull String[]... stringArrays) {
            return collect(Maybe.empty(), stringArrays);
        }

        public String collect(Maybe<Integer> limit, @Nonnull String[]... stringArrays) {
            StringBuilder stringBuilder = new StringBuilder();
            boolean isJust = limit.isJust();
            int justLimit = limit.fromJust();
            outer:
            for (int i = 0; !isJust || justLimit > i; ++i) {
                for (String[] strings : stringArrays) {
                    for (String string : strings) {
                        if (this.eat(string)) {
                            stringBuilder.append(string);
                            continue outer;
                        }
                    }
                }
                break;
            }
            return stringBuilder.toString();
        }

        public boolean match(@Nonnull String str) {
            return this.index + str.length() <= pattern.length() && pattern.startsWith(str, this.index);
        }

        public boolean matchAny(@Nonnull String... strings) {
            for (String string : strings) {
                if (this.match(string)) {
                    return true;
                }
            }
            return false;
        }

        public boolean empty() {
            return this.index >= pattern.length();
        }

    }

    private PatternAcceptor(@Nonnull String pattern, boolean unicode) {
        this.pattern = pattern;
        this.unicode = unicode;
    }

    public static boolean acceptRegex(@Nonnull String pattern, boolean uFlag) {
        PatternAcceptor acceptor = new PatternAcceptor(pattern, uFlag);
        return acceptor.acceptRegex();
    }

    private boolean acceptRegex() {
        State state = new State();
        if (!acceptDisjunction(state, Maybe.empty())) {
            return false;
        }
        return state.verifyBackreferences();
    }

    @SafeVarargs
    private final <B> F<State, Maybe<B>> anyOf(F<State, Maybe<B>>... expressions) {
        return state -> {
            for (F<State, Maybe<B>> expression : expressions) {
                Maybe<B> value = expression.apply(state);
                if (value.isJust()) {
                    return value;
                }
            }
            return Maybe.empty();
        };
    }

    private boolean acceptDisjunction(State state, Maybe<String> terminator) {
        do {
            if (terminator.isJust() && state.eat(terminator.fromJust())) {
                return true;
            } else if (state.match("|")) {
                continue;
            }
            if (!acceptAlternative(state, terminator)) {
                return false;
            }
        } while(state.eat("|"));
        if (terminator.isJust()) {
            if (!state.eat(terminator.fromJust())) {
                return false;
            }
        }
        return true;
    }

    private boolean acceptAlternative(State state, Maybe<String> terminator) {
        while (!state.match("|") && !state.empty() && (terminator.isNothing() || !state.match(terminator.fromJust()))) {
            if (!acceptTerm(state)) {
                return false;
            }
        }
        return true;
    }

    private boolean acceptTerm(State state) {
        // non-quantified references are rolled into quantified accepts to improve performance significantly.
        if (this.unicode) {
            return acceptAssertion(state) ||
                    acceptQuantified(this::acceptAtom).apply(state);
        }
        return acceptQuantified(this::acceptQuantifiableAssertion).apply(state) ||
                acceptAssertion(state) ||
                acceptQuantified(this::acceptAtom).apply(state);
    }

    private F<State, Boolean> acceptLabeledGroup(F<State, Boolean> predicate) {
        return currentState -> currentState.backtrackOnFailure(state -> {
            if (!state.eat("(")) {
                return false;
            }
            if (predicate.apply(state)) {
               return acceptDisjunction(state, Maybe.of(")"));
            }
            return false;
        });
    }

    private boolean acceptAssertion(State state) {
        return state.eatAny("^", "$", "\\b", "\\B").isJust() ||
                acceptLabeledGroup(subState -> {
                    if (this.unicode) {
                        return subState.eatAny("?=", "?!").isJust();
                    }
                    return false;
                }).apply(state);
    }

    private boolean acceptQuantifiableAssertion(State state) {
        return acceptLabeledGroup(subState -> subState.eatAny("?=", "?!").isJust()).apply(state);
    }

    private boolean acceptDecimal(State state) {
        return state.collect(decimalDigits).length() > 0;
    }

    private F<State, Boolean> acceptQuantified(F<State, Boolean> acceptor) {
        return superState -> superState.backtrackOnFailure(state ->{
            if (!acceptor.apply(state)) {
                return false;
            }
            if (state.match("{")) {
                return state.backtrackOnFailure(subState -> {
                    if (!subState.eat("{")) {
                        return false;
                    }
                    String decimal1 = subState.collect(decimalDigits);
                    if (decimal1.length() == 0) {
                        return false;
                    }
                    if (subState.eat(",") && subState.matchAny(decimalDigits)) {
                        String decimal2 = subState.collect(decimalDigits);
                        if (Integer.parseInt(decimal1) > Integer.parseInt(decimal2)) {
                            return false;
                        }
                    }
                    if (!subState.eat("}")) {
                        return false;
                    }
                    subState.eat("?");
                    return true;
                }) || !this.unicode;
            } else if (state.eatAny("*", "+", "?").isJust()) {
                state.eat("?");
            }
            return true;
        });
    }

    private boolean acceptPatternCharacter(State state) {
        Maybe<String> nextCodePoint = state.nextCodePoint();
        if (nextCodePoint.isNothing() || syntaxCharacters.contains(nextCodePoint.fromJust())) {
            return false;
        }
        state.skipCodePoint();
        return true;
    }

    private boolean acceptExtendedPatternCharacter(State state) {
        Maybe<String> nextCodePoint = state.nextCodePoint();
        if (nextCodePoint.isNothing() || extendedSyntaxCharacters.contains(nextCodePoint.fromJust())) {
            return false;
        }
        state.skipCodePoint();
        return true;
    }

    private boolean acceptInvalidBracedQuantifier(State state) {
        return state.backtrackOnFailure(subState -> {
            if (!subState.eat("{")) {
                return false;
            }
            if (!acceptDecimal(subState)) {
                return false;
            }
            if (subState.eat(",") && subState.matchAny(decimalDigits) && !acceptDecimal(subState)) {
                return false;
            }
            return subState.eat("}");
        });
    }

    private boolean acceptAtom(State state) {
        if (this.unicode) {
            return acceptPatternCharacter(state) ||
                    state.eat(".") ||
                    state.backtrackOnFailure(subState -> {
                        if (!subState.eat("\\")) {
                            return false;
                        }
                        return acceptAtomEscape(subState);
                    }) ||
                    acceptCharacterClass(state) ||
                    acceptLabeledGroup(subState -> subState.eat("?:")).apply(state) ||
                    acceptGrouping(state);
        }
        boolean matched = state.eat(".") ||
                state.backtrackOnFailure(subState -> {
                    if (!subState.eat("\\")) {
                        return false;
                    }
                    return acceptAtomEscape(subState);
                }) ||
                acceptCharacterClass(state) ||
                acceptLabeledGroup(subState -> subState.eat("?:")).apply(state) ||
                acceptGrouping(state);
        if (!matched && acceptInvalidBracedQuantifier(state)) {
            return false;
        }
        return matched || acceptExtendedPatternCharacter(state);
    }

    private boolean acceptGrouping(State superState) {
        return superState.backtrackOnFailure(state -> {
            if (!state.eat("(")) {
                return false;
            }
            if (!acceptDisjunction(state, Maybe.of(")"))) {
                return false;
            }
            ++state.capturingGroups;
            return true;
        });
    }

    private boolean acceptAtomEscape(State state) {
        return acceptDecimalEscapeBackreference(state) ||
                acceptCharacterClassEscape(state) ||
                acceptCharacterEscape(state).map(i -> true).orJust(false);
    }

    private boolean acceptDecimalEscapeBackreference(State superState) {
        return superState.backtrackOnFailure(state -> {
            StringBuilder digits = new StringBuilder();
            Maybe<String> firstDecimal = state.eatAny(decimalDigits);
            if (firstDecimal.isNothing()) {
                return false;
            }
            if (firstDecimal.fromJust().equals("0")) {
                return true;
            }
            digits.append(firstDecimal.fromJust());
            Maybe<String> digit;
            while ((digit = state.eatAny(decimalDigits)).isJust()) {
                digits.append(digit.fromJust());
            }
            state.backreference(Integer.parseInt(digits.toString()));
            return true;
        });
    }

    @Nonnull
    private Maybe<Integer> acceptDecimalEscape(State superState) {
        return superState.backtrackOnFailureMaybe(state -> {
            StringBuilder digits = new StringBuilder();
            Maybe<String> firstDigit = state.eatAny(decimalDigits);
            if (firstDigit.isNothing()) {
                return Maybe.empty();
            }
            if (firstDigit.isJust() && firstDigit.fromJust().equals("0")) {
                return Maybe.of(0);
            }
            if (this.unicode) {
                return Maybe.empty();
            }
            digits.append(firstDigit.fromJust());
            Maybe<String> digit = state.eatAny(decimalDigits);
            if (digit.isJust()) {
                String justDigit = digit.fromJust();
                digits.append(justDigit);
                if (firstDigit.fromJust().equals("1")) {
                    if (justDigit.equals("0") || justDigit.equals("1")) {
                        state.eatAny(decimalDigits).foreach(digits::append);
                    } else if (justDigit.equals("2")) {
                        state.eatAny(octalDigits).foreach(digits::append);
                    }
                }
            }
            return Maybe.of(Integer.parseInt(digits.toString()));
        });
    }

    private boolean acceptCharacterClassEscape(State state) {
        return state.eatAny("d", "D", "s", "S", "w", "W").isJust();
    }

    @Nonnull
    private Maybe<Integer> acceptUnicodeEscape(State superState) {
        return superState.backtrackOnFailureMaybe(state -> {
            if (!state.eat("u")) {
                return Maybe.empty();
            }
            if (this.unicode && state.eat("{")) {
                String hex = state.collect(hexDigits);
                if (!state.eat("}")) {
                    return Maybe.empty();
                }
                int value = Integer.parseInt(hex, 16);
                return value > 0x10FFFF ? Maybe.empty() : Maybe.of(value);
            }
            String hex = state.collect(Maybe.of(4), hexDigits);
            if (hex.length() != 4) {
                return Maybe.empty();
            }
            int value = Integer.parseInt(hex, 16);

            if (value >= 0xD800 && value <= 0xDBFF) {
                Maybe<Integer> surrogatePairValue = state.backtrackOnFailureMaybe(subState -> {
                    if (!subState.eat("\\u")) {
                        return Maybe.empty();
                    }
                    String hex2 = subState.collect(Maybe.of(4), hexDigits);
                    if (hex2.length() != 4) {
                        return Maybe.empty();
                    }
                    int value2 = Integer.parseInt(hex2, 16);
                    if (value2 < 0xDC00 || value2 >= 0xE000) {
                        return Maybe.empty();
                    }
                    return Maybe.of(0x10000 + ((value & 0x3FF) << 10) + (value2 & 0x03FF));
                });
                if (surrogatePairValue.isJust()) {
                    return surrogatePairValue;
                }
            }
            return Maybe.of(value);
        });
    }

    private Maybe<Integer> acceptCharacterEscape(State superState) {
        return anyOf(
            state -> {
                    Maybe<String> escaped = state.eatAny(controlEscapeCharacters);
                    if (escaped.isNothing() || !controlEscapeCharacterValues.containsKey(escaped.fromJust())) {
                        return Maybe.empty();
                    }
                    return Maybe.of(controlEscapeCharacterValues.get(escaped.fromJust()));
                },
            state -> state.backtrackOnFailureMaybe(subState -> {
                    if (!subState.eat("c")) {
                        return Maybe.empty();
                    }
                    Maybe<String> character = subState.eatAny(controlCharacters);
                    if (character.isNothing()) {
                        return Maybe.empty();
                    }
                    return Maybe.of(character.fromJust().charAt(0) % 32);
                }),
            state -> state.backtrackOnFailureMaybe(subState -> {
                    if (!subState.eat("0")) {
                        return Maybe.empty();
                    }
                    if (subState.eatAny(decimalDigits).isJust()) {
                        return Maybe.empty();
                    }
                    return Maybe.of(0);
                }),
            state -> state.backtrackOnFailureMaybe(subState -> {
                    if (!subState.eat("x")) {
                        return Maybe.empty();
                    }
                    String hex = subState.collect(Maybe.of(2), hexDigits);
                    if (hex.length() != 2) {
                        return Maybe.empty();
                    }
                    return Maybe.of(Integer.parseInt(hex, 16));
                }),
                this::acceptUnicodeEscape,
            state -> state.backtrackOnFailureMaybe(subState -> {
                    if (this.unicode) {
                        return Maybe.empty();
                    }
                    F<State, Maybe<Integer>> acceptOctalDigit = subState2 -> subState2.backtrackOnFailureMaybe(subState3 -> {
                        Maybe<String> octal2 = subState3.eatAny(octalDigits);
                        if (octal2.isNothing()) {
                            return Maybe.empty();
                        }
                        return Maybe.of(Integer.parseInt(octal2.fromJust(), 8));
                    });
                    Maybe<Integer> octal1 = acceptOctalDigit.apply(subState);
                    if (octal1.isNothing()) {
                        return Maybe.empty();
                    }
                    Maybe<Integer> octal2 = acceptOctalDigit.apply(subState);
                    if (octal2.isNothing()) {
                        return octal1;
                    } else if (octal1.fromJust() < 4) {
                        Maybe<Integer> octal3 = acceptOctalDigit.apply(subState);
                        if (octal3.isNothing()) {
                            return Maybe.of(octal1.fromJust() << 3 | octal2.fromJust());
                        }
                        return Maybe.of(octal1.fromJust() << 6 | octal2.fromJust() << 3 | octal1.fromJust());
                    } else {
                        return Maybe.of(octal1.fromJust() << 3 | octal2.fromJust());
                    }
                }),
            state -> state.backtrackOnFailureMaybe(subState -> {
                    if (!this.unicode) {
                        return Maybe.empty();
                    }
                    Maybe<String> maybeCharacter = subState.eatAny(syntaxCharacterArray);
                    return maybeCharacter.map(character -> character.codePointAt(0));
                }),
            state -> {
                    if(this.unicode && state.eat("/")) {
                        return Maybe.of((int) "/".charAt(0));
                    }
                    return Maybe.empty();
                },
            state -> state.backtrackOnFailureMaybe(subState -> {
                    if (this.unicode) {
                        return Maybe.empty();
                    }
                    Maybe<String> maybeCharacter = subState.nextCodePoint();
                    if (maybeCharacter.isJust() && !maybeCharacter.fromJust().equals("c")) {
                        subState.skipCodePoint();
                        return Maybe.of(maybeCharacter.fromJust().codePointAt(0));
                    }
                    return Maybe.empty();
                })
        ).apply(superState);
    }

    private Maybe<Maybe<Integer>> acceptClassEscape(State superState) {
        return this.<Maybe<Integer>>anyOf(
            state -> state.backtrackOnFailureMaybe(subState -> {
                    if (!subState.eat("b")) {
                        return Maybe.empty();
                    }
                    return Maybe.of(0x0008); // backspace
                }).map(Maybe::of),
            state -> acceptDecimalEscape(state).map(Maybe::of),
            state -> {
                    if (this.unicode && state.eat("-")) {
                        return Maybe.of(Maybe.of((int)"-".charAt(0)));
                    }
                    return Maybe.empty();
                },
            state -> state.backtrackOnFailureMaybe(subState -> {
                    if (this.unicode || !subState.eat("c")) {
                        return Maybe.empty();
                    }
                    return subState.eatAny(decimalDigits, new String[]{"_"}).map(str -> str.codePointAt(0) % 32);
                }).map(Maybe::of),
            state -> acceptCharacterClassEscape(state) ? Maybe.of(Maybe.empty()) : Maybe.empty(),
            state -> acceptCharacterEscape(state).map(Maybe::of)
        ).apply(superState);
    }

    private Maybe<Maybe<Integer>> acceptClassAtomNoDash(State state) {
        if (state.eat("\\")) {
			return this.anyOf(
					this::acceptClassEscape,
                subState -> subState.backtrackOnFailureMaybe(subState2 -> {
						if (!this.unicode && subState2.match("c")) {
							return Maybe.of(0x005C); // reverse solidus
						}
						return Maybe.empty();
					}).map(Maybe::of)
			).apply(state);
        }
        Maybe<String> nextCodePoint = state.nextCodePoint();
        if (nextCodePoint.isNothing() || nextCodePoint.fromJust().equals("]") || nextCodePoint.fromJust().equals("-")) {
            return Maybe.empty();
        }
        state.skipCodePoint();
        return Maybe.of(Maybe.of(nextCodePoint.fromJust().codePointAt(0)));
    }

    private Maybe<Maybe<Integer>> acceptClassAtom(State state) {
        if (state.eat("-")) {
            return Maybe.of(Maybe.of((int)"-".charAt(0)));
        }
        return acceptClassAtomNoDash(state);
    }

    private Maybe<Maybe<Integer>> finishClassRange(State state, Maybe<Integer> atom) {
        if (state.eat("-")) {
            if (state.match("]")) {
                return Maybe.of(Maybe.empty()); // terminate gracefully
            }
            Maybe<Maybe<Integer>> otherAtom = acceptClassAtom(state);
            if (otherAtom.isNothing()) {
                return Maybe.empty();
            }
            Maybe<Integer> justOtherAtom = otherAtom.fromJust();
            if (this.unicode && (atom.isNothing() || justOtherAtom.isNothing())) {
                return Maybe.empty();
            } else if (!(!this.unicode && (atom.isNothing() || justOtherAtom.isNothing())) && atom.fromJust() > justOtherAtom.fromJust()) {
                return Maybe.empty();
            } else if (state.match("]")) {
                return Maybe.of(Maybe.empty());
            }
            return acceptNonEmptyClassRanges(state);
        }
        if (state.match("]")) {
            return Maybe.of(Maybe.empty());
        }
        return acceptNonEmptyClassRangesNoDash(state);
    }

    private Maybe<Maybe<Integer>> acceptNonEmptyClassRanges(State state) {
        Maybe<Maybe<Integer>> atom = acceptClassAtom(state);
        if (atom.isNothing()) {
            return Maybe.empty();
        }
        return finishClassRange(state, atom.fromJust());
    }

    private Maybe<Maybe<Integer>> acceptNonEmptyClassRangesNoDash(State state) {
        if (state.eat("-") && !state.match("]")) {
            return Maybe.empty();
        }
        Maybe<Maybe<Integer>> atom = acceptClassAtomNoDash(state);
        if (atom.isNothing()) {
            return Maybe.empty();
        }
        return finishClassRange(state, atom.fromJust());
    }

    // all `Maybe<Maybe<Integer>>` types represent matched, not terminating, and contain in the character value for ranges
    private boolean acceptCharacterClass(State superState) {
        return superState.backtrackOnFailure(state -> {
            if (!state.eat("[")) {
                return false;
            }
            state.eat("^");
            if (state.eat("]")) {
                return true;
            }
            if (acceptNonEmptyClassRanges(state).isJust()) {
                return state.eat("]");
            }
            return false;
        });
    }

}
