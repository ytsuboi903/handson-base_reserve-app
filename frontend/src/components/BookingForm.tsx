import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { bookingApi, resourceApi } from '../services/api';
import { Booking, Resource, BookingStatus } from '../types';

/**
 * BookingForm Component
 * Form for creating new bookings
 */
const BookingForm = () => {
  const { id } = useParams();
  const isEditMode = Boolean(id);
  const [resources, setResources] = useState<Resource[]>([]);
  const [loading, setLoading] = useState(false);
  const [loadingBooking, setLoadingBooking] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  
  const [formData, setFormData] = useState({
    resourceId: '',
    customerName: '',
    customerEmail: '',
    startTime: '',
    endTime: '',
    status: BookingStatus.CONFIRMED,
    notes: '',
  });

  useEffect(() => {
    fetchResources();
    if (isEditMode) {
      fetchBooking();
    }
  }, [isEditMode]);

  const fetchResources = async () => {
    try {
      const data = await resourceApi.getAll({ available: true });
      setResources(data);
    } catch (err) {
      console.error('Failed to fetch resources:', err);
    }
  };

  const toLocalInputValue = (dateString: string) => {
    const date = new Date(dateString);
    const offsetMinutes = date.getTimezoneOffset();
    const localDate = new Date(date.getTime() - offsetMinutes * 60000);
    return localDate.toISOString().slice(0, 16);
  };

  const fetchBooking = async () => {
    if (!id) {
      return;
    }

    try {
      setLoadingBooking(true);
      const booking = await bookingApi.getById(Number(id));
      setFormData({
        resourceId: booking.resourceId.toString(),
        customerName: booking.customerName,
        customerEmail: booking.customerEmail,
        startTime: toLocalInputValue(booking.startTime),
        endTime: toLocalInputValue(booking.endTime),
        status: booking.status,
        notes: booking.notes || '',
      });
    } catch (err) {
      console.error('Failed to fetch booking:', err);
      setMessage({ type: 'error', text: '予約情報の取得に失敗しました' });
    } finally {
      setLoadingBooking(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    const updatedValue = name === 'status' ? (value as BookingStatus) : value;
    setFormData((prev) => ({
      ...prev,
      [name]: updatedValue,
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

      const booking: Booking = {
        resourceId: parseInt(formData.resourceId),
        customerName: formData.customerName,
        customerEmail: formData.customerEmail,
        startTime: formData.startTime,
        endTime: formData.endTime,
        status: isEditMode ? formData.status : BookingStatus.CONFIRMED,
        notes: formData.notes || undefined,
      };

      if (isEditMode && id) {
        await bookingApi.update(Number(id), booking);
        setMessage({ type: 'success', text: '予約が正常に更新されました' });
        return;
      }

      // Check availability for new booking
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

      await bookingApi.create(booking);
      setMessage({ type: 'success', text: '予約が正常に作成されました' });
      
      // Reset form
      setFormData({
        resourceId: '',
        customerName: '',
        customerEmail: '',
        startTime: '',
        endTime: '',
        status: BookingStatus.CONFIRMED,
        notes: '',
      });
    } catch (err: any) {
      const fallbackMessage = isEditMode ? '予約の更新に失敗しました' : '予約の作成に失敗しました';
      const errorMessage = err.response?.data?.error || fallbackMessage;
      setMessage({ type: 'error', text: errorMessage });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loadingBooking) {
    return <div className="loading">読み込み中...</div>;
  }

  return (
    <div className="booking-form">
      <h2>{isEditMode ? '予約変更' : '新規予約'}</h2>
      
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

        {isEditMode && (
          <div className="form-group">
            <label htmlFor="status">ステータス *</label>
            <select
              id="status"
              name="status"
              value={formData.status}
              onChange={handleChange}
              required
            >
              <option value={BookingStatus.CONFIRMED}>確定</option>
              <option value={BookingStatus.PENDING}>保留中</option>
              <option value={BookingStatus.CANCELLED}>キャンセル</option>
            </select>
          </div>
        )}

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
          {loading ? '処理中...' : isEditMode ? '予約を更新' : '予約を作成'}
        </button>
      </form>
    </div>
  );
};

export default BookingForm;

