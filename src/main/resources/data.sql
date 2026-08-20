INSERT INTO person (dtype, id, nick, email, country, city, phone_number,
                    contact_person_name, commission_percent, company_name, nip)
VALUES ('Booker', 1,
        'Zwolak', 'deerhunterbooking@mail.com', 'Poland', 'Warsaw', '500600700',
        'Paweł Zwoliński', 5.00, 'Deer Hunter Booking Sp. z o.o.', '8567346215')
ON CONFLICT (id) DO NOTHING;

INSERT INTO person (dtype, id, nick, email, country, city, phone_number,
                    gender, date_of_birth, bio, open_to_play)
VALUES ('Musician', 2,
        'Klusek', 'klusek@gmail.com', 'Poland', 'Warsaw', '987654321',
        'MALE', DATE '1984-02-13', 'Doswiadczony basista i wokalista', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO person (dtype, id, nick, email, country, city, phone_number,
                    contact_person_name, verified)
VALUES ('Owner', 3,
        'Nowaq', 'jannowak@gmail.com', 'Poland', 'Warsaw', '600700800',
        'Jan Nowak', NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO building (id, name, city, street, number, country, owner_id)
VALUES (1, 'Hydrozagadka', 'Warsaw', '11 listopada', '22', 'Poland', 3),
       (2, 'Chmury', 'Warsaw', '11 listopada', '22', 'Poland', 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO room (dtype, id, name, capacity, area, price_per_hour, building_id,
                  stage_size, acoustic_rating, lighting_system, sound_system)
VALUES ('ConcertHall', 1, 'Duża', 500, 600.0, 3000, 1, 120.0, 8, TRUE, TRUE),
       ('ConcertHall', 2, 'Mała', 200, 250.0, 1500, 1, 60.0, 6, TRUE, FALSE),
       ('ConcertHall', 3, 'Główna', 800, 850.0, 5000, 2, 160.0, 9, TRUE, TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO genre (id, name)
VALUES (1, 'Psychedelic Rock'),
       (2, 'Metal'),
       (3, 'Stoner'),
       (4, 'Sludge')
ON CONFLICT (id) DO NOTHING;

INSERT INTO band (id, name, country, city, open_to_recruitment)
VALUES (1, 'Belzebong', 'Poland', 'Kielce', TRUE),
       (2, 'Dopelord', 'Poland', 'Lublin', FALSE),
       (3, 'Electric Wizard', 'England', 'Dorset', TRUE),
       (4, 'Kyuss', 'USA', 'Palm Desert', FALSE),
       (5, 'Mgła', 'Poland', 'Kraków', FALSE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO band_genres (band_id, genre_id)
VALUES (1, 3),
       (1, 1),
       (2, 3),
       (2, 2),
       (3, 3),
       (3, 4),
       (3, 2),
       (4, 1),
       (4, 3),
       (5, 2)
ON CONFLICT DO NOTHING;

SELECT setval('person_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM person), 999), true);
SELECT setval('band_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM band), 999), true);
SELECT setval('building_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM building), 999), true);
SELECT setval('room_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM room), 999), true);
SELECT setval('genre_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM genre), 999), true);
SELECT setval('event_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM event), 999), true);
SELECT setval('instrument_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM instrument), 999), true);
SELECT setval('band_concert_participation_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM band_concert_participation), 999), true);