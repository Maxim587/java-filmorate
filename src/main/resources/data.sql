-- Очищаем таблицы перед вставкой
DELETE FROM film_director;
DELETE FROM film_like;
DELETE FROM film_genre;
DELETE FROM film;
DELETE FROM directors;
DELETE FROM friendship;
DELETE FROM users;

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

-- Вставляем режиссеров (ВАЖНО: ID должны соответствовать тестам)
MERGE INTO directors (director_id, name) VALUES (1, 'Режиссер 1');
MERGE INTO directors (director_id, name) VALUES (2, 'Режиссер 2');
MERGE INTO directors (director_id, name) VALUES (3, 'Стивен Спилберг');

-- Вставляем фильмы с правильными датами для тестов
MERGE INTO film (film_id, name, description, release_date, duration, rating_id)
VALUES (1, 'Фильм 1', 'Описание 1', '2000-04-17', 120, 1);

MERGE INTO film (film_id, name, description, release_date, duration, rating_id)
VALUES (2, 'Фильм 2', 'Описание 2', '2001-04-17', 130, 2);

MERGE INTO film (film_id, name, description, release_date, duration, rating_id)
VALUES (3, 'Фильм 3', 'Описание 3', '2002-04-17', 140, 3);

-- Связываем фильмы с режиссерами
MERGE INTO film_director (film_id, director_id) VALUES (1, 3);
MERGE INTO film_director (film_id, director_id) VALUES (2, 3);
MERGE INTO film_director (film_id, director_id) VALUES (3, 3);
