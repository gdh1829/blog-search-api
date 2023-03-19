
INSERT INTO BLOGSEARCHPRIORITY (version, createdTime, updatedTime, `source`, use, priority) VALUES
(0, now(), now(), 'KAKAO', true, 0),
(0, now(), now(), 'NAVER', true, 1)
;

INSERT INTO KEYWORDSTATISTICS(keyword, createdTime, updatedTime, deletedTime, searchCount) VALUES
('사과', now(), now(), null, 40),
('제주도', now(), now(), null, 30),
('여행', now(), now(), null, 10),
('프로그래밍', now(), now(), null, 26),
('코틀린', now(), now(), null, 10),
('농구', now(), now(), null, 10),
('브런치', now(), now(), null, 50),
('일기', now(), now(), null, 10),
('깃헙', now(), now(), null, 11),
('맥북', now(), now(), null, 30),
('전래동화', now(), now(), null, 7)
;
