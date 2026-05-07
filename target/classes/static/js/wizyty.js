// --- ZMIENNE GLOBALNE ---
const form = document.getElementById('form-wizyta');
const selectKlient = document.getElementById('klientSelect');
const selectFryzjer = document.getElementById('fryzjerSelect');
const wrapper = document.getElementById('uslugiWrapper');
const btnText = document.getElementById('btnText');
const listaUslug = document.getElementById('listaUslug');
const tabela = document.getElementById('tabelaWizyt');
const totalCzas = document.getElementById('totalCzas');
const totalCena = document.getElementById('totalCena');

// Mapy do zamiany ID na Nazwy (do tabeli)
let mapaKlientow = {};
let mapaFryzjerow = {};

// --- START ---
document.addEventListener('DOMContentLoaded', async () => {
    // 1. Pobierz wszystko na raz
    try {
        const [kRes, fRes, uRes, wRes] = await Promise.all([
            fetch(`${API_URL}/klienci`),
            fetch(`${API_URL}/fryzjerzy`),
            fetch(`${API_URL}/uslugi`),
            fetch(`${API_URL}/wizyty`)
        ]);

        const klienci = await kRes.json();
        const fryzjerzy = await fRes.json();
        const uslugi = await uRes.json();
        const wizyty = await wRes.json();

        // 2. Wypełnij Selecty
        wypelnijKlientow(klienci);
        wypelnijFryzjerow(fryzjerzy);
        stworzMapeNazw(klienci, fryzjerzy);

        // 3. Zbuduj naszą listę rozwijaną usług
        budujMultiselect(uslugi);

        // 4. Wyświetl tabelę wizyt
        rysujTabele(wizyty);

    } catch (err) {
        console.error("Błąd ładowania:", err);
    }
});

// --- LOGIKA MULTISELECTA ---

// Otwieranie/zamykanie po kliknięciu w przycisk
wrapper.addEventListener('click', (e) => {
    // Jeśli kliknięto wewnątrz listy (np. w checkbox), nie zamykaj
    if (e.target.closest('.list-items')) return;
    wrapper.classList.toggle('open');
});

// Zamykanie jak klikniesz poza komponentem
document.addEventListener('click', (e) => {
    if (!wrapper.contains(e.target)) {
        wrapper.classList.remove('open');
    }
});

function budujMultiselect(uslugi) {
    listaUslug.innerHTML = '';

    if (uslugi.length === 0) {
        listaUslug.innerHTML = '<div style="padding:10px;">Brak usług</div>';
        return;
    }

    uslugi.forEach(u => {
        // Tworzymy element opcji
        const item = document.createElement('div');
        item.className = 'item';

        item.innerHTML = `
            <input type="checkbox" class="checkbox" value="${u.uslugaId}"
                   data-czas="${u.czasTrwaniaMin}" data-cena="${u.cenaNetto}" data-nazwa="${u.nazwa}">
            <span class="item-text">${u.nazwa}</span>
            <span class="item-price">${u.cenaNetto} zł, ${u.czasTrwaniaMin} min</span>
        `;

        // Obsługa kliknięcia w wiersz (żeby nie trzeba było celować w sam checkbox)
        item.addEventListener('click', (e) => {
            // Jeśli kliknięto w sam checkbox, pozwól mu działać domyślnie
            if (e.target.classList.contains('checkbox')) {
                aktualizujPodsumowanie();
                return;
            }

            // Jeśli kliknięto w tekst, przełącz checkbox
            const cb = item.querySelector('.checkbox');
            cb.checked = !cb.checked;
            aktualizujPodsumowanie();
        });

        listaUslug.appendChild(item);
    });
}

function aktualizujPodsumowanie() {
    const checkedBoxes = listaUslug.querySelectorAll('.checkbox:checked');

    // 1. Aktualizuj tekst na przycisku
    if (checkedBoxes.length > 0) {
        if (checkedBoxes.length === 1) {
            btnText.innerText = checkedBoxes[0].dataset.nazwa;
        } else {
            btnText.innerText = `Wybrano usług: ${checkedBoxes.length}`;
        }
    } else {
        btnText.innerText = "Wybierz usługi...";
    }

    // 2. Policz czas i cenę
    let czas = 0;
    let cena = 0;
    checkedBoxes.forEach(cb => {
        czas += parseInt(cb.dataset.czas);
        cena += parseFloat(cb.dataset.cena);
    });

    totalCzas.innerText = czas;
    totalCena.innerText = cena.toFixed(2);
}

// --- FUNKCJE POMOCNICZE ---

function wypelnijKlientow(list) {
    list.forEach(k => {
        const opt = document.createElement('option');
        opt.value = k.klientId; // Zgodnie z KlientDTO
        opt.innerText = `${k.imie} ${k.nazwisko}`;
        selectKlient.appendChild(opt);
    });
}

function wypelnijFryzjerow(list) {
    list.forEach(f => {
        const opt = document.createElement('option');
        opt.value = f.fryzjerId; // Zgodnie z FryzjerDTO
        opt.innerText = `${f.imie} ${f.nazwisko}`;
        selectFryzjer.appendChild(opt);
    });
}

function stworzMapeNazw(klienci, fryzjerzy) {
    klienci.forEach(k => mapaKlientow[k.klientId] = `${k.imie} ${k.nazwisko}`);
    fryzjerzy.forEach(f => mapaFryzjerow[f.fryzjerId] = `${f.imie} ${f.nazwisko}`);
}

function rysujTabele(wizyty) {
    tabela.innerHTML = '';

    // Sortuj: najnowsze na górze
    wizyty.sort((a, b) => new Date(b.dataGodzinaRozpoczecia) - new Date(a.dataGodzinaRozpoczecia));

    wizyty.forEach(w => {
        const tr = document.createElement('tr');

        // Data i Godzina (ładne formatowanie)
        const dataStr = new Date(w.dataGodzinaRozpoczecia).toLocaleString('pl-PL', {
            year: 'numeric', month: '2-digit', day: '2-digit',
            hour: '2-digit', minute: '2-digit'
        });

        const klient = mapaKlientow[w.klientId] || `ID: ${w.klientId}`;
        const fryzjer = mapaFryzjerow[w.fryzjerId] || `ID: ${w.fryzjerId}`;

        tr.innerHTML = `
            <td>${dataStr}</td>
            <td>${klient}</td>
            <td>${fryzjer}</td>
            <td>
                <button class="btn-usun" onclick="usunWizyte(${w.wizytaId})">Anuluj</button>
            </td>
        `;
        tabela.appendChild(tr);
    });
}


form.addEventListener('submit', async (e) => {
    e.preventDefault();

    // 1. Walidacja usług
    const wybraneUslugiIds = [];
    listaUslug.querySelectorAll('.checkbox:checked').forEach(cb => {
        wybraneUslugiIds.push(parseInt(cb.value));
    });

    if (wybraneUslugiIds.length === 0) {
        alert("Musisz wybrać przynajmniej jedną usługę!");
        return;
    }

    // --- 2. WALIDACJA DATY I GODZINY (NAPRAWA 00:00) ---
    const dataInput = document.getElementById('dataWizyty').value;
    if (!dataInput) {
        alert("Wybierz datę i godzinę!");
        return;
    }

    const dataStart = new Date(dataInput);
    const godzina = dataStart.getHours();

    // SPRAWDZENIE: Czy godzina jest między 10 a 18?
    if (godzina < 10 || godzina >= 18) {
        alert("BŁĄD: Zakład jest otwarty w godzinach 10:00 - 18:00.\nWybrałeś godzinę: " + godzina + ":00.\nProszę zmienić czas wizyty.");
        return; // Zatrzymujemy funkcję, nic się nie wyśle!
    }

    // SPRAWDZENIE: Czy wizyta nie wykracza poza zamknięcie?
    const czasTrwania = parseInt(totalCzas.innerText);
    const dataKoniec = new Date(dataStart.getTime() + czasTrwania * 60000);

    // Jeśli koniec jest po 18:00 LUB jest równo 18:00 ale są jakieś minuty (np 18:05)
    if (dataKoniec.getHours() > 18 || (dataKoniec.getHours() === 18 && dataKoniec.getMinutes() > 0)) {
        alert(`BŁĄD: Ta wizyta skończyłaby się o ${dataKoniec.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}.\nZamykamy o 18:00!`);
        return;
    }
    // ----------------------------------------------------

    // --- 3. SPRAWDZENIE KONFLIKTÓW (Czy fryzjer jest wolny?) ---
    const fryzjerId = parseInt(selectFryzjer.value);

    try {
        // Pobieramy aktualne wizyty, żeby sprawdzić kolizje
        const wRes = await fetch(`${API_URL}/wizyty`);
        const wszystkieWizyty = await wRes.json();

        // Filtrujemy wizyty tego konkretnego fryzjera
        const wizytyFryzjera = wszystkieWizyty.filter(w => w.fryzjerId === fryzjerId);

        const nowyStart = dataStart.getTime();
        const nowyKoniec = dataKoniec.getTime();

        let konflikt = false;

        for (const w of wizytyFryzjera) {
            const istniejacyStart = new Date(w.dataGodzinaRozpoczecia).getTime();
            const istniejacyKoniec = istniejacyStart + (w.czasTrwaniaCalkowity * 60000);

            // Warunek nakładania się przedziałów: (StartA < EndB) && (EndA > StartB)
            if (nowyStart < istniejacyKoniec && nowyKoniec > istniejacyStart) {
                konflikt = true;
                break;
            }
        }

        if (konflikt) {
            alert("BŁĄD: Wybrany fryzjer ma już w tym czasie inną wizytę!\nProszę wybrać inną godzinę lub innego fryzjera.");
            return;
        }

    } catch (err) {
        console.error("Błąd sprawdzania dostępności:", err);
        alert("Nie udało się sprawdzić dostępności fryzjera.");
        return;
    }
    // ----------------------------------------------------

    // Obiekt WIZYTA DTO
    const nowaWizyta = {
        klientId: parseInt(selectKlient.value),
        fryzjerId: parseInt(selectFryzjer.value),
        dataGodzinaRozpoczecia: dataInput,
        uslugiIds: wybraneUslugiIds,
        czasTrwaniaCalkowity: czasTrwania
    };

    console.log("Wysyłam:", nowaWizyta);

    try {
        const res = await fetch(`${API_URL}/wizyty`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(nowaWizyta)
        });

        if (res.ok) {
            alert("Sukces! Wizyta umówiona.");
            location.reload();
        } else {
            const txt = await res.text();
            alert("Błąd serwera: " + txt);
        }
    } catch (err) {
        console.error(err);
        alert("Błąd połączenia z serwerem.");
    }
});

// --- USUWANIE (DELETE) ---
window.usunWizyte = async (id) => {
    if (!confirm("Czy na pewno chcesz anulować wizytę?")) return;

    try {
        const res = await fetch(`${API_URL}/wizyty/${id}`, { method: 'DELETE' });
        if (res.ok) location.reload();
        else alert("Nie udało się usunąć.");
    } catch (err) { console.error(err); }
};