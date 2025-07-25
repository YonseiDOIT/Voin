import Home from '../pages/home';
import Notification from '../pages/notification/index';
import NotFound from '../pages/notFound';
import Test from '../pages/test/index';
import SignUp from '../pages/signup/index';
import TodaysDiary from '../pages/findCoin/todaysDiary/index';
import TodaysCategory from '../pages/findCoin/todaysDiary/categories/index';
import Login from '../pages/login/index';
import KakaoCallback from '../pages/auth/KakaoCallback';
import CaseReview from '../pages/findCoin/caseReview/index';
import CaseReviewWriteCase1 from '../pages/findCoin/caseReview/writeCase1/index';
import CaseReviewWriteCase2 from '../pages/findCoin/caseReview/writeCase1/writeCase2/index';
import CaseReviewCategory from '../pages/findCoin/caseReview/writeCase1/writeCase2/categories/index';
import CaseReviewAdvantageResult from '../pages/findCoin/caseReview/writeCase1/writeCase2/categories/advantageResult/index';
import CaseReviewResult from '../pages/findCoin/caseReview/writeCase1/writeCase2/categories/advantageResult/index';
import CaseReviewCommentAndImage from '../pages/findCoin/caseReview/writeCase1/writeCase2/categories/advantageResult/commentAndImage/index';
import CaseReviewFinalResult from '../pages/findCoin/caseReview/writeCase1/writeCase2/categories/advantageResult/commentAndImage/finalResult/index';
import React from 'react';

export interface RouteConfig {
    path: string;
    component: React.ComponentType;
    isProtected: boolean;
}

export const routes: RouteConfig[] = [
    // 공개 라우트
    {
        path: '/login',
        component: Login,
        isProtected: false,
    },
    {
        path: '/auth/kakao/callback',
        component: KakaoCallback,
        isProtected: false,
    },
    {
        path: '/signup',
        component: SignUp,
        isProtected: false,
    },

    // 보호된 라우트
    {
        path: '/',
        component: Home,
        isProtected: true,
    },
    {
        path: '/home',
        component: Home,
        isProtected: true,
    },
    {
        path: '/notification',
        component: Notification,
        isProtected: true,
    },
    {
        path: '/todays-diary',
        component: TodaysDiary,
        isProtected: true,
    },
    {
        path: '/todays-diary/categories',
        component: TodaysCategory,
        isProtected: true,
    },
    {
        path: '/test',
        component: Test,
        isProtected: true,
    },
    {
        path: '/case-review',
        component: CaseReview,
        isProtected: true,
    },
    {
        path: '/case-review/write-case-1',
        component: CaseReviewWriteCase1,
        isProtected: true,
    },
    {
        path: '/case-review/write-case-2',
        component: CaseReviewWriteCase2,
        isProtected: true,
    },
    {
        path: '/case-review/categories',
        component: CaseReviewCategory,
        isProtected: true,
    },
    {
        path: '/case-review/advantage-result',
        component: CaseReviewAdvantageResult,
        isProtected: true,
    },
    {
        path: '/case-review/result',
        component: CaseReviewResult,
        isProtected: true,
    },
    {
        path: '/case-review/comment-and-image',
        component: CaseReviewCommentAndImage,
        isProtected: true,
    },
    {
        path: '/case-review/final-result',
        component: CaseReviewFinalResult,
        isProtected: true,
    },
    // 404 페이지
    {
        path: '*',
        component: NotFound,
        isProtected: false,
    },
];
