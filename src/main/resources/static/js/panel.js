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
    let publicHolidays = [];

    try {
        const year = new Date().getFullYear();
        const swietaRes = await fetch(`https://date.nager.at/api/v3/PublicHolidays/${year}/PL`);
        if (swietaRes.ok) publicHolidays = await swietaRes.json();
    } catch(err) {
        console.warn("API Nager.Date niedostępne", err);
    }

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
    const calendarOverlay = document.getElementById('calendarOverlay');

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
        dayCellDidMount: function(info) {
            const localDate = new Date(info.date.getTime() - (info.date.getTimezoneOffset() * 60000)).toISOString().split('T')[0];
            const isWeekend = info.date.getDay() === 0 || info.date.getDay() === 6;
            const holiday = publicHolidays.find(h => h.date === localDate);
            
            if (isWeekend || holiday) {
                info.el.classList.add('holiday-cell');
                info.el.title = holiday ? holiday.localName : "Weekend (Zamknięte)";
            }
        },
        selectAllow: function (selectInfo) {
            const today = new Date();
            today.setHours(0,0,0,0);
            return selectInfo.start >= today;
        },
        dateClick: async function (info) {
            if (!selectedFryzjerId) return;

            const today = new Date();
            today.setHours(0,0,0,0);
            const clickedDate = new Date(info.dateStr);
            if (clickedDate < today) {
                Swal.fire({icon: 'error', title: 'Błąd', text: 'Nie możesz zarezerwować wizyty w przeszłości!', background: '#1a1a1a', color: '#fff', confirmButtonColor: '#d4af37'});
                return;
            }
            
            if (info.dayEl.classList.contains('holiday-cell')) {
                const reason = info.dayEl.title;
                Swal.fire({icon: 'warning', title: 'Dzień Wolny', text: `Ten dzień to: ${reason}. Salon jest w tym dniu nieczynny!`, background: '#1a1a1a', color: '#fff', confirmButtonColor: '#d4af37'});
                return;
            }
            
            selectedDate = info.dateStr;
            const dateTextDisplay = document.getElementById('selectedDateText');
            dateTextDisplay.innerText = new Date(selectedDate).toLocaleDateString('pl-PL', {
                weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
            });
            dateTextDisplay.style.color = '#fff';
            document.getElementById('step-date').classList.add('active');

            // Highlight selected day
            document.querySelectorAll('.fc-daygrid-day').forEach(el => {
                el.style.backgroundColor = '';
            });
            info.dayEl.style.backgroundColor = 'rgba(212, 175, 55, 0.15)';

            // Fetch daily schedule
            try {
                const schedRes = await fetch(`${API_URL}/harmonogram/data/${selectedDate}`);
                scheduledWorkHours = await schedRes.json();
                
                document.getElementById('step-time').classList.add('active');
                generateTimeSlots();
            } catch(e) { 
                console.error(e); 
            }
        }
    });
    calendar.render();

    // Reset UI state when Service changes
    serviceSelect.addEventListener('change', function () {
        const serviceId = this.value ? parseInt(this.value) : null;
        
        // Reset sub-steps
        fryzjerSelect.innerHTML = '<option value="">-- Najpierw wybierz usługę --</option>';
        fryzjerSelect.disabled = true;
        document.getElementById('step-stylist').classList.remove('active');
        
        lockCalendar();
        
        timeSelect.innerHTML = '<option value="">-- Najpierw wybierz dzień z kalendarza --</option>';
        timeSelect.disabled = true;
        document.getElementById('step-time').classList.remove('active');
        
        selectedFryzjerId = null;
        selectedDate = null;
        selectedSlot = null;
        selectedService = null;
        summaryBadge.style.display = 'none';

        if (!serviceId) {
            validateBookingState();
            return;
        }

        selectedService = services.find(s => s.id === serviceId);
        
        // Update summary
        totalDurationText.innerText = selectedService.czasTrwaniaMin;
        totalPriceText.innerText = selectedService.cenaNetto.toFixed(2);
        summaryBadge.style.display = 'block';

        // Filter hairdressers
        const matchingFryzjerzy = fryzjerzyList.filter(f => {
            if (!f.specjalizacja) return true;
            const specList = f.specjalizacja.split(',').map(s => s.trim().toLowerCase());
            return specList.some(spec => spec.includes(selectedService.nazwa.toLowerCase()) || selectedService.nazwa.toLowerCase().includes(spec));
        });

        fryzjerSelect.innerHTML = '<option value="">-- Wybierz fryzjera --</option>';
        if (matchingFryzjerzy.length === 0) {
            const opt = document.createElement('option');
            opt.value = "";
            opt.innerText = "Brak fryzjerów wykonujących tę usługę";
            fryzjerSelect.appendChild(opt);
        } else {
            matchingFryzjerzy.forEach(f => {
                const opt = document.createElement('option');
                opt.value = f.id;
                opt.innerText = `${f.imie} ${f.nazwisko}`;
                fryzjerSelect.appendChild(opt);
            });
            fryzjerSelect.disabled = false;
            document.getElementById('step-stylist').classList.add('active');
        }

        validateBookingState();
    });

    function lockCalendar() {
        calendarEl.classList.add('calendar-disabled');
        calendarOverlay.style.display = 'block';
        document.getElementById('selectedDateText').innerText = "Oczekiwanie na wybór w kalendarzu...";
        document.getElementById('selectedDateText').style.color = '#888';
        document.getElementById('step-date').classList.remove('active');
        document.querySelectorAll('.fc-daygrid-day').forEach(el => el.style.backgroundColor = '');
    }

    // Fryzjer Select Listener
    fryzjerSelect.addEventListener('change', async function () {
        selectedFryzjerId = this.value ? parseInt(this.value) : null;
        
        lockCalendar();
        timeSelect.innerHTML = '<option value="">-- Najpierw wybierz dzień z kalendarza --</option>';
        timeSelect.disabled = true;
        document.getElementById('step-time').classList.remove('active');
        selectedDate = null;
        selectedSlot = null;

        if (!selectedFryzjerId) {
            validateBookingState();
            return;
        }

        try {
            // Fetch appointments of the selected hairdresser
            const bookingsRes = await fetch(`${API_URL}/wizyty/fryzjer/${selectedFryzjerId}`, {
                headers: { 'Authorization': 'Bearer ' + token }
            });
            if (bookingsRes.ok) {
                existingBookings = await bookingsRes.json();
            }
            
            // Unlock calendar
            calendarEl.classList.remove('calendar-disabled');
            calendarOverlay.style.display = 'none';
        } catch (err) {
            console.error("Błąd pobierania wizyt:", err);
            Swal.fire({icon: 'error', title: 'Błąd sieci', text: 'Błąd połączenia z serwerem', background: '#1a1a1a', color: '#fff'});
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
                Swal.fire({
                    icon: 'success',
                    title: 'Sukces!',
                    text: 'Rezerwacja zakończona pomyślnie.',
                    background: '#1a1a1a', color: '#fff', confirmButtonColor: '#d4af37'
                });
                
                selectedSlot = null;
                selectedDate = null;
                lockCalendar();
                timeSelect.innerHTML = '<option value="">-- Najpierw wybierz dzień z kalendarza --</option>';
                timeSelect.disabled = true;
                document.getElementById('step-time').classList.remove('active');
                
                loadClientAppointments();
            } else {
                Swal.fire({icon: 'error', title: 'Błąd', text: 'Nie udało się utworzyć rezerwacji.', background: '#1a1a1a', color: '#fff', confirmButtonColor: '#d4af37'});
            }
            bookBtn.disabled = false;
            bookBtn.innerText = 'Potwierdź i Rezerwuj';
        } catch (err) {
            console.error("Błąd podczas rezerwacji:", err);
            Swal.fire({icon: 'error', title: 'Błąd sieci', text: 'Brak połączenia z serwerem.', background: '#1a1a1a', color: '#fff', confirmButtonColor: '#d4af37'});
            bookBtn.disabled = false;
            bookBtn.innerText = 'Potwierdź i Rezerwuj';
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
        Swal.fire({
            title: 'Czy na pewno?',
            text: "Chcesz anulować tę rezerwację?",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#444',
            confirmButtonText: 'Tak, anuluj!',
            cancelButtonText: 'Wróć',
            background: '#1a1a1a', color: '#fff'
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const res = await fetch(`${API_URL}/wizyty/${id}`, {
                        method: 'DELETE',
                        headers: { 'Authorization': 'Bearer ' + token }
                    });

                    if (res.ok) {
                        Swal.fire({icon: 'success', title: 'Anulowano', text: 'Wizyta została odwołana.', background: '#1a1a1a', color: '#fff', confirmButtonColor: '#d4af37'});
                        loadClientAppointments();
                        
                        // If same date is selected, refresh slots
                        if (selectedDate && selectedFryzjerId) {
                            // Fetch bookings again
                            const bRes = await fetch(`${API_URL}/wizyty/fryzjer/${selectedFryzjerId}`, { headers: { 'Authorization': 'Bearer ' + token } });
                            if (bRes.ok) existingBookings = await bRes.json();
                            generateTimeSlots();
                        }
                    } else {
                        Swal.fire({icon: 'error', title: 'Błąd', text: 'Nie udało się anulować wizyty.', background: '#1a1a1a', color: '#fff', confirmButtonColor: '#d4af37'});
                    }
                } catch (err) {
                    Swal.fire({icon: 'error', title: 'Błąd sieci', text: 'Błąd połączenia z serwerem.', background: '#1a1a1a', color: '#fff', confirmButtonColor: '#d4af37'});
                }
            }
        });
    };
});
