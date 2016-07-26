package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class DataTablesResponseDto<T> {
  /**
   * The DataTables Request contains the following parameters:
   * <ul>
   * <li><b>iDisplayStart</b> Integer : start point of current data set (database offset)</li>
   * <li><b>iDisplayLength</b> Integer : number of records to be returned, unless database contains fewer records (database limit)</li>
   * <li><b>sSearch</b> String : global search field.</li>
   * <li><b>sSortDir_0</b> String : direction of sort (asc/desc).</li>
   * <li><b>iSortCol_0</b> Integer : index of column being sorted on.</li>
   * <li><b>mDataProp_(int)</b> String : property name for column at position (int). Useful to figure out name of column being sorted on.
   * </li>
   * <li><b>sEcho</b> String : request number sent from client. Response should include this number (cast to Integer).</li>
   * </ul>
   */

  /**
   * Total number of records in the database.
   */
  private Long iTotalRecords;

  /**
   * Total number of records after filtering (ie. total number of search hits, which may be larger than total number of records being
   * returned in a particular search).
   */
  private Long iTotalDisplayRecords;

  /**
   * An unaltered copy of sEcho which is sent from the client side (changes with each request). Should be cast to Integer to avoid XSS
   * attacks.
   */
  private Long sEcho;

  /**
   * The returned data in a 2D array.
   */
  private List<T> aaData;

  public Long getITotalRecords() {
    return iTotalRecords;
  }

  @JsonProperty("iTotalRecords")
  public void setITotalRecords(Long numSamples) {
    this.iTotalRecords = numSamples;
  }

  public Long getITotalDisplayRecords() {
    return iTotalDisplayRecords;
  }

  @JsonProperty("iTotalDisplayRecords")
  public void setITotalDisplayRecords(Long iTotalDisplayRecords) {
    this.iTotalDisplayRecords = iTotalDisplayRecords;
  }

  public Long getSEcho() {
    return sEcho;
  }

  @JsonProperty("sEcho")
  public void setSEcho(Long sEcho) {
    this.sEcho = sEcho;
  }

  public List<T> getAaData() {
    return aaData;
  }

  @JsonProperty("aaData")
  public void setAaData(List<T> aaData) {
    this.aaData = aaData;
  }

}