const api = {
  categorias: "/api/categorias",
  comercios: "/api/comercios",
  health: "/api/health"
};

const categoriaSelect = document.getElementById("categoriaSelect");
const categoriaComercio = document.getElementById("categoriaComercio");
const comercioResena = document.getElementById("comercioResena");
const comerciosGrid = document.getElementById("comerciosGrid");
const totalComercios = document.getElementById("totalComercios");
const healthStatus = document.getElementById("healthStatus");

async function fetchJson(url, options = {}) {
  const response = await fetch(url, {
    headers: { "Content-Type": "application/json" },
    ...options
  });

  if (!response.ok) {
    const fallback = "Error inesperado";
    let message = fallback;
    try {
      const body = await response.json();
      message = body.details?.join(". ") || body.error || fallback;
    } catch (_e) {
      message = fallback;
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

function showToast(message, type = "success") {
  const container = document.getElementById("toastContainer");
  const toast = document.createElement("div");
  toast.className = `toast align-items-center text-bg-${type === "success" ? "success" : "danger"} border-0`;
  toast.role = "alert";
  toast.ariaLive = "assertive";
  toast.ariaAtomic = "true";
  toast.innerHTML = `
    <div class="d-flex">
      <div class="toast-body">${message}</div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
    </div>
  `;
  container.appendChild(toast);
  const bsToast = new bootstrap.Toast(toast, { delay: 2600 });
  bsToast.show();
  toast.addEventListener("hidden.bs.toast", () => toast.remove());
}

async function loadHealth() {
  try {
    const data = await fetchJson(api.health);
    healthStatus.textContent = `API operativa (${data.service})`;
  } catch (_e) {
    healthStatus.textContent = "No se puede conectar con la API";
  }
}

function buildCategoryOptions(categorias) {
  const options = ["<option value=''>Todas las categorías</option>"];
  categorias.forEach((categoria) => {
    options.push(`<option value='${categoria.id}'>${categoria.nombreCategoria}</option>`);
  });
  categoriaSelect.innerHTML = options.join("");

  const optionsOnly = categorias
    .map((categoria) => `<option value='${categoria.id}'>${categoria.nombreCategoria}</option>`)
    .join("");
  categoriaComercio.innerHTML = optionsOnly;
}

function buildComercioSelect(comercios) {
  const options = comercios
    .map((comercio) => `<option value='${comercio.id}'>${comercio.nombreComercio}</option>`)
    .join("");
  comercioResena.innerHTML = options;
}

function renderComercios(comercios) {
  totalComercios.textContent = `${comercios.length} resultados`;

  if (!comercios.length) {
    comerciosGrid.innerHTML = "<p class='text-muted'>No hay comercios para esta categoría.</p>";
    return;
  }

  comerciosGrid.innerHTML = comercios
    .map((comercio) => `
      <div class="col-md-6 col-xl-4">
        <article class="comercio-card">
          <div class="comercio-banner"></div>
          <div class="comercio-content">
            <span class="comercio-tag">${comercio.categoria}</span>
            <h3 class="h5 mt-2">${comercio.nombreComercio}</h3>
            <p class="text-muted mb-2">${comercio.descripcion || "Sin descripción"}</p>
            <small class="text-body-secondary">${comercio.horario || "Horario no indicado"}</small>
          </div>
        </article>
      </div>
    `)
    .join("");
}

async function loadCategorias() {
  const categorias = await fetchJson(api.categorias);
  buildCategoryOptions(categorias);
  return categorias;
}

async function loadComercios() {
  const categoriaId = categoriaSelect.value;
  const query = categoriaId ? `?categoriaId=${categoriaId}` : "";
  const comercios = await fetchJson(`${api.comercios}${query}`);
  renderComercios(comercios);
  buildComercioSelect(comercios);
  return comercios;
}

async function createComercio(event) {
  event.preventDefault();

  const payload = {
    nombreComercio: document.getElementById("nombreComercio").value,
    descripcion: document.getElementById("descripcionComercio").value,
    horario: document.getElementById("horarioComercio").value,
    diasApertura: "Lunes-Domingo",
    categoriaId: Number(categoriaComercio.value)
  };

  try {
    await fetchJson(api.comercios, {
      method: "POST",
      body: JSON.stringify(payload)
    });
    showToast("Comercio creado correctamente");
    event.target.reset();
    await loadComercios();
  } catch (error) {
    showToast(error.message, "error");
  }
}

async function createResena(event) {
  event.preventDefault();

  const comercioId = Number(comercioResena.value);
  const payload = {
    titulo: document.getElementById("tituloResena").value,
    comentario: document.getElementById("comentarioResena").value,
    valoracion: Number(document.getElementById("valoracionResena").value),
    autorNombre: document.getElementById("autorNombre").value
  };

  try {
    await fetchJson(`/api/comercios/${comercioId}/resenas`, {
      method: "POST",
      body: JSON.stringify(payload)
    });
    showToast("Reseña publicada");
    event.target.reset();
  } catch (error) {
    showToast(error.message, "error");
  }
}

async function init() {
  await loadHealth();
  await loadCategorias();
  await loadComercios();

  categoriaSelect.addEventListener("change", loadComercios);
  document.getElementById("reloadBtn").addEventListener("click", loadComercios);
  document.getElementById("comercioForm").addEventListener("submit", createComercio);
  document.getElementById("resenaForm").addEventListener("submit", createResena);
}

init().catch((error) => {
  showToast(error.message, "error");
});
