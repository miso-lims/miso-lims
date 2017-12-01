-- complentions_index

CREATE INDEX StatusCompletion ON Status (statusId, health, lastUpdated);


-- extra_indexes

CREATE INDEX BoxChangeLogDerivedInfo ON BoxChangeLog(boxId, changeTime);
CREATE INDEX ExperimentChangeLogDerivedInfo ON ExperimentChangeLog(experimentId, changeTime);
CREATE INDEX KitDescriptorChangeLogDerivedInfo ON KitDescriptorChangeLog(kitDescriptorId, changeTime);
CREATE INDEX LibraryChangeLogDerivedInfo ON LibraryChangeLog(libraryId, changeTime);
CREATE INDEX PoolChangeLogDerivedInfo ON PoolChangeLog(poolId, changeTime);
CREATE INDEX RunChangeLogDerivedInfo ON RunChangeLog(runId, changeTime);
CREATE INDEX SampleChangeLogDerivedInfo ON SampleChangeLog(sampleId, changeTime);
CREATE INDEX SequencerPartitionContainerChangeLogDerivedInfo ON SequencerPartitionContainerChangeLog(containerId, changeTime);
CREATE INDEX StudyChangeLogDerivedInfo ON StudyChangeLog(studyId, changeTime);
CREATE INDEX Group_name ON _Group(name);


