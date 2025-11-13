-- Очищаем таблицы перед вставкой
DELETE FROM film_director;
DELETE FROM film;
DELETE FROM directors;
DELETE FROM genre;
DELETE FROM rating;
DELETE FROM friendship_status;

-- Вставляем данные
INSERT INTO friendship_status (FRIENDSHIP_STATUS_ID, STATUS)
VALUES (1, 'CONFIRMED'),
       (2, 'NOT_CONFIRMED');

INSERT INTO rating (RATING_ID, NAME)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

INSERT INTO genre (GENRE_ID, NAME)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');

INSERT INTO directors (DIRECTOR_ID, NAME)
VALUES (1, 'Кристофер Нолан'),
       (2, 'Квентин Тарантино'),
       (3, 'Стивен Спилберг'),
       (4, 'Джеймс Кэмерон'),
       (5, 'Мартин Скорсезе'),
       (6, 'Дэвид Финчер'),
       (7, 'Питер Джексон'),
       (8, 'Ридли Скотт'),
       (9, 'Вуди Аллен'),
       (10, 'Алексей Герман');

INSERT INTO film (FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
VALUES (1, 'Начало', 'Фильм о снах', '2010-07-16', 148, 3),
       (2, 'Криминальное чтиво', 'Культовый фильм Тарантино', '1994-05-21', 154, 4),
       (3, 'Парк Юрского периода', 'Динозавры оживают', '1993-06-11', 127, 2),
       (4, 'Титаник', 'История любви на тонущем корабле', '1997-12-19', 195, 3),
       (5, 'Отступники', 'Противостояние полиции и мафии', '2006-09-26', 151, 4);

INSERT INTO film_director (FILM_ID, DIRECTOR_ID)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5);
