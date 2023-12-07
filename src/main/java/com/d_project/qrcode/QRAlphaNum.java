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
 * QRAlphaNum
 *
 * @author Kazuhiko Arase
 * @author Leonard Woo
 */
class QRAlphaNum extends QRData {

  public QRAlphaNum(String data) {
    super(Mode.MODE_ALPHA_NUM, data);
  }

  static final int[] TABLE = {
   //  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
      36, -1, -1, -1, 37, 38, -1, -1, -1, -1, 39, 40, -1, 41, 42, 43, // 20-2f
       0,  1,  2,  3,  4,  5,  6,  7, 8,  9,  44, -1, -1, -1, -1, -1, // 30-3f 0-9
      -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, // 40-4f A-O
      25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35,                     // 50-5a P-Z
                                                  -1, -1, -1, -1, -1, // 5b-5f
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 60-6f a-o
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,                     // 70-7a p-z
                                                  -1, -1, -1, -1, -1, // 7b-7f
  };

  private static int getCode(char c) {
    int code = TABLE[c];
    if (code > -1) {
      return code;
    }
    throw new IllegalArgumentException("Illegal char: " + c);
  }

  public void write(BitBuffer buffer) {
    char[] c = getData().toCharArray();
    int i = 0;

    while (i + 1 < c.length) {
      buffer.put(getCode(c[i]) * 45 + getCode(c[i + 1]), 11);
      i += 2;
    }

    if (i < c.length) {
      buffer.put(getCode(c[i]), 6);
    }
  }

  public int getLength() {
    return getData().length();
  }
}
