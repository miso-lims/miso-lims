-- GLT-4829: PMGC sample sheet formats
-- Type, Requisition Description, FCID, barcode_ids and slide_matrix_id have no
-- data source in MISO yet -- included as blank placeholder columns (sources: [])

INSERT INTO SampleSheet(name, platformType, parameters, sections) VALUES
('Visium', 'ILLUMINA', '[]', '[
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "FCID",
        "sources": []
      },
      {
        "name": "Lane",
        "sources": [
          {
            "source": "PARTITION"
          }
        ]
      },
      {
        "name": "Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "NAME"
          }
        ]
      },
      {
        "name": "Alias",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "ALIAS"
          }
        ]
      },
      {
        "name": "Project",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_CODE"
          }
        ]
      },
      {
        "name": "ProjectName",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_NAME"
          }
        ]
      },
      {
        "name": "Requisition ID",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ID" 
          }
        ]
      },
      {
        "name": "Requisition Alias",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ALIAS" 
          }
        ]
      },
      {
        "name": "Assay",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ASSAY" 
          }
        ]
      },
      {
        "name": "Tissue Origin",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_ORIGIN"
          }
        ]
      },
      {
        "name": "Tissue Type",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_TYPE"
          }
        ]
      },
      {
        "name": "Description",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "LIBRARY_DESCRIPTION"
          }
        ]
      },
      {
        "name": "Sample Type",
        "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SAMPLE_TYPE" 
          }
        ]
      },
      {
        "name": "Sci. Name",
        "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SCI_NAME" 
          }
        ]
      },
      {
        "name": "Code",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "DESIGN_CODE"
          }
        ]
      },
      {
        "name": "Platform",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PLATFORM"
          }
        ]
      },
      {
        "name": "Type",
        "sources": []
      },
      {
        "name": "Selection",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "SELECTION"
          }
        ]
      },
      {
        "name": "Strategy",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "STRATEGY"
          }
        ]
      },
      {
        "name": "Index Kit",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_FAMILY"
          }
        ]
      },
      {
        "name": "I7_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_NAME"
          }
        ]
      },
      {
        "name": "index",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
          }
        ]
      },
      {
        "name": "I5_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_NAME"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "index2",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_SEQUENCE"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "Has UMIs",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "HAS_UMIS"
          }
        ]
      },
      {
        "name": "Creation Date",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "CREATED_DATE"
          }
        ]
      },
      {
        "name": "Requisition Description",
        "sources": []
      },
      {
        "name": "Requisition Contacts",
        "sources": [
          {
            "source": "REQUISITION",
            "sourceProperty": "CONTACTS"
          }
        ]
      },
      {
        "name": "Targeted Sequencing",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TARGETED_SEQUENCING"
          }
        ]
      },
      {
        "name": "External Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "EXTERNAL_NAME"
          }
        ]
      }
    ]
  }
]'),
('GEX-OCM-Flex-3pr5pr-Multiome', 'ILLUMINA', '[]', '[
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "FCID",
        "sources": []
      },
      {
        "name": "Lane",
        "sources": [
          {
            "source": "PARTITION"
          }
        ]
      },
      {
        "name": "Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "NAME"
          }
        ]
      },
      {
        "name": "Alias",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "ALIAS"
          }
        ]
      },
      {
        "name": "Project",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_CODE"
          }
        ]
      },
      {
        "name": "ProjectName",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_NAME"
          }
        ]
      },
      {
        "name": "Requisition ID",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ID" 
          }
        ]
      },
      {
        "name": "Requisition Alias",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ALIAS" 
          }
        ]
      },
      {
        "name": "Assay",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ASSAY" 
          }
        ]
      },
      {
        "name": "Tissue Origin",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_ORIGIN"
          }
        ]
      },
      {
        "name": "Tissue Type",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_TYPE"
          }
        ]
      },
      {
        "name": "Description",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "LIBRARY_DESCRIPTION"
          }
        ]
      },
      {
        "name": "Sample Type",
         "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SAMPLE_TYPE" 
          }
        ]
      },
      {
        "name": "Sci. Name",
        "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SCI_NAME" 
          }
        ]
      },
      {
        "name": "Code",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "DESIGN_CODE"
          }
        ]
      },
      {
        "name": "Platform",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PLATFORM"
          }
        ]
      },
      {
        "name": "Type",
        "sources": []
      },
      {
        "name": "Selection",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "SELECTION"
          }
        ]
      },
      {
        "name": "Strategy",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "STRATEGY"
          }
        ]
      },
      {
        "name": "Index Kit",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_FAMILY"
          }
        ]
      },
      {
        "name": "I7_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_NAME"
          }
        ]
      },
      {
        "name": "index",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
          }
        ]
      },
      {
        "name": "I5_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_NAME"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "index2",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_SEQUENCE"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "Has UMIs",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "HAS_UMIS"
          }
        ]
      },
      {
        "name": "Creation Date",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "CREATED_DATE"
          }
        ]
      },
      {
        "name": "Requisition Description",
        "sources": []
      },
      {
        "name": "Requisition Contacts",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "CONTACTS" 
          }
        ]
      },
      {
        "name": "Targeted Sequencing",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TARGETED_SEQUENCING"
          }
        ]
      },
      {
        "name": "External Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "EXTERNAL_NAME"
          }
        ]
      }
    ]
  }
]'),
('ATAC-SeqOnly', 'ILLUMINA', '[]', '[
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "FCID",
        "sources": []
      },
      {
        "name": "Lane",
        "sources": [
          {
            "source": "PARTITION"
          }
        ]
      },
      {
        "name": "Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "NAME"
          }
        ]
      },
      {
        "name": "Alias",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "ALIAS"
          }
        ]
      },
      {
        "name": "Project",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_CODE"
          }
        ]
      },
      {
        "name": "ProjectName",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_NAME"
          }
        ]
      },
      {
        "name": "Requisition ID",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ID" 
          }
        ]
      },
      {
        "name": "Requisition Alias",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ALIAS" 
          }
        ]
      },
      {
        "name": "Assay",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ASSAY" 
          }
        ]
      },
      {
        "name": "Tissue Origin",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_ORIGIN"
          }
        ]
      },
      {
        "name": "Tissue Type",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_TYPE"
          }
        ]
      },
      {
        "name": "Description",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "LIBRARY_DESCRIPTION"
          }
        ]
      },
      {
        "name": "Sample Type",
         "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SAMPLE_TYPE" 
          }
        ]
      },
      {
        "name": "Sci. Name",
        "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SCI_NAME" 
          }
        ]
      },
      {
        "name": "Code",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "DESIGN_CODE"
          }
        ]
      },
      {
        "name": "Platform",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PLATFORM"
          }
        ]
      },
      {
        "name": "Type",
        "sources": []
      },
      {
        "name": "Selection",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "SELECTION"
          }
        ]
      },
      {
        "name": "Strategy",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "STRATEGY"
          }
        ]
      },
      {
        "name": "Index Kit",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_FAMILY"
          }
        ]
      },
      {
        "name": "I7_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_NAME"
          }
        ]
      },
      {
        "name": "index",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
          }
        ]
      },
      {
        "name": "I5_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_NAME"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "index2",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_SEQUENCE"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "Has UMIs",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "HAS_UMIS"
          }
        ]
      },
      {
        "name": "Creation Date",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "CREATED_DATE"
          }
        ]
      },
      {
        "name": "Requisition Description",
        "sources": []
      },
      {
        "name": "Requisition Contacts",
        "sources": [
          {
            "source": "REQUISITION",
            "sourceProperty": "CONTACTS"
          }
        ]
      },
      {
        "name": "Targeted Sequencing",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TARGETED_SEQUENCING"
          }
        ]
      },
      {
        "name": "External Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "EXTERNAL_NAME"
          }
        ]
      }
    ]
  }
]'),
('Tapestri', 'ILLUMINA', '[]', '[
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "FCID",
        "sources": []
      },
      {
        "name": "Lane",
        "sources": [
          {
            "source": "PARTITION"
          }
        ]
      },
      {
        "name": "Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "NAME"
          }
        ]
      },
      {
        "name": "Alias",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "ALIAS"
          }
        ]
      },
      {
        "name": "Project",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_CODE"
          }
        ]
      },
      {
        "name": "ProjectName",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_NAME"
          }
        ]
      },
      {
        "name": "Requisition ID",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ID" 
          }
        ]
      },
      {
        "name": "Requisition Alias",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ALIAS" 
          }
        ]
      },
      {
        "name": "Assay",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ASSAY" 
          }
        ]
      },
      {
        "name": "Tissue Origin",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_ORIGIN"
          }
        ]
      },
      {
        "name": "Tissue Type",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_TYPE"
          }
        ]
      },
      {
        "name": "Description",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "LIBRARY_DESCRIPTION"
          }
        ]
      },
      {
        "name": "Sample Type",
         "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SAMPLE_TYPE" 
          }
        ]
      },
      {
        "name": "Sci. Name",
        "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SCI_NAME" 
          }
        ]
      },
      {
        "name": "Code",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "DESIGN_CODE"
          }
        ]
      },
      {
        "name": "Platform",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PLATFORM"
          }
        ]
      },
      {
        "name": "Type",
        "sources": []
      },
      {
        "name": "Selection",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "SELECTION"
          }
        ]
      },
      {
        "name": "Strategy",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "STRATEGY"
          }
        ]
      },
      {
        "name": "Index Kit",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_FAMILY"
          }
        ]
      },
      {
        "name": "I7_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_NAME"
          }
        ]
      },
      {
        "name": "index",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
          }
        ]
      },
      {
        "name": "I5_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_NAME"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "index2",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_SEQUENCE"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "Has UMIs",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "HAS_UMIS"
          }
        ]
      },
      {
        "name": "Creation Date",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "CREATED_DATE"
          }
        ]
      },
      {
        "name": "Requisition Description",
        "sources": []
      },
      {
        "name": "Requisition Contacts",
        "sources": [
          {
            "source": "REQUISITION",
            "sourceProperty": "CONTACTS"
          }
        ]
      },
      {
        "name": "Targeted Sequencing",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TARGETED_SEQUENCING"
          }
        ]
      },
      {
        "name": "External Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "EXTERNAL_NAME"
          }
        ]
      }
    ]
  }
]'),
('CRISPR', 'ILLUMINA', '[]', '[
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "FCID",
        "sources": []
      },
      {
        "name": "Lane",
        "sources": [
          {
            "source": "PARTITION"
          }
        ]
      },
      {
        "name": "Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "NAME"
          }
        ]
      },
      {
        "name": "Alias",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "ALIAS"
          }
        ]
      },
      {
        "name": "Project",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_CODE"
          }
        ]
      },
      {
        "name": "ProjectName",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_NAME"
          }
        ]
      },
      {
        "name": "Requisition ID",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ID" 
          }
        ]
      },
      {
        "name": "Requisition Alias",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ALIAS" 
          }
        ]
      },
      {
        "name": "Assay",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ASSAY" 
          }
        ]
      },
      {
        "name": "Tissue Origin",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_ORIGIN"
          }
        ]
      },
      {
        "name": "Tissue Type",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_TYPE"
          }
        ]
      },
      {
        "name": "Description",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "LIBRARY_DESCRIPTION"
          }
        ]
      },
      {
        "name": "Sample Type",
         "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SAMPLE_TYPE" 
          }
        ]
      },
      {
        "name": "Sci. Name",
        "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SCI_NAME" 
          }
        ]
      },
      {
        "name": "Code",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "DESIGN_CODE"
          }
        ]
      },
      {
        "name": "Platform",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PLATFORM"
          }
        ]
      },
      {
        "name": "Type",
        "sources": []
      },
      {
        "name": "Selection",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "SELECTION"
          }
        ]
      },
      {
        "name": "Strategy",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "STRATEGY"
          }
        ]
      },
      {
        "name": "Index Kit",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_FAMILY"
          }
        ]
      },
      {
        "name": "I7_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_NAME"
          }
        ]
      },
      {
        "name": "index",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
          }
        ]
      },
      {
        "name": "I5_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_NAME"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "index2",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_SEQUENCE"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "Has UMIs",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "HAS_UMIS"
          }
        ]
      },
      {
        "name": "Creation Date",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "CREATED_DATE"
          }
        ]
      },
      {
        "name": "Requisition Description",
        "sources": []
      },
      {
        "name": "Requisition Contacts",
        "sources": [
          {
            "source": "REQUISITION",
            "sourceProperty": "CONTACTS"
          }
        ]
      },
      {
        "name": "Targeted Sequencing",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TARGETED_SEQUENCING"
          }
        ]
      },
      {
        "name": "External Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "EXTERNAL_NAME"
          }
        ]
      }
    ]
  }
]'),
('Epi Bulk-ATAC', 'ILLUMINA', '[]', '[
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "FCID",
        "sources": []
      },
      {
        "name": "Lane",
        "sources": [
          {
            "source": "PARTITION"
          }
        ]
      },
      {
        "name": "Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "NAME"
          }
        ]
      },
      {
        "name": "Alias",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "ALIAS"
          }
        ]
      },
      {
        "name": "Project",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_CODE"
          }
        ]
      },
      {
        "name": "ProjectName",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_NAME"
          }
        ]
      },
      {
        "name": "Requisition ID",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ID" 
          }
        ]
      },
      {
        "name": "Requisition Alias",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ALIAS" 
          }
        ]
      },
      {
        "name": "Assay",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ASSAY" 
          }
        ]
      },
      {
        "name": "Tissue Origin",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_ORIGIN"
          }
        ]
      },
      {
        "name": "Tissue Type",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_TYPE"
          }
        ]
      },
      {
        "name": "Description",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "LIBRARY_DESCRIPTION"
          }
        ]
      },
      {
        "name": "Sample Type",
         "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SAMPLE_TYPE" 
          }
        ]
      },
      {
        "name": "Sci. Name",
        "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SCI_NAME" 
          }
        ]
      },
      {
        "name": "Code",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "DESIGN_CODE"
          }
        ]
      },
      {
        "name": "Platform",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PLATFORM"
          }
        ]
      },
      {
        "name": "Type",
        "sources": []
      },
      {
        "name": "Selection",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "SELECTION"
          }
        ]
      },
      {
        "name": "Strategy",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "STRATEGY"
          }
        ]
      },
      {
        "name": "Index Kit",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_FAMILY"
          }
        ]
      },
      {
        "name": "I7_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_NAME"
          }
        ]
      },
      {
        "name": "index",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
          }
        ]
      },
      {
        "name": "I5_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_NAME"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "index2",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_SEQUENCE"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "Has UMIs",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "HAS_UMIS"
          }
        ]
      },
      {
        "name": "Creation Date",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "CREATED_DATE"
          }
        ]
      },
      {
        "name": "Requisition Description",
        "sources": []
      },
      {
        "name": "Requisition Contacts",
        "sources": [
          {
            "source": "REQUISITION",
            "sourceProperty": "CONTACTS"
          }
        ]
      },
      {
        "name": "Targeted Sequencing",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TARGETED_SEQUENCING"
          }
        ]
      },
      {
        "name": "External Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "EXTERNAL_NAME"
          }
        ]
      }
    ]
  }
]'),
('WG-WE-RNAseq-SequencingReady', 'ILLUMINA', '[]', '[
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "FCID",
        "sources": []
      },
      {
        "name": "Lane",
        "sources": [
          {
            "source": "PARTITION"
          }
        ]
      },
      {
        "name": "Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "NAME"
          }
        ]
      },
      {
        "name": "Alias",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "ALIAS"
          }
        ]
      },
      {
        "name": "Project",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_CODE"
          }
        ]
      },
      {
        "name": "ProjectName",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_NAME"
          }
        ]
      },
      {
        "name": "Requisition ID",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ID" 
          }
        ]
      },
      {
        "name": "Requisition Alias",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ALIAS" 
          }
        ]
      },
      {
        "name": "Assay",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ASSAY" 
          }
        ]
      },
      {
        "name": "Tissue Origin",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_ORIGIN"
          }
        ]
      },
      {
        "name": "Tissue Type",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_TYPE"
          }
        ]
      },
      {
        "name": "Description",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "LIBRARY_DESCRIPTION"
          }
        ]
      },
      {
        "name": "Sample Type",
         "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SAMPLE_TYPE" 
          }
        ]
      },
      {
        "name": "Sci. Name",
        "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SCI_NAME" 
          }
        ]
      },
      {
        "name": "Code",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "DESIGN_CODE"
          }
        ]
      },
      {
        "name": "Platform",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PLATFORM"
          }
        ]
      },
      {
        "name": "Type",
        "sources": []
      },
      {
        "name": "Selection",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "SELECTION"
          }
        ]
      },
      {
        "name": "Strategy",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "STRATEGY"
          }
        ]
      },
      {
        "name": "Index Kit",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_FAMILY"
          }
        ]
      },
      {
        "name": "I7_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_NAME"
          }
        ]
      },
      {
        "name": "index",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
          }
        ]
      },
      {
        "name": "I5_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_NAME"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "index2",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_SEQUENCE"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "Has UMIs",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "HAS_UMIS"
          }
        ]
      },
      {
        "name": "Creation Date",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "CREATED_DATE"
          }
        ]
      },
      {
        "name": "Requisition Description",
        "sources": []
      },
      {
        "name": "Requisition Contacts",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "CONTACTS" 
          }
        ]
      },
      {
        "name": "Targeted Sequencing",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TARGETED_SEQUENCING"
          }
        ]
      },
      {
        "name": "External Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "EXTERNAL_NAME"
          }
        ]
      }
    ]
  }
]'),
('10X Single Index', 'ILLUMINA', '[]', '[
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "FCID",
        "sources": []
      },
      {
        "name": "Lane",
        "sources": [
          {
            "source": "PARTITION"
          }
        ]
      },
      {
        "name": "Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "NAME"
          }
        ]
      },
      {
        "name": "Alias",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "ALIAS"
          },
          {
            "value": "_"
          },
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
          }
        ]
      },
      {
        "name": "Project",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_CODE"
          }
        ]
      },
      {
        "name": "ProjectName",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PROJECT_NAME"
          }
        ]
      },
      {
        "name": "Requisition ID",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ID" 
          }
        ]
      },
      {
        "name": "Requisition Alias",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ALIAS" 
          }
        ]
      },
      {
        "name": "Assay",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "ASSAY" 
          }
        ]
      },
      {
        "name": "Tissue Origin",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_ORIGIN"
          }
        ]
      },
      {
        "name": "Tissue Type",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TISSUE_TYPE"
          }
        ]
      },
      {
        "name": "Description",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "LIBRARY_DESCRIPTION"
          }
        ]
      },
      {
        "name": "Sample Type",
         "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SAMPLE_TYPE" 
          }
        ]
      },
      {
        "name": "Sci. Name",
        "sources": [
          { 
            "source": "LIBRARY_ALIQUOT", 
            "sourceProperty": "SCI_NAME" 
          }
        ]
      },
      {
        "name": "Code",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "DESIGN_CODE"
          }
        ]
      },
      {
        "name": "Platform",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "PLATFORM"
          }
        ]
      },
      {
        "name": "Type",
        "sources": []
      },
      {
        "name": "Selection",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "SELECTION"
          }
        ]
      },
      {
        "name": "Strategy",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "STRATEGY"
          }
        ]
      },
      {
        "name": "Index Kit",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_FAMILY"
          }
        ]
      },
      {
        "name": "I7_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_NAME"
          }
        ]
      },
      {
        "name": "index",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_1_SEQUENCE"
          }
        ]
      },
      {
        "name": "I5_Index_ID",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_NAME"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "index2",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "INDEX_2_SEQUENCE"
          }
        ],
        "omitIfEmpty": true
      },
      {
        "name": "Has UMIs",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "HAS_UMIS"
          }
        ]
      },
      {
        "name": "Creation Date",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "CREATED_DATE"
          }
        ]
      },
      {
        "name": "Requisition Description",
        "sources": []
      },
      {
        "name": "Requisition Contacts",
        "sources": [
          { 
            "source": "REQUISITION", 
            "sourceProperty": "CONTACTS" 
          }
        ]
      },
      {
        "name": "Targeted Sequencing",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "TARGETED_SEQUENCING"
          }
        ]
      },
      {
        "name": "External Name",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "EXTERNAL_NAME"
          }
        ]
      }
    ]
  }
]'),
('Cell Ranger Demultiplexing', 'ILLUMINA', '[]', '[
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "sample_id",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "ALIAS"
          }
        ]
      },
      {
        "name": "barcode_ids",
        "sources": []
      },
      {
        "name": "requisition_alias",
        "sources": [
          {
            "source": "REQUISITION",
            "sourceProperty": "ALIAS"
          }
        ]
      }
    ]
  }
]'),
('Space Ranger Demultiplexing', 'ILLUMINA', '[]', '[
  {
    "name": "Data",
    "format": "COLUMNS",
    "multivalue": "LIBRARY_ALIQUOTS",
    "fields": [
      {
        "name": "sample_id",
        "sources": [
          {
            "source": "LIBRARY_ALIQUOT",
            "sourceProperty": "ALIAS"
          }
        ]
      },
      {
        "name": "slide_matrix_id",
        "sources": []
      },
      {
        "name": "requisition_alias",
        "sources": [
          {
            "source": "REQUISITION",
            "sourceProperty": "ALIAS"
          }
        ]
      }
    ]
  }
]');
