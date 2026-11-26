---
layout: doc
title: Environmental & Climate Data
subtitle: Managing environmental, climatic definitions, and measurement values.
---

Environmental data captures sensory data measured across sites and seasons.

## Data Hierarchy

* **Datasets**: Groupings of environmental observations collected within a specific experiment or season.
* **Locations**: Geographic sites with latitude, longitude, elevation, and country metadata.
* **Variables**: Standardized definitions including variable name, short name, data type (`numeric`, `categorical`, `date`, `text`), and measurement units.

## Environment Variables

In Germinate, environmental data captures climatic, weather, and site conditions - such as temperature, rainfall, or geographic context-associated with germplasm collection sites, locations, and experimental trials. Storing environmental measurements alongside phenotypic and genotypic records helps researchers and breeders examine how growing conditions influence trait expression and adaptation across locations or seasons. You can  explore, filter, or export the data through the Germinate interface.

![Environmental Variables Overview table]({{ '/assets/images/climate/variables.png' | relative_url }})
<p class="image-caption" markdown="1">Environmental Variables Overview table: The main table showing environmental variables pre-filtered to show only those with `numeric` data types.</p>

### Visualizations for Variables

There are various ways in which data can be visualized from a variable's point of view. The main differentiating factor between the visualizations is the data type. Numerical and date-based variable values are visualized in a different way (box plots) to categorical or text-based variables (bar charts). In either case, there are various ways in which you can break the data shown in the chart down. Examples of these are:

- **Datasets**: When data for a variable has been recorded in multiple datasets, the chart can show the data points from each dataset to more easily compare them.
- **Location**: Often it's useful to highlight the data points from a selection of locations against the rest of the data. Select any number of location entries to highlight.
- **Groups**: Groups highlight pre-defined subsets of locations.
- **Years**: For a temporal context, data can be broken down by year of recording which lets you see developments/differences over time.

![Environmental Variables Data]({{ '/assets/images/climate/climate-boxplot.png' | relative_url }})
<p class="image-caption" markdown="1">Environmental Variables Data: Visualization showing environmental values for the variable `Cloud cover`. The boxplot breaks the data points down by locations (e.g. `Japan | Tokyo`) as well as showing the overall combined data.</p>

## Finding and exploring environmental data

There are various ways in which you can find the data you're looking for. We will cover a few of the main use-cases here.

### Dataset-based exploration

This is the way Germinate was originally designed to work. The workflow is basically the following:

1. Identify any environmental dataset you might be interested in
2. On the details page for these datasets, visualize the data for any environmental variables contained in the selected datasets
3. Mark locations based on the visualizations
4. Potentially export the data for use in other tools

Below are two examples showing this type of exploration. In each case, the user has a certain keyword (`yield` and `ODAP`) they are interested in and want to find any datasets that have a relation to this search term. Germinate will present any matching datasets for the respective search. Users can then select which of the datasets to explore in more detail.

![Dataset-based exploration]({{ '/assets/images/climate/explore-dataset-balruddery.png' | relative_url }})
<p class="image-caption" markdown="1">Dataset-based exploration: Environmental datasets filtered to the ones containing `balruddery` tag information.</p>

### Variable-based exploration

### Location-based exploration


## Environmental data visualizations

The data export/visualization page is what you see after having selected environmental datasets from the table. It breaks down into several sections:

