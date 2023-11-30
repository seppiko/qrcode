package com.d_project.qrcode;

/**
 * QRData
 *
 * @author Kazuhiko Arase
 * @author Leonard Woo
 */
abstract class QRData {

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
