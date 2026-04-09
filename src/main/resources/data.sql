<<<<<<< HEAD
<<<<<<< HEAD
INSERT INTO uzytkownik (username, password, rola) VALUES
('admin', 'admin', 'ADMIN'),
('pracownik', 'pracownik', 'PRACOWNIK'),
('klient', 'klient', 'KLIENT')
ON CONFLICT (username) DO NOTHING;

INSERT INTO klient (imie, nazwisko, telefon, data_rejestracji) VALUES
('Jan', 'Kowalski', '123456789', '2024-01-15'),
('Anna', 'Nowak', '987654321', '2024-02-20'),
('Piotr', 'Wiśniewski', '555666777', '2024-03-10');

INSERT INTO fryzjer (imie, nazwisko, telefon, specjalizacja, data_zatrudnienia) VALUES
('Marta', 'Kaczmarek', '111222333', 'Koloryzacja', '2020-06-01'),
('Tomasz', 'Lewandowski', '444555666', 'Strzyżenie męskie', '2019-03-15'),
('Karolina', 'Dąbrowska', '777888999', 'Stylizacja', '2021-09-01');

INSERT INTO usluga (nazwa, opis, czas_trwania_min, cena_netto, stawkavat) VALUES
('Strzyżenie damskie', 'Strzyżenie włosów damskich', 45, 80.00, 23.00),
('Strzyżenie męskie', 'Strzyżenie włosów męskich', 30, 50.00, 23.00),
('Koloryzacja', 'Farbowanie włosów', 120, 150.00, 23.00),
('Modelowanie', 'Stylizacja i modelowanie włosów', 30, 40.00, 23.00),
('Trwała ondulacja', 'Trwała ondulacja włosów', 90, 120.00, 23.00);

INSERT INTO fryzjer_usluga (fryzjerid, uslugaid, cena_fryzjera) VALUES
(1, 1, 85.00),
(1, 3, 160.00),
(1, 4, 45.00),
(2, 2, 55.00),
(2, 4, 40.00),
(3, 1, 80.00),
(3, 3, 150.00),
(3, 4, 45.00),
(3, 5, 130.00);

INSERT INTO wizyta (klientid, fryzjerid, data_godzina_rozpoczecia, czas_trwania_całkowity, data_rezerwacji) VALUES
(1, 1, '2024-12-05 10:00:00', 45, '2024-12-01'),
(2, 2, '2024-12-05 11:00:00', 30, '2024-12-02'),
(3, 3, '2024-12-06 14:00:00', 120, '2024-12-03');

INSERT INTO szczegoly_wizyty (wizytaid, uslugaid, cena_laczna_uslugi) VALUES
(1, 1, 98.40),
(2, 2, 61.50),
(3, 3, 184.50),
(3, 4, 49.20);
=======
=======
-- Czyścimy tabele przed wstawieniem danych (kolejność ważna ze względu na klucze obce)
TRUNCATE TABLE SZCZEGOLY_WIZYTY, FRYZJER_USLUGA, WIZYTA, KLIENT, FRYZJER, USLUGA RESTART IDENTITY CASCADE;

>>>>>>> a532424 (FrontV1.1)
-- Fryzjerzy
INSERT INTO FRYZJER (imie, nazwisko, telefon, specjalizacja, data_zatrudnienia) VALUES 
('Anna', 'Nowak', '111-222-333', 'Strzyżenie damskie', '2023-01-15'),
('Tomasz', 'Kowalski', '222-333-444', 'Strzyżenie męskie', '2022-05-10'),
('Katarzyna', 'Wiśniewska', '333-444-555', 'Koloryzacja', '2021-11-20'),
('Piotr', 'Wójcik', '444-555-666', 'Stylizacja', '2024-02-01');

