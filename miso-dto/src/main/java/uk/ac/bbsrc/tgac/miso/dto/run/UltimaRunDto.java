package uk.ac.bbsrc.tgac.miso.dto.run;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "Ultima")
public class UltimaRunDto extends RunDto {

    private Integer completedFlows;
    private Integer expectedFlows;
    private Integer waferShelf;

    public Integer getCompletedFlows() {
        return completedFlows;
    }

    public void setCompletedFlows(Integer completedFlows) {
        this.completedFlows = completedFlows;
    }

    public Integer getExpectedFlows() {
        return expectedFlows;
    }

    public void setExpectedFlows(Integer expectedFlows) {
        this.expectedFlows = expectedFlows;
    }

    public Integer getWaferShelf() {
        return waferShelf;
    }

    public void setWaferShelf(Integer waferShelf) {
        this.waferShelf = waferShelf;
    }
}
