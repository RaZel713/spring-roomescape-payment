package roomescape.infrastructure;

import roomescape.application.request.PaymentInfo;
import roomescape.domain.payment.Payment;

public interface PaymentClient {

    Payment confirmPayment(PaymentInfo paymentInfo);
}
