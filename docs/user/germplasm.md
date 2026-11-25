---
layout: doc
title: Germplasm & Passport Data
subtitle: Managing accessions, pedigrees, taxonomy, and MCPD standards.
---

Germinate is a germplasm-centric system. Most of the data stored in the database has some relation to a germplasm entry. The user interface, therefore, heavily focusses on letting you browse, filter, and search for germplasm with the ultimate aim to visualize, compare, and export the connected data.

## Passport Data

Germinate stores core plant material records following the [**Multi-Crop Passport Descriptors (MCPD)**](https://hdl.handle.net/10568/69166) standard. 

The Multi-Crop Passport Descriptors (MCPD) provide a standardized baseline of "passport" metadata for plant genetic resources held in genebanks and repositories worldwide. Rather than detailing physical traits or experimental results, they capture foundational records that identify and trace each sample:

- **Sample identity and tracking**: Unique identifiers, names, and codes that distinguish an accession and link duplicate samples across institutions.
- **Custody and storage**: Which institute holds the material, when it was acquired, and how it is maintained or preserved.
- **Taxonomic classification**: The scientific identity of the plant material so collections can be searched and compared consistently across crops.
- **Geographic and site origin**: Where a sample was originally collected or sourced, including location details and coordinates.
- **Biological status and lineage**: Whether the material is wild, cultivated, or bred, along with basic background on its origin or ancestry.
- **Data exchange compatibility**: A uniform structure that allows genebanks and global portals to share and aggregate collection records reliably.

## Passport Page {#passport}

Germinate's passport page is the central location where all information about a specific germplasm connects together. It is here that you can find out everything there is to know about this germplasm as well as where to find phenotypic and genotypic data related to it. There are also links to other resources that may have more information about this germplasm.

Below is an example of a passport page from the [Commonwealth Potato Collection](https://germinate.hutton.ac.uk/cpc/#/data/germplasm/CPC%202154).

![Passport Page]({{ '/assets/images/germplasm/passport.png' | relative_url }})
<p class="image-caption">Passport Page: Provides an overview of all the data related to a specific germplasm.</p>

For each germplasm, there is a list of datasets which contain information about this germplasm. These include phenotypic trials measurements, genotypic data, and pedigree information. The example below is from the [BreedingValue Project](https://germinate.hutton.ac.uk/breedingvalue/#/data/germplasm/JHI-1422).

![Passport Page Datasets]({{ '/assets/images/germplasm/passport-datasets.png' | relative_url }})
<p class="image-caption">Passport Page Datasets: You can easily see which datasets a specific germplasm is contained in. This lets you explore phenotypic and genotypic data for germplasm of interest.</p>

In addition to pointing you to specialised data pages, the passport page also highlights overview statistics about this germplasm. For example, you can compare the phenotypic performance of the current germplasm against the rest of the database to see how it compares when it comes to things like yield, disease resistance or any other trait of interest.

![Passport Page Germplasm Performance]({{ '/assets/images/germplasm/passport-performance2.png' | relative_url }})
<p class="image-caption">Passport Page Germplasm Performance: Get an idea of the germplasm's performance compared to the rest of the data.</p>

The same data can also be visualized as a radar plot for a selection of the traits.

![Passport Page Germplasm Performance]({{ '/assets/images/germplasm/passport-performance.png' | relative_url }})
<p class="image-caption">Passport Page Germplasm Performance: Plot the same information in a radar plot.</p>

## Marking/Selecting Germplasm

Whether you are just browsing Germinate to see what data is available or if you're looking for a specific list of germplasm, Germinate allows you to add any selection of germplasm to a list. This list can then be used at a later point in data visualizations or data exports to only consider the material of interest. There are multiple ways in which germplasm (or markers and locations) can be added to a user's list. This is covered in the <a href="{{ '/user/lists-groups/#working-with-lists' | relative_url }}">section on lists and groups</a>.

## Pedigree Data

A plant pedigree is a documented lineage - often represented as a family tree or breeding record - that tracks the parentage, ancestry, and genetic crosses of a plant cultivar or breeding line across generations. Breeders and researchers use pedigrees to predict how desirable traits (such as disease resistance, yield, drought tolerance, or flavour) are inherited, map genetic diversity, select promising parents for future crosses, and verify variety identity or intellectual property rights.

Germinate can store pedigree data in two forms:

- **Pedigree definitions**: A string (text) representation of the full pedigree in a certain notation (e.g. Purdy or Lamacraft).
- **Pedigree structure**: A hierarchical graph representation of the pedigree which allows for interactive exploration in tools like [Helium](https://helium.hutton.ac.uk).

Pedigree data can be exported via the main menu and following dataset selection or can be previewed for a specific germplasm on the [Passport page](#passport).