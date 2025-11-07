import { afterEach, describe, expect, it, vi } from 'vitest'
import { useDashboard } from '../src/components/Dashboard/DashboardLogic.js'
import { fetchSellersFromAPI, fetchSummaryFromAPI } from '../src/service/DashboardService.js'

vi.mock('../src/service/DashboardService.js')

afterEach(() => vi.resetAllMocks())

function deferred() {
  let resolve
  const promise = new Promise((r) => { resolve = r })
  return { promise, resolve }
}

describe('useDashboard', () => {
  it('loads the seller list', async () => {
    fetchSellersFromAPI.mockResolvedValue([{ id: 1, name: 'TechZone' }])
    const d = useDashboard()

    await d.loadSellers()

    expect(d.sellers.value).toEqual([{ id: 1, name: 'TechZone' }])
    expect(d.sellersLoading.value).toBe(false)
  })

  it('records a seller list failure', async () => {
    fetchSellersFromAPI.mockRejectedValue(new Error('Could not load the seller list'))
    const d = useDashboard()

    await d.loadSellers()

    expect(d.sellers.value).toEqual([])
    expect(d.sellersError.value).toBe('Could not load the seller list')
  })

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

  it('drops a slow response for a seller the user switched away from', async () => {
    const slow = deferred()
    fetchSummaryFromAPI.mockImplementationOnce(() => slow.promise)
    fetchSummaryFromAPI.mockResolvedValueOnce({ totalSales: 9, alerts: [] })
    const d = useDashboard()

    d.sellerId.value = 1
    const first = d.fetchSummary()
    d.sellerId.value = 2
    await d.fetchSummary()
    slow.resolve({ totalSales: 1, alerts: [] })
    await first

    expect(d.summary.value).toEqual({ totalSales: 9, alerts: [] })
  })
})
