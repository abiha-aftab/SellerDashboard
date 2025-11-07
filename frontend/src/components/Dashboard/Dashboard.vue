<script setup>
import { useDashboard } from './DashboardLogic.js'
import './DashboardStyles.css'

const { sellers, sellerId, summary, loading, error, fetchSummary } = useDashboard()
</script>

<template>
  <div class="container">
    <label for="seller-select" class="label">Select Seller:</label>
    <select
      id="seller-select"
      v-model="sellerId"
      @change="fetchSummary"
      class="custom-dropdown"
    >
      <option disabled value="">-- choose seller --</option>
      <option v-for="s in sellers" :key="s.id" :value="s.id">{{ s.name }}</option>
    </select>

    <div class="content">
      <div v-if="loading" class="loading">Loading...</div>
      <div v-if="error" class="error">{{ error }}</div>

      <div v-if="summary" class="summary-box">
        <p><strong>Total Sales (units):</strong> {{ summary.totalSales }}</p>
        <p><strong>Total Revenue:</strong> ${{ summary.totalRevenue.toFixed(2) }}</p>
        <p><strong>Return Rate:</strong> {{ (summary.returnRate * 100).toFixed(2) }}%</p>

        <div v-if="summary.alerts && summary.alerts.length" class="alerts">
          <h4>Alerts</h4>
          <ul>
            <li v-for="a in summary.alerts" :key="a" class="alert-item">{{ a }}</li>
          </ul>
        </div>
        <div v-else>
          <h4>No Alerts</h4>
        </div>
      </div>
    </div>
  </div>
</template>
