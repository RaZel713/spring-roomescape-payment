package roomescape.domain.payment;

public record Payment(
        String paymentKey,
        String orderId,
        String orderName,
        long amount
) {
}
