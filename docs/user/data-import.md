---
layout: doc
title: Data Import & Templates
subtitle: Validating and uploading structured data into Germinate.
---

Data enters Germinate through standardized Excel/tab-delimited templates or programmatically via the Breeding API (BrAPI).

## Template Workflow

1. **Download Template**: Obtain the relevant `.xlsx` template (Germplasm, Trials, Genotypes, or Climate) from the Germinate interface.
2. **Populate Columns**: Fill required fields (marked in bold/required headers) using controlled vocabularies.
3. **Validate & Upload**: Germinate runs automated integrity checks to flag missing accessions, invalid dates, or duplicate entries before committing records to the database.

## BrAPI Support

Germinate implements **BrAPI (Breeding API)** endpoints, allowing external tools such as Field Book, BMS, and R packages (`brapir`) to query and push data directly.
