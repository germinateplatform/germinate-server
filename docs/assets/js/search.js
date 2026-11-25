function escapeHtml(value) {
  const div = document.createElement("div")
  div.textContent = value
  return div.innerHTML
}

function hideSearchResults(resultsList) {
  resultsList.hidden = true
}

function renderSearchResults(resultsList, matches) {
  if (matches.length === 0) {
    resultsList.innerHTML = "<li class=\"site-search-empty\">No matching pages found.</li>"
    resultsList.hidden = false
    return
  }

  const itemsHtml = matches.slice(0, 8).map(function (item) {
    return (
      "<li class=\"site-search-result-item\">" +
      "<a href=\"" + escapeHtml(item.url) + "\">" +
      "<strong>" + escapeHtml(item.title) + "</strong>" +
      "<span>" + escapeHtml(item.excerpt) + "</span>" +
      "</a>" +
      "</li>"
    )
  }).join("")

  resultsList.innerHTML = itemsHtml
  resultsList.hidden = false
}

function bindSearchWidget(widget, indexData) {
  const input = widget.querySelector(".site-search-input")
  const resultsList = widget.querySelector(".site-search-results")
  if (!input || !resultsList) return

  function runFilter() {
    const query = input.value.trim().toLowerCase()
    if (!query) {
      resultsList.innerHTML = ""
      hideSearchResults(resultsList)
      return
    }

    const terms = query.split(/\s+/)
    const matches = indexData.filter(function (entry) {
      const haystack = ((entry.title || "") + " " + (entry.content || "")).toLowerCase()
      return terms.every(function (term) {
        return haystack.indexOf(term) !== -1
      })
    })

    renderSearchResults(resultsList, matches)
  }

  input.addEventListener("input", runFilter)

  input.addEventListener("focus", function () {
    if (input.value.trim()) {
      runFilter()
    }
  })

  document.addEventListener("click", function (event) {
    if (!widget.contains(event.target)) {
      hideSearchResults(resultsList)
    }
  })
}

function initSiteSearch() {
  const widgets = document.querySelectorAll("[data-search-widget]")
  if (widgets.length === 0) return

  const indexUrl = document.body.getAttribute("data-search-url") || "/search.json"

  fetch(indexUrl)
    .then(function (response) {
      return response.json()
    })
    .then(function (data) {
      widgets.forEach(function (widget) {
        bindSearchWidget(widget, data)
      })
    })
    .catch(function () {
      widgets.forEach(function (widget) {
        const resultsList = widget.querySelector(".site-search-results")
        if (resultsList) {
          resultsList.innerHTML = "<li class=\"site-search-empty\">Search index unavailable.</li>"
          resultsList.hidden = false
        }
      })
    })
}

initSiteSearch()