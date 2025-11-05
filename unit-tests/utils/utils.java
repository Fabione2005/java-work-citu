import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

class BiometricsAggregatorTest {

    @Test
    void shouldFallIntoJsonProcessingExceptionCatch() throws Exception {
        // 1) Interceptar "new ObjectMapper()" dentro del método
        try (MockedConstruction<ObjectMapper> mocked = mockConstruction(
                ObjectMapper.class,
                (mock, context) -> {
                    // 2) Forzar que lance JsonProcessingException al serializar
                    when(mock.writeValueAsString(any()))
                            .thenThrow(new JsonProcessingException("boom") {});
                }
        )) {
            // 3) Instanciar la clase bajo prueba (inyectá dependencias reales o mocks según tu caso)
            BiometricsAggregator aggregator = new BiometricsAggregator(
                /* ... dependencias reales/mocks que use tu ctor ... */
            );

            // 4) Preparar inputs mínimos para que el flujo entre al try donde se crea el ObjectMapper
            //    Ajustá los tipos/nombres según tu firma real:
            Object getDeviceEnrollmentResponse = /* construir mock/obj con userEnrolledDeviceId distinto a uniqueDeviceIdentifier */;
            Object bioEnrollReq = /* construir mock/obj con isForceEnroll == false */;
            String uniqueDeviceIdentifier = "ABC-123";

            // IMPORTANTE:
            // Asegurate que:
            // - userEnrolledDeviceId sea NOT BLANK
            // - userEnrolledDeviceId != uniqueDeviceIdentifier
            // - !bioEnrollReq.isForceEnroll()
            // Eso dispara el bloque try { ObjectMapper mapper = new ObjectMapper(); ... }

            // 5) Invocar el método PRIVADO con ReflectionTestUtils
            //    Cambiá el nombre EXACTO del método y parámetros a los tuyos reales
            Exception thrown = assertThrows(Exception.class, () -> {
                ReflectionTestUtils.invokeMethod(
                    aggregator,
                    "processSnapshotAndDonrPopperEnrollment",  // <- nombre exacto del privado
                    getDeviceEnrollmentResponse,
                    bioEnrollReq,
                    uniqueDeviceIdentifier
                );
            });

            // 6) Afirmaciones útiles:
            // - Se construyó un ObjectMapper (se interceptó el "new")
            assertEquals(1, mocked.constructed().size());

            // - El flujo llegó al catch: en tu código, después del catch
            //   normalmente lanzás una BiometricsAPIException (o similar).
            //   Si sabés el tipo exacto, reemplazá Exception por ese tipo y validá mensaje/código.
            //   Ejemplo:
            // assertTrue(thrown instanceof BiometricsAPIException);
            // assertEquals(BiometricsCodeEnum.ENROLL_SP_CONFIRM_ERROR.getMessage(), thrown.getMessage());
        }
    }
}

