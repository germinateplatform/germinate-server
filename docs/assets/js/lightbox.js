function initDocLightbox() {
  if (typeof GLightbox !== 'function') return

  const images = document.querySelectorAll('.doc-body img')
  if (images.length === 0) return

  const elements = Array.from(images).map(function (img) {
    return {
      href: img.currentSrc || img.src,
      type: 'image',
      title: img.alt || '',
    }
  })

  const lightbox = GLightbox({
    elements: elements,
    zoomable: true,
    draggable: true,
    openEffect: 'none',
    closeEffect: 'none',
  })

  images.forEach(function (img, index) {
    img.style.cursor = 'zoom-in'
    img.addEventListener('click', function (event) {
      event.preventDefault()
      lightbox.openAt(index)
    })
  })
}

document.addEventListener('DOMContentLoaded', initDocLightbox)