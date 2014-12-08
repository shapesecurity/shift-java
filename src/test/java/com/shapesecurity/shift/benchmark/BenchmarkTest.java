package com.shapesecurity.shift.benchmark;

import com.shapesecurity.shift.TestBase;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.codegen.CodeGen;
import com.shapesecurity.shift.minifier.Minifier;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.scope.ScopeAnalyzer;
import com.shapesecurity.shift.visitor.CloneReducer;
import com.shapesecurity.shift.visitor.LazyCloner;

import java.io.IOException;

import org.junit.Test;

public class BenchmarkTest extends TestBase {

  int WARMUP_TIMES = 2000;
  int TIMING_TIMES = 500;

  public void startProfiling() {
    // Profiler trigger
  }

  public void stopProfiling() {
    // Profiler trigger
  }

  @Test
  public void benchmarkClone() throws IOException, JsError {
    String source = readLibrary("angular-1.2.5.js");
    Script program = Parser.parse(source);
    System.out.println("Cloner warm-up started.");
    for (int i = 0; i < WARMUP_TIMES; i++) {
      program.reduce(CloneReducer.INSTANCE);
    }
    System.out.println("Cloner warm-up finished.");
    final int N = TIMING_TIMES;
    long start = System.nanoTime();
    startProfiling();
    for (int i = 0; i < N; i++) {
      program.reduce(CloneReducer.INSTANCE);
    }
    double elapsed = (System.nanoTime() - start) * 1e-6 / N;
    stopProfiling();
    System.out.printf("Cloner time: %.3fms\n", elapsed);
  }


  @Test
  public void benchmarkLazyClone() throws IOException, JsError {
    String source = readLibrary("angular-1.2.5.js");
    Script program = Parser.parse(source);
    System.out.println("LazyCloner warm-up started.");
    for (int i = 0; i < WARMUP_TIMES; i++) {
      program.reduce(LazyCloner.INSTANCE);
    }
    System.out.println("LazyCloner warm-up finished.");
    final int N = TIMING_TIMES;
    long start = System.nanoTime();
    startProfiling();
    for (int i = 0; i < N; i++) {
      program.reduce(LazyCloner.INSTANCE);
    }
    double elapsed = (System.nanoTime() - start) * 1e-6 / N;
    stopProfiling();
    System.out.printf("LazyCloner time: %.3fms\n", elapsed);
  }


  @Test
  public void benchmarkScopeAnalysis() throws IOException, JsError {
    String source = readLibrary("angular-1.2.5.js");
    System.out.println("ScopeAnalysis warm-up started.");
    Script program = Parser.parse(source);
    for (int i = 0; i < WARMUP_TIMES; i++) {
      ScopeAnalyzer.analyze(program);
    }
    System.out.println("ScopeAnalysis warm-up finished.");
    final int N = TIMING_TIMES;
    long start = System.nanoTime();
    startProfiling();
    for (int i = 0; i < N; i++) {
      ScopeAnalyzer.analyze(program);
    }
    double elapsed = (System.nanoTime() - start) * 1e-6 / N;
    stopProfiling();
    System.out.printf("ScopeAnalysis time: %.3fms\n", elapsed);
  }

  @Test
  public void benchmarkMinifier() throws IOException, JsError {
    String source = readLibrary("angular-1.2.5.js");
    Script program = Parser.parse(source);
    System.out.println("Minifier warm-up started.");
    for (int i = 0; i < WARMUP_TIMES; i++) {
      CodeGen.codeGen(Minifier.minify(program));
    }
    System.out.println("Minifier warm-up finished.");
    final int N = TIMING_TIMES;
    startProfiling();
    long start = System.nanoTime();
    for (int i = 0; i < N; i++) {
      CodeGen.codeGen(Minifier.minify(program));
    }
    double elapsed = (System.nanoTime() - start) * 1e-6 / N;
    stopProfiling();
    System.out.printf("Minifier time: %.3fms\n", elapsed);
  }
}
