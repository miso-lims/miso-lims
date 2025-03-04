package uk.ac.bbsrc.tgac;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.setDateString;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ApiKey;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.UserDto;

public class ApiKeyDto {

  private long id;
  private String key;
  private UserDto user;
  private String creatorName;
  private String created;

  public static ApiKeyDto from(ApiKey from) {
    ApiKeyDto to = new ApiKeyDto();
    to.setId(from.getId());
    to.setUser(Dtos.asDto(from.getUser()));
    to.setCreatorName(from.getCreator().getFullName());
    setDateString(to::setCreated, from.getCreated());
    // Include the "key" portion, but obscure the "secret" portion
    to.setKey(from.getKey() + "-" + "********************************");
    return to;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public UserDto getUser() {
    return user;
  }

  public void setUser(UserDto user) {
    this.user = user;
  }

  public String getCreatorName() {
    return creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

}
