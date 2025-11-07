const currency = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' })

export function formatCurrency(amount) {
  return currency.format(amount)
}

export function formatPercentage(rate) {
  return `${(rate * 100).toFixed(2)}%`
}
