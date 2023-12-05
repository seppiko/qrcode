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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * QRコード.
 * <br/>■使い方
 * <br/>(1) 誤り訂正レベル、データ等、諸パラメータを設定します。
 * <br/>(2) make() を呼び出してQRコードを作成します。
 * <br/>(3) getModuleCount() と isDark() で、QRコードのデータを取得します。
 * <br/>
 *
 * @author Kazuhiko Arase
 * @author Leonard Woo
 */
public class QRCode {

  private static final int PAD0 = 0xEC;
  private static final int PAD1 = 0x11;

  private static String _8BitByteEncoding = QRUtil.getJISEncoding();
  private int typeNumber;
  private Boolean[][] modules;
  private int moduleCount;
  private int errorCorrectionLevel;
  private final List<QRData> qrDataList;

  /**
   * コンストラクタ
   * <br>型番1, 誤り訂正レベルH のQRコードのインスタンスを生成します。
   *
   * @see ErrorCorrectionLevel
   */
  public QRCode() {
    this.typeNumber = 1;
    this.errorCorrectionLevel = ErrorCorrectionLevel.H;
    this.qrDataList = new ArrayList<>(1);
  }

  static byte[] createData(int typeNumber, int errorCorrectionLevel, QRData[] dataArray) {
    RSBlock[] rsBlocks = RSBlock.getRSBlocks(typeNumber, errorCorrectionLevel);
    BitBuffer buffer = new BitBuffer();

    for (QRData data : dataArray) {
      buffer.put(data.getMode(), 4);
      buffer.put(data.getLength(), data.getLengthInBits(typeNumber));
      data.write(buffer);
    }

    // 最大データ数を計算
    int totalDataCount = 0;
    for (RSBlock rsBlock : rsBlocks) {
      totalDataCount += rsBlock.getDataCount();
    }

    if (buffer.getLengthInBits() > totalDataCount * 8) {
      throw new IllegalArgumentException("code length overflow. ("
        + buffer.getLengthInBits()
        + ">"
        +  totalDataCount * 8
        + ")");
    }

    // 終端コード
    if (buffer.getLengthInBits() + 4 <= totalDataCount * 8) {
      buffer.put(0, 4);
    }

    // padding
    while (buffer.getLengthInBits() % 8 != 0) {
      buffer.put(false);
    }

    // padding
    while (true) {
      if (buffer.getLengthInBits() >= totalDataCount * 8) {
        break;
      }
      buffer.put(PAD0, 8);

      if (buffer.getLengthInBits() >= totalDataCount * 8) {
        break;
      }
      buffer.put(PAD1, 8);
    }

    return createBytes(buffer, rsBlocks);
  }

  private static byte[] createBytes(BitBuffer buffer, RSBlock[] rsBlocks) {
    int offset = 0;
    int maxDcCount = 0;
    int maxEcCount = 0;

    int[][] dcdata = new int[rsBlocks.length][];
    int[][] ecdata = new int[rsBlocks.length][];

    for (int r = 0; r < rsBlocks.length; r++) {
      int dcCount = rsBlocks[r].getDataCount();
      int ecCount = rsBlocks[r].getTotalCount() - dcCount;

      maxDcCount = Math.max(maxDcCount, dcCount);
      maxEcCount = Math.max(maxEcCount, ecCount);

      dcdata[r] = new int[dcCount];
      for (int i = 0; i < dcdata[r].length; i++) {
        dcdata[r][i] = 0xff & buffer.getBuffer()[i + offset];
      }
      offset += dcCount;

      Polynomial rsPoly = QRUtil.getErrorCorrectPolynomial(ecCount);
      Polynomial rawPoly = new Polynomial(dcdata[r], rsPoly.getLength() - 1);
      Polynomial modPoly = rawPoly.mod(rsPoly);
      ecdata[r] = new int[rsPoly.getLength() - 1];
      for (int i = 0; i < ecdata[r].length; i++) {
        int modIndex = i + modPoly.getLength() - ecdata[r].length;
        ecdata[r][i] = (modIndex >= 0)? modPoly.get(modIndex) : 0;
      }
    }

    int totalCodeCount = 0;
    for (RSBlock rsBlock : rsBlocks) {
      totalCodeCount += rsBlock.getTotalCount();
    }

    byte[] data = new byte[totalCodeCount];

    int index = 0;

    for (int i = 0; i < maxDcCount; i++) {
      for (int r = 0; r < rsBlocks.length; r++) {
        if (i < dcdata[r].length) {
          data[index++] = (byte)dcdata[r][i];
        }
      }
    }

    for (int i = 0; i < maxEcCount; i++) {
      for (int r = 0; r < rsBlocks.length; r++) {
        if (i < ecdata[r].length) {
          data[index++] = (byte)ecdata[r][i];
        }
      }
    }

    return data;
  }