-- Klienci
INSERT INTO KLIENT (imie, nazwisko, telefon, data_rejestracji) VALUES
('Jan', 'Kowalski', '500-100-100', '2024-01-10'),
('Maria', 'Nowak', '500-200-200', '2024-02-15'),
('Adam', 'Zieliński', '500-300-300', '2024-03-20'),
('Ewa', 'Mazur', '500-400-400', '2024-04-05'),
('Krzysztof', 'Krawczyk', '500-500-500', '2024-05-12'),
('Agnieszka', 'Lewandowska', '500-600-600', '2024-06-18'),
('Michał', 'Kamiński', '500-700-700', '2024-07-22'),
('Zofia', 'Zając', '500-800-800', '2024-08-30');

-- Uslugi
INSERT INTO USLUGA (nazwa, opis, czas_trwania_min, cena_netto, stawka_vat) VALUES
('Strzyżenie Męskie', 'Klasyczne strzyżenie męskie', 45, 50.00, 0.23),
('Strzyżenie Damskie', 'Strzyżenie i modelowanie', 60, 120.00, 0.23),
('Koloryzacja', 'Farbowanie włosów', 120, 250.00, 0.23),
('Modelowanie', 'Modelowanie fryzury na wyjście', 45, 80.00, 0.23),
('Baleyage', 'Rozjaśnianie pasemkami', 150, 350.00, 0.23),
('Keratyna', 'Prostowanie keratynowe', 180, 450.00, 0.23);

-- Powiązania Fryzjer-Usluga
INSERT INTO FRYZJER_USLUGA (fryzjer_id, usluga_id, cena_fryzjera) VALUES
(1, 2, 120.00), (1, 3, 250.00), (1, 5, 350.00),
(2, 1, 50.00), (2, 4, 80.00),
(3, 3, 260.00), (3, 5, 360.00), (3, 6, 460.00),
(4, 1, 55.00), (4, 2, 130.00), (4, 4, 90.00);

-- Wizyty 15.12.2025 - 19.12.2025

-- 15.12 Poniedziałek
INSERT INTO WIZYTA (klient_id, fryzjer_id, data_godzina_rozpoczecia, czas_trwania_calkowity, data_rezerwacji) 
VALUES (1, 1, '2025-12-15 10:00:00', 120, '2025-12-01');
INSERT INTO SZCZEGOLY_WIZYTY (wizyta_id, usluga_id, cena_laczna_uslugi) VALUES ((SELECT MAX(wizyta_id) FROM WIZYTA), 3, 250.00);

INSERT INTO WIZYTA (klient_id, fryzjer_id, data_godzina_rozpoczecia, czas_trwania_calkowity, data_rezerwacji) 
VALUES (2, 2, '2025-12-15 10:30:00', 45, '2025-12-02');
INSERT INTO SZCZEGOLY_WIZYTY (wizyta_id, usluga_id, cena_laczna_uslugi) VALUES ((SELECT MAX(wizyta_id) FROM WIZYTA), 1, 50.00);

INSERT INTO WIZYTA (klient_id, fryzjer_id, data_godzina_rozpoczecia, czas_trwania_calkowity, data_rezerwacji) 
VALUES (3, 3, '2025-12-15 12:00:00', 180, '2025-12-03');
INSERT INTO SZCZEGOLY_WIZYTY (wizyta_id, usluga_id, cena_laczna_uslugi) VALUES ((SELECT MAX(wizyta_id) FROM WIZYTA), 6, 460.00);

INSERT INTO WIZYTA (klient_id, fryzjer_id, data_godzina_rozpoczecia, czas_trwania_calkowity, data_rezerwacji) 
VALUES (4, 4, '2025-12-15 12:00:00', 60, '2025-12-04');
INSERT INTO SZCZEGOLY_WIZYTY (wizyta_id, usluga_id, cena_laczna_uslugi) VALUES ((SELECT MAX(wizyta_id) FROM WIZYTA), 2, 130.00);

