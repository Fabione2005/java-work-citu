package com.citi.enroll.biometrics.config;

import com.citi.enroll.biometrics.config.MfaConfig;
import com.citi.enroll.biometrics.config.MfaConfig.DataBasedOnEnrollmentType;
import com.citi.enroll.biometrics.exception.NoTenantException;
import com.citi.enroll.biometrics.util.BiometricsConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentTenantConfigTest {

    private EnrollmentTenantConfig cfg;

    @BeforeEach
    void setUp() {
        cfg = new EnrollmentTenantConfig();
    }

    //
    // postConstruct(): covers validateConfig(mfaConfig) and all branches
    //

    @Test
    void postConstruct_whenMfaConfigIsNull_shouldThrow() {
        cfg.setMfaConfig(null);
        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(cfg, "postConstruct"),
            "postConstruct() should fail if mfaConfig == null");
    }

    @Test
    void postConstruct_whenMfaConfigEmpty_shouldNotThrow() {
        cfg.setMfaConfig(Collections.emptyMap());
        // empty map → no entries → no exception
        ReflectionTestUtils.invokeMethod(cfg, "postConstruct");
    }

    @Test
    void postConstruct_whenTenantNameBlank_shouldThrow() {
        Map<String, MfaConfig> m = new HashMap<>();
        m.put("", new MfaConfig());
        cfg.setMfaConfig(m);

        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(cfg, "postConstruct"),
            "blank tenant name must trigger NoTenantException");
    }

    @Test
    void postConstruct_whenMfaConfigValueIsNull_shouldThrow() {
        Map<String, MfaConfig> m = new HashMap<>();
        m.put("t1", null);
        cfg.setMfaConfig(m);

        NoTenantException ex = assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(cfg, "postConstruct"));
        assertTrue(ex.getMessage().contains("Data is null for tenant"),
            "null MfaConfig should mention 'Data is null for tenant'");
    }

    @Test
    void postConstruct_whenEventTypeBlank_shouldThrow() {
        MfaConfig mfa = new MfaConfig();
        mfa.setEventType("  ");  // blank
        mfa.setDigitalApplicationType(Collections.singletonMap("app", "type"));
        // use a real EnrollmentType key
        mfa.setDataBasedOnEnrollmentType(
            Collections.singletonMap(
                BiometricsConstants.EnrollmentType.SNAPSHOT,
                new DataBasedOnEnrollmentType()
            )
        );
        cfg.setMfaConfig(Collections.singletonMap("tenantA", mfa));

        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(cfg, "postConstruct"),
            "blank eventType must trigger NoTenantException");
    }

    @Test
    void postConstruct_whenDigitalApplicationTypeEmpty_shouldThrow() {
        MfaConfig mfa = new MfaConfig();
        mfa.setEventType("evt");
        mfa.setDigitalApplicationType(Collections.emptyMap());  // invalid
        mfa.setDataBasedOnEnrollmentType(
            Collections.singletonMap(
                BiometricsConstants.EnrollmentType.SNAPSHOT,
                new DataBasedOnEnrollmentType()
            )
        );
        cfg.setMfaConfig(Collections.singletonMap("tenantB", mfa));

        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(cfg, "postConstruct"),
            "empty digitalApplicationType must trigger NoTenantException");
    }

    @Test
    void postConstruct_whenDataBasedOnEnrollmentTypeNull_shouldThrow() {
        MfaConfig mfa = new MfaConfig();
        mfa.setEventType("evt");
        mfa.setDigitalApplicationType(Collections.singletonMap("app", "type"));
        mfa.setDataBasedOnEnrollmentType(null);  // invalid
        cfg.setMfaConfig(Collections.singletonMap("tenantC", mfa));

        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(cfg, "postConstruct"),
            "null dataBasedOnEnrollmentType must trigger NoTenantException");
    }

    @Test
    void postConstruct_whenDataBasedOnEnrollmentTypeHasNullValue_shouldThrow() {
        Map<BiometricsConstants.EnrollmentType, DataBasedOnEnrollmentType> badMap =
            Collections.singletonMap(BiometricsConstants.EnrollmentType.SNAPSHOT, null);
        MfaConfig mfa = new MfaConfig();
        mfa.setEventType("evt");
        mfa.setDigitalApplicationType(Collections.singletonMap("app", "type"));
        mfa.setDataBasedOnEnrollmentType(badMap);
        cfg.setMfaConfig(Collections.singletonMap("tenantD", mfa));

        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(cfg, "postConstruct"),
            "null DataBasedOnEnrollmentType entry must trigger NoTenantException");
    }

    @Test
    void postConstruct_whenClientDefinedEventTypeNull_shouldThrow() {
        DataBasedOnEnrollmentType dob = new DataBasedOnEnrollmentType();
        dob.setClientDefinedEventType(null);  // invalid
        MfaConfig mfa = new MfaConfig();
        mfa.setEventType("evt");
        mfa.setDigitalApplicationType(Collections.singletonMap("app", "type"));
        mfa.setDataBasedOnEnrollmentType(
            Collections.singletonMap(
                BiometricsConstants.EnrollmentType.SNAPSHOT,
                dob
            )
        );
        cfg.setMfaConfig(Collections.singletonMap("tenantE", mfa));

        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(cfg, "postConstruct"),
            "null clientDefinedEventType should trigger NoTenantException");
    }

    @Test
    void postConstruct_whenClientDefinedEventTypeBlankValue_shouldThrow() {
        DataBasedOnEnrollmentType dob = new DataBasedOnEnrollmentType();
        // use a real CustomerType key
        dob.setClientDefinedEventType(
            Collections.singletonMap(BiometricsConstants.CustomerType.BANK, "")
        );
        MfaConfig mfa = new MfaConfig();
        mfa.setEventType("evt");
        mfa.setDigitalApplicationType(Collections.singletonMap("app", "type"));
        mfa.setDataBasedOnEnrollmentType(
            Collections.singletonMap(
                BiometricsConstants.EnrollmentType.SNAPSHOT,
                dob
            )
        );
        cfg.setMfaConfig(Collections.singletonMap("tenantF", mfa));

        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(cfg, "postConstruct"),
            "blank clientDefinedEventType value should trigger NoTenantException");
    }

    @Test
    void postConstruct_whenEverythingValid_shouldNotThrow() {
        DataBasedOnEnrollmentType dob = new DataBasedOnEnrollmentType();
        dob.setClientDefinedEventType(
            Collections.singletonMap(BiometricsConstants.CustomerType.BANK, "someEvent")
        );
        MfaConfig mfa = new MfaConfig();
        mfa.setEventType("evt");
        mfa.setDigitalApplicationType(Collections.singletonMap("app", "type"));
        mfa.setDataBasedOnEnrollmentType(
            Collections.singletonMap(
                BiometricsConstants.EnrollmentType.SNAPSHOT,
                dob
            )
        );
        cfg.setMfaConfig(Collections.singletonMap("tenantOK", mfa));

        // no exception on valid config
        ReflectionTestUtils.invokeMethod(cfg, "postConstruct");
    }

    //
    // Directly test each validator in isolation
    //

    @Test
    void validateEventType_blank_throws() {
        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(cfg, "validateEventType", "T", ""),
            "blank eventType must throw");
    }

    @Test
    void validateEventType_nonBlank_passes() {
        ReflectionTestUtils.invokeMethod(cfg, "validateEventType", "T", "ok");
    }

    @Test
    void validateDigitalApplicationType_emptyMap_throws() {
        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(
                cfg, "validateDigitalApplicationType", "T", Collections.emptyMap()),
            "empty digitalApplicationType must throw");
    }

    @Test
    void validateDigitalApplicationType_validMap_passes() {
        Map<String, String> good = Collections.singletonMap("app", "type");
        ReflectionTestUtils.invokeMethod(
            cfg, "validateDigitalApplicationType", "T", good);
    }

    @Test
    void validateClientDefinedEventType_nullMap_throws() {
        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(
                cfg, "validateClientDefinedEventType", "T", null),
            "null clientDefinedEventType must throw");
    }

    @Test
    void validateClientDefinedEventType_blankValue_throws() {
        Map<BiometricsConstants.CustomerType, String> bad =
            Collections.singletonMap(BiometricsConstants.CustomerType.BANK, "");
        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(
                cfg, "validateClientDefinedEventType", "T", bad),
            "blank clientDefinedEventType value must throw");
    }

    @Test
    void validateClientDefinedEventType_validMap_passes() {
        Map<BiometricsConstants.CustomerType, String> good =
            Collections.singletonMap(BiometricsConstants.CustomerType.BANK, "evt");
        ReflectionTestUtils.invokeMethod(
            cfg, "validateClientDefinedEventType", "T", good);
    }

    @Test
    void validateDataBasedOnEnrollmentType_null_throws() {
        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(
                cfg, "validateDataBasedOnEnrollmentType", "T", null),
            "null dataBasedOnEnrollmentType must throw");
    }

    @Test
    void validateDataBasedOnEnrollmentType_emptyMap_passes() {
        ReflectionTestUtils.invokeMethod(
            cfg, "validateDataBasedOnEnrollmentType", "T", Collections.emptyMap());
    }

    @Test
    void validateDataBasedOnEnrollmentType_nullValue_throws() {
        Map<BiometricsConstants.EnrollmentType, DataBasedOnEnrollmentType> bad =
            Collections.singletonMap(BiometricsConstants.EnrollmentType.SNAPSHOT, null);
        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(
                cfg, "validateDataBasedOnEnrollmentType", "T", bad),
            "null DataBasedOnEnrollmentType entry must throw");
    }

    @Test
    void validateDataBasedOnEnrollmentType_valid_passes() {
        DataBasedOnEnrollmentType dob = new DataBasedOnEnrollmentType();
        dob.setClientDefinedEventType(
            Collections.singletonMap(BiometricsConstants.CustomerType.BANK, "evt"));
        Map<BiometricsConstants.EnrollmentType, DataBasedOnEnrollmentType> good =
            Collections.singletonMap(BiometricsConstants.EnrollmentType.SNAPSHOT, dob);
        ReflectionTestUtils.invokeMethod(
            cfg, "validateDataBasedOnEnrollmentType", "T", good);
    }

    @Test
    void validateCustomerType_null_returnsFalse() {
        boolean ok = (boolean) ReflectionTestUtils.invokeMethod(
            cfg, "validateCustomerType", (Object) null);
        assertFalse(ok, "null customerType → false");
    }

    @Test
    void validateCustomerType_valid_returnsTrue() {
        boolean ok = (boolean) ReflectionTestUtils.invokeMethod(
            cfg, "validateCustomerType", BiometricsConstants.CustomerType.BANK);
        assertTrue(ok, "valid customerType → true");
    }
}
