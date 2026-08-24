import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

function CustomerDashboard() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterStatus, setFilterStatus] = useState("ALL");
  const [selectedOrder, setSelectedOrder] = useState(null);
  
  // Reschedule Modal State
  const [rescheduleOrder, setRescheduleOrder] = useState(null);
  const [newDeliveryDate, setNewDeliveryDate] = useState("");
  const [rescheduleReason, setRescheduleReason] = useState("");
  const [rescheduleLoading, setRescheduleLoading] = useState(false);
  const [modalError, setModalError] = useState("");

  const navigate = useNavigate();
  const { userId, userEmail, logout } = useAuth();

  useEffect(() => {
    if (userId) {
      loadOrders();
    }
  }, [userId]);

  const loadOrders = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/api/orders/customer/${userId}`);
      setOrders(response.data);
    } catch (error) {
      console.error("Failed to load customer orders", error);
    } finally {
      setLoading(false);
    }
  };

  const handleRescheduleSubmit = async (e) => {
    e.preventDefault();
    setModalError("");
    setRescheduleLoading(true);

    try {
      await api.post(`/api/reschedules/${rescheduleOrder.id}`, null, {
        params: {
          newDeliveryDate: newDeliveryDate,
          reason: rescheduleReason,
          actorId: userId
        }
      });
      alert("Order rescheduled successfully!");
      setRescheduleOrder(null);
      setNewDeliveryDate("");
      setRescheduleReason("");
      loadOrders();
    } catch (err) {
      console.error("Reschedule failed", err);
      setModalError(
        err.response?.data?.message || err.response?.data || "Failed to reschedule delivery"
      );
    } finally {
      setRescheduleLoading(false);
    }
  };

  const filteredOrders = orders.filter(order => {
    if (filterStatus === "ALL") return true;
    if (filterStatus === "ACTIVE") return order.status !== "DELIVERED" && order.status !== "FAILED";
    return order.status === filterStatus;
  });

  return (
    <div className="dashboard">
      <nav className="navbar">
        <div className="nav-brand">🚚 Last Mile Delivery</div>
        <div className="nav-right">
          <span className="user-badge">{userEmail || "Customer"}</span>
          <button className="logout-btn" onClick={logout}>Logout</button>
        </div>
      </nav>

      <main className="dashboard-content">
        <div className="welcome">
          <h1>Customer Portal</h1>
          <p>Track your parcels, view rate charges, and reschedule deliveries.</p>
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
            <h3>Delivered</h3>
            <strong>{orders.filter(o => o.status === "DELIVERED").length}</strong>
          </div>
          <div className="stat-card">
            <h3>Failed / Action Req</h3>
            <strong>{orders.filter(o => o.status === "FAILED").length}</strong>
          </div>
        </div>

        <div className="section-header">
          <div style={{ display: "flex", gap: "0.5rem", alignItems: "center" }}>
            <h2>My Deliveries</h2>
            <div style={{ display: "flex", gap: "0.35rem", marginLeft: "1rem" }}>
              {["ALL", "ACTIVE", "DELIVERED", "FAILED"].map(st => (
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

          <button className="primary-button" onClick={() => navigate("/customer/create-order")}>
            + Create New Order
          </button>
        </div>

        {loading ? (
          <div style={{ textAlign: "center", padding: "3rem", color: "var(--text-muted)" }}>
            Loading your orders...
          </div>
        ) : filteredOrders.length === 0 ? (
          <div style={{ textAlign: "center", padding: "4rem", background: "var(--bg-card)", borderRadius: "var(--radius-md)", border: "1px solid var(--border-color)" }}>
            <h3>No orders found</h3>
            <p style={{ color: "var(--text-muted)", marginTop: "0.5rem" }}>
              {filterStatus === "ALL" ? "Create your first delivery order to get started." : "No orders matching this filter status."}
            </p>
          </div>
        ) : (
          <div className="orders-list">
            {filteredOrders.map(order => (
              <div className="order-card" key={order.id}>
                <div className="order-card-details">
                  <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
                    <h3>Order #{order.id}</h3>
                    <span className={`status ${order.status.toLowerCase()}`}>
                      {order.status}
                    </span>
                    <span style={{ fontSize: "0.75rem", padding: "0.2rem 0.5rem", borderRadius: "4px", background: "rgba(255,255,255,0.06)" }}>
                      {order.orderType || "B2C"} • {order.paymentType || "PREPAID"}
                    </span>
                  </div>

                  <div className="order-meta">
                    <span>📍 Pickup: <strong>{order.pickupAreaName || "Area"}</strong></span>
                    <span>🎯 Drop: <strong>{order.dropAreaName || "Area"}</strong></span>
                    <span>📦 Weight: {order.actualWeight || "-"} kg</span>
                    <span>📅 Created: {new Date(order.createdAt).toLocaleString()}</span>
                  </div>
                </div>

                <div className="order-info">
                  <div className="order-price">₹{order.totalCharge}</div>
                  
                  {order.status === "FAILED" && (
                    <button
                      className="primary-button"
                      style={{ background: "linear-gradient(135deg, #f59e0b 0%, #d97706 100%)", boxShadow: "0 4px 14px rgba(245,158,11,0.3)" }}
                      onClick={() => setRescheduleOrder(order)}
                    >
                      Reschedule
                    </button>
                  )}

                  <button
                    className="secondary-button"
                    onClick={() => navigate(`/customer/tracking/${order.id}`)}
                  >
                    Track Progress →
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Reschedule Modal */}
        {rescheduleOrder && (
          <div className="modal-overlay">
            <div className="modal-card">
              <h3>Reschedule Delivery for Order #{rescheduleOrder.id}</h3>
              <p style={{ color: "var(--text-muted)", fontSize: "0.88rem", marginBottom: "1.25rem" }}>
                Select a new delivery date and reason for rescheduling.
              </p>

              {modalError && <div className="error-message">{modalError}</div>}

              <form onSubmit={handleRescheduleSubmit}>
                <div className="form-group">
                  <label>New Delivery Date</label>
                  <input
                    type="date"
                    value={newDeliveryDate}
                    min={new Date().toISOString().split("T")[0]}
                    onChange={(e) => setNewDeliveryDate(e.target.value)}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Reason for Reschedule</label>
                  <select
                    value={rescheduleReason}
                    onChange={(e) => setRescheduleReason(e.target.value)}
                    required
                  >
                    <option value="">Select Reason...</option>
                    <option value="Customer unavailable at address">Customer unavailable at address</option>
                    <option value="Incorrect address details">Incorrect address details</option>
                    <option value="Requested preferred delivery slot">Requested preferred delivery slot</option>
                    <option value="Weather / Traffic Delay">Weather / Traffic Delay</option>
                  </select>
                </div>

                <div style={{ display: "flex", gap: "0.75rem", justifyContent: "flex-end", marginTop: "1.5rem" }}>
                  <button
                    type="button"
                    className="secondary-button"
                    onClick={() => setRescheduleOrder(null)}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="primary-button"
                    disabled={rescheduleLoading}
                  >
                    {rescheduleLoading ? "Saving..." : "Confirm Reschedule"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default CustomerDashboard;