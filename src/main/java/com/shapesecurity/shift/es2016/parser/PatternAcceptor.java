/**
 * Copyright 2018 Shape Security, Inc. <p> Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at <p> http://www.apache.org/licenses/LICENSE-2.0 <p> Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package com.shapesecurity.shift.es2016.parser;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.data.Maybe;

import javax.annotation.Nonnull;

import java.util.*;

import static com.shapesecurity.shift.es2016.utils.Utils.isIdentifierPart;
import static com.shapesecurity.shift.es2016.utils.Utils.isIdentifierStart;

public class PatternAcceptor {

    public final String pattern;
    public final boolean gFlag;
    public final boolean iFlag;
    public final boolean mFlag;
    public final boolean yFlag;
    public final boolean uFlag;

    private static final String[] decimalDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static final String[] octalDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7"};
    private static final String[] hexDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "A", "B", "C", "D", "E", "F"};
    private static final String syntaxCharacters = "^$\\.*+?()[]{}|";
    private static final String[] syntaxCharacterArray = "^$\\.*+?()[]{}|".split("");
    private static final String extendedSyntaxCharacters = "^$\\.*+?()[|";
    private static final String[] controlCharacters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private static final HashSet<String> utf16GeneralCategoryValues = new HashSet<>(Arrays.asList("Cased_Letter", "LC", "Close_Punctuation", "Pe", "Connector_Punctuation", "Pc", "Control", "Cc", "cntrl", "Currency_Symbol", "Sc", "Dash_Punctuation", "Pd", "Decimal_Number", "Nd", "digit", "Enclosing_Mark", "Me", "Final_Punctuation", "Pf", "Format", "Cf", "Initial_Punctuation", "Pi", "Letter", "L", "Letter_Number", "Nl", "Line_Separator", "Zl", "Lowercase_Letter", "Ll", "Mark", "M", "Combining_Mark", "Math_Symbol", "Sm", "Modifier_Letter", "Lm", "Modifier_Symbol", "Sk", "Nonspacing_Mark", "Mn", "Number", "N", "Open_Punctuation", "Ps", "Other", "C", "Other_Letter", "Lo", "Other_Number", "No", "Other_Punctuation", "Po", "Other_Symbol", "So", "Paragraph_Separator", "Zp", "Private_Use", "Co", "Punctuation", "P", "punct", "Separator", "Z", "Space_Separator", "Zs", "Spacing_Mark", "Mc", "Surrogate", "Cs", "Symbol", "S", "Titlecase_Letter", "Lt", "Unassigned", "Cn", "Uppercase_Letter", "Lu"));
    private static final HashSet<String> utf16ScriptCategoryValues = new HashSet<>(Arrays.asList("Adlam", "Adlm", "Ahom", "Anatolian_Hieroglyphs", "Hluw", "Arabic", "Arab", "Armenian", "Armn", "Avestan", "Avst", "Balinese", "Bali", "Bamum", "Bamu", "Bassa_Vah", "Bass", "Batak", "Batk", "Bengali", "Beng", "Bhaiksuki", "Bhks", "Bopomofo", "Bopo", "Brahmi", "Brah", "Braille", "Brai", "Buginese", "Bugi", "Buhid", "Buhd", "Canadian_Aboriginal", "Cans", "Carian", "Cari", "Caucasian_Albanian", "Aghb", "Chakma", "Cakm", "Cham", "Cherokee", "Cher", "Common", "Zyyy", "Coptic", "Copt", "Qaac", "Cuneiform", "Xsux", "Cypriot", "Cprt", "Cyrillic", "Cyrl", "Deseret", "Dsrt", "Devanagari", "Deva", "Dogra", "Dogr", "Duployan", "Dupl", "Egyptian_Hieroglyphs", "Egyp", "Elbasan", "Elba", "Ethiopic", "Ethi", "Georgian", "Geor", "Glagolitic", "Glag", "Gothic", "Goth", "Grantha", "Gran", "Greek", "Grek", "Gujarati", "Gujr", "Gunjala_Gondi", "Gong", "Gurmukhi", "Guru", "Han", "Hani", "Hangul", "Hang", "Hanifi_Rohingya", "Rohg", "Hanunoo", "Hano", "Hatran", "Hatr", "Hebrew", "Hebr", "Hiragana", "Hira", "Imperial_Aramaic", "Armi", "Inherited", "Zinh", "Qaai", "Inscriptional_Pahlavi", "Phli", "Inscriptional_Parthian", "Prti", "Javanese", "Java", "Kaithi", "Kthi", "Kannada", "Knda", "Katakana", "Kana", "Kayah_Li", "Kali", "Kharoshthi", "Khar", "Khmer", "Khmr", "Khojki", "Khoj", "Khudawadi", "Sind", "Lao", "Laoo", "Latin", "Latn", "Lepcha", "Lepc", "Limbu", "Limb", "Linear_A", "Lina", "Linear_B", "Linb", "Lisu", "Lycian", "Lyci", "Lydian", "Lydi", "Mahajani", "Mahj", "Makasar", "Maka", "Malayalam", "Mlym", "Mandaic", "Mand", "Manichaean", "Mani", "Marchen", "Marc", "Medefaidrin", "Medf", "Masaram_Gondi", "Gonm", "Meetei_Mayek", "Mtei", "Mende_Kikakui", "Mend", "Meroitic_Cursive", "Merc", "Meroitic_Hieroglyphs", "Mero", "Miao", "Plrd", "Modi", "Mongolian", "Mong", "Mro", "Mroo", "Multani", "Mult", "Myanmar", "Mymr", "Nabataean", "Nbat", "New_Tai_Lue", "Talu", "Newa", "Nko", "Nkoo", "Nushu", "Nshu", "Ogham", "Ogam", "Ol_Chiki", "Olck", "Old_Hungarian", "Hung", "Old_Italic", "Ital", "Old_North_Arabian", "Narb", "Old_Permic", "Perm", "Old_Persian", "Xpeo", "Old_Sogdian", "Sogo", "Old_South_Arabian", "Sarb", "Old_Turkic", "Orkh", "Oriya", "Orya", "Osage", "Osge", "Osmanya", "Osma", "Pahawh_Hmong", "Hmng", "Palmyrene", "Palm", "Pau_Cin_Hau", "Pauc", "Phags_Pa", "Phag", "Phoenician", "Phnx", "Psalter_Pahlavi", "Phlp", "Rejang", "Rjng", "Runic", "Runr", "Samaritan", "Samr", "Saurashtra", "Saur", "Sharada", "Shrd", "Shavian", "Shaw", "Siddham", "Sidd", "SignWriting", "Sgnw", "Sinhala", "Sinh", "Sogdian", "Sogd", "Sora_Sompeng", "Sora", "Soyombo", "Soyo", "Sundanese", "Sund", "Syloti_Nagri", "Sylo", "Syriac", "Syrc", "Tagalog", "Tglg", "Tagbanwa", "Tagb", "Tai_Le", "Tale", "Tai_Tham", "Lana", "Tai_Viet", "Tavt", "Takri", "Takr", "Tamil", "Taml", "Tangut", "Tang", "Telugu", "Telu", "Thaana", "Thaa", "Thai", "Tibetan", "Tibt", "Tifinagh", "Tfng", "Tirhuta", "Tirh", "Ugaritic", "Ugar", "Vai", "Vaii", "Warang_Citi", "Wara", "Yi", "Yiii", "Zanabazar_Square", "Zanb"));

    private static HashSet<String> constructUtf16LonePropertyValues() {
        HashSet<String> set = new HashSet<>(Arrays.asList("ASCII", "ASCII_Hex_Digit", "AHex", "Alphabetic", "Alpha", "Any", "Assigned", "Bidi_Control", "Bidi_C", "Bidi_Mirrored", "Bidi_M", "Case_Ignorable", "CI", "Cased", "Changes_When_Casefolded", "CWCF", "Changes_When_Casemapped", "CWCM", "Changes_When_Lowercased", "CWL", "Changes_When_NFKC_Casefolded", "CWKCF", "Changes_When_Titlecased", "CWT", "Changes_When_Uppercased", "CWU", "Dash", "Default_Ignorable_Code_Point", "DI", "Deprecated", "Dep", "Diacritic", "Dia", "Emoji", "Emoji_Component", "Emoji_Modifier", "Emoji_Modifier_Base", "Emoji_Presentation", "Extended_Pictographic", "Extender", "Ext", "Grapheme_Base", "Gr_Base", "Grapheme_Extend", "Gr_Ext", "Hex_Digit", "Hex", "IDS_Binary_Operator", "IDSB", "IDS_Trinary_Operator", "IDST", "ID_Continue", "IDC", "ID_Start", "IDS", "Ideographic", "Ideo", "Join_Control", "Join_C", "Logical_Order_Exception", "LOE", "Lowercase", "Lower", "Math", "Noncharacter_Code_Point", "NChar", "Pattern_Syntax", "Pat_Syn", "Pattern_White_Space", "Pat_WS", "Quotation_Mark", "QMark", "Radical", "Regional_Indicator", "RI", "Sentence_Terminal", "STerm", "Soft_Dotted", "SD", "Terminal_Punctuation", "Term", "Unified_Ideograph", "UIdeo", "Uppercase", "Upper", "Variation_Selector", "VS", "White_Space", "space", "XID_Continue", "XIDC", "XID_Start", "XIDS"));
        set.addAll(utf16GeneralCategoryValues);
        return set;
    }

    private static final HashSet<String> utf16LonePropertyValues = constructUtf16LonePropertyValues();

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

    private static HashMap<String, HashSet<String>> constructUtf16NonBinaryPropertyNames() {
        HashMap<String, HashSet<String>> map = new HashMap<>();
        map.put("General_Category", utf16GeneralCategoryValues);
        map.put("gc", utf16GeneralCategoryValues);
        map.put("Script", utf16ScriptCategoryValues);
        map.put("sc", utf16ScriptCategoryValues);
        map.put("Script_Extensions", utf16ScriptCategoryValues);
        map.put("scx", utf16ScriptCategoryValues);
        return map;
    }

    private static final HashMap<String, HashSet<String>> utf16NonBinaryPropertyNames = constructUtf16NonBinaryPropertyNames();

    private static class RegexException extends RuntimeException {
        public RegexException(String message) {
            super(message);
        }
    }

    private class Context {
        private int index;
        private HashSet<String> backreferenceNames = new HashSet<>();
        private HashSet<String> groupingNames = new HashSet<>();
        private List<Integer> backreferences = new LinkedList<>();
        private int nParenthesis = 0;

        private Context(int index) {
            this.index = index;
        }

        public Context() {
            this(0);
        }

        public boolean addGrouping(@Nonnull Maybe<String> name) {
            if (name.isJust()) {
                if (this.groupingNames.contains(name.fromJust())) {
                    return false;
                }
                this.groupingNames.add(name.fromJust());
            }
            this.nParenthesis++;
            return true;
        }

        public void backreferenceName(@Nonnull String name) {
            backreferenceNames.add(name);
        }

        public void backreference(int num) {
            backreferences.add(num);
        }

        public boolean verifyBackreferences() {
            for (Integer backreference : backreferences) {
                if (backreference > nParenthesis) {
                    return false;
                }
            }
            for (String backreferenceName : backreferenceNames) {
                if (!groupingNames.contains(backreferenceName)) {
                    return false;
                }
            }
            return true;
        }

        public Context goDeeper() {
            return new Context(this.index);
        }

        public boolean goDeeper(F<Context, Boolean> predicate) {
            try {
                Context context = this.goDeeper();
                boolean accepted = predicate.apply(context);
                if (accepted) {
                    this.absorb(context);
                }
                return accepted;
            } catch (RegexException e) {
                return false;
            }
        }

        public <B> Maybe<B> goDeeperExtended(F<Context, Maybe<B>> predicate) {
            try {
                Context context = this.goDeeper();
                Maybe<B> accepted = predicate.apply(context);
                if (accepted.isJust()) {
                    this.absorb(context);
                }
                return accepted;
            } catch (RegexException e) {
                return Maybe.empty();
            }
        }

        public void absorb(Context otherContext) {
            this.index = otherContext.index;
            this.backreferenceNames = otherContext.backreferenceNames;
            this.backreferences = otherContext.backreferences;
            this.groupingNames = otherContext.groupingNames;
            this.nParenthesis = otherContext.nParenthesis;
        }

        public Maybe<String> nextCodePoint() {
            return this.index >= pattern.length() ? Maybe.empty() : Maybe.of(new String(Character.toChars(pattern.codePointAt(this.index))));
        }

        public void skip(int n) {
            for (int i = 0; i < n && this.index < pattern.length(); i++) {
                this.index += this.nextCodePoint().fromJust().length();
            }
            if (this.index > pattern.length()) {
                this.index = pattern.length();
            }
        }

        public boolean eat(String str) {
            if (this.index + str.length() > pattern.length() || !pattern.startsWith(str, this.index)) {
                return false;
            }
            this.index += str.length();
            return true;
        }

        public Maybe<Integer> eatIdentifierStart() {
            if (this.index >= pattern.length()) {
                return Maybe.empty();
            }
            return this.goDeeperExtended(context -> {
                int characterValue = 0;
                if (context.match("\\u")) {
                    context.skip(1);
                    characterValue = acceptUnicodeEscape(context).fromJust();
                } else {
                    characterValue = pattern.codePointAt(this.index);
                    this.index += Character.toChars(characterValue).length;
                }
                String character = new String(Character.toChars(characterValue));
                if (character.equals("_") || character.equals("$") || isIdentifierStart(characterValue)) {
                    return Maybe.of(characterValue);
                }
                return Maybe.empty();
            });
        }

        public Maybe<Integer> eatIdentifierPart() {
            if (this.index >= pattern.length()) {
                return Maybe.empty();
            }
            return this.goDeeperExtended(context -> {
                int characterValue = 0;
                if (context.match("\\u")) {
                    context.skip(1);
                    characterValue = acceptUnicodeEscape(context).fromJust();
                } else {
                    characterValue = pattern.codePointAt(this.index);
                    this.index += Character.toChars(characterValue).length;
                }
                String character = new String(Character.toChars(characterValue));
                if (character.equals("\\u200C") || character.equals("\\u200D") || character.equals("$") || isIdentifierPart(characterValue)) {
                    return Maybe.of(characterValue);
                }
                return Maybe.empty();
            });
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

            masterLoop:
            for (int i = 0; limit < 0 || i < limit; i++) {
                for (String[] strings : stringArrays) {
                    for (String string : strings) {
                        if (this.eat(string)) {
                            stringBuilder.append(string);
                            continue masterLoop;
                        }
                    }
                }
                break;
            }
            return stringBuilder.toString();
        }

        public void expect(@Nonnull String str) {
            if (!this.eat(str)) {
                throw new RegexException("Expected \"" + str + "\" at index " + this.index + ", not found");
            }
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

    private PatternAcceptor(@Nonnull String pattern, boolean gFlag, boolean iFlag, boolean mFlag, boolean yFlag, boolean uFlag) {
        this.pattern = pattern;
        this.gFlag = gFlag;
        this.iFlag = iFlag;
        this.mFlag = mFlag;
        this.yFlag = yFlag;
        this.uFlag = uFlag;
    }

    public static boolean acceptRegex(@Nonnull String pattern, boolean gFlag, boolean iFlag, boolean mFlag, boolean yFlag, boolean uFlag) {
        PatternAcceptor acceptor = new PatternAcceptor(pattern, gFlag, iFlag, mFlag, yFlag, uFlag);
        return acceptor.acceptRegex();
    }

    private boolean acceptRegex() {
        Context context = new Context();
        try {
            if (!acceptDisjunction(context, Maybe.empty())) {
                return false;
            }
        } catch (RegexException e) {
            return false;
        }
        if (this.uFlag && !context.verifyBackreferences()) {
            return false;
        }
        return true;
    }

    private <B> F<Context, Maybe<B>> maybeLogicalOr(F<Context, Maybe<B>>... expressions) {
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
            context.expect(terminator.fromJust());
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
        if (this.uFlag) {
            return acceptAssertion(context) ||
                    acceptQuantified(this::acceptAtom).apply(context);
        }
        return acceptQuantified(this::acceptQuantifiableAssertion).apply(context) ||
                acceptAssertion(context) ||
                acceptQuantified(this::acceptAtom).apply(context);
    }

    private F<Context, Boolean> acceptLabeledGroup(F<Context, Boolean> predicate) {
        return currentContext -> currentContext.goDeeper(context -> {
            context.expect("(");
            if (predicate.apply(context)) {
               return acceptDisjunction(context, Maybe.of(")"));
            }
            return false;
        });
    }

    private boolean acceptAssertion(Context context) {
        return context.eatAny("^", "$", "\\b", "\\B").isJust() ||
                acceptLabeledGroup(subContext -> {
                    if (uFlag) {
                        return subContext.eatAny("?=", "?!", "?<=", "?<!").isJust();
                    }
                    return subContext.eatAny( "?<=", "?<!").isJust();
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
                    subContext.expect("{");
                    if (!acceptDecimal(subContext)) {
                        return false;
                    }
                    if (subContext.eat(",") && subContext.matchAny(decimalDigits) && !acceptDecimal(subContext)) {
                        return false;
                    }
                    subContext.expect("}");
                    subContext.eat("?");
                    return true;
                }) || !uFlag;
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
        context.skip(nextCodePoint.orJust("").length());
        return true;
    }

    private boolean acceptExtendedPatternCharacter(Context context) {
        Maybe<String> nextCodePoint = context.nextCodePoint();
        if (nextCodePoint.isNothing() || extendedSyntaxCharacters.contains(nextCodePoint.fromJust())) {
            return false;
        }
        context.skip(nextCodePoint.orJust("").length());
        return true;
    }

    private boolean acceptInvalidBracedQuantifier(Context context) {
        return context.goDeeper(subContext -> {
            subContext.expect("{");
            if (!acceptDecimal(subContext)) {
                return false;
            }
            if (subContext.eat(",") && subContext.matchAny(decimalDigits) && !acceptDecimal(subContext)) {
                return false;
            }
            subContext.expect("}");
            return true;
        });
    }

    private boolean acceptAtom(Context context) {
        if (this.uFlag) {
            return acceptPatternCharacter(context) ||
                    context.eat(".") ||
                    context.goDeeper(subContext -> {
                        subContext.expect("\\");
                        return acceptAtomEscape(subContext);
                    }) ||
                    acceptCharacterClass(context) ||
                    acceptLabeledGroup(subContext -> subContext.eat("?:")).apply(context) ||
                    acceptGrouping(context);
        }
        boolean matched = context.eat(".") ||
                context.goDeeper(subContext -> {
                    subContext.expect("\\");
                    if (subContext.match("c")) {
                        return true;
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
            context.expect("(");
            String[] groupName = new String[1];
            context.goDeeper(subContext -> {
                subContext.expect("?");
                Maybe<String> maybeGroupName = acceptGroupName(subContext);
                if (maybeGroupName.isJust()) {
                    groupName[0] = maybeGroupName.fromJust();
                    return true;
                }
                return false;
           });
           if (!acceptDisjunction(context, Maybe.of(")"))) {
               return false;
           }
           if (!context.addGrouping(Maybe.fromNullable(groupName[0]))) {
               return false;
            }
           return true;
        });
    }

    private boolean acceptAtomEscape(Context context) {
        return acceptDecimalEscape(context) ||
                acceptCharacterClassEscape(context) ||
                acceptCharacterEscape(context).map(i -> true).orJust(false) ||
                acceptGroupNameBackreference(context);
    }

    private boolean acceptDecimalEscape(Context superContext) {
        return superContext.goDeeper(context -> {
            StringBuilder digits = new StringBuilder();
            Maybe<String> firstDecimal = context.eatAny(decimalDigits);
            if (firstDecimal.isNothing() || firstDecimal.fromJust().equals("0")) {
                return false;
            }
            digits.append(firstDecimal.fromJust());
            Maybe<String> digit = Maybe.empty();
            while ((digit = context.eatAny(decimalDigits)).isJust()) {
                digits.append(digit.fromJust());
            }
            context.backreference(Integer.parseInt(digits.toString()));
            return true;
        });
    }

    private boolean acceptCharacterClassEscape(Context context) {
        if (context.eatAny("d", "D", "s", "S", "w", "W").isJust()) {
            return true;
        }
        return this.uFlag && context.goDeeper(subContext -> {
            if(!(subContext.eat("p{") || subContext.eat("P{"))) {
                return false;
            }
            if (!acceptUnicodePropertyValueExpression(subContext)) {
                return false;
            }
            return context.eat("}");
        });
    }

    private String acceptUnicodePropertyName(Context context) {
        return context.collect(controlCharacters, new String[]{"_"});
    }

    private String acceptUnicodePropertyValue(Context context) {
        return context.collect(controlCharacters, decimalDigits, new String[]{"_"});
    }

    private boolean acceptLoneUnicodePropertyNameOrValue(Context context) {
        return utf16LonePropertyValues.contains(acceptUnicodePropertyValue(context));
    }

    private boolean acceptUnicodePropertyValueExpression(Context superContext) {
        return superContext.goDeeper(context -> {
            String name = acceptUnicodePropertyName(context);
            if (name.length() == 0) {
                return false;
            }
            context.expect("=");
            String value = acceptUnicodePropertyValue(context);
            if (value.length() == 0) {
                return false;
            }
            return utf16NonBinaryPropertyNames.get(name).contains(value);
        }) || superContext.goDeeper(this::acceptLoneUnicodePropertyNameOrValue);
    }

    private Maybe<Integer> acceptUnicodeEscape(Context superContext) {
        return superContext.goDeeperExtended(context -> {
            context.expect("u");
            if (uFlag && context.eat("{")) {
                String hex = context.collect(hexDigits);
                context.expect("}");
                int value = Integer.parseInt(hex, 16);
                return value > 0x10FFFF ? Maybe.empty() : Maybe.of(value);
            }
            String hex = context.collect(4, hexDigits);
            if (hex.length() != 4) {
                return Maybe.empty();
            }
            int value = Integer.parseInt(hex, 16);
            if (value >= 0xD800 && value <= 0xDBFF && context.eat("\\u")) {
                String hex2 = context.collect(4, hexDigits);
                if (hex2.length() != 4) {
                    return Maybe.empty();
                }
                int value2 = Integer.parseInt(hex2, 16);
                if (value2 < 0xDC00 || value2 >= 0xE000) {
                    return Maybe.empty();
                }
                return Maybe.of(0x10000 + ((value & 0x3FF) << 10) + (value2 & 0x03FF));
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
                context -> context.goDeeperExtended(subContext -> {
                    subContext.expect("c");
                    Maybe<String> character = subContext.eatAny(controlCharacters);
                    if (character.isNothing()) {
                        return Maybe.empty();
                    }
                    return Maybe.of(character.fromJust().codePointAt(0) % 32);
                }),
                context -> context.goDeeperExtended(subContext -> {
                    subContext.expect("0");
                    if (subContext.eatAny(decimalDigits).isJust()) {
                        return Maybe.empty();
                    }
                    return Maybe.of(0);
                }),
                context -> context.goDeeperExtended(subContext -> {
                    subContext.expect("x");
                    String hex = subContext.collect(2, hexDigits);
                    if (hex.length() != 2) {
                        return Maybe.empty();
                    }
                    return Maybe.of(Integer.parseInt(hex, 16));
                }),
                this::acceptUnicodeEscape,
                context -> context.goDeeperExtended(subContext -> {
                    if (uFlag) {
                        return Maybe.empty();
                    }
                    F<Context, Maybe<Integer>> acceptOctalDigit = subContext2 -> subContext2.goDeeperExtended(subContext3 -> {
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
                context -> context.goDeeperExtended(subContext -> {
                    if (!uFlag) {
                        return Maybe.empty();
                    }
                    Maybe<String> maybeCharacter = subContext.eatAny(syntaxCharacterArray);
                    return maybeCharacter.map(character -> character.codePointAt(0));
                }),
                context -> {
                    if(uFlag && context.eat("/")) {
                        return Maybe.<Integer>of((int) "/".charAt(0));
                    }
                    return Maybe.<Integer>empty();
                },
                context -> context.goDeeperExtended(subContext -> {
                    if (uFlag) {
                        return Maybe.empty();
                    }
                    Maybe<String> maybeCharacter = subContext.nextCodePoint();
                    if (maybeCharacter.isJust() && !maybeCharacter.fromJust().equals("c") && !maybeCharacter.fromJust().equals("k")) {
                        subContext.skip(1);
                        return Maybe.of(maybeCharacter.fromJust().codePointAt(0));
                    }
                    return Maybe.empty();
                })
        ).apply(superContext);
    }

    private boolean acceptGroupNameBackreference(Context superContext) {
        return superContext.goDeeper(context -> {
            context.expect("k");
            Maybe<String> name = acceptGroupName(context);
            if (name.isNothing()) {
                return false;
            }
            context.backreferenceName(name.fromJust());
            return true;
        });
    }

    private Maybe<String> acceptGroupName(Context superContext) {
        return superContext.goDeeperExtended(context -> {
            context.expect("<");
            Maybe<String> start = context.eatIdentifierStart().map(i -> new String(Character.toChars(i)));
            if (start.isNothing()) {
                return Maybe.empty();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(start.fromJust());
            Maybe<String> part;
            while ((part = context.eatIdentifierPart().map(i -> new String(Character.toChars(i)))).isJust()) {
                stringBuilder.append(part.fromJust());
            }
            context.expect(">");
            return Maybe.of(stringBuilder.toString());
        });
    }

    private Maybe<Integer> acceptClassEscape(Context superContext) {
        return this.<Integer>maybeLogicalOr(
                context -> context.goDeeperExtended(subContext -> {
                    subContext.expect("b");
                    return Maybe.of(0x0008); // backspace
                }),
                context -> {
                    if (uFlag && context.eat("-")) {
                        return Maybe.of((int)"-".charAt(0));
                    }
                    return Maybe.empty();
                },
                context -> context.goDeeperExtended(subContext -> {
                    if (uFlag || !subContext.eat("c")) {
                        return Maybe.<Integer>empty();
                    }
                    return subContext.eatAny(decimalDigits, new String[]{"_"}).map(str -> str.codePointAt(0) % 32);
                }),
                context -> acceptCharacterClassEscape(context) ? Maybe.of(-1) : Maybe.empty(),
                this::acceptCharacterEscape
        ).apply(superContext);
    }

    private Maybe<Integer> acceptClassAtomNoDash(Context context) {
        if (context.eat("\\")) {
            return this.<Integer>maybeLogicalOr(
                    this::acceptClassEscape,
                    subContext -> subContext.goDeeperExtended(subContext2 -> {
                        if (subContext2.match("c")) {
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
        context.skip(nextCodePoint.fromJust().length());
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
            if (this.uFlag && (atom == -1 || otherAtom.fromJust() == -1)) {
                return Maybe.empty();
            } else if (!(!this.uFlag && (atom == -1 || otherAtom.fromJust() == -1)) && atom > otherAtom.fromJust()) {
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
            context.expect("[");
            context.eat("^");
            if (context.eat("]")) {
                return true;
            }
            if (acceptNonEmptyClassRanges(context).isJust()) {
                context.expect("]");
                return true;
            }
            return false;
        });
    }

}
