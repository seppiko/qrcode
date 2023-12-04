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
 * Polynomial
 *
 * @author Kazuhiko Arase
 */
class Polynomial {

  private final int[] num;

  public Polynomial(int[] num) {
    this(num, 0);
  }

  public Polynomial(int[] num, int shift) {
    int offset = 0;

    while (offset < num.length && num[offset] == 0) {
      offset++;
    }

    this.num = new int[num.length - offset + shift];
    System.arraycopy(num, offset, this.num, 0, num.length - offset);
  }

  public int get(int index) {
    return num[index];
  }

  public int getLength() {
    return num.length;
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();

    for (int i = 0; i < getLength(); i++) {
      if (i > 0) {
        buffer.append(",");
      }
      buffer.append(get(i));
    }

    return buffer.toString();
  }

  public String toLogString() {
    StringBuilder buffer = new StringBuilder();

    for (int i = 0; i < getLength(); i++) {
      if (i > 0) {
        buffer.append(",");
      }
      buffer.append(QRMath.glog(get(i)));
    }

    return buffer.toString();
  }

  public Polynomial multiply(Polynomial e) {
    int[] num = new int[getLength() + e.getLength() - 1];

    for (int i = 0; i < getLength(); i++) {
      for (int j = 0; j < e.getLength(); j++) {
        num[i + j] ^= QRMath.gexp(QRMath.glog(get(i)) + QRMath.glog(e.get(j)));
      }
    }

    return new Polynomial(num);
  }

  public Polynomial mod(Polynomial e) {
    if (getLength() - e.getLength() < 0) {
      return this;
    }

    // 最上位桁の比率
    int ratio = QRMath.glog(get(0)) - QRMath.glog(e.get(0));

    // コピー作成
    int[] num = new int[getLength()];
    for (int i = 0; i < getLength(); i++) {
      num[i] = get(i);
    }

    // 引き算して余りを計算
    for (int i = 0; i < e.getLength(); i++) {
      num[i] ^= QRMath.gexp(QRMath.glog(e.get(i)) + ratio);
    }

    // 再帰計算
    return new Polynomial(num).mod(e);
  }
}
