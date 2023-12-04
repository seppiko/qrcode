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

  private static int getCode(char c) {
    if ('0' <= c && c <= '9') {
      return c - '0';
    } else if ('A' <= c && c <= 'Z') {
      return c - 'A' + 10;
    } else {
      return switch (c) {
        case ' ' -> 36;
        case '$' -> 37;
        case '%' -> 38;
        case '*' -> 39;
        case '+' -> 40;
        case '-' -> 41;
        case '.' -> 42;
        case '/' -> 43;
        case ':' -> 44;
        default -> throw new IllegalArgumentException("illegal char :" + c);
      };
    }
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
