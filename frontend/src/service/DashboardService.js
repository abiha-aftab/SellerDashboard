import axios from 'axios'

export async function fetchSummaryFromAPI(sellerId) {
  try {
    const response = await axios.get(`/api/seller/${sellerId}/summary`)
    return response.data
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Error fetching summary from API')
  }
}
