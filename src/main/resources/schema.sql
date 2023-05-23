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
	CONSTRAINT FILMS_GENRES_PK PRIMARY KEY (GENRE_ID,FILM_ID)
);

CREATE TABLE IF NOT EXISTS USERS_FILMS_LIKES (
	USER_ID BIGINT NOT NULL,
	FILM_ID BIGINT NOT NULL,
	CONSTRAINT USERS_FILMS_LIKES_PK PRIMARY KEY (FILM_ID,USER_ID)
);

CREATE TABLE IF NOT EXISTS USERS_FRIENDSHIP (
	USER_FROM BIGINT NOT NULL,
	USER_TO BIGINT NOT NULL,
	CONSTRAINT USERS_FRIENDSHIP_PK PRIMARY KEY (USER_FROM,USER_TO)
);