import { useEffect, useState } from 'react';
import { notificationApi } from '../services/api';
import { format, parseISO, isSameDay } from 'date-fns';

type Notification = {
  id: number;
  bookingId: number;
  type: string;
  title: string;
  body?: string;
  createdAt?: string;
};

type Props = {
  onClose: () => void;
};

const NotificationModal = ({ onClose }: Props) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(false);
  const [filterDate, setFilterDate] = useState<string>('');

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const data = await notificationApi.getAll();
      setNotifications(data);
    } catch (err) {
      console.error('Failed to fetch notifications', err);
    } finally {
      setLoading(false);
    }
  };

  const filtered = notifications.filter((n) => {
    if (!filterDate) return true;
    if (!n.createdAt) return false;
    return isSameDay(parseISO(n.createdAt), parseISO(filterDate));
  });

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <div className="modal-header">
          <h3>通知一覧</h3>
          <button aria-label="close" onClick={onClose}>
            ×
          </button>
        </div>
        <div className="modal-controls">
          <label>
            日付フィルタ:
            <input
              type="date"
              value={filterDate}
              onChange={(e) => setFilterDate(e.target.value ? e.target.value : '')}
            />
          </label>
        </div>
        <div className="modal-body">
          {loading ? (
            <p>読み込み中...</p>
          ) : filtered.length === 0 ? (
            <p>通知がありません</p>
          ) : (
            <ul>
              {filtered.map((n) => (
                <li key={n.id} className="notification-item">
                  <strong>{n.title}</strong>
                  <div>{n.body}</div>
                  <div className="created">{n.createdAt ? format(new Date(n.createdAt), 'yyyy/MM/dd HH:mm') : ''}</div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
};

export default NotificationModal;
