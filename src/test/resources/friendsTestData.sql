INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY)
VALUES
('Matthew@McConaughey.com', 'Matt', 'Matthew McConaughey', '1969-11-04'),
('Groot@Iam.Groot', 'Groot', 'Groot', '2014-03-24'),
('chan@ya.ru', 'ChonWang', 'Jackie Chan', '1954-04-07'),
('murray@ya.ru','DrPeterVenkman','Bill Murray','1950-09-21');

INSERT INTO USERS_FRIENDSHIP (USER_FROM, USER_TO)
VALUES
(1, 2),
(2, 1),
(1, 3),
(3, 1),
(2, 3),
(3, 2);