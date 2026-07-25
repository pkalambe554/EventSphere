/**
 * Monitoring module (Milestone 6).
 *
 * Actuator + Prometheus are already exposed via application.yml
 * (management.endpoints.web.exposure.include). When you build this out, add
 * a MetricsConfig here registering custom Micrometer counters:
 *   - bookings.success / bookings.failed
 *   - payment.gateway.latency
 *   - seat.lock.contention
 * Then point a Grafana dashboard at the scraped /actuator/prometheus data.
 */
package com.eventsphere.monitoring;
