import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import BookingList from '../components/BookingList';
import * as api from '../services/api';

vi.mock('../services/api');
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof import('react-router-dom')>('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

const mockBookings = [
  {
    id: 1,
    resourceId: 1,
    customerName: 'テストユーザー1',
    customerEmail: 'test1@example.com',
    startTime: new Date().toISOString(),
    endTime: new Date(Date.now() + 3600000).toISOString(),
    status: 'CONFIRMED' as const,
    notes: 'テストメモ',
  },
  {
    id: 2,
    resourceId: 2,
    customerName: 'テストユーザー2',
    customerEmail: 'test2@example.com',
    startTime: new Date().toISOString(),
    endTime: new Date(Date.now() + 7200000).toISOString(),
    status: 'CANCELLED' as const,
  },
];

const mockResources = [
  { id: 1, name: '会議室A', capacity: 10, available: true },
  { id: 2, name: '会議室B', capacity: 20, available: true },
];

describe('BookingList', () => {
  const renderWithRouter = () => {
    return render(
      <MemoryRouter>
        <BookingList />
      </MemoryRouter>
    );
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (api.bookingApi.getAll as unknown as vi.Mock).mockResolvedValue(mockBookings);
    (api.resourceApi.getAll as unknown as vi.Mock).mockResolvedValue(mockResources);
  });

  it('renders booking list', async () => {
    renderWithRouter();

    await waitFor(() => {
      expect(screen.getByText(/予約一覧/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/テストユーザー1/i)).toBeInTheDocument();
    expect(screen.getByText(/テストユーザー2/i)).toBeInTheDocument();
  });

  it('shows loading state initially', () => {
    (api.bookingApi.getAll as unknown as vi.Mock).mockImplementation(
      () => new Promise(() => {}) // Never resolves
    );

    renderWithRouter();

    expect(screen.getByText(/読み込み中/i)).toBeInTheDocument();
  });

  it('filters bookings by status', async () => {
    const user = userEvent.setup();
    renderWithRouter();

    await waitFor(() => {
      expect(screen.getByText(/テストユーザー1/i)).toBeInTheDocument();
    });

    const statusSelect = screen.getByLabelText(/ステータス/i);
    await user.selectOptions(statusSelect, 'CANCELLED');

    await waitFor(() => {
      expect(api.bookingApi.getAll).toHaveBeenCalledWith(
        expect.objectContaining({ status: 'CANCELLED' })
      );
    });
  });

  it('shows cancel button for confirmed bookings', async () => {
    renderWithRouter();

    await waitFor(() => {
      expect(screen.getByText(/テストユーザー1/i)).toBeInTheDocument();
    });

    // キャンセルボタンを探す（CONFIRMEDステータスの予約のみに表示される）
    const cancelButtons = await screen.findAllByRole('button', { name: 'キャンセル' });
    expect(cancelButtons.length).toBeGreaterThan(0);
  });

  it('shows error message when API call fails', async () => {
    (api.bookingApi.getAll as unknown as vi.Mock).mockRejectedValue(new Error('API Error'));

    renderWithRouter();

    await waitFor(() => {
      expect(screen.getByText(/予約の取得に失敗しました/i)).toBeInTheDocument();
    });
  });

  it('shows no bookings message when list is empty', async () => {
    (api.bookingApi.getAll as unknown as vi.Mock).mockResolvedValue([]);

    renderWithRouter();

    await waitFor(() => {
      expect(screen.getByText(/予約がありません/i)).toBeInTheDocument();
    });
  });

  it('shows edit button for all bookings and navigates', async () => {
    renderWithRouter();

    await waitFor(() => {
      expect(screen.getByText(/テストユーザー1/i)).toBeInTheDocument();
    });

    const editButtons = screen.getAllByRole('button', { name: '変更' });
    expect(editButtons).toHaveLength(2);

    await userEvent.click(editButtons[0]);
    expect(mockNavigate).toHaveBeenCalledWith('/bookings/1/edit');
  });
});

