INSERT INTO USERS (name, role, email, password)
VALUES ('어드민', 'ADMIN', 'admin@email.com', 'password'),
       ('사용자', 'USER', 'user@email.com', 'userpass'),
       ('라젤', 'USER', 'razel@email.com', 'razelpass'),
       ('포포', 'USER', 'popo@email.com', 'popopass');

INSERT INTO TIME_SLOT (start_at)
VALUES ('10:00'),
       ('12:00'),
       ('14:00'),
       ('16:00'),
       ('18:00');

INSERT INTO THEME (name, description, thumbnail)
VALUES ('인기 테마 3순위 테마', '나는 1번째 테마지만, 인기 순위는 3위야',
        'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('인기 테마 2순위 테마', '나는 2번째 테마지만, 인기 순위는 2위야',
        'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('인기 테마 1순위 테마', '나는 3번째 테마지만, 인기 순위는 1위야',
        'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('인기 테마 4순위 테마', '나는 4번째 테마지만, 인기 순위는 4위야',
        'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('인기 테마 5순위 테마', '나는 5번째 테마지만, 인기 순위는 5위야',
        'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('인기 테마 6순위 테마', '나는 6번째 테마지만, 인기 순위는 6위야',
        'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

-- 현재 날짜로부터 3일 전 날짜를 동적으로 계산하여 삽입
INSERT INTO RESERVATION (user_id, date, time_slot_id, theme_id)
VALUES (2, DATEADD(DAY, -3, CURRENT_DATE), 1, 3),
       (3, DATEADD(DAY, -3, CURRENT_DATE), 5, 3),
       (4, DATEADD(DAY, -3, CURRENT_DATE), 5, 3),
       (2, DATEADD(DAY, -3, CURRENT_DATE), 3, 3),
       (3, DATEADD(DAY, -3, CURRENT_DATE), 1, 3),
       (4, DATEADD(DAY, -3, CURRENT_DATE), 2, 2),
       (2, DATEADD(DAY, -3, CURRENT_DATE), 1, 2),
       (3, DATEADD(DAY, -3, CURRENT_DATE), 1, 2),
       (4, DATEADD(DAY, -3, CURRENT_DATE), 2, 2),
       (2, DATEADD(DAY, -3, CURRENT_DATE), 3, 1),
       (3, DATEADD(DAY, -3, CURRENT_DATE), 4, 1),
       (4, DATEADD(DAY, -3, CURRENT_DATE), 5, 1),
       (2, DATEADD(DAY, -3, CURRENT_DATE), 2, 4),
       (3, DATEADD(DAY, -3, CURRENT_DATE), 3, 4),
       (4, DATEADD(DAY, -3, CURRENT_DATE), 3, 5),
       (2, DATEADD(DAY, -3, CURRENT_DATE), 4, 5),
       (3, DATEADD(DAY, -3, CURRENT_DATE), 4, 6);
