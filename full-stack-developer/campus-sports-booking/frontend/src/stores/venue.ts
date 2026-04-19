import { defineStore } from 'pinia'
import { ref } from 'vue'
import { venueApi, type VenueCategory, type Venue, type TimeSlot } from '@/api/venue'

export const useVenueStore = defineStore('venue', () => {
  const categories = ref<VenueCategory[]>([])
  const venues = ref<Venue[]>([])
  const currentVenue = ref<Venue | null>(null)
  const timeSlots = ref<TimeSlot[]>([])
  const loading = ref(false)

  const fetchCategories = async () => {
    loading.value = true
    try {
      const data = await venueApi.getCategories()
      categories.value = data
    } finally {
      loading.value = false
    }
  }

  const fetchVenues = async () => {
    loading.value = true
    try {
      const data = await venueApi.getVenues()
      venues.value = data
    } finally {
      loading.value = false
    }
  }

  const fetchVenuesByCategory = async (categoryId: number) => {
    loading.value = true
    try {
      const data = await venueApi.getVenuesByCategory(categoryId)
      venues.value = data
    } finally {
      loading.value = false
    }
  }

  const fetchVenueById = async (id: number) => {
    loading.value = true
    try {
      const data = await venueApi.getVenueById(id)
      currentVenue.value = data
      return data
    } finally {
      loading.value = false
    }
  }

  const fetchTimeSlots = async (venueId: number, date: string) => {
    const data = await venueApi.getTimeSlots(venueId, date)
    timeSlots.value = data
    return data
  }

  const fetchAvailableSlots = async (venueId: number, date: string) => {
    const data = await venueApi.getAvailableSlots(venueId, date)
    timeSlots.value = data
    return data
  }

  return {
    categories,
    venues,
    currentVenue,
    timeSlots,
    loading,
    fetchCategories,
    fetchVenues,
    fetchVenuesByCategory,
    fetchVenueById,
    fetchTimeSlots,
    fetchAvailableSlots
  }
})
