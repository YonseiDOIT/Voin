import Home from '../pages/home';
import Notification from '../pages/notification/index';
import NotFound from '../pages/notFound';
import Test from '../pages/test/index';
import SignUp from '../pages/signup/index';
import TodaysDiary from '../pages/findCoin/todaysDiary/index';
import TodaysCategory from '../pages/findCoin/todaysDiary/categories/index';
import Login from '../pages/login/index';
import CaseReview from '../pages/findCoin/caseReview/index';
import WriteCase from '../pages/findCoin/caseReview/writeCase/index';
import CaseReviewCategory from '../pages/findCoin/caseReview/categories/index';
import AdvantageResultPage from '../pages/findCoin/advantageResult/index';
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
        path: '/signup',
        component: SignUp,
        isProtected: false,
    },

    // 보호된 라우트
    {
        path: '/',
        component: Home,
        isProtected: false,
    },
    {
        path: '/home',
        component: Home,
        isProtected: false,
    },
    {
        path: '/notification',
        component: Notification,
        isProtected: false,
    },
    {
        path: '/todays-diary',
        component: TodaysDiary,
        isProtected: false,
    },
    {
        path: '/todays-diary/categories',
        component: TodaysCategory,
        isProtected: false,
    },
    {
        path: '/test',
        component: Test,
        isProtected: false,
    },
    {
        path: '/case-review',
        component: CaseReview,
        isProtected: false,
    },
    {
        path: '/case-review/write',
        component: WriteCase,
        isProtected: false,
    },
    {
        path: '/case-review/categories',
        component: CaseReviewCategory,
        isProtected: false,
    },
    {
        path: '/advantage-result',
        component: AdvantageResultPage,
        isProtected: false,
    },
    // 404 페이지
    {
        path: '*',
        component: NotFound,
        isProtected: false,
    },
];
