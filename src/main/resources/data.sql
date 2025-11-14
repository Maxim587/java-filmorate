MERGE INTO public.friendship_status (FRIENDSHIP_STATUS_ID, STATUS)
    VALUES (1, 'CONFIRMED'),
    (2, 'NOT_CONFIRMED');

MERGE INTO rating (RATING_ID, NAME)
    VALUES (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');

MERGE INTO genre (GENRE_ID, NAME)
    VALUES (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');

MERGE INTO directors (DIRECTOR_ID, NAME) KEY(DIRECTOR_ID)
    VALUES
    (1, 'Кристофер Нолан'),
    (2, 'Квентин Тарантино'),
    (3, 'Стивен Спилберг'),
    (4, 'Джеймс Кэмерон'),
    (5, 'Мартин Скорсезе'),
    (6, 'Дэвид Финчер'),
    (7, 'Питер Джексон'),
    (8, 'Ридли Скотт'),
    (9, 'Вуди Аллен'),
    (10, 'Алексей Герман');

MERGE INTO users (USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY) KEY(USER_ID)
VALUES
(1, 'user1@example.com', 'user1', 'User One', '1990-01-01'),
(2, 'user2@example.com', 'user2', 'User Two', '1990-02-02'),
(3, 'user3@example.com', 'user3', 'User Three', '1990-03-03');

MERGE INTO film (FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) KEY(FILM_ID)
    VALUES (1, 'Начало', 'Фильм о снах', '2010-07-16', 148, 3),
    (2, 'Криминальное чтиво', 'Культовый фильм Тарантино', '1994-05-21', 154, 4),
    (3, 'Парк Юрского периода', 'Динозавры оживают', '1993-06-11', 127, 2),
    (4, 'Титаник', 'История любви на тонущем корабле', '1997-12-19', 195, 3),
    (5, 'Отступники', 'Противостояние полиции и мафии', '2006-09-26', 151, 4);

MERGE INTO film_director (FILM_ID, DIRECTOR_ID) KEY(FILM_ID, DIRECTOR_ID)
    VALUES (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5);

-- Добавьте в конец data.sql:
MERGE INTO users (USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY) KEY(USER_ID)
VALUES
(1, 'user1@example.com', 'user1', 'User One', '1990-01-01'),
(2, 'user2@example.com', 'user2', 'User Two', '1990-02-02'),
(3, 'user3@example.com', 'user3', 'User Three', '1990-03-03');

-- Добавьте несколько лайков для тестирования:
MERGE INTO film_like (FILM_ID, USER_ID) KEY(FILM_ID, USER_ID)
VALUES
(1, 1),
(1, 2),
(2, 1),
(3, 1),
(3, 2),
(3, 3);

-- Жанры для фильмов
MERGE INTO film_genre (FILM_ID, GENRE_ID) KEY(FILM_ID, GENRE_ID)
VALUES
(1, 1), (1, 2),  -- Начало: Комедия, Драма
(2, 6),          -- Криминальное чтиво: Боевик
(3, 4), (3, 6),  -- Парк Юрского периода: Триллер, Боевик
(4, 2),          -- Титаник: Драма
(5, 2), (5, 4);  -- Отступники: Драма, Триллер
