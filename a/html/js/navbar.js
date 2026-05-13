// Módulo de menú dinámico: se carga al inicio de cualquier página
// Detecta si hay token JWT y muestra menú logueado o no logueado

document.addEventListener('DOMContentLoaded', async () => {
  await loadNavbar();
});

async function loadNavbar() {
  const token = localStorage.getItem('token');
  const navbarContainer = document.getElementById('navbar-container');
  
  if (!navbarContainer) {
    console.warn('navbar-container no encontrado en el DOM');
    return;
  }

  if (token) {
    // Usuario logueado: obtener datos
    try {
      const userData = await fetch('http://localhost:8080/api/auth/me', {
        headers: { 'Authorization': `Bearer ${token}` }
      }).then(r => r.json());

      renderNavbarLogued(userData);
    } catch (error) {
      console.error('Error al obtener datos de usuario:', error);
      renderNavbarNotLogued();
    }
  } else {
    // No logueado
    renderNavbarNotLogued();
  }
}

function renderNavbarLogued(userData) {
  const navbar = document.getElementById('navbar-container');
  const userName = userData.nombre || 'Usuario';
  
  navbar.innerHTML = `
    <nav class="navbar navbar-expand-lg navbar-light bg-light border-bottom">
      <div class="container-fluid">
        <!-- Logo -->
        <a class="navbar-brand" href="index.html">
          <img src="images/detubarrio-logo.png" alt="DeTuBarrio" height="40">
          <span>DeTuBarrio</span>
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav ms-auto">
            <li class="nav-item">
              <a class="nav-link" href="listado_comercio.html">Comercios</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="que-es.html">¿Qué es?</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="contacto.html">Contacto</a>
            </li>
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" id="userMenu" role="button" data-bs-toggle="dropdown">
                ${userName} <i class="bi bi-person-circle"></i>
              </a>
              <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userMenu">
                <li><a class="dropdown-item" href="gestion_usuario.html">Mi Perfil</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" onclick="logoutUser()">Logout</a></li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  `;
}

function renderNavbarNotLogued() {
  const navbar = document.getElementById('navbar-container');
  
  navbar.innerHTML = `
    <nav class="navbar navbar-expand-lg navbar-light bg-light border-bottom">
      <div class="container-fluid">
        <!-- Logo -->
        <a class="navbar-brand" href="index.html">
          <img src="images/detubarrio-logo.png" alt="DeTuBarrio" height="40">
          <span>DeTuBarrio</span>
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav ms-auto">
            <li class="nav-item">
              <a class="nav-link" href="listado_comercio.html">Comercios</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="que-es.html">¿Qué es?</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="contacto.html">Contacto</a>
            </li>
            <li class="nav-item">
              <a class="nav-link btn btn-outline-primary ms-2" href="login_db.html">Iniciar Sesión</a>
            </li>
            <li class="nav-item">
              <a class="nav-link btn btn-primary text-white ms-2" href="login_db.html">Registrar</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  `;
}

function logoutUser() {
  localStorage.removeItem('token');
  localStorage.removeItem('userId');
  localStorage.removeItem('rol');
  window.location.href = 'index.html';
}
