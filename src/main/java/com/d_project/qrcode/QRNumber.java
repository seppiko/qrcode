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
 * QRNumber
 *
 * @author Kazuhiko Arase
 */
class QRNumber extends QRData {

  public QRNumber(String data) {
    super(Mode.MODE_NUMBER, data);
  }

  private static int parseInt(String s) {
    int num = 0;
    for (int i = 0; i < s.length(); i++) {
      num = num * 10 + parseInt(s.charAt(i));
    }
    return num;
  }

  private static int parseInt(char c) {
    if ('0' <= c && c <= '9') {
      return c - '0';
    }
    throw new IllegalArgumentException("illegal char :" + c);
  }

  public void write(BitBuffer buffer) {

    String data = getData();

    int i = 0;

    while (i + 2 < data.length()) {
      int num = parseInt(data.substring(i, i + 3));
      buffer.put(num, 10);
      i += 3;
    }

    if (i < data.length()) {
      if (data.length() - i == 1) {
        int num = parseInt(data.substring(i, i + 1));
        buffer.put(num, 4);
      } else if (data.length() - i == 2) {
        int num = parseInt(data.substring(i, i + 2));
        buffer.put(num, 7);
      }
    }
  }

  public int getLength() {
    return getData().length();
  }
}
