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

import static com.shapesecurity.shift.es2017.utils.Utils.isIdentifierPart;
import static com.shapesecurity.shift.es2017.utils.Utils.isIdentifierStart;

public class PatternAcceptor {

    @Nonnull
    public final String pattern;
    public final boolean unicode;

    private static final String[] decimalDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static final String[] octalDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7"};
    private static final String[] hexDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "A", "B", "C", "D", "E", "F"};
    private static final String syntaxCharacters = "^$\\.*+?()[]{}|";
    private static final String[] syntaxCharacterArray = syntaxCharacters.split("");
    private static final String extendedSyntaxCharacters = "^$\\.*+?()[|";
    private static final String[] controlCharacters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private static final HashMap<String, Integer> controlEscapeCharacterValues = new HashMap<>();
    private static final HashSet<String> utf16GeneralCategoryValues = new HashSet<>(Arrays.asList("Cased_Letter", "LC", "Close_Punctuation", "Pe", "Connector_Punctuation", "Pc", "Control", "Cc", "cntrl", "Currency_Symbol", "Sc", "Dash_Punctuation", "Pd", "Decimal_Number", "Nd", "digit", "Enclosing_Mark", "Me", "Final_Punctuation", "Pf", "Format", "Cf", "Initial_Punctuation", "Pi", "Letter", "L", "Letter_Number", "Nl", "Line_Separator", "Zl", "Lowercase_Letter", "Ll", "Mark", "M", "Combining_Mark", "Math_Symbol", "Sm", "Modifier_Letter", "Lm", "Modifier_Symbol", "Sk", "Nonspacing_Mark", "Mn", "Number", "N", "Open_Punctuation", "Ps", "Other", "C", "Other_Letter", "Lo", "Other_Number", "No", "Other_Punctuation", "Po", "Other_Symbol", "So", "Paragraph_Separator", "Zp", "Private_Use", "Co", "Punctuation", "P", "punct", "Separator", "Z", "Space_Separator", "Zs", "Spacing_Mark", "Mc", "Surrogate", "Cs", "Symbol", "S", "Titlecase_Letter", "Lt", "Unassigned", "Cn", "Uppercase_Letter", "Lu"));
    private static final HashSet<String> utf16ScriptCategoryValues = new HashSet<>(Arrays.asList("Adlam", "Adlm", "Ahom", "Anatolian_Hieroglyphs", "Hluw", "Arabic", "Arab", "Armenian", "Armn", "Avestan", "Avst", "Balinese", "Bali", "Bamum", "Bamu", "Bassa_Vah", "Bass", "Batak", "Batk", "Bengali", "Beng", "Bhaiksuki", "Bhks", "Bopomofo", "Bopo", "Brahmi", "Brah", "Braille", "Brai", "Buginese", "Bugi", "Buhid", "Buhd", "Canadian_Aboriginal", "Cans", "Carian", "Cari", "Caucasian_Albanian", "Aghb", "Chakma", "Cakm", "Cham", "Cherokee", "Cher", "Common", "Zyyy", "Coptic", "Copt", "Qaac", "Cuneiform", "Xsux", "Cypriot", "Cprt", "Cyrillic", "Cyrl", "Deseret", "Dsrt", "Devanagari", "Deva", "Dogra", "Dogr", "Duployan", "Dupl", "Egyptian_Hieroglyphs", "Egyp", "Elbasan", "Elba", "Ethiopic", "Ethi", "Georgian", "Geor", "Glagolitic", "Glag", "Gothic", "Goth", "Grantha", "Gran", "Greek", "Grek", "Gujarati", "Gujr", "Gunjala_Gondi", "Gong", "Gurmukhi", "Guru", "Han", "Hani", "Hangul", "Hang", "Hanifi_Rohingya", "Rohg", "Hanunoo", "Hano", "Hatran", "Hatr", "Hebrew", "Hebr", "Hiragana", "Hira", "Imperial_Aramaic", "Armi", "Inherited", "Zinh", "Qaai", "Inscriptional_Pahlavi", "Phli", "Inscriptional_Parthian", "Prti", "Javanese", "Java", "Kaithi", "Kthi", "Kannada", "Knda", "Katakana", "Kana", "Kayah_Li", "Kali", "Kharoshthi", "Khar", "Khmer", "Khmr", "Khojki", "Khoj", "Khudawadi", "Sind", "Lao", "Laoo", "Latin", "Latn", "Lepcha", "Lepc", "Limbu", "Limb", "Linear_A", "Lina", "Linear_B", "Linb", "Lisu", "Lycian", "Lyci", "Lydian", "Lydi", "Mahajani", "Mahj", "Makasar", "Maka", "Malayalam", "Mlym", "Mandaic", "Mand", "Manichaean", "Mani", "Marchen", "Marc", "Medefaidrin", "Medf", "Masaram_Gondi", "Gonm", "Meetei_Mayek", "Mtei", "Mende_Kikakui", "Mend", "Meroitic_Cursive", "Merc", "Meroitic_Hieroglyphs", "Mero", "Miao", "Plrd", "Modi", "Mongolian", "Mong", "Mro", "Mroo", "Multani", "Mult", "Myanmar", "Mymr", "Nabataean", "Nbat", "New_Tai_Lue", "Talu", "Newa", "Nko", "Nkoo", "Nushu", "Nshu", "Ogham", "Ogam", "Ol_Chiki", "Olck", "Old_Hungarian", "Hung", "Old_Italic", "Ital", "Old_North_Arabian", "Narb", "Old_Permic", "Perm", "Old_Persian", "Xpeo", "Old_Sogdian", "Sogo", "Old_South_Arabian", "Sarb", "Old_Turkic", "Orkh", "Oriya", "Orya", "Osage", "Osge", "Osmanya", "Osma", "Pahawh_Hmong", "Hmng", "Palmyrene", "Palm", "Pau_Cin_Hau", "Pauc", "Phags_Pa", "Phag", "Phoenician", "Phnx", "Psalter_Pahlavi", "Phlp", "Rejang", "Rjng", "Runic", "Runr", "Samaritan", "Samr", "Saurashtra", "Saur", "Sharada", "Shrd", "Shavian", "Shaw", "Siddham", "Sidd", "SignWriting", "Sgnw", "Sinhala", "Sinh", "Sogdian", "Sogd", "Sora_Sompeng", "Sora", "Soyombo", "Soyo", "Sundanese", "Sund", "Syloti_Nagri", "Sylo", "Syriac", "Syrc", "Tagalog", "Tglg", "Tagbanwa", "Tagb", "Tai_Le", "Tale", "Tai_Tham", "Lana", "Tai_Viet", "Tavt", "Takri", "Takr", "Tamil", "Taml", "Tangut", "Tang", "Telugu", "Telu", "Thaana", "Thaa", "Thai", "Tibetan", "Tibt", "Tifinagh", "Tfng", "Tirhuta", "Tirh", "Ugaritic", "Ugar", "Vai", "Vaii", "Warang_Citi", "Wara", "Yi", "Yiii", "Zanabazar_Square", "Zanb"));
    private static final HashSet<String> utf16LonePropertyValues = new HashSet<>(Arrays.asList("ASCII", "ASCII_Hex_Digit", "AHex", "Alphabetic", "Alpha", "Any", "Assigned", "Bidi_Control", "Bidi_C", "Bidi_Mirrored", "Bidi_M", "Case_Ignorable", "CI", "Cased", "Changes_When_Casefolded", "CWCF", "Changes_When_Casemapped", "CWCM", "Changes_When_Lowercased", "CWL", "Changes_When_NFKC_Casefolded", "CWKCF", "Changes_When_Titlecased", "CWT", "Changes_When_Uppercased", "CWU", "Dash", "Default_Ignorable_Code_Point", "DI", "Deprecated", "Dep", "Diacritic", "Dia", "Emoji", "Emoji_Component", "Emoji_Modifier", "Emoji_Modifier_Base", "Emoji_Presentation", "Extended_Pictographic", "Extender", "Ext", "Grapheme_Base", "Gr_Base", "Grapheme_Extend", "Gr_Ext", "Hex_Digit", "Hex", "IDS_Binary_Operator", "IDSB", "IDS_Trinary_Operator", "IDST", "ID_Continue", "IDC", "ID_Start", "IDS", "Ideographic", "Ideo", "Join_Control", "Join_C", "Logical_Order_Exception", "LOE", "Lowercase", "Lower", "Math", "Noncharacter_Code_Point", "NChar", "Pattern_Syntax", "Pat_Syn", "Pattern_White_Space", "Pat_WS", "Quotation_Mark", "QMark", "Radical", "Regional_Indicator", "RI", "Sentence_Terminal", "STerm", "Soft_Dotted", "SD", "Terminal_Punctuation", "Term", "Unified_Ideograph", "UIdeo", "Uppercase", "Upper", "Variation_Selector", "VS", "White_Space", "space", "XID_Continue", "XIDC", "XID_Start", "XIDS"));
    private static final HashMap<String, HashSet<String>> utf16NonBinaryPropertyNames = new HashMap<>();

    static {
        controlEscapeCharacterValues.put("f", (int) '\f');
        controlEscapeCharacterValues.put("n", (int) '\n');
        controlEscapeCharacterValues.put("r", (int) '\r');
        controlEscapeCharacterValues.put("t", (int) '\t');
        controlEscapeCharacterValues.put("v", 0x11); // \v in javascript

        utf16LonePropertyValues.addAll(utf16GeneralCategoryValues);
        utf16NonBinaryPropertyNames.put("General_Category", utf16GeneralCategoryValues);
        utf16NonBinaryPropertyNames.put("gc", utf16GeneralCategoryValues);
        utf16NonBinaryPropertyNames.put("Script", utf16ScriptCategoryValues);
        utf16NonBinaryPropertyNames.put("sc", utf16ScriptCategoryValues);
        utf16NonBinaryPropertyNames.put("Script_Extensions", utf16ScriptCategoryValues);
        utf16NonBinaryPropertyNames.put("scx", utf16ScriptCategoryValues);
    }

    private static final String[] controlEscapeCharacters = controlEscapeCharacterValues.keySet().toArray(new String[0]);
    private static final ImmutableSet<String> blockedIdentityEscapes = ImmutableSet.ofUsingEquality("c", "k");

    private class State {
        private int index;
        @Nonnull
        private ImmutableSet<String> backreferenceNames;
        @Nonnull
        private ImmutableSet<String> groupingNames;
        private int largestBackreference;
        private int capturingGroups;
        // \\k with no group name is only illegal when no group names are found
        private boolean failedNamedBackreferenceParse;
        // set of blocked identity escapes (defaults to "\c" and can contain "\k")

        private State(@Nonnull State state) {
            this.index = state.index;
            this.backreferenceNames = state.backreferenceNames;
            this.groupingNames = state.groupingNames;
            this.largestBackreference = state.largestBackreference;
            this.capturingGroups = state.capturingGroups;
            this.failedNamedBackreferenceParse = state.failedNamedBackreferenceParse;
        }

        public State() {
            this.index = 0;
            this.backreferenceNames = ImmutableSet.emptyUsingEquality();
            this.groupingNames = ImmutableSet.emptyUsingEquality();
            this.largestBackreference = 0;
            this.capturingGroups = 0;
            this.failedNamedBackreferenceParse = false;
        }

        public void backreferenceName(@Nonnull String name) {
            this.backreferenceNames = this.backreferenceNames.put(name);
        }

        public void backreference(int num) {
            if (num > this.largestBackreference) {
                this.largestBackreference = num;
            }
        }

        public boolean hasGroupingNames() {
            return groupingNames.length() > 0;
        }

        public boolean verifyBackreferences() {
            if (PatternAcceptor.this.unicode) {
                if (this.failedNamedBackreferenceParse || this.largestBackreference > this.capturingGroups) {
                    return false;
                }
            }
            // "\k" is an invalid escape anywhere if we have a grouping name defined anywhere.
            if (this.failedNamedBackreferenceParse && this.hasGroupingNames()) {
                return false;
            }
            if (this.hasGroupingNames() || PatternAcceptor.this.unicode) {
                for (String backreferenceName : this.backreferenceNames) {
                    if (!groupingNames.contains(backreferenceName)) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Nonnull
        public State backtrackOnFailure() {
            return new State(this);
        }

        public boolean backtrackOnFailure(@Nonnull F<State, Boolean> predicate) {
            State state = this.backtrackOnFailure();
            boolean accepted = predicate.apply(state);
            if (accepted) {
                this.absorb(state);
            }
            return accepted;
        }

        @Nonnull
        public <B> Maybe<B> backtrackOnFailureMaybe(@Nonnull F<State, Maybe<B>> predicate) {
            State state = this.backtrackOnFailure();
            Maybe<B> accepted = predicate.apply(state);
            if (accepted.isJust()) {
                this.absorb(state);
            }
            return accepted;
        }

        private void absorb(@Nonnull State otherState) {
            this.index = otherState.index;
            this.backreferenceNames = otherState.backreferenceNames;
            this.largestBackreference = otherState.largestBackreference;
            this.groupingNames = otherState.groupingNames;
            this.capturingGroups = otherState.capturingGroups;
            this.failedNamedBackreferenceParse = otherState.failedNamedBackreferenceParse;
        }

        @Nonnull
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

        @Nonnull
        public Maybe<String> eatAny(@Nonnull String... strings) {
            for (String string : strings) {
                if (this.eat(string)) {
                    return Maybe.of(string);
                }
            }
            return Maybe.empty();
        }

        @Nonnull
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

        @Nonnull
        public String collect(@Nonnull String[]... stringArrays) {
            return collect(Maybe.empty(), stringArrays);
        }

        @Nonnull
        public String collect(Maybe<Integer> limit, @Nonnull String[]... stringArrays) {
            StringBuilder stringBuilder = new StringBuilder();
            boolean isJust = limit.isJust();
            int justLimit = limit.orJust(0);
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


        private Maybe<String> eatUnicodeOrCharacter() {
            if (this.empty()) {
                return Maybe.empty();
            }
            return this.backtrackOnFailureMaybe(state -> {
                if (state.match("\\u")) {
                    state.skipCodePoint();
                    Maybe<Integer> maybeCharacterValue = acceptUnicodeEscape(state);
                    return maybeCharacterValue.map(value -> new String(Character.toChars(value)));
                }
                Maybe<String> character = state.nextCodePoint();
                if (character.isJust()) {
                    state.skipCodePoint();
                }
                return character;
            });
        }

        @Nonnull
        public Maybe<Integer> eatIdentifierStart() {
            if (this.empty()) {
                return Maybe.empty();
            }
            return this.backtrackOnFailureMaybe(state -> {
                Maybe<String> maybeCharacter = state.eatUnicodeOrCharacter();
                if (maybeCharacter.isNothing()) {
                    return Maybe.empty();
                }
                String character = maybeCharacter.fromJust();
                if (character.equals("_") || character.equals("$") || isIdentifierStart(character.codePointAt(0))) {
                    return Maybe.of(character.codePointAt(0));
                }
                return Maybe.empty();
            });
        }

        @Nonnull
        public Maybe<Integer> eatIdentifierPart() {
            if (this.empty()) {
                return Maybe.empty();
            }
            return this.backtrackOnFailureMaybe(state -> {
                Maybe<String> maybeCharacter = state.eatUnicodeOrCharacter();
                if (maybeCharacter.isNothing()) {
                    return Maybe.empty();
                }
                String character = maybeCharacter.fromJust();
                if (character.equals("\\u200C") || character.equals("\\u200D") || character.equals("$") || isIdentifierPart(character.codePointAt(0))) {
                    return Maybe.of(character.codePointAt(0));
                }
                return Maybe.empty();
            });
        }

        public void flagFailedNamedBackreferenceParse() {
            this.failedNamedBackreferenceParse = true;
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
            return state.eat(terminator.fromJust());
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
                        return subState.eatAny("?=", "?!", "?<=", "?<!").isJust();
                    }
                    return subState.eatAny("?<=", "?<!").isJust();
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
                state.backtrackOnFailure(subState -> {
                    if (!subState.eat("\\")) {
                        return false;
                    }
                    return subState.match("c");
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
            Maybe<String> groupName = state.backtrackOnFailureMaybe(subState -> {
                if (!subState.eat("?")) {
                    return Maybe.empty();
                }
                return acceptGroupName(subState);
            });
            if (!acceptDisjunction(state, Maybe.of(")"))) {
                return false;
            }
            if (groupName.isJust()) {
                if (state.groupingNames.contains(groupName.fromJust())) {
                    return false;
                }
                state.groupingNames = state.groupingNames.put(groupName.fromJust());
            }
            ++state.capturingGroups;
            return true;
        });
    }

    private boolean acceptGroupNameBackreference(State superState) {
        return superState.backtrackOnFailure(state -> {
            if (!state.eat("k")) {
                return false;
            }
            Maybe<String> name = acceptGroupName(state);
            if (name.isNothing()) {
                state.flagFailedNamedBackreferenceParse();
                // keep going but fail later if we find a grouping name definition for non-unicode and when we haven't matched a group yet
                return !this.unicode && !state.hasGroupingNames();
            }
            state.backreferenceName(name.fromJust());
            return true;
        });
    }

    private Maybe<String> acceptGroupName(State superState) {
        return superState.backtrackOnFailureMaybe(state -> {
            if (!state.eat("<")) {
                return Maybe.empty();
            }
            Maybe<String> start = state.eatIdentifierStart().map(i -> new String(Character.toChars(i)));
            if (start.isNothing()) {
                return Maybe.empty();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(start.fromJust());
            Maybe<String> part;
            while ((part = state.eatIdentifierPart().map(i -> new String(Character.toChars(i)))).isJust()) {
                stringBuilder.append(part.fromJust());
            }
            if (!state.eat(">")) {
                return Maybe.empty();
            }
            return Maybe.of(stringBuilder.toString());
        });
    }


    private boolean acceptAtomEscape(State state) {
        return acceptDecimalEscape(state) ||
            acceptCharacterClassEscape(state) ||
            acceptCharacterEscape(state).map(i -> true).orJust(false) ||
            acceptGroupNameBackreference(state);
    }

    private boolean acceptDecimalEscape(State superState) {
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

    private boolean acceptCharacterClassEscape(State state) {
        if (state.eatAny("d", "D", "s", "S", "w", "W").isJust()) {
            return true;
        }
        return this.unicode && state.backtrackOnFailure(subState -> {
            if(!(subState.eat("p{") || subState.eat("P{"))) {
                return false;
            }
            if (!acceptUnicodePropertyValueExpression(subState)) {
                return false;
            }
            return subState.eat("}");
        });
    }


    private String acceptUnicodePropertyName(State state) {
        return state.collect(controlCharacters, new String[]{"_"});
    }

    private String acceptUnicodePropertyValue(State state) {
        return state.collect(controlCharacters, decimalDigits, new String[]{"_"});
    }

    private boolean acceptLoneUnicodePropertyNameOrValue(State state) {
        return utf16LonePropertyValues.contains(acceptUnicodePropertyValue(state));
    }

    private boolean acceptUnicodePropertyValueExpression(State superState) {
        return superState.backtrackOnFailure(state -> {
            String name = acceptUnicodePropertyName(state);
            if (name.length() == 0 || !state.eat("=")) {
                return false;
            }
            String value = acceptUnicodePropertyValue(state);
            if (value.length() == 0) {
                return false;
            }
            HashSet<String> nonBinaryNames = utf16NonBinaryPropertyNames.get(name);
            if (nonBinaryNames == null) {
                return false;
            }
            return nonBinaryNames.contains(value);
        }) || superState.backtrackOnFailure(this::acceptLoneUnicodePropertyNameOrValue);
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

            if (this.unicode && value >= 0xD800 && value <= 0xDBFF) {
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
                        return Maybe.of(octal1.fromJust() << 6 | octal2.fromJust() << 3 | octal3.fromJust());
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
                    if (maybeCharacter.isJust() && !blockedIdentityEscapes.contains(maybeCharacter.fromJust())) {
                        subState.skipCodePoint();
                        return Maybe.of(maybeCharacter.fromJust().codePointAt(0));
                    }
                    return Maybe.empty();
                })
        ).apply(superState);
    }

    // all `Maybe<Maybe<Integer>>` types represent matched, not terminating, and contain in the character value for ranges
    private Maybe<Maybe<Integer>> acceptClassEscape(State superState) {
        return this.<Maybe<Integer>>anyOf(
            state -> state.backtrackOnFailureMaybe(subState -> {
                    if (!subState.eat("b")) {
                        return Maybe.empty();
                    }
                    return Maybe.of(0x0008); // backspace
                }).map(Maybe::of),
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
