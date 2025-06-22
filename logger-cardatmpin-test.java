import com.citigroup.ccp.logging.common.properties.DataMaskingProperties;
import com.citigroup.ccp.logging.security.MessageConversionUtil;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class AccountNumberMaskingTest {

    @BeforeEach
    void setupMasking() {
        // Activar el masking
        ReflectionTestUtils.setField(MessageConversionUtil.class, "isMaskingEnabled", true);

        // Crear configuración de masking para accountNumber
        DataMaskingProperties maskingProps = new DataMaskingProperties();
        maskingProps.setType("accountNumber");
        maskingProps.setDataPattern("\\b(A|a)(ccount)(Number){0,1}([\"']{0,1})(:|=)([\"']{0,1})(\\d*)(\\d{4})\\b");
        maskingProps.setMaskingFormat("$1$2$3$4$5$6xxxxxxx$8");

        // Inyectar la configuración
        ReflectionTestUtils.setField(
                MessageConversionUtil.class,
                "dataMaskingConfig",
                List.of(maskingProps)
        );
    }

    @Test
    void testEscapeLogInjectionWithAccountNumber() {
        String rawMessage = "accountNumber is:: 1234567890123456";

        // Aplicar enmascaramiento
        String maskedMessage = MessageConversionUtil.escapeLogInjection(rawMessage, "test");

        // Verificar que el número fue enmascarado
        assertTrue(maskedMessage.contains("accountNumberxxxxxxx3456"));
        System.out.println("Masked log: " + maskedMessage);
    }

    @Test
    void testGetContactInfoLogsMaskedAccountNumber() {
        // Capturar los logs de la clase que contiene el método getContactInfo
        LogCaptor logCaptor = LogCaptor.forClass(CardPinSubmitAggregator.class);

        // Llamar al método con un número de cuenta real
        CardPinSubmitAggregator service = new CardPinSubmitAggregator();
        service.getContactInfo(List.of(), "1234567890123456");

        // Revisar si algún log contiene el valor enmascarado
        boolean foundMaskedLog = logCaptor.getDebugLogs().stream()
                .map(log -> MessageConversionUtil.escapeLogInjection(log, "test"))
                .anyMatch(log -> log.contains("accountNumberxxxxxxx3456"));

        assertTrue(foundMaskedLog, "Expected masked account number in logs");
    }
}
