-- StartNoTest
SELECT kitDescriptorId INTO @goodKit FROM KitDescriptor WHERE name = 'KAPA Hyper Prep and Agilent SureSelect XT2';
SELECT kitDescriptorId INTO @badKit1 FROM KitDescriptor WHERE name = 'KAPA Hyper Prep and SureSelect XT2 v5';
SELECT kitDescriptorId INTO @badKit2 FROM KitDescriptor WHERE name = 'KAPA Hyper Prep/Agilent SureSelect XT2';

SELECT targetedSequencingId INTO @goodTarSeq FROM TargetedSequencing WHERE alias = 'Agilent SureSelect Human All Exon V5';
SELECT targetedSequencingId INTO @badTarSeq FROM TargetedSequencing WHERE alias = 'SureSelect v5';

UPDATE DetailedLibrary
SET kitDescriptorId = @goodKit
WHERE kitDescriptorId IN (@badKit1, @badKit2);

UPDATE LibraryDilution
SET targetedSequencingId = @goodTarSeq
WHERE targetedSequencingId = @badTarSeq;

INSERT INTO TargetedSequencing_KitDescriptor (kitDescriptorId, targetedSequencingId)
SELECT DISTINCT @goodKit, targetedSequencingId FROM (
  SELECT targetedSequencingId FROM TargetedSequencing_KitDescriptor
  WHERE kitDescriptorId IN (@badKit1, @badKit2)
  AND targetedSequencingId NOT IN (
    SELECT targetedSequencingId FROM TargetedSequencing_KitDescriptor
    WHERE kitDescriptorId = @goodKit
  )
) AS temp;

DELETE FROM TargetedSequencing_KitDescriptor
WHERE kitDescriptorId IN (@badKit1, @badKit2)
AND targetedSequencingId IN (
  SELECT targetedSequencingId
  FROM (
    SELECT targetedSequencingId FROM TargetedSequencing_KitDescriptor
    WHERE kitDescriptorId = @goodKit
  ) AS temp
);

INSERT INTO TargetedSequencing_KitDescriptor (kitDescriptorId, targetedSequencingId)
SELECT DISTINCT kitDescriptorId, @goodTarSeq FROM (
  SELECT kitDescriptorId FROM TargetedSequencing_KitDescriptor
  WHERE targetedSequencingId = @badTarSeq
  AND kitDescriptorId NOT IN (
    SELECT kitDescriptorId FROM TargetedSequencing_KitDescriptor
    WHERE targetedSequencingId = @goodTarSeq
  )
) AS temp;

DELETE FROM TargetedSequencing_KitDescriptor
WHERE targetedSequencingId = @badTarSeq
AND kitDescriptorId IN (
  SELECT kitDescriptorId
  FROM (
    SELECT kitDescriptorId FROM TargetedSequencing_KitDescriptor
    WHERE targetedSequencingId = @goodTarSeq
  ) AS temp
);

DELETE FROM KitDescriptorChangeLog WHERE kitDescriptorId IN (@badKit1, @badKit2);
DELETE FROM KitDescriptor WHERE kitDescriptorId IN (@badKit1, @badKit2);
DELETE FROM TargetedSequencing WHERE targetedSequencingId = @badTarSeq;
-- EndNoTest
