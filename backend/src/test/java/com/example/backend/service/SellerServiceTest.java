package com.example.backend.service;

import com.example.backend.dto.SellerSummary;
import com.example.backend.model.Sale;
import com.example.backend.model.Seller;
import com.example.backend.repo.SaleRepository;
import com.example.backend.repo.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class SellerServiceTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 3, 15);
    private static final Seller SELLER = new Seller(1L, "TechZone", "North America");

    private final SaleRepository saleRepo = mock(SaleRepository.class);
    private final SellerRepository sellerRepo = mock(SellerRepository.class);
    private SellerService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        service = new SellerService(saleRepo, sellerRepo, clock);
        when(sellerRepo.existsById(1L)).thenReturn(true);
    }

    private void givenSales(Sale... sales) {
        when(saleRepo.findBySellerIdAndDateBetween(1L, TODAY.minusDays(13), TODAY)).thenReturn(List.of(sales));
    }

    private static Sale sale(int daysAgo, int qty, double price, boolean returned) {
        return new Sale(null, SELLER, TODAY.minusDays(daysAgo), qty, price, returned);
    }

    @Test
    void unknownSellerThrowsWithoutQueryingSales() {
        when(sellerRepo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.getSummary(99L))
                .isInstanceOf(SellerNotFoundException.class)
                .hasMessage("Seller 99 not found");
        verifyNoInteractions(saleRepo);
    }

    @Test
    void queriesExactlyTheLastFourteenDays() {
        givenSales();

        service.getSummary(1L);

        verify(saleRepo).findBySellerIdAndDateBetween(1L, LocalDate.of(2026, 3, 2), TODAY);
    }

    @Test
    void sellerWithNoSalesHasZeroMetricsAndNoAlerts() {
        givenSales();

        SellerSummary s = service.getSummary(1L);

        assertThat(s.getTotalSales()).isZero();
        assertThat(s.getTotalRevenue()).isZero();
        assertThat(s.getReturnRate()).isZero();
        assertThat(s.getAlerts()).isEmpty();
    }

    @Test
    void daySixBelongsToThisWeekAndDaySevenToLastWeek() {
        givenSales(sale(0, 2, 10.0, false), sale(6, 1, 5.0, false), sale(7, 4, 100.0, false), sale(13, 1, 1.0, false));

        SellerSummary s = service.getSummary(1L);

        assertThat(s.getTotalSales()).isEqualTo(3);
        assertThat(s.getTotalRevenue()).isEqualTo(25.0);
    }

    @Test
    void revenueDropOfMoreThanThirtyPercentRaisesAlert() {
        givenSales(sale(1, 1, 69.99, false), sale(8, 1, 100.0, false));

        assertThat(service.getSummary(1L).getAlerts()).containsExactly(SellerService.SALES_DROP_ALERT);
    }

    @Test
    void revenueDropOfExactlyThirtyPercentDoesNotAlert() {
        givenSales(sale(1, 7, 10.0, false), sale(8, 1, 100.0, false));

        assertThat(service.getSummary(1L).getAlerts()).isEmpty();
    }

    @Test
    void noDropAlertWhenLastWeekHadNoSales() {
        givenSales(sale(1, 1, 10.0, false));

        assertThat(service.getSummary(1L).getAlerts()).isEmpty();
    }

    @Test
    void returnRateIsReturnedUnitsOverUnitsSold() {
        givenSales(sale(0, 3, 10.0, true), sale(2, 5, 10.0, false));

        SellerSummary s = service.getSummary(1L);

        assertThat(s.getReturnRate()).isEqualTo(0.375);
        assertThat(s.getAlerts()).containsExactly(SellerService.RETURN_RATE_ALERT);
    }

    @Test
    void returnRateOfExactlyTenPercentDoesNotAlert() {
        givenSales(sale(0, 1, 10.0, true), sale(1, 9, 10.0, false));

        SellerSummary s = service.getSummary(1L);

        assertThat(s.getReturnRate()).isEqualTo(0.1);
        assertThat(s.getAlerts()).isEmpty();
    }

    @Test
    void lastWeekReturnsDoNotCountTowardsThisWeeksRate() {
        givenSales(sale(0, 10, 10.0, false), sale(9, 10, 10.0, true));

        assertThat(service.getSummary(1L).getReturnRate()).isZero();
    }

    @Test
    void bothAlertsCanFireTogether() {
        givenSales(sale(0, 1, 10.0, true), sale(10, 1, 100.0, false));

        assertThat(service.getSummary(1L).getAlerts())
                .containsExactly(SellerService.SALES_DROP_ALERT, SellerService.RETURN_RATE_ALERT);
    }

    @Test
    void roundsRevenueToCentsAndRateToFourPlaces() {
        givenSales(sale(0, 3, 0.1, false), sale(0, 1, 10.0, true), sale(0, 2, 10.0, false));

        SellerSummary s = service.getSummary(1L);

        assertThat(s.getTotalRevenue()).isEqualTo(30.3);
        assertThat(s.getReturnRate()).isEqualTo(0.1667);
    }
}
