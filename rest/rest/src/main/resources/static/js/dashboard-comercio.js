import { authFetch, clearAuth, getAuth } from "./auth-store.js";

function goLogin() {
  window.location.href = "login_db.html";
}

function bindLogout() {
  const logoutBtn = document.getElementById("logoutComercioBtn");
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

  if (auth.rol !== "COMERCIO") {
    window.location.href = auth.rol === "USUARIO" ? "gestion_usuario.html" : "login_db.html";
    return;
  }

  try {
    const me = await authFetch("/api/auth/me");
    const comercioNombre = me.comercioNombre || "Mi Comercio";

    document.getElementById("comercioNombreSidebar").textContent = comercioNombre;
    document.getElementById("comercioPanelTitulo").textContent = `Panel General - ${comercioNombre}`;

    await authFetch("/api/dashboard/comercio");
  } catch (_error) {
    clearAuth();
    goLogin();
    return;
  }

  bindLogout();
}

init();
