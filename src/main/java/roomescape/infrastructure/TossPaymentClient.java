package roomescape.infrastructure;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.request.PaymentInfo;
import roomescape.domain.payment.Payment;

@Component
public class TossPaymentClient implements PaymentClient {

    private static final String TOSS_BASE_URL = "https://api.tosspayments.com/";
    private static final String TOSS_PAYMENT_CONFIRM_URI = "/v1/payments/confirm";
    private static final String SECRET_KEY_PREFIX = "Basic ";
    private static final String DELIMITER = ":";

    private final String key;

    public TossPaymentClient(
            @Value("${payment.secret-key.toss}") String secretKey
    ) {
        this.key = secretKey + DELIMITER;
    }

    public Payment confirmPayment(PaymentInfo paymentInfo) {
        RestClient restClient = RestClient.builder().baseUrl(TOSS_BASE_URL).build();

        String encodedKey = getEncodedKey();

        return restClient.post().uri(TOSS_PAYMENT_CONFIRM_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, SECRET_KEY_PREFIX + encodedKey)
                .body(paymentInfo).retrieve().body(Payment.class);
    }

    private String getEncodedKey() {
        return Base64.getEncoder().encodeToString(key.getBytes());
    }
}
