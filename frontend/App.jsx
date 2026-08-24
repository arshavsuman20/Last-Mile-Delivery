import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Login from "./pages/Login";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/login" element={<Login />} />

          <Route
            path="/customer"
            element={<h1>Customer Dashboard</h1>}
          />

          <Route
            path="/agent"
            element={<h1>Delivery Agent Dashboard</h1>}
          />

          <Route
            path="/admin"
            element={<h1>Admin Dashboard</h1>}
          />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;