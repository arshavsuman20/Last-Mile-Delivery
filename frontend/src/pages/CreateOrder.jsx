import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import api from "../services/api";

function CreateOrder() {
  const navigate = useNavigate();
  const { userId, logout } = useAuth();

  const [areas, setAreas] = useState([]);
  const [formData, setFormData] = useState({
    pickupAreaId: "",
    dropAreaId: "",
    pickupAddress: "",
    dropAddress: "",
    length: "20",
    breadth: "15",
    height: "10",
    actualWeight: "2",
    orderType: "B2C",
    paymentType: "PREPAID"
  });

  const [rateEstimate, setRateEstimate] = useState(null);
  const [calculatingRate, setCalculatingRate] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    loadAreas();
  }, []);

  const loadAreas = async () => {
    try {
      const response = await api.get("/api/areas");
      setAreas(response.data);
      if (response.data.length >= 2) {
        setFormData(prev => ({
          ...prev,
          pickupAreaId: String(response.data[0].id),
          dropAreaId: String(response.data[1].id)
        }));
      } else if (response.data.length === 1) {
        setFormData(prev => ({
          ...prev,
          pickupAreaId: String(response.data[0].id),
          dropAreaId: String(response.data[0].id)
        }));
      }
    } catch (err) {
      console.error("Failed to load areas", err);
    }
  };

  useEffect(() => {
    if (
      formData.pickupAreaId &&
      formData.dropAreaId &&
      formData.length > 0 &&
      formData.breadth > 0 &&
      formData.height > 0 &&
      formData.actualWeight > 0
    ) {
      calculateRate();
    }
  }, [
    formData.pickupAreaId,
    formData.dropAreaId,
    formData.length,
    formData.breadth,
    formData.height,
    formData.actualWeight,
    formData.orderType,
    formData.paymentType
  ]);

  const calculateRate = async () => {
    setCalculatingRate(true);
    try {
      const response = await api.post("/api/rates/calculate", {
        pickupAreaId: Number(formData.pickupAreaId),
        dropAreaId: Number(formData.dropAreaId),
        length: Number(formData.length),
        breadth: Number(formData.breadth),
        height: Number(formData.height),
        actualWeight: Number(formData.actualWeight),
        orderType: formData.orderType,
        paymentType: formData.paymentType
      });
      setRateEstimate(response.data);
    } catch (err) {
      console.error("Rate calculation failed", err);
    } finally {
      setCalculatingRate(false);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      await api.post("/api/orders", {
        customerId: Number(userId),
        pickupAreaId: Number(formData.pickupAreaId),
        dropAreaId: Number(formData.dropAreaId),
        pickupAddress: formData.pickupAddress,
        dropAddress: formData.dropAddress,
        length: Number(formData.length),
        breadth: Number(formData.breadth),
        height: Number(formData.height),
        actualWeight: Number(formData.actualWeight),
        orderType: formData.orderType,
        paymentType: formData.paymentType
      });

      navigate("/customer");
    } catch (err) {
      console.error("Failed to create order", err);
      setError(
        err.response?.data?.message ||
        err.response?.data ||
        "Failed to create delivery order"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dashboard">
      <nav className="navbar">
        <div className="nav-brand">🚚 Last Mile Delivery</div>
        <div className="nav-right">
          <span className="user-badge">Customer Portal</span>
          <button className="logout-btn" onClick={logout}>Logout</button>
        </div>
      </nav>

      <main className="dashboard-content">
        <div className="form-card">
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.5rem" }}>
            <div>
              <h2>Create Delivery Order</h2>
              <p style={{ color: "var(--text-muted)", fontSize: "0.9rem" }}>Calculate rate and dispatch your parcel</p>
            </div>
            <button className="secondary-button" onClick={() => navigate("/customer")}>
              ← Cancel
            </button>
          </div>

          {error && <div className="error-message">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="form-grid">
              <div className="form-group">
                <label>Pickup Area</label>
                <select name="pickupAreaId" value={formData.pickupAreaId} onChange={handleChange} required>
                  {areas.map(area => (
                    <option key={area.id} value={area.id}>
                      {area.name} ({area.zone?.name || "Zone"})
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Delivery (Drop) Area</label>
                <select name="dropAreaId" value={formData.dropAreaId} onChange={handleChange} required>
                  {areas.map(area => (
                    <option key={area.id} value={area.id}>
                      {area.name} ({area.zone?.name || "Zone"})
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-group">
              <label>Pickup Street Address</label>
              <input
                type="text"
                name="pickupAddress"
                value={formData.pickupAddress}
                onChange={handleChange}
                placeholder="e.g. 102 Tech Park Road, Gate 3"
                required
              />
            </div>

            <div className="form-group">
              <label>Delivery Street Address</label>
              <input
                type="text"
                name="dropAddress"
                value={formData.dropAddress}
                onChange={handleChange}
                placeholder="e.g. Apartment 4B, Metro Towers"
                required
              />
            </div>

            <div className="form-grid" style={{ gridTemplateColumns: "1fr 1fr 1fr 1fr" }}>
              <div className="form-group">
                <label>Length (cm)</label>
                <input type="number" name="length" value={formData.length} onChange={handleChange} min="0.1" step="0.1" required />
              </div>
              <div className="form-group">
                <label>Breadth (cm)</label>
                <input type="number" name="breadth" value={formData.breadth} onChange={handleChange} min="0.1" step="0.1" required />
              </div>
              <div className="form-group">
                <label>Height (cm)</label>
                <input type="number" name="height" value={formData.height} onChange={handleChange} min="0.1" step="0.1" required />
              </div>
              <div className="form-group">
                <label>Weight (kg)</label>
                <input type="number" name="actualWeight" value={formData.actualWeight} onChange={handleChange} min="0.1" step="0.1" required />
              </div>
            </div>

            <div className="form-grid">
              <div className="form-group">
                <label>Order Type</label>
                <select name="orderType" value={formData.orderType} onChange={handleChange}>
                  <option value="B2C">B2C (Retail Delivery)</option>
                  <option value="B2B">B2B (Commercial Bulk)</option>
                </select>
              </div>

              <div className="form-group">
                <label>Payment Method</label>
                <select name="paymentType" value={formData.paymentType} onChange={handleChange}>
                  <option value="PREPAID">Prepaid (Online)</option>
                  <option value="COD">Cash On Delivery (COD)</option>
                </select>
              </div>
            </div>

            {/* Live Rate Calculator Widget */}
            {rateEstimate && (
              <div className="rate-preview-card">
                <div className="rate-preview-header">
                  <h4 style={{ color: "#00f2fe" }}>⚡ Live Rate Estimate</h4>
                  {calculatingRate && <span style={{ fontSize: "0.8rem", color: "var(--text-dim)" }}>Calculating...</span>}
                </div>
                <div className="rate-preview-row">
                  <span>Volumetric Weight:</span>
                  <strong>{rateEstimate.volumetricWeight?.toFixed(2)} kg</strong>
                </div>
                <div className="rate-preview-row">
                  <span>Billable Weight:</span>
                  <strong>{rateEstimate.billableWeight?.toFixed(2)} kg</strong>
                </div>
                <div className="rate-preview-row">
                  <span>Base Charge:</span>
                  <span>₹{rateEstimate.baseCharge}</span>
                </div>
                {rateEstimate.codSurcharge > 0 && (
                  <div className="rate-preview-row">
                    <span>COD Surcharge:</span>
                    <span>₹{rateEstimate.codSurcharge}</span>
                  </div>
                )}
                <div className="rate-preview-total">
                  <span>Estimated Total:</span>
                  <span style={{ color: "#00f2fe" }}>₹{rateEstimate.totalCharge}</span>
                </div>
              </div>
            )}

            <button
              type="submit"
              className="primary-button"
              style={{ width: "100%", marginTop: "1rem" }}
              disabled={loading}
            >
              {loading ? "Creating Order..." : "Confirm & Create Order"}
            </button>
          </form>
        </div>
      </main>
    </div>
  );
}

export default CreateOrder;