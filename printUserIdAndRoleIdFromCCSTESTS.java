import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;

class YourClassTest {

    private YourClass yourClass; // Reemplaza con el nombre real de la clase que contiene el método

    @Mock
    private ValidateCredDomain apiRequest;
    
    @Mock
    private CardValidationResponse cardResponse;

    @Mock
    private ProductProfileResponse productProfileResponse;

    @Mock
    private ProductProfileDetails productProfileDetails;

    @Mock
    private FujiConstants.E2ERespFunc e2ERespFunc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        yourClass = new YourClass(); // Instancia de la clase real
    }

    @Test
    void testUserIdProductMapVacio() {
        Map<String, ProductProfileResponse> userIdProductMap = new HashMap<>();
        yourClass.printUserIdAndRoleIdFromCCS(userIdProductMap, apiRequest);
        
        // Verificamos que no se haya agregado nada al mapa
        assertTrue(userIdProductMap.isEmpty());
    }

    @Test
    void testProductProfileResponseEsNull() {
        Map<String, ProductProfileResponse> userIdProductMap = new HashMap<>();
        userIdProductMap.put("user1", null); // Simulamos un usuario sin respuesta de perfil
        
        yourClass.printUserIdAndRoleIdFromCCS(userIdProductMap, apiRequest);
        
        // Verificamos que no se haya agregado nada
        assertTrue(userIdProductMap.isEmpty());
    }

    @Test
    void testProductProfileDetailsVacio() {
        Map<String, ProductProfileResponse> userIdProductMap = new HashMap<>();
        userIdProductMap.put("user1", productProfileResponse);
        
        when(productProfileResponse.getProductProfileDetails()).thenReturn(Collections.emptyList());

        yourClass.printUserIdAndRoleIdFromCCS(userIdProductMap, apiRequest);
        
        assertTrue(userIdProductMap.isEmpty());
    }

    @Test
    void testProductProfileNoCoincide() {
        Map<String, ProductProfileResponse> userIdProductMap = new HashMap<>();
        userIdProductMap.put("user1", productProfileResponse);
        
        when(productProfileResponse.getProductProfileDetails()).thenReturn(List.of(productProfileDetails));
        when(productProfileDetails.getProductType()).thenReturn("ABCD"); // No cumple con .startsWith("CC")

        yourClass.printUserIdAndRoleIdFromCCS(userIdProductMap, apiRequest);
        
        assertTrue(userIdProductMap.isEmpty());
    }

    @Test
    void testCoincidenciaConPreviousCardNumber() {
        Map<String, ProductProfileResponse> userIdProductMap = new HashMap<>();
        userIdProductMap.put("user1", productProfileResponse);
        
        when(apiRequest.getResponseMap()).thenReturn(Map.of(ResponseEnum.CREDICARDVALIDATIONFUTURE, cardResponse));
        when(cardResponse.getPreviousAccountNumber()).thenReturn("12345");
        when(cardResponse.getNewEAccountNumber()).thenReturn("67890");

        when(productProfileResponse.getProductProfileDetails()).thenReturn(List.of(productProfileDetails));
        when(productProfileDetails.getProductType()).thenReturn("CC1");
        when(productProfileDetails.getProductAccountNumber()).thenReturn("12345");

        yourClass.printUserIdAndRoleIdFromCCS(userIdProductMap, apiRequest);
        
        assertEquals("user1", userIdProductMap.keySet().iterator().next());
    }

    @Test
    void testCoincidenciaConNewCardNumber() {
        Map<String, ProductProfileResponse> userIdProductMap = new HashMap<>();
        userIdProductMap.put("user1", productProfileResponse);
        
        when(apiRequest.getResponseMap()).thenReturn(Map.of(ResponseEnum.CREDICARDVALIDATIONFUTURE, cardResponse));
        when(cardResponse.getPreviousAccountNumber()).thenReturn(null);
        when(cardResponse.getNewEAccountNumber()).thenReturn("67890");

        when(productProfileResponse.getProductProfileDetails()).thenReturn(List.of(productProfileDetails));
        when(productProfileDetails.getProductType()).thenReturn("CC1");
        when(productProfileDetails.getProductAccountNumber()).thenReturn("67890");

        yourClass.printUserIdAndRoleIdFromCCS(userIdProductMap, apiRequest);
        
        assertEquals("user1", userIdProductMap.keySet().iterator().next());
    }
}
