package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.BarcodableVisitor;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.GroupIdentifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pair;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.service.printing.LabelCanvas.FontStyle;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public enum PrintableField implements PrintableText {
  ALIAS {

    @Override
    public String text(Barcodable barcodable) {
      return barcodable.getAlias();
    }

  },
  BARCODE {

    @Override
    public String text(Barcodable barcodable) {
      String str = barcodable.getIdentificationBarcode();
      if (LimsUtils.isStringBlankOrNull(str)) {
        str = barcodable.getName();
      }
      return str;
    }

  },
  BARCODE_BASE64 {

    @Override
    public String text(Barcodable barcodable) {
      return new String(Base64.encodeBase64(BARCODE.text(barcodable).getBytes(StandardCharsets.UTF_8)));
    }

  },
  CONCENTRATION {

    @Override
    public String text(Barcodable barcodable) {
      return unescapeHtml(barcodable.visit(new BarcodableVisitor<String>() {

        @Override
        public String visitLibrary(Library library) {
          return LimsUtils.makeConcentrationLabel(library.getConcentration(), library.getConcentrationUnits());
        }

        @Override
        public String visitLibraryAliquot(LibraryAliquot libraryAliquot) {
          return LimsUtils.makeConcentrationLabel(libraryAliquot.getConcentration(), libraryAliquot.getConcentrationUnits());
        }

        @Override
        public String visitSample(Sample sample) {
          return LimsUtils.makeConcentrationLabel(sample.getConcentration(), sample.getConcentrationUnits());
        }

      }));
    }
  },
  DATE {

    @Override
    public String text(Barcodable barcodable) {
      return LimsUtils.formatDate(barcodable.getBarcodeDate());
    }

  },
  DESCRIPTION {

    @Override
    public String text(Barcodable barcodable) {
      return barcodable.visit(new BarcodableVisitor<String>() {

        @Override
        public String visitKit(Kit kit) {
          return kit.getKitDescriptor().getName();
        }

        @Override
        public String visitLibrary(Library library) {
          return library.getDescription();
        }

        @Override
        public String visitPool(Pool pool) {
          return pool.getDescription();
        }

        @Override
        public String visitSample(Sample sample) {
          return sample.getDescription();
        }

      });
    }

  },
  DESIGN_CODE {

    @Override
    public String text(Barcodable barcodable) {
      return barcodable.visit(new BarcodableVisitor<String>() {

        @Override
        public String visitLibraryAliquotDetailed(DetailedLibraryAliquot libraryAliquot) {
          return libraryAliquot.getLibraryDesignCode() == null ? visitLibraryDetailed((DetailedLibrary) libraryAliquot.getLibrary())
              : libraryAliquot.getLibraryDesignCode().getCode();
        }

        @Override
        public String visitLibraryDetailed(DetailedLibrary library) {
          return library.getLibraryDesignCode().getCode();
        }

      });
    }

  },
  EXTERNAL_NAME {

    @Override
    public String text(Barcodable barcodable) {
      return barcodable.visit(getParentSampleField(SampleIdentity.class, i -> i.getExternalName()));
    }

  },
  GROUP_DESC {

    @Override
    public String text(Barcodable barcodable) {
      return barcodable.visit(new BarcodableVisitor<String>() {

        @Override
        public String visitLibraryAliquotDetailed(DetailedLibraryAliquot libraryAliquot) {
          GroupIdentifiable groupParent = libraryAliquot.getEffectiveGroupIdEntity();
          return groupParent == null ? null : groupParent.getGroupDescription();
        }

        @Override
        public String visitLibraryDetailed(DetailedLibrary library) {
          GroupIdentifiable groupParent = library.getEffectiveGroupIdEntity();
          return groupParent == null ? null : groupParent.getGroupDescription();
        }

        @Override
        public String visitSampleDetailed(DetailedSample sample) {
          GroupIdentifiable groupParent = sample.getEffectiveGroupIdEntity();
          return groupParent == null ? null : groupParent.getGroupDescription();
        }

      });
    }
  },
  LABEL_TEXT {

    @Override
    public String text(Barcodable barcodable) {
      return barcodable.getLabelText();
    }

  },
  NAME {

    @Override
    public String text(Barcodable barcodable) {
      return barcodable.getName();
    }

  },
  SECONDARY_ID {

    @Override
    public String text(Barcodable barcodable) {
      return barcodable.visit(getParentSampleField(SampleTissue.class, t -> t.getSecondaryIdentifier()));
    }

  },
  SIZE {

    @Override
    public String text(Barcodable barcodable) {
      return barcodable.visit(new BarcodableVisitor<String>() {

        @Override
        public String visitBox(Box box) {
          return box.getSize().getLabel();
        }

        @Override
        public String visitContainer(SequencerPartitionContainer container) {
          return String.format("%s: %d", container.getModel().getPlatformType().getPluralPartitionName(), container.getPartitions().size());
        }

        @Override
        public String visitLibrary(Library library) {
          return library.getDnaSize() == null ? null : library.getDnaSize().toString();
        }

        @Override
        public String visitLibraryAliquot(LibraryAliquot libraryAliquot) {
          return libraryAliquot.getDnaSize() == null ? visitLibrary(libraryAliquot.getLibrary()) : libraryAliquot.getDnaSize().toString();
        }

      });
    }

  },
  TISSUE_NUMBERS {

    @Override
    public String text(Barcodable barcodable) {
      return barcodable.visit(getParentSampleField(SampleTissue.class, t -> {
        if (t.getTimesReceived() == null || t.getTubeNumber() == null) {
          return null;
        }
        return t.getTimesReceived() + "-" + t.getTubeNumber();
      }));
    }

  },
  TISSUE_ORIGIN {

    @Override
    public String text(Barcodable barcodable) {
      return barcodable.visit(getParentSampleField(SampleTissue.class, t -> t.getTissueOrigin().getAlias()));
    }

  },
  TISSUE_TYPE {

    @Override
    public String text(Barcodable barcodable) {
      return barcodable.visit(getParentSampleField(SampleTissue.class, t -> t.getTissueType().getAlias()));
    }

  };

  private static <T extends DetailedSample> BarcodableVisitor<String> getParentSampleField(Class<T> clazz, Function<T, String> getter) {
    return new BarcodableVisitor<String>() {

      @Override
      public String visitLibraryAliquotDetailed(DetailedLibraryAliquot libraryAliquot) {
        T sampleTissue = LimsUtils.getParent(clazz, (DetailedSample) libraryAliquot.getLibrary().getSample());
        return sampleTissue == null ? null : getter.apply(sampleTissue);
      }

      @Override
      public String visitLibraryDetailed(DetailedLibrary library) {
        T sampleTissue = LimsUtils.getParent(clazz, (DetailedSample) library.getSample());
        return sampleTissue == null ? null : getter.apply(sampleTissue);
      }

      @Override
      public String visitSampleDetailed(DetailedSample sample) {
        T sampleTissue = LimsUtils.getParent(clazz, sample);
        return sampleTissue == null ? null : getter.apply(sampleTissue);
      }

    };
  }

  private static String unescapeHtml(String string) {
    if (string == null) return null;
    return StringEscapeUtils.unescapeHtml(string);
  }

  @Override
  public final JsonNode asJson() {
    final ObjectNode node = JsonNodeFactory.instance.objectNode();
    node.put("use", name());
    return node;
  }

  @Override
  public final Pair<FontStyle, String> line(Barcodable barcodable) {
    return new Pair<>(FontStyle.REGULAR, text(barcodable));
  }

  @Override
  public final Stream<Pair<FontStyle, String>> lines(Barcodable barcodable) {
    return Stream.of(line(barcodable));
  }

}
