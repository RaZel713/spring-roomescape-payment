package roomescape.presentation.rest;

import static org.mockito.BDDMockito.given;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import roomescape.application.request.PaymentInfo;
import roomescape.domain.payment.Payment;
import roomescape.exception.PaymentErrorCode;
import roomescape.exception.PaymentException;
import roomescape.infrastructure.PaymentClient;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationControllerTest {

    @MockBean
    private PaymentClient paymentClient;

    private static final Map<String, String> RESERVATION_BODY = Map.of("date", "3000-03-17", "timeId", "1", "themeId",
            "1", "memberId", "2", "paymentKey", "paymentKey", "orderId", "orderId", "amount", "1000");

    @Test
    @DisplayName("예약 추가 요청시, id를 포함한 예약 내용과 CREATED를 응답한다")
    void createReservation() {

        PaymentInfo paymentInfo = new PaymentInfo("paymentKey", "orderId", 1000);
        Payment payment = new Payment("paymentKey", "orderId", "테스트 방탈출 예약 결제 1건", 1000);

        given(paymentClient.confirmPayment(paymentInfo)).willReturn(payment);

        var token = RestAssured.given().contentType(ContentType.JSON)
                .body(Map.of("email", "user1@email.com", "password", "password1")).when().post("/login").then()
                .statusCode(200).extract().response().getDetailedCookies().getValue("token");

        RestAssured.given().log().all().contentType(ContentType.JSON).cookie("token", token) // 쿠키로 인증 정보 전달
                .body(RESERVATION_BODY).when().post("/reservations").then().log().all()
                .statusCode(HttpStatus.CREATED.value()).body("date", Matchers.equalTo("3000-03-17"));
    }

    @EnumSource(PaymentErrorCode.class)
    @ParameterizedTest
    @DisplayName("예약 추가 요청시, 결제에 실패하면 실패 에러 응답 코드를 반환한다")
    void createReservation_WhenPaymentFailed(PaymentErrorCode errorCode) {

        PaymentInfo paymentInfo = new PaymentInfo("paymentKey", "orderId", 1000);

        given(paymentClient.confirmPayment(paymentInfo)).willThrow(new PaymentException(errorCode));

        var token = RestAssured.given().contentType(ContentType.JSON)
                .body(Map.of("email", "user1@email.com", "password", "password1")).when().post("/login").then()
                .statusCode(200).extract().response().getDetailedCookies().getValue("token");

        RestAssured.given().log().all().contentType(ContentType.JSON).cookie("token", token) // 쿠키로 인증 정보 전달
                .body(RESERVATION_BODY).when().post("/reservations").then().log().all()
                .statusCode(errorCode.getStatusCode().value()).body("message", Matchers.equalTo(errorCode.getMessage()));
    }

    @Test
    @DisplayName("예약 조회 요청시, 존재하는 모든 예약과 OK를 응답한다")
    void findReservations() {
        RestAssured.given().log().all().when().get("/reservations")

                .then().log().all().statusCode(HttpStatus.OK.value()).body("size()", Matchers.is(3));
    }

    @Test
    @DisplayName("예약 삭제 요청시, 주어진 아이디에 해당하는 예약이 없다면 NOT FOUND를 응답한다.")
    void removeReservation_WhenReservationDoesNotExisted() {
        RestAssured.given().log().all().when().delete("/reservations/1000").then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("예약 삭제 요청시, 주어진 아이디에 해당하는 예약이 있다면 삭제하고 NO CONTENT를 응답한다.")
    void removeReservation() {
        RestAssured.given().log().all().when().delete("/reservations/1").then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
