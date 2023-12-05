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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * GIFイメージ(B/W)
 *
 * @author Kazuhiko Arase
 * @author Leonard Woo
 */
public class GIFImage {

  private final int width;
  private final int height;
  private final int[] data;

  /**
   * Initialize gif image size.
   *
   * @param width 幅
   * @param height 高さ
   */
  public GIFImage(int width, int height) {
    this.width = width;
    this.height = height;
    this.data = new int[width * height];
  }

  private static void write(OutputStream out, int i) throws IOException {
    out.write(i & 0xff);
    out.write( (i >>> 8) & 0xff);
  }

  /**
   * Set pixel value.
   *
   * @param x Pixel position X.
   * @param y Pixel position Y.
   * @param pixel Pixel value.
   */
  public void setPixel(int x, int y, int pixel) {
    if ( (x < 0 || width <= x) ||
         (y < 0 || height <= y) ) {
      throw new IllegalArgumentException();
    }
    data[y * width + x] = pixel;
  }

  /**
   * Get pixel value.
   *
   * @param x Pixel position X.
   * @param y Pixel position Y.
   * @return Pixel value.
   */
  public int getPixel(int x, int y) {
    if (x < 0 || width  <= x) throw new IllegalArgumentException();
    if (y < 0 || height <= y) throw new IllegalArgumentException();
    return data[y * width + x];
  }

  /**
   * Write image to OutputStream.
   *
   * @param out OutputStream.
   * @throws IOException if an I/O error occurs.
   */
  public void write(OutputStream out) throws IOException {

    //---------------------------------
    // GIF Signature

    out.write("GIF87a".getBytes(StandardCharsets.ISO_8859_1) );

    //---------------------------------
    // Screen Descriptor

    write(out, width);
    write(out, height);

    out.write(0x80); // 2bit
    out.write(0);
    out.write(0);

    //---------------------------------
    // Global Color Map

    // black
    out.write(0x00);
    out.write(0x00);
    out.write(0x00);

    // white
    out.write(0xff);
    out.write(0xff);
    out.write(0xff);

    //---------------------------------
    // Image Descriptor

    out.write(',');
    write(out, 0);
    write(out, 0);
    write(out, width);
    write(out, height);
    out.write(0);

    //---------------------------------
    // Local Color Map

    //---------------------------------
    // Raster Data

    int lzwMinCodeSize = 2;
    byte[] raster = getLZWRaster(lzwMinCodeSize);
    out.write(lzwMinCodeSize);

    int offset = 0;
    while (raster.length - offset > 255) {
      out.write(255);
      out.write(raster, offset, 255);
      offset += 255;
    }

    out.write(raster.length - offset);
    out.write(raster, offset, raster.length - offset);
    out.write(0x00);

    //---------------------------------
    // GIF Terminator
    out.write(';');

    out.flush();
  }

  private byte[] getLZWRaster(int lzwMinCodeSize) throws IOException {
    int clearCode = 1 << lzwMinCodeSize;
    int endCode = (1 << lzwMinCodeSize) + 1;
    int bitLength = lzwMinCodeSize + 1;

    // Setup LZWTable
    LZWTable table = new LZWTable();

    for (int i = 0; i < clearCode; i++) {
      table.add(String.valueOf( (char)i) );
    }
    table.add(String.valueOf( (char)clearCode) );
    table.add(String.valueOf( (char)endCode) );


    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    BitOutputStream bitOut = new BitOutputStream(byteOut);

    try {
      // clear code
      bitOut.write(clearCode, bitLength);

      int dataIndex = 0;
      String s = String.valueOf( (char)data[dataIndex++]);

      while (dataIndex < data.length) {
        char c = (char)data[dataIndex++];
        if (table.contains(s + c) ) {
          s = s + c;
        } else {
          bitOut.write(table.indexOf(s), bitLength);
          if (table.size() < 0xfff) {
            if (table.size() == (1 << bitLength) ) {
              bitLength++;
            }
            table.add(s + c);
          }
          s = String.valueOf(c);
        }
      }
      bitOut.write(table.indexOf(s), bitLength);

      // end code
      bitOut.write(endCode, bitLength);
    } finally {
      bitOut.close();
    }

    return byteOut.toByteArray();
  }

  private static class LZWTable {
    private final Map<String, Integer> map;

    public LZWTable() {
      map = new HashMap<>();
    }

    public void add(String key) {
      if (contains(key) ) {
        throw new IllegalArgumentException("dup key:" + key);
      }
      map.put(key, map.size() );
    }

    public int size() {
      return map.size();
    }

    public int indexOf(String key) {
      return map.get(key);
    }

    public boolean contains(String key) {
      return map.containsKey(key);
    }
  }

  private static class BitOutputStream {
    private final OutputStream out;
    private int bitLength;
    private int bitBuffer;

    public BitOutputStream(OutputStream out) {
      this.out = out;
      this.bitLength = 0;
    }

    public void write(int data, int length) throws IOException{

      if ( (data >>> length) != 0) {
        throw new IOException("length over");
      }

      while (bitLength + length >= 8) {
        out.write(0xff & ( (data << bitLength) | bitBuffer) );
        length -= (8 - bitLength);
        data >>>= (8 - bitLength);
        bitBuffer = 0;
        bitLength = 0;
      }

      bitBuffer = (data << bitLength) | bitBuffer;
      bitLength = bitLength + length;
    }

    public void flush() throws IOException {
      if (bitLength > 0) {
        out.write(bitBuffer);
      }
      out.flush();
    }

    public void close() throws IOException {
      flush();
      out.close();
    }
  }
}
