const AUTH_KEY = "detubarrio_auth";

export function saveAuth(authResponse) {
  localStorage.setItem(AUTH_KEY, JSON.stringify(authResponse));
}

export function getAuth() {
  const raw = localStorage.getItem(AUTH_KEY);
  if (!raw) return null;

  try {
    return JSON.parse(raw);
  } catch (_e) {
    clearAuth();
    return null;
  }
}

export function clearAuth() {
  localStorage.removeItem(AUTH_KEY);
}

export function getToken() {
  return getAuth()?.token || null;
}

export async function authFetch(url, options = {}) {
  const token = getToken();
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {})
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(url, {
    ...options,
    headers
  });

  if (!response.ok) {
    let message = "No se pudo completar la operación";
    try {
      const body = await response.json();
      message = body.details?.join(". ") || body.error || message;
    } catch (_e) {
      // ignored
    }
    const error = new Error(message);
    error.status = response.status;
    throw error;
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}
