import { useEffect, useState } from "react";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

function AdminDashboard() {
  const { logout, userId } = useAuth();

  const [activeTab, setActiveTab] = useState("ORDERS"); // ORDERS, RATES, ZONES, AGENTS

  // Data states
  const [orders, setOrders] = useState([]);
  const [agents, setAgents] = useState([]);
  const [rateCards, setRateCards] = useState([]);
  const [zones, setZones] = useState([]);
  const [areas, setAreas] = useState([]);

  // Filter states
  const [statusFilter, setStatusFilter] = useState("");
  const [zoneFilter, setZoneFilter] = useState("");
  const [agentFilter, setAgentFilter] = useState("");

  // Rate Editing state
  const [editingRate, setEditingRate] = useState(null);
  const [rateForm, setRateForm] = useState({ intraZoneRatePerKg: "", interZoneRatePerKg: "", codSurcharge: "" });

  // Zone/Area creation states
  const [newZoneName, setNewZoneName] = useState("");
  const [newZoneDesc, setNewZoneDesc] = useState("");
  const [newAreaName, setNewAreaName] = useState("");
  const [newAreaZoneId, setNewAreaZoneId] = useState("");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const statuses = ["CREATED", "PICKED_UP", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED", "FAILED"];

  useEffect(() => {
    loadAllData();
  }, []);

  const loadAllData = async () => {
    try {
      setLoading(true);
      await Promise.all([loadOrders(), loadAgents(), loadRateCards(), loadZonesAndAreas()]);
    } catch (err) {
      console.error("Error loading admin data", err);
    } finally {
      setLoading(false);
    }
  };

  const loadOrders = async () => {
    try {
      const response = await api.get("/api/orders");
      setOrders(response.data);
    } catch (err) {
      console.error("Failed to load orders", err);
    }
  };

  const loadAgents = async () => {
    try {
      const response = await api.get("/api/assignments/available-agents");
      setAgents(response.data);
    } catch (err) {
      console.error("Failed to load agents", err);
    }
  };

  const loadRateCards = async () => {
    try {
      const response = await api.get("/api/rates/cards");
      setRateCards(response.data);
    } catch (err) {
      console.error("Failed to load rate cards", err);
    }
  };

  const loadZonesAndAreas = async () => {
    try {
      const [zRes, aRes] = await Promise.all([api.get("/api/zones"), api.get("/api/areas")]);
      setZones(zRes.data);
      setAreas(aRes.data);
      if (zRes.data.length > 0) setNewAreaZoneId(String(zRes.data[0].id));
    } catch (err) {
      console.error("Failed to load zones and areas", err);
    }
  };

  const filterOrders = async () => {
    try {
      setLoading(true);
      setError("");
      const params = {};
      if (statusFilter) params.status = statusFilter;
      if (zoneFilter) params.zoneId = zoneFilter;
      if (agentFilter) params.agentId = agentFilter;

      const response = await api.get("/api/orders/filter", { params });
      setOrders(response.data);
    } catch (err) {
      setError("Failed to filter orders");
    } finally {
      setLoading(false);
    }
  };

  const clearFilters = () => {
    setStatusFilter("");
    setZoneFilter("");
    setAgentFilter("");
    loadOrders();
  };

  const assignAgent = async (orderId, agentId) => {
    try {
      await api.post(`/api/assignments/${orderId}/agent/${agentId}`);
      alert("Delivery agent assigned successfully");
      await loadOrders();
      await loadAgents();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to assign agent");
    }
  };

  const autoAssign = async (orderId) => {
    try {
      await api.post(`/api/assignments/${orderId}/auto`);
      alert("Agent automatically assigned!");
      await loadOrders();
      await loadAgents();
    } catch (err) {
      alert(err.response?.data?.message || "No suitable agent available in pickup zone");
    }
  };

  const updateStatus = async (orderId, status) => {
    const actorId = userId || localStorage.getItem("userId");
    try {
      await api.put(`/api/tracking/${orderId}/admin-status`, null, {
        params: { status, actorId }
      });
      alert("Status override successful!");
      await loadOrders();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to update status");
    }
  };

  const handleUpdateRateCard = async (e) => {
    e.preventDefault();
    try {
      await api.put(`/api/rates/cards/${editingRate.orderType}`, {
        intraZoneRatePerKg: Number(rateForm.intraZoneRatePerKg),
        interZoneRatePerKg: Number(rateForm.interZoneRatePerKg),
        codSurcharge: Number(rateForm.codSurcharge)
      });
      alert("Rate Card updated successfully!");
      setEditingRate(null);
      loadRateCards();
    } catch (err) {
      alert("Failed to update rate card");
    }
  };

  const handleCreateZone = async (e) => {
    e.preventDefault();
    try {
      await api.post("/api/zones", { name: newZoneName, description: newZoneDesc });
      alert("Zone created!");
      setNewZoneName("");
      setNewZoneDesc("");
      loadZonesAndAreas();
    } catch (err) {
      alert("Failed to create zone");
    }
  };

  const handleCreateArea = async (e) => {
    e.preventDefault();
    try {
      await api.post("/api/areas", { name: newAreaName, zoneId: Number(newAreaZoneId) });
      alert("Area created!");
      setNewAreaName("");
      loadZonesAndAreas();
    } catch (err) {
      alert("Failed to create area");
    }
  };

  return (
    <div className="dashboard">
      <nav className="navbar">
        <div className="nav-brand">🚚 Last Mile Delivery</div>
        <div className="nav-right">
          <span className="user-badge" style={{ background: "rgba(139, 92, 246, 0.15)", color: "#a78bfa", borderColor: "rgba(139, 92, 246, 0.3)" }}>
            Administrator Portal
          </span>
          <button className="logout-btn" onClick={logout}>Logout</button>
        </div>
      </nav>

      <main className="dashboard-content">
        <div className="welcome">
          <h1>Admin Control Center</h1>
          <p>Manage order assignments, pricing rate cards, delivery zones, and fleet availability.</p>
        </div>

        <div className="stats">
          <div className="stat-card">
            <h3>Total Orders</h3>
            <strong>{orders.length}</strong>
          </div>
          <div className="stat-card">
            <h3>Available Agents</h3>
            <strong>{agents.length}</strong>
          </div>
          <div className="stat-card">
            <h3>Active Deliveries</h3>
            <strong>{orders.filter(o => o.status !== "DELIVERED" && o.status !== "FAILED").length}</strong>
          </div>
          <div className="stat-card">
            <h3>Delivery Zones</h3>
            <strong>{zones.length}</strong>
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="admin-tabs">
          <button className={`tab-btn ${activeTab === "ORDERS" ? "active" : ""}`} onClick={() => setActiveTab("ORDERS")}>
            📦 Orders & Assignments
          </button>
          <button className={`tab-btn ${activeTab === "RATES" ? "active" : ""}`} onClick={() => setActiveTab("RATES")}>
            💳 Rate Cards Manager
          </button>
          <button className={`tab-btn ${activeTab === "ZONES" ? "active" : ""}`} onClick={() => setActiveTab("ZONES")}>
            🗺️ Zones & Areas
          </button>
          <button className={`tab-btn ${activeTab === "AGENTS" ? "active" : ""}`} onClick={() => setActiveTab("AGENTS")}>
            👤 Fleet / Delivery Agents
          </button>
        </div>

        {/* TAB 1: ORDERS */}
        {activeTab === "ORDERS" && (
          <div>
            <div className="filters-bar">
              <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
                <option value="">All Statuses</option>
                {statuses.map(st => <option key={st} value={st}>{st}</option>)}
              </select>

              <select value={zoneFilter} onChange={(e) => setZoneFilter(e.target.value)}>
                <option value="">All Zones</option>
                {zones.map(z => <option key={z.id} value={z.id}>{z.name}</option>)}
              </select>

              <button className="primary-button" style={{ padding: "0.6rem 1rem", fontSize: "0.85rem" }} onClick={filterOrders}>
                Filter
              </button>
              <button className="secondary-button" style={{ padding: "0.6rem 1rem", fontSize: "0.85rem" }} onClick={clearFilters}>
                Clear
              </button>
            </div>

            {error && <div className="error-message">{error}</div>}

            {loading ? (
              <p style={{ color: "var(--text-muted)", padding: "2rem" }}>Loading orders...</p>
            ) : orders.length === 0 ? (
              <div style={{ textAlign: "center", padding: "3rem", background: "var(--bg-card)", borderRadius: "var(--radius-md)" }}>
                No orders match criteria.
              </div>
            ) : (
              <div className="orders-list">
                {orders.map(order => (
                  <div className="order-card" key={order.id} style={{ flexDirection: "column", alignItems: "stretch", gap: "1.25rem" }}>
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                      <div>
                        <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
                          <h3>Order #{order.id}</h3>
                          <span className={`status ${order.status?.toLowerCase()}`}>{order.status}</span>
                          <span style={{ fontSize: "0.75rem", padding: "0.2rem 0.5rem", borderRadius: "4px", background: "rgba(255,255,255,0.06)" }}>
                            {order.orderType} • {order.paymentType}
                          </span>
                        </div>
                        <div className="order-meta" style={{ marginTop: "0.4rem" }}>
                          <span>Customer: {order.customerName || "Customer #" + order.customerId}</span>
                          <span>Pickup: {order.pickupAreaName || "Area #" + order.pickupAreaId}</span>
                          <span>Drop: {order.dropAreaName || "Area #" + order.dropAreaId}</span>
                        </div>
                      </div>

                      <div style={{ textAlign: "right" }}>
                        <div className="order-price">₹{order.totalCharge}</div>
                        <span style={{ fontSize: "0.8rem", color: "var(--text-dim)" }}>
                          {order.assignedAgentName ? `Agent: ${order.assignedAgentName}` : "Unassigned"}
                        </span>
                      </div>
                    </div>

                    <div style={{ display: "flex", gap: "1rem", flexWrap: "wrap", paddingTop: "0.75rem", borderTop: "1px solid var(--border-color)", alignItems: "center" }}>
                      {/* Manual Assignment */}
                      <select
                        defaultValue=""
                        style={{ padding: "0.5rem 0.75rem", borderRadius: "var(--radius-sm)", background: "rgba(15, 23, 42, 0.7)", border: "1px solid var(--border-color)", color: "var(--text-main)", fontSize: "0.85rem" }}
                        onChange={(e) => {
                          if (e.target.value) {
                            assignAgent(order.id, e.target.value);
                            e.target.value = "";
                          }
                        }}
                      >
                        <option value="">Assign Agent...</option>
                        {agents.map(ag => (
                          <option key={ag.id} value={ag.id}>
                            {ag.user?.name || `Agent #${ag.id}`} ({ag.zone?.name || "Zone"})
                          </option>
                        ))}
                      </select>

                      <button className="secondary-button" style={{ padding: "0.5rem 0.85rem", fontSize: "0.85rem" }} onClick={() => autoAssign(order.id)}>
                        ⚡ Auto Assign
                      </button>

                      {/* Status Override */}
                      <select
                        value=""
                        style={{ padding: "0.5rem 0.75rem", borderRadius: "var(--radius-sm)", background: "rgba(15, 23, 42, 0.7)", border: "1px solid var(--border-color)", color: "var(--text-main)", fontSize: "0.85rem", marginLeft: "auto" }}
                        onChange={(e) => {
                          if (e.target.value) updateStatus(order.id, e.target.value);
                        }}
                      >
                        <option value="">Override Status...</option>
                        {statuses.map(st => <option key={st} value={st}>{st}</option>)}
                      </select>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* TAB 2: RATE CARDS */}
        {activeTab === "RATES" && (
          <div>
            <h3 style={{ marginBottom: "1.25rem" }}>Configured Delivery Rate Cards</h3>
            <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(320px, 1fr))", gap: "1.5rem" }}>
              {rateCards.map(rc => (
                <div key={rc.id} style={{ background: "var(--bg-card)", border: "1px solid var(--border-color)", borderRadius: "var(--radius-md)", padding: "1.5rem" }}>
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1rem" }}>
                    <h3 style={{ color: "#00f2fe" }}>Rate Card: {rc.orderType}</h3>
                    <button
                      className="secondary-button"
                      style={{ padding: "0.35rem 0.75rem", fontSize: "0.8rem" }}
                      onClick={() => {
                        setEditingRate(rc);
                        setRateForm({
                          intraZoneRatePerKg: rc.intraZoneRatePerKg,
                          interZoneRatePerKg: rc.interZoneRatePerKg,
                          codSurcharge: rc.codSurcharge
                        });
                      }}
                    >
                      Edit Rates
                    </button>
                  </div>

                  <div className="rate-preview-row">
                    <span>Intra-Zone Rate / kg:</span>
                    <strong>₹{rc.intraZoneRatePerKg}</strong>
                  </div>
                  <div className="rate-preview-row">
                    <span>Inter-Zone Rate / kg:</span>
                    <strong>₹{rc.interZoneRatePerKg}</strong>
                  </div>
                  <div className="rate-preview-row">
                    <span>COD Surcharge:</span>
                    <strong>₹{rc.codSurcharge}</strong>
                  </div>
                </div>
              ))}
            </div>

            {/* Rate Edit Modal */}
            {editingRate && (
              <div className="modal-overlay">
                <div className="modal-card">
                  <h3>Edit Rate Card for {editingRate.orderType}</h3>
                  <form onSubmit={handleUpdateRateCard} style={{ marginTop: "1rem" }}>
                    <div className="form-group">
                      <label>Intra-Zone Rate (₹ / kg)</label>
                      <input
                        type="number"
                        step="0.5"
                        value={rateForm.intraZoneRatePerKg}
                        onChange={(e) => setRateForm({ ...rateForm, intraZoneRatePerKg: e.target.value })}
                        required
                      />
                    </div>
                    <div className="form-group">
                      <label>Inter-Zone Rate (₹ / kg)</label>
                      <input
                        type="number"
                        step="0.5"
                        value={rateForm.interZoneRatePerKg}
                        onChange={(e) => setRateForm({ ...rateForm, interZoneRatePerKg: e.target.value })}
                        required
                      />
                    </div>
                    <div className="form-group">
                      <label>COD Surcharge (₹)</label>
                      <input
                        type="number"
                        step="0.5"
                        value={rateForm.codSurcharge}
                        onChange={(e) => setRateForm({ ...rateForm, codSurcharge: e.target.value })}
                        required
                      />
                    </div>
                    <div style={{ display: "flex", gap: "0.75rem", justifyContent: "flex-end", marginTop: "1.5rem" }}>
                      <button type="button" className="secondary-button" onClick={() => setEditingRate(null)}>Cancel</button>
                      <button type="submit" className="primary-button">Save Changes</button>
                    </div>
                  </form>
                </div>
              </div>
            )}
          </div>
        )}

        {/* TAB 3: ZONES & AREAS */}
        {activeTab === "ZONES" && (
          <div>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "2rem" }}>
              {/* Left Column: Create Forms */}
              <div style={{ display: "flex", flexDirection: "column", gap: "1.5rem" }}>
                <div style={{ background: "var(--bg-card)", border: "1px solid var(--border-color)", borderRadius: "var(--radius-md)", padding: "1.5rem" }}>
                  <h3>Add New Zone</h3>
                  <form onSubmit={handleCreateZone} style={{ marginTop: "1rem" }}>
                    <div className="form-group">
                      <label>Zone Name</label>
                      <input type="text" placeholder="e.g. East Zone" value={newZoneName} onChange={(e) => setNewZoneName(e.target.value)} required />
                    </div>
                    <div className="form-group">
                      <label>Description</label>
                      <input type="text" placeholder="e.g. Eastern Suburbs and Port" value={newZoneDesc} onChange={(e) => setNewZoneDesc(e.target.value)} />
                    </div>
                    <button type="submit" className="primary-button" style={{ width: "100%" }}>Create Zone</button>
                  </form>
                </div>

                <div style={{ background: "var(--bg-card)", border: "1px solid var(--border-color)", borderRadius: "var(--radius-md)", padding: "1.5rem" }}>
                  <h3>Add New Area</h3>
                  <form onSubmit={handleCreateArea} style={{ marginTop: "1rem" }}>
                    <div className="form-group">
                      <label>Area Name</label>
                      <input type="text" placeholder="e.g. Airport Hub" value={newAreaName} onChange={(e) => setNewAreaName(e.target.value)} required />
                    </div>
                    <div className="form-group">
                      <label>Assigned Zone</label>
                      <select value={newAreaZoneId} onChange={(e) => setNewAreaZoneId(e.target.value)} required>
                        {zones.map(z => <option key={z.id} value={z.id}>{z.name}</option>)}
                      </select>
                    </div>
                    <button type="submit" className="primary-button" style={{ width: "100%" }}>Create Area</button>
                  </form>
                </div>
              </div>

              {/* Right Column: Existing Zones & Areas */}
              <div style={{ background: "var(--bg-card)", border: "1px solid var(--border-color)", borderRadius: "var(--radius-md)", padding: "1.5rem" }}>
                <h3>Existing Zones & Linked Areas</h3>
                <div style={{ marginTop: "1rem", display: "flex", flexDirection: "column", gap: "1.25rem" }}>
                  {zones.map(z => (
                    <div key={z.id} style={{ background: "rgba(15, 23, 42, 0.6)", padding: "1rem", borderRadius: "var(--radius-sm)", border: "1px solid var(--border-color)" }}>
                      <div style={{ fontWeight: "700", color: "#00f2fe" }}>{z.name} (ID #{z.id})</div>
                      <div style={{ fontSize: "0.8rem", color: "var(--text-dim)", marginBottom: "0.5rem" }}>{z.description || "No description"}</div>
                      <div style={{ display: "flex", flexWrap: "wrap", gap: "0.4rem" }}>
                        {areas.filter(a => a.zone?.id === z.id).map(a => (
                          <span key={a.id} style={{ fontSize: "0.75rem", padding: "0.25rem 0.5rem", borderRadius: "4px", background: "rgba(255,255,255,0.08)" }}>
                            📍 {a.name}
                          </span>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}

        {/* TAB 4: DELIVERY AGENTS */}
        {activeTab === "AGENTS" && (
          <div>
            <h3 style={{ marginBottom: "1.25rem" }}>Active Delivery Fleet</h3>
            <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(280px, 1fr))", gap: "1.25rem" }}>
              {agents.map(ag => (
                <div key={ag.id} style={{ background: "var(--bg-card)", border: "1px solid var(--border-color)", borderRadius: "var(--radius-md)", padding: "1.5rem" }}>
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "0.75rem" }}>
                    <h3>{ag.user?.name || `Agent #${ag.id}`}</h3>
                    <span className={`status ${ag.available ? "delivered" : "failed"}`}>
                      {ag.available ? "AVAILABLE" : "BUSY"}
                    </span>
                  </div>
                  <p style={{ fontSize: "0.85rem", color: "var(--text-muted)" }}>Email: {ag.user?.email}</p>
                  <p style={{ fontSize: "0.85rem", color: "var(--text-muted)" }}>Phone: {ag.user?.phone || "N/A"}</p>
                  <p style={{ fontSize: "0.85rem", color: "#00f2fe", marginTop: "0.5rem" }}>Zone: {ag.zone?.name || "Unassigned"}</p>
                </div>
              ))}
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default AdminDashboard;