package roomescape.presentation.response;

public record PaymentResponse(
    String paymentKey,
    String orderId,
    String orderName
) {
}

