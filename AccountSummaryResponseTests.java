import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
class YourClassTest {  // Reemplaza con el nombre real de la clase que contiene el método

    @InjectMocks
    private YourClass yourClass; // Reemplaza con la clase que contiene el método

    @Mock
    private Tps4604ApiClient tps4604ApiClient;

    @Mock
    private AccountSummaryRequest bankAccountSummaryRequest;

    @Mock
    private TPS4604Response tps4604Response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAccountSummaryResponse_Success() {
        // Configuración del mock para la solicitud
        String cin = "12345";
        HttpHeaders headers = new HttpHeaders();

        when(yourClass.populateBankAccountSummaryRequest(cin)).thenReturn(bankAccountSummaryRequest);
        when(tps4604ApiClient.getTps4604AccountSummary(bankAccountSummaryRequest, headers))
                .thenReturn(tps4604Response);

        // Ejecución del método
        TPS4604Response response = yourClass.getAccountSummaryResponse(cin, headers);

        // Verificaciones
        assertNotNull(response);
        assertEquals(tps4604Response, response);
        verify(yourClass).populateBankAccountSummaryRequest(cin);
        verify(tps4604ApiClient).getTps4604AccountSummary(bankAccountSummaryRequest, headers);
    }

    @Test
    void testGetAccountSummaryResponse_ExceptionThrown() {
        // Configuración del mock para la solicitud
        String cin = "12345";
        HttpHeaders headers = new HttpHeaders();

        when(yourClass.populateBankAccountSummaryRequest(cin)).thenReturn(bankAccountSummaryRequest);
        when(tps4604ApiClient.getTps4604AccountSummary(bankAccountSummaryRequest, headers))
                .thenThrow(new RuntimeException("API Error"));

        // Ejecución y verificación de la excepción
        Exception exception = assertThrows(RuntimeException.class, () ->
                yourClass.getAccountSummaryResponse(cin, headers)
        );

        // Verificación del mensaje de la excepción
        assertEquals("API Error", exception.getMessage());

        // Verificaciones de llamadas a métodos
        verify(yourClass).populateBankAccountSummaryRequest(cin);
        verify(tps4604ApiClient).getTps4604AccountSummary(bankAccountSummaryRequest, headers);
    }
}
