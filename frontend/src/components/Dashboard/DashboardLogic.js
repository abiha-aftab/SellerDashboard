import { ref } from 'vue'
import { fetchSellersFromAPI, fetchSummaryFromAPI } from '../../service/DashboardService.js'

export function useDashboard() {
  const sellers = ref([])
  const sellersLoading = ref(false)
  const sellersError = ref('')
  const sellerId = ref('')
  const summary = ref(null)
  const loading = ref(false)
  const error = ref('')

  async function loadSellers() {
    sellersLoading.value = true
    sellersError.value = ''
    try {
      sellers.value = await fetchSellersFromAPI()
    } catch (err) {
      sellersError.value = err.message
    } finally {
      sellersLoading.value = false
    }
  }

  async function fetchSummary() {
    if (!sellerId.value) return
    const requestedId = sellerId.value
    loading.value = true
    error.value = ''
    summary.value = null
    try {
      const data = await fetchSummaryFromAPI(requestedId)
      // Ignore a slow response for a seller the user has already switched away from.
      if (requestedId === sellerId.value) summary.value = data
    } catch (err) {
      if (requestedId === sellerId.value) error.value = err.message || 'Error fetching summary'
    } finally {
      if (requestedId === sellerId.value) loading.value = false
    }
  }

  return { sellers, sellersLoading, sellersError, sellerId, summary, loading, error, loadSellers, fetchSummary }
}
