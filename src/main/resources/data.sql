-- Очищаем таблицы перед вставкой
DELETE FROM film_director;
DELETE FROM film_like;
DELETE FROM film_genre;
DELETE FROM film;
DELETE FROM directors;
DELETE FROM genre;
DELETE FROM rating;
DELETE FROM friendship_status;
DELETE FROM friendship;
DELETE FROM users;

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

-- Добавляем пользователей для тестирования
INSERT INTO users (USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES (1, 'user1@test.ru', 'user1', 'User One', '1990-01-01'),
       (2, 'user2@test.ru', 'user2', 'User Two', '1990-01-02'),
       (3, 'user3@test.ru', 'user3', 'User Three', '1990-01-03'),
       (4, 'user4@test.ru', 'user4', 'User Four', '1990-01-04'),
       (5, 'user5@test.ru', 'user5', 'User Five', '1990-01-05');

INSERT INTO film (FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
VALUES (1, 'Начало', 'Фильм о снах', '2010-07-16', 148, 3),
       (2, 'Криминальное чтиво', 'Культовый фильм Тарантино', '1994-05-21', 154, 4),
       (3, 'Парк Юрского периода', 'Динозавры оживают', '1993-06-11', 127, 2),
       (4, 'Титаник', 'История любви на тонущем корабле', '1997-12-19', 195, 3),
       (5, 'Отступники', 'Противостояние полиции и мафии', '2006-09-26', 151, 4),
       (6, 'Update', 'Film about updates', '2020-01-01', 120, 3),
       (7, 'System Update', 'Film about system updates', '2021-01-01', 130, 3);

-- Добавляем жанры к фильмам
INSERT INTO film_genre (FILM_ID, GENRE_ID)
VALUES (1, 4), -- Начало - Триллер
       (2, 6), -- Криминальное чтиво - Боевик
       (2, 1), -- Криминальное чтиво - Комедия
       (3, 6), -- Парк Юрского периода - Боевик
       (4, 2), -- Титаник - Драма
       (5, 4); -- Отступники - Триллер

INSERT INTO film_director (FILM_ID, DIRECTOR_ID)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5),
       (6, 6),
       (7, 7);

-- Добавляем лайки для тестирования поиска и популярности
INSERT INTO film_like (FILM_ID, USER_ID)
VALUES (1, 1), (1, 2), (1, 3), -- Начало - 3 лайка
       (2, 1), (2, 2),         -- Криминальное чтиво - 2 лайка
       (3, 1),                 -- Парк Юрского периода - 1 лайк
       (4, 1), (4, 2), (4, 3), (4, 4), -- Титаник - 4 лайка
       (5, 1), (5, 2),         -- Отступники - 2 лайка
       (6, 1), (6, 2), (7, 1);
