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

import java.io.Serializable;

/**
 * QRData
 *
 * @author Kazuhiko Arase
 * @author Leonard Woo
 */
abstract class QRData implements Serializable {

  private final int mode;
  private final String data;

  protected QRData(int mode, String data) {
    this.mode = mode;
    this.data = data;
  }

  public int getMode() {
    return mode;
  }

  public String getData() {
    return data;
  }

  public abstract int getLength();

  public abstract void write(BitBuffer buffer);

  /**
   * 型番及びモードに対するビット長を取得する。
   *
   * @param type 型番
   * @return ビット長
   */
  public int getLengthInBits(int type) {
    if (1 <= type && type < 10) {
      // 1 - 9
      return switch (mode) {
        case Mode.MODE_NUMBER -> 10;
        case Mode.MODE_ALPHA_NUM -> 9;
        case Mode.MODE_8BIT_BYTE, Mode.MODE_KANJI -> 8;
        default -> throw new IllegalArgumentException("mode: " + mode);
      };

    } else if (type < 27) {
      // 10 - 26
      return switch (mode) {
        case Mode.MODE_NUMBER -> 12;
        case Mode.MODE_ALPHA_NUM -> 11;
        case Mode.MODE_8BIT_BYTE -> 16;
        case Mode.MODE_KANJI -> 10;
        default -> throw new IllegalArgumentException("mode: " + mode);
      };

    } else if (type < 41) {
      // 27 - 40
      return switch (mode) {
        case Mode.MODE_NUMBER -> 14;
        case Mode.MODE_ALPHA_NUM -> 13;
        case Mode.MODE_8BIT_BYTE -> 16;
        case Mode.MODE_KANJI -> 12;
        default -> throw new IllegalArgumentException("mode: " + mode);
      };

    }
    throw new IllegalArgumentException("type: " + type);
  }
}
