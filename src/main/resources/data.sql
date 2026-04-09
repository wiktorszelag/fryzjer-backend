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

INSERT INTO wizyta (klientid, fryzjerid, data_godzina_rozpoczecia, czas_trwania_całkowity, data_rezerwacji) VALUES
(1, 1, '2024-12-05 10:00:00', 45, '2024-12-01'),
(2, 2, '2024-12-05 11:00:00', 30, '2024-12-02'),
(3, 3, '2024-12-06 14:00:00', 120, '2024-12-03');
