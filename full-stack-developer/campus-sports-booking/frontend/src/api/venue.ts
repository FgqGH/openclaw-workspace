import request from '@/utils/request'

export interface VenueCategory {
  id?: number
  name: string
  icon?: string
  sortOrder?: number
  status?: number
}

export interface Venue {
  id?: number
  categoryId: number
  name: string
  location: string
  capacity: number
  description?: string
  imageUrl?: string
  status?: number
}

export interface TimeSlot {
  id?: number
  venueId: number
  slotDate: string
  startTime: string
  endTime: string
  maxBookings: number
  bookedCount?: number
  status?: number
  createdAt?: string
}

export const venueApi = {
  // 分类
  getCategories(): Promise<VenueCategory[]> {
    return request.get<VenueCategory[]>('/api/venue/category/list')
  },

  getCategoryById(id: number): Promise<VenueCategory> {
    return request.get<VenueCategory>(`/api/venue/category/${id}`)
  },

  createCategory(data: VenueCategory) {
    return request.post('/api/venue/category', data)
  },

  updateCategory(data: VenueCategory) {
    return request.put('/api/venue/category', data)
  },

  deleteCategory(id: number) {
    return request.delete(`/api/venue/category/${id}`)
  },

  // 场地
  getVenues(): Promise<Venue[]> {
    return request.get<Venue[]>('/api/venue/list')
  },

  getVenuesByCategory(categoryId: number): Promise<Venue[]> {
    return request.get<Venue[]>(`/api/venue/list/category/${categoryId}`)
  },

  getVenueById(id: number): Promise<Venue> {
    return request.get<Venue>(`/api/venue/${id}`)
  },

  createVenue(data: Venue) {
    return request.post('/api/venue', data)
  },

  updateVenue(data: Venue) {
    return request.put('/api/venue', data)
  },

  deleteVenue(id: number) {
    return request.delete(`/api/venue/${id}`)
  },

  // 时段
  getTimeSlots(venueId: number, date: string): Promise<TimeSlot[]> {
    return request.get<TimeSlot[]>(`/api/venue/timeslot/venue/${venueId}/date/${date}`)
  },

  getAvailableSlots(venueId: number, date: string): Promise<TimeSlot[]> {
    return request.get<TimeSlot[]>(`/api/venue/timeslot/venue/${venueId}/date/${date}/available`)
  },

  createTimeSlot(data: TimeSlot) {
    return request.post('/api/venue/timeslot', data)
  },

  batchCreateTimeSlots(data: TimeSlot[]) {
    return request.post('/api/venue/timeslot/batch', data)
  },

  updateTimeSlot(data: TimeSlot) {
    return request.put('/api/venue/timeslot', data)
  },

  deleteTimeSlot(id: number) {
    return request.delete(`/api/venue/timeslot/${id}`)
  }
}
