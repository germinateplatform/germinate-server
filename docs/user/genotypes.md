---
layout: doc
title: Genotypic & Marker Data
subtitle: High-density SNP matrices, genetic maps, and allele calls.
---

Germinate is optimized to store and query large-scale molecular marker datasets alongside passport and trial data.

## Supported Structures

* **Markers**: Named genetic loci (e.g., SNPs, SSRs) associated with specific marker types.
* **Maps**: Chromosome or linkage group assignments with physical (bp) or genetic (cM) positions.
* **Genotype Matrices**: Compressed storage of allele calls across thousands of germplasm lines and markers.

## Maps

Genetic (linkage) maps plot the relative order and spacing of DNA markers or genes on a chromosome using how often they separate during recombination (crossing over). Markers closer together are inherited together more often. Physical and sequence maps measure actual physical distances along DNA (such as base pairs) rather than recombination rates, with full genome sequencing offering the highest resolution.

Germinate can store maps of any type. From a technical viewpoint, they break down into `Marker name`, `Chromosome` or `Linkage group` and `Position`.

Germinate shows all available maps on the dedicated maps page as well as the genotypic export page.

![Genotypic Data Maps]({{ '/assets/images/genotypes/maps.png' | relative_url }})
<p class="image-caption" markdown="1">Genotypic Data Maps: A table showing maps filtered for a specific project (BARN).</p>

When selecting a map all markers are shown in a table. Same as germplasm, markers can be added to lists and groups by filtering and selecting.

![Genotypic Data Map Markers]({{ '/assets/images/genotypes/maps-markers.png' | relative_url }})
<p class="image-caption" markdown="1">Genotypic Data Map Markers: A table showing all the markers (1.5 million) on the currently selected map.</p>

To visualize a map, Germinate shows a marker density heatmap. This is broken down by chromosome or linkage group and the density is shown on a colour scale (Viridis by default). Higher density areas are more yellow while lower density areas are more purple.

![Genotypic Data Map Heatmap]({{ '/assets/images/genotypes/maps-heatmap.png' | relative_url }})
<p class="image-caption" markdown="1">Genotypic Data Map Heatmap: A heatmap visualizes the marker density per chromosome/linkage group. Yellow areas show high marker density.</p>

To add markers from regions of interest to your list (or group), select the area in the heatmap, then use the options menu to mark them.

![Genotypic Data Map Heatmap Selection]({{ '/assets/images/genotypes/maps-heatmap-selection.png' | relative_url }})
<p class="image-caption" markdown="1">Genotypic Data Map Heatmap Selection: Select any area of interest across the chromosomes/linkage groups to mark markers.</p>

## Markers

Markers have been covered in the maps section already. There is also a dedicated page just for markers across maps. Use the table to filter, sort and search for markers. You can use the <a href="{{ '/user/lists-groups#table-marking' | relative_url }}">table marking functionality</a> to add markers to your <a href="{{ '/user/lists-groups' | relative_url }}">list</a>.

## Data Export

To export genotypic data from Germinate, navigate to the corresponding export page. Here, select the genotypic datasets you would like to export.

![Genotypic Data Export]({{ '/assets/images/genotypes/export.png' | relative_url }})
<p class="image-caption" markdown="1">Genotypic Data Export: Use the table to search for datasets of interest. Then select any you wish to export.</p>

The next page allows you to further sub-select the data you would like to download. Here you can select the germplasm, markers and map to use.

For the germplasm and markers, either export everything or select subsets based on groups or lists.

Out of all available maps that overlap with the selected datasets, Germinate will select the one with the most matching markers on it. Change the selection if required.

The table below shows a preview based on your sub-selection. It highlights how many of the sub-selected germplasm and marker elements are actually contained in the current datasets. Sometimes, you might select a group that isn't actually contained in the dataset or has little overlap and this table will highlight this.

![Genotypic Data Export Selection]({{ '/assets/images/genotypes/export-selection.png' | relative_url }})
<p class="image-caption" markdown="1">Genotypic Data Export Selection: Further narrow your regions of interest down. Select germplasm and marker groups as well as the map.</p>

Finally, select which data format you would like to get in the download. By default, data will be exported into a tab-separated file. You can optionally include Flapjack and Hapmap formats as well.

![Genotypic Data Export Formats]({{ '/assets/images/genotypes/export-formats.png' | relative_url }})
<p class="image-caption" markdown="1">Genotypic Data Export Formats: Decide which data formats you would like to include in the export.</p>

Once the download has started, it will show the progress in the export sidebar.

![Genotypic Data Export Progress]({{ '/assets/images/genotypes/export-progress.png' | relative_url }})
<p class="image-caption" markdown="1">Genotypic Data Export Progress: The sidebar will show the progress of the current data export.</p>

![Genotypic Data Export Result]({{ '/assets/images/genotypes/export-result.png' | relative_url }})
<p class="image-caption" markdown="1">Genotypic Data Export Result: When your export is finished, a download button will appear for each result.</p>

## Integration with Flapjack

Genotypic subsets can be exported directly in **Flapjack** format for graphical genotyping, haplotype visualization, and marker-assisted selection workflows.

The example below shows the previously exported data in Flapjack

![Genotypic Data Export Flapjack]({{ '/assets/images/genotypes/flapjack.png' | relative_url }})
<p class="image-caption" markdown="1">Genotypic Data Export Flapjack: The exported data loaded into Flapjack.</p>