- **Similar datasets**: When you select certain datasets, there is a high chance that other datasets will be pretty similar or comparable. This similarity is determined solely on the overlap of germplasm, traits, locations, markers and climates. If a dataset that you have not already selected shows high similarity score, Germinate will flag it as being of potential interest. Click the banner to add a selection of datasets to the current export/visualization page.
- **Selected datasets**: When you are on an export page, you may sometimes still want to see information about the datasets that you selected previously to get here. This expanding section lets you see the currently selected datasets.
- **Available file resources**: Some datasets have static file resources associated with them. Often, that's the <a href="{{ '/user/data-import' | relative_url }}">Germinate Data Template</a> used to create this datasets, but sometimes other file resources may have been associated with one of the selected datasets.
- **Visualization type selection**: The set of coloured buttons lets you decide which type of visualization to use for plotting the data within these datasets. For environmental data, you can choose from:

  [`Data statistics`](#data-statistics), [`Data matrix`](#data-matrix), [`Data trends`](#data-trends), [`Data table`](#data-table), [`Climate locations`](#climate-locations), [`Data download`](#data-download)
- **Visualization**: The last element on this page is the selected visualization. As explained above, the visualization shown here will differ based on the selected tab, but most of them have a similar structure:
  - **Variable selection**: In most cases you will be able to select which environmental variables should be used in the chart. You may only be interested in certain ones or there are just too many to visualize at a time. Select a subset here.
  - **Location selection**: The same holds true for locations. You can either visualize the data for every location included in the dataset or use pre-defined groups to narrow it down. Even your <a href="{{ '/user/ui/#item-marking' | relative_url }}">marked item list</a> can be used here.
  - **Highlight selection**: By default, the charts will just show the data points in the same colour. Often, breaking the data down or using certain characteristics for highlighting can be very insightful. This section lets you select a characteristic which will be highlighted in the chart. Examples include highlighting specific locations, breaking the data down by dataset or plotting the year/season options.

![Environmental Data Visualization Overview]({{ '/assets/images/climate/visualization-overview.png' | relative_url }})
<p class="image-caption" markdown="1">Environmental Data Visualization Overview: The export page for environmental data shows various sections.</p>

### Data statistics

This is where high-level statistics for a selection of the observation variables are shown. For `numeric` and `date` variables boxplots will be used while `categorical` and `text` variables are visualized using bar charts.

*This tab is useful to see data distributions and potential outliers.*

![Environmental Data Visualization Statistics]({{ '/assets/images/climate/visualization-statistics.png' | relative_url }})
<p class="image-caption" markdown="1">Environmental Data Visualization Statistics: Box plots and bar charts to see data distribution and potential outliers.</p>

### Data matrix

Often, looking at data for multiple climate variables at the same time and how they compare/relate can be useful. On the matrix tab, you can select a number of variables and then plot them against each other. The result is a scatter matrix where each individual scatter plot visualizes the data points for two variables.

*This tab is useful to get an idea of potential data clusters and potential correlations.*

![Observation Data Visualization Matrix]({{ '/assets/images/climate/visualization-matrix.png' | relative_url }})
<p class="image-caption" markdown="1">Observation Data Visualization Matrix: See selected environment variables as dimensions. Data points represent observations for individual location entries.</p>

### Data trends

Oftentimes variables are measured over a period of time to see the development with a temporal dimension. This can highlight certain growing periods or weather events that are of interest or differences between selected locations.

*Use this tab to get an insight into temporal effects on the dataset.*

![Environmental Data Visualization Trend]({{ '/assets/images/climate/visualization-trend.png' | relative_url }})
<p class="image-caption" markdown="1">Environmental Data Visualization Trend: Environmental data over time for a selected variable.</p>

### Data table

Rather than offering a visualization, this tab shows the data in a big table. The advantage of this is, that you can use the <a href="{{ '/user/ui#table-filtering' | relative_url }}">table filtering mechanisms</a> to look for any combination of conditions.

*This is useful if you have very specific requirements that can be expressed in logical conditions on the table.*

![Environmental Data Visualization Table Filter]({{ '/assets/images/climate/visualization-table-filter.png' | relative_url }})
<p class="image-caption" markdown="1">Environmental Data Visualization Table Filter: Browse, sort and filter environmental data based on combinations of criteria.</p>

![Environmental Data Visualization Table Result]({{ '/assets/images/climate/visualization-table-result.png' | relative_url }})
<p class="image-caption" markdown="1">Environmental Data Visualization Table Result: Resulting items have to match the combination of requirements.</p>

### Climate locations

Since data may come from different geographic sources, it can be useful to put these on a map to interactively look at.

*This can provide a geographic overview over locations within the dataset.*

![Environmental Data Visualization Locations]({{ '/assets/images/climate/visualization-locations.png' | relative_url }})
<p class="image-caption" markdown="1">Environmental Data Visualization Locations: Locations within the dataset visualized on a map.</p>

### Data download

The purpose of this tab is pretty straightforward: Sometimes Germinate just doesn't offer the exact visualization you need or you want to use the data stored in Germinate in some analysis. One easy solution to this is downloading the data in a tab-separated format to load into other tools. Germinate lets you make sub-selections of variables and locations here to only export areas of interest.

*If you need to download the data for other tools, this is where you can do exactly that.*

![Environmental Data Visualization Export]({{ '/assets/images/climate/visualization-export.png' | relative_url }})
<p class="image-caption" markdown="1">Environmental Data Visualization Export: Export environmental variable data in tab-separated formats of different types.</p>