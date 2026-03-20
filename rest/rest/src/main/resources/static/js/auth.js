import { saveAuth, authFetch, getAuth } from "./auth-store.js";

const loginForm = document.getElementById("loginForm");
const registerForm = document.getElementById("registerForm");
const feedback = document.getElementById("authFeedback");
const registerRol = document.getElementById("registerRol");
const registerComercioWrapper = document.getElementById("registerComercioWrapper");

function showFeedback(message, type = "danger") {
  if (!feedback) return;

  feedback.classList.remove("d-none", "alert-danger", "alert-success");
  feedback.classList.add(type === "success" ? "alert-success" : "alert-danger");
  feedback.textContent = message;
}

function redirectByRole(auth) {
  if (auth.rol === "COMERCIO") {
    window.location.href = "gestion_comercio.html";
    return;
  }
  window.location.href = "gestion_usuario.html";
}

function setupRegisterRoleToggle() {
  if (!registerRol || !registerComercioWrapper) return;

  const toggle = () => {
    const isComercio = registerRol.value === "COMERCIO";
    registerComercioWrapper.classList.toggle("d-none", !isComercio);
  };

  registerRol.addEventListener("change", toggle);
  toggle();
}

async function handleLogin(event) {
  event.preventDefault();

  const payload = {
    email: document.getElementById("loginEmail").value.trim(),
    password: document.getElementById("loginPassword").value
  };

  try {
    const auth = await authFetch("/api/auth/login", {
      method: "POST",
      body: JSON.stringify(payload)
    });
    saveAuth(auth);
    redirectByRole(auth);
  } catch (error) {
    showFeedback(error.message || "No se pudo iniciar sesión");
  }
}

async function handleRegister(event) {
  event.preventDefault();

  const comercioIdRaw = document.getElementById("registerComercioId").value;
  const comercioId = comercioIdRaw ? Number(comercioIdRaw) : null;

  const payload = {
    nombre: document.getElementById("registerNombre").value.trim(),
    email: document.getElementById("registerEmail").value.trim(),
    password: document.getElementById("registerPassword").value,
    rol: document.getElementById("registerRol").value,
    comercioId: Number.isFinite(comercioId) && comercioId > 0 ? comercioId : null
  };

  try {
    const auth = await authFetch("/api/auth/register", {
      method: "POST",
      body: JSON.stringify(payload)
    });
    saveAuth(auth);
    showFeedback("Registro completado. Redirigiendo...", "success");
    setTimeout(() => redirectByRole(auth), 400);
  } catch (error) {
    showFeedback(error.message || "No se pudo registrar el usuario");
  }
}

function init() {
  const existing = getAuth();
  if (existing?.token && existing?.rol) {
    redirectByRole(existing);
    return;
  }

  setupRegisterRoleToggle();
  loginForm?.addEventListener("submit", handleLogin);
  registerForm?.addEventListener("submit", handleRegister);
}

init();
