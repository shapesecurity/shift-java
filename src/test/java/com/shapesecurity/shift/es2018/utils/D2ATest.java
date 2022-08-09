package com.shapesecurity.shift.es2018.utils;

import com.google.gson.Gson;
import com.oracle.truffle.js.runtime.JSRuntime;
import com.shapesecurity.shift.es2018.parser.JsError;
import com.shapesecurity.shift.es2018.parser.Token;
import com.shapesecurity.shift.es2018.parser.Tokenizer;
import com.shapesecurity.shift.es2018.parser.token.EOFToken;
import com.shapesecurity.shift.es2018.parser.token.NumericLiteralToken;
import org.graalvm.polyglot.Context;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

public class D2ATest {
	@Test
	public void testSmallIntegersRoundTrip() {
		for (double i = -1e7; i <= 1e7; ++i) {
			assertRoundTrips(i);
		}
	}

	@Test
	public void testRandomDoublesRoundTrip() {
		ThreadLocalRandom rng = ThreadLocalRandom.current();
		for (int size = 20; size < 100; ++size) {
			double bound = Math.pow(2, size);
			for (int i = 0; i < 1e4; ++i) {
				assertRoundTrips(rng.nextDouble(bound));
			}
		}

		for (int size = 20; size < 100; ++size) {
			double bound = Math.pow(2, size);
			for (int i = 0; i < 1e4; ++i) {
				assertRoundTrips(Math.round(rng.nextDouble(bound)));
			}
		}
	}

	@Test
	public void testInterestingDoubles() {
		long[] signs = { 0b0L, 0b1L };

		long[] mantissas = {
			0b0000000000000000000000000000000000000000000000000000L,
			0b0000000000000000000000000000000000000000000000000001L,
			0b0101010101010101010101010101010101010101010101010101L,
			0b0111111111111111111111111111111111111111111111111111L,
			0b1000000000000000000000000000000000000000000000000000L,
			0b1000000000000000000000000000000000000000000000000001L,
			0b1010101010101010101010101010101010101010101010101010L,
			0b1111111111111111111111111111111111111111111111111110L,
			0b1111111111111111111111111111111111111111111111111111L,
		};

		for (long sign: signs) {
			for (long exponent = 0; exponent <= 0b111_1111_1111L; ++exponent) {
				for (long mantissa: mantissas) {
					double candidate = Double.longBitsToDouble((sign << 63) + (exponent << 52) + mantissa);
					assertRoundTrips(candidate);
				}
			}
		}
	}

	@Test
	public void testFoo() {
		assertRoundTrips(938249922368853.2);
		assertRoundTrips(2251799813685248.5);
		assertRoundTrips(1.8446744073709552E19);
		assertRoundTrips(1125899906842623.7);
	}

	void assertRoundTrips(double value) {
		String rep = D2A.d2a(value);
		double parsed = Double.parseDouble(rep);
		assertEquals(value, parsed, 0.0);

		// String graal;
		// try (var ctx = Context.newBuilder("js").option("engine.WarnInterpreterOnly", "false").build()) {
		// 	graal = ctx.eval("js", "String(" + value + ")").asString();
		// }
		//
		// assertEquals("graal oracle", graal, rep);

		String shortRep = D2A.shortD2a(value);
		if (Double.isNaN(value)) {
			assertEquals("NaN", shortRep);
			return;
		}
		try {
			int sign = 1;
			if (shortRep.startsWith("-")) {
				shortRep = shortRep.substring(1);
				sign = -1;
			}
			Tokenizer tokenizer = new Tokenizer(shortRep, false);
			Token token = tokenizer.lex();
			if (!(token instanceof NumericLiteralToken) || !(tokenizer.lookahead instanceof EOFToken)) {
				throw new RuntimeException("short rep did not parse as number: " + shortRep);
			}
			double shortParsed = sign * ((NumericLiteralToken) token).value;
			assertEquals(value, shortParsed, 0.0);
		} catch (JsError e) {
			throw new RuntimeException(e);
		}
	}

}
