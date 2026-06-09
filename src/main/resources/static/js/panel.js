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
    let selectedService = null;
    
    let services = [];
    let scheduledWorkHours = []; // Harmonogram na wybrany dzień
    let existingBookings = []; // Wszystkie wizyty wybranego fryzjera
    let fryzjerzyList = [];

    // Elements
    const welcomeUser = document.getElementById('welcomeUser');
    const selectedDateText = document.getElementById('selectedDateText');
    const serviceSelect = document.getElementById('serviceSelect');
    const fryzjerSelect = document.getElementById('fryzjerSelect');
    const timeSelect = document.getElementById('timeSelect');
    const totalDurationText = document.getElementById('totalDuration');
    const totalPriceText = document.getElementById('totalPrice');
    const summaryBadge = document.getElementById('summaryBadge');
    const bookBtn = document.getElementById('bookBtn');
    const myAppointmentsTable = document.getElementById('myAppointmentsTable');
    
    const reservationFormCard = document.getElementById('reservationFormCard');
    const placeholderCard = document.getElementById('placeholderCard');

    // 1. Fetch Client Profile (Me)
    try {
        const meRes = await fetch(`${API_URL}/klienci/me`, {
            headers: { 'Authorization': 'Bearer ' + token },
            credentials: 'include'
        });
        if (!meRes.ok) {
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

    // 2. Fetch Services and Populate Dropdown
    try {
        const servicesRes = await fetch(`${API_URL}/uslugi`);
        services = await servicesRes.json();
        populateServices();
    } catch (err) {
        console.error("Błąd ładowania usług:", err);
    }

    // 3. Fetch All Hairdressers (for metadata)
    try {
        const fRes = await fetch(`${API_URL}/fryzjerzy`);
        fryzjerzyList = await fRes.json();
    } catch (err) {
        console.error("Błąd pobierania fryzjerów:", err);
    }

    // Populate services select dropdown
    function populateServices() {
        serviceSelect.innerHTML = '<option value="">-- Wybierz usługę --</option>';
        services.forEach(u => {
            const opt = document.createElement('option');
            opt.value = u.id;
            opt.innerText = `${u.nazwa} (${u.cenaNetto.toFixed(2)} zł | ${u.czasTrwaniaMin} min)`;
            serviceSelect.appendChild(opt);
        });
    }

    // Initialize Calendar
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
        contentHeight: 'auto',
        selectable: true,
        selectAllow: function (selectInfo) {
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

            // Highlight selected day
            document.querySelectorAll('.fc-daygrid-day').forEach(el => {
                el.style.backgroundColor = '';
            });
            info.dayEl.style.backgroundColor = 'rgba(212, 175, 55, 0.15)';

            // Reset and show reservation form
            onDateSelected();
        }
    });
    calendar.render();

    // Reset workflow state when a new date is clicked
    async function onDateSelected() {
        try {
            placeholderCard.style.display = 'none';
            reservationFormCard.style.display = 'block';

            // Reset inputs
            serviceSelect.value = "";
            fryzjerSelect.innerHTML = '<option value="">-- Najpierw wybierz usługę --</option>';
            fryzjerSelect.disabled = true;
            timeSelect.innerHTML = '<option value="">-- Najpierw wybierz fryzjera --</option>';
            timeSelect.disabled = true;
            
            selectedFryzjerId = null;
            selectedSlot = null;
            selectedService = null;
            
            summaryBadge.style.display = 'none';
            validateBookingState();

            // Fetch schedules for the selected date
            const schedRes = await fetch(`${API_URL}/harmonogram/data/${selectedDate}`);
            scheduledWorkHours = await schedRes.json();

            // Set active steps
            document.getElementById('step-date').classList.add('active');
            document.getElementById('step-service').classList.add('active');
            document.getElementById('step-stylist').classList.remove('active');
            document.getElementById('step-time').classList.remove('active');
        } catch (err) {
            console.error("Błąd pobierania harmonogramu:", err);
        }
    }

    // Service Select Listener
    serviceSelect.addEventListener('change', function () {
        const serviceId = this.value ? parseInt(this.value) : null;
        if (!serviceId) {
            selectedService = null;
            fryzjerSelect.innerHTML = '<option value="">-- Najpierw wybierz usługę --</option>';
            fryzjerSelect.disabled = true;
            timeSelect.innerHTML = '<option value="">-- Najpierw wybierz fryzjera --</option>';
            timeSelect.disabled = true;
            summaryBadge.style.display = 'none';
            
            document.getElementById('step-stylist').classList.remove('active');
            document.getElementById('step-time').classList.remove('active');
            
            validateBookingState();
            return;
        }

        selectedService = services.find(s => s.id === serviceId);
        
        // Update summary
        totalDurationText.innerText = selectedService.czasTrwaniaMin;
        totalPriceText.innerText = selectedService.cenaNetto.toFixed(2);
        summaryBadge.style.display = 'block';

        // Filter hairdressers by selected service name match in specialization & check if they work today
        const matchingFryzjerzy = fryzjerzyList.filter(f => {
            // Check if hairdresser has a work schedule on this date
            const worksToday = scheduledWorkHours.some(sch => sch.fryzjerId === f.id);
            if (!worksToday) return false;

            // Check specialization
            if (!f.specjalizacja) return true; // fallback if empty
            const specList = f.specjalizacja.split(',').map(s => s.trim().toLowerCase());
            return specList.some(spec => spec.includes(selectedService.nazwa.toLowerCase()) || selectedService.nazwa.toLowerCase().includes(spec));
        });

        // Populate hairdresser select
        fryzjerSelect.innerHTML = '<option value="">-- Wybierz fryzjera --</option>';
        if (matchingFryzjerzy.length === 0) {
            const opt = document.createElement('option');
            opt.value = "";
            opt.innerText = "Brak fryzjerów ze specjalizacją na ten dzień";
            fryzjerSelect.appendChild(opt);
            fryzjerSelect.disabled = true;
            document.getElementById('step-stylist').classList.remove('active');
        } else {
            matchingFryzjerzy.forEach(f => {
                const sch = scheduledWorkHours.find(s => s.fryzjerId === f.id);
                const opt = document.createElement('option');
                opt.value = f.id;
                opt.innerText = `${f.imie} ${f.nazwisko} (${sch.godzinaOd.slice(0, 5)} - ${sch.godzinaDo.slice(0, 5)})`;
                fryzjerSelect.appendChild(opt);
            });
            fryzjerSelect.disabled = false;
            document.getElementById('step-stylist').classList.add('active');
        }

        // Reset and disable time selection
        timeSelect.innerHTML = '<option value="">-- Najpierw wybierz fryzjera --</option>';
        timeSelect.disabled = true;
        document.getElementById('step-time').classList.remove('active');
        selectedSlot = null;
        validateBookingState();
    });

    // Fryzjer Select Listener
    fryzjerSelect.addEventListener('change', async function () {
        selectedFryzjerId = this.value ? parseInt(this.value) : null;
        selectedSlot = null;
        timeSelect.innerHTML = '<option value="">-- Ładowanie godzin... --</option>';
        timeSelect.disabled = true;
        document.getElementById('step-time').classList.remove('active');

        if (!selectedFryzjerId) {
            timeSelect.innerHTML = '<option value="">-- Najpierw wybierz fryzjera --</option>';
            validateBookingState();
            return;
        }

        try {
            // Fetch appointments of the selected hairdresser using authentication token!
            const bookingsRes = await fetch(`${API_URL}/wizyty/fryzjer/${selectedFryzjerId}`, {
                headers: { 'Authorization': 'Bearer ' + token }
            });
            if (!bookingsRes.ok) {
                const errData = await bookingsRes.json().catch(() => ({}));
                console.error("Błąd pobierania wizyt fryzjera:", errData.message || bookingsRes.statusText);
                timeSelect.innerHTML = '<option value="">Błąd ładowania wolnych terminów</option>';
                return;
            }
            existingBookings = await bookingsRes.json();
            
            generateTimeSlots();
            document.getElementById('step-time').classList.add('active');
        } catch (err) {
            console.error("Błąd pobierania wizyt:", err);
            timeSelect.innerHTML = '<option value="">Błąd połączenia z serwerem</option>';
        }
    });

    // Generate Dynamic Time Slots Dropdown
    function generateTimeSlots() {
        timeSelect.innerHTML = '<option value="">-- Wybierz godzinę --</option>';
        
        if (!selectedDate || !selectedFryzjerId || !selectedService) {
            timeSelect.innerHTML = '<option value="">-- Najpierw uzupełnij pola powyżej --</option>';
            validateBookingState();
            return;
        }

        const duration = selectedService.czasTrwaniaMin;

        // Find work schedule
        const schedule = scheduledWorkHours.find(s => s.fryzjerId === selectedFryzjerId);
        if (!schedule) {
            timeSelect.innerHTML = '<option value="">Pracownik nie pracuje w tym dniu</option>';
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
                const hour = String(Math.floor(time / 60)).padStart(2, '0');
                const minute = String(time % 60).padStart(2, '0');
                const timeString = `${hour}:${minute}`;

                const opt = document.createElement('option');
                opt.value = timeString;
                opt.innerText = timeString;
                timeSelect.appendChild(opt);
                slotsCount++;
            }
        }

        if (slotsCount === 0) {
            timeSelect.innerHTML = '<option value="">Brak wolnych terminów w tym dniu</option>';
            timeSelect.disabled = true;
        } else {
            timeSelect.disabled = false;
        }
        
        validateBookingState();
    }

    // Time Select Listener
    timeSelect.addEventListener('change', function () {
        selectedSlot = this.value || null;
        validateBookingState();
    });

    function validateBookingState() {
        if (selectedDate && selectedFryzjerId && selectedSlot && selectedService) {
            bookBtn.disabled = false;
        } else {
            bookBtn.disabled = true;
        }
    }

    // Submit booking
    window.submitBooking = async function () {
        if (!selectedDate || !selectedFryzjerId || !selectedSlot || !selectedService) return;

        bookBtn.disabled = true;
        bookBtn.innerText = 'Rezerwowanie...';

        const dataGodzina = `${selectedDate}T${selectedSlot}:00`;

        const payload = {
            klientId: currentKlient.id,
            fryzjerId: selectedFryzjerId,
            dataGodzinaRozpoczecia: dataGodzina,
            czasTrwaniaCalkowity: selectedService.czasTrwaniaMin,
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
            const res = await fetch(`${API_URL}/wizyty/klient/${currentKlient.id}`, {
                headers: { 'Authorization': 'Bearer ' + token }
            });
            if (!res.ok) {
                const errData = await res.json().catch(() => ({}));
                console.error("Błąd pobierania wizyt klienta:", errData.message || res.statusText);
                myAppointmentsTable.innerHTML = `<tr><td colspan="4" style="text-align: center; color: #ff4d4d;">Nie udało się załadować listy rezerwacji.</td></tr>`;
                return;
            }
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
