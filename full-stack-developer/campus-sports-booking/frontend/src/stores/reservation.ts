import { defineStore } from 'pinia'
import { ref } from 'vue'
import { reservationApi, type Reservation } from '@/api/reservation'

export const useReservationStore = defineStore('reservation', () => {
  const myReservations = ref<Reservation[]>([])
  const allReservations = ref<Reservation[]>([])
  const loading = ref(false)

  const fetchMyReservations = async () => {
    loading.value = true
    try {
      const data = await reservationApi.getMyReservations()
      myReservations.value = data
    } finally {
      loading.value = false
    }
  }

  const fetchAllReservations = async () => {
    loading.value = true
    try {
      const data = await reservationApi.getAllReservations()
      allReservations.value = data
    } finally {
      loading.value = false
    }
  }

  const createReservation = async (timeSlotId: number) => {
    return await reservationApi.create({ timeSlotId })
  }

  const cancelReservation = async (id: number) => {
    await reservationApi.cancel(id)
    await fetchMyReservations()
  }

  const adminCancelReservation = async (id: number) => {
    await reservationApi.adminCancel(id)
    await fetchAllReservations()
  }

  return {
    myReservations,
    allReservations,
    loading,
    fetchMyReservations,
    fetchAllReservations,
    createReservation,
    cancelReservation,
    adminCancelReservation
  }
})
