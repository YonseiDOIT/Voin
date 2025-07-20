// 카테고리별 장점 아이콘들을 동적으로 import
// advantageCategory 폴더의 카테고리별 하위 폴더에서 한글 파일명으로 저장된 SVG들을 매핑

// Growth 카테고리 아이콘들
import 끈기Icon from '../assets/svgs/advantageCategory/growth/끈기.svg?react';
import 인내심Icon from '../assets/svgs/advantageCategory/growth/인내심.svg?react';
import 성실함Icon from '../assets/svgs/advantageCategory/growth/성실함.svg?react';
import 침착함Icon from '../assets/svgs/advantageCategory/growth/침착함.svg?react';
import 학습력Icon from '../assets/svgs/advantageCategory/growth/학습력.svg?react';
import 적응력Icon from '../assets/svgs/advantageCategory/growth/적응력.svg?react';
import 수용력Icon from '../assets/svgs/advantageCategory/growth/수용력.svg?react';
import 성찰력Icon from '../assets/svgs/advantageCategory/growth/성찰력.svg?react';
import 절제력Icon from '../assets/svgs/advantageCategory/growth/절제력.svg?react';

// Emotion 카테고리 아이콘들
import 감수성Icon from '../assets/svgs/advantageCategory/emotion/감수성.svg?react';
import 긍정성Icon from '../assets/svgs/advantageCategory/emotion/긍정성.svg?react';
import 밝은에너지Icon from '../assets/svgs/advantageCategory/emotion/밝은 에너지.svg?react';
import 열정Icon from '../assets/svgs/advantageCategory/emotion/열정.svg?react';
import 유머감각Icon from '../assets/svgs/advantageCategory/emotion/유머 감각.svg?react';
import 표현력Icon from '../assets/svgs/advantageCategory/emotion/표현력.svg?react';

// Creativity 카테고리 아이콘들
import 기획력Icon from '../assets/svgs/advantageCategory/creativity/기획력.svg?react';
import 몰입력Icon from '../assets/svgs/advantageCategory/creativity/몰입력.svg?react';
import 집중력Icon from '../assets/svgs/advantageCategory/creativity/집중력.svg?react';
import 창의력Icon from '../assets/svgs/advantageCategory/creativity/창의력.svg?react';
import 호기심Icon from '../assets/svgs/advantageCategory/creativity/호기심.svg?react';
import 탐구력Icon from '../assets/svgs/advantageCategory/creativity/탐구력.svg?react';

// ProblemSolving 카테고리 아이콘들
import 논리력Icon from '../assets/svgs/advantageCategory/problemSolving/논리력.svg?react';
import 문제해결력Icon from '../assets/svgs/advantageCategory/problemSolving/문제 해결력.svg?react';
import 분석력Icon from '../assets/svgs/advantageCategory/problemSolving/분석력.svg?react';
import 신중성Icon from '../assets/svgs/advantageCategory/problemSolving/신중성.svg?react';
import 융통성Icon from '../assets/svgs/advantageCategory/problemSolving/융통성.svg?react';
import 통찰력Icon from '../assets/svgs/advantageCategory/problemSolving/통찰력.svg?react';
import 판단력Icon from '../assets/svgs/advantageCategory/problemSolving/판단력.svg?react';

// Relationship 카테고리 아이콘들
import 겸손함Icon from '../assets/svgs/advantageCategory/relationship/겸손함.svg?react';
import 경청태도Icon from '../assets/svgs/advantageCategory/relationship/경청 태도.svg?react';
import 공감력Icon from '../assets/svgs/advantageCategory/relationship/공감력.svg?react';
import 배려심Icon from '../assets/svgs/advantageCategory/relationship/배려심.svg?react';
import 예의바름Icon from '../assets/svgs/advantageCategory/relationship/예의 바름.svg?react';
import 온화함Icon from '../assets/svgs/advantageCategory/relationship/온화함.svg?react';
import 조율력Icon from '../assets/svgs/advantageCategory/relationship/조율력.svg?react';
import 중재력Icon from '../assets/svgs/advantageCategory/relationship/중재력.svg?react';
import 지지력Icon from '../assets/svgs/advantageCategory/relationship/지지력.svg?react';
import 친화력Icon from '../assets/svgs/advantageCategory/relationship/친화력.svg?react';
import 포용력Icon from '../assets/svgs/advantageCategory/relationship/포용력.svg?react';

