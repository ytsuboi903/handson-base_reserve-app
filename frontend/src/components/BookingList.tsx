import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { bookingApi, resourceApi } from '../services/api';
import { Booking, Resource, BookingStatus } from '../types';
import { format } from 'date-fns';
import NotificationModal from './NotificationModal';

/**
 * BookingList Component
 * Displays a list of all bookings with filtering options
 */
const BookingList = () => {
  const navigate = useNavigate();
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [resources, setResources] = useState<Resource[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filterStatus, setFilterStatus] = useState<BookingStatus | ''>('');
  const [notificationModalOpen, setNotificationModalOpen] = useState(false);

  useEffect(() => {
    fetchBookings();
    fetchResources();
  }, [filterStatus]);

  const fetchBookings = async () => {
    try {
      setLoading(true);
      const params = filterStatus ? { status: filterStatus } : undefined;
      const data = await bookingApi.getAll(params);
      setBookings(data);
      setError(null);
    } catch (err) {
      setError('予約の取得に失敗しました');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchResources = async () => {
    try {
      const data = await resourceApi.getAll();
      setResources(data);
    } catch (err) {
      console.error('Failed to fetch resources:', err);
    }
  };

  const getResourceName = (resourceId: number): string => {
    const resource = resources.find((r) => r.id === resourceId);
    return resource ? resource.name : `リソースID: ${resourceId}`;
  };

  const handleCancel = async (id: number) => {
    if (!window.confirm('この予約をキャンセルしますか?')) {
      return;
    }

    try {
      await bookingApi.cancel(id);
      fetchBookings();
    } catch (err) {
      alert('キャンセルに失敗しました');
      console.error(err);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('この予約を削除しますか?')) {
      return;
    }

    try {
      await bookingApi.delete(id);
      fetchBookings();
    } catch (err) {
      alert('削除に失敗しました');
      console.error(err);
    }
  };

  const handleEdit = (id: number) => {
    navigate(`/bookings/${id}/edit`);
  };

  const getStatusBadgeClass = (status: BookingStatus): string => {
    switch (status) {
      case BookingStatus.CONFIRMED:
        return 'status-confirmed';
      case BookingStatus.PENDING:
        return 'status-pending';
      case BookingStatus.CANCELLED:
        return 'status-cancelled';
      default:
        return '';
    }
  };

  const getStatusText = (status: BookingStatus): string => {
    switch (status) {
      case BookingStatus.CONFIRMED:
        return '確定';
      case BookingStatus.PENDING:
        return '保留中';
      case BookingStatus.CANCELLED:
        return 'キャンセル';
      default:
        return status;
    }
  };

  if (loading) {
    return <div className="loading">読み込み中...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  return (
    <div className="booking-list">
      <div className="list-header">
        <h2>予約一覧</h2>
        <div className="header-controls">
          <div className="filter-controls">
            <label>
              ステータス:
              <select
                value={filterStatus}
                onChange={(e) => setFilterStatus(e.target.value as BookingStatus | '')}
              >
                <option value="">すべて</option>
                <option value={BookingStatus.CONFIRMED}>確定</option>
                <option value={BookingStatus.PENDING}>保留中</option>
                <option value={BookingStatus.CANCELLED}>キャンセル</option>
              </select>
            </label>
          </div>
          <div className="notification-link-area">
            <button
              className="notification-link"
              onClick={() => setNotificationModalOpen(true)}
            >
              通知一覧
            </button>
          </div>
        </div>
      </div>

      {bookings.length === 0 ? (
        <p className="no-data">予約がありません</p>
      ) : (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>リソース</th>
                <th>予約者</th>
                <th>メール</th>
                <th>開始時刻</th>
                <th>終了時刻</th>
                <th>ステータス</th>
                <th>備考</th>
                <th>アクション</th>
              </tr>
            </thead>
            <tbody>
              {bookings.map((booking) => (
                <tr key={booking.id}>
                  <td>{booking.id}</td>
                  <td>{getResourceName(booking.resourceId)}</td>
                  <td>{booking.customerName}</td>
                  <td>{booking.customerEmail}</td>
                  <td>{format(new Date(booking.startTime), 'yyyy/MM/dd HH:mm')}</td>
                  <td>{format(new Date(booking.endTime), 'yyyy/MM/dd HH:mm')}</td>
                  <td>
                    <span className={`status-badge ${getStatusBadgeClass(booking.status)}`}>
                      {getStatusText(booking.status)}
                    </span>
                  </td>
                  <td>{booking.notes || '-'}</td>
                  <td>
                    <div className="action-buttons">
                      {booking.status !== BookingStatus.CANCELLED && (
                        <button
                          onClick={() => handleCancel(booking.id!)}
                          className="btn-cancel btn-icon"
                          aria-label="キャンセル"
                          title="キャンセル"
                        >
                          <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
                            <path
                              d="M6 6l12 12M18 6l-12 12"
                              fill="none"
                              stroke="currentColor"
                              strokeWidth="2"
                              strokeLinecap="round"
                            />
                          </svg>
                        </button>
                      )}
                      <button
                        onClick={() => handleEdit(booking.id!)}
                        className="btn-edit btn-icon"
                        aria-label="変更"
                        title="変更"
                      >
                        <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
                          <path d="M4 20h4l10-10-4-4L4 16v4z" fill="currentColor" />
                        </svg>
                      </button>
                      <button
                        onClick={() => handleDelete(booking.id!)}
                        className="btn-delete btn-icon"
                        aria-label="削除"
                        title="削除"
                      >
                        <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
                          <path d="M6 7h12l-1 13H7L6 7zm3-3h6l1 2H8l1-2z" fill="currentColor" />
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      <NotificationModal open={notificationModalOpen} onClose={() => setNotificationModalOpen(false)} />
    </div>
  );
};

export default BookingList;

