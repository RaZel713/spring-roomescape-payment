package roomescape.infrastructure.payment;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.request.PaymentInfo;
import roomescape.application.response.PaymentResponse;
import roomescape.application.response.TossErrorResponse;
import roomescape.exception.PaymentException;

@Component
public class TossPaymentClient implements PaymentClient {

    private static final String TOSS_BASE_URL = "https://api.tosspayments.com/";
    private static final String TOSS_PAYMENT_CONFIRM_URI = "/v1/payments/confirm";
    private static final String SECRET_KEY_PREFIX = "Basic ";
    private static final String DELIMITER = ":";

    private final RestClient restClient;

    public TossPaymentClient(@Value("${payment.secret-key.toss}") String secretKey, ObjectMapper objectMapper,
                             RestClient.Builder restClientBuilder) {
        this.restClient = getRestClient(restClientBuilder, objectMapper, secretKey);
    }

    public PaymentResponse confirmPayment(PaymentInfo paymentInfo) {
        return restClient.post().uri(TOSS_PAYMENT_CONFIRM_URI).contentType(MediaType.APPLICATION_JSON).body(paymentInfo)
                .retrieve().body(PaymentResponse.class);
    }

    private RestClient getRestClient(RestClient.Builder builder, ObjectMapper objectMapper, String secretKey) {
        String encodedKey = getEncodedKey(secretKey + DELIMITER);

        return builder.baseUrl(TOSS_BASE_URL)
                .defaultStatusHandler(status -> status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> {
                            try {
                                TossErrorResponse error = objectMapper.readValue(response.getBody(),
                                        TossErrorResponse.class);
                                PaymentErrorCode code = PaymentErrorCode.from(error.code());
                                throw new PaymentException(code);
                            } catch (Exception e) {
                                throw new PaymentException(PaymentErrorCode.UNKNOWN);
                            }
                        })
                .defaultHeader(AUTHORIZATION, SECRET_KEY_PREFIX + encodedKey)
                .build();
    }

    private String getEncodedKey(String key) {
        return Base64.getEncoder().encodeToString(key.getBytes());
    }
}