-- 16.12 Wtorek
INSERT INTO WIZYTA (klient_id, fryzjer_id, data_godzina_rozpoczecia, czas_trwania_calkowity, data_rezerwacji) 
VALUES (5, 1, '2025-12-16 14:00:00', 150, '2025-12-05');
INSERT INTO SZCZEGOLY_WIZYTY (wizyta_id, usluga_id, cena_laczna_uslugi) VALUES ((SELECT MAX(wizyta_id) FROM WIZYTA), 5, 350.00);

INSERT INTO WIZYTA (klient_id, fryzjer_id, data_godzina_rozpoczecia, czas_trwania_calkowity, data_rezerwacji) 
VALUES (6, 2, '2025-12-16 15:00:00', 45, '2025-12-05');
INSERT INTO SZCZEGOLY_WIZYTY (wizyta_id, usluga_id, cena_laczna_uslugi) VALUES ((SELECT MAX(wizyta_id) FROM WIZYTA), 1, 50.00);

-- 17.12 Środa
INSERT INTO WIZYTA (klient_id, fryzjer_id, data_godzina_rozpoczecia, czas_trwania_calkowity, data_rezerwacji) 
VALUES (7, 1, '2025-12-17 16:00:00', 60, '2025-12-10');
INSERT INTO SZCZEGOLY_WIZYTY (wizyta_id, usluga_id, cena_laczna_uslugi) VALUES ((SELECT MAX(wizyta_id) FROM WIZYTA), 2, 120.00);

INSERT INTO WIZYTA (klient_id, fryzjer_id, data_godzina_rozpoczecia, czas_trwania_calkowity, data_rezerwacji) 
VALUES (8, 2, '2025-12-17 16:00:00', 45, '2025-12-10');
INSERT INTO SZCZEGOLY_WIZYTY (wizyta_id, usluga_id, cena_laczna_uslugi) VALUES ((SELECT MAX(wizyta_id) FROM WIZYTA), 1, 50.00);

INSERT INTO WIZYTA (klient_id, fryzjer_id, data_godzina_rozpoczecia, czas_trwania_calkowity, data_rezerwacji) 
VALUES (1, 3, '2025-12-17 16:00:00', 120, '2025-12-10');
INSERT INTO SZCZEGOLY_WIZYTY (wizyta_id, usluga_id, cena_laczna_uslugi) VALUES ((SELECT MAX(wizyta_id) FROM WIZYTA), 3, 260.00);

-- 18.12 Czwartek
INSERT INTO WIZYTA (klient_id, fryzjer_id, data_godzina_rozpoczecia, czas_trwania_calkowity, data_rezerwacji) 
VALUES (2, 4, '2025-12-18 10:00:00', 90, '2025-12-12');
INSERT INTO SZCZEGOLY_WIZYTY (wizyta_id, usluga_id, cena_laczna_uslugi) VALUES ((SELECT MAX(wizyta_id) FROM WIZYTA), 2, 130.00);

-- 19.12 Piątek
INSERT INTO WIZYTA (klient_id, fryzjer_id, data_godzina_rozpoczecia, czas_trwania_calkowity, data_rezerwacji) 
VALUES (3, 2, '2025-12-19 17:00:00', 45, '2025-12-13');
INSERT INTO SZCZEGOLY_WIZYTY (wizyta_id, usluga_id, cena_laczna_uslugi) VALUES ((SELECT MAX(wizyta_id) FROM WIZYTA), 4, 80.00);

INSERT INTO WIZYTA (klient_id, fryzjer_id, data_godzina_rozpoczecia, czas_trwania_calkowity, data_rezerwacji) 
VALUES (5, 4, '2025-12-19 17:00:00', 45, '2025-12-13');
INSERT INTO SZCZEGOLY_WIZYTY (wizyta_id, usluga_id, cena_laczna_uslugi) VALUES ((SELECT MAX(wizyta_id) FROM WIZYTA), 4, 90.00);
>>>>>>> 1105a15 (frontendV1)
