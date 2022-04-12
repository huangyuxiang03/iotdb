/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.tsfile.encoding.decoder;

import org.apache.iotdb.tsfile.encoding.encoder.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RAKEDecoderTest {
  private static final double DELTA = 0;
  private static final int FLOAT_MAX_POINT_VALUE = 10000;
  private static final long DOUBLE_MAX_POINT_VALUE = 1000000000000000L;
  private static final List<Float> floatList = new ArrayList<>();
  private static final List<Double> doubleList = new ArrayList<>();
  private static final List<Integer> intList = new ArrayList<>();
  private static final List<Long> longList = new ArrayList<>();

  private static final List<Integer> iterations = new ArrayList<>();

  @BeforeClass
  public static void setUp() {
    int hybridCount = 11;
    int hybridNum = 50;
    int hybridStart = 2000;
    for (int i = 0; i < hybridNum; i++) {
      for (int j = 0; j < hybridCount; j++) {
        floatList.add((float) hybridStart / FLOAT_MAX_POINT_VALUE);
        doubleList.add((double) hybridStart / DOUBLE_MAX_POINT_VALUE);
        intList.add(hybridStart);
        longList.add((long) hybridStart);
      }
      for (int j = 0; j < hybridCount; j++) {
        floatList.add((float) hybridStart / FLOAT_MAX_POINT_VALUE);
        doubleList.add((double) hybridStart / DOUBLE_MAX_POINT_VALUE);
        intList.add(hybridStart);
        longList.add((long) hybridStart);
        hybridStart += 3;
      }
      hybridCount += 2;
    }

    iterations.add(1);
    iterations.add(3);
    iterations.add(8);
    iterations.add(16);
    iterations.add(1000);
    iterations.add(10000);
  }

  @Test
  public void testIntSingleValue() throws IOException {
    Encoder encoder = new IntRAKEEncoder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    encoder.encode(777, baos);
    encoder.flush(baos);

    ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());

    Decoder decoder = new IntRAKEDecoder();
    if (decoder.hasNext(buffer)) {
      assertEquals(777, decoder.readInt(buffer));
    }
    if (decoder.hasNext(buffer)) {
      fail();
    }
  }

  @Test
  public void testFloatSingleValue() throws IOException {
    Encoder encoder = new FloatRAKEEncoder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    encoder.encode(Float.MAX_VALUE, baos);
    encoder.flush(baos);

    ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());

    Decoder decoder = new FloatRAKEDecoder();
    if (decoder.hasNext(buffer)) {
      assertEquals(Float.MAX_VALUE, decoder.readFloat(buffer), DELTA);
    }
    if (decoder.hasNext(buffer)) {
      fail();
    }
  }

  @Test
  public void testLongSingleValue() throws IOException {
    Encoder encoder = new LongRAKEEncoder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    encoder.encode((long) Integer.MAX_VALUE + 10, baos);
    encoder.flush(baos);

    ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());

    Decoder decoder = new LongRAKEDecoder();
    if (decoder.hasNext(buffer)) {
      assertEquals((long) Integer.MAX_VALUE + 10, decoder.readLong(buffer));
    }
    if (decoder.hasNext(buffer)) {
      fail();
    }
  }

  @Test
  public void testDoubleSingleValue() throws IOException {
    Encoder encoder = new DoubleRAKEEncoder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    encoder.encode(Double.MAX_VALUE, baos);
    encoder.flush(baos);

    ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());

    Decoder decoder = new DoubleRAKEDecoder();
    if (decoder.hasNext(buffer)) {
      assertEquals(Double.MAX_VALUE, decoder.readDouble(buffer), DELTA);
    }
    if (decoder.hasNext(buffer)) {
      fail();
    }
  }

  @Test
  public void testIntZeroNumber() throws IOException {
    Encoder encoder = new IntRAKEEncoder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int value = 0;
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.flush(baos);
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.flush(baos);
    ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
    for (int i = 0; i < 2; i++) {
      Decoder decoder = new IntRAKEDecoder();
      if (decoder.hasNext(buffer)) {
        assertEquals(value, decoder.readInt(buffer), DELTA);
      }
      if (decoder.hasNext(buffer)) {
        assertEquals(value, decoder.readInt(buffer), DELTA);
      }
      if (decoder.hasNext(buffer)) {
        assertEquals(value, decoder.readInt(buffer), DELTA);
      }
    }
  }

  @Test
  public void testFloatZeroNumber() throws IOException {
    Encoder encoder = new FloatRAKEEncoder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    float value = 0f;
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.flush(baos);
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.flush(baos);
    ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
    for (int i = 0; i < 2; i++) {
      Decoder decoder = new FloatRAKEDecoder();
      if (decoder.hasNext(buffer)) {
        assertEquals(value, decoder.readFloat(buffer), DELTA);
      }
      if (decoder.hasNext(buffer)) {
        assertEquals(value, decoder.readFloat(buffer), DELTA);
      }
      if (decoder.hasNext(buffer)) {
        assertEquals(value, decoder.readFloat(buffer), DELTA);
      }
    }
  }

  @Test
  public void testLongZeroNumber() throws IOException {
    Encoder encoder = new LongRAKEEncoder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    long value = 0;
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.flush(baos);
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.flush(baos);
    ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
    for (int i = 0; i < 2; i++) {
      Decoder decoder = new LongRAKEDecoder();
      if (decoder.hasNext(buffer)) {
        assertEquals(value, decoder.readLong(buffer), DELTA);
      }
      if (decoder.hasNext(buffer)) {
        assertEquals(value, decoder.readLong(buffer), DELTA);
      }
      if (decoder.hasNext(buffer)) {
        assertEquals(value, decoder.readLong(buffer), DELTA);
      }
    }
  }

  @Test
  public void testDoubleZeroNumber() throws IOException {
    Encoder encoder = new DoubleRAKEEncoder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    double value = 0f;
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.flush(baos);
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.encode(value, baos);
    encoder.flush(baos);
    ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
    for (int i = 0; i < 2; i++) {
      Decoder decoder = new DoubleRAKEDecoder();
      if (decoder.hasNext(buffer)) {
        assertEquals(value, decoder.readDouble(buffer), DELTA);
      }
      if (decoder.hasNext(buffer)) {
        assertEquals(value, decoder.readDouble(buffer), DELTA);
      }
      if (decoder.hasNext(buffer)) {
        assertEquals(value, decoder.readDouble(buffer), DELTA);
      }
    }
  }

  @Test
  public void testInteger() throws IOException {

    for (Integer num : iterations) {

      Encoder encoder = new IntRAKEEncoder();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int value = 7;
      for (int i = 0; i < num; i++) {
        encoder.encode(value + 2 * i, baos);
      }
      encoder.flush(baos);

      ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());

      Decoder decoder = new IntRAKEDecoder();
      for (int i = 0; i < num; i++) {
        if (decoder.hasNext(buffer)) {
          assertEquals(value + 2 * i, decoder.readInt(buffer));
          continue;
        }
        fail();
      }
      if (decoder.hasNext(buffer)) {
        fail();
      }
    }
  }

  @Test
  public void testIntegerTime() throws IOException {
    int num = 100000;
    long encode_time = 0;
    for (int k = 0; k < 10; k++) {
      Encoder encoder = new DeltaBinaryEncoder.IntDeltaEncoder();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int value = 7;
      long start_time = System.currentTimeMillis();
      for (int i = 0; i < num; i++) {
        encoder.encode(value + 2 * i, baos);
      }
      encoder.flush(baos);
      long end_time = System.currentTimeMillis();
      System.out.println(end_time - start_time + "ms");
      encode_time += end_time - start_time;
    }
    System.out.println(encode_time / 10);
    //
    //
    //              ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
    //
    //              Decoder decoder = new IntRAKEDecoder();
    //              for (int i = 0; i < num; i++) {
    //                if (decoder.hasNext(buffer)) {
    //                  assertEquals(value + 2 * i, decoder.readInt(buffer));
    //                  continue;
    //                }
    //                fail();
    //              }
    //              if (decoder.hasNext(buffer)) {
    //                fail();
    //              }
    //            }
  }

  @Test
  public void testFloat() throws IOException {
    for (Integer num : iterations) {
      Encoder encoder = new FloatRAKEEncoder();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      float value = 7.101f;
      for (int i = 0; i < num; i++) {
        encoder.encode(value + 2 * i, baos);
      }
      encoder.flush(baos);

      ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());

      Decoder decoder = new FloatRAKEDecoder();
      for (int i = 0; i < num; i++) {
        if (decoder.hasNext(buffer)) {
          assertEquals(value + 2 * i, decoder.readFloat(buffer), DELTA);
          continue;
        }
        fail();
      }
      if (decoder.hasNext(buffer)) {
        fail();
      }
    }
  }

  @Test
  public void testLong() throws IOException {
    for (Integer num : iterations) {
      Encoder encoder = new LongRAKEEncoder();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      long value = 7;
      for (int i = 0; i < num; i++) {
        encoder.encode(value + 2 * i, baos);
      }
      encoder.flush(baos);

      ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());

      Decoder decoder = new LongRAKEDecoder();
      for (int i = 0; i < num; i++) {
        if (decoder.hasNext(buffer)) {
          assertEquals(value + 2 * i, decoder.readLong(buffer));
          continue;
        }
        fail();
      }
      if (decoder.hasNext(buffer)) {
        fail();
      }
    }
  }

  @Test
  public void testDouble() throws IOException {
    for (Integer num : iterations) {
      Encoder encoder = new DoubleRAKEEncoder();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      double value = 7.101f;
      for (int i = 0; i < num; i++) {
        encoder.encode(value + 2 * i, baos);
      }
      encoder.flush(baos);

      ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());

      Decoder decoder = new DoubleRAKEDecoder();
      for (int i = 0; i < num; i++) {
        if (decoder.hasNext(buffer)) {
          assertEquals(value + 2 * i, decoder.readDouble(buffer), DELTA);
          continue;
        }
        fail();
      }
      if (decoder.hasNext(buffer)) {
        fail();
      }
    }
  }

  @Test
  public void testIntegerRepeat() throws Exception {
    for (int i = 1; i <= 10; i++) {
      testInteger(i);
    }
  }

  private void testInteger(int repeatCount) throws Exception {
    Encoder encoder = new IntRAKEEncoder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (int i = 0; i < repeatCount; i++) {
      for (int value : RAKEDecoderTest.intList) {
        encoder.encode(value, baos);
      }
      encoder.flush(baos);
    }

    ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());

    for (int i = 0; i < repeatCount; i++) {
      Decoder decoder = new IntRAKEDecoder();
      for (int expected : RAKEDecoderTest.intList) {
        if (decoder.hasNext(buffer)) {
          int actual = decoder.readInt(buffer);
          assertEquals(expected, actual);
          continue;
        }
        fail();
      }
    }

    encoder = new IntRAKEEncoder();
    baos = new ByteArrayOutputStream();
    for (int i = 0; i < repeatCount; i++) {
      for (int value : RAKEDecoderTest.intList) {
        encoder.encode(-value, baos);
      }
      encoder.flush(baos);
    }
    encoder.flush(baos);
    buffer = ByteBuffer.wrap(baos.toByteArray());

    for (int i = 0; i < repeatCount; i++) {
      IntRAKEDecoder decoder = new IntRAKEDecoder();
      for (int expected : RAKEDecoderTest.intList) {
        if (decoder.hasNext(buffer)) {
          int actual = decoder.readInt(buffer);
          assertEquals(expected, -actual);
          continue;
        }
        fail();
      }
    }
  }

  @Test
  public void testFloatRepeat() throws Exception {
    for (int i = 1; i <= 10; i++) {
      testFloat(i);
    }
  }

  private void testFloat(int repeatCount) throws Exception {
    Encoder encoder = new FloatRAKEEncoder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (int i = 0; i < repeatCount; i++) {
      for (float value : RAKEDecoderTest.floatList) {
        encoder.encode(value, baos);
      }
      encoder.flush(baos);
    }

    ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());

    for (int i = 0; i < repeatCount; i++) {
      Decoder decoder = new FloatRAKEDecoder();
      for (float expected : RAKEDecoderTest.floatList) {
        if (decoder.hasNext(buffer)) {
          float actual = decoder.readFloat(buffer);
          assertEquals(expected, actual, DELTA);
          continue;
        }
        fail();
      }
    }

    encoder = new FloatRAKEEncoder();
    baos = new ByteArrayOutputStream();
    for (int i = 0; i < repeatCount; i++) {
      for (float value : RAKEDecoderTest.floatList) {
        encoder.encode(-value, baos);
      }
      encoder.flush(baos);
    }

    buffer = ByteBuffer.wrap(baos.toByteArray());

    for (int i = 0; i < repeatCount; i++) {
      Decoder decoder = new FloatRAKEDecoder();
      for (float expected : RAKEDecoderTest.floatList) {
        if (decoder.hasNext(buffer)) {
          float actual = decoder.readFloat(buffer);
          assertEquals(expected, -actual, DELTA);
          continue;
        }
        fail();
      }
    }
  }

  @Test
  public void testLongRepeat() throws Exception {
    for (int i = 1; i <= 10; i++) {
      testLong(i);
    }
  }

  private void testLong(int repeatCount) throws Exception {
    Encoder encoder = new LongRAKEEncoder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (int i = 0; i < repeatCount; i++) {
      for (long value : RAKEDecoderTest.longList) {
        encoder.encode(value, baos);
      }
      encoder.flush(baos);
    }

    ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
    for (int i = 0; i < repeatCount; i++) {
      Decoder decoder = new LongRAKEDecoder();

      for (long expected : RAKEDecoderTest.longList) {
        if (decoder.hasNext(buffer)) {
          long actual = decoder.readLong(buffer);
          assertEquals(expected, actual);
          continue;
        }
        fail();
      }
    }

    encoder = new LongRAKEEncoder();
    baos = new ByteArrayOutputStream();
    for (int i = 0; i < repeatCount; i++) {
      for (long value : RAKEDecoderTest.longList) {
        encoder.encode(-value, baos);
      }
      encoder.flush(baos);
    }

    buffer = ByteBuffer.wrap(baos.toByteArray());

    for (int i = 0; i < repeatCount; i++) {
      Decoder decoder = new LongRAKEDecoder();
      for (long expected : RAKEDecoderTest.longList) {
        if (decoder.hasNext(buffer)) {
          long actual = decoder.readLong(buffer);
          assertEquals(expected, -actual);
          continue;
        }
        fail();
      }
    }
  }

  @Test
  public void testDoubleRepeat() throws Exception {
    for (int i = 1; i <= 10; i++) {
      testDouble(i);
    }
  }

  private void testDouble(int repeatCount) throws Exception {
    Encoder encoder = new DoubleRAKEEncoder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (int i = 0; i < repeatCount; i++) {
      for (double value : RAKEDecoderTest.doubleList) {
        encoder.encode(value, baos);
      }
      encoder.flush(baos);
    }

    ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());

    for (int i = 0; i < repeatCount; i++) {
      Decoder decoder = new DoubleRAKEDecoder();
      for (double expected : RAKEDecoderTest.doubleList) {
        if (decoder.hasNext(buffer)) {
          double actual = decoder.readDouble(buffer);
          assertEquals(expected, actual, DELTA);
          continue;
        }
        fail();
      }
    }

    encoder = new DoubleRAKEEncoder();
    baos = new ByteArrayOutputStream();
    for (int i = 0; i < repeatCount; i++) {
      for (double value : RAKEDecoderTest.doubleList) {
        encoder.encode(-value, baos);
      }
      encoder.flush(baos);
    }

    buffer = ByteBuffer.wrap(baos.toByteArray());

    for (int i = 0; i < repeatCount; i++) {
      Decoder decoder = new DoubleRAKEDecoder();
      for (double expected : RAKEDecoderTest.doubleList) {
        if (decoder.hasNext(buffer)) {
          double actual = decoder.readDouble(buffer);
          assertEquals(expected, -actual, DELTA);
          continue;
        }
        fail();
      }
    }
  }
}
