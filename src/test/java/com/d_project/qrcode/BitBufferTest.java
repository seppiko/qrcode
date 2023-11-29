package com.d_project.qrcode;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class BitBufferTest {

  @Test
  public void test1() {
    BitBuffer bb = new BitBuffer();
    QRData qd = new QR8BitByte("534TEST!!!あ");
    qd.write(bb);

    Assertions.assertEquals(96, bb.getLengthInBits() );
  }

  @Test
  public void test2() {
    BitBuffer bb = new BitBuffer();
    QRData qd = new QRAlphaNum("534TEST $%*+-./:");
    qd.write(bb);

    Assertions.assertEquals(88, bb.getLengthInBits() );
  }

  @Test
  public void test3() {
    BitBuffer bb = new BitBuffer();
    QRData qd = new QRKanji("あいうえお");
    qd.write(bb);

    Assertions.assertEquals(65, bb.getLengthInBits() );
  }

  @Test
  public void test4() {
    BitBuffer bb = new BitBuffer();
    QRData qd = new QRNumber("0123456789");
    qd.write(bb);

    Assertions.assertEquals(34, bb.getLengthInBits() );
  }
}
