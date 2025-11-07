import { afterEach, describe, expect, it, vi } from 'vitest'
import { useDashboard } from '../src/components/Dashboard/DashboardLogic.js'
import { fetchSummaryFromAPI } from '../src/service/DashboardService.js'

vi.mock('../src/service/DashboardService.js')

afterEach(() => vi.resetAllMocks())

describe('useDashboard', () => {
  it('does nothing until a seller is chosen', async () => {
    const d = useDashboard()

    await d.fetchSummary()

    expect(fetchSummaryFromAPI).not.toHaveBeenCalled()
  })

  it('loads the summary for the chosen seller', async () => {
    fetchSummaryFromAPI.mockResolvedValue({ totalSales: 5, alerts: [] })
    const d = useDashboard()
    d.sellerId.value = 2

    const pending = d.fetchSummary()
    expect(d.loading.value).toBe(true)
    await pending

    expect(fetchSummaryFromAPI).toHaveBeenCalledWith(2)
    expect(d.summary.value).toEqual({ totalSales: 5, alerts: [] })
    expect(d.loading.value).toBe(false)
  })

  it('keeps the error and clears the old summary on failure', async () => {
    fetchSummaryFromAPI.mockRejectedValue(new Error('Seller 2 not found'))
    const d = useDashboard()
    d.summary.value = { totalSales: 1 }
    d.sellerId.value = 2

    await d.fetchSummary()

    expect(d.summary.value).toBeNull()
    expect(d.error.value).toBe('Seller 2 not found')
    expect(d.loading.value).toBe(false)
  })
})
