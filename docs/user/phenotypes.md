---
layout: doc
title: Phenotypic & Trial Data
subtitle: Organizing multi-environment field trials, traits, and observations.
---

Phenotypic data captures observable characteristics measured across field trials, glasshouses, or growth chambers.

## Data Hierarchy

* **Datasets**: Groupings of trial observations collected within a specific experiment or season.
* **Locations**: Geographic sites with latitude, longitude, elevation, and country metadata.
* **Traits/Variables**: Standardized definitions including trait name, short name, data type (`numeric`, `categorical`, `date`, `text`), and measurement units.

## Observation Variables

In agricultural and breeding data standards, an **observation variable** defines a recorded measurement unambiguously by pairing three components: the **trait** (the biological characteristic, quality, or behavior being evaluated, such as plant height or fruit firmness), the **method** (the specific procedure, instrument, or protocol used to measure or score it, such as a ruler at maturity or a penetrometer), and the **scale** (the valid units, numeric range, or categorical scoring system used to express the result, such as centimeters or a 1–9 visual severity scale). Combining all three prevents ambiguous data - ensuring that values collected across different trials, teams, years, or breeding databases mean the exact same thing and can be reliably compared or analyzed together.

Since v5, Germinate supports ontology-based observation variables with the distinction of trait, method and scale.

