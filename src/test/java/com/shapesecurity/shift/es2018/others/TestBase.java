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
//package com.shapesecurity.shift.others;
//
//import org.hamcrest.CoreMatchers;
//import org.junit.Assert;
//import org.junit.Rule;
//import org.junit.rules.ErrorCollector;
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import javax.annotation.Nonnull;
//
//public class TestBase {
//  public static final double NANOS_TO_SECONDS = 1e-9;
//  private static final String BASE_PATH =
//      System.getenv("CONFIG_DIR") == null ? "src/test/resources" : System.getenv("CONFIG_DIR");
//  @Rule
//  public ErrorCollector collector = new ErrorCollector();
//  private boolean fatal;
//
//  public TestBase() {
//    this.fatal = true;
//  }
//
//  protected static Path getPath(String path) {
//    return Paths.get(BASE_PATH + '/' + path);
//  }
//
//  @Nonnull
//  protected static String readFile(@Nonnull String path) throws IOException {
//    byte[] encoded = Files.readAllBytes(getPath(path));
//    return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
//  }
//
//  @Nonnull
//  protected static String readLibrary(@Nonnull String path) throws IOException {
//    return readFile("libraries/" + path);
//  }
//
//  public void assertEquals(String msg, Object expected, Object actual) {
//    if (getFatal()) {
//      Assert.assertEquals(msg, expected, actual);
//    } else {
//      this.collector.checkThat(msg, actual, CoreMatchers.equalTo(expected));
//    }
//  }
//
//  public void assertEquals(Object expected, Object actual) {
//    assertEquals("", expected, actual);
//  }
//
//  public boolean getFatal() {
//    return this.fatal;
//  }
//
//  public void setFatal(boolean fatalFlag) {
//    this.fatal = fatalFlag;
//  }
//}
