-- Czyścimy wszystko, aby uniknąć konfliktów ze starymi wersjami bazy (zwłaszcza constraintów)
DROP TABLE IF EXISTS szczegoly_wizyty CASCADE;
DROP TABLE IF EXISTS fryzjer_usluga CASCADE;
DROP TABLE IF EXISTS wizyta CASCADE;
DROP TABLE IF EXISTS uzytkownik CASCADE;
DROP TABLE IF EXISTS klient CASCADE;
DROP TABLE IF EXISTS fryzjer CASCADE;
DROP TABLE IF EXISTS usluga CASCADE;

-- Tworzymy tabele od nowa zgodnie z aktualnymi encjami
CREATE TABLE uzytkownik (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rola VARCHAR(50) NOT NULL
);

CREATE TABLE klient (
    id BIGSERIAL PRIMARY KEY,
    imie VARCHAR(100),
    nazwisko VARCHAR(100),
    telefon VARCHAR(20),
    data_rejestracji DATE
);

CREATE TABLE fryzjer (
    id BIGSERIAL PRIMARY KEY,
    imie VARCHAR(100),
    nazwisko VARCHAR(100),
    telefon VARCHAR(20),
    specjalizacja VARCHAR(100),
    data_zatrudnienia DATE
);

CREATE TABLE usluga (
    id BIGSERIAL PRIMARY KEY,
    nazwa VARCHAR(200) NOT NULL,
    opis TEXT,
    czas_trwania_min INT,
    cena_netto NUMERIC(10,2),
    stawkavat NUMERIC(5,2)
);

CREATE TABLE wizyta (
    id BIGSERIAL PRIMARY KEY,
    klientid BIGINT REFERENCES klient(id),
    fryzjerid BIGINT REFERENCES fryzjer(id),
    data_godzina_rozpoczecia TIMESTAMP,
    czas_trwania_całkowity INT,
    data_rezerwacji DATE
);
