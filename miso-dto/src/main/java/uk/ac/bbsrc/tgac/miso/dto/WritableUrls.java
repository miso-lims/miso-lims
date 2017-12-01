package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.springframework.web.util.UriComponentsBuilder;

public interface WritableUrls {
  public static String buildUriPath(URI baseUri, String path, Object... values) {
    return UriComponentsBuilder.fromUri(baseUri).path(path).buildAndExpand(values).toUriString();
  }

  public void writeUrls(URI baseUri);

  public default void writeUrls(UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    writeUrls(baseUri);
  }
}
