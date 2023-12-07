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
 * QRKanji
 *
 * @author Kazuhiko Arase
 */
class QRKanji extends QRData {

  public QRKanji(String data) {
    super(Mode.MODE_KANJI, data);
  }

  public void write(BitBuffer buffer) {
    byte[] data = getData().getBytes(QRUtil.getJISEncoding());
    int i = 0;
    while (i + 1 < data.length) {
      int c = ((0xff & data[i]) << 8) | (0xff & data[i + 1]);

      if (QRUtil.between(c, 0x8140, 0x9FFC)) {
        c -= 0x8140;
      } else if (QRUtil.between(c, 0xE040, 0xEBBF)) {
        c -= 0xC140;
      } else {
        throw new IllegalArgumentException(
            "Illegal char at " + (i + 1) + "/" + Integer.toHexString(c));
      }

      c = ((c >>> 8) & 0xff) * 0xC0 + (c & 0xff);
      buffer.put(c, 13);
      i += 2;
    }

    if (i < data.length) {
      throw new IllegalArgumentException("Illegal char at " + (i + 1));
    }
  }

  public int getLength() {
    return getData().getBytes(QRUtil.getJISEncoding()).length / 2;
  }
}
