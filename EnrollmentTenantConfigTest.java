package com.citi.enroll.biometrics.config;

import com.citi.enroll.biometrics.exception.NoTenantException;
import com.citi.enroll.biometrics.util.BiometricsConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
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
    // postConstruct() → validateConfig(mfaConfig)
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
        mfa.setEventType("  ");                // blank
        mfa.setDigitalApplicationType(Collections.singletonMap("app","type"));
        mfa.setDataBasedOnEnrollmentType(
            Collections.singletonMap(
                BiometricsConstants.EnrollmentType.FEDERATED_ENROLL,
                new MfaConfig.DataBasedOnEnrollmentType()
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
        mfa.setDigitalApplicationType(Collections.emptyMap());   // empty → invalid
        mfa.setDataBasedOnEnrollmentType(
            Collections.singletonMap(
                BiometricsConstants.EnrollmentType.FEDERATED_ENROLL,
                new MfaConfig.DataBasedOnEnrollmentType()
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
        mfa.setDigitalApplicationType(Collections.singletonMap("app","type"));
        mfa.setDataBasedOnEnrollmentType(null);                  // null → invalid
        cfg.setMfaConfig(Collections.singletonMap("tenantC", mfa));

        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(cfg, "postConstruct"),
            "null dataBasedOnEnrollmentType must trigger NoTenantException");
    }

    @Test
    void postConstruct_whenDataBasedOnEnrollmentTypeHasNullValue_shouldThrow() {
        MfaConfig.DataBasedOnEnrollmentType dob = null;
        MfaConfig mfa = new MfaConfig();
        mfa.setEventType("evt");
        mfa.setDigitalApplicationType(Collections.singletonMap("app","type"));
        mfa.setDataBasedOnEnrollmentType(
            Collections.singletonMap(
                BiometricsConstants.EnrollmentType.FEDERATED_ENROLL,
                dob
            )
        );
        cfg.setMfaConfig(Collections.singletonMap("tenantD", mfa));

        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(cfg, "postConstruct"),
            "entry with null DataBasedOnEnrollmentType should trigger NoTenantException");
    }

    @Test
    void postConstruct_whenClientDefinedEventTypeNull_shouldThrow() {
        MfaConfig.DataBasedOnEnrollmentType dob = new MfaConfig.DataBasedOnEnrollmentType();
        dob.setClientDefinedEventType(null);                     // null → invalid
        MfaConfig mfa = new MfaConfig();
        mfa.setEventType("evt");
        mfa.setDigitalApplicationType(Collections.singletonMap("app","type"));
        mfa.setDataBasedOnEnrollmentType(
            Collections.singletonMap(
                BiometricsConstants.EnrollmentType.FEDERATED_ENROLL,
                dob
            )
        );
        cfg.setMfaConfig(Collections.singletonMap("tenantE", mfa));

        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(cfg, "postConstruct"),
            "null clientDefinedEventType should trigger NoTenantException");
    }

    @Test
    void postConstruct_whenClientDefinedEventTypeContainsBlankValue_shouldThrow() {
        MfaConfig.DataBasedOnEnrollmentType dob = new MfaConfig.DataBasedOnEnrollmentType();
        dob.setClientDefinedEventType(
            Collections.singletonMap(BiometricsConstants.CustomerType.INDIVIDUAL, "")
        );
        MfaConfig mfa = new MfaConfig();
        mfa.setEventType("evt");
        mfa.setDigitalApplicationType(Collections.singletonMap("app","type"));
        mfa.setDataBasedOnEnrollmentType(
            Collections.singletonMap(
                BiometricsConstants.EnrollmentType.FEDERATED_ENROLL,
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
        MfaConfig.DataBasedOnEnrollmentType dob = new MfaConfig.DataBasedOnEnrollmentType();
        // valid clientDefinedEventType
        dob.setClientDefinedEventType(
            Collections.singletonMap(BiometricsConstants.CustomerType.INDIVIDUAL, "evtA")
        );

        MfaConfig mfa = new MfaConfig();
        mfa.setEventType("evt");
        mfa.setDigitalApplicationType(Collections.singletonMap("app","type"));
        mfa.setDataBasedOnEnrollmentType(
            Collections.singletonMap(
                BiometricsConstants.EnrollmentType.FEDERATED_ENROLL,
                dob
            )
        );

        cfg.setMfaConfig(Collections.singletonMap("tenantOK", mfa));
        // should complete without exception
        ReflectionTestUtils.invokeMethod(cfg, "postConstruct");
    }

    //
    // Now directly test each private/package‐private validator in isolation
    //

    @Test
    void validateEventType_blank_throws() {
        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(
                cfg, "validateEventType", "X", ""
            ),
            "blank eventType must throw");
    }

    @Test
    void validateEventType_nonBlank_passes() {
        ReflectionTestUtils.invokeMethod(
            cfg, "validateEventType", "X", "something"
        );
    }

    @Test
    void validateDigitalApplicationType_emptyMap_throws() {
        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(
                cfg, "validateDigitalApplicationType", "T", Collections.emptyMap()
            ),
            "empty digitalApplicationType must throw");
    }

    @Test
    void validateDigitalApplicationType_validMap_passes() {
        Map<String,String> good = Collections.singletonMap("id","val");
        ReflectionTestUtils.invokeMethod(
            cfg, "validateDigitalApplicationType", "T", good
        );
    }

    @Test
    void validateClientDefinedEventType_nullMap_throws() {
        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(
                cfg, "validateClientDefinedEventType", "T", null
            ),
            "null clientDefinedEventType must throw");
    }

    @Test
    void validateClientDefinedEventType_blankValue_throws() {
        Map<BiometricsConstants.CustomerType,String> bad =
            Collections.singletonMap(BiometricsConstants.CustomerType.INDIVIDUAL, "");
        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(
                cfg, "validateClientDefinedEventType", "T", bad
            ),
            "blank clientDefinedEventType value must throw");
    }

    @Test
    void validateClientDefinedEventType_validMap_passes() {
        Map<BiometricsConstants.CustomerType,String> good =
            Collections.singletonMap(BiometricsConstants.CustomerType.INDIVIDUAL, "foo");
        ReflectionTestUtils.invokeMethod(
            cfg, "validateClientDefinedEventType", "T", good
        );
    }

    @Test
    void validateDataBasedOnEnrollmentType_nullMap_throws() {
        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(
                cfg, "validateDataBasedOnEnrollmentType",
                "T", null
            ),
            "null dataBasedOnEnrollmentType must throw");
    }

    @Test
    void validateDataBasedOnEnrollmentType_emptyMap_passes() {
        ReflectionTestUtils.invokeMethod(
            cfg, "validateDataBasedOnEnrollmentType",
            "T", Collections.emptyMap()
        );
    }

    @Test
    void validateDataBasedOnEnrollmentType_nullValue_throws() {
        Map<BiometricsConstants.EnrollmentType, MfaConfig.DataBasedOnEnrollmentType> bad =
            Collections.singletonMap(BiometricsConstants.EnrollmentType.FEDERATED_ENROLL, null);
        assertThrows(NoTenantException.class,
            () -> ReflectionTestUtils.invokeMethod(
                cfg, "validateDataBasedOnEnrollmentType", "T", bad
            ),
            "null DataBasedOnEnrollmentType entry must throw");
    }

    @Test
    void validateDataBasedOnEnrollmentType_valid_passes() {
        MfaConfig.DataBasedOnEnrollmentType dob = new MfaConfig.DataBasedOnEnrollmentType();
        dob.setClientDefinedEventType(
            Collections.singletonMap(BiometricsConstants.CustomerType.INDIVIDUAL, "evt")
        );
        Map<BiometricsConstants.EnrollmentType, MfaConfig.DataBasedOnEnrollmentType> good =
            Collections.singletonMap(BiometricsConstants.EnrollmentType.FEDERATED_ENROLL, dob);
        ReflectionTestUtils.invokeMethod(
            cfg, "validateDataBasedOnEnrollmentType", "T", good
        );
    }

    @Test
    void validateCustomerType_null_returnsFalse() {
        boolean ok = (boolean) ReflectionTestUtils.invokeMethod(
            cfg, "validateCustomerType", (Object) null
        );
        assertFalse(ok, "null customerType → false");
    }

    @Test
    void validateCustomerType_valid_returnsTrue() {
        boolean ok = (boolean) ReflectionTestUtils.invokeMethod(
            cfg, "validateCustomerType",
            BiometricsConstants.CustomerType.INDIVIDUAL
        );
        assertTrue(ok, "valid customerType → true");
    }
}
