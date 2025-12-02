/**
 * Type definitions for the Booking Management System
 */

export enum BookingStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED',
}

export interface Resource {
  id?: number;
  name: string;
  description?: string;
  capacity: number;
  available: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface Booking {
  id?: number;
  resourceId: number;
  customerName: string;
  customerEmail: string;
  startTime: string;
  endTime: string;
  status: BookingStatus;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface AvailabilityCheck {
  available: boolean;
}

export interface Notification {
  id?: number;
  bookingId?: number;
  type?: string;
  title?: string;
  body?: string;
  resourceId?: number;
  startTime?: string;
  endTime?: string;
  createdAt?: string;
}

