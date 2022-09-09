CREATE TABLE IF NOT EXISTS users (
    user_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(255),
    user_name VARCHAR(255),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS requests (
    request_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description VARCHAR(1000),
    requester_id INT REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
    item_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    item_name VARCHAR(255),
    description VARCHAR(1000),
    is_available BOOLEAN DEFAULT FALSE,
    owner_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    request_id INT
    );

CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    booking_start TIMESTAMP WITHOUT TIME ZONE,
    booking_end TIMESTAMP WITHOUT TIME ZONE,
    item_id INT REFERENCES items(item_id) ON DELETE CASCADE,
    booker_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    status VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    comment_text VARCHAR(1000),
    item_id INT REFERENCES items(item_id) ON DELETE CASCADE,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE
);
