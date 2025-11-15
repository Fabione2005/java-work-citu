@ExtendWith(MockitoExtension.class)
class CmrServiceTest {

    @Mock
    private ServiceResponseHandler serviceResponseHandler;

    @Mock
    private CmrClient cmrClient;

    @InjectMocks
    private CmrService cmrService;

    @Test
    void testGetCmrResponseModel_successSetsSuccessStatus() throws Exception {
        // -------- Arrange --------
        SessionDetails sessionDetails = Mockito.mock(SessionDetails.class);
        Mockito.when(sessionDetails.getAusr()).thenReturn("123456");

        // XML válido retornado por el cliente CMR
        String mockXmlResponse =
                "<CmrResponseModel>" +
                        "<cmrResponseStatus>INIT</cmrResponseStatus>" +
                        "<accountList></accountList>" +
                "</CmrResponseModel>";

        // Simular llamada exitosa del cliente CMR
        Mockito.when(cmrClient.getCmrData(Mockito.anyString()))
                .thenReturn(mockXmlResponse);

        // El response handler NO debe lanzar error
        Mockito.doNothing().when(serviceResponseHandler)
                .validateResponse(Mockito.anyString());

        // -------- Act --------
        CmrResponseModel result =
                cmrService.getCmrResponseModel(sessionDetails, null);

        // -------- Assert --------
        Assertions.assertNotNull(result);
        Assertions.assertEquals("SUCCESS", result.getCmrResponseStatus());
    }
}
