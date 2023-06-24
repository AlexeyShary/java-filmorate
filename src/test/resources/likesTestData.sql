INSERT INTO MPA (MPA_NAME)
VALUES('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

INSERT INTO GENRES (GENRE_NAME)
VALUES('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
VALUES
('Интерстеллар', 'Крутой фильм Кристофера Нолана под крутую музыку Ханса Циммера', '2014-11-06', 169, 1),
('Стражи галактики', 'Я есть Грут', '2014-07-21', 121, 1),
('tst3', 'tst3', '2014-07-21', 121, 1),
('tst4', 'tst4', '2017-07-21', 121, 2),
('tst5', 'tst5', '1988-07-21', 121, 3);

INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY)
VALUES
('Matthew@McConaughey.com', 'Matt', 'Matthew McConaughey', '1969-11-04'),
('Groot@Iam.Groot', 'Groot', 'Groot', '2014-03-24'),
('chan@ya.ru', 'ChonWang', 'Jackie Chan', '1954-04-07'),
('murray@ya.ru','DrPeterVenkman','Bill Murray','1950-09-21');

INSERT INTO USERS_FILMS_LIKES (USER_ID, FILM_ID)
VALUES
(1, 1),
(1, 2),
(1, 3),
(2, 2),
(2, 3),
(3, 2);

INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID)
VALUES
(1,4),
(2,1),
(3,4),
(4,3),
(5,3);