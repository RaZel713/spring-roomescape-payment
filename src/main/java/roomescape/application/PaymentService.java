package roomescape.application;

import org.springframework.stereotype.Service;
import roomescape.application.request.PaymentInfo;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.infrastructure.PaymentClient;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(final PaymentClient paymentClient, final PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public Payment savePayment(final PaymentInfo paymentInfo) {
        Payment payment = paymentClient.confirmPayment(paymentInfo);

        return paymentRepository.save(payment);
    }
}
