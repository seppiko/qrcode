package com.d_project.qrcode;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;


public class GIFImageTest {

  @Test
  public void test() throws Exception {

    GIFImage g = new GIFImage(2, 2);
    g.setPixel(0, 0, 1);
    g.setPixel(0, 1, 0);
    g.setPixel(1, 0, 0);
    g.setPixel(1, 1, 1);

    ByteArrayOutputStream b = new ByteArrayOutputStream();
    g.write(b);
    byte[] raw = b.toByteArray();
    for (int i = 0; i < raw.length; i++) {
      if (i > 0) {
        System.out.print(",");
      }
      System.out.print(raw[i] & 0xff);
    }
    System.out.println();
  }
}