const state = {
  categorias: [],
  selectedCategoriaIds: new Set(),
  search: "",
  comercios: []
};

const refs = {
  categoriaFilters: document.getElementById("categoriaFilters"),
  searchInput: document.getElementById("searchInput"),
  resultsCount: document.getElementById("resultsCount"),
  comercioGrid: document.getElementById("comercioGrid"),
  applyFiltersBtn: document.getElementById("applyFiltersBtn"),
  clearFiltersBtn: document.getElementById("clearFiltersBtn")
};

async function fetchJson(url, options = {}) {
  const response = await fetch(url, options);
  if (!response.ok) {
    throw new Error("No se pudo cargar la informacion de comercios");
  }
  return response.json();
}

function renderCategoriaFilters() {
  refs.categoriaFilters.innerHTML = state.categorias
    .map((categoria) => {
      const checked = state.selectedCategoriaIds.has(categoria.id) ? "checked" : "";
      return `
        <div class="form-check">
          <input class="form-check-input" type="checkbox" value="${categoria.id}" id="cat-${categoria.id}" ${checked}>
          <label class="form-check-label" for="cat-${categoria.id}">${categoria.nombreCategoria}</label>
        </div>
      `;
    })
    .join("");
}

function buildStars(value) {
  const rating = Number(value || 0);
  const rounded = Math.round(rating);
  let stars = "";
  for (let i = 1; i <= 5; i += 1) {
    stars += i <= rounded ? "<i class='bi bi-star-fill'></i>" : "<i class='bi bi-star'></i>";
  }
  return stars;
}

function renderComercios() {
  const filtered = state.comercios.filter((comercio) => {
    const byCategory = state.selectedCategoriaIds.size === 0 || [...state.selectedCategoriaIds].includes(comercio.categoriaId);
    const bySearch = comercio.nombreComercio.toLowerCase().includes(state.search) || (comercio.descripcion || "").toLowerCase().includes(state.search);
    return byCategory && bySearch;
  });

  refs.resultsCount.textContent = `Mostrando ${filtered.length} comercios`;

  refs.comercioGrid.innerHTML = filtered
    .map((comercio) => `
      <div class="col-12 col-sm-6 col-md-4 col-xl-3">
        <div class="card commerce-card shadow-sm">
          <img src="${comercio.logo || 'images/logo_og.png'}" class="card-img-top" alt="${comercio.nombreComercio}">
          <span class="badge badge-status bg-success">Disponible</span>
          <div class="card-body">
            <p class="card-text text-muted mb-1" style="font-size: 0.85rem">${comercio.categoria}</p>
            <h5 class="card-title fw-bold mb-2">${comercio.nombreComercio}</h5>
            <div class="d-flex align-items-center mb-2 text-warning">
              ${buildStars(comercio.puntuacionMedia)}
              <span class="fw-medium ms-2" style="font-size: 0.95rem">${Number(comercio.puntuacionMedia || 0).toFixed(1)}</span>
              <span class="text-muted ms-1" style="font-size: 0.8rem">(${comercio.totalResenas || 0})</span>
            </div>
            <div class="d-grid">
              <a href="comercio_individual.html?id=${comercio.id}" class="btn btn-sm btn-primary rounded-3" style="background-color: var(--db-secondary); border-color: var(--db-secondary);">Ver Detalles</a>
            </div>
          </div>
        </div>
      </div>
    `)
    .join("");

  if (!filtered.length) {
    refs.comercioGrid.innerHTML = "<div class='col-12'><div class='alert alert-light border'>No hay comercios para los filtros seleccionados.</div></div>";
  }
}

function setupEvents() {
  refs.applyFiltersBtn.addEventListener("click", () => {
    state.selectedCategoriaIds = new Set(
      [...refs.categoriaFilters.querySelectorAll("input[type='checkbox']:checked")].map((item) => Number(item.value))
    );
    renderComercios();
  });

  refs.clearFiltersBtn.addEventListener("click", () => {
    state.selectedCategoriaIds = new Set();
    state.search = "";
    refs.searchInput.value = "";
    renderCategoriaFilters();
    renderComercios();
  });

  refs.searchInput.addEventListener("input", (event) => {
    state.search = event.target.value.trim().toLowerCase();
    renderComercios();
  });
}

async function init() {
  const [categorias, comercios] = await Promise.all([
    fetchJson("/api/categorias"),
    fetchJson("/api/comercios")
  ]);

  state.categorias = categorias;
  state.comercios = comercios.map((item) => {
    const categoria = categorias.find((cat) => cat.nombreCategoria === item.categoria);
    return {
      ...item,
      categoriaId: categoria?.id
    };
  });

  renderCategoriaFilters();
  renderComercios();
  setupEvents();
}

init().catch(() => {
  refs.resultsCount.textContent = "No se pudo cargar la API";
  refs.comercioGrid.innerHTML = "<div class='col-12'><div class='alert alert-danger'>Error cargando comercios desde el backend.</div></div>";
});
