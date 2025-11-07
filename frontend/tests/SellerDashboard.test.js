import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'
import SellerDashboard from '../src/components/Dashboard/SellerDashboard.vue'
import { fetchSellersFromAPI, fetchSummaryFromAPI } from '../src/service/DashboardService.js'

vi.mock('../src/service/DashboardService.js')

const sellers = [
  { id: 3, name: 'EcoMart', region: 'Asia' },
  { id: 1, name: 'TechZone', region: 'North America' }
]

async function mountAndChoose(id) {
  const wrapper = mount(SellerDashboard)
  await flushPromises()
  await wrapper.get('#seller-select').setValue(id)
  await flushPromises()
  return wrapper
}

beforeEach(() => fetchSellersFromAPI.mockResolvedValue(sellers))
afterEach(() => vi.resetAllMocks())

describe('SellerDashboard', () => {
  it('lists sellers from the API and prompts for a choice', async () => {
    const wrapper = mount(SellerDashboard)
    await flushPromises()

    const options = wrapper.findAll('option').map((o) => o.text())
    expect(options).toEqual(['Choose a seller', 'EcoMart · Asia', 'TechZone · North America'])
    expect(wrapper.text()).toContain('Pick a seller')
    expect(wrapper.get('label').attributes('for')).toBe('seller-select')
  })

  it('shows the figures and an all-clear when there are no alerts', async () => {
    fetchSummaryFromAPI.mockResolvedValue({ totalSales: 48, totalRevenue: 3150.25, returnRate: 0.0833, alerts: [] })

    const wrapper = await mountAndChoose(1)

    expect(fetchSummaryFromAPI).toHaveBeenCalledWith(1)
    expect(wrapper.get('h2').text()).toContain('TechZone')
    expect(wrapper.findAll('.figure dd').map((d) => d.text())).toEqual(['48', '$3,150.25', '8.33%'])
    expect(wrapper.find('.all-clear').exists()).toBe(true)
    expect(wrapper.find('.figure--warn').exists()).toBe(false)
  })

  it('lists alerts and flags a high return rate', async () => {
    fetchSummaryFromAPI.mockResolvedValue({
      totalSales: 60, totalRevenue: 4200, returnRate: 0.21, alerts: ['Return rate above 10%']
    })

    const wrapper = await mountAndChoose(3)

    expect(wrapper.findAll('.alerts li').map((li) => li.text())).toEqual(['Return rate above 10%'])
    expect(wrapper.find('.figure--warn').exists()).toBe(true)
  })

  it('shows an API error as an alert with a retry', async () => {
    fetchSummaryFromAPI.mockRejectedValue(new Error('Seller 3 not found'))

    const wrapper = await mountAndChoose(3)

    const alert = wrapper.get('[role="alert"]')
    expect(alert.text()).toContain('Seller 3 not found')
    expect(alert.find('button').text()).toBe('Retry')
  })

  it('disables the picker and offers a retry when sellers fail to load', async () => {
    fetchSellersFromAPI.mockRejectedValue(new Error('Could not load the seller list'))

    const wrapper = mount(SellerDashboard)
    await flushPromises()

    expect(wrapper.get('#seller-select').attributes('disabled')).toBeDefined()
    expect(wrapper.get('[role="alert"]').text()).toContain('Could not load the seller list')
  })
})
