// auth.js - Globalny guard sesji dla wszystkich stron panelu

const AUTH_API = '/api/auth';

/**
 * Sprawdza czy użytkownik jest zalogowany.
 * Jeśli nie - przekierowuje na login.html
 */
async function sprawdzSesje() {
    try {
        const resp = await fetch(`${AUTH_API}/me`, {
            credentials: 'include'
        });
        if (resp.status === 401) {
            window.location.replace('index.html');
            return null;
        }
        const data = await resp.json();
        // Wyświetl nazwę zalogowanego użytkownika w nav (jeśli istnieje element)
        const span = document.getElementById('nav-username');
        if (span) span.textContent = `${data.username} (${data.rola})`;
        return data;
    } catch (e) {
        console.error('Błąd sprawdzania sesji:', e);
        window.location.replace('index.html');
        return null;
    }
}

/**
 * Wylogowuje użytkownika i przekierowuje na index.html
 */
async function wyloguj() {
    await fetch(`${AUTH_API}/logout`, {
        method: 'POST',
        credentials: 'include'
    });
    window.location.replace('index.html');
}
