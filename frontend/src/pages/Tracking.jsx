import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../services/api";

function Tracking() {
  const { orderId } = useParams();
  const navigate = useNavigate();

  const [order, setOrder] = useState(null);
  const [tracking, setTracking] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const steps = ["CREATED", "PICKED_UP", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED"];

  useEffect(() => {
    loadOrderDetails();
    loadTracking();
  }, [orderId]);

  const loadOrderDetails = async () => {
    try {
      const response = await api.get(`/api/orders/${orderId}`);
      setOrder(response.data);
    } catch (err) {
      console.error("Failed to load order details", err);
    }
  };

  const loadTracking = async () => {
    try {
      setLoading(true);
      setError("");
      const response = await api.get(`/api/tracking/${orderId}`);
      setTracking(response.data);
    } catch (err) {
      console.error("Failed to load tracking", err);
      setError(err.response?.data?.message || "Failed to load tracking information");
    } finally {
      setLoading(false);
    }
  };

  const currentStatus = order?.status || (tracking.length > 0 ? tracking[tracking.length - 1].status : "CREATED");
  
  const currentStepIndex = steps.indexOf(currentStatus) !== -1 ? steps.indexOf(currentStatus) : (currentStatus === "FAILED" ? 3 : 0);
  const progressPercent = Math.min(100, Math.max(10, (currentStepIndex / (steps.length - 1)) * 100));

  const formatStatus = (st) => {
    if (!st) return "";
    return st.replaceAll("_", " ").toLowerCase().replace(/\b\w/g, char => char.toUpperCase());
  };

  return (
    <div className="dashboard">
      <nav className="navbar">
        <div className="nav-brand">🚚 Last Mile Delivery</div>
        <div className="nav-right">
          <button className="secondary-button" onClick={() => navigate(-1)}>
            ← Back
          </button>
        </div>
      </nav>

      <main className="dashboard-content">
        <div style={{ background: "var(--bg-card)", backdropFilter: "blur(16px)", border: "1px solid var(--border-color)", borderRadius: "var(--radius-lg)", padding: "2.5rem", boxShadow: "var(--shadow-glow)" }}>
          
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: "2rem" }}>
            <div>
              <span className={`status ${currentStatus.toLowerCase()}`} style={{ marginBottom: "0.75rem" }}>
                {formatStatus(currentStatus)}
              </span>
              <h1 style={{ fontSize: "2rem" }}>Tracking Order #{orderId}</h1>
              {order && (
                <p style={{ color: "var(--text-muted)", marginTop: "0.25rem" }}>
                  Pickup: {order.pickupAddress || order.pickupAreaName} ➔ Drop: {order.dropAddress || order.dropAreaName}
                </p>
              )}
            </div>

            {order && (
              <div style={{ textAlign: "right" }}>
                <div style={{ fontSize: "0.85rem", color: "var(--text-muted)" }}>Total Charge</div>
                <div style={{ fontSize: "1.75rem", fontWeight: "800", color: "#00f2fe", fontFamily: "var(--font-heading)" }}>
                  ₹{order.totalCharge}
                </div>
              </div>
            )}
          </div>

          {/* Simulated Visual Route & Live Progress Map */}
          <div className="tracking-map-container">
            <div style={{ display: "flex", justifyContent: "space-between", color: "var(--text-muted)", fontSize: "0.85rem", marginBottom: "1rem" }}>
              <span>📍 Pickup Point</span>
              <span>🚚 In Transit</span>
              <span>🎯 Destination</span>
            </div>

            <div className="map-route-visualizer">
              <div className="map-line">
                <div className="map-progress-line" style={{ width: `${progressPercent}%` }}></div>
              </div>

              <div className="map-node">
                <div className="map-node-icon" style={{ borderColor: currentStepIndex >= 0 ? "#00f2fe" : "var(--border-color)" }}>
                  🏢
                </div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>Warehouse</span>
              </div>

              <div className="map-node">
                <div className="map-node-icon" style={{ borderColor: currentStepIndex >= 2 ? "#00f2fe" : "var(--border-color)" }}>
                  🚚
                </div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>Hub</span>
              </div>

              <div className="map-node">
                <div className="map-node-icon" style={{ borderColor: currentStepIndex >= 4 ? "#10b981" : "var(--border-color)" }}>
                  🏡
                </div>
                <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>Customer</span>
              </div>
            </div>
          </div>

          {/* Timeline History Log */}
          <div style={{ marginTop: "2.5rem" }}>
            <h3 style={{ marginBottom: "1.5rem" }}>Delivery Event History</h3>

            {loading ? (
              <p style={{ color: "var(--text-muted)" }}>Loading tracking timeline...</p>
            ) : tracking.length === 0 ? (
              <div style={{ textAlign: "center", padding: "2rem", color: "var(--text-muted)" }}>
                Order created. Awaiting pickup assignment.
              </div>
            ) : (
              <div style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
                {tracking.map((item, index) => (
                  <div
                    key={item.id || index}
                    style={{
                      display: "flex",
                      gap: "1.25rem",
                      background: "rgba(15, 23, 42, 0.6)",
                      border: "1px solid var(--border-color)",
                      borderRadius: "var(--radius-md)",
                      padding: "1.25rem"
                    }}
                  >
                    <div style={{ width: "36px", height: "36px", borderRadius: "50%", background: "rgba(79, 172, 254, 0.15)", color: "#00f2fe", display: "flex", alignItems: "center", justifyContent: "center", fontWeight: "700" }}>
                      ✓
                    </div>
                    <div style={{ flex: 1 }}>
                      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                        <span className={`status ${item.status.toLowerCase()}`}>
                          {formatStatus(item.status)}
                        </span>
                        <span style={{ fontSize: "0.85rem", color: "var(--text-dim)" }}>
                          {new Date(item.timestamp).toLocaleString()}
                        </span>
                      </div>
                      <p style={{ marginTop: "0.5rem", fontSize: "0.9rem", color: "var(--text-muted)" }}>
                        Updated by: <strong style={{ color: "var(--text-main)" }}>{item.actor?.name || "System"}</strong>
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}

export default Tracking;