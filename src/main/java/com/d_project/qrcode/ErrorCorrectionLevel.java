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
   * Parser Error Correction Level.
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
