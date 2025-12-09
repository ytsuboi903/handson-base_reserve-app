import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ResourceList from '../components/ResourceList';
import * as api from '../services/api';

vi.mock('../services/api');

const mockResources = [
  {
    id: 1,
    name: '会議室A',
    capacity: 10,
    available: true,
    description: '会議室Aの説明',
  },
  {
    id: 2,
    name: '会議室B',
    capacity: 20,
    available: false,
    description: '会議室Bの説明',
  },
  {
    id: 3,
    name: '会議室C',
    capacity: 30,
    available: true,
    description: null,
  },
];

describe('ResourceList', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    (api.resourceApi.getAll as unknown as vi.Mock).mockResolvedValue(mockResources);
  });

  it('renders resource list', async () => {
    render(<ResourceList />);

    await waitFor(() => {
      expect(screen.getByText(/リソース一覧/i)).toBeInTheDocument();
    });

    expect(screen.getAllByText(/会議室A/i).length).toBeGreaterThan(0);
    expect(screen.getAllByText(/会議室B/i).length).toBeGreaterThan(0);
    expect(screen.getAllByText(/会議室C/i).length).toBeGreaterThan(0);
  });

  it('shows loading state initially', () => {
    (api.resourceApi.getAll as unknown as vi.Mock).mockImplementation(
      () => new Promise(() => {}) // Never resolves
    );

    render(<ResourceList />);

    expect(screen.getByText(/読み込み中/i)).toBeInTheDocument();
  });

  it('displays resource details', async () => {
    render(<ResourceList />);

    await waitFor(() => {
      expect(screen.getAllByText(/会議室A/i).length).toBeGreaterThan(0);
    });

    // 定員情報は複数の要素に分かれている可能性があるため、より柔軟に検索
    expect(screen.getAllByText(/定員:/i).length).toBeGreaterThan(0);
    expect(screen.getByText(/10名/i)).toBeInTheDocument();
    expect(screen.getByText(/20名/i)).toBeInTheDocument();
    expect(screen.getByText(/30名/i)).toBeInTheDocument();
  });

  it('shows availability status', async () => {
    render(<ResourceList />);

    await waitFor(() => {
      const availableBadges = screen.getAllByText(/利用可能/i);
      expect(availableBadges.length).toBeGreaterThan(0);
      expect(screen.getByText(/利用不可/i)).toBeInTheDocument();
    });
  });

  it('shows default description when description is null', async () => {
    render(<ResourceList />);

    await waitFor(() => {
      expect(screen.getByText(/説明なし/i)).toBeInTheDocument();
    });
  });

  it('filters resources by search term', async () => {
    const user = userEvent.setup();
    render(<ResourceList />);

    await waitFor(() => {
      expect(screen.getAllByText(/会議室A/i).length).toBeGreaterThan(0);
    });

    const searchInput = screen.getByPlaceholderText(/リソース名で検索/i);
    await user.type(searchInput, '会議室A');

    await waitFor(() => {
      expect(api.resourceApi.getAll).toHaveBeenCalledWith({ search: '会議室A' });
    });
  });

  it('shows no data message when no resources', async () => {
    (api.resourceApi.getAll as unknown as vi.Mock).mockResolvedValue([]);

    render(<ResourceList />);

    await waitFor(() => {
      expect(screen.getByText(/リソースがありません/i)).toBeInTheDocument();
    });
  });

  it('shows error message when API call fails', async () => {
    (api.resourceApi.getAll as unknown as vi.Mock).mockRejectedValue(new Error('API Error'));

    render(<ResourceList />);

    await waitFor(() => {
      expect(screen.getByText(/リソースの取得に失敗しました/i)).toBeInTheDocument();
    });
  });

  it('clears search and fetches all resources', async () => {
    const user = userEvent.setup();
    render(<ResourceList />);

    await waitFor(() => {
      expect(screen.getByPlaceholderText(/リソース名で検索/i)).toBeInTheDocument();
    });

    const searchInput = screen.getByPlaceholderText(/リソース名で検索/i);
    await user.type(searchInput, 'test');
    await user.clear(searchInput);

    await waitFor(() => {
      expect(api.resourceApi.getAll).toHaveBeenCalledWith(undefined);
    });
  });
});

