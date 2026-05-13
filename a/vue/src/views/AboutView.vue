<script setup>

</script>

<template>
    <section class="py-5 bg-light">
        <div class="container">
            <div class="about-hero position-relative overflow-hidden rounded-5 p-4 p-lg-5 text-white shadow-sm">
                <div class="row align-items-center g-4 position-relative" style="z-index: 1;">
                    <div class="col-lg-7">
                        <span class="badge rounded-pill text-bg-light text-primary mb-3 px-3 py-2">¿Qué es DetuBarrio?</span>
                        <h1 class="display-5 fw-bold mb-3 text-white">
                            Conectamos vecinos con comercio local.
                        </h1>
                        <p class="lead mb-4 text-white-75">
                            DetuBarrio nace para hacer visible el barrio: descubrir negocios cercanos, comparar servicios y apoyar la economía de proximidad.
                        </p>
                        <div class="d-flex flex-wrap gap-2">
                            <RouterLink to="/comercios" class="btn btn-light fw-bold px-4">Ver comercios</RouterLink>
                            <a href="#como-funciona" class="btn btn-outline-light fw-bold px-4">Cómo funciona</a>
                        </div>
                    </div>
                    <div class="col-lg-5">
                        <div class="glass-card rounded-4 p-4">
                            <h2 class="h5 fw-bold mb-3 text-white">Lo que resuelve la plataforma</h2>
                            <ul class="list-unstyled mb-0 d-grid gap-3">
                                <li class="d-flex gap-3 align-items-start">
                                    <span class="feature-icon"><i class="bi bi-search"></i></span>
                                    <span>Buscar comercios por nombre, categoría y servicios.</span>
                                </li>
                                <li class="d-flex gap-3 align-items-start">
                                    <span class="feature-icon"><i class="bi bi-star-fill"></i></span>
                                    <span>Leer valoraciones y tomar decisiones más rápidas.</span>
                                </li>
                                <li class="d-flex gap-3 align-items-start">
                                    <span class="feature-icon"><i class="bi bi-geo-alt-fill"></i></span>
                                    <span>Encontrar opciones cerca de casa sin perder tiempo.</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-4 mt-1">
                <div class="col-md-4">
                    <div class="stat-card card h-100 border-0 shadow-sm rounded-4">
                        <div class="card-body p-4">
                            <i class="bi bi-people-fill fs-3 text-primary mb-3"></i>
                            <h3 class="h5 fw-bold">Comunidad</h3>
                            <p class="text-muted mb-0">Hacemos más fácil que vecinos y negocios se encuentren en un mismo espacio digital.</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="stat-card card h-100 border-0 shadow-sm rounded-4">
                        <div class="card-body p-4">
                            <i class="bi bi-shop-window fs-3 text-primary mb-3"></i>
                            <h3 class="h5 fw-bold">Comercio local</h3>
                            <p class="text-muted mb-0">Damos visibilidad a tiendas, bares y servicios de barrio para impulsar su alcance.</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="stat-card card h-100 border-0 shadow-sm rounded-4">
                        <div class="card-body p-4">
                            <i class="bi bi-lightning-charge-fill fs-3 text-primary mb-3"></i>
                            <h3 class="h5 fw-bold">Búsqueda rápida</h3>
                            <p class="text-muted mb-0">La experiencia está pensada para encontrar lo que necesitas con pocos clics.</p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-4 mt-2" id="como-funciona">
                <div class="col-lg-7">
                    <div class="card border-0 shadow-sm rounded-4 h-100">
                        <div class="card-body p-4 p-lg-5">
                            <h2 class="h3 fw-bold mb-4">Cómo funciona</h2>
                            <div class="timeline-item">
                                <span class="timeline-step">1</span>
                                <div>
                                    <h3 class="h6 fw-bold mb-1">Explora</h3>
                                    <p class="text-muted mb-0">Entra en el listado de comercios y navega por categorías, valoraciones y horarios.</p>
                                </div>
                            </div>
                            <div class="timeline-item">
                                <span class="timeline-step">2</span>
                                <div>
                                    <h3 class="h6 fw-bold mb-1">Busca</h3>
                                    <p class="text-muted mb-0">Usa el buscador para localizar negocios concretos por palabra clave o nombre.</p>
                                </div>
                            </div>
                            <div class="timeline-item mb-0">
                                <span class="timeline-step">3</span>
                                <div>
                                    <h3 class="h6 fw-bold mb-1">Conecta</h3>
                                    <p class="text-muted mb-0">Consulta detalles, revisa opiniones y descubre comercios cercanos con más contexto.</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-lg-5">
                    <div class="card border-0 shadow-sm rounded-4 h-100">
                        <div class="card-body p-4 p-lg-5">
                            <h2 class="h3 fw-bold mb-4">Qué aporta a cada perfil</h2>
                            <div class="mb-4">
                                <h3 class="h6 fw-bold">Para usuarios</h3>
                                <p class="text-muted mb-0">Encuentra comercios fiables, guarda tiempo y apoya negocios cercanos con información clara.</p>
                            </div>
                            <div class="mb-4">
                                <h3 class="h6 fw-bold">Para comercios</h3>
                                <p class="text-muted mb-0">Gana visibilidad, recibe reseñas y muestra mejor tus productos y servicios.</p>
                            </div>
                            <div>
                                <h3 class="h6 fw-bold">Para el barrio</h3>
                                <p class="text-muted mb-0">La comunidad local gana presencia digital y una forma más sencilla de descubrirse.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Sección: Qué Ofrecemos -->
    <section class="py-5">
      <div class="container">
        <h2 class="h3 fw-bold mb-4 text-center">🎯 Qué Ofrecemos</h2>
        <div class="row g-4">
          <div class="col-md-6">
            <div class="card border-0 shadow-sm rounded-4 h-100">
              <div class="card-body p-4">
                <h5 class="fw-bold mb-3">Para Clientes/Usuarios</h5>
                <ul class="list-unstyled d-grid gap-2">
                  <li class="d-flex gap-2"><i class="bi bi-check-circle text-primary"></i> Catálogo de comercios locales por categoría</li>
                  <li class="d-flex gap-2"><i class="bi bi-check-circle text-primary"></i> Sistema de reseñas y valoraciones</li>
                  <li class="d-flex gap-2"><i class="bi bi-check-circle text-primary"></i> Información de horarios y contacto</li>
                  <li class="d-flex gap-2"><i class="bi bi-check-circle text-primary"></i> Consultar productos y servicios disponibles</li>
                  <li class="d-flex gap-2"><i class="bi bi-check-circle text-primary"></i> Guardar favoritos y crear listas personales</li>
                  <li class="d-flex gap-2"><i class="bi bi-check-circle text-primary"></i> Ver promociones y ofertas especiales</li>
                </ul>
              </div>
            </div>
          </div>
          <div class="col-md-6">
            <div class="card border-0 shadow-sm rounded-4 h-100">
              <div class="card-body p-4">
                <h5 class="fw-bold mb-3">Para Comercios/Vendedores</h5>
                <ul class="list-unstyled d-grid gap-2">
                  <li class="d-flex gap-2"><i class="bi bi-check-circle text-primary"></i> Perfil personalizado de tu negocio</li>
                  <li class="d-flex gap-2"><i class="bi bi-check-circle text-primary"></i> Gestión de productos y precios</li>
                  <li class="d-flex gap-2"><i class="bi bi-check-circle text-primary"></i> Crear ofertas y promociones</li>
                  <li class="d-flex gap-2"><i class="bi bi-check-circle text-primary"></i> Recibir reseñas y feedback de clientes</li>
                  <li class="d-flex gap-2"><i class="bi bi-check-circle text-primary"></i> Acceso a estadísticas de visitas</li>
                  <li class="d-flex gap-2"><i class="bi bi-check-circle text-primary"></i> Conectar directamente con tu comunidad</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Sección: Planes -->
    <section class="py-5 bg-light">
      <div class="container">
        <h2 class="h3 fw-bold mb-4 text-center">📋 Planes Disponibles</h2>
        <div class="row g-4">
          <div class="col-md-4">
            <div class="card text-center border-0 shadow-sm rounded-4 h-100">
              <div class="card-body p-4">
                <h5 class="card-title fw-bold mb-3">Navegación Libre</h5>
                <p class="text-muted small mb-3">Sin registro</p>
                <ul class="list-unstyled d-grid gap-2 mb-3 text-start">
                  <li class="small"><i class="bi bi-check text-primary fw-bold"></i> Ver comercios</li>
                  <li class="small"><i class="bi bi-check text-primary fw-bold"></i> Ver reseñas</li>
                  <li class="small"><i class="bi bi-check text-primary fw-bold"></i> Filtrar por categoría</li>
                </ul>
                <p class="fw-bold"><span class="text-primary" style="font-size: 1.5rem;">Gratis</span></p>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="card text-center border-0 shadow-sm rounded-4 h-100 border-primary" style="border-width: 2px !important;">
              <div class="card-body p-4">
                <h5 class="card-title fw-bold mb-3">Usuario Registrado</h5>
                <p class="text-muted small mb-3">Para clientes</p>
                <ul class="list-unstyled d-grid gap-2 mb-3 text-start">
                  <li class="small"><i class="bi bi-check text-primary fw-bold"></i> Todo del plan anterior +</li>
                  <li class="small"><i class="bi bi-check text-primary fw-bold"></i> Crear reseñas</li>
                  <li class="small"><i class="bi bi-check text-primary fw-bold"></i> Valorar comercios</li>
                  <li class="small"><i class="bi bi-check text-primary fw-bold"></i> Acceso a ofertas</li>
                </ul>
                <p class="fw-bold"><span class="text-primary" style="font-size: 1.5rem;">Gratis</span></p>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="card text-center border-0 shadow-sm rounded-4 h-100">
              <div class="card-body p-4">
                <h5 class="card-title fw-bold mb-3">Perfil Comercio</h5>
                <p class="text-muted small mb-3">Para vendedores</p>
                <ul class="list-unstyled d-grid gap-2 mb-3 text-start">
                  <li class="small"><i class="bi bi-check text-primary fw-bold"></i> Panel de control</li>
                  <li class="small"><i class="bi bi-check text-primary fw-bold"></i> Gestionar productos</li>
                  <li class="small"><i class="bi bi-check text-primary fw-bold"></i> Crear ofertas</li>
                  <li class="small"><i class="bi bi-check text-primary fw-bold"></i> Ver estadísticas</li>
                </ul>
                <p class="fw-bold"><span class="text-primary" style="font-size: 1.5rem;">14.95€</span><span class="text-muted">/mes</span></p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

</template>

<style scoped>

.about-hero {
    background:
        linear-gradient(135deg, rgba(0, 51, 102, 0.96), rgba(8, 84, 167, 0.88)),
        radial-gradient(circle at top right, rgba(255, 255, 255, 0.18), transparent 35%);
}

.glass-card {
    background: rgba(255, 255, 255, 0.12);
    border: 1px solid rgba(255, 255, 255, 0.18);
    backdrop-filter: blur(10px);
}

.feature-icon {
    width: 2.25rem;
    height: 2.25rem;
    border-radius: 999px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.16);
    flex: 0 0 auto;
}

.stat-card {
    transition: transform 160ms ease, box-shadow 160ms ease;
}

.stat-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 1rem 2rem rgba(0, 0, 0, 0.08) !important;
}

.timeline-item {
    display: flex;
    gap: 1rem;
    align-items: flex-start;
    margin-bottom: 1.5rem;
}

.timeline-step {
    width: 2.25rem;
    height: 2.25rem;
    border-radius: 999px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background: var(--db-secondary);
    color: #fff;
    font-weight: 700;
    flex: 0 0 auto;
}

@media (max-width: 991.98px) {
    .about-hero {
        border-radius: 1.5rem;
    }
}

</style>