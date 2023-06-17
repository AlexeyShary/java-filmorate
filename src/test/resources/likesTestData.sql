INSERT INTO MPA (MPA_NAME)
VALUES('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
VALUES
('Интерстеллар', 'Крутой фильм Кристофера Нолана под крутую музыку Ханса Циммера', '2014-11-06', 169, 1),
('Стражи галактики', 'Я есть Грут', '2014-07-21', 121, 1),
('tst3', 'tst3', '2014-07-21', 121, 1);

INSERT INTO USERS_FILMS_LIKES (USER_ID, FILM_ID)
VALUES
(1, 1),
(1, 2),
(1, 3),
(2, 2),
(2, 3),
(3, 2);