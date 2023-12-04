/*
 * Copyright 2023 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.d_project.qrcode;

/**
 * QRMath
 *
 * @author Kazuhiko Arase
 */
class QRMath {

  private static final int[] EXP_TABLE;
  private static final int[] LOG_TABLE;

  static {
    EXP_TABLE = new int[256];

    for (int i = 0; i < 8; i++) {
      EXP_TABLE[i] = 1 << i;
    }

    for (int i = 8; i < 256; i++) {
      EXP_TABLE[i] = EXP_TABLE[i - 4] ^ EXP_TABLE[i - 5] ^ EXP_TABLE[i - 6] ^ EXP_TABLE[i - 8];
    }

    LOG_TABLE = new int[256];
    for (int i = 0; i < 255; i++) {
      LOG_TABLE[EXP_TABLE[i]] = i;
    }
  }

  private QRMath() {}

  public static int glog(int n) {
    if (n < 1) {
      throw new ArithmeticException("log(" + n + ")");
    }

    return LOG_TABLE[n];
  }

  public static int gexp(int n) {
    while (n < 0) {
      n += 255;
    }

    while (n >= 256) {
      n -= 255;
    }

    return EXP_TABLE[n];
  }
}
