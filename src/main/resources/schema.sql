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
	USER_ID BIGINT NOT NULL,
	FILM_ID BIGINT NOT NULL,
	CONSTRAINT USERS_FILMS_LIKES_PK PRIMARY KEY (FILM_ID,USER_ID),
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

CREATE TABLE IF NOT EXISTS `REVIEWS` (
    `REVIEW_ID`   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    `CONTENT`     varchar NOT NULL,
    `IS_POSITIVE` boolean NOT NULL,
    `USER_ID`   BIGINT NOT NULL REFERENCES `USERS` (`USER_ID`) ON DELETE CASCADE,
    `FILM_ID`  BIGINT NOT NULL REFERENCES FILMS (`FILM_ID`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `REVIEW_LIKES` (
    `REVIEW_ID`   BIGINT REFERENCES `REVIEWS` (REVIEW_ID) ON DELETE CASCADE,
    `USER_ID`   BIGINT REFERENCES `USERS` (USER_ID) ON DELETE CASCADE,
    `IS_POSITIVE` boolean,
    PRIMARY KEY (`REVIEW_ID`, `USER_ID`)
);