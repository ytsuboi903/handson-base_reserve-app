/**
 * API service for communicating with the backend
 */
import axios from 'axios';
import { Resource, Booking, BookingStatus, AvailabilityCheck } from '../types';

const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Resource API
export const resourceApi = {
  getAll: async (params?: { available?: boolean; search?: string }): Promise<Resource[]> => {
    const response = await api.get('/resources', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Resource> => {
    const response = await api.get(`/resources/${id}`);
    return response.data;
  },

  create: async (resource: Resource): Promise<Resource> => {
    const response = await api.post('/resources', resource);
    return response.data;
  },

  update: async (id: number, resource: Resource): Promise<Resource> => {
    const response = await api.put(`/resources/${id}`, resource);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/resources/${id}`);
  },
};

// Booking API
export const bookingApi = {
  getAll: async (params?: {
    resourceId?: number;
    status?: BookingStatus;
    customerEmail?: string;
    start?: string;
    end?: string;
  }): Promise<Booking[]> => {
    const response = await api.get('/bookings', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Booking> => {
    const response = await api.get(`/bookings/${id}`);
    return response.data;
  },

  checkAvailability: async (
    resourceId: number,
    start: string,
    end: string
  ): Promise<AvailabilityCheck> => {
    const response = await api.get('/bookings/available', {
      params: { resourceId, start, end },
    });
    return response.data;
  },

  create: async (booking: Booking): Promise<Booking> => {
    const response = await api.post('/bookings', booking);
    return response.data;
  },

  update: async (id: number, booking: Booking): Promise<Booking> => {
    const response = await api.put(`/bookings/${id}`, booking);
    return response.data;
  },

  cancel: async (id: number): Promise<Booking> => {
    const response = await api.patch(`/bookings/${id}/cancel`);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/bookings/${id}`);
  },
};

export default api;

