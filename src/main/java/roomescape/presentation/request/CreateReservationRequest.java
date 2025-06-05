package roomescape.presentation.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.application.request.PaymentInfo;

public record CreateReservationRequest(
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull
        LocalDate date,

        @NotNull
        Long timeId,

        @NotNull
        Long themeId,

        String paymentKey,

        String orderId,

        Long amount
) {

    public PaymentInfo toPaymentInfo() {
        return new PaymentInfo(
                paymentKey,
                orderId,
                amount
        );
    }
}
