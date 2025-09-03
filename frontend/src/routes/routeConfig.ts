// src/routes/routeConfig.ts
import React from 'react';

import Home from '@/pages/home';
import Notification from '@/pages/notification/index';
import NotFound from '@/pages/notFound';
import Test from '@/pages/test/index';
import SignUp from '@/pages/signup/index';
import KakaoCallback from '@/pages/auth/KakaoCallback';

import TodaysDiary from '@/pages/findCoin/todaysDiary/index';
import TodaysCategory from '@/pages/findCoin/todaysDiary/categories/index';
import TodaysAiClassify from '@/pages/findCoin/todaysDiary/categories/ai-classify';
import TodaysAdvantageResult from '@/pages/findCoin/todaysDiary/categories/advantageResult/index';
import TodaysCommentAndImage from '@/pages/findCoin/todaysDiary/categories/advantageResult/commentAndImage/index';
import TodaysFinalResult from '@/pages/findCoin/todaysDiary/categories/advantageResult/commentAndImage/finalResult/index';

import CaseReview from '@/pages/findCoin/caseReview/index';
import CaseReviewWriteCase1 from '@/pages/findCoin/caseReview/writeCase1/index';
import CaseReviewWriteCase2 from '@/pages/findCoin/caseReview/writeCase1/writeCase2/index';
import CaseReviewCategory from '@/pages/findCoin/caseReview/writeCase1/writeCase2/categories/index';
import CaseReviewAiClassify from '@/pages/findCoin/caseReview/writeCase1/writeCase2/categories/ai-classify';
import CaseReviewAdvantageResult from '@/pages/findCoin/caseReview/writeCase1/writeCase2/categories/advantageResult/index';
import CaseReviewCommentAndImage from '@/pages/findCoin/caseReview/writeCase1/writeCase2/categories/advantageResult/commentAndImage/index';
import CaseReviewFinalResult from '@/pages/findCoin/caseReview/writeCase1/writeCase2/categories/advantageResult/commentAndImage/finalResult/index';

import MemoriesTogether from '@/pages/findCoin/memories-together/index';
import MemoriesTogetherWriteCase1 from '@/pages/findCoin/memories-together/writeCase1/index';
import MemoriesTogetherWriteCase2 from '@/pages/findCoin/memories-together/writeCase1/writeCase2/index';
import MemoriesTogetherCategory from '@/pages/findCoin/memories-together/writeCase1/writeCase2/categories/index';
import MemoriesTogetherAiClassify from '@/pages/findCoin/memories-together/writeCase1/writeCase2/categories/ai-classify';
import MemoriesTogetherAdvantageResult from '@/pages/findCoin/memories-together/writeCase1/writeCase2/categories/advantageResult/index';
import MemoriesTogetherCommentAndImage from '@/pages/findCoin/memories-together/writeCase1/writeCase2/categories/advantageResult/commentAndImage/index';
import MemoriesTogetherSelectShareType from '@/pages/findCoin/memories-together/writeCase1/writeCase2/categories/advantageResult/commentAndImage/selectShareType/index';
import MemoriesTogetherFinalResult from '@/pages/findCoin/memories-together/writeCase1/writeCase2/categories/advantageResult/commentAndImage/selectShareType/finalResult/index';
import Login from "@/pages/login";

export interface RouteConfig {
    path: string;
    component: React.ComponentType;
    isProtected: boolean;
}

export const routes: RouteConfig[] = [
    // 공개 라우트
    { path: '/login', component: Login, isProtected: false },

    // ✅ 백엔드가 보내는 프론트 콜백 경로로 맞춤
    { path: '/auth/callback', component: KakaoCallback, isProtected: false },

    // (선택) 과거 경로 호환용 – 임시로 동일 컴포넌트 매핑해두면 404 방지됨
    { path: '/auth/kakao/callback', component: KakaoCallback, isProtected: false },

    // 회원가입은 토큰 있어야만 접근하게 하고 싶으면 true로 바꾸세요(권장)
    // 지금은 로그인 직후에만 들어오니 false여도 동작은 합니다.
    { path: '/signup', component: SignUp, isProtected: true },

    // 보호된 라우트
    { path: '/', component: Home, isProtected: true },
    { path: '/home', component: Home, isProtected: true },
    { path: '/notification', component: Notification, isProtected: true },

    // todays
    { path: '/todays-diary', component: TodaysDiary, isProtected: true },
    { path: '/todays-diary/ai-classify', component: TodaysAiClassify, isProtected: true },
    { path: '/todays-diary/categories', component: TodaysCategory, isProtected: true },
    { path: '/todays-diary/advantage-result', component: TodaysAdvantageResult, isProtected: true },
    { path: '/todays-diary/comment-and-image', component: TodaysCommentAndImage, isProtected: true },
    { path: '/todays-diary/final-result', component: TodaysFinalResult, isProtected: true },
    { path: '/test', component: Test, isProtected: true },

    // case-review
    { path: '/case-review', component: CaseReview, isProtected: true },
    { path: '/case-review/write-case-1', component: CaseReviewWriteCase1, isProtected: true },
    { path: '/case-review/write-case-2', component: CaseReviewWriteCase2, isProtected: true },
    { path: '/case-review/categories', component: CaseReviewCategory, isProtected: true },
    { path: '/case-review/ai-classify', component: CaseReviewAiClassify, isProtected: true },
    { path: '/case-review/advantage-result', component: CaseReviewAdvantageResult, isProtected: true },
    { path: '/case-review/comment-and-image', component: CaseReviewCommentAndImage, isProtected: true },
    { path: '/case-review/final-result', component: CaseReviewFinalResult, isProtected: true },

    // memories-together
    { path: '/memories-together', component: MemoriesTogether, isProtected: true },
    { path: '/memories-together/write-case-1', component: MemoriesTogetherWriteCase1, isProtected: true },
    { path: '/memories-together/write-case-2', component: MemoriesTogetherWriteCase2, isProtected: true },
    { path: '/memories-together/categories', component: MemoriesTogetherCategory, isProtected: true },
    { path: '/memories-together/ai-classify', component: MemoriesTogetherAiClassify, isProtected: true },
    { path: '/memories-together/advantage-result', component: MemoriesTogetherAdvantageResult, isProtected: true },
    { path: '/memories-together/comment-and-image', component: MemoriesTogetherCommentAndImage, isProtected: true },
    { path: '/memories-together/select-share', component: MemoriesTogetherSelectShareType, isProtected: true },
    { path: '/memories-together/final-result', component: MemoriesTogetherFinalResult, isProtected: true },

    // 404
    { path: '*', component: NotFound, isProtected: false },
];
