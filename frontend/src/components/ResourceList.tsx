import { useState, useEffect } from 'react';
import { resourceApi } from '../services/api';
import { Resource } from '../types';

/**
 * ResourceList Component
 * Displays a list of all resources
 */
const ResourceList = () => {
  const [resources, setResources] = useState<Resource[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchResources();
  }, [searchTerm]);

  const fetchResources = async () => {
    try {
      setLoading(true);
      const params = searchTerm ? { search: searchTerm } : undefined;
      const data = await resourceApi.getAll(params);
      setResources(data);
      setError(null);
    } catch (err) {
      setError('リソースの取得に失敗しました');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading">読み込み中...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  return (
    <div className="resource-list">
      <div className="list-header">
        <h2>リソース一覧</h2>
        <div className="search-box">
          <input
            type="text"
            placeholder="リソース名で検索..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {resources.length === 0 ? (
        <p className="no-data">リソースがありません</p>
      ) : (
        <div className="resource-grid">
          {resources.map((resource) => (
            <div key={resource.id} className="resource-card">
              <div className="resource-card-header">
                <h3>{resource.name}</h3>
                <span className={`availability-badge ${resource.available ? 'available' : 'unavailable'}`}>
                  {resource.available ? '利用可能' : '利用不可'}
                </span>
              </div>
              <div className="resource-card-body">
                <p className="description">{resource.description || '説明なし'}</p>
                <div className="resource-details">
                  <div className="detail-item">
                    <span className="label">定員:</span>
                    <span className="value">{resource.capacity}名</span>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ResourceList;

