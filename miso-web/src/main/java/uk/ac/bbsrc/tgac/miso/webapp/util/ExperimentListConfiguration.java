package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pair;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ExperimentDto;
import uk.ac.bbsrc.tgac.miso.dto.ExperimentDto.RunPartitionDto;
import uk.ac.bbsrc.tgac.miso.dto.PartitionDto;
import uk.ac.bbsrc.tgac.miso.dto.PlatformDto;
import uk.ac.bbsrc.tgac.miso.dto.RunDto;
import uk.ac.bbsrc.tgac.miso.dto.StudyDto;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;

public class ExperimentListConfiguration {
  public static class AddRequest {
    private ExperimentDto experiment;
    private PartitionDto partition;

    public ExperimentDto getExperiment() {
      return experiment;
    }

    public PartitionDto getPartition() {
      return partition;
    }

    public void setExperiment(ExperimentDto experiment) {
      this.experiment = experiment;
    }

    public void setPartition(PartitionDto partition) {
      this.partition = partition;
    }
  }

  public static class StudiesForExperiment {
    private ExperimentDto experiment;
    private List<StudyDto> studies;

    public ExperimentDto getExperiment() {
      return experiment;
    }

    public List<StudyDto> getStudies() {
      return studies;
    }

    public void setExperiment(ExperimentDto experiment) {
      this.experiment = experiment;
    }

    public void setStudies(List<StudyDto> studies) {
      this.studies = studies;
    }
  }

  private final List<AddRequest> addToExperiment;
  private final List<StudiesForExperiment> studiesForExperiment;
  private final long runId;

  public ExperimentListConfiguration(ExperimentService experimentService, LibraryService libraryService, Platform platform, Run run) {
    runId = run.getId();
    PlatformDto platformDto = Dtos.asDto(platform);
    RunDto runDto = Dtos.asDto(run);

    // Gather all the libraries in this run and then build a map from library to all the partitions that hold it in this run
    Map<Library, List<Partition>> libraryGroups = run.getSequencerPartitionContainers().stream()//
        .flatMap(container -> container.getPartitions().stream())//
        .filter(partition -> partition.getPool() != null)//
        .flatMap(partition -> {
          return partition.getPool().getPoolableElementViews().stream()//
              .map(PoolableElementView::getLibraryId)//
              .distinct()
              .map(libraryId -> new Pair<>(libraryId, partition));
        })//
        .collect(Collectors.groupingBy(Pair::getKey))//
        .entrySet().stream()//
        .collect(Collectors.toMap(//
            WhineyFunction
                .rethrow(entry -> libraryService.get(entry.getKey())), //
            entry -> entry.getValue().stream()//
                .map(Pair::getValue)//
                .collect(Collectors.toList())));

    // Create an experiment for each library, shoving all the run/partitions pairs inside it and supply a list of possible studies for that
    // library
    studiesForExperiment = libraryGroups.entrySet().stream().map(group -> new Pair<>(group.getKey(),
        group.getValue().stream().map(Dtos::asDto).map(partitionDto -> new RunPartitionDto(runDto, partitionDto))
            .collect(Collectors.toList())))
        .map(group -> {
          StudiesForExperiment result = new StudiesForExperiment();
          result.experiment = new ExperimentDto();
          result.experiment.setLibrary(Dtos.asDto(group.getKey()));
          result.experiment.setPlatform(platformDto);
          result.experiment.setPartitions(group.getValue());

          result.studies = group.getKey().getSample().getProject().getStudies().stream().map(Dtos::asDto).collect(Collectors.toList());
          return result;
        }).collect(Collectors.toList());

    // Pull all the existing experiments for these libraries. If there are any experiments of the right type for which we have partitions
    // that could be added, produce a list.
    addToExperiment = libraryGroups.entrySet().stream()
        .<AddRequest> flatMap(WhineyFunction.rethrow(group -> //
        experimentService.listAllByLibraryId(group.getKey().getId()).stream()//
            .filter(experiment -> experiment.getPlatform().getId().equals(run.getSequencerReference().getPlatform().getId()))
            .flatMap(experiment -> group.getValue().stream()//
                .filter(partition -> experiment.getRunPartitions().stream().noneMatch(rp -> rp.getPartition().equals(partition)))
                .map(partition -> {
                  AddRequest request = new AddRequest();
                  request.experiment = Dtos.asDto(experiment);
                  request.partition = Dtos.asDto(partition);
                  return request;
                }))))
        .collect(Collectors.toList());
  }

  public List<AddRequest> getAddToExperiment() {
    return addToExperiment;
  }

  public List<StudiesForExperiment> getStudiesForExperiment() {
    return studiesForExperiment;
  }

  public long getRunId() {
    return runId;
  }

}
