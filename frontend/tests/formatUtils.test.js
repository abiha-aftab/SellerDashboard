import { describe, expect, it } from 'vitest'
import { formatCurrency, formatPercentage } from '../src/utils/formatUtils.js'

describe('formatCurrency', () => {
  it('formats US dollars with grouping and cents', () => {
    expect(formatCurrency(4500.339)).toBe('$4,500.34')
    expect(formatCurrency(0)).toBe('$0.00')
  })
})

describe('formatPercentage', () => {
  it('turns a 0..1 rate into a percentage', () => {
    expect(formatPercentage(0.085)).toBe('8.50%')
    expect(formatPercentage(1)).toBe('100.00%')
  })
})
