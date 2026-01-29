import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import NotificationModal from '../components/NotificationModal';
import * as api from '../services/api';

vi.mock('../services/api');

const mockNotifications = [
  {
    id: 1,
    title: '予約作成完了',
    startTime: '2026-01-29T10:00:00',
    endTime: '2026-01-29T12:00:00',
    resourceId: 1,
  },
  {
    id: 2,
    title: '予約作成完了',
    startTime: '2026-01-30T09:00:00',
    endTime: '2026-01-30T10:00:00',
    resourceId: 2,
  },
];

describe('NotificationModal', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    (api.notificationApi.getAll as unknown as vi.Mock).mockResolvedValue(mockNotifications);
  });

  it('表示時に通知一覧を取得し表示する', async () => {
    render(<NotificationModal open={true} onClose={() => {}} />);
    await waitFor(() => {
      expect(screen.getAllByText('通知一覧').some(el => el.tagName === 'H3')).toBe(true);
    });
    expect(screen.getAllByText('予約作成完了').length).toBeGreaterThan(1);
  });

  it('日付フィルタで絞り込める', async () => {
    render(<NotificationModal open={true} onClose={() => {}} />);
    await waitFor(() => {
      expect(screen.getByText('通知一覧')).toBeInTheDocument();
    });
    const input = screen.getByLabelText('開始日でフィルタ:');
    fireEvent.change(input, { target: { value: '2026-01-29' } });
    expect(screen.getAllByText('予約作成完了').length).toBe(1);
    expect(screen.getByText('2026/01/29 10:00')).toBeInTheDocument();
  });

  it('通知がない場合はメッセージを表示', async () => {
    (api.notificationApi.getAll as unknown as vi.Mock).mockResolvedValue([]);
    render(<NotificationModal open={true} onClose={() => {}} />);
    await waitFor(() => {
      expect(screen.getByText('通知がありません')).toBeInTheDocument();
    });
  });

  it('閉じるボタンでonCloseが呼ばれる', async () => {
    const onClose = vi.fn();
    render(<NotificationModal open={true} onClose={onClose} />);
    await waitFor(() => {
      expect(screen.getByText('通知一覧')).toBeInTheDocument();
    });
    fireEvent.click(screen.getByRole('button', { name: /×/ }));
    expect(onClose).toHaveBeenCalled();
  });
});
