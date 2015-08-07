package com.shapesecurity.shift.integration;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.Module;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.codegen.CodeGen;
import com.shapesecurity.shift.fuzzer.Fuzzer;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.serialization.Serializer;
import com.shapesecurity.shift.validator.ValidationError;
import com.shapesecurity.shift.validator.Validator;

import org.junit.Test;

import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class IntegrationTest {
    @Test
    public void testFuzzerToValidator() {
        testFuzzerToValidatorHelper((long) 0, 5);
        testFuzzerToValidatorHelper((long) 2, 5);
        testFuzzerToValidatorHelper((long) 4, 5);
        testFuzzerToValidatorHelper((long) 6, 5);
        testFuzzerToValidatorHelper((long) 8, 5);
        testFuzzerToValidatorHelper((long) 10, 5);
    }

    @Test
    public void testFuzzerToCodeGenToParserToValidator() throws JsError {
        testFuzzerToCodeGenToParserToValidatorHelper((long) 1, 5);
        testFuzzerToCodeGenToParserToValidatorHelper((long) 3, 5);
        testFuzzerToCodeGenToParserToValidatorHelper((long) 5, 5);
        testFuzzerToCodeGenToParserToValidatorHelper((long) 7, 5);
        testFuzzerToCodeGenToParserToValidatorHelper((long) 9, 5);
        testFuzzerToCodeGenToParserToValidatorHelper((long) 11, 5);
    }

    @Test
    public void testFuzzerToCodeGenToParserSerialization() throws JsError {
        testFuzzerToCodeGenToParserSerializationHelper((long) 0.5, 5);
        testFuzzerToCodeGenToParserSerializationHelper((long) 1.5, 5);
        testFuzzerToCodeGenToParserSerializationHelper((long) 2.5, 5);
        testFuzzerToCodeGenToParserSerializationHelper((long) 3.5, 5);
        testFuzzerToCodeGenToParserSerializationHelper((long) 4.5, 5);
        testFuzzerToCodeGenToParserSerializationHelper((long) 5.5, 5);
    }

    @Test
    public void testFuzzerToValidatorTimedFiveSeconds() {
        Random random = new Random(48957);
        long start = System.currentTimeMillis();
        boolean hasErrors = false;
        while (System.currentTimeMillis() - start <= 5000) {
            int seed = random.nextInt();
            Node generated = Fuzzer.generate(new Random(seed), 5);
            ImmutableList<ValidationError> validationErrors;
            if (generated instanceof Script) {
                validationErrors = Validator.validate((Script) generated);
            } else {
                validationErrors = Validator.validate((Module) generated);
            }
            if (validationErrors.length > 0) {
                hasErrors = true;
                System.out.println("seed " + seed + " caused Fuzzer to generate " + validationErrors.length + " validation errors.");
            }
        }
        if (!hasErrors) {
            System.out.println("Fuzzer ran for 5 seconds and generated all valid ASTs.");
        }
        assertTrue(!hasErrors);
    }

    private void testFuzzerToValidatorHelper(long seed, int depth) {
        Node generated = Fuzzer.generate(new Random(seed), depth);
        ImmutableList<ValidationError> validationErrors;
        if (generated instanceof Script) {
            validationErrors = Validator.validate((Script) generated);
        } else {
            validationErrors = Validator.validate((Module) generated);
        }
        assertTrue(validationErrors.length == 0);
    }

    private void testFuzzerToCodeGenToParserToValidatorHelper(long seed, int depth) throws JsError {
        Node generated = Fuzzer.generate(new Random(seed), depth);
        ImmutableList<ValidationError> validationErrors;
        if (generated instanceof Script) {
            String code = CodeGen.codeGen((Script) generated);
            Script parsed = Parser.parseScript(code);
            validationErrors = Validator.validate(parsed);
        } else {
            String code = CodeGen.codeGen((Module) generated);
            Module parsed = Parser.parseModule(code);
            validationErrors = Validator.validate(parsed);
        }
        assertTrue(validationErrors.length == 0);
    }

    private void testFuzzerToCodeGenToParserSerializationHelper(long seed, int depth) throws JsError {
        Node generated = Fuzzer.generate(new Random(seed), depth);
        String originalSerialized = Serializer.serialize(generated);
        if (generated instanceof Script) {
            String code = CodeGen.codeGen((Script) generated);
            Script parsed = Parser.parseScript(code);
            String finalSerialized = Serializer.serialize(parsed);
            assertEquals(originalSerialized, finalSerialized);
        } else {
            String code = CodeGen.codeGen((Module) generated);
            Module parsed = Parser.parseModule(code);
            String finalSerialized = Serializer.serialize(parsed);
            assertEquals(originalSerialized, finalSerialized);
        }
    }
}