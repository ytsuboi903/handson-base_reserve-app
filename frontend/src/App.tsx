import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import BookingList from './components/BookingList';
import ResourceList from './components/ResourceList';
import BookingForm from './components/BookingForm';
import './App.css';

/**
 * Main App Component
 */
function App() {
  return (
    <Router>
      <div className="app">
        <header className="app-header">
          <h1>予約管理システム</h1>
          <nav>
            <Link to="/">予約一覧</Link>
            <Link to="/resources">リソース一覧</Link>
            <Link to="/new-booking">新規予約</Link>
          </nav>
        </header>

        <main className="app-main">
          <Routes>
            <Route path="/" element={<BookingList />} />
            <Route path="/resources" element={<ResourceList />} />
            <Route path="/new-booking" element={<BookingForm />} />
            <Route path="/bookings/:id/edit" element={<BookingForm />} />
          </Routes>
        </main>

        <footer className="app-footer">
          <p>&copy; 2025 予約管理システム - Agentic AI PoC</p>
        </footer>
      </div>
    </Router>
  );
}

export default App;