// Beliefs 카테고리 아이콘들
import 결단력Icon from '../assets/svgs/advantageCategory/Beliefs/결단력.svg?react';
import 계획성Icon from '../assets/svgs/advantageCategory/Beliefs/계획성.svg?react';
import 공정성Icon from '../assets/svgs/advantageCategory/Beliefs/공정성.svg?react';
import 도덕심Icon from '../assets/svgs/advantageCategory/Beliefs/도덕심.svg?react';
import 도전력Icon from '../assets/svgs/advantageCategory/Beliefs/도전력.svg?react';
import 리더십Icon from '../assets/svgs/advantageCategory/Beliefs/리더십.svg?react';
import 신념Icon from '../assets/svgs/advantageCategory/Beliefs/신념.svg?react';
import 실행력Icon from '../assets/svgs/advantageCategory/Beliefs/실행력.svg?react';
import 용기Icon from '../assets/svgs/advantageCategory/Beliefs/용기.svg?react';
import 정의감Icon from '../assets/svgs/advantageCategory/Beliefs/정의감.svg?react';
import 정직함Icon from '../assets/svgs/advantageCategory/Beliefs/정직함.svg?react';
import 주도성Icon from '../assets/svgs/advantageCategory/Beliefs/주도성.svg?react';
import 주체성Icon from '../assets/svgs/advantageCategory/Beliefs/주체성.svg?react';
import 책임감Icon from '../assets/svgs/advantageCategory/Beliefs/책임감.svg?react';

// 기본 아이콘 (매핑되지 않은 경우 사용)
import DefaultIcon from '../assets/svgs/advantageCategory/growth/끈기.svg?react';

export const iconMap = {
    // Growth 카테고리
    '끈기': 끈기Icon,
    '인내심': 인내심Icon,
    '성실함': 성실함Icon,
    '침착함': 침착함Icon,
    '학습력': 학습력Icon,
    '적응력': 적응력Icon,
    '수용력': 수용력Icon,
    '성찰력': 성찰력Icon,
    '절제력': 절제력Icon,
    
    // Emotion 카테고리
    '감수성': 감수성Icon,
    '긍정성': 긍정성Icon,
    '밝은 에너지': 밝은에너지Icon,
    '열정': 열정Icon,
    '유머 감각': 유머감각Icon,
    '표현력': 표현력Icon,
    
    // Creativity 카테고리
    '기획력': 기획력Icon,
    '몰입력': 몰입력Icon,
    '집중력': 집중력Icon,
    '창의력': 창의력Icon,
    '호기심': 호기심Icon,
    '탐구력': 탐구력Icon,
    
    // ProblemSolving 카테고리
    '논리력': 논리력Icon,
    '문제해결력': 문제해결력Icon,
    '분석력': 분석력Icon,
    '신중성': 신중성Icon,
    '융통성': 융통성Icon,
    '통찰력': 통찰력Icon,
    '판단력': 판단력Icon,
    
    // Relationship 카테고리
    '겸손함': 겸손함Icon,
    '경청 태도': 경청태도Icon,
    '공감력': 공감력Icon,
    '배려심': 배려심Icon,
    '예의 바름': 예의바름Icon,
    '온화함': 온화함Icon,
    '조율력': 조율력Icon,
    '중재력': 중재력Icon,
    '지지력': 지지력Icon,
    '친화력': 친화력Icon,
    '포용력': 포용력Icon,
    
    // Beliefs 카테고리
    '결단력': 결단력Icon,
    '계획성': 계획성Icon,
    '공정성': 공정성Icon,
    '도덕심': 도덕심Icon,
    '도전력': 도전력Icon,
    '리더십': 리더십Icon,
    '신념': 신념Icon,
    '실행력': 실행력Icon,
    '용기': 용기Icon,
    '정의감': 정의감Icon,
    '정직함': 정직함Icon,
    '주도성': 주도성Icon,
    '주체성': 주체성Icon,
    '책임감': 책임감Icon,
} as const;

export type IconName = keyof typeof iconMap;

export { DefaultIcon };

// 아이콘 가져오기 헬퍼 함수
export const getAdvantageIcon = (itemName: string) => {
    return iconMap[itemName as IconName] || DefaultIcon;
};
