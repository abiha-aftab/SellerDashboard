// Utility function to format currency
export function formatCurrency(amount) {
    return `$${amount.toFixed(2)}`
  }
  
  // Utility function to format percentages
  export function formatPercentage(rate) {
    return `${(rate * 100).toFixed(2)}%`
  }
  