  /**
   * 最小の型番となる QRCode を作成する。
   *
   * @param data データ
   * @param errorCorrectionLevel 誤り訂正レベル
   * @return QRCode instance.
   */
  public static QRCode getMinimumQRCode(String data, int errorCorrectionLevel) {
    int mode = QRUtil.getMode(data);

    QRCode qr = new QRCode();
    qr.setErrorCorrectionLevel(errorCorrectionLevel);
    qr.addData(data, mode);

    int length = qr.getData(0).getLength();

    for (int typeNumber = 1; typeNumber <= 10; typeNumber++) {
      if (length <= QRUtil.getMaxLength(typeNumber, mode, errorCorrectionLevel) ) {
        qr.setTypeNumber(typeNumber);
        break;
      }
    }

    qr.make();

    return qr;
  }

  /**
   * Get 8Bit encoding
   *
   * @return Encoding name.
   */
  protected static String get8BitByteEncoding() {
    return _8BitByteEncoding;
  }

  /**
   * Set 8Bit encoding
   *
   * @param _8BitByteEncoding Encoding name.
   */
  protected static void set8BitByteEncoding(final String _8BitByteEncoding) {
    QRCode._8BitByteEncoding = _8BitByteEncoding;
  }

  /**
   * 型番を取得する。
   *
   * @return 型番
   */
  public int getTypeNumber() {
    return typeNumber;
  }

  /**
   * 型番を設定する。
   *
   * @param typeNumber 型番
   */
  public void setTypeNumber(int typeNumber) {
    this.typeNumber = typeNumber;
  }

  /**
   * 誤り訂正レベルを取得する。
   *
   * @return 誤り訂正レベル
   * @see ErrorCorrectionLevel
   */
  public int getErrorCorrectionLevel() {
      return errorCorrectionLevel;
  }

  /**
   * 誤り訂正レベルを設定する。
   *
   * @param errorCorrectionLevel 誤り訂正レベル
   * @see ErrorCorrectionLevel
   */
  public void setErrorCorrectionLevel(int errorCorrectionLevel) {
      this.errorCorrectionLevel = errorCorrectionLevel;
  }

  /**
   * データを追加する。
   *
   * @param data データ
   */
  public void addData(String data) {
    addData(data, QRUtil.getMode(data));
  }

  /**
   * モードを指定してデータを追加する。
   *
   * @param data データ
   * @param mode モード
   * @see Mode
   */
  public void addData(String data, int mode) {
    if (Mode.MODE_NUMBER == mode) {
      addData(new QRNumber(data));
    } else if (Mode.MODE_ALPHA_NUM == mode) {
      addData(new QRAlphaNum(data));
    } else if (Mode.MODE_8BIT_BYTE == mode) {
      addData(new QR8BitByte(data));
    } else if (Mode.MODE_KANJI == mode) {
      addData(new QRKanji(data));
    } else {
      throw new IllegalArgumentException("mode: " + mode);
    }
  }

  /**
   * データをクリアする。
   * <br/>addData で追加されたデータをクリアします。
   */
  public void clearData() {
    qrDataList.clear();
  }

  /**
   * Add QRData .
   *
   * @param qrData QRData instance.
   */
  private void addData(QRData qrData) {
    qrDataList.add(qrData);
  }

  /**
   * Add QRData list count.
   *
   * @return QRData list count.
   */
  private int getDataCount() {
    return qrDataList.size();
  }

  /**
   * Get QRData with index
   *
   * @param index QRData list index.
   * @return QRData instance.
   */
  private QRData getData(int index) {
    return qrDataList.get(index);
  }

  /**
   * 暗モジュールかどうかを取得する。
   *
   * @param row 行 (0 ～ モジュール数 - 1)
   * @param col 列 (0 ～ モジュール数 - 1)
   * @return true if this pixel is dark.
   */
  public boolean isDark(int row, int col) {
    return Optional.ofNullable(modules[row][col]).orElse(false);
  }

  /**
   * モジュール数を取得する。
   *
   * @return モジュール数
   */
  public int getModuleCount() {
    return moduleCount;
  }

  /**
   * QRコードを作成する。
   */
  public void make() {
    make(false, getBestMaskPattern());
  }

  private int getBestMaskPattern() {
    int minLostPoint = 0;
    int pattern = 0;
    for (int i = 0; i < 8; i++) {
      make(true, i);
      int lostPoint = QRUtil.getLostPoint(this);

      if (i == 0 || minLostPoint >  lostPoint) {
        minLostPoint = lostPoint;
        pattern = i;
      }
    }

    return pattern;
  }

