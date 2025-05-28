package roomescape.infrastructure;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.request.PaymentInfo;
import roomescape.application.response.TossErrorResponse;
import roomescape.domain.payment.Payment;
import roomescape.exception.PaymentErrorCode;
import roomescape.exception.PaymentException;

@Component
public class TossPaymentClient implements PaymentClient {

    private static final String TOSS_BASE_URL = "https://api.tosspayments.com/";
    private static final String TOSS_PAYMENT_CONFIRM_URI = "/v1/payments/confirm";
    private static final String SECRET_KEY_PREFIX = "Basic ";
    private static final String DELIMITER = ":";

    private final String key;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(
            @Value("${payment.secret-key.toss}") String secretKey, final ObjectMapper objectMapper
    ) {
        this.key = secretKey + DELIMITER;
        this.objectMapper = objectMapper;
    }

    public Payment confirmPayment(PaymentInfo paymentInfo) {
        RestClient restClient = RestClient.builder().baseUrl(TOSS_BASE_URL)
                .defaultStatusHandler(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> {
                            TossErrorResponse error = objectMapper.readValue(response.getBody(),
                                    TossErrorResponse.class);
                            PaymentErrorCode code = PaymentErrorCode.from(error.code());
                            throw new PaymentException(code);
                        }
                ).build();

        String encodedKey = getEncodedKey();

        return restClient.post().uri(TOSS_PAYMENT_CONFIRM_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, SECRET_KEY_PREFIX + encodedKey)
                .body(paymentInfo).retrieve().body(Payment.class)
                ;
    }

    private String getEncodedKey() {
        return Base64.getEncoder().encodeToString(key.getBytes());
    }
}
