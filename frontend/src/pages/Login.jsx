import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Login() {
  const navigate = useNavigate();
  const { login, register } = useAuth();

  const [isRegister, setIsRegister] = useState(false);

  // Form states
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [phone, setPhone] = useState("");
  const [role, setRole] = useState("CUSTOMER");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleRedirect = (userRole) => {
    if (userRole === "CUSTOMER") navigate("/customer");
    else if (userRole === "DELIVERY_AGENT") navigate("/agent");
    else if (userRole === "ADMIN") navigate("/admin");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      if (isRegister) {
        const data = await register(name, email, password, phone, role, null);
        handleRedirect(data.role);
      } else {
        const data = await login(email, password);
        handleRedirect(data.role);
      }
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message ||
        err.response?.data ||
        "Authentication failed. Please check your details."
      );
    } finally {
      setLoading(false);
    }
  };

  const fillDemo = (demoRole) => {
    setError("");
    setIsRegister(false);
    if (demoRole === "CUSTOMER") {
      setEmail("customer@lastmile.com");
      setPassword("password123");
    } else if (demoRole === "AGENT") {
      setEmail("agent1@lastmile.com");
      setPassword("password123");
    } else if (demoRole === "ADMIN") {
      setEmail("admin@lastmile.com");
      setPassword("password123");
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div style={{ textAlign: "center", marginBottom: "1.5rem" }}>
          <h1 style={{ fontSize: "1.75rem", marginBottom: "0.25rem", background: "var(--primary-gradient)", WebkitBackgroundClip: "text", WebkitTextFillColor: "transparent" }}>
            🚚 Last Mile Tracker
          </h1>
          <p style={{ color: "var(--text-muted)", fontSize: "0.9rem" }}>
            Smart Logistics & Real-Time Delivery Tracking
          </p>
        </div>

        <div className="auth-tabs">
          <button
            type="button"
            className={`auth-tab ${!isRegister ? "active" : ""}`}
            onClick={() => { setIsRegister(false); setError(""); }}
          >
            Sign In
          </button>
          <button
            type="button"
            className={`auth-tab ${isRegister ? "active" : ""}`}
            onClick={() => { setIsRegister(true); setError(""); }}
          >
            Register
          </button>
        </div>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit}>
          {isRegister && (
            <div className="form-group">
              <label>Full Name</label>
              <input
                type="text"
                placeholder="John Doe"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>
          )}

          <div className="form-group">
            <label>Email Address</label>
            <input
              type="email"
              placeholder="name@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          {isRegister && (
            <>
              <div className="form-group">
                <label>Phone Number</label>
                <input
                  type="tel"
                  placeholder="9876543210"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                />
              </div>

              <div className="form-group">
                <label>Account Role</label>
                <select
                  value={role}
                  onChange={(e) => setRole(e.target.value)}
                >
                  <option value="CUSTOMER">Customer</option>
                  <option value="DELIVERY_AGENT">Delivery Agent</option>
                  <option value="ADMIN">Admin</option>
                </select>
              </div>
            </>
          )}

          <button
            type="submit"
            className="primary-button"
            style={{ width: "100%", marginTop: "1rem" }}
            disabled={loading}
          >
            {loading ? "Processing..." : isRegister ? "Create Account" : "Sign In"}
          </button>
        </form>

        {!isRegister && (
          <div style={{ marginTop: "1.75rem", paddingTop: "1.25rem", borderTop: "1px solid var(--border-color)", textAlign: "center" }}>
            <p style={{ fontSize: "0.8rem", color: "var(--text-muted)", marginBottom: "0.75rem" }}>
              Quick Demo Accounts
            </p>
            <div style={{ display: "flex", gap: "0.5rem", justifyContent: "center" }}>
              <button
                type="button"
                className="secondary-button"
                style={{ fontSize: "0.75rem", padding: "0.4rem 0.75rem" }}
                onClick={() => fillDemo("CUSTOMER")}
              >
                Customer
              </button>
              <button
                type="button"
                className="secondary-button"
                style={{ fontSize: "0.75rem", padding: "0.4rem 0.75rem" }}
                onClick={() => fillDemo("AGENT")}
              >
                Agent
              </button>
              <button
                type="button"
                className="secondary-button"
                style={{ fontSize: "0.75rem", padding: "0.4rem 0.75rem" }}
                onClick={() => fillDemo("ADMIN")}
              >
                Admin
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default Login;