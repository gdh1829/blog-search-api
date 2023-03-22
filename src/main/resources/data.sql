
INSERT INTO BLOGSEARCHPRIORITY (version, createdTime, updatedTime, `source`, use, priority) VALUES
(0, now(), now(), 'KAKAO', true, 0),
(0, now(), now(), 'NAVER', true, 1)
;

INSERT INTO KEYWORDSTATISTICS(keyword, createdTime, updatedTime, deletedTime, searchCount) VALUES
('사과', now(), now(), null, 5),
('제주도', now(), now(), null, 10),
('여행', now(), now(), null, 3),
('프로그래밍', now(), now(), null, 7),
('코틀린', now(), now(), null, 2),
('농구', now(), now(), null, 10),
('브런치', now(), now(), null, 3),
('일기', now(), now(), null, 5),
('깃헙', now(), now(), null, 6),
('맥북', now(), now(), null, 9),
('전래동화', now(), now(), null, 6)
;
