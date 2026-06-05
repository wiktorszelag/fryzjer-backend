document.addEventListener('DOMContentLoaded', async function () {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    let currentKlient = null;
    let selectedDate = null;
    let selectedFryzjerId = null;
    let selectedSlot = null;
    let services = [];
    let scheduledWorkHours = []; // Harmonogram na wybrany dzień
    let existingBookings = []; // Wszystkie wizyty
    let fryzjerzyList = [];

    // Elements
    const welcomeUser = document.getElementById('welcomeUser');
    const selectedDateText = document.getElementById('selectedDateText');
    const servicesList = document.getElementById('servicesList');
    const fryzjerSelect = document.getElementById('fryzjerSelect');
    const slotsContainer = document.getElementById('slotsContainer');
    const totalDurationText = document.getElementById('totalDuration');
    const totalPriceText = document.getElementById('totalPrice');
    const summaryBadge = document.getElementById('summaryBadge');
    const bookBtn = document.getElementById('bookBtn');
    const myAppointmentsTable = document.getElementById('myAppointmentsTable');

    // 1. Fetch Client Profile (Me)
    try {
        const meRes = await fetch(`${API_URL}/klienci/me`, {
            headers: { 'Authorization': 'Bearer ' + token },
            credentials: 'include'
        });
        if (!meRes.ok) {
            // Token expired or invalid
            localStorage.removeItem('token');
            window.location.href = 'login.html';
            return;
        }
        currentKlient = await meRes.json();
        welcomeUser.innerText = `Witaj, ${currentKlient.imie} ${currentKlient.nazwisko || ''}! Twój Panel Klienta`;
        
        // Load Client Appointments
        loadClientAppointments();
    } catch (err) {
        console.error("Błąd autoryzacji:", err);
        window.location.href = 'login.html';
        return;
    }

    // 2. Fetch Services
    try {
        const servicesRes = await fetch(`${API_URL}/uslugi`);
        services = await servicesRes.json();
        renderServices();
    } catch (err) {
        console.error("Błąd ładowania usług:", err);
    }

    // 3. Fetch Fryzjerzy
    try {
        const fRes = await fetch(`${API_URL}/fryzjerzy`);
        fryzjerzyList = await fRes.json();
    } catch (err) {
        console.error("Błąd pobierania fryzjerów:", err);
    }

    // Render list of services
    function renderServices() {
        servicesList.innerHTML = '';
        services.forEach(u => {
            const item = document.createElement('div');
            item.className = 'service-item';
            item.innerHTML = `
                <div style="display:flex; align-items:center;">
                    <input type="checkbox" value="${u.id}" class="service-checkbox" data-duration="${u.czasTrwaniaMin}" data-price="${u.cenaNetto}">
                    <span class="service-info"><strong>${u.nazwa}</strong><br><small style="color:#aaa;">${u.opis || ''}</small></span>
                </div>
                <span class="service-meta">${u.cenaNetto.toFixed(2)} zł (${u.czasTrwaniaMin} min)</span>
            `;
            
            // Allow clicking the item to toggle checkbox
            item.addEventListener('click', (e) => {
                if (e.target.tagName !== 'INPUT') {
                    const cb = item.querySelector('input');
                    cb.checked = !cb.checked;
                }
                updateSummary();
            });
            servicesList.appendChild(item);
        });
    }

    // Update selected services summary
    function updateSummary() {
        const checkboxes = document.querySelectorAll('.service-checkbox:checked');
        let totalDuration = 0;
        let totalPrice = 0;

        checkboxes.forEach(cb => {
            totalDuration += parseInt(cb.dataset.duration);
            totalPrice += parseFloat(cb.dataset.price);
        });

        totalDurationText.innerText = totalDuration;
        totalPriceText.innerText = totalPrice.toFixed(2);

        if (checkboxes.length > 0) {
            summaryBadge.style.display = 'block';
        } else {
            summaryBadge.style.display = 'none';
        }

        // Recalculate slots if hairdresser and day are selected
        if (selectedDate && selectedFryzjerId) {
            generateTimeSlots();
        } else {
            validateBookingState();
        }
    }

    function getSelectedDuration() {
        const checkboxes = document.querySelectorAll('.service-checkbox:checked');
        let duration = 0;
        checkboxes.forEach(cb => duration += parseInt(cb.dataset.duration));
        return duration;
    }

    // 4. Initialize Calendar
    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'pl',
        firstDay: 1,
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: ''
        },
        selectable: true,
        selectAllow: function (selectInfo) {
            // Block selecting days in the past
            const today = new Date();
            today.setHours(0,0,0,0);
            return selectInfo.start >= today;
        },
        dateClick: function (info) {
            const today = new Date();
            today.setHours(0,0,0,0);
            const clickedDate = new Date(info.dateStr);
            if (clickedDate < today) {
                alert("Nie możesz zarezerwować wizyty w przeszłości.");
                return;
            }
            
            selectedDate = info.dateStr;
            selectedDateText.innerText = new Date(selectedDate).toLocaleDateString('pl-PL', {
                weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
            });

            // Highlight selected day on FullCalendar
            document.querySelectorAll('.fc-daygrid-day').forEach(el => {
                el.style.backgroundColor = '';
            });
            info.dayEl.style.backgroundColor = 'rgba(212, 175, 55, 0.15)';

            // Fetch schedules and bookings for this day
            onDateSelected();
        }
    });
    calendar.render();

    // Triggered when a valid day is clicked
    async function onDateSelected() {
        try {
            // Disable hairdresser selection initially
            fryzjerSelect.disabled = true;
            fryzjerSelect.innerHTML = '<option value="">-- Ładowanie grafiku... --</option>';
            slotsContainer.innerHTML = '<span style="color:#aaa;">Wybierz fryzjera, aby zobaczyć wolne godziny.</span>';
            selectedSlot = null;

            // Fetch schedule for the selected date
            const schedRes = await fetch(`${API_URL}/harmonogram/data/${selectedDate}`);
            scheduledWorkHours = await schedRes.json();

            // Fetch all bookings
            const bookingsRes = await fetch(`${API_URL}/wizyty`);
            existingBookings = await bookingsRes.json();

            if (scheduledWorkHours.length === 0) {
                fryzjerSelect.innerHTML = '<option value="">Brak pracujących fryzjerów w tym dniu</option>';
                slotsContainer.innerHTML = '<span style="color:#ff4d4d;">Brak dostępnych terminów w tym dniu. Spróbuj wybrać inną datę.</span>';
                validateBookingState();
                return;
            }

            // Populate fryzjer select
            fryzjerSelect.innerHTML = '<option value="">-- Wybierz fryzjera --</option>';
            scheduledWorkHours.forEach(sch => {
                const fryzjer = fryzjerzyList.find(f => f.id === sch.fryzjerId);
                if (fryzjer) {
                    const opt = document.createElement('option');
                    opt.value = fryzjer.id;
                    opt.innerText = `${fryzjer.imie} ${fryzjer.nazwisko} (${sch.godzinaOd.slice(0, 5)} - ${sch.godzinaDo.slice(0, 5)})`;
                    fryzjerSelect.appendChild(opt);
                }
            });

            fryzjerSelect.disabled = false;
        } catch (err) {
            console.error("Błąd pobierania danych na dany dzień:", err);
        }
    }

    // Fryzjer selection change
    fryzjerSelect.addEventListener('change', function () {
        selectedFryzjerId = this.value ? parseInt(this.value) : null;
        selectedSlot = null;
        generateTimeSlots();
    });

    // Generate dynamic available time slots
    function generateTimeSlots() {
        slotsContainer.innerHTML = '';
        
        if (!selectedDate || !selectedFryzjerId) {
            slotsContainer.innerHTML = '<span style="color:#aaa;">Wybierz fryzjera, aby zobaczyć wolne godziny.</span>';
            validateBookingState();
            return;
        }

        const duration = getSelectedDuration();
        if (duration <= 0) {
            slotsContainer.innerHTML = '<span style="color:#ffc107;">Wybierz przynajmniej jedną usługę, aby obliczyć wolne godziny.</span>';
            validateBookingState();
            return;
        }

        // Find employee schedule for this day
        const schedule = scheduledWorkHours.find(s => s.fryzjerId === selectedFryzjerId);
        if (!schedule) {
            slotsContainer.innerHTML = '<span style="color:#ff4d4d;">Wybrany pracownik nie pracuje w tym dniu.</span>';
            validateBookingState();
            return;
        }

        // Parse work hours
        const [startH, startM] = schedule.godzinaOd.split(':').map(Number);
        const [endH, endM] = schedule.godzinaDo.split(':').map(Number);

        let workStartMinutes = startH * 60 + startM;
        let workEndMinutes = endH * 60 + endM;

        // Parse existing bookings for this hairdresser on this date
        const dayBookings = existingBookings.filter(b => {
            if (b.fryzjerId !== selectedFryzjerId) return false;
            // Compare dates
            const bookingDate = b.dataGodzinaRozpoczecia.slice(0, 10);
            return bookingDate === selectedDate;
        }).map(b => {
            const timePart = b.dataGodzinaRozpoczecia.slice(11, 16);
            const [h, m] = timePart.split(':').map(Number);
            const startMin = h * 60 + m;
            const roundedBookingDuration = Math.ceil(b.czasTrwaniaCalkowity / 30) * 30;
            return {
                start: startMin,
                end: startMin + roundedBookingDuration
            };
        });

        // Current time threshold for today's slots
        const now = new Date();
        const isToday = (selectedDate === now.toISOString().slice(0, 10));
        const currentMinutes = now.getHours() * 60 + now.getMinutes();

        // Generate 30-min intervals rounded up to the nearest 30 mins
        let slotsCount = 0;
        const roundedDuration = Math.ceil(duration / 30) * 30;
        for (let time = workStartMinutes; time <= workEndMinutes - roundedDuration; time += 30) {
            // If today, filter out past times
            if (isToday && time <= currentMinutes + 15) { // 15 mins buffer
                continue;
            }

            const candidateEnd = time + roundedDuration;

            // Check overlap with existing bookings
            let hasOverlap = false;
            for (let booking of dayBookings) {
                if (time < booking.end && candidateEnd > booking.start) {
                    hasOverlap = true;
                    break;
                }
            }

            if (!hasOverlap) {
                // Render slot button
                const hour = String(Math.floor(time / 60)).padStart(2, '0');
                const minute = String(time % 60).padStart(2, '0');
                const timeString = `${hour}:${minute}`;

                const btn = document.createElement('button');
                btn.type = 'button';
                btn.className = 'slot-btn';
                btn.innerText = timeString;
                btn.addEventListener('click', function () {
                    document.querySelectorAll('.slot-btn').forEach(b => b.classList.remove('active'));
                    btn.classList.add('active');
                    selectedSlot = timeString;
                    validateBookingState();
                });

                slotsContainer.appendChild(btn);
                slotsCount++;
            }
        }

        if (slotsCount === 0) {
            slotsContainer.innerHTML = '<span style="color:#ff4d4d;">Brak wolnych godzin u tego pracownika w tym dniu. Wybierz inną datę lub innego fryzjera.</span>';
        }
        
        validateBookingState();
    }

    function validateBookingState() {
        const duration = getSelectedDuration();
        if (selectedDate && selectedFryzjerId && selectedSlot && duration > 0) {
            bookBtn.disabled = false;
        } else {
            bookBtn.disabled = true;
        }
    }

    // Submit booking
    window.submitBooking = async function () {
        const duration = getSelectedDuration();
        if (!selectedDate || !selectedFryzjerId || !selectedSlot || duration <= 0) return;

        bookBtn.disabled = true;
        bookBtn.innerText = 'Rezerwowanie...';

        const dataGodzina = `${selectedDate}T${selectedSlot}:00`;

        const payload = {
            klientId: currentKlient.id,
            fryzjerId: selectedFryzjerId,
            dataGodzinaRozpoczecia: dataGodzina,
            czasTrwaniaCalkowity: duration,
            imieKlientaZUlicy: null
        };

        try {
            const res = await fetch(`${API_URL}/wizyty`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                alert("Rezerwacja zakończona sukcesem!");
                // Clear selection
                selectedSlot = null;
                // Reload
                onDateSelected();
                loadClientAppointments();
            } else {
                alert("Nie udało się utworzyć rezerwacji. Spróbuj ponownie.");
                bookBtn.disabled = false;
                bookBtn.innerText = 'Rezerwuj Wizytę';
            }
        } catch (err) {
            console.error("Błąd podczas rezerwacji:", err);
            alert("Błąd połączenia z serwerem.");
            bookBtn.disabled = false;
            bookBtn.innerText = 'Rezerwuj Wizytę';
        }
    };

    // Load logged-in client appointments
    async function loadClientAppointments() {
        if (!currentKlient) return;

        try {
            const res = await fetch(`${API_URL}/wizyty/klient/${currentKlient.id}`);
            const wizyty = await res.json();
            
            // Fetch fryzjerzy to map IDs to names
            const fRes = await fetch(`${API_URL}/fryzjerzy`);
            const fryzjerzy = await fRes.json();
            const fMap = {};
            fryzjerzy.forEach(f => fMap[f.id] = `${f.imie} ${f.nazwisko}`);

            // Sort appointments descending
            wizyty.sort((a, b) => new Date(b.dataGodzinaRozpoczecia) - new Date(a.dataGodzinaRozpoczecia));

            myAppointmentsTable.innerHTML = '';
            if (wizyty.length === 0) {
                myAppointmentsTable.innerHTML = `<tr><td colspan="4" style="text-align: center; color: #888;">Brak zaplanowanych wizyt.</td></tr>`;
                return;
            }

            wizyty.forEach(w => {
                const tr = document.createElement('tr');
                const dateObj = new Date(w.dataGodzinaRozpoczecia);
                const dateStr = dateObj.toLocaleString('pl-PL', {
                    year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'
                });

                const fryzjerName = fMap[w.fryzjerId] || `Fryzjer #${w.fryzjerId}`;

                tr.innerHTML = `
                    <td>${dateStr}</td>
                    <td>${fryzjerName}</td>
                    <td>${w.czasTrwaniaCalkowity} min</td>
                    <td>
                        <button class="cancel-btn" onclick="cancelAppointment(${w.id})">Anuluj</button>
                    </td>
                `;
                myAppointmentsTable.appendChild(tr);
            });

        } catch (err) {
            console.error("Błąd ładowania wizyt klienta:", err);
        }
    }

    // Cancel appointment
    window.cancelAppointment = async function (id) {
        if (!confirm("Czy na pewno chcesz anulować tę rezerwację?")) return;

        try {
            const res = await fetch(`${API_URL}/wizyty/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': 'Bearer ' + token }
            });

            if (res.ok) {
                alert("Wizyta została pomyślnie anulowana.");
                loadClientAppointments();
                if (selectedDate) {
                    onDateSelected();
                }
            } else {
                alert("Nie udało się anulować wizyty.");
            }
        } catch (err) {
            console.error("Błąd podczas usuwania wizyty:", err);
            alert("Błąd połączenia z serwerem.");
        }
    };
});
