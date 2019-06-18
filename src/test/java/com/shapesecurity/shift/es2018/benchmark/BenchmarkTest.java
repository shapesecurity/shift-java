///*
// * Copyright 2014 Shape Security, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.shapesecurity.shift.benchmark;
//
//import com.shapesecurity.shift.TestBase;
//import Script;
//import CodeGen;
//import com.shapesecurity.shift.minifier.Minifier;
//import JsError;
//import Parser;
//import ScopeAnalyzer;
//import com.shapesecurity.shift.visitor.CloneReducer;
//import com.shapesecurity.shift.visitor.LazyCloner;
//
//import java.io.IOException;
//
//import org.junit.Test;
//
//public class BenchmarkTest extends TestBase {
//
//  int WARMUP_TIMES = 2000;
//  int TIMING_TIMES = 500;
//
//  public void startProfiling() {
//    // Profiler trigger
//  }
//
//  public void stopProfiling() {
//    // Profiler trigger
//  }
//
//  @Test
//  public void benchmarkParser() throws IOException, JsError {
//    String source = readLibrary("angular-1.2.5.js");
//    System.out.println("Parser warm-up started.");
//    for (int i = 0; i < WARMUP_TIMES; i++) {
//      Parser.parse(source);
//    }
//    System.out.println("Parser warm-up finished.");
//    final int N = TIMING_TIMES;
//    long start = System.nanoTime();
//    startProfiling();
//    for (int i = 0; i < N; i++) {
//      Parser.parse(source);
//    }
//    double elapsed = (System.nanoTime() - start) * 1e-6 / N;
//    stopProfiling();
//    System.out.printf("Parser time: %.3fms\n", elapsed);
//  }
//
//  @Test
//  public void benchmarkCodeGen() throws IOException, JsError {
//    String source = readLibrary("angular-1.2.5.js");
//    Script tree = Parser.parse(source);
//    System.out.println("Parser warm-up started.");
//    for (int i = 0; i < WARMUP_TIMES; i++) {
//      CodeGen.codeGen(tree);
//    }
//    System.out.println("Parser warm-up finished.");
//    final int N = TIMING_TIMES;
//    long start = System.nanoTime();
//    startProfiling();
//    for (int i = 0; i < N; i++) {
//      CodeGen.codeGen(tree);
//    }
//    double elapsed = (System.nanoTime() - start) * 1e-6 / N;
//    stopProfiling();
//    System.out.printf("Parser time: %.3fms\n", elapsed);
//  }
//
//  @Test
//  public void benchmarkClone() throws IOException, JsError {
//    String source = readLibrary("angular-1.2.5.js");
//    Script program = Parser.parse(source);
//    System.out.println("Cloner warm-up started.");
//    for (int i = 0; i < WARMUP_TIMES; i++) {
//      program.reduce(CloneReducer.INSTANCE);
//    }
//    System.out.println("Cloner warm-up finished.");
//    final int N = TIMING_TIMES;
//    long start = System.nanoTime();
//    startProfiling();
//    for (int i = 0; i < N; i++) {
//      program.reduce(CloneReducer.INSTANCE);
//    }
//    double elapsed = (System.nanoTime() - start) * 1e-6 / N;
//    stopProfiling();
//    System.out.printf("Cloner time: %.3fms\n", elapsed);
//  }
//
//
//  @Test
//  public void benchmarkLazyClone() throws IOException, JsError {
//    String source = readLibrary("angular-1.2.5.js");
//    Script program = Parser.parse(source);
//    System.out.println("LazyCloner warm-up started.");
//    for (int i = 0; i < WARMUP_TIMES; i++) {
//      program.reduce(LazyCloner.INSTANCE);
//    }
//    System.out.println("LazyCloner warm-up finished.");
//    final int N = TIMING_TIMES;
//    long start = System.nanoTime();
//    startProfiling();
//    for (int i = 0; i < N; i++) {
//      program.reduce(LazyCloner.INSTANCE);
//    }
//    double elapsed = (System.nanoTime() - start) * 1e-6 / N;
//    stopProfiling();
//    System.out.printf("LazyCloner time: %.3fms\n", elapsed);
//  }
//
//
//  @Test
//  public void benchmarkScopeAnalysis() throws IOException, JsError {
//    String source = readLibrary("angular-1.2.5.js");
//    System.out.println("ScopeAnalysis warm-up started.");
//    Script program = Parser.parse(source);
//    for (int i = 0; i < WARMUP_TIMES; i++) {
//      ScopeAnalyzer.analyze(program);
//    }
//    System.out.println("ScopeAnalysis warm-up finished.");
//    final int N = TIMING_TIMES;
//    long start = System.nanoTime();
//    startProfiling();
//    for (int i = 0; i < N; i++) {
//      ScopeAnalyzer.analyze(program);
//    }
//    double elapsed = (System.nanoTime() - start) * 1e-6 / N;
//    stopProfiling();
//    System.out.printf("ScopeAnalysis time: %.3fms\n", elapsed);
//  }
//
//  @Test
//  public void benchmarkMinifier() throws IOException, JsError {
//    String source = readLibrary("angular-1.2.5.js");
//    Script program = Parser.parse(source);
//    System.out.println("Minifier warm-up started.");
//    for (int i = 0; i < WARMUP_TIMES; i++) {
//      CodeGen.codeGen(Minifier.minify(program));
//    }
//    System.out.println("Minifier warm-up finished.");
//    final int N = TIMING_TIMES;
//    startProfiling();
//    long start = System.nanoTime();
//    for (int i = 0; i < N; i++) {
//      CodeGen.codeGen(Minifier.minify(program));
//    }
//    double elapsed = (System.nanoTime() - start) * 1e-6 / N;
//    stopProfiling();
//    System.out.printf("Minifier time: %.3fms\n", elapsed);
//  }
//}
