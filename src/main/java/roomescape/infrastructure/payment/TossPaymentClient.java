package roomescape.infrastructure.payment;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.request.PaymentInfo;
import roomescape.application.response.TossErrorResponse;
import roomescape.domain.payment.Payment;
import roomescape.exception.PaymentException;

@Component
public class TossPaymentClient implements PaymentClient {

    private static final String TOSS_BASE_URL = "https://api.tosspayments.com/";
    private static final String TOSS_PAYMENT_CONFIRM_URI = "/v1/payments/confirm";
    private static final String SECRET_KEY_PREFIX = "Basic ";
    private static final String DELIMITER = ":";
    private static final int CONNECT_TIMEOUT = 3000;
    private static final int READ_TIMEOUT = 10000;

    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public TossPaymentClient(
            @Value("${payment.secret-key.toss}") String secretKey, final ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
        this.restClient = getRestClient(secretKey);
    }

    public Payment confirmPayment(PaymentInfo paymentInfo) {
        return restClient.post().uri(TOSS_PAYMENT_CONFIRM_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentInfo).retrieve().body(Payment.class);
    }

    private RestClient getRestClient(String secretKey) {
        String encodedKey = getEncodedKey(secretKey + DELIMITER);

        return RestClient.builder()
                .requestFactory(createFactory())
                .baseUrl(TOSS_BASE_URL)
                .defaultStatusHandler(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> {
                            TossErrorResponse error = objectMapper.readValue(response.getBody(),
                                    TossErrorResponse.class);
                            PaymentErrorCode code = PaymentErrorCode.from(error.code());
                            throw new PaymentException(code);
                        }
                )
                .defaultHeader(AUTHORIZATION, SECRET_KEY_PREFIX + encodedKey)
                .build();
    }

    private ClientHttpRequestFactory createFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);
        return factory;
    }

    private String getEncodedKey(String key) {
        return Base64.getEncoder().encodeToString(key.getBytes());
    }
}
