import ProtectedRoute from '@/components/ProtectedRoute';
import { routes } from '@/routes/routeConfig';
import { Route, Routes } from "react-router-dom";

function App() {
  return (
    <Routes>
      {routes.map(({ path, component: Component, isProtected }) => (
        <Route
          key={path}
          path={path}
          element={
            isProtected ? (
              <ProtectedRoute>
                <Component />
              </ProtectedRoute>
            ) : (
              <Component />
            )
          }
        />
      ))}
    </Routes>
  );
}

export default App