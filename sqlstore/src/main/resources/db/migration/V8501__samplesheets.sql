CREATE TABLE SampleSheet (
    sampleSheetId BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    platformType VARCHAR(50) NOT NULL,
    parameters JSON,
    sections JSON,
    CONSTRAINT uk_samplesheet_name UNIQUE KEY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO SampleSheet(sampleSheetId, name, platformType, parameters, sections) VALUES
(1, 'Clone Checking', 'ILLUMINA', '[
  {
    "name": "Genome Folder",
    "type": "TEXT",
    "defaultValue": "Homo_sapiens\\\\UCSC\\\\hg19\\\\Sequence\\\\WholeGenomeFasta"
  },
  {
    "name": "Custom Read 1 Primer Well",
    "type": "TEXT"
  },
  {
    "name": "Custom Index Primer Well",
    "type": "TEXT"
  },
  {
    "name": "Custom Read 2 Primer Well",
    "type": "TEXT"
  }
]', '[
  {
    "name": "Header",
    "format": "ROWS",
    "fields": [
      {
        "name": "Index Adapters",
        "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_FAMILY",
            "separator": "/"
        }]
      },
      {
        "name": "IEMFileVersion",
        "sources": [{
          "value": "5"
        }]
      },
      {
        "name": "Experiment Name",
        "sources": [{
          "source": "POOL",
          "sourceProperty": "ALIAS",
          "separator": "/"
        }]
      },
      {
        "name": "Date",
        "sources": [{
          "source": "CURRENT_TIME",
          "dateFormat": "M/d/yyyy"
        }]
      },
      {
        "name": "Instrument Type",
        "sources": [{
          "source": "INSTRUMENT_MODEL"
        }]
      },
      {
        "name": "Chemistry",
        "sources": [{
          "value": "Amplicon"
        }]
      },
      {
        "name": "Workflow",
        "sources": [{
          "value": "GenerateFASTQ"
        }]
      },
      {
        "name": "Application",
        "sources": [{
          "value": "Clone Checking"
        }]
      },
      {
        "name": "Assay",
        "sources": [{
          "value": "Nextera XT"
        }]
      }
    ]
  },
  {
    "name": "Reads",
    "format": "ROWS",
    "omitFieldNames": true,
    "fields": [
      {
        "name": "Read 1 Length",
        "sources": [{
          "source": "SEQUENCING_PARAMETERS",
          "sourceProperty": "READ_1_LENGTH"
        }]
      },
      {
        "name": "Read 2 Length",
        "sources": [{
          "source": "SEQUENCING_PARAMETERS",
          "sourceProperty": "READ_2_LENGTH"
        }],
        "omitIfEmpty": true
      }
    ]
  },
  {
    "name": "Settings",
    "format": "ROWS",
    "fields": [
      {
        "name": "Adapter",
        "sources": [{
          "value": "CTGTCTCTTATACACATCT"
        }]
      },
      {
        "name": "CustomRead1PrimerMix",
        "sources": [{
          "source": "Custom Read 1 Primer Well"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "CustomIndexPrimerMix",
        "sources": [{
          "source": "Custom Index Primer Well"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "CustomRead2PrimerMix",
        "sources": [{
          "source": "Custom Read 2 Primer Well"
        }],
        "omitIfEmpty": true
      }
    ]
  },
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "Sample_ID",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "ALIAS"
        }]
      },
      {
        "name": "Sample_Plate",
        "sources": []
      },
      {
        "name": "Sample_Well",
          "sources": []
      },
      {
        "name": "I7_Index_ID",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_NAME"
        }]
      },
      {
        "name": "index",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
        }]
      },
      {
        "name": "I5_Index_ID",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_NAME"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "index2",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "INDEX_2_SEQUENCE"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "GenomeFolder",
        "sources": [{
          "source": "Genome Folder"
        }]
      },
      {
        "name": "Sample_Project",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "PROJECT_CODE"
        }]
      },
      {
        "name": "Description",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "IDENTIFICATION_BARCODE"
        }]
      }
    ]
  }
]'),
(2, 'Library QC', 'ILLUMINA', '[
  {
    "name": "Genome Folder",
    "type": "TEXT",
    "defaultValue": "Homo_sapiens\\\\UCSC\\\\hg19\\\\Sequence\\\\WholeGenomeFasta"
  },
  {
    "name": "Custom Read 1 Primer Well",
    "type": "TEXT"
  },
  {
    "name": "Custom Index Primer Well",
    "type": "TEXT"
  },
  {
    "name": "Custom Read 2 Primer Well",
    "type": "TEXT"
  }
]', '[
  {
    "name": "Header",
    "format": "ROWS",
    "fields": [
      {
        "name": "Index Adapters",
        "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_FAMILY",
            "separator": "/"
        }]
      },
      {
        "name": "IEMFileVersion",
        "sources": [{
          "value": "5"
        }]
      },
      {
        "name": "Experiment Name",
        "sources": [{
          "source": "POOL",
          "sourceProperty": "ALIAS",
          "separator": "/"
        }]
      },
      {
        "name": "Date",
        "sources": [{
          "source": "CURRENT_TIME",
          "dateFormat": "M/d/yyyy"
        }]
      },
      {
        "name": "Instrument Type",
        "sources": [{
          "source": "INSTRUMENT_MODEL"
        }]
      },
      {
        "name": "Chemistry",
        "sources": [{
          "value": "Amplicon"
        }]
      },
      {
        "name": "Workflow",
        "sources": [{
          "value": "LibraryQC"
        }]
      },
      {
        "name": "Application",
        "sources": [{
          "value": "Library QC"
        }]
      },
      {
        "name": "Assay",
        "sources": [{
          "value": "Nextera DNA"
        }]
      }
    ]
  },
  {
    "name": "Reads",
    "format": "ROWS",
    "omitFieldNames": true,
    "fields": [
      {
        "name": "Read 1 Length",
        "sources": [{
          "source": "SEQUENCING_PARAMETERS",
          "sourceProperty": "READ_1_LENGTH"
        }]
      },
      {
        "name": "Read 2 Length",
        "sources": [{
          "source": "SEQUENCING_PARAMETERS",
          "sourceProperty": "READ_2_LENGTH"
        }],
        "omitIfEmpty": true
      }
    ]
  },
  {
    "name": "Settings",
    "format": "ROWS",
    "fields": [
      {
        "name": "FlagPCRDuplicates",
        "sources": [{
          "value": "1"
        }]
      },
      {
        "name": "ReverseComplement",
        "sources": [{
          "value": "0"
        }]
      },
      {
        "name": "RunBwaAln",
        "sources": [{
          "value": "0"
        }]
      },
      {
        "name": "Adapter",
        "sources": [{
          "value": "CTGTCTCTTATACACATCT"
        }]
      },
      {
        "name": "CustomRead1PrimerMix",
        "sources": [{
          "source": "Custom Read 1 Primer Well"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "CustomIndexPrimerMix",
        "sources": [{
          "source": "Custom Index Primer Well"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "CustomRead2PrimerMix",
        "sources": [{
          "source": "Custom Read 2 Primer Well"
        }],
        "omitIfEmpty": true
      }
    ]
  },
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "Sample_ID",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "ALIAS"
        }]
      },
      {
        "name": "Sample_Plate",
        "sources": []
      },
      {
        "name": "Sample_Well",
          "sources": []
      },
      {
        "name": "I7_Index_ID",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_NAME"
        }]
      },
      {
        "name": "index",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
        }]
      },
      {
        "name": "I5_Index_ID",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_NAME"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "index2",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "INDEX_2_SEQUENCE"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "GenomeFolder",
        "sources": [{
          "source": "Genome Folder"
        }]
      },
      {
        "name": "Sample_Project",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "PROJECT_CODE"
        }]
      },
      {
        "name": "Description",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "IDENTIFICATION_BARCODE"
        }]
      }
    ]
  }
]'),
(3, 'Metagenomics 16S rRNA', 'ILLUMINA', '[
  {
    "name": "Genome Folder",
    "type": "TEXT",
    "defaultValue": "Homo_sapiens\\\\UCSC\\\\hg19\\\\Sequence\\\\WholeGenomeFasta"
  },
  {
    "name": "Custom Read 1 Primer Well",
    "type": "TEXT"
  },
  {
    "name": "Custom Index Primer Well",
    "type": "TEXT"
  },
  {
    "name": "Custom Read 2 Primer Well",
    "type": "TEXT"
  }
]', '[
  {
    "name": "Header",
    "format": "ROWS",
    "fields": [
      {
        "name": "Index Adapters",
        "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_FAMILY",
            "separator": "/"
        }]
      },
      {
        "name": "IEMFileVersion",
        "sources": [{
          "value": "5"
        }]
      },
      {
        "name": "Experiment Name",
        "sources": [{
          "source": "POOL",
          "sourceProperty": "ALIAS",
          "separator": "/"
        }]
      },
      {
        "name": "Date",
        "sources": [{
          "source": "CURRENT_TIME",
          "dateFormat": "M/d/yyyy"
        }]
      },
      {
        "name": "Instrument Type",
        "sources": [{
          "source": "INSTRUMENT_MODEL"
        }]
      },
      {
        "name": "Chemistry",
        "sources": [{
          "value": "Amplicon"
        }]
      },
      {
        "name": "Workflow",
        "sources": [{
          "value": "Metagenomics"
        }]
      },
      {
        "name": "Application",
        "sources": [{
          "value": "Metagenomics 16S rRNA"
        }]
      },
      {
        "name": "Assay",
        "sources": [{
          "value": "TruSeq DNA PCR-Free"
        }]
      }
    ]
  },
  {
    "name": "Reads",
    "format": "ROWS",
    "omitFieldNames": true,
    "fields": [
      {
        "name": "Read 1 Length",
        "sources": [{
          "source": "SEQUENCING_PARAMETERS",
          "sourceProperty": "READ_1_LENGTH"
        }]
      },
      {
        "name": "Read 2 Length",
        "sources": [{
          "source": "SEQUENCING_PARAMETERS",
          "sourceProperty": "READ_2_LENGTH"
        }],
        "omitIfEmpty": true
      }
    ]
  },
  {
    "name": "Settings",
    "format": "ROWS",
    "fields": [
      {
        "name": "Adapter",
        "sources": [{
          "value": "AGATCGGAAGAGCACACGTCTGAACTCCAGTCA"
        }]
      },
      {
        "name": "AdapterRead2",
        "sources": [{
          "value": "AGATCGGAAGAGCGTCGTGTAGGGAAAGAGTGT"
        }]
      },
      {
        "name": "CustomRead1PrimerMix",
        "sources": [{
          "source": "Custom Read 1 Primer Well"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "CustomIndexPrimerMix",
        "sources": [{
          "source": "Custom Index Primer Well"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "CustomRead2PrimerMix",
        "sources": [{
          "source": "Custom Read 2 Primer Well"
        }],
        "omitIfEmpty": true
      }
    ]
  },
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "Sample_ID",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "ALIAS"
        }]
      },
      {
        "name": "Sample_Plate",
        "sources": []
      },
      {
        "name": "Sample_Well",
          "sources": []
      },
      {
        "name": "I7_Index_ID",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_NAME"
        }]
      },
      {
        "name": "index",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
        }]
      },
      {
        "name": "I5_Index_ID",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_NAME"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "index2",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "INDEX_2_SEQUENCE"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "GenomeFolder",
        "sources": [{
          "source": "Genome Folder"
        }]
      },
      {
        "name": "Sample_Project",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "PROJECT_CODE"
        }]
      },
      {
        "name": "Description",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "IDENTIFICATION_BARCODE"
        }]
      }
    ]
  }
]'),
(4, 'FASTQ Only (Nextera XT)', 'ILLUMINA', '[
  {
    "name": "Genome Folder",
    "type": "TEXT",
    "defaultValue": "Homo_sapiens\\\\UCSC\\\\hg19\\\\Sequence\\\\WholeGenomeFasta"
  },
  {
    "name": "Custom Read 1 Primer Well",
    "type": "TEXT"
  },
  {
    "name": "Custom Index Primer Well",
    "type": "TEXT"
  },
  {
    "name": "Custom Read 2 Primer Well",
    "type": "TEXT"
  }
]', '[
  {
    "name": "Header",
    "format": "ROWS",
    "fields": [
      {
        "name": "Index Adapters",
        "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_FAMILY",
            "separator": "/"
        }]
      },
      {
        "name": "IEMFileVersion",
        "sources": [{
          "value": "5"
        }]
      },
      {
        "name": "Experiment Name",
        "sources": [{
          "source": "POOL",
          "sourceProperty": "ALIAS",
          "separator": "/"
        }]
      },
      {
        "name": "Date",
        "sources": [{
          "source": "CURRENT_TIME",
          "dateFormat": "M/d/yyyy"
        }]
      },
      {
        "name": "Instrument Type",
        "sources": [{
          "source": "INSTRUMENT_MODEL"
        }]
      },
      {
        "name": "Chemistry",
        "sources": [{
          "value": "Amplicon"
        }]
      },
      {
        "name": "Workflow",
        "sources": [{
          "value": "GenerateFASTQ"
        }]
      },
      {
        "name": "Application",
        "sources": [{
          "value": "FASTQ Only"
        }]
      },
      {
        "name": "Assay",
        "sources": [{
          "value": "Nextera XT"
        }]
      }
    ]
  },
  {
    "name": "Reads",
    "format": "ROWS",
    "omitFieldNames": true,
    "fields": [
      {
        "name": "Read 1 Length",
        "sources": [{
          "source": "SEQUENCING_PARAMETERS",
          "sourceProperty": "READ_1_LENGTH"
        }]
      },
      {
        "name": "Read 2 Length",
        "sources": [{
          "source": "SEQUENCING_PARAMETERS",
          "sourceProperty": "READ_2_LENGTH"
        }],
        "omitIfEmpty": true
      }
    ]
  },
  {
    "name": "Settings",
    "format": "ROWS",
    "fields": [
      {
        "name": "ReverseComplement",
        "sources": [{
          "value": "0"
        }]
      },
      {
        "name": "Adapter",
        "sources": [{
          "value": "CTGTCTCTTATACACATCT"
        }]
      },
      {
        "name": "CustomRead1PrimerMix",
        "sources": [{
          "source": "Custom Read 1 Primer Well"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "CustomIndexPrimerMix",
        "sources": [{
          "source": "Custom Index Primer Well"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "CustomRead2PrimerMix",
        "sources": [{
          "source": "Custom Read 2 Primer Well"
        }],
        "omitIfEmpty": true
      }
    ]
  },
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "Sample_ID",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "ALIAS"
        }]
      },
      {
        "name": "Sample_Plate",
        "sources": []
      },
      {
        "name": "Sample_Well",
          "sources": []
      },
      {
        "name": "I7_Index_ID",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_NAME"
        }]
      },
      {
        "name": "index",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
        }]
      },
      {
        "name": "I5_Index_ID",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_NAME"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "index2",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "INDEX_2_SEQUENCE"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "GenomeFolder",
        "sources": [{
          "source": "Genome Folder"
        }]
      },
      {
        "name": "Sample_Project",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "PROJECT_CODE"
        }]
      },
      {
        "name": "Description",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "IDENTIFICATION_BARCODE"
        }]
      }
    ]
  }
]'),
(5, 'FASTQ Only (TruSeq Nano DNA)', 'ILLUMINA', '[
  {
    "name": "Genome Folder",
    "type": "TEXT",
    "defaultValue": "Homo_sapiens\\\\UCSC\\\\hg19\\\\Sequence\\\\WholeGenomeFasta"
  },
  {
    "name": "Custom Read 1 Primer Well",
    "type": "TEXT"
  },
  {
    "name": "Custom Index Primer Well",
    "type": "TEXT"
  },
  {
    "name": "Custom Read 2 Primer Well",
    "type": "TEXT"
  }
]', '[
  {
    "name": "Header",
    "format": "ROWS",
    "fields": [
      {
        "name": "Index Adapters",
        "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_FAMILY",
            "separator": "/"
        }]
      },
      {
        "name": "IEMFileVersion",
        "sources": [{
          "value": "5"
        }]
      },
      {
        "name": "Experiment Name",
        "sources": [{
          "source": "POOL",
          "sourceProperty": "ALIAS",
          "separator": "/"
        }]
      },
      {
        "name": "Date",
        "sources": [{
          "source": "CURRENT_TIME",
          "dateFormat": "M/d/yyyy"
        }]
      },
      {
        "name": "Instrument Type",
        "sources": [{
          "source": "INSTRUMENT_MODEL"
        }]
      },
      {
        "name": "Chemistry",
        "sources": [{
          "value": "Amplicon"
        }]
      },
      {
        "name": "Workflow",
        "sources": [{
          "value": "GenerateFASTQ"
        }]
      },
      {
        "name": "Application",
        "sources": [{
          "value": "FASTQ Only"
        }]
      },
      {
        "name": "Assay",
        "sources": [{
          "value": "TruSeq Nano DNA"
        }]
      }
    ]
  },
  {
    "name": "Reads",
    "format": "ROWS",
    "omitFieldNames": true,
    "fields": [
      {
        "name": "Read 1 Length",
        "sources": [{
          "source": "SEQUENCING_PARAMETERS",
          "sourceProperty": "READ_1_LENGTH"
        }]
      },
      {
        "name": "Read 2 Length",
        "sources": [{
          "source": "SEQUENCING_PARAMETERS",
          "sourceProperty": "READ_2_LENGTH"
        }],
        "omitIfEmpty": true
      }
    ]
  },
  {
    "name": "Settings",
    "format": "ROWS",
    "fields": [
      {
        "name": "ReverseComplement",
        "sources": [{
          "value": "0"
        }]
      },
      {
        "name": "Adapter",
        "sources": [{
          "value": "AGATCGGAAGAGCACACGTCTGAACTCCAGTCA"
        }]
      },
      {
        "name": "AdapterRead2",
        "sources": [{
          "value": "AGATCGGAAGAGCGTCGTGTAGGGAAAGAGTGT"
        }]
      },
      {
        "name": "CustomRead1PrimerMix",
        "sources": [{
          "source": "Custom Read 1 Primer Well"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "CustomIndexPrimerMix",
        "sources": [{
          "source": "Custom Index Primer Well"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "CustomRead2PrimerMix",
        "sources": [{
          "source": "Custom Read 2 Primer Well"
        }],
        "omitIfEmpty": true
      }
    ]
  },
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "Sample_ID",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "ALIAS"
        }]
      },
      {
        "name": "Sample_Plate",
        "sources": []
      },
      {
        "name": "Sample_Well",
          "sources": []
      },
      {
        "name": "I7_Index_ID",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_NAME"
        }]
      },
      {
        "name": "index",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
        }]
      },
      {
        "name": "I5_Index_ID",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_NAME"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "index2",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "INDEX_2_SEQUENCE"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "GenomeFolder",
        "sources": [{
          "source": "Genome Folder"
        }]
      },
      {
        "name": "Sample_Project",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "PROJECT_CODE"
        }]
      },
      {
        "name": "Description",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "IDENTIFICATION_BARCODE"
        }]
      }
    ]
  }
]'),
(6, 'BCL Convert', 'ILLUMINA', '[
  {
    "name": "DRAGEN Version",
    "type": "DECIMAL"
  },
  {
    "name": "FastQ Compression Format",
    "type": "DROPDOWN",
    "source": [
      {
        "value": "gzip"
      },
      {
        "value": "DRAGEN ORA"
      }
    ] 
  },
  {
    "name": "Trim UMI?",
    "type": "DROPDOWN",
    "source": [
      {
        "value": "true"
      },
      {
        "vaue": "false"
      }
    ]
  }
]', '[
  {
    "name": "Header",
    "format": "ROWS",
    "fields": [
      {
        "name": "Index Adapters",
        "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_FAMILY",
            "separator": "/"
        }]
      },
      {
        "name": "FileFormatVersion",
        "sources": [{
          "value": "2"
        }]
      },
      {
        "name": "RunName",
        "sources": [{
          "source": "CURRENT_TIME",
          "dateFormat": "yyyyMMddHHmmss"
        }]
      },
      {
        "name": "InstrumentPlatform",
        "sources": [{
          "source": "INSTRUMENT_MODEL"
        }]
      },
      {
        "name": "Index Orientation",
        "sources": [{
          "value": "Forward"
        }]
      }
    ]
  },
  {
    "name": "Reads",
    "format": "ROWS",
    "fields": [
      {
        "name": "Read1Cycles",
        "sources": [{
          "source": "SEQUENCING_PARAMETERS",
          "sourceProperty": "READ_1_LENGTH"
        }]
      },
      {
        "name": "Read2Cycles",
        "sources": [{
          "source": "SEQUENCING_PARAMETERS",
          "sourceProperty": "READ_2_LENGTH"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "Index1Cycles",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "INDEX_1_SEQUENCE",
          "aggregation": "MAX_LENGTH"
        }]
      },
      {
        "name": "Index2Cycles",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "INDEX_2_SEQUENCE",
          "aggregation": "MAX_LENGTH"
        }],
        "omitIfEmpty": true
      }
    ]
  },
  {
    "name": "BCLConvert_Settings",
    "format": "ROWS",
    "fields": [
      {
        "name": "FastqCompressionFormat",
        "sources": [{
          "source": "FastQ Compression Format"
        }]
      },
      {
        "name": "TrimUMI",
        "sources": [{
          "source": "Trim UMI?"
        }]
      },
      {
        "name": "SoftwareVersion",
        "sources": [{
          "source": "DRAGEN Version"
        }]
      },
      {
        "name": "OverrideCycles",
        "sources": [{
          "value": "Y"
        }, {
          "source": "SEQUENCING_PARAMETERS",
          "sourceProperty": "READ_1_LENGTH"
        }, {
          "value": ";I"
        }, {
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "INDEX_1_SEQUENCE",
          "aggregation": "MAX_LENGTH"
        }, {
          "value": ";I"
        }, {
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "INDEX_2_SEQUENCE",
          "aggregation": "MAX_LENGTH"
        }, {
          "value": ";Y"
        }, {
          "source": "SEQUENCING_PARAMETERS",
          "sourceProperty": "READ_2_LENGTH"
        }
      ]
      }
    ]
  },
  {
    "name": "BCLConvert_Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "Lane",
        "sources": [{
          "source": "PARTITION"
        }]
      },
      {
        "name": "Sample_ID",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "ALIAS"
        }]
      },
      {
        "name": "Index",
          "sources": [{
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
        }]
      },
      {
        "name": "Index2",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "INDEX_2_SEQUENCE"
        }],
        "omitIfEmpty": true
      }
    ]
  },
  {
    "name": "Cloud Settings",
    "format": "ROWS",
    "fields": [
      {
        "name": "GeneratedVersion",
        "sources": [{
          "value": "1.16.0.202410292136"
        }]
      }
    ]
  },
  {
    "name": "Cloud Data",
    "format": "COLUMNS",
    "multivalue": "DISTINCT_LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "Sample_ID",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "ALIAS"
        }]
      },
      {
        "name": "ProjectName",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "PROJECT_CODE"
        }]
      },
      {
        "name": "LibraryName",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "NAME"
        }]
      }
    ]
  }
]');
