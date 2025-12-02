import { useState, useEffect } from 'react';
import { bookingApi, resourceApi } from '../services/api';
import { Booking, Resource, BookingStatus } from '../types';

/**
 * BookingForm Component
 * Form for creating new bookings
 */
const BookingForm = () => {
  const [resources, setResources] = useState<Resource[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  
  const [formData, setFormData] = useState({
    resourceId: '',
    customerName: '',
    customerEmail: '',
    startTime: '',
    endTime: '',
    notes: '',
  });

  useEffect(() => {
    fetchResources();
  }, []);

  const fetchResources = async () => {
    try {
      const data = await resourceApi.getAll({ available: true });
      setResources(data);
    } catch (err) {
      console.error('Failed to fetch resources:', err);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage(null);

    // Validation
    if (!formData.resourceId || !formData.customerName || !formData.customerEmail || 
        !formData.startTime || !formData.endTime) {
      setMessage({ type: 'error', text: 'すべての必須項目を入力してください' });
      return;
    }

    // Check if end time is after start time
    if (new Date(formData.endTime) <= new Date(formData.startTime)) {
      setMessage({ type: 'error', text: '終了時刻は開始時刻より後に設定してください' });
      return;
    }

    try {
      setLoading(true);

      // Check availability
      const availability = await bookingApi.checkAvailability(
        parseInt(formData.resourceId),
        formData.startTime,
        formData.endTime
      );

      if (!availability.available) {
        setMessage({ type: 'error', text: '選択された時間帯は予約できません' });
        setLoading(false);
        return;
      }

      // Create booking
      const booking: Booking = {
        resourceId: parseInt(formData.resourceId),
        customerName: formData.customerName,
        customerEmail: formData.customerEmail,
        startTime: formData.startTime,
        endTime: formData.endTime,
        status: BookingStatus.CONFIRMED,
        notes: formData.notes || undefined,
      };

      await bookingApi.create(booking);
      setMessage({ type: 'success', text: '予約が正常に作成されました' });
      
      // Reset form
      setFormData({
        resourceId: '',
        customerName: '',
        customerEmail: '',
        startTime: '',
        endTime: '',
        notes: '',
      });
    } catch (err: any) {
      const errorMessage = err.response?.data?.error || '予約の作成に失敗しました';
      setMessage({ type: 'error', text: errorMessage });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="booking-form">
      <h2>新規予約</h2>
      
      {message && (
        <div className={`message ${message.type}`}>
          {message.text}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="resourceId">リソース *</label>
          <select
            id="resourceId"
            name="resourceId"
            value={formData.resourceId}
            onChange={handleChange}
            required
          >
            <option value="">リソースを選択してください</option>
            {resources.map((resource) => (
              <option key={resource.id} value={resource.id}>
                {resource.name} (定員: {resource.capacity}名)
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="customerName">予約者名 *</label>
          <input
            type="text"
            id="customerName"
            name="customerName"
            value={formData.customerName}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="customerEmail">メールアドレス *</label>
          <input
            type="email"
            id="customerEmail"
            name="customerEmail"
            value={formData.customerEmail}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="startTime">開始時刻 *</label>
            <input
              type="datetime-local"
              id="startTime"
              name="startTime"
              value={formData.startTime}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="endTime">終了時刻 *</label>
            <input
              type="datetime-local"
              id="endTime"
              name="endTime"
              value={formData.endTime}
              onChange={handleChange}
              required
            />
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="notes">備考</label>
          <textarea
            id="notes"
            name="notes"
            value={formData.notes}
            onChange={handleChange}
            rows={3}
          />
        </div>

        <button type="submit" className="btn-submit" disabled={loading}>
          {loading ? '処理中...' : '予約を作成'}
        </button>
      </form>
    </div>
  );
};

export default BookingForm;

