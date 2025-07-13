import Home from './pages/home'
import Notification from './pages/notification/index'
import NotFound from './pages/notFound'
import Router from './pages/router'
import Test from './pages/test/index'
import RegisterProfile from './pages/registerProfile/index'
import TodaysDiary from './pages/findCoin/todaysDiary/index'
import Category from './pages/findCoin/todaysDiary/categories/index'

import { Route, Routes } from "react-router-dom";

function App() {
  return (
    <Routes>
      <Route path="/" Component={Router} />
      <Route path="/home" Component={Home} />
      <Route path="/notification" Component={Notification} />
      <Route path="/register-profile" Component={RegisterProfile} />
      <Route path="/todays-diary" Component={TodaysDiary} />
      <Route path="/todays-diary/categories" Component={Category} />
      <Route path="/test" Component={Test}/>
      <Route path="*" Component={NotFound} />
    </Routes>
  )
}

export default App