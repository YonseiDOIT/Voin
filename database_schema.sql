-- Voin Database Schema for PostgreSQL
-- 새로운 요구사항에 따른 테이블 설계

-- 1. MEMBERS 테이블
CREATE TABLE members (
    id BIGSERIAL PRIMARY KEY,
    kakao_id VARCHAR(100) UNIQUE NOT NULL,
    nickname VARCHAR(50) UNIQUE NOT NULL,
    profile_image VARCHAR(500),
    friend_code VARCHAR(8) UNIQUE NOT NULL, -- 고유 코드 (친구 추가용)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. COINS 테이블 (정해진 6개 카테고리)
CREATE TABLE coins (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL, -- '관리와 성장', '감정과 태도', 등
    description TEXT,
    color VARCHAR(7), -- 색상 코드 (예: #FF5733)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. KEYWORDS 테이블 (각 COIN에 속하는 키워드들)
CREATE TABLE keywords (
    id BIGSERIAL PRIMARY KEY,
    coin_id BIGINT NOT NULL REFERENCES coins(id) ON DELETE CASCADE,
    name VARCHAR(100) UNIQUE NOT NULL, -- 키워드명
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. FORMS 테이블 (3가지 방식)
CREATE TABLE forms (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('DAILY_DIARY', 'EXPERIENCE_REFLECTION', 'FRIEND_STRENGTH')),
    content JSONB, -- 폼의 질문들과 구조를 JSON으로 저장
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. CARDS 테이블
CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE, -- 작성자
    target_member_id BIGINT REFERENCES members(id) ON DELETE CASCADE, -- 대상자 (친구의 장점 찾아주기 경우)
    form_id BIGINT NOT NULL REFERENCES forms(id) ON DELETE CASCADE,
    keyword_id BIGINT NOT NULL REFERENCES keywords(id) ON DELETE CASCADE, -- 단 하나의 키워드
    content TEXT NOT NULL, -- 카드 내용
    form_response JSONB, -- 폼 응답 내용을 JSON으로 저장
    is_public BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- target_member_id가 있으면 친구에게 만들어준 카드, 없으면 자신의 카드
    CONSTRAINT check_target_member CHECK (
        (target_member_id IS NULL) OR 
        (target_member_id IS NOT NULL AND target_member_id != member_id)
    )
);

-- 6. FRIENDS 테이블 (친구 관계 및 COIN 공유 추적)
CREATE TABLE friends (
    id BIGSERIAL PRIMARY KEY,
    requester_id BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE, -- 요청자
    receiver_id BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE, -- 수신자
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'BLOCKED')),
    coin_share_count INTEGER DEFAULT 0, -- 함께 공유한 COIN 개수
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP, -- 수락 시간
    
    -- 자기 자신과는 친구가 될 수 없음
    CONSTRAINT check_not_self_friend CHECK (requester_id != receiver_id),
    -- 같은 사람 간의 중복 친구 요청 방지
    CONSTRAINT unique_friendship UNIQUE (requester_id, receiver_id)
);

-- 7. MEMBER_COINS 테이블 (MEMBER가 가진 COIN들 추적)
CREATE TABLE member_coins (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    coin_id BIGINT NOT NULL REFERENCES coins(id) ON DELETE CASCADE,
    count INTEGER DEFAULT 1, -- 해당 COIN을 몇 개나 가지고 있는지
    first_obtained_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_obtained_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 한 멤버가 같은 COIN을 중복으로 가지는 것을 방지하고 count로 관리
    CONSTRAINT unique_member_coin UNIQUE (member_id, coin_id)
);

-- 인덱스 생성
CREATE INDEX idx_members_kakao_id ON members(kakao_id);
CREATE INDEX idx_members_friend_code ON members(friend_code);
CREATE INDEX idx_keywords_coin_id ON keywords(coin_id);
CREATE INDEX idx_cards_member_id ON cards(member_id);
CREATE INDEX idx_cards_target_member_id ON cards(target_member_id);
CREATE INDEX idx_cards_keyword_id ON cards(keyword_id);
CREATE INDEX idx_friends_requester_id ON friends(requester_id);
CREATE INDEX idx_friends_receiver_id ON friends(receiver_id);
CREATE INDEX idx_friends_status ON friends(status);
CREATE INDEX idx_member_coins_member_id ON member_coins(member_id);
CREATE INDEX idx_member_coins_coin_id ON member_coins(coin_id);

-- 트리거 함수: updated_at 자동 업데이트
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 트리거 적용
CREATE TRIGGER update_members_updated_at BEFORE UPDATE ON members FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_forms_updated_at BEFORE UPDATE ON forms FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_cards_updated_at BEFORE UPDATE ON cards FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_friends_updated_at BEFORE UPDATE ON friends FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 초기 데이터 삽입: 6개 기본 COIN
INSERT INTO coins (name, description, color) VALUES
('관리와 성장', '시간 관리, 목표 설정, 자기계발 등 개인의 성장을 도모하는 능력', '#FF6B6B'),
('감정과 태도', '감정 조절, 긍정적 태도, 스트레스 관리 등 정서적 역량', '#4ECDC4'),
('창의와 몰입', '창의적 사고, 집중력, 몰입 상태 등 창작과 관련된 능력', '#45B7D1'),
('사고와 해결', '논리적 사고, 문제 해결, 분석 능력 등 인지적 역량', '#96CEB4'),
('관계와 공감', '소통 능력, 공감 능력, 팀워크 등 대인관계 역량', '#FFEAA7'),
('신념과 실행', '의지력, 실행력, 책임감 등 행동으로 옮기는 능력', '#DDA0DD');

-- 초기 데이터 삽입: 각 COIN별 예시 KEYWORD들
INSERT INTO keywords (coin_id, name, description) VALUES
-- 관리와 성장
(1, '시간관리', '주어진 시간을 효율적으로 활용하는 능력'),
(1, '목표달성', '설정한 목표를 끝까지 이루어내는 능력'),
(1, '계획수립', '체계적이고 실현 가능한 계획을 세우는 능력'),
(1, '자기계발', '지속적으로 자신을 발전시키려는 의지'),

-- 감정과 태도  
(2, '긍정사고', '어려운 상황에서도 긍정적으로 바라보는 능력'),
(2, '감정조절', '자신의 감정을 적절히 컨트롤하는 능력'),
(2, '스트레스관리', '스트레스 상황을 효과적으로 대처하는 능력'),
(2, '인내심', '어려움을 참고 견뎌내는 정신력'),

-- 창의와 몰입
(3, '창의적사고', '기존과 다른 새로운 아이디어를 생각해내는 능력'),
(3, '집중력', '한 가지 일에 깊이 몰두할 수 있는 능력'),
(3, '상상력', '현실을 넘어서는 풍부한 상상을 하는 능력'),
(3, '혁신적사고', '기존의 틀을 깨고 새로운 방식을 찾는 능력'),

-- 사고와 해결
(4, '논리적사고', '체계적이고 합리적으로 생각하는 능력'),
(4, '문제해결', '복잡한 문제를 분석하고 해결책을 찾는 능력'),
(4, '분석력', '정보를 세밀하게 분석하고 패턴을 찾는 능력'),
(4, '비판적사고', '정보를 객관적으로 평가하고 판단하는 능력'),

-- 관계와 공감
(5, '소통능력', '상대방과 효과적으로 의사소통하는 능력'),
(5, '공감능력', '다른 사람의 감정과 상황을 이해하는 능력'),
(5, '팀워크', '다른 사람들과 협력하여 목표를 달성하는 능력'),
(5, '리더십', '다른 사람들을 이끌고 동기부여하는 능력'),

-- 신념과 실행
(6, '실행력', '계획한 것을 실제로 행동으로 옮기는 능력'),
(6, '책임감', '자신의 역할과 의무를 충실히 이행하는 자세'),
(6, '의지력', '어려움이 있어도 포기하지 않는 강한 정신력'),
(6, '신뢰성', '약속을 지키고 일관된 모습을 보이는 능력');

-- 초기 데이터 삽입: 3가지 FORM 타입
INSERT INTO forms (title, type, content) VALUES
('오늘의 일기', 'DAILY_DIARY', '{"questions": [{"type": "textarea", "question": "오늘 하루 어떤 일이 있었나요?", "required": true}, {"type": "textarea", "question": "오늘 나의 강점이 드러난 순간이 있었다면?", "required": true}]}'),

('경험 돌아보기', 'EXPERIENCE_REFLECTION', '{"questions": [{"type": "textarea", "question": "기억에 남는 경험을 하나 떠올려보세요.", "required": true}, {"type": "textarea", "question": "그 경험에서 당신이 보여준 강점은 무엇인가요?", "required": true}, {"type": "textarea", "question": "그 강점이 어떻게 도움이 되었나요?", "required": true}, {"type": "select", "question": "이 강점과 가장 관련 있는 영역은?", "options": ["관리와 성장", "감정과 태도", "창의와 몰입", "사고와 해결", "관계와 공감", "신념과 실행"], "required": true}]}'),

('친구의 장점 찾아주기', 'FRIEND_STRENGTH', '{"questions": [{"type": "select", "question": "장점을 찾아줄 친구를 선택하세요", "source": "friends", "required": true}, {"type": "textarea", "question": "친구의 어떤 모습에서 강점을 발견했나요?", "required": true}, {"type": "textarea", "question": "구체적으로 어떤 상황에서 그 강점이 드러났나요?", "required": true}, {"type": "textarea", "question": "친구에게 전하고 싶은 메시지가 있다면?", "required": false}]}'); 