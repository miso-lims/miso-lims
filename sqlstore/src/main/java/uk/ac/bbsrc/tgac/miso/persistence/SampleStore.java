package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.IdentityView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface SampleStore extends PaginatedDataSource<Sample> {

    Sample get(long id) throws IOException;

    /**
     * Retrieve a Sample from an underlying data store given a Sample ID barcode
     * <p/>
     * This method intends to retrieve objects in an 'ignorant' fashion, i.e.
     * 
     * @param barcode of type String
     * @return Sample
     * @throws IOException when
     */
    Sample getByBarcode(String barcode) throws IOException;

    List<EntityReference> listByAlias(String alias) throws IOException;

    /**
     * List all Samples associated with identificationBarcodes from the given identificationBarcode list
     * 
     * @return Collection<Sample>
     * @throws IOException when the objects cannot be retrieved
     */
    Collection<Sample> listByBarcodeList(Collection<String> barcodeList) throws IOException;

    /**
     * List all Samples associated with ids from the given id list
     * 
     * @return Collection<Sample>
     * @throws IOException when the objects cannot be retrieved
     */
    List<Sample> listByIdList(Collection<Long> idList) throws IOException;

    Sample getByLibraryAliquotId(long aliquotId) throws IOException;

    List<Sample> list() throws IOException;

    Sample getSample(long id) throws IOException;

    Long create(Sample sample) throws IOException;

    long update(Sample sample) throws IOException;

    /**
     * List all the identities associated with a given project which have at least one external name
     * which exactly or partially matches the input String or a comma-separated portion of the input
     * String.
     * 
     * @param externalName a single external name String
     * @param projectId Long
     * @param exactMatch boolean
     * @return List<Sample> set of Identities belonging to a given project which have an external name
     *         that matches the input string
     * @throws IOException
     */
    List<IdentityView> getIdentitiesByExternalNameOrAliasAndProject(String externalName, Long projectId,
            boolean exactMatch)
            throws IOException;

    /**
     * List all the identities matching any of the provided external names
     * 
     * @param externalNames the external names to search for
     * @param exactMatch if true, only identities exactly matching one of the provided external names
     *        will be included; otherwise, all identities with external names containing one of the
     *        provided external names will be included
     * @param project if not null, only identities belonging to this project will be included
     * @return all matching identities
     * @throws IOException
     */
    List<IdentityView> getIdentities(Collection<String> externalNames, boolean exactMatch, Project project)
            throws IOException;

    /**
     * Find a ghost Tissue with Identity, Tissue Origin, Tissue Type, times received, tube number, and
     * passage number matching the provided Tissue
     * 
     * @param tissue partially-formed tissue, minimally containing all of the above-noted attributes.
     *        Tissue Origin, Tissue Type, and parent (Identity) must have their IDs set. Passage number
     *        may be null
     * @return the matching ghost tissue, if one exists; null otherwise
     * @throws IOException
     */
    SampleTissue getMatchingGhostTissue(SampleTissue tissue) throws IOException;

    long getChildSampleCount(Sample sample);

    /**
     * @param sample the "current" sample
     * @return a reference to the sample in the same project as the "current" sample with the next ID in
     *         numerical order
     */
    EntityReference getNextInProject(Sample sample);

    /**
     * @param sample the "current" sample
     * @return a reference to the sample in the same project as the "current" sample with the previous
     *         ID in numerical order
     */
    EntityReference getPreviousInProject(Sample sample);

    /**
     * Get all descendants of the specified samples that have a particular sample category and are
     * associated with a particular requisition
     * 
     * @param parentIds ids of the samples from which to find descendants
     * @param targetSampleCategory sample category of descendants to return
     * @param effectiveRequisitionId id of the requisition with which the samples must be associated
     * @return all of the matching descendants
     * @throws IOException
     */
    List<Sample> getChildren(Collection<Long> parentIds, String targetSampleCategory, long effectiveRequisitionId)
            throws IOException;

    /**
     * Get the sample IDs of all descendants of the specified samples that have a particular sample
     * category
     *
     * @param parentIds ids of the samples from which to find descendants
     * @param targetSampleCategory sample category of descendants to return
     * @param effectiveRequisitionId id of the requisition for which these are being retrieved; samples
     *        associated with a different requisition will be excluded
     * @return all of the matching descendants' sample IDs
     * @throws IOException
     */
    Set<Long> getChildIds(Collection<Long> parentIds, String targetSampleCategory, Long effectiveRequisitionId)
            throws IOException;

}
