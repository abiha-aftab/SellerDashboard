<template>
  <main class="shell">
    <header class="masthead">
      <h1>Seller Dashboard</h1>
      <p>Weekly units, revenue and return rate, with alerts when a seller slips.</p>
    </header>
    <SellerDashboard />
  </main>
</template>

<script setup>
import SellerDashboard from './components/Dashboard/SellerDashboard.vue'
</script>
