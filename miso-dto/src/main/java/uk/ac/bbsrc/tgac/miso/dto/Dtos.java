package uk.ac.bbsrc.tgac.miso.dto;

import java.util.Set;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;

public class Dtos {

  private static DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();

  public static TissueOriginDto asDto(TissueOrigin from) {
    TissueOriginDto dto = new TissueOriginDto();
    dto.setId(from.getTissueOriginId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
    return dto;
  }

  public static Set<TissueOriginDto> asDto(Set<TissueOrigin> from) {
    Set<TissueOriginDto> dtoSet = Sets.newHashSet();
    for (TissueOrigin tissueOrigin : from) {
      dtoSet.add(asDto(tissueOrigin));
    }
    return dtoSet;
  }

  public static TissueOrigin to(TissueOriginDto from) {
    TissueOrigin to = new TissueOriginImpl();
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    return to;
  }

}
