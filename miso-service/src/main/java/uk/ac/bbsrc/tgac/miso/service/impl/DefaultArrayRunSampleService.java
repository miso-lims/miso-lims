package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.loadChildEntity;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.updateQcDetails;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.validateQcUser;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayRunSampleService;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayRunService;
import uk.ac.bbsrc.tgac.miso.core.service.RunItemQcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.persistence.ArrayRunSampleDao;


@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultArrayRunSampleService implements ArrayRunSampleService{

    @Autowired
    private ArrayRunSampleDao arrayRunSampleDao;

    @Autowired
    private ArrayRunService arrayRunService;

    @Autowired
    private RunItemQcStatusService runItemQcStatusService;

    @Autowired
    private AuthorizationManager authorizationManager;

    @Override
    public ArrayRunSample get(ArrayRun run,String position) throws IOException {
        return arrayRunSampleDao.get(run, position);
    }

    @Override
    public List<ArrayRunSample> listByRunId(long arrayRunId) throws IOException {
        ArrayRun run = arrayRunService.get(arrayRunId);
        if( run == null || run.getArray() == null) {
            return  Collections.emptyList();
        }
        Array array = run.getArray();
        List<ArrayRunSample> existing = arrayRunSampleDao.listByRunId(arrayRunId);
        Map<String, Sample> samples = array.getSamples();
        if(samples == null || samples.isEmpty()){
            List<ArrayRunSample> filtered = existing.stream()
                    .filter(item -> item.getArray() != null && item.getArray().getId() == array.getId())
                    .collect(Collectors.toList());

            return filtered;
        }

        Map<String, ArrayRunSample> byPosition = existing.stream()
                .filter(item -> item.getArray() != null && item.getArray().getId() == array.getId())
                .collect(Collectors.toMap(ArrayRunSample::getPosition, sample -> sample));
        List<ArrayRunSample> results = new ArrayList<>(samples.size());
        for(Map.Entry<String, Sample> entry : samples.entrySet()) {
            ArrayRunSample item = byPosition.get(entry.getKey());
            if(item ==null) {
                item = new ArrayRunSample(run, entry.getKey());
            }
            item.setArray(array);
            item.setSample(entry.getValue());
            results.add(item);

        }
        results.sort(Comparator.comparing(ArrayRunSample::getPosition));
        return results;

    }

    @Override
    public void save(List<ArrayRunSample> arrayRunSamples) throws IOException {
        for (ArrayRunSample sample : arrayRunSamples) {
            save(sample);
        }
    }

    @Override
    public void save(ArrayRunSample arrayRunSample) throws IOException {
        ArrayRun run = arrayRunService.get(arrayRunSample.getArrayRun().getId());
        if(run == null){
            throw new ValidationException(new ValidationError("arrayRunId", "Invallid Array Run"));
        }
        if(run.getArray() == null) {
            throw new ValidationException(new ValidationError("arrayRunId", "Array Run has no array"));
        }
        Sample arraySample;

        try{
            arraySample = run.getArray().getSample(arrayRunSample.getPosition());
        } catch (IllegalArgumentException | IndexOutOfBoundsException e ){
            throw new ValidationException(new ValidationError("position", "Invalid array position"));
        }

        if(arraySample == null) {
            throw new ValidationException(new ValidationError("position", "No Sample at this position"));
        }

        ArrayRunSample managed = arrayRunSampleDao.get(run, arrayRunSample.getPosition());
        arrayRunSample.setArray(run.getArray());
        arrayRunSample.setSample(arraySample);
        loadChildEntity(arrayRunSample::setQcStatus, arrayRunSample.getQcStatus(), runItemQcStatusService, "qcStatusId");
        User user = authorizationManager.getCurrentUser();
        updateQcDetails(arrayRunSample, managed, ArrayRunSample::getQcStatus, ArrayRunSample::getQcUser,
                ArrayRunSample::setQcUser, authorizationManager, ArrayRunSample::getQcDate,ArrayRunSample::setQcDate);

        List<ValidationError> errors = new ArrayList<>();
        validateQcUser(arrayRunSample.getQcStatus(), arrayRunSample.getQcUser(), errors);
        if(!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        managed.setArrayRun(run);
        managed.setPosition(arrayRunSample.getPosition());
        managed.setArray(arrayRunSample.getArray());
        managed.setSample(arrayRunSample.getSample());
        managed.setQcStatus(arrayRunSample.getQcStatus());
        managed.setQcNote(arrayRunSample.getQcNote());
        managed.setQcUser(arrayRunSample.getQcUser());
        managed.setQcDate(arrayRunSample.getQcDate());
        managed.setLastModifier(user);
        arrayRunSampleDao.save(managed);

    }
}
