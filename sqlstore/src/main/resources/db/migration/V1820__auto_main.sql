-- run_sample_sheets
INSERT INTO SampleSheet(name, platformType, parameters, sections) VALUES
('BCL2FASTQ', 'ILLUMINA', '[]', '[
  {
    "name": "Header",
    "format": "ROWS",
    "fields": [
      {
        "name": "Date",
        "sources": [{
          "source": "CURRENT_TIME",
          "dateFormat": "yyyy-MM-dd"
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
        "name": "Sample_Name",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "NAME"
        }]
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
      }
    ]
  }
]'),
('Cell Ranger', 'ILLUMINA', '[]', '[
  {
    "name": "Data",
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
          "sourceProperty": "NAME"
        }]
      },
      {
        "name": "Sample_Name",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "ALIAS"
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
        "name": "index2",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "INDEX_2_SEQUENCE"
        }],
        "omitIfEmpty": true
      },
      {
        "name": "Sample_Project",
        "sources": [{
          "source": "LIBRARY_ALIQUOT",
          "sourceProperty": "PROJECT_CODE"
        }]
      }
    ]
  }
]');

