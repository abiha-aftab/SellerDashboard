import { ref } from 'vue'
import { fetchSummaryFromAPI } from '../../service/DashboardService.js'

export function useDashboard() {
  const sellers = ref([
    { id: 1, name: 'TechZone' },
    { id: 2, name: 'GadgetHub' },
    { id: 3, name: 'EcoMart' }
  ])
  const sellerId = ref('')
  const summary = ref(null)
  const loading = ref(false)
  const error = ref('')

  async function fetchSummary() {
    if (!sellerId.value) return
    loading.value = true
    error.value = ''
    summary.value = null
    try {
      const data = await fetchSummaryFromAPI(sellerId.value)
      summary.value = data
    } catch (err) {
      error.value = err.message || 'Error fetching summary'
    } finally {
      loading.value = false
    }
  }

  return { sellers, sellerId, summary, loading, error, fetchSummary }
}
