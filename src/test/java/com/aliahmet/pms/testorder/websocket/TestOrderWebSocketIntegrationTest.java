package com.aliahmet.pms.testorder.websocket;

import com.aliahmet.pms.patient.AdmissionStatus;
import com.aliahmet.pms.patient.ClinicType;
import com.aliahmet.pms.patient.InsuranceType;
import com.aliahmet.pms.patient.Patient;
import com.aliahmet.pms.patient.PatientRepository;

import com.aliahmet.pms.testorder.TestOrderRepository;
import com.aliahmet.pms.testorder.TestOrderService;
import com.aliahmet.pms.testorder.TestType;
import com.aliahmet.pms.testorder.dto.CreateTestOrderRequest;
import com.aliahmet.pms.testorder.dto.TestOrderResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class TestOrderWebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TestOrderRepository testOrderRepository;

    @Autowired
    private TestOrderService testOrderService;

    private Patient patient;

    @BeforeEach
    void setUp() {

        testOrderRepository.deleteAll();
        patientRepository.deleteAll();

        patient = new Patient();

        patient.setFullName("Ayşe Kaya");
        patient.setEmail("ayse.kaya@example.com");
        patient.setPhone("05551112233");
        patient.setClinicType(ClinicType.CARDIOLOGY);
        patient.setInsuranceType(InsuranceType.GOVERNMENT);
        patient.setMedicalHistory("Hypertension history");
        patient.setConsentGiven(true);
        patient.setAdmissionStatus(AdmissionStatus.ADMITTED);
        patient.setAdmissionReason(
                "Patient admission was approved."
        );

        patient = patientRepository.save(patient);
    }

    @Test
    void shouldReceiveProcessingAndCompletedMessages()
            throws Exception {

        BlockingQueue<Map<String, Object>> messages =
                new ArrayBlockingQueue<>(10);

        WebSocketStompClient stompClient =
                new WebSocketStompClient(
                        new StandardWebSocketClient()
                );

        stompClient.setMessageConverter(
                new JacksonJsonMessageConverter()
        );

        StompSession session =
                stompClient.connectAsync(
                                "ws://localhost:" + port + "/ws",
                                new StompSessionHandlerAdapter() {
                                }
                        )
                        .get(5, TimeUnit.SECONDS);

        session.subscribe(
                "/topic/test-orders",
                new StompFrameHandler() {

                    @Override
                    public Type getPayloadType(
                            StompHeaders headers
                    ) {
                        return Map.class;
                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public void handleFrame(
                            StompHeaders headers,
                            Object payload
                    ) {
                        messages.offer(
                                (Map<String, Object>) payload
                        );
                    }
                }
        );

        // Subscription'ın broker'a ulaşması için kısa süre.
        Thread.sleep(200);

        CreateTestOrderRequest request =
                new CreateTestOrderRequest();

        request.setPatientId(patient.getId());
        request.setTestType(TestType.X_RAY);

        TestOrderResponse created =
                testOrderService.createTestOrder(request);

        testOrderService.prepareForExecution(
                created.getId()
        );

        Map<String, Object> processing =
                messages.poll(
                        3,
                        TimeUnit.SECONDS
                );

        assertNotNull(processing);

        assertEquals(
                "PROCESSING",
                processing.get("status")
        );

        assertEquals(
                created.getId().longValue(),
                ((Number) processing.get("testOrderId"))
                        .longValue()
        );

        testOrderService.executeTestOrder(
                created.getId()
        );

        Map<String, Object> completed =
                messages.poll(
                        3,
                        TimeUnit.SECONDS
                );

        assertNotNull(completed);

        assertEquals(
                "COMPLETED",
                completed.get("status")
        );

        assertEquals(
                "X-RAY was performed by Radiology Department",
                completed.get("resultMessage")
        );

        session.disconnect();
        stompClient.stop();
    }
}