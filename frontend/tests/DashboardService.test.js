import { afterEach, describe, expect, it, vi } from 'vitest'
import axios from 'axios'
import { fetchSummaryFromAPI } from '../src/service/DashboardService.js'

vi.mock('axios')

afterEach(() => vi.resetAllMocks())

describe('fetchSummaryFromAPI', () => {
  it('requests the seller summary and returns the body', async () => {
    axios.get.mockResolvedValue({ data: { totalSales: 3 } })

    await expect(fetchSummaryFromAPI(2)).resolves.toEqual({ totalSales: 3 })
    expect(axios.get).toHaveBeenCalledWith('/api/seller/2/summary')
  })

  it('surfaces the API error message', async () => {
    axios.get.mockRejectedValue({ response: { data: { message: 'Seller 9 not found' } } })

    await expect(fetchSummaryFromAPI(9)).rejects.toThrow('Seller 9 not found')
  })

  it('falls back to a generic message when the API sent none', async () => {
    axios.get.mockRejectedValue(new Error('Network Error'))

    await expect(fetchSummaryFromAPI(1)).rejects.toThrow('Error fetching summary from API')
  })
})
