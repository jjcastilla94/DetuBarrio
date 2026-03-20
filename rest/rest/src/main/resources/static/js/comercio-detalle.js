let currentComercioId = 1;

function getComercioId() {
  const params = new URLSearchParams(window.location.search);
  const id = Number(params.get("id"));
  return Number.isFinite(id) && id > 0 ? id : 1;
}

async function fetchJson(url, options = {}) {
  const response = await fetch(url, options);
  if (!response.ok) {
    let message = "No se pudo completar la operación";
    try {
      const body = await response.json();
      message = body.details?.join(". ") || body.error || message;
    } catch (_e) {
      // Ignorado: mantenemos el mensaje por defecto
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

function showFeedback(message, type) {
  const feedback = document.getElementById("resenaFeedback");
  if (!feedback) return;

  feedback.classList.remove("d-none", "alert-success", "alert-danger");
  feedback.classList.add(type === "success" ? "alert-success" : "alert-danger");
  feedback.textContent = message;
}

function renderProductos(productos = []) {
  const container = document.getElementById("productosGrid");
  if (!productos.length) {
    container.innerHTML = "<div class='col-12'><div class='alert alert-light border'>Este comercio aun no tiene productos publicados.</div></div>";
    return;
  }

  container.innerHTML = productos
    .map((producto) => `
      <div class="col-md-6">
        <div class="card product-card shadow-sm h-100">
          <img src="images/panaderia_pan.png" class="card-img-top" alt="${producto.nombreProducto}">
          <div class="card-body">
            <h6 class="card-title">${producto.nombreProducto}</h6>
            <p class="text-muted small">${producto.descripcion || "Sin descripcion"}</p>
            <p class="fw-bold">${Number(producto.precio).toFixed(2)} €</p>
          </div>
        </div>
      </div>
    `)
    .join("");
}

function renderResenas(resenas = []) {
  const container = document.getElementById("resenasList");
  if (!resenas.length) {
    container.innerHTML = "<p class='text-muted small'>Aun no hay reseñas para este comercio.</p>";
    return;
  }

  container.innerHTML = resenas
    .map((resena) => `
      <div class="border rounded-3 p-3 mb-2 bg-white">
        <div class="d-flex justify-content-between align-items-center mb-1">
          <strong>${resena.titulo}</strong>
          <span class="badge text-bg-primary">${resena.valoracion}/5</span>
        </div>
        <p class="mb-1 text-muted small">${resena.comentario || "Sin comentario"}</p>
        <small class="text-secondary">Por ${resena.autorNombre}</small>
      </div>
    `)
    .join("");
}

async function handleSubmitResena(event) {
  event.preventDefault();

  const payload = {
    comercioId: currentComercioId,
    titulo: document.getElementById("resenaTitulo").value.trim(),
    comentario: document.getElementById("resenaComentario").value.trim(),
    valoracion: Number(document.getElementById("resenaValoracion").value),
    autorNombre: document.getElementById("resenaAutor").value.trim(),
    autorEmail: document.getElementById("resenaEmail").value.trim()
  };

  try {
    await fetchJson("/api/comentarios", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });

    showFeedback("Reseña publicada correctamente.", "success");
    event.target.reset();
    await loadComercioDetail(currentComercioId);
  } catch (error) {
    showFeedback(error.message, "error");
  }
}

async function loadComercioDetail(comercioId) {
  const data = await fetchJson(`/api/comercios/${comercioId}`);

  document.title = `${data.nombreComercio} - DeTuBarrio`;
  document.getElementById("comercioNombre").textContent = data.nombreComercio;
  document.getElementById("comercioCategoria").textContent = data.categoria;
  document.getElementById("comercioDescripcion").textContent = data.descripcion || "Sin descripcion disponible.";
  document.getElementById("comercioHorario").textContent = data.horario || "Horario no disponible";
  document.getElementById("comercioDias").textContent = data.diasApertura || "Dias de apertura no disponibles";
  document.getElementById("ratingValue").textContent = Number(data.puntuacionMedia || 0).toFixed(1);
  document.getElementById("ratingCount").textContent = `${data.totalResenas || 0} opiniones`;

  if (data.banner) {
    document.getElementById("heroBanner").src = data.banner;
  }

  renderProductos(data.productos);
  renderResenas(data.resenas);
}

async function init() {
  currentComercioId = getComercioId();
  await loadComercioDetail(currentComercioId);

  const form = document.getElementById("resenaForm");
  if (form) {
    form.addEventListener("submit", handleSubmitResena);
  }
}

init().catch(() => {
  const nombre = document.getElementById("comercioNombre");
  if (nombre) {
    nombre.textContent = "No se pudo cargar el comercio";
  }
});
