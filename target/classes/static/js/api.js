// Adres backendu (relatywny, bo front i back są na tym samym serwerze)
const API_URL = "/api";

// Funkcja pomocnicza do logowania błędów
function logBlad(error) {
    console.error("Wystąpił błąd:", error);
    // Możesz to odkomentować, jeśli chcesz wyskakujące okienka przy każdym błędzie:
    // alert("Wystąpił błąd! Sprawdź konsolę.");
}