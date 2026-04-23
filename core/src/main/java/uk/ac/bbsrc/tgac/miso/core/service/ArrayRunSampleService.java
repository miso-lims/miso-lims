package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;

public interface ArrayRunSampleService {

    ArrayRunSample get(ArrayRun run, Array array, String position, Sample sample) throws IOException;

    List<ArrayRunSample> listByRunId(long arrayRunId) throws IOException;

    void delete(ArrayRunSample arrayRunSample) throws IOException;

    void deleteByRunId(long arrayRunId) throws IOException;

    void save(ArrayRunSample arrayRunSample) throws IOException;

    void save(List<ArrayRunSample> arrayRunSamples) throws IOException;

    List<ArrayRunSample> listByArrayId(long arrayId) throws IOException;
}
