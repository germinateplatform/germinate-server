---
layout: doc
title: User Documentation
subtitle: Learn how to use Germinate one step at a time.
toc: false
---

<div class="card-grid">
  {% for item in site.user_pages %}
    <a href="{{ item.url | relative_url }}" class="card">
      <h3>{{ item.title }}</h3>
      <p>{{ item.description }}</p>
    </a>
  {% endfor %}
</div>