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
 * BitBuffer
 *
 * @author Kazuhiko Arase
 */
class BitBuffer {

  private byte[] buffer;
  private int length;
  private final int inclements;

  public BitBuffer() {
    inclements = 32;
    buffer = new byte[inclements];
    length = 0;
  }

  public byte[] getBuffer() {
    return buffer;
  }

  public int getLengthInBits() {
    return length;
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < getLengthInBits(); i++) {
      buffer.append(get(i)? '1': '0');
    }
    return buffer.toString();
  }

  private boolean get(int index) {
    return ((buffer[index / 8] >>> (7 - index % 8)) & 1) == 1;
  }

  public void put(int num, int length) {
    for (int i = 0; i < length; i++) {
      put(((num >>> (length - i - 1)) & 1) == 1);
    }
  }

  public void put(boolean bit) {
    if (length == buffer.length * 8) {
      byte[] newBuffer = new byte[buffer.length + inclements];
      System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
      buffer = newBuffer;
    }

    if (bit) {
      buffer[length / 8] |= (byte) (0x80 >>> (length % 8));
    }

    length++;
  }
}
