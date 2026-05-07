document.addEventListener('DOMContentLoaded', async function () {
    const calendarEl = document.getElementById('calendar');
    const fryzjerFilter = document.getElementById('fryzjerFilter');
    let calendar;
    let allEvents = [];
    const fryzjerColors = ['#007bff', '#28a745', '#dc3545', '#ffc107', '#17a2b8', '#6610f2', '#fd7e14', '#20c997'];

    // 1. Pobieramy wszystkie potrzebne dane
    try {
        const [wizytyRes, klienciRes, fryzjerzyRes] = await Promise.all([
            fetch(`${API_URL}/wizyty`),
            fetch(`${API_URL}/klienci`),
            fetch(`${API_URL}/fryzjerzy`)
        ]);

        const wizyty = await wizytyRes.json();
        const klienci = await klienciRes.json();
        const fryzjerzy = await fryzjerzyRes.json();

        // 2. Tworzymy mapy ID -> Imię Nazwisko (żeby szybko szukać)
        const mapaKlientow = {};
        klienci.forEach(k => mapaKlientow[k.klientId] = `${k.imie} ${k.nazwisko}`);

        const mapaFryzjerow = {};
        fryzjerzy.forEach((f, index) => {
            mapaFryzjerow[f.fryzjerId] = {
                nazwa: `${f.imie} ${f.nazwisko}`,
                color: fryzjerColors[index % fryzjerColors.length]
            };

            // Dodaj do filtra
            const opt = document.createElement('option');
            opt.value = f.fryzjerId;
            opt.innerText = `${f.imie} ${f.nazwisko}`;
            fryzjerFilter.appendChild(opt);
        });

        // 3. Konwertujemy Twoje wizyty na format FullCalendar
        allEvents = wizyty.map(w => {
            // Obliczamy datę końca na podstawie czasu trwania
            // Data startu przychodzi z backendu (ISO String)
            const startDate = new Date(w.dataGodzinaRozpoczecia);
            // Dodajemy minuty do daty startu
            const endDate = new Date(startDate.getTime() + w.czasTrwaniaCalkowity * 60000);

            const klient = mapaKlientow[w.klientId] || 'Nieznany klient';
            const fryzjerInfo = mapaFryzjerow[w.fryzjerId] || { nazwa: 'Nieznany fryzjer', color: '#6c757d' };

            return {
                id: w.wizytaId,
                title: `${klient} (Fryzjer: ${fryzjerInfo.nazwa})`,
                start: w.dataGodzinaRozpoczecia,
                end: endDate.toISOString(), // Kalendarz potrzebuje ISO
                backgroundColor: fryzjerInfo.color, // Kolor kafelka
                borderColor: fryzjerInfo.color,
                extendedProps: {
                    fryzjerId: w.fryzjerId
                }
            };
        });

        // 4. Inicjalizujemy kalendarz
        calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'timeGridWeek', // Widok tygodniowy z godzinami
            locale: 'pl',               // Język polski
            firstDay: 1,                // Tydzień zaczyna się w poniedziałek
            headerToolbar: {
                left: 'prev,next today', // Przyciski w jednej sekcji po lewej
                center: 'title',
                right: 'dayGridMonth,timeGridWeek,timeGridDay'
            },
            buttonText: {
                today: 'Dzisiaj',
                month: 'Miesiąc',
                week: 'Tydzień',
                day: 'Dzień',
                list: 'Lista'
            },
            contentHeight: 'auto',      // Dopasuj wysokość do zawartości
            slotMinTime: '10:00:00',    // Start o 10:00
            slotMaxTime: '18:00:00',    // Koniec o 18:00
            slotDuration: '00:30:00',   // Siatka co 30 minut
            slotLabelInterval: '00:30', // Etykiety co 30 minut (10:00, 10:30...)
            slotLabelFormat: {
                hour: 'numeric',
                minute: '2-digit',
                omitZeroMinute: false,
                meridiem: 'short'
            },
            allDaySlot: false,          // Ukrywamy pasek "cały dzień"
            events: allEvents,         // Tutaj wrzucamy nasze przetworzone dane

            // Po kliknięciu w wizytę (opcjonalnie)
            eventClick: function (info) {
                alert('Szczegóły wizyty:\n' + info.event.title +
                    '\nStart: ' + info.event.start.toLocaleString());
            }
        });

        calendar.render();

        // Obsługa filtra
        fryzjerFilter.addEventListener('change', function () {
            const selectedId = this.value;

            if (selectedId === 'all') {
                // Pokaż wszystkie
                calendar.removeAllEvents();
                calendar.addEventSource(allEvents);
            } else {
                // Filtruj
                const filtered = allEvents.filter(e => e.extendedProps.fryzjerId == selectedId);
                calendar.removeAllEvents();
                calendar.addEventSource(filtered);
            }
        });

    } catch (err) {
        console.error("Błąd ładowania kalendarza:", err);
    }
});