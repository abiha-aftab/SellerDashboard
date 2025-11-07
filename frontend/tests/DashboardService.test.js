import { afterEach, describe, expect, it, vi } from 'vitest'
import axios from 'axios'
import { fetchSellersFromAPI, fetchSummaryFromAPI } from '../src/service/DashboardService.js'

vi.mock('axios')

afterEach(() => vi.resetAllMocks())

describe('fetchSellersFromAPI', () => {
  it('returns the seller list', async () => {
    axios.get.mockResolvedValue({ data: [{ id: 1, name: 'TechZone' }] })

    await expect(fetchSellersFromAPI()).resolves.toEqual([{ id: 1, name: 'TechZone' }])
    expect(axios.get).toHaveBeenCalledWith('/api/sellers')
  })

  it('reports a readable error when the API is down', async () => {
    axios.get.mockRejectedValue(new Error('Network Error'))

    await expect(fetchSellersFromAPI()).rejects.toThrow('Could not load the seller list')
  })
})

describe('fetchSummaryFromAPI', () => {
  it('requests the seller summary and returns the body', async () => {
    axios.get.mockResolvedValue({ data: { totalSales: 3 } })

    await expect(fetchSummaryFromAPI(2)).resolves.toEqual({ totalSales: 3 })
    expect(axios.get).toHaveBeenCalledWith('/api/seller/2/summary')
  })

  it('surfaces the API error message and keeps the cause', async () => {
    const original = { response: { data: { message: 'Seller 9 not found' } } }
    axios.get.mockRejectedValue(original)

    const err = await fetchSummaryFromAPI(9).catch((e) => e)
    expect(err.message).toBe('Seller 9 not found')
    expect(err.cause).toBe(original)
  })

  it('falls back to a generic message when the API sent none', async () => {
    axios.get.mockRejectedValue(new Error('Network Error'))

    await expect(fetchSummaryFromAPI(1)).rejects.toThrow('Error fetching summary from API')
  })
})
