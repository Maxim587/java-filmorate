CREATE TABLE IF NOT EXISTS film
(
    film_id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    varchar
(
    100
) NOT NULL,
    description varchar
(
    200
),
    release_date date NOT NULL,
    duration integer NOT NULL,
    rating_id integer NOT NULL,
    CONSTRAINT name_not_blank CHECK
(
    name
    <>
    ''
),
    CONSTRAINT positive_duration CHECK
(
    duration >
    0
)
    );

CREATE TABLE IF NOT EXISTS film_like
(
    film_id
    integer
    NOT
    NULL,
    user_id
    integer
    NOT
    NULL
);

CREATE TABLE IF NOT EXISTS genre
(
    genre_id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    varchar
(
    50
) NOT NULL
    );

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id
    integer
    NOT
    NULL,
    genre_id
    integer
    NOT
    NULL
);

CREATE TABLE IF NOT EXISTS rating
(
    rating_id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    varchar
(
    50
) NOT NULL,
    CONSTRAINT rating_name_values CHECK
(
    name
    IN
(
    'G',
    'PG',
    'PG-13',
    'R',
    'NC-17'
))
    );

CREATE TABLE IF NOT EXISTS users
(
    user_id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    email
    varchar
(
    320
) NOT NULL,
    login varchar
(
    100
) NOT NULL,
    name varchar
(
    100
) NOT NULL,
    birthday date NOT NULL,
    CONSTRAINT email_not_blank CHECK
(
    email
    <>
    ''
)
    );

CREATE TABLE IF NOT EXISTS friendship
(
    user_id
    integer
    NOT
    NULL,
    friend_id
    integer
    NOT
    NULL,
    friendship_status_id
    integer
    NOT
    NULL
);

CREATE TABLE IF NOT EXISTS friendship_status
(
    friendship_status_id
    integer
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    status
    varchar
    NOT
    NULL,
    CONSTRAINT
    status_values
    CHECK (
    status
    IN
(
    'NOT_CONFIRMED',
    'CONFIRMED'
))
    );

ALTER TABLE film
    ADD FOREIGN KEY (rating_id) REFERENCES rating (rating_id);

ALTER TABLE film_like
    ADD FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE;

ALTER TABLE film_like
    ADD FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE film_genre
    ADD FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE;

ALTER TABLE film_genre
    ADD FOREIGN KEY (genre_id) REFERENCES genre (genre_id) ON DELETE CASCADE;

ALTER TABLE friendship
    ADD FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE friendship
    ADD FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE friendship
    ADD FOREIGN KEY (friendship_status_id) REFERENCES friendship_status (friendship_status_id);

CREATE INDEX IF NOT EXISTS idx_friendship ON friendship(user_id, friend_id);