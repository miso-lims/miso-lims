insert into TargetedResequencing (alias, description, kitDescriptorId, createdBy, creationDate, updatedBy, lastUpdated) VALUES
  ('BART_IAD40521_23', 'Used by the BART and BFS projects',(select kitDescriptorId from KitDescriptor where name = 'Ampliseq-Illumina V1'),1,NOW(),1,NOW()),
  ('DYS_IAD78789_185', 'Used by the DYS project',(select kitDescriptorId from KitDescriptor where name = 'AmpliSeq-KAPA Hyper Prep V1'),1,NOW(),1,NOW()),
  ('GECCO_IAD82491', 'Used by the GECCO project',(select kitDescriptorId from KitDescriptor where name = 'AmpliSeq-KAPA Hyper Prep V1'),1,NOW(),1,NOW());
