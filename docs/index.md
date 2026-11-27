---
layout: default
title: Home
---

<section class="hero">
  <div class="hero-content">
    <h1>Germinate Documentation</h1>
    <p>Explore guides, data schemas, and workflows for managing plant genetic resources, phenotypic trials, and genotypic datasets.</p>
  </div>

  <div class="hero-media">
    <img
      src="{{ '/assets/images/germinate-square.svg' | relative_url }}"
      alt="Germinate logo"
      class="hero-logo"
    >
  </div>
</section>

{% include search-box.html %}

<div class="prose-section" markdown="1">
Germinate is an open source plant database infrastructure and application programming platform
on which complex data from genetic resource collections can be stored, queried and visualized
using common, reusable, programming components. Germinate utilises modern web and database
standards to provide a common architecture and high performance web-based user interface and
analytics functionality across a wide variety of data types including: passport, phenotypic,
field trial, pedigree, genetic, climatic, geographic location data as well as user-submitted
annotations. In addition, it provides links to an expanding variety of external visualization
and analysis tools such as [Helium](https://helium.hutton.ac.uk) for pedigree visualization,
[Flapjack](https://ics.hutton.ac.uk/flapjack) for graphical genotyping and
[CurlyWhirly](https://ics.hutton.ac.uk/curlywhirly) for 3D coordinate data.

The single point of access for all background data on genetic resources collections that
Germinate implements, provides a valuable digital curation resource as well as offering
essential continuity on how new data is recorded and presented to researchers and breeders
which brings focus and facilitates community building into research communities.

Find out more about Germinate at <https://germinate.hutton.ac.uk>.
</div>

<h2 id="core-functionality">User documentation</h2>
<div class="card-grid">
  {% for item in site.user_pages %}
    <a href="{{ item.url | relative_url }}" class="card">
      <h3>{{ item.title }}</h3>
      <p>{{ item.description }}</p>
    </a>
  {% endfor %}
</div>

<h2 id="setup-documentation">Admin documentation</h2>
<div class="card-grid">
  {% for item in site.admin_pages %}
    <a href="{{ item.url | relative_url }}" class="card">
      <h3>{{ item.title }}</h3>
      <p>{{ item.description }}</p>
    </a>
  {% endfor %}
</div>

{% if site.funders %}
<section class="funders-section">
  <h2>Funders & Partners</h2>
  <div class="funder-grid">
    {% for funder in site.funders %}
      <a href="{{ funder.url }}" target="_blank" rel="noopener" class="funder-item" title="{{ funder.name }}">
        <img src="{{ funder.logo | relative_url }}" alt="{{ funder.name }}">
      </a>
    {% endfor %}
  </div>

  <p class="prose-section">
    This work is part of the Crop Trust <a href="https://bold.croptrust.org/">BOLD</a> project, which is generously funded by the Government of Norway. BOLD (Biodiversity for Opportunities, Livelihoods and Development) is strengthening food and nutrition security worldwide by supporting the conservation and use of crop diversity.
  </p>
</section>
{% endif %}