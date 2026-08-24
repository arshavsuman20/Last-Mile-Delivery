import { useEffect, useState } from "react";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

function AgentDashboard() {
  const { logout, userId, userEmail } = useAuth();

  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [filterStatus, setFilterStatus] = useState("ASSIGNED");

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    try {
      setLoading(true);
      const response = await api.get("/api/orders");
      setOrders(response.data);
    } catch (err) {
      console.error(err);
      setError("Failed to load assigned deliveries");
    } finally {
      setLoading(false);
    }
  };

  const updateStatus = async (orderId, status) => {
    const actorId = userId || localStorage.getItem("userId");
    try {
      await api.put(`/api/tracking/${orderId}/status`, null, {
        params: {
          status: status,
          actorId: actorId
        }
      });
      alert(`Status updated to ${status}`);
      loadOrders();
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "Failed to update delivery status");
    }
  };

  const filteredOrders = orders.filter(order => {
    if (filterStatus === "ASSIGNED") return order.status !== "DELIVERED" && order.status !== "FAILED";
    if (filterStatus === "DELIVERED") return order.status === "DELIVERED";
    if (filterStatus === "FAILED") return order.status === "FAILED";
    return true;
  });

  return (
    <div className="dashboard">
      <nav className="navbar">
        <div className="nav-brand">🚚 Last Mile Delivery</div>
        <div className="nav-right">
          <span className="user-badge" style={{ background: "rgba(16, 185, 129, 0.15)", color: "#34d399", borderColor: "rgba(16, 185, 129, 0.3)" }}>
            Delivery Agent ({userEmail || "Agent"})
          </span>
          <button className="logout-btn" onClick={logout}>Logout</button>
        </div>
      </nav>

      <main className="dashboard-content">
        <div className="welcome">
          <h1>Delivery Fleet Portal</h1>
          <p>Manage assigned packages, navigate delivery routes, and update real-time status.</p>
        </div>

        <div className="stats">
          <div className="stat-card">
            <h3>Total Orders</h3>
            <strong>{orders.length}</strong>
          </div>
          <div className="stat-card">
            <h3>Active Deliveries</h3>
            <strong>{orders.filter(o => o.status !== "DELIVERED" && o.status !== "FAILED").length}</strong>
          </div>
          <div className="stat-card">
            <h3>Completed</h3>
            <strong>{orders.filter(o => o.status === "DELIVERED").length}</strong>
          </div>
        </div>

        <div className="section-header">
          <div style={{ display: "flex", gap: "0.5rem", alignItems: "center" }}>
            <h2>Assigned Parcels</h2>
            <div style={{ display: "flex", gap: "0.35rem", marginLeft: "1rem" }}>
              {["ASSIGNED", "DELIVERED", "FAILED", "ALL"].map(st => (
                <button
                  key={st}
                  type="button"
                  className={`tab-btn ${filterStatus === st ? "active" : ""}`}
                  style={{ padding: "0.4rem 0.85rem", fontSize: "0.8rem" }}
                  onClick={() => setFilterStatus(st)}
                >
                  {st}
                </button>
              ))}
            </div>
          </div>
        </div>

        {loading ? (
          <div style={{ textAlign: "center", padding: "3rem", color: "var(--text-muted)" }}>
            Loading delivery assignments...
          </div>
        ) : error ? (
          <div className="error-message">{error}</div>
        ) : filteredOrders.length === 0 ? (
          <div style={{ textAlign: "center", padding: "4rem", background: "var(--bg-card)", borderRadius: "var(--radius-md)", border: "1px solid var(--border-color)" }}>
            <h3>No delivery orders found</h3>
            <p style={{ color: "var(--text-muted)", marginTop: "0.5rem" }}>
              You currently have no orders matching this filter.
            </p>
          </div>
        ) : (
          <div className="orders-list">
            {filteredOrders.map(order => (
              <div className="order-card" key={order.id} style={{ flexDirection: "column", alignItems: "stretch", gap: "1.25rem" }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                  <div>
                    <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
                      <h3>Order #{order.id}</h3>
                      <span className={`status ${order.status?.toLowerCase()}`}>{order.status}</span>
                      <span style={{ fontSize: "0.75rem", padding: "0.2rem 0.5rem", borderRadius: "4px", background: "rgba(255,255,255,0.06)" }}>
                        {order.orderType || "B2C"} • {order.paymentType || "PREPAID"}
                      </span>
                    </div>

                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1.5rem", marginTop: "1rem" }}>
                      <div style={{ background: "rgba(15, 23, 42, 0.6)", padding: "1rem", borderRadius: "var(--radius-sm)", border: "1px solid var(--border-color)" }}>
                        <div style={{ fontSize: "0.8rem", color: "#00f2fe", fontWeight: "700", marginBottom: "0.25rem" }}>📍 PICKUP LOCATION</div>
                        <div style={{ fontSize: "0.95rem", fontWeight: "600" }}>{order.pickupAreaName || "Pickup Area"}</div>
                        <div style={{ fontSize: "0.85rem", color: "var(--text-muted)" }}>{order.pickupAddress || "Standard Hub Address"}</div>
                      </div>

                      <div style={{ background: "rgba(15, 23, 42, 0.6)", padding: "1rem", borderRadius: "var(--radius-sm)", border: "1px solid var(--border-color)" }}>
                        <div style={{ fontSize: "0.8rem", color: "#10b981", fontWeight: "700", marginBottom: "0.25rem" }}>🎯 DROP ADDRESS</div>
                        <div style={{ fontSize: "0.95rem", fontWeight: "600" }}>{order.dropAreaName || "Delivery Area"}</div>
                        <div style={{ fontSize: "0.85rem", color: "var(--text-muted)" }}>{order.dropAddress || "Customer Street Address"}</div>
                      </div>
                    </div>
                  </div>

                  <div style={{ textAlign: "right" }}>
                    <div className="order-price">₹{order.totalCharge}</div>
                    <div style={{ fontSize: "0.8rem", color: "var(--text-dim)" }}>Weight: {order.actualWeight || "2"} kg</div>
                  </div>
                </div>

                {order.status !== "DELIVERED" && order.status !== "FAILED" && (
                  <div style={{ display: "flex", gap: "0.75rem", alignItems: "center", paddingTop: "0.75rem", borderTop: "1px solid var(--border-color)" }}>
                    <span style={{ fontSize: "0.85rem", color: "var(--text-muted)", fontWeight: "600" }}>
                      Update Status:
                    </span>
                    <select
                      defaultValue=""
                      style={{ padding: "0.55rem 1rem", borderRadius: "var(--radius-sm)", background: "rgba(15, 23, 42, 0.9)", border: "1px solid var(--border-glow)", color: "#00f2fe", fontWeight: "700", fontSize: "0.9rem" }}
                      onChange={(e) => {
                        if (e.target.value) {
                          updateStatus(order.id, e.target.value);
                          e.target.value = "";
                        }
                      }}
                    >
                      <option value="">Choose Next Lifecycle Step...</option>
                      <option value="PICKED_UP">📦 Mark Picked Up</option>
                      <option value="IN_TRANSIT">🚚 Mark In Transit</option>
                      <option value="OUT_FOR_DELIVERY">🛵 Mark Out For Delivery</option>
                      <option value="DELIVERED">✅ Mark Delivered</option>
                      <option value="FAILED">❌ Mark Failed (Customer Absent / Rejected)</option>
                    </select>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}

export default AgentDashboard;