  /**
   * QRコードを作成する。
   */
  private void make(boolean test, int maskPattern) {
    // モジュール初期化
    moduleCount = typeNumber * 4 + 17;
    modules = new Boolean[moduleCount][moduleCount];

    // 位置検出パターン及び分離パターンを設定
    setupPositionProbePattern(0, 0);
    setupPositionProbePattern(moduleCount - 7, 0);
    setupPositionProbePattern(0, moduleCount - 7);

    setupPositionAdjustPattern();
    setupTimingPattern();

    setupTypeInfo(test, maskPattern);

    if (typeNumber >= 7) {
      setupTypeNumber(test);
    }

    QRData[] dataArray = qrDataList.toArray(QRData[]::new);
    byte[] data = createData(typeNumber, errorCorrectionLevel, dataArray);

    mapData(data, maskPattern);
  }

  private void mapData(byte[] data, int maskPattern) {
    int inc = -1;
    int row = moduleCount - 1;
    int bitIndex = 7;
    int byteIndex = 0;

    for (int col = moduleCount - 1; col > 0; col -= 2) {
      if (col == 6) {
        col--;
      }

      while (true) {
        for (int c = 0; c < 2; c++) {

          if (modules[row][col - c] == null) {
            boolean dark = false;
            if (byteIndex < data.length) {
              dark = ( ( (data[byteIndex] >>> bitIndex) & 1) == 1);
            }

            boolean mask = QRUtil.getMask(maskPattern, row, col - c);
            if (mask) {
              dark = !dark;
            }

            modules[row][col - c] = dark;
            bitIndex--;

            if (bitIndex == -1) {
              byteIndex++;
              bitIndex = 7;
            }
          }
        }

        row += inc;

        if (row < 0 || moduleCount <= row) {
          row -= inc;
          inc = -inc;
          break;
        }
      }
    }
  }

  /**
   * 位置合わせパターンを設定
   */
  private void setupPositionAdjustPattern() {
    int[] pos = QRUtil.getPatternPosition(typeNumber);
    for (int row : pos) {
      for (int col : pos) {
        if (modules[row][col] != null) {
          continue;
        }

        for (int r = -2; r <= 2; r++) {
          for (int c = -2; c <= 2; c++) {
            modules[row + r][col + c] =
                (r == -2 || r == 2 || c == -2 || c == 2 || (r == 0 && c == 0));
          }
        }
      }
    }
  }

  /**
   * 位置検出パターンを設定
   */
  private void setupPositionProbePattern(int row, int col) {
    for (int r = -1; r <= 7; r++) {
      for (int c = -1; c <= 7; c++) {
        if ( setupProbe0(row + r) || setupProbe0(col + c) ) {
          continue;
        }

        modules[row + r][col + c] =
            ( (0 <= r && r <= 6 && (c == 0 || c == 6) ) ||
                (0 <= c && c <= 6 && (r == 0 || r == 6) ) ||
                (2 <= r && r <= 4 && 2 <= c && c <= 4) );
      }
    }
  }

  private boolean setupProbe0(int position) {
    return (position <= -1) || (moduleCount <= position);
  }

  /**
   * タイミングパターンを設定
   */
  private void setupTimingPattern() {
    for (int r = 8; r < moduleCount - 8; r++) {
      if (modules[r][6] != null) {
        continue;
      }
      modules[r][6] = (r % 2 == 0);
    }
    for (int c = 8; c < moduleCount - 8; c++) {
      if (modules[6][c] != null) {
        continue;
      }
      modules[6][c] = (c % 2 == 0);
    }
  }

  /**
   * 型番を設定
   */
  private void setupTypeNumber(boolean test) {
    int bits = QRUtil.getBCHTypeNumber(typeNumber);
    for (int i = 0; i < 18; i++) {
      modules[i / 3][i % 3 + moduleCount - 8 - 3] = (!test && ( (bits >> i) & 1) == 1);
    }
    for (int i = 0; i < 18; i++) {
      modules[i % 3 + moduleCount - 8 - 3][i / 3] = (!test && ( (bits >> i) & 1) == 1);
    }
  }

  /**
   * 形式情報を設定
   */
  private void setupTypeInfo(boolean test, int maskPattern) {
    int data = (errorCorrectionLevel << 3) | maskPattern;
    int bits = QRUtil.getBCHTypeInfo(data);

    // 縦方向
    for (int i = 0; i < 15; i++) {
      boolean mod = (!test && ( (bits >> i) & 1) == 1);
      if (i < 6) {
        modules[i][8] = mod;
      } else if (i < 8) {
        modules[i + 1][8] = mod;
      } else {
        modules[moduleCount - 15 + i][8] = mod;
      }
    }

    // 横方向
    for (int i = 0; i < 15; i++) {
      boolean mod = (!test && ( (bits >> i) & 1) == 1);
      if (i < 8) {
        modules[8][moduleCount - i - 1] = mod;
      } else if (i < 9) {
        modules[8][15 - i - 1 + 1] = mod;
      } else {
        modules[8][15 - i - 1] = mod;
      }
    }

    // 固定
    modules[moduleCount - 8][8] = (!test);
  }
}
