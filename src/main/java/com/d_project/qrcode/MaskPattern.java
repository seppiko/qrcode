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
 * マスクパターン.
 *
 * @author Kazuhiko Arase
 */
interface MaskPattern {

  /** マスクパターン000 */
  int PATTERN000 = 0;

  /** マスクパターン001 */
  int PATTERN001 = 1;

  /** マスクパターン010 */
  int PATTERN010 = 2;

  /** マスクパターン011 */
  int PATTERN011 = 3;

  /** マスクパターン100 */
  int PATTERN100 = 4;

  /** マスクパターン101 */
  int PATTERN101 = 5;

  /** マスクパターン110 */
  int PATTERN110 = 6;

  /** マスクパターン111 */
  int PATTERN111 = 7;
}
