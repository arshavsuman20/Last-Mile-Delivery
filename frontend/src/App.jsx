import "./App.css";

import {
    BrowserRouter,
    Routes,
    Route,
    Navigate
} from "react-router-dom";

import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";

import Login from "./pages/Login";
import CustomerDashboard from "./pages/CustomerDashboard";
import CreateOrder from "./pages/CreateOrder";
import Tracking from "./pages/Tracking";
import AdminDashboard from "./pages/AdminDashboard";
import AgentDashboard from "./pages/AgentDashboard";

function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <Routes>

                    <Route
                        path="/login"
                        element={<Login />}
                    />

                    <Route
                        path="/customer"
                        element={
                            <ProtectedRoute allowedRoles={["CUSTOMER"]}>
                                <CustomerDashboard />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/customer/create-order"
                        element={
                            <ProtectedRoute allowedRoles={["CUSTOMER"]}>
                                <CreateOrder />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/customer/tracking/:orderId"
                        element={
                            <ProtectedRoute allowedRoles={["CUSTOMER"]}>
                                <Tracking />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/admin"
                        element={
                            <ProtectedRoute allowedRoles={["ADMIN"]}>
                                <AdminDashboard />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/agent"
                        element={
                            <ProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
                                <AgentDashboard />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="*"
                        element={
                            <Navigate
                                to="/login"
                                replace
                            />
                        }
                    />

                </Routes>
            </AuthProvider>
        </BrowserRouter>
    );
}

export default App;
