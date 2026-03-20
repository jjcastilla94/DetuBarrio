import { authFetch, clearAuth, getAuth } from "./auth-store.js";

function goLogin() {
  window.location.href = "login_db.html";
}

function bindLogout() {
  const logoutBtn = document.getElementById("logoutUsuarioBtn");
  if (!logoutBtn) return;

  logoutBtn.addEventListener("click", (event) => {
    event.preventDefault();
    clearAuth();
    goLogin();
  });
}

async function init() {
  const auth = getAuth();
  if (!auth?.token) {
    goLogin();
    return;
  }

  if (auth.rol !== "USUARIO") {
    window.location.href = auth.rol === "COMERCIO" ? "gestion_comercio.html" : "login_db.html";
    return;
  }

  try {
    const me = await authFetch("/api/auth/me");
    document.getElementById("usuarioNombreSidebar").textContent = me.nombre;
    document.getElementById("usuarioSaludo").textContent = `Hola, ${me.nombre}`;

    await authFetch("/api/dashboard/usuario");
  } catch (_error) {
    clearAuth();
    goLogin();
    return;
  }

  bindLogout();
}

init();
