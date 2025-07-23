-- Voin Database Schema for PostgreSQL
-- Story 기반 리팩토링된 구조

-- 1. MEMBERS 테이블
-- 카카오 로그인 정보와 친구 추가 코드를 관리합니다.
CREATE TABLE IF NOT EXISTS members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    kakao_id VARCHAR(100) UNIQUE NOT NULL,
    nickname VARCHAR(50) UNIQUE NOT NULL,
    profile_image_url VARCHAR(500),
    friend_code VARCHAR(5) UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. COINS 테이블 (6개 고정 카테고리)
-- 장점의 대분류를 정의합니다.
CREATE TABLE IF NOT EXISTS coins (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    color VARCHAR(7),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. KEYWORDS 테이블 (각 COIN에 속하는 키워드들)
-- 장점의 소분류를 정의합니다.
CREATE TABLE IF NOT EXISTS keywords (
    id BIGSERIAL PRIMARY KEY,
    coin_id BIGINT NOT NULL REFERENCES coins(id) ON DELETE CASCADE,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. STORIES 테이블 (경험/일기 데이터)
-- 카드(장점) 발견의 기반이 되는 사용자의 이야기입니다.
CREATE TABLE IF NOT EXISTS stories (
    id BIGSERIAL PRIMARY KEY,
    member_id UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL, -- 일기 내용 또는 경험 요약
    story_type VARCHAR(30) NOT NULL, -- DAILY_DIARY, EXPERIENCE_REFLECTION
    situation_context VARCHAR(100), -- 경험 돌아보기의 상황
    answer1 TEXT, -- 다단계 질문 1번 답변
    answer2 TEXT, -- 다단계 질문 2번 답변
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. CARDS 테이블 (사용자가 발견한 장점 카드)
-- 하나의 Story에서 하나의 Keyword를 발견하여 생성됩니다.
CREATE TABLE IF NOT EXISTS cards (
    id BIGSERIAL PRIMARY KEY,
    creator_id UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE, -- 카드 생성자
    owner_id UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,   -- 카드 현재 소유자
    target_member_id UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE, -- 카드에 명시된 대상자
    story_id BIGINT NOT NULL REFERENCES stories(id) ON DELETE CASCADE,
    keyword_id BIGINT NOT NULL REFERENCES keywords(id) ON DELETE CASCADE,
    content VARCHAR(1000), -- 카드에 담긴 메시지 (선택적)
    is_public BOOLEAN DEFAULT FALSE,
    is_gift BOOLEAN DEFAULT FALSE, -- 선물 여부
    situation_context VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. FRIENDS 테이블 (친구 관계)
CREATE TABLE IF NOT EXISTS friends (
    id BIGSERIAL PRIMARY KEY,
    requester_id UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    receiver_id UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'BLOCKED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP WITH TIME ZONE,
    
    CONSTRAINT check_not_self_friend CHECK (requester_id != receiver_id),
    CONSTRAINT unique_friendship UNIQUE (requester_id, receiver_id)
);

-- 7. MEMBER_COINS 테이블 (MEMBER가 가진 COIN 통계)
-- 카드를 통해 얻은 코인의 개수를 추적합니다.
CREATE TABLE IF NOT EXISTS member_coins (
    id BIGSERIAL PRIMARY KEY,
    member_id UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    coin_id BIGINT NOT NULL REFERENCES coins(id) ON DELETE CASCADE,
    count INTEGER DEFAULT 1,
    first_obtained_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_obtained_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT unique_member_coin UNIQUE (member_id, coin_id)
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_members_kakao_id ON members(kakao_id);
CREATE INDEX IF NOT EXISTS idx_members_friend_code ON members(friend_code);
CREATE INDEX IF NOT EXISTS idx_keywords_coin_id ON keywords(coin_id);
CREATE INDEX IF NOT EXISTS idx_stories_member_id ON stories(member_id);
CREATE INDEX IF NOT EXISTS idx_stories_story_type ON stories(story_type);
CREATE INDEX IF NOT EXISTS idx_stories_created_at ON stories(created_at);
CREATE INDEX IF NOT EXISTS idx_cards_owner_id ON cards(owner_id);
CREATE INDEX IF NOT EXISTS idx_cards_creator_id ON cards(creator_id);
CREATE INDEX IF NOT EXISTS idx_cards_story_id ON cards(story_id);
CREATE INDEX IF NOT EXISTS idx_friends_requester_id ON friends(requester_id);
CREATE INDEX IF NOT EXISTS idx_friends_receiver_id ON friends(receiver_id);
CREATE INDEX IF NOT EXISTS idx_member_coins_member_id ON member_coins(member_id);

-- 트리거 함수: updated_at 자동 업데이트
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 트리거 적용
DROP TRIGGER IF EXISTS update_members_updated_at ON members;
DROP TRIGGER IF EXISTS update_stories_updated_at ON stories;
DROP TRIGGER IF EXISTS update_cards_updated_at ON cards;
DROP TRIGGER IF EXISTS update_friends_updated_at ON friends;
DROP TRIGGER IF EXISTS update_member_coins_updated_at ON member_coins;

CREATE TRIGGER update_members_updated_at BEFORE UPDATE ON members FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_stories_updated_at BEFORE UPDATE ON stories FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_cards_updated_at BEFORE UPDATE ON cards FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_friends_updated_at BEFORE UPDATE ON friends FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_member_coins_updated_at BEFORE UPDATE ON member_coins FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 초기 데이터는 DataInitializer.java에서 관리됩니다.
-- 6개 코인과 55개 키워드가 애플리케이션 시작 시 자동으로 삽입됩니다. 