import axios from 'axios'

function apiError(error, fallback) {
  return new Error(error.response?.data?.message || fallback, { cause: error })
}

export async function fetchSellersFromAPI() {
  try {
    const response = await axios.get('/api/sellers')
    return response.data
  } catch (error) {
    throw apiError(error, 'Could not load the seller list')
  }
}

export async function fetchSummaryFromAPI(sellerId) {
  try {
    const response = await axios.get(`/api/seller/${sellerId}/summary`)
    return response.data
  } catch (error) {
    throw apiError(error, 'Error fetching summary from API')
  }
}
