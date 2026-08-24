import { createContext, useContext, useState } from "react";
import api from "../services/api";

const AuthContext = createContext();

export function AuthProvider({ children }) {

    const [token, setToken] = useState(
        localStorage.getItem("token")
    );

    const [role, setRole] = useState(
        localStorage.getItem("role")
    );

    const [userId, setUserId] = useState(
        localStorage.getItem("userId")
    );

    const [userEmail, setUserEmail] = useState(
        localStorage.getItem("userEmail") || ""
    );

    const login = async (email, password) => {

        const response = await api.post("/api/auth/login", {
            email,
            password,
        });

        const data = response.data;

        localStorage.setItem("token", data.token);
        localStorage.setItem("role", data.role);
        localStorage.setItem("userId", data.userId);
        localStorage.setItem("userEmail", email);

        setToken(data.token);
        setRole(data.role);
        setUserId(data.userId);
        setUserEmail(email);

        return data;
    };

    const register = async (name, email, password, phone, role, zoneId) => {
        const response = await api.post("/api/auth/register", {
            name,
            email,
            password,
            phone,
            role,
            zoneId: zoneId ? Number(zoneId) : null
        });

        const data = response.data;

        localStorage.setItem("token", data.token);
        localStorage.setItem("role", data.role);
        localStorage.setItem("userId", data.userId);
        localStorage.setItem("userEmail", email);

        setToken(data.token);
        setRole(data.role);
        setUserId(data.userId);
        setUserEmail(email);

        return data;
    };

    const logout = () => {

        localStorage.removeItem("token");
        localStorage.removeItem("role");
        localStorage.removeItem("userId");
        localStorage.removeItem("userEmail");

        setToken(null);
        setRole(null);
        setUserId(null);
        setUserEmail("");
    };

    return (
        <AuthContext.Provider
            value={{
                token,
                role,
                userId,
                userEmail,
                login,
                register,
                logout,
                isAuthenticated: !!token,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}