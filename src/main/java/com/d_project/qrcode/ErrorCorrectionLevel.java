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
 * 誤り訂正レベル.
 *
 * @author Kazuhiko Arase
 * @author Leonard Woo
 */
public interface ErrorCorrectionLevel {

  /** 復元能力 7%. */
  int L = 1;

  /** 復元能力 15%. */
  int M = 0;

  /** 復元能力 25%. */
  int Q = 3;

  /** 復元能力 30%. */
  int H = 2;

  /**
   * Parser Error Correction Level String.
   *
   * @param ecl Error Correction Level String.
   * @return Error Correction Level Number.
   */
  static int parser(String ecl) {
    return switch (ecl) {
      case "l", "L" -> L;
      case "m", "M" -> M;
      case "q", "Q" -> Q;
      case "h", "H" -> H;
      default -> throw new IllegalArgumentException("Invalid error correct level: " + ecl);
    };
  }
}
