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
