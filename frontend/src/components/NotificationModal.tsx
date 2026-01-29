import { useEffect, useState } from 'react';
import { Notification } from '../types';
import { notificationApi } from '../services/api';
import { format } from 'date-fns';
import './NotificationModal.css';

interface NotificationModalProps {
  open: boolean;
  onClose: () => void;
}

const NotificationModal = ({ open, onClose }: NotificationModalProps) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [filterDate, setFilterDate] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (open) fetchNotifications();
  }, [open]);

  const fetchNotifications = async () => {
    setLoading(true);
    try {
      const data = await notificationApi.getAll();
      setNotifications(data);
      setError(null);
    } catch (err) {
      setError('通知の取得に失敗しました');
    } finally {
      setLoading(false);
    }
  };

  const filtered = filterDate
    ? notifications.filter((n) => n.startTime && n.startTime.startsWith(filterDate))
    : notifications;

  if (!open) return null;

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>通知一覧</h3>
          <button className="modal-close" onClick={onClose}>&times;</button>
        </div>
        <div className="modal-controls">
          <label>
            開始日でフィルタ:
            <input
              type="date"
              value={filterDate}
              onChange={(e) => setFilterDate(e.target.value)}
            />
          </label>
        </div>
        {loading ? (
          <div className="loading">読み込み中...</div>
        ) : error ? (
          <div className="error">{error}</div>
        ) : filtered.length === 0 ? (
          <div className="no-data">通知がありません</div>
        ) : (
          <table className="notification-table">
            <thead>
              <tr>
                <th>タイトル</th>
                <th>開始時刻</th>
                <th>終了時刻</th>
                <th>リソースID</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((n) => (
                <tr key={n.id}>
                  <td>{n.title}</td>
                  <td>{n.startTime ? format(new Date(n.startTime), 'yyyy/MM/dd HH:mm') : '-'}</td>
                  <td>{n.endTime ? format(new Date(n.endTime), 'yyyy/MM/dd HH:mm') : '-'}</td>
                  <td>{n.resourceId ?? '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default NotificationModal;
