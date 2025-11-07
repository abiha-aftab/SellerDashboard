<script setup>
import { computed, onMounted } from 'vue'
import { useDashboard } from './DashboardLogic.js'
import { formatCurrency, formatPercentage } from '../../utils/formatUtils.js'
import './DashboardStyles.css'

const RETURN_RATE_LIMIT = 0.1

const {
  sellers, sellersLoading, sellersError, sellerId, summary, loading, error, loadSellers, fetchSummary
} = useDashboard()

const selectedSeller = computed(() => sellers.value.find((s) => s.id === sellerId.value))
const returnRateHigh = computed(() => summary.value && summary.value.returnRate > RETURN_RATE_LIMIT)

onMounted(loadSellers)
</script>

<template>
  <section class="dashboard">
    <div class="picker">
      <label for="seller-select">Seller</label>
      <select
        id="seller-select"
        v-model="sellerId"
        :disabled="sellersLoading || !sellers.length"
        @change="fetchSummary"
      >
        <option disabled value="">
          {{ sellersLoading ? 'Loading sellers…' : 'Choose a seller' }}
        </option>
        <option v-for="s in sellers" :key="s.id" :value="s.id">
          {{ s.name }} · {{ s.region }}
        </option>
      </select>
      <p v-if="sellersError" class="notice notice--error" role="alert">
        {{ sellersError }}
        <button type="button" class="link-button" @click="loadSellers">Try again</button>
      </p>
    </div>

    <div class="panel" :aria-busy="loading">
      <p v-if="!sellerId && !sellersError" class="placeholder">
        Pick a seller to see units, revenue and returns for the last seven days.
      </p>

      <div v-else-if="loading" class="placeholder" role="status">
        Crunching this week's numbers…
      </div>

      <p v-else-if="error" class="notice notice--error" role="alert">
        {{ error }}
        <button type="button" class="link-button" @click="fetchSummary">Retry</button>
      </p>

      <template v-else-if="summary">
        <h2 class="panel__title">
          {{ selectedSeller?.name }}
          <span class="panel__subtitle">last 7 days</span>
        </h2>

        <dl class="figures">
          <div class="figure">
            <dt>Units sold</dt>
            <dd>{{ summary.totalSales }}</dd>
          </div>
          <div class="figure">
            <dt>Revenue</dt>
            <dd>{{ formatCurrency(summary.totalRevenue) }}</dd>
          </div>
          <div class="figure" :class="{ 'figure--warn': returnRateHigh }">
            <dt>Return rate</dt>
            <dd>{{ formatPercentage(summary.returnRate) }}</dd>
            <span class="figure__hint">limit {{ formatPercentage(RETURN_RATE_LIMIT) }}</span>
          </div>
        </dl>

        <div v-if="summary.alerts.length" class="alerts" role="status">
          <h3>Needs attention</h3>
          <ul>
            <li v-for="a in summary.alerts" :key="a">{{ a }}</li>
          </ul>
        </div>
        <p v-else class="all-clear" role="status">
          No alerts: sales are holding up and returns are under the limit.
        </p>
      </template>
    </div>
  </section>
</template>
