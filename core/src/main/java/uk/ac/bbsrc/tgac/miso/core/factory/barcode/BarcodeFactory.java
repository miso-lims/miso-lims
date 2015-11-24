/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.factory.barcode;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.codabar.CodabarBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.EAN128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.datamatrix.SymbolShapeHint;
import org.krysalis.barcode4j.impl.fourstate.RoyalMailCBCBean;
import org.krysalis.barcode4j.impl.fourstate.USPSIntelligentMailBean;
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5Bean;
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;
import org.krysalis.barcode4j.impl.upcean.UPCABean;
import org.krysalis.barcode4j.impl.upcean.UPCEBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;

/**
 * uk.ac.bbsrc.tgac.miso.core.factory.barcode
 * <p/>
 * Simple factory that builds barcode RenderedImages given a MISO Barcodable object and a BarcodeGenerator
 * 
 * @author Rob Davey
 * @date 09-Feb-2011
 * @since 0.0.3
 */
public class BarcodeFactory {
  protected static final Logger log = LoggerFactory.getLogger(BarcodeFactory.class);

  public final BarcodeGenerator CODABAR = new CodabarBean();
  public final BarcodeGenerator CODE128 = new Code128Bean();
  public final BarcodeGenerator CODE39 = new Code39Bean();
  public final BarcodeGenerator DATAMATRIX = new DataMatrixBean();
  public final BarcodeGenerator EAN128 = new EAN128Bean();
  public final BarcodeGenerator EAN13 = new EAN13Bean();
  public final BarcodeGenerator EAN8 = new EAN8Bean();
  public final BarcodeGenerator INTERLEAVED2OF5 = new Interleaved2Of5Bean();
  public final BarcodeGenerator PDF417 = new PDF417Bean();
  public final BarcodeGenerator ROYALMAILCBC = new RoyalMailCBCBean();
  public final BarcodeGenerator UPCA = new UPCABean();
  public final BarcodeGenerator UPCE = new UPCEBean();
  public final BarcodeGenerator USPSINTELLIGENTMAIL = new USPSIntelligentMailBean();

  private int bitmapResolution = 150;
  private int imageType = BufferedImage.TYPE_BYTE_GRAY;
  private boolean antialias = true;
  private int orientation = 0;
  private String outputType = "png";
  private float pointPixels = 4.0f;

  private static final Map<String, BarcodeGenerator> generators = new HashMap<String, BarcodeGenerator>();

  public BarcodeFactory() {
    generators.put("CODABAR", CODABAR);
    generators.put("CODE128", CODE128);
    generators.put("CODE39", CODE39);
    generators.put("DATAMATRIX", DATAMATRIX);
    generators.put("EAN128", EAN128);
    generators.put("EAN13", EAN13);
    generators.put("EAN8", EAN8);
    generators.put("INTERLEAVED2OF5", INTERLEAVED2OF5);
    generators.put("PDF417", PDF417);
    generators.put("ROYALMAILCBC", ROYALMAILCBC);
    generators.put("UPCA", UPCA);
    generators.put("UPCE", UPCE);
    generators.put("USPSINTELLIGENTMAIL", USPSINTELLIGENTMAIL);
  }

  public static BarcodeGenerator lookupGenerator(String name) {
    return generators.get(name);
  }

  public void setPointPixels(float pointPixels) {
    this.pointPixels = pointPixels;
  }

  public int getBitmapResolution() {
    return bitmapResolution;
  }

  public void setBitmapResolution(int bitmapResolution) {
    this.bitmapResolution = bitmapResolution;
  }

  public int getImageType() {
    return imageType;
  }

  public void setImageType(int imageType) {
    this.imageType = imageType;
  }

  public boolean isAntialias() {
    return antialias;
  }

  public void setAntialias(boolean antialias) {
    this.antialias = antialias;
  }

  public int getOrientation() {
    return orientation;
  }

  public void setOrientation(int orientation) {
    this.orientation = orientation;
  }

  public String getOutputType() {
    return outputType;
  }

  public void setOutputType(String outputType) {
    this.outputType = outputType;
  }

  private RenderedImage getImage(Barcodable barcodable, BarcodeGenerator barcodeGenerator, BarcodeDimension dimension) throws IOException {
    String input = barcodable.getIdentificationBarcode();

    if (!isStringEmptyOrNull(input)) {
      String enc = new String(Base64.encodeBase64(input.getBytes("UTF-8")));

      BitmapCanvasProvider provider = new BitmapCanvasProvider(bitmapResolution, imageType, antialias, orientation);
      provider.establishDimensions(dimension);
      if (barcodeGenerator instanceof AbstractBarcodeBean) {
        AbstractBarcodeBean bean = (AbstractBarcodeBean) barcodeGenerator;
        bean.setModuleWidth(UnitConv.in2mm(pointPixels / bitmapResolution));
        bean.doQuietZone(false);
        bean.generateBarcode(provider, enc);
      } else {
        barcodeGenerator.generateBarcode(provider, enc);
      }
      provider.finish();
      return provider.getBufferedImage();
    }
    return null;
  }

  private void writeImageToStream(RenderedImage image, OutputStream output) throws IOException {
    ImageIO.write(image, outputType, output);
  }

  public RenderedImage generateBarcode(Barcodable barcodable, BarcodeGenerator barcodeGenerator) throws IOException {
    return getImage(barcodable, barcodeGenerator, new BarcodeDimension(100, 100));
  }

  public RenderedImage generateBarcode(Barcodable barcodable, BarcodeGenerator barcodeGenerator, BarcodeDimension dim) throws IOException {
    return getImage(barcodable, barcodeGenerator, dim);
  }

  public void generateBarcode(Barcodable barcodable, BarcodeGenerator barcodeGenerator, OutputStream output) throws IOException {
    writeImageToStream(getImage(barcodable, barcodeGenerator, new BarcodeDimension(100, 100)), output);
  }

  public RenderedImage generateSquareDataMatrix(Barcodable barcodable, int width) throws IOException {
    DataMatrixBean dmb = (DataMatrixBean) DATAMATRIX;
    dmb.setShape(SymbolShapeHint.FORCE_SQUARE);
    return getImage(barcodable, dmb, new BarcodeDimension(width, width));
  }

  public void generateSquareDataMatrix(Barcodable barcodable, int width, OutputStream output) throws IOException {
    writeImageToStream(generateSquareDataMatrix(barcodable, width), output);
  }

  public RenderedImage generateRectDataMatrix(Barcodable barcodable, int width, int height) throws IOException {
    DataMatrixBean dmb = (DataMatrixBean) DATAMATRIX;
    dmb.setShape(SymbolShapeHint.FORCE_RECTANGLE);
    return getImage(barcodable, dmb, new BarcodeDimension(width, height));
  }

  public void generateRectDataMatrix(Barcodable barcodable, int width, int height, OutputStream output) throws IOException {
    writeImageToStream(generateRectDataMatrix(barcodable, width, height), output);
  }
}
