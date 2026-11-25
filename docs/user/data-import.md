---
layout: doc
title: Data Import & Templates
subtitle: Validating and uploading structured data into Germinate.
---

Data enters Germinate through standardized Excel/tab-delimited templates or programmatically via the Breeding API (BrAPI). On this page we describe the different Germinate Data Templates and how to upload data using them.

## Template Workflow

1. **Download Template**: Obtain the relevant `.xlsx` (or text-based) template (Germplasm, Trials, Genotypes, or Climate) from the Germinate interface.
2. **Populate Columns**: Fill required fields using controlled vocabularies and add your research data.
3. **Validate & Upload**: Germinate runs automated integrity checks to flag missing accessions, invalid dates, or duplicate entries before committing records to the database.

The template files are [available on GitHub](https://github.com/germinateplatform/germinate-data-templates).

<div class="callout callout--info" role="region" aria-label="Info">
  <div class="callout-title">Required Permissions</div>
  <p markdown="1">Please note, that you have to use Gatekeeper and have a user account with at least the `Data Curator` permissions on this Germinate instance..</p>
</div>

## Template Types



### Germplasm Data (MCPD)

Since Germinate is a very germplasm-centric database, most data that gets uploaded is associated with germplasm entries. Therefore, before you upload phenotypic trials data, pedigrees or genotypic data, Germinate has to know about the germplasm this data will be associated with.

When uploading germplasm data, make sure that the `ACCENUMB` column in the MCPD sheet is a unique identifer across your whole collection. Also note, that any data that you wish to upload at a later date has to use the same `ACCENUMB` identifier to let Germinate link up the data properly.

Detailed information about this data template is available on the dedicated [GitHub documentation page](https://github.com/germinateplatform/germinate-data-templates/tree/master/germplasm).

### Pedigree Data

Pedigree data records the ancestry, crosses, and lineage relationships of plant lines and varieties across generations in breeding programs and genetic repositories. Pedigree data uploaded to Germinate can later be exported into [Helium format](https://helium.hutton.ac.uk) for visualization.

Detailed information about this data template is available on the dedicated [GitHub documentation page](https://github.com/germinateplatform/germinate-data-templates/tree/master/pedigree-data).

### Phenotypic Trials Data

Phenotypic observation data is the most frequent data type that people use Germinate for. Basically put, these are measurements for a specific germplasm entry for a variable. These can include things like `Plant height`, `Yield`, `Disease resistance` or any other variable you can think of. Usually, this data is collected from environments like fields, glass houses, poly tunnels or controlled environments, but the structure of the data is always the same. You basically have, among others, the following attributes:

- **Germplasm identifier**: Which germplasm does this data related to?
- **Variable definition**: What is the variable that's being measured? A variable is a combination of a trait, a method, and a scale.
- **Timestamp**: The exact time point when the measurement was taken.
- **Trial setup information**: Information about any potential layout/arrangement of the germplasm entries. This can include the trial site, reps, treatments, and blocks.
- **Observation data**: The actual observationals and measurements.

Detailed information about this data template is available on the dedicated [GitHub documentation page](https://github.com/germinateplatform/germinate-data-templates/tree/master/trials-data).

### Environmental Data

Environmental data usually comes from remote sensing infrastructure like weather stations/sensors or similar. From a technical point of view, this data is very similar to phenotypic trials data, just that we aren't recording data for germplasm entries, but for locations instead.

Detailed information about this data template is available on the dedicated [GitHub documentation page](https://github.com/germinateplatform/germinate-data-templates/tree/master/environmental-data).

### Genotypic Data

Genotypic data in Germinate consists of large-scale molecular marker information. It is basically a huge matrix of germplasm entries by markers with the data content being the individual allele calls.

Due to the large size of genotypic datasets and the two different orientations in which it can be stores, Germinate allows the upload of this data in a variety of formats and orientations. In addition to Excel-based templates, basic text-based data and the Hapmap format are supported.

Detailed information about this data template is available on the dedicated [GitHub documentation page](https://github.com/germinateplatform/germinate-data-templates/tree/master/genotypic-data).

### Images

Images are another type of data that uses non-Excel based data containers. To be precise, image data gets uploaded in a `.zip` file containing the images themselves plus a mapping file that tells Germinate which image to map to which database entry. This mapping file is actually Excel-based again.

Images can be mapped to germplasm and observation variables.

Detailed information about this data template is available on the dedicated [GitHub documentation page](https://github.com/germinateplatform/germinate-data-templates/tree/master/images).

### Groups



Detailed information about this data template is available on the dedicated [GitHub documentation page](https://github.com/germinateplatform/germinate-data-templates/tree/master/groups).


## BrAPI Support

Germinate implements **BrAPI (Breeding API)** endpoints, allowing external tools such as Field Book, BMS, and R packages (`brapir`) to query and push data directly.