![Observation Variables Overview table]({{ '/assets/images/phenotypic/variables.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Variables Overview table: The main table showing observation variables (called traits) pre-filtered to show `Phenological` traits, with `Estimation` methods and `Date` scales.</p>

### Visualizations for Variables

There are various ways in which data can be visualized from a variable's point of view. The main differentiating factor between the visualizations is the scale's data type. Numerical and date-based variable values are visualized in a different way (box plots) to categorical or text-based variables (bar charts). In either case, there are various ways in which you can break the data shown in the chart down. Examples of these are:

- **Datasets**: When data for a variable has been recorded in multiple datasets, the chart can show the data points from each dataset to more easily compare them.
- **Germplasm**: Often it's useful to highlight the data points from a selection of germplasm entries against the rest of the data. Select any number of germplasm entries to highlight.
- **Plots**: Plots break the germplasm selection down even further by letting you select the individual observation units for each germplasm.
- **Groups**: Groups highlight pre-defined subsets of germplasm.
- **Years**: For a temporal context, data can be broken down by year of recording which lets you see developments/differences over time.
- **Taxonomies**: When working with multiple subtaxa of a specific crop or even completely different genera, these can be selected as highlights.
- **Reps**: Differences between replicates within a trial can highlight interesting insights.
- **Treatments**: The main reason for the application of different treatmens within a trial is to see the effect each treatment has on the result. Select any number of treatments to see how their data differs.

![Observation Variables Data]({{ '/assets/images/phenotypic/trait-boxplot.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Variables Data: Visualization showing observation values for the variable `Plant height`. The boxplot breaks the data points down by replicate (`1` and `2`) as well as showing the overall combined data.</p>

![Observation Variables Data]({{ '/assets/images/phenotypic/trait-boxplot2.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Variables Data: Another example showing the variable-specific boxplots. This time, the year of recording is used to break the points down into separate boxes.</p>

![Observation Variables Data Highlight]({{ '/assets/images/phenotypic/trait-boxplot-highlight.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Variables Data Highlight: The `Highlight items` selector lets you pick how to break down the chart data.</p>

## Recording Observations

Each observation links the following entries:

- **Germplasm**: The entry in Germinate for which this phenotypic observation was recorded.
- **Variable**: The ontology variable (trait + method + scale) that was used to record this measurement.
- **Dataset**: The dataset/study this observation is part of. This includes information about the trial site, associated individuals, general trial information, and linked data resources.
- **Replicate**: The replicate this observation was taken from (defaults to 1).
- **Block**: The block this observation was taken from.
- **Treatment**: The treatment that may have been applied to the observation unit (e.g. low/high nitrogen, drought, etc.).
- **Timestamp**: The exact time point when the measurement was taken.
- **Layout position**: Row, column, and potentially GPS coordinates associated with the observation unit.

## Finding and exploring trials data

There are various ways in which you can find the data you're looking for. We will cover a few of the main use-cases here.

### Trial-based exploration

This is the way Germinate was originally designed to work. The workflow is basically the following:

1. Identify any trials dataset you might be interested in
2. On the details page for these datasets, visualize the data for any observation variables contained in the selected datasets
3. Mark germplasm based on the visualizations
4. Potentially export the data for use in other tools

Below are two examples showing this type of exploration. In each case, the user has a certain keyword (`yield` and `ODAP`) they are interested in and want to find any datasets that have a relation to this search term. Germinate will present any matching datasets for the respective search. Users can then select which of the datasets to explore in more detail.

![Trial-based exploration]({{ '/assets/images/phenotypic/explore-dataset-yield.png' | relative_url }})
<p class="image-caption" markdown="1">Trial-based exploration: Trials datasets filtered to the ones containing `yield` information.</p>

![Trial-based exploration]({{ '/assets/images/phenotypic/explore-dataset-odap.png' | relative_url }})
<p class="image-caption" markdown="1">Trial-based exploration: Trials datasets filtered to the ones containing `ODAP` information.</p>

### Trait-based exploration

This approach has basically already been covered in the <a href="{{ '/user/phenotypes/#observation-variables' | relative_url }}">observation variables</a> section.

### Germplasm-based exploration

To approach observation data, you can utilise the <a href="{{ '/user/germplasm/#passport-page' | relative_url }}">passport page</a> for a specific germplasm. On there, find the `Trait performance` section to explore the observation data for this germplasm compared to the rest of the database.


## Observation data visualizations

The data export/visualization page is what you see after having selected trials datasets from the table. It breaks down into several sections:

- **Similar datasets**: When you select certain datasets, there is a high chance that other datasets will be pretty similar or comparable. This similarity is determined solely on the overlap of germplasm, traits, locations, markers and climates. If a dataset that you have not already selected shows high similarity score, Germinate will flag it as being of potential interest. Click the banner to add a selection of datasets to the current export/visualization page.
- **Selected datasets**: When you are on an export page, you may sometimes still want to see information about the datasets that you selected previously to get here. This expanding section lets you see the currently selected datasets.
- **Available file resources**: Some datasets have static file resources associated with them. Often, that's the <a href="{{ '/user/data-import' | relative_url }}">Germinate Data Template</a> used to create this datasets, but sometimes other file resources may have been associated with one of the selected datasets.
- **Visualization type selection**: The set of coloured buttons lets you decide which type of visualization to use for plotting the data within these datasets. For trials data, you can choose from:

  [`Data statistics`](#data-statistics), [`Data matrix`](#data-matrix), [`Comparison`](#comparison), [`Data table`](#data-table), [`Trial locations`](#trial-locations), [`Timeseries`](#time-series), [`Data download`](#data-download)
- **Visualization**: The last element on this page is the selected visualization. As explained above, the visualization shown here will differ based on the selected tab, but most of them have a similar structure:
  - **Variable selection**: In most cases you will be able to select which observation variables should be used in the chart. You may only be interested in certain ones or there are just too many to visualize at a time. Select a subset here.
  - **Germplasm selection**: The same holds true for germplasm. You can either visualize the data for every germplasm included in the trial or use pre-defined groups to narrow it down. Even your <a href="{{ '/user/ui/#item-marking' | relative_url }}">marked item list</a> can be used here.
  - **Highlight selection**: By default, the charts will just show the data points in the same colour. Often, breaking the data down or using certain characteristics for highlighting can be very insightful. This section lets you select a characteristic which will be highlighted in the chart. Examples include highlighting specific germplasm, breaking the data down by dataset or plotting the treatment options.

![Observation Data Visualization Overview]({{ '/assets/images/phenotypic/visualization-overview.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Data Visualization Overview: The export page for trials data shows various sections.</p>

### Data statistics

This is where high-level statistics for a selection of the observation variables are shown. For `numeric` and `date` variables boxplots will be used while `categorical` and `text` variables are visualized using bar charts.

*This tab is useful to see data distributions and potential outliers.*

![Observation Data Visualization Statistics]({{ '/assets/images/phenotypic/visualization-statistics.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Data Visualization Statistics: Box plots and bar charts to see data distribution and potential outliers.</p>

### Data matrix

Often, looking at data for multiple traits at the same time and how they compare/relate can be useful. On the matrix tab, you can select a number of variables and then plot them against each other. The result is a scatter matrix where each individual scatter plot visualizes the data points for two variables.

*This tab is useful to get an idea of potential data clusters and potential correlations.*

![Observation Data Visualization Matrix]({{ '/assets/images/phenotypic/visualization-matrix.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Data Visualization Matrix: See selected traits as dimensions. Data points represent observations for individual germplasm entries.</p>

### Comparison

For a small number of selected germplasm, placing them head-to-head can be insightful. Select a number of variables and Germinate will produce a chart that plots the data for each germplasm per variable so you can see how they compare.

*These kinds of charts are useful to look at the performance of certain individual germplasm entries.*

![Observation Data Visualization Comparison]({{ '/assets/images/phenotypic/visualization-comparison.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Data Visualization Comparison: Compare selected germplasm head-to-head across a variety of traits.</p>

### Data table

Rather than offering a visualization, this tab shows the data in a big table. The advantage of this is, that you can use the <a href="{{ '/user/ui#table-filtering' | relative_url }}">table filtering mechanisms</a> to look for any combination of conditions.

*This is useful if you have very specific requirements that can be expressed in logical conditions on the table.*

![Observation Data Visualization Table Filter]({{ '/assets/images/phenotypic/visualization-table-filter.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Data Visualization Table Filter: Browse, sort and filter trials data based on combinations of criteria.</p>

![Observation Data Visualization Table Result]({{ '/assets/images/phenotypic/visualization-table-result.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Data Visualization Table Result: Resulting items have to match the combination of requirements.</p>

### Trial locations

When dealing with trials data, the geographic context can often be very interesting. You might be dealing with a multi-site trial and want to see the different locations or your trial has row and column information, so Germinate can show you the trial design. On top of just showing the layout, you can visualize certain characteristics and even trait data on top.

*Use this tab to see any geographic aspect of the trial.*

![Observation Data Visualization Layout]({{ '/assets/images/phenotypic/visualization-layout-trait-1.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Data Visualization Layout: Trial layout with `Row type` highlighted.</p>

![Observation Data Visualization Layout]({{ '/assets/images/phenotypic/visualization-layout-trait-2.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Data Visualization Layout: Trial layout with `Plant height` highlighted.</p>

![Observation Data Visualization Layout]({{ '/assets/images/phenotypic/visualization-layout-germplasm.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Data Visualization Layout: Trial layout with selected germplasm highlighted.</p>

### Time series

Oftentimes variables are measured over a period of time to see the development with a temporal dimension. This can highlight certain growing periods or weather events that are of interest or differences between selected germplasm.

*Use this tab to get an insight into temporal effects on the trial.*

![Observation Data Visualization Timeseries]({{ '/assets/images/phenotypic/visualization-timeseries.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Data Visualization Timeseries: Trait data over time for a selected trait with a subset of germplasm highlighted.</p>

### Data download

The purpose of this tab is pretty straightforward: Sometimes Germinate just doesn't offer the exact visualization you need or you want to use the data stored in Germinate in some analysis. One easy solution to this is downloading the data in a tab-separated format to load into other tools. Germinate lets you make sub-selections of variables and germplasm here to only export areas of interest.

*If you need to download the data for other tools, this is where you can do exactly that.*

![Observation Data Visualization Export]({{ '/assets/images/phenotypic/visualization-export.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Data Visualization Export: Export trait data in tab-separated formats of different types.</p>