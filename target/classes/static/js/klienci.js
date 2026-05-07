const tabela = document.getElementById('tabela-klientow');
const formularz = document.getElementById('form-klient');
const btnAnuluj = document.getElementById('btn-anuluj');
const naglowekFormularza = document.getElementById('form-naglowek');

// Pola formularza (USUNĄŁEM EMAIL, bo nie ma go w Javie)
const idInput = document.getElementById('klientId');
const imieInput = document.getElementById('imie');
const nazwiskoInput = document.getElementById('nazwisko');
const telefonInput = document.getElementById('telefon');
// const emailInput - usuwamy, bo DTO nie ma emaila

// START
document.addEventListener('DOMContentLoaded', pobierzKlientow);

// 1. POBIERANIE (GET)
async function pobierzKlientow() {
    try {
        const res = await fetch(`${API_URL}/klienci`);
        if (!res.ok) throw new Error(`Błąd pobierania: ${res.status}`);
        const data = await res.json();
        generujTabele(data);
    } catch (err) {
        console.error(err);
    }
}

function generujTabele(klienci) {
    tabela.innerHTML = '';
    klienci.forEach(k => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${k.imie} ${k.nazwisko}</td>
            <td>${k.telefon}</td>
            <td>
                <div class="actions">
                    <button onclick='rozpocznijEdycje(${JSON.stringify(k)})' class="btn-table btn-edit">Edytuj</button>

                    <button onclick="usunKlienta(${k.klientId})" class="btn-table btn-delete">Usuń</button>
                </div>
            </td>
        `;
        tabela.appendChild(tr);
    });
}
// 2. DODAWANIE (POST) LUB EDYCJA (PUT)
formularz.addEventListener('submit', async (e) => {
    e.preventDefault();

    // DANE ZGODNE Z KlientDTO.java
    const klientData = {
        imie: imieInput.value,
        nazwisko: nazwiskoInput.value,
        telefon: telefonInput.value
        // dataRejestracji zazwyczaj ustawia backend automatycznie, więc jej nie wysyłamy
    };

    const id = idInput.value;

    // Ważne: W DTO pole ID nazywa się 'klientId', a nie 'id'.
    // Przy edycji musimy wiedzieć, czy backend wymaga ID w body czy tylko w URL.
    // Zazwyczaj w PUT wystarczy w URL, ale dla pewności przy edycji dodajemy:
    if (id) {
        klientData.klientId = parseInt(id);
    }

    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API_URL}/klienci/${id}` : `${API_URL}/klienci`;

    console.log("Wysyłam:", JSON.stringify(klientData));

    try {
        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(klientData)
        });

        if (res.ok) {
            alert(id ? 'Zaktualizowano klienta!' : 'Dodano klienta!');
            resetFormularza();
            pobierzKlientow();
        } else {
            const text = await res.text();
            console.error("Błąd serwera:", text);
            alert('Błąd zapisu! Kod: ' + res.status);
        }
    } catch (err) {
        console.error("Błąd połączenia:", err);
    }
});

// 3. USUWANIE (DELETE)
window.usunKlienta = async (id) => {
    if(!confirm('Na pewno usunąć klienta?')) return;
    try {
        const res = await fetch(`${API_URL}/klienci/${id}`, { method: 'DELETE' });
        if (res.ok) {
            pobierzKlientow();
        } else {
            alert("Błąd usuwania.");
        }
    } catch (err) { console.error(err); }
};

// 4. TRYB EDYCJI
window.rozpocznijEdycje = (klient) => {
    // Uwaga: w DTO pole ID to 'klientId', upewnij się że backend zwraca json z takim kluczem
    idInput.value = klient.klientId || klient.id;
    imieInput.value = klient.imie;
    nazwiskoInput.value = klient.nazwisko;
    telefonInput.value = klient.telefon;

    naglowekFormularza.innerText = "Edytuj klienta: " + klient.imie;
    btnAnuluj.style.display = 'inline-block';
    // Przewiń do góry strony
    document.documentElement.scrollTop = 0;
};

// 5. ANULOWANIE
btnAnuluj.addEventListener('click', resetFormularza);

function resetFormularza() {
    formularz.reset();
    idInput.value = '';
    naglowekFormularza.innerText = "Dodaj nowego klienta";
    btnAnuluj.style.display = 'none';
}