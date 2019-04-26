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

    private static HashMap<String, Integer> constructControlEscapeCharacterValues() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("f", (int) '\f');
        map.put("n", (int) '\n');
        map.put("r", (int) '\r');
        map.put("t", (int) '\t');
        map.put("v", 0x11); // \v in javascript
        return map;
    }

    private static final HashMap<String, Integer> controlEscapeCharacterValues = constructControlEscapeCharacterValues();

    private static final String[] controlEscapeCharacters = controlEscapeCharacterValues.keySet().toArray(new String[0]);
    
    private class Context {
        private int index;
        private ImmutableSet<String> backreferenceNames;
        private ImmutableSet<String> groupingNames;
        private int largestBackreference;
        private int capturingGroups;

        private Context(@Nonnull Context context) {
            this.index = context.index;
            this.backreferenceNames = context.backreferenceNames;
            this.groupingNames = context.groupingNames;
            this.largestBackreference = context.largestBackreference;
            this.capturingGroups = context.capturingGroups;
        }

        public Context() {
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

        public Context goDeeper() {
            return new Context(this);
        }

        public boolean goDeeper(F<Context, Boolean> predicate) {
            Context context = this.goDeeper();
            boolean accepted = predicate.apply(context);
            if (accepted) {
                this.absorb(context);
            }
            return accepted;
        }

        public <B> Maybe<B> goDeeperMaybe(F<Context, Maybe<B>> predicate) {
            Context context = this.goDeeper();
            Maybe<B> accepted = predicate.apply(context);
            if (accepted.isJust()) {
                this.absorb(context);
            }
            return accepted;
        }

        private void absorb(Context otherContext) {
            this.index = otherContext.index;
            this.backreferenceNames = otherContext.backreferenceNames;
            this.largestBackreference = otherContext.largestBackreference;
            this.groupingNames = otherContext.groupingNames;
            this.capturingGroups = otherContext.capturingGroups;
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
            return collect(-1, stringArrays);
        }

        public String collect(int limit, @Nonnull String[]... stringArrays) {
            StringBuilder stringBuilder = new StringBuilder();

            outer:
            for (int i = 0; limit < 0 || i < limit; ++i) {
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
        Context context = new Context();
        if (!acceptDisjunction(context, Maybe.empty())) {
            return false;
        }
        return context.verifyBackreferences();
    }

    @SafeVarargs
    private final <B> F<Context, Maybe<B>> maybeLogicalOr(F<Context, Maybe<B>>... expressions) {
        return context -> {
            for (F<Context, Maybe<B>> expression : expressions) {
                Maybe<B> value = expression.apply(context);
                if (value.isJust()) {
                    return value;
                }
            }
            return Maybe.empty();
        };
    }

    private boolean acceptDisjunction(Context context, Maybe<String> terminator) {
        do {
            if (terminator.isJust() && context.eat(terminator.fromJust())) {
                return true;
            } else if (context.match("|")) {
                continue;
            }
            if (!acceptAlternative(context, terminator)) {
                return false;
            }
        } while(context.eat("|"));
        if (terminator.isJust()) {
            if (!context.eat(terminator.fromJust())) {
                return false;
            }
        }
        return true;
    }

    private boolean acceptAlternative(Context context, Maybe<String> terminator) {
        while (!context.match("|") && !context.empty() && (terminator.isNothing() || !context.match(terminator.fromJust()))) {
            if (!acceptTerm(context)) {
                return false;
            }
        }
        return true;
    }

    private boolean acceptTerm(Context context) {
        // non-quantified references are rolled into quantified accepts to improve performance significantly.
        if (this.unicode) {
            return acceptAssertion(context) ||
                    acceptQuantified(this::acceptAtom).apply(context);
        }
        return acceptQuantified(this::acceptQuantifiableAssertion).apply(context) ||
                acceptAssertion(context) ||
                acceptQuantified(this::acceptAtom).apply(context);
    }

    private F<Context, Boolean> acceptLabeledGroup(F<Context, Boolean> predicate) {
        return currentContext -> currentContext.goDeeper(context -> {
            if (!context.eat("(")) {
                return false;
            }
            if (predicate.apply(context)) {
               return acceptDisjunction(context, Maybe.of(")"));
            }
            return false;
        });
    }

    private boolean acceptAssertion(Context context) {
        return context.eatAny("^", "$", "\\b", "\\B").isJust() ||
                acceptLabeledGroup(subContext -> {
                    if (this.unicode) {
                        return subContext.eatAny("?=", "?!").isJust();
                    }
                    return false;
                }).apply(context);
    }

    private boolean acceptQuantifiableAssertion(Context context) {
        return acceptLabeledGroup(subContext -> subContext.eatAny("?=", "?!").isJust()).apply(context);
    }

    private boolean acceptDecimal(Context context) {
        return context.collect(decimalDigits).length() > 0;
    }

    private F<Context, Boolean> acceptQuantified(F<Context, Boolean> acceptor) {
        return superContext -> superContext.goDeeper(context ->{
            if (!acceptor.apply(context)) {
                return false;
            }
            if (context.match("{")) {
                return context.goDeeper(subContext -> {
                    if (!subContext.eat("{")) {
                        return false;
                    }
                    String decimal1 = subContext.collect(decimalDigits);
                    if (decimal1.length() == 0) {
                        return false;
                    }
                    if (subContext.eat(",") && subContext.matchAny(decimalDigits)) {
                        String decimal2 = subContext.collect(decimalDigits);
                        if (Integer.parseInt(decimal1) > Integer.parseInt(decimal2)) {
                            return false;
                        }
                    }
                    if (!subContext.eat("}")) {
                        return false;
                    }
                    subContext.eat("?");
                    return true;
                }) || !this.unicode;
            } else if (context.eatAny("*", "+", "?").isJust()) {
                context.eat("?");
            }
            return true;
        });
    }

    private boolean acceptPatternCharacter(Context context) {
        Maybe<String> nextCodePoint = context.nextCodePoint();
        if (nextCodePoint.isNothing() || syntaxCharacters.contains(nextCodePoint.fromJust())) {
            return false;
        }
        context.skipCodePoint();
        return true;
    }

    private boolean acceptExtendedPatternCharacter(Context context) {
        Maybe<String> nextCodePoint = context.nextCodePoint();
        if (nextCodePoint.isNothing() || extendedSyntaxCharacters.contains(nextCodePoint.fromJust())) {
            return false;
        }
        context.skipCodePoint();
        return true;
    }

    private boolean acceptInvalidBracedQuantifier(Context context) {
        return context.goDeeper(subContext -> {
            if (!subContext.eat("{")) {
                return false;
            }
            if (!acceptDecimal(subContext)) {
                return false;
            }
            if (subContext.eat(",") && subContext.matchAny(decimalDigits) && !acceptDecimal(subContext)) {
                return false;
            }
            return subContext.eat("}");
        });
    }

    private boolean acceptAtom(Context context) {
        if (this.unicode) {
            return acceptPatternCharacter(context) ||
                    context.eat(".") ||
                    context.goDeeper(subContext -> {
                        if (!subContext.eat("\\")) {
                            return false;
                        }
                        return acceptAtomEscape(subContext);
                    }) ||
                    acceptCharacterClass(context) ||
                    acceptLabeledGroup(subContext -> subContext.eat("?:")).apply(context) ||
                    acceptGrouping(context);
        }
        boolean matched = context.eat(".") ||
                context.goDeeper(subContext -> {
                    if (!subContext.eat("\\")) {
                        return false;
                    }
                    return acceptAtomEscape(subContext);
                }) ||
                acceptCharacterClass(context) ||
                acceptLabeledGroup(subContext -> subContext.eat("?:")).apply(context) ||
                acceptGrouping(context);
        if (!matched && acceptInvalidBracedQuantifier(context)) {
            return false;
        }
        return matched || acceptExtendedPatternCharacter(context);
    }

    private boolean acceptGrouping(Context superContext) {
        return superContext.goDeeper(context -> {
            if (!context.eat("(")) {
                return false;
            }
            if (!acceptDisjunction(context, Maybe.of(")"))) {
                return false;
            }
            ++context.capturingGroups;
            return true;
        });
    }

    private boolean acceptAtomEscape(Context context) {
        return acceptDecimalEscapeBackreference(context) ||
                acceptCharacterClassEscape(context) ||
                acceptCharacterEscape(context).map(i -> true).orJust(false);
    }

    private boolean acceptDecimalEscapeBackreference(Context superContext) {
        return superContext.goDeeper(context -> {
            StringBuilder digits = new StringBuilder();
            Maybe<String> firstDecimal = context.eatAny(decimalDigits);
            if (firstDecimal.isNothing()) {
                return false;
            }
            if (firstDecimal.fromJust().equals("0")) {
                return true;
            }
            digits.append(firstDecimal.fromJust());
            Maybe<String> digit;
            while ((digit = context.eatAny(decimalDigits)).isJust()) {
                digits.append(digit.fromJust());
            }
            context.backreference(Integer.parseInt(digits.toString()));
            return true;
        });
    }

    @Nonnull
    private Maybe<Integer> acceptDecimalEscape(Context superContext) {
        return superContext.goDeeperMaybe(context -> {
            StringBuilder digits = new StringBuilder();
            Maybe<String> firstDigit = context.eatAny(decimalDigits);
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
            Maybe<String> digit = context.eatAny(decimalDigits);
            if (digit.isJust()) {
                String justDigit = digit.fromJust();
                digits.append(justDigit);
                if (firstDigit.fromJust().equals("1")) {
                    if (justDigit.equals("0") || justDigit.equals("1")) {
                        context.eatAny(decimalDigits).foreach(digits::append);
                    } else if (justDigit.equals("2")) {
                        context.eatAny(octalDigits).foreach(digits::append);
                    }
                }
            }
            return Maybe.of(Integer.parseInt(digits.toString()));
        });
    }

    private boolean acceptCharacterClassEscape(Context context) {
        return context.eatAny("d", "D", "s", "S", "w", "W").isJust();
    }

    @Nonnull
    private Maybe<Integer> acceptUnicodeEscape(Context superContext) {
        return superContext.goDeeperMaybe(context -> {
            if (!context.eat("u")) {
                return Maybe.empty();
            }
            if (this.unicode && context.eat("{")) {
                String hex = context.collect(hexDigits);
                if (!context.eat("}")) {
                    return Maybe.empty();
                }
                int value = Integer.parseInt(hex, 16);
                return value > 0x10FFFF ? Maybe.empty() : Maybe.of(value);
            }
            String hex = context.collect(4, hexDigits);
            if (hex.length() != 4) {
                return Maybe.empty();
            }
            int value = Integer.parseInt(hex, 16);

            if (value >= 0xD800 && value <= 0xDBFF) {
                Maybe<Integer> surrogatePairValue = context.goDeeperMaybe(subContext -> {
                    if (!subContext.eat("\\u")) {
                        return Maybe.empty();
                    }
                    String hex2 = subContext.collect(4, hexDigits);
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

    private Maybe<Integer> acceptCharacterEscape(Context superContext) {
        return maybeLogicalOr(
                context -> {
                    Maybe<String> escaped = context.eatAny(controlEscapeCharacters);
                    if (escaped.isNothing() || !controlEscapeCharacterValues.containsKey(escaped.fromJust())) {
                        return Maybe.empty();
                    }
                    return Maybe.of(controlEscapeCharacterValues.get(escaped.fromJust()));
                },
                context -> context.goDeeperMaybe(subContext -> {
                    if (!subContext.eat("c")) {
                        return Maybe.empty();
                    }
                    Maybe<String> character = subContext.eatAny(controlCharacters);
                    if (character.isNothing()) {
                        return Maybe.empty();
                    }
                    return Maybe.of(character.fromJust().charAt(0) % 32);
                }),
                context -> context.goDeeperMaybe(subContext -> {
                    if (!subContext.eat("0")) {
                        return Maybe.empty();
                    }
                    if (subContext.eatAny(decimalDigits).isJust()) {
                        return Maybe.empty();
                    }
                    return Maybe.of(0);
                }),
                context -> context.goDeeperMaybe(subContext -> {
                    if (!subContext.eat("x")) {
                        return Maybe.empty();
                    }
                    String hex = subContext.collect(2, hexDigits);
                    if (hex.length() != 2) {
                        return Maybe.empty();
                    }
                    return Maybe.of(Integer.parseInt(hex, 16));
                }),
                this::acceptUnicodeEscape,
                context -> context.goDeeperMaybe(subContext -> {
                    if (this.unicode) {
                        return Maybe.empty();
                    }
                    F<Context, Maybe<Integer>> acceptOctalDigit = subContext2 -> subContext2.goDeeperMaybe(subContext3 -> {
                        Maybe<String> octal2 = subContext3.eatAny(octalDigits);
                        if (octal2.isNothing()) {
                            return Maybe.empty();
                        }
                        return Maybe.of(Integer.parseInt(octal2.fromJust(), 8));
                    });
                    Maybe<Integer> octal1 = acceptOctalDigit.apply(subContext);
                    if (octal1.isNothing()) {
                        return Maybe.empty();
                    }
                    Maybe<Integer> octal2 = acceptOctalDigit.apply(subContext);
                    if (octal2.isNothing()) {
                        return octal1;
                    } else if (octal1.fromJust() < 4) {
                        Maybe<Integer> octal3 = acceptOctalDigit.apply(subContext);
                        if (octal3.isNothing()) {
                            return Maybe.of(octal1.fromJust() << 3 | octal2.fromJust());
                        }
                        return Maybe.of(octal1.fromJust() << 6 | octal2.fromJust() << 3 | octal1.fromJust());
                    } else {
                        return Maybe.of(octal1.fromJust() << 3 | octal2.fromJust());
                    }
                }),
                context -> context.goDeeperMaybe(subContext -> {
                    if (!this.unicode) {
                        return Maybe.empty();
                    }
                    Maybe<String> maybeCharacter = subContext.eatAny(syntaxCharacterArray);
                    return maybeCharacter.map(character -> character.codePointAt(0));
                }),
                context -> {
                    if(this.unicode && context.eat("/")) {
                        return Maybe.of((int) "/".charAt(0));
                    }
                    return Maybe.empty();
                },
                context -> context.goDeeperMaybe(subContext -> {
                    if (this.unicode) {
                        return Maybe.empty();
                    }
                    Maybe<String> maybeCharacter = subContext.nextCodePoint();
                    if (maybeCharacter.isJust() && !maybeCharacter.fromJust().equals("c")) {
                        subContext.skipCodePoint();
                        return Maybe.of(maybeCharacter.fromJust().codePointAt(0));
                    }
                    return Maybe.empty();
                })
        ).apply(superContext);
    }

    private Maybe<Integer> acceptClassEscape(Context superContext) {
        return this.maybeLogicalOr(
                context -> context.goDeeperMaybe(subContext -> {
                    if (!subContext.eat("b")) {
                        return Maybe.empty();
                    }
                    return Maybe.of(0x0008); // backspace
                }),
                PatternAcceptor.this::acceptDecimalEscape,
                context -> {
                    if (this.unicode && context.eat("-")) {
                        return Maybe.of((int)"-".charAt(0));
                    }
                    return Maybe.empty();
                },
                context -> context.goDeeperMaybe(subContext -> {
                    if (this.unicode || !subContext.eat("c")) {
                        return Maybe.empty();
                    }
                    return subContext.eatAny(decimalDigits, new String[]{"_"}).map(str -> str.codePointAt(0) % 32);
                }),
                context -> acceptCharacterClassEscape(context) ? Maybe.of(-1) : Maybe.empty(),
                this::acceptCharacterEscape
        ).apply(superContext);
    }

    private Maybe<Integer> acceptClassAtomNoDash(Context context) {
        if (context.eat("\\")) {
			return this.maybeLogicalOr(
					this::acceptClassEscape,
					subContext -> subContext.goDeeperMaybe(subContext2 -> {
						if (!this.unicode && subContext2.match("c")) {
							return Maybe.of(0x005C); // reverse solidus
						}
						return Maybe.empty();
					})
			).apply(context);
        }
        Maybe<String> nextCodePoint = context.nextCodePoint();
        if (nextCodePoint.isNothing() || nextCodePoint.fromJust().equals("]") || nextCodePoint.fromJust().equals("-")) {
            return Maybe.empty();
        }
        context.skipCodePoint();
        return Maybe.of(nextCodePoint.fromJust().codePointAt(0));
    }

    private Maybe<Integer> acceptClassAtom(Context context) {
        if (context.eat("-")) {
            return Maybe.of((int)"-".charAt(0));
        }
        return acceptClassAtomNoDash(context);
    }

    private Maybe<Integer> finishClassRange(Context context, int atom) {
        if (context.eat("-")) {
            if (context.match("]")) {
                return Maybe.of(-1); // termination sentinel
            }
            Maybe<Integer> otherAtom = acceptClassAtom(context);
            if (otherAtom.isNothing()) {
                return Maybe.empty();
            }
            if (this.unicode && (atom == -1 || otherAtom.fromJust() == -1)) {
                return Maybe.empty();
            } else if (!(!this.unicode && (atom == -1 || otherAtom.fromJust() == -1)) && atom > otherAtom.fromJust()) {
                return Maybe.empty();
            } else if (context.match("]")) {
                return Maybe.of(-1);
            }
            return acceptNonEmptyClassRanges(context);
        }
        if (context.match("]")) {
            return Maybe.of(-1);
        }
        return acceptNonEmptyClassRangesNoDash(context);
    }

    private Maybe<Integer> acceptNonEmptyClassRanges(Context context) {
        Maybe<Integer> atom = acceptClassAtom(context);
        if (atom.isNothing()) {
            return Maybe.empty();
        }
        return finishClassRange(context, atom.fromJust());
    }

    private Maybe<Integer> acceptNonEmptyClassRangesNoDash(Context context) {
        if (context.eat("-") && !context.match("]")) {
            return Maybe.empty();
        }
        Maybe<Integer> atom = acceptClassAtomNoDash(context);
        if (atom.isNothing()) {
            return Maybe.empty();
        }
        return finishClassRange(context, atom.fromJust());
    }

    private boolean acceptCharacterClass(Context superContext) {
        return superContext.goDeeper(context -> {
            if (!context.eat("[")) {
                return false;
            }
            context.eat("^");
            if (context.eat("]")) {
                return true;
            }
            if (acceptNonEmptyClassRanges(context).isJust()) {
                if (!context.eat("]")) {
                    return false;
                }
                return true;
            }
            return false;
        });
    }

}
