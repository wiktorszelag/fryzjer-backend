const tabela = document.getElementById('tabela-fryzjerow');
const formularz = document.getElementById('form-fryzjer');
const selectUslugi = document.getElementById('specjalizacjaSelect');

// Na start ładujemy dwie rzeczy: listę usług (do selecta) i listę fryzjerów (do tabeli)
document.addEventListener('DOMContentLoaded', () => {
    pobierzUslugiDoWyboru();
    pobierzFryzjerow();
});

// 1. POBIERANIE USŁUG (żeby wypełnić listę rozwijaną)
async function pobierzUslugiDoWyboru() {
    try {
        const res = await fetch(`${API_URL}/uslugi`);
        if (!res.ok) throw new Error("Błąd pobierania usług");
        const uslugi = await res.json();

        selectUslugi.innerHTML = '<option value="" disabled selected>-- Wybierz specjalizację --</option>';

        uslugi.forEach(u => {
            const option = document.createElement('option');
            // Backend w FryzjerDTO oczekuje Stringa "specjalizacja", więc wysyłamy nazwę usługi
            option.value = u.nazwa;
            option.textContent = u.nazwa;
            selectUslugi.appendChild(option);
        });

    } catch (err) {
        console.error(err);
        selectUslugi.innerHTML = '<option value="">Błąd ładowania listy</option>';
    }
}

// 2. POBIERANIE FRYZJERÓW (GET)
async function pobierzFryzjerow() {
    try {
        const res = await fetch(`${API_URL}/fryzjerzy`);
        if (!res.ok) throw new Error("Błąd pobierania fryzjerów");
        const data = await res.json();
        generujWiersze(data);
    } catch (err) {
        console.error(err);
    }
}

function generujWiersze(fryzjerzy) {
    tabela.innerHTML = '';
    fryzjerzy.forEach(f => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><strong>${f.imie} ${f.nazwisko}</strong></td>
            <td>${f.specjalizacja || '-'}</td>
            <td>${f.telefon}</td>
            <td>${f.dataZatrudnienia}</td>
            <td>
                <button onclick="usunFryzjera(${f.fryzjerId})" class="btn-usun">Zwolnij</button>
            </td>
        `;
        tabela.appendChild(row);
    });
}

// 3. DODAWANIE (POST)
formularz.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Obiekt zgodny z FryzjerDTO.java
    const nowyFryzjer = {
        imie: document.getElementById('imie').value,
        nazwisko: document.getElementById('nazwisko').value,
        telefon: document.getElementById('telefon').value,
        dataZatrudnienia: document.getElementById('dataZatrudnienia').value,
        specjalizacja: selectUslugi.value // Tu wpadnie nazwa wybranej usługi
    };

    console.log("Wysyłam:", nowyFryzjer);

    try {
        const res = await fetch(`${API_URL}/fryzjerzy`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(nowyFryzjer)
        });

        if (res.ok) {
            alert('Dodano fryzjera!');
            formularz.reset();
            // Po resetowaniu select wraca do stanu początkowego, więc warto go "odświeżyć" wizualnie
            selectUslugi.value = "";
            pobierzFryzjerow();
        } else {
            const text = await res.text();
            alert('Błąd serwera: ' + res.status + '\n' + text);
        }
    } catch (err) {
        console.error(err);
        alert("Błąd połączenia.");
    }
});

// 4. USUWANIE (DELETE)
window.usunFryzjera = async (id) => {
    if(!confirm("Czy na pewno chcesz usunąć tego pracownika?")) return;

    try {
        const res = await fetch(`${API_URL}/fryzjerzy/${id}`, { method: 'DELETE' });
        if(res.ok) {
            pobierzFryzjerow();
        } else {
            alert("Nie udało się usunąć fryzjera (być może ma przypisane wizyty).");
        }
    } catch (err) {
        console.error(err);
    }
};