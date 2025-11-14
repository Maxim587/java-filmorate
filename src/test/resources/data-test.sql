-- Вставляем рейтинги MPA
MERGE INTO RATING (rating_id, name) VALUES (1, 'G');
MERGE INTO RATING (rating_id, name) VALUES (2, 'PG');
MERGE INTO RATING (rating_id, name) VALUES (3, 'PG-13');
MERGE INTO RATING (rating_id, name) VALUES (4, 'R');
MERGE INTO RATING (rating_id, name) VALUES (5, 'NC-17');

-- Вставляем жанры
MERGE INTO GENRE (genre_id, name) VALUES (1, 'Комедия');
MERGE INTO GENRE (genre_id, name) VALUES (2, 'Драма');
MERGE INTO GENRE (genre_id, name) VALUES (3, 'Мультфильм');
MERGE INTO GENRE (genre_id, name) VALUES (4, 'Триллер');
MERGE INTO GENRE (genre_id, name) VALUES (5, 'Документальный');
MERGE INTO GENRE (genre_id, name) VALUES (6, 'Боевик');

-- Вставляем статусы дружбы
MERGE INTO FRIENDSHIP_STATUS (friendship_status_id, status) VALUES (1, 'CONFIRMED');
MERGE INTO FRIENDSHIP_STATUS (friendship_status_id, status) VALUES (2, 'NOT_CONFIRMED');
