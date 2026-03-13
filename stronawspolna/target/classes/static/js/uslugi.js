const tabela = document.getElementById('lista-uslug');
const formularz = document.getElementById('form-usluga');

// Start
document.addEventListener('DOMContentLoaded', pobierzUslugi);

// 1. POBIERANIE (GET)
async function pobierzUslugi() {
    try {
        const response = await fetch(`${API_URL}/uslugi`);
        if (!response.ok) throw new Error("Błąd pobierania");
        const data = await response.json();
        generujTabele(data);
    } catch (err) {
        console.error(err);
    }
}

function generujTabele(uslugi) {
    tabela.innerHTML = '';

    uslugi.forEach(u => {
        const tr = document.createElement('tr');

        // Pola zgodne z UslugaDTO.java
        tr.innerHTML = `
            <td><strong>${u.nazwa}</strong></td>
            <td>${u.opis || '-'}</td>
            <td>${u.cenaNetto} zł</td>
            <td>${u.stawkaVat}%</td>
            <td>${u.czasTrwaniaMin} min</td>
            <td>
                <button onclick="usunUsluge(${u.uslugaId})" class="btn-usun">Usuń</button>
            </td>
        `;
        tabela.appendChild(tr);
    });
}

// 2. DODAWANIE (POST)
formularz.addEventListener('submit', async (e) => {
    e.preventDefault();

    // TWORZYMY OBIEKT ZGODNY Z JAVA DTO
    const nowaUsluga = {
        nazwa: document.getElementById('nazwa').value,
        opis: document.getElementById('opis').value,
        // Parsujemy liczby, bo input zwraca tekst
        cenaNetto: parseFloat(document.getElementById('cenaNetto').value),
        stawkaVat: parseFloat(document.getElementById('stawkaVat').value),
        czasTrwaniaMin: parseInt(document.getElementById('czasTrwaniaMin').value)
    };

    console.log("Wysyłam:", nowaUsluga);

    try {
        const response = await fetch(`${API_URL}/uslugi`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(nowaUsluga)
        });

        if (response.ok) {
            alert('Dodano usługę!');
            formularz.reset();
            pobierzUslugi();
        } else {
            const text = await response.text();
            alert('Błąd serwera: ' + response.status + '\n' + text);
        }
    } catch (err) {
        console.error(err);
        alert("Błąd połączenia.");
    }
});

// 3. USUWANIE (DELETE)
window.usunUsluge = async (id) => {
    if(!confirm('Usunąć tę usługę?')) return;

    try {
        const response = await fetch(`${API_URL}/uslugi/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            pobierzUslugi();
        } else {
            alert('Nie udało się usunąć (być może usługa jest używana w wizytach).');
        }
    } catch (err) {
        console.error(err);
    }
};