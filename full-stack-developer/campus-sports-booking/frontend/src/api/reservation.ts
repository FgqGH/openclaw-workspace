import request from '@/utils/request'

export interface Reservation {
  id: number
  userId: number
  timeSlotId: number
  venueId: number
  status: number
  createdAt: string
  cancelledAt: string
  // 冗余信息
  venueName?: string
  slotDate?: string
  startTime?: string
  endTime?: string
}

export interface ReservationRequest {
  timeSlotId: number
}

export const reservationApi = {
  create(data: ReservationRequest): Promise<Reservation> {
    return request.post<Reservation>('/api/reservation', data)
  },

  cancel(id: number) {
    return request.delete(`/api/reservation/${id}`)
  },

  getMyReservations(): Promise<Reservation[]> {
    return request.get<Reservation[]>('/api/reservation/my')
  },

  getAllReservations(): Promise<Reservation[]> {
    return request.get<Reservation[]>('/api/reservation/all')
  },

  adminCancel(id: number) {
    return request.delete(`/api/reservation/admin/${id}`)
  }
}
