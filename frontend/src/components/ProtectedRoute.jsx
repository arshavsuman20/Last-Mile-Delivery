import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function ProtectedRoute({ children, allowedRoles }) {

    const { isAuthenticated, role } = useAuth();

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    if (allowedRoles && !allowedRoles.includes(role)) {
        if (role === "CUSTOMER") {
            return <Navigate to="/customer" replace />;
        }

        if (role === "DELIVERY_AGENT") {
            return <Navigate to="/agent" replace />;
        }

        if (role === "ADMIN") {
            return <Navigate to="/admin" replace />;
        }

        return <Navigate to="/login" replace />;
    }

    return children;
}

export default ProtectedRoute;
