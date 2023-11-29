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
