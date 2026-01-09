import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import BookingList from '../components/BookingList';
import * as api from '../services/api';

vi.mock('../services/api');

const mockNotifications = [
  {
    id: 1,
    bookingId: 10,
    type: 'CREATED',
    title: '予約作成完了',
    body: 'リソース:1 開始:2026-01-10 10:00 終了:2026-01-10 12:00',
    createdAt: new Date().toISOString(),
  },
];

const mockBookings: any[] = [];
const mockResources: any[] = [];

describe('BookingList Notifications', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    (api.bookingApi.getAll as unknown as vi.Mock).mockResolvedValue(mockBookings);
    (api.resourceApi.getAll as unknown as vi.Mock).mockResolvedValue(mockResources);
    (api.notificationApi.getAll as unknown as vi.Mock).mockResolvedValue(mockNotifications);
  });

  it('opens notification modal and shows notifications', async () => {
    const user = userEvent.setup();
    render(<BookingList />);

    // Click notification button
    const btn = await screen.findByText('通知一覧');
    await user.click(btn);

    await waitFor(() => {
      expect(screen.getByRole('heading', { name: /通知一覧/i })).toBeInTheDocument();
    });

    expect(screen.getByText(/予約作成完了/i)).toBeInTheDocument();
  });

  it('filters notifications by date', async () => {
    const user = userEvent.setup();
    render(<BookingList />);

    await user.click(await screen.findByText('通知一覧'));

    await waitFor(() => expect(screen.getByText(/予約作成完了/i)).toBeInTheDocument());

    // set date to tomorrow (no notifications expected)
    const dateInput = screen.getByLabelText(/日付フィルタ/i);
    await user.type(dateInput, '1999-01-01');

    await waitFor(() => expect(screen.getByText(/通知がありません/i)).toBeInTheDocument());
  });
});
