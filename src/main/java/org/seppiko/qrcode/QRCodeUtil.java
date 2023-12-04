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

package org.seppiko.qrcode;

import com.d_project.qrcode.ErrorCorrectionLevel;
import com.d_project.qrcode.GIFImage;
import com.d_project.qrcode.QRCode;
import java.awt.image.BufferedImage;

/**
 * QR Code utility
 *
 * @author Leonard Woo
 */
public class QRCodeUtil {

  public static String createText(QRCode qrCode, String newline) {
    StringBuilder sb = new StringBuilder();
    int moduleCount = qrCode.getModuleCount();
    for (int row = 0; row < moduleCount; row++) {
      for (int col = 0; col < moduleCount; col++) {
        sb.append(qrCode.isDark(row, col)? "1" : "0");
      }
      sb.append(newline);
    }

    return sb.toString();
  }

  public static BufferedImage createImage(QRCode qrCode, int cellSize, int margin,
      int color, int backgroundColor) {
    checkImage(cellSize, margin);
    int imageSize = qrCode.getModuleCount() * cellSize + margin * 2;
    BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);

    for (int y = 0; y < imageSize; y++) {
      for (int x = 0; x < imageSize; x++) {
        if ((margin <= x) && (x < imageSize - margin) &&
            (margin <= y) && (y < imageSize - margin)) {

          int col = (x - margin) / cellSize;
          int row = (y - margin) / cellSize;

          if (qrCode.isDark(row, col)) {
            image.setRGB(x, y, color);
          } else {
            image.setRGB(x, y, backgroundColor);
          }

        } else {
          image.setRGB(x, y, backgroundColor);
        }
      }
    }

    return image;
  }

  public static GIFImage createGIFImage(QRCode qrCode, int cellSize, int margin) {
    checkImage(cellSize, margin);

    int imageSize = qrCode.getModuleCount() * cellSize + margin * 2;
    GIFImage image = new GIFImage(imageSize, imageSize);

    for (int y = 0; y < imageSize; y++) {
      for (int x = 0; x < imageSize; x++) {
        if ((margin <= x) && (x < imageSize - margin) &&
            (margin <= y) && (y < imageSize - margin) ) {

          int col = (x - margin) / cellSize;
          int row = (y - margin) / cellSize;

          if (qrCode.isDark(row, col)) {
            image.setPixel(x, y, 0);
          } else {
            image.setPixel(x, y, 1);
          }

        } else {
          image.setPixel(x, y, 1);
        }
      }
    }
    return image;
  }

  private static void checkImage(int cellSize, int margin) {
    if (cellSize < 1 || 4 < cellSize) {
      throw new IllegalArgumentException("Illegal cell size : " + cellSize);
    }

    if (margin < 0 || 32 < margin) {
      throw new IllegalArgumentException("Illegal margin : " + margin);
    }
  }

  public static QRCode getQRCode(String text, int typeNumber, String ecl)
      throws IllegalArgumentException {
    return getQRCode(text, typeNumber, ErrorCorrectionLevel.parser(ecl));
  }

  public static QRCode getQRCode(String text, int typeNumber, int errorCorrectionLevel)
      throws IllegalArgumentException {
    checkTypeNumber(typeNumber);

    QRCode qrCode;
    if (typeNumber == 0) {
      qrCode = QRCode.getMinimumQRCode(text, errorCorrectionLevel);
    } else {
      qrCode = new QRCode();
      qrCode.addData(text);
      qrCode.setTypeNumber(typeNumber);
      qrCode.setErrorCorrectionLevel(errorCorrectionLevel);
      qrCode.make();
    }
    return qrCode;
  }

  private static void checkTypeNumber(int typeNumber) {
    if (typeNumber < 0 || 10 < typeNumber) {
      throw new IllegalArgumentException("Illegal type number: " + typeNumber);
    }
  }
}
