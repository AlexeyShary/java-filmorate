DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS GENRES (
	GENRE_ID BIGINT NOT NULL AUTO_INCREMENT,
	GENRE_NAME CHARACTER VARYING NOT NULL,
	CONSTRAINT GENRES_PK PRIMARY KEY (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS MPA (
	MPA_ID BIGINT NOT NULL AUTO_INCREMENT,
	MPA_NAME CHARACTER VARYING NOT NULL,
	CONSTRAINT MPA_PK PRIMARY KEY (MPA_ID)
);

CREATE TABLE IF NOT EXISTS USERS (
	USER_ID BIGINT NOT NULL AUTO_INCREMENT,
	EMAIL CHARACTER VARYING NOT NULL,
	LOGIN CHARACTER VARYING NOT NULL,
	USER_NAME CHARACTER VARYING NOT NULL,
	BIRTHDAY DATE NOT NULL,
	CONSTRAINT USERS_PK PRIMARY KEY (USER_ID)
);

CREATE TABLE IF NOT EXISTS FILMS (
	FILM_ID BIGINT NOT NULL AUTO_INCREMENT,
	FILM_NAME CHARACTER VARYING NOT NULL,
	DESCRIPTION CHARACTER VARYING,
	RELEASE_DATE DATE NOT NULL,
	DURATION INTEGER NOT NULL,
	MPA_ID BIGINT,
	CONSTRAINT FILMS_PK PRIMARY KEY (FILM_ID)
);

CREATE TABLE IF NOT EXISTS FILMS_GENRES (
	FILM_ID BIGINT NOT NULL,
	GENRE_ID BIGINT NOT NULL,
	CONSTRAINT FILMS_GENRES_PK PRIMARY KEY (GENRE_ID,FILM_ID),
	CONSTRAINT FILMS_GENRES_PK_F FOREIGN KEY (FILM_ID) REFERENCES FILMS(FILM_ID) ON DELETE CASCADE,
	CONSTRAINT FILMS_GENRES_PK_G FOREIGN KEY (GENRE_ID) REFERENCES GENRES(GENRE_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS USERS_FILMS_LIKES (
    LIKE_ID BIGINT NOT NULL AUTO_INCREMENT,
	USER_ID BIGINT NOT NULL,
	FILM_ID BIGINT NOT NULL,
	CONSTRAINT USERS_FILMS_LIKES_PK PRIMARY KEY (LIKE_ID),
	CONSTRAINT USERS_FILMS_LIKES_PK_U FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE,
	CONSTRAINT USERS_FILMS_LIKES_PK_F FOREIGN KEY (FILM_ID) REFERENCES FILMS(FILM_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS USERS_FRIENDSHIP (
	USER_FROM BIGINT NOT NULL,
	USER_TO BIGINT NOT NULL,
	CONSTRAINT USERS_FRIENDSHIP_PK PRIMARY KEY (USER_FROM,USER_TO),
	CONSTRAINT USERS_FRIENDSHIP_PK_UF FOREIGN KEY (USER_FROM) REFERENCES USERS(USER_ID) ON DELETE CASCADE,
	CONSTRAINT USERS_FRIENDSHIP_PK_UT FOREIGN KEY (USER_TO) REFERENCES USERS(USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS DIRECTORS (
	DIRECTOR_ID BIGINT NOT NULL AUTO_INCREMENT,
	DIRECTOR_NAME CHARACTER VARYING NOT NULL,
	CONSTRAINT DIRECTORS_PK PRIMARY KEY (DIRECTOR_ID)
);

CREATE TABLE IF NOT EXISTS FILMS_DIRECTORS (
	FILM_ID BIGINT NOT NULL,
	DIRECTOR_ID BIGINT NOT NULL,
	CONSTRAINT FILMS_DIRECTORS_PK PRIMARY KEY (DIRECTOR_ID,FILM_ID),
	CONSTRAINT FILMS_DIRECTORS_FK_D FOREIGN KEY (DIRECTOR_ID) REFERENCES DIRECTORS(DIRECTOR_ID) ON DELETE CASCADE,
	CONSTRAINT FILMS_DIRECTORS_FK_F FOREIGN KEY (FILM_ID) REFERENCES FILMS(FILM_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REVIEWS (
    REVIEW_ID   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    CONTENT     varchar NOT NULL,
    IS_POSITIVE boolean NOT NULL,
    USER_ID   BIGINT NOT NULL REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    FILM_ID  BIGINT NOT NULL REFERENCES FILMS (FILM_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REVIEW_LIKES (
    REVIEW_ID   BIGINT REFERENCES REVIEWS (REVIEW_ID) ON DELETE CASCADE,
    USER_ID   BIGINT REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    IS_POSITIVE boolean,
    PRIMARY KEY (REVIEW_ID, USER_ID)
    );

CREATE TABLE IF NOT EXISTS USERS_EVENTS
(
    EVENT_ID BIGINT NOT NULL AUTO_INCREMENT,
    USER_ID BIGINT NOT NULL,
    ENTITY_ID BIGINT NOT NULL,
    EVENT_TYPE_ID BIGINT NOT NULL,
    EVENT_OPERATION_ID BIGINT NOT NULL,
    EVENT_TIMESTAMP BIGINT NOT NULL,
    CONSTRAINT USERS_EVENTS_PK PRIMARY KEY (EVENT_ID)
);

CREATE TABLE IF NOT EXISTS EVENTS_TYPES
(
    EVENT_TYPE_ID BIGINT NOT NULL AUTO_INCREMENT,
    EVENT_TYPE_NAME VARCHAR NOT NULL,
    CONSTRAINT EVENTS_TYPES_PK PRIMARY KEY (EVENT_TYPE_ID)
);

CREATE TABLE IF NOT EXISTS EVENTS_OPERATIONS
(
    EVENT_OPERATION_ID BIGINT NOT NULL AUTO_INCREMENT,
    EVENT_OPERATION_NAME VARCHAR NOT NULL,
    CONSTRAINT EVENT_OPERATIONS_PK PRIMARY KEY (EVENT_OPERATION_ID)
);