package com.citi.enroll.biometrics.ids;

import com.citi.enroll.biometrics.pojo.accountprofileservice.AccountProfileResponse;
import com.citi.enroll.biometrics.pojo.accountprofileservice.BankAccount;
import com.citi.enroll.biometrics.pojo.accountprofileservice.CreditCardAccount;
import com.citi.enroll.biometrics.pojo.accountprofileservice.LineAndLoanAccount;
import com.citi.enroll.biometrics.pojo.accountprofileservice.RetirementAccount;
import com.citi.enroll.biometrics.pojo.accountprofileservice.BrokerageAccount;
import com.citi.enroll.biometrics.pojo.client.InterdictionStatusResponse;
import com.citi.enroll.biometrics.pojo.client.InterdictionStatusResponse.TmxResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BiometricsIdsLoggerTest2 {

    private BiometricsIdsLogger logger;

    @BeforeEach
    void setUp() {
        logger = new BiometricsIdsLogger();
    }

    //
    // Tests for getAccountNumbers(...)
    //

    @Test
    void getAccountNumbers_nullResponse_returnsEmptyString() {
        String result = ReflectionTestUtils.invokeMethod(
            logger, "getAccountNumbers", (AccountProfileResponse) null);
        assertEquals("", result, "Null response should yield empty string");
    }

    @Test
    void getAccountNumbers_bankAccounts_present_returnsCommaSeparated() {
        AccountProfileResponse resp = mock(AccountProfileResponse.class);
        BankAccount ba = mock(BankAccount.class);
        when(ba.getAccountNumber()).thenReturn("111");
        when(resp.getBankAccounts()).thenReturn(Arrays.asList(ba));

        String result = ReflectionTestUtils.invokeMethod(
            logger, "getAccountNumbers", resp);

        assertEquals("111,", result,
            "When bankAccounts is non-empty, should return their numbers with commas");
    }

    @Test
    void getAccountNumbers_creditAccounts_when_bankEmpty() {
        AccountProfileResponse resp = mock(AccountProfileResponse.class);
        when(resp.getBankAccounts()).thenReturn(Collections.emptyList());
        CreditCardAccount ca = mock(CreditCardAccount.class);
        when(ca.getAccountNumber()).thenReturn("222");
        when(resp.getCreditCardAccounts()).thenReturn(Arrays.asList(ca));

        String result = ReflectionTestUtils.invokeMethod(
            logger, "getAccountNumbers", resp);

        assertEquals("222,", result,
            "If bankAccounts empty but creditCardAccounts non-empty, return credit numbers");
    }

    @Test
    void getAccountNumbers_lineAndLoan_when_previousEmpty() {
        AccountProfileResponse resp = mock(AccountProfileResponse.class);
        when(resp.getBankAccounts()).thenReturn(Collections.emptyList());
        when(resp.getCreditCardAccounts()).thenReturn(Collections.emptyList());
        LineAndLoanAccount la = mock(LineAndLoanAccount.class);
        when(la.getAccountNumber()).thenReturn("333");
        when(resp.getLinesAndLoanAccounts()).thenReturn(Arrays.asList(la));

        String result = ReflectionTestUtils.invokeMethod(
            logger, "getAccountNumbers", resp);

        assertEquals("333,", result);
    }

    @Test
    void getAccountNumbers_retirement_when_previousEmpty() {
        AccountProfileResponse resp = mock(AccountProfileResponse.class);
        when(resp.getBankAccounts()).thenReturn(Collections.emptyList());
        when(resp.getCreditCardAccounts()).thenReturn(Collections.emptyList());
        when(resp.getLinesAndLoanAccounts()).thenReturn(Collections.emptyList());
        RetirementAccount ra = mock(RetirementAccount.class);
        when(ra.getAccountNumber()).thenReturn("444");
        when(resp.getRetirementAccounts()).thenReturn(Arrays.asList(ra));

        String result = ReflectionTestUtils.invokeMethod(
            logger, "getAccountNumbers", resp);

        assertEquals("444,", result);
    }

    @Test
    void getAccountNumbers_brokerage_when_previousEmpty() {
        AccountProfileResponse resp = mock(AccountProfileResponse.class);
        when(resp.getBankAccounts()).thenReturn(Collections.emptyList());
        when(resp.getCreditCardAccounts()).thenReturn(Collections.emptyList());
        when(resp.getLinesAndLoanAccounts()).thenReturn(Collections.emptyList());
        when(resp.getRetirementAccounts()).thenReturn(Collections.emptyList());
        BrokerageAccount bc = mock(BrokerageAccount.class);
        when(bc.getAccountNumber()).thenReturn("555");
        when(resp.getBrokerageAccounts()).thenReturn(Arrays.asList(bc));

        String result = ReflectionTestUtils.invokeMethod(
            logger, "getAccountNumbers", resp);

        assertEquals("555,", result);
    }

    @Test
    void getAccountNumbers_allEmpty_returnsEmptyString() {
        AccountProfileResponse resp = mock(AccountProfileResponse.class);
        when(resp.getBankAccounts()).thenReturn(Collections.emptyList());
        when(resp.getCreditCardAccounts()).thenReturn(Collections.emptyList());
        when(resp.getLinesAndLoanAccounts()).thenReturn(Collections.emptyList());
        when(resp.getRetirementAccounts()).thenReturn(Collections.emptyList());
        when(resp.getBrokerageAccounts()).thenReturn(Collections.emptyList());

        String result = ReflectionTestUtils.invokeMethod(
            logger, "getAccountNumbers", resp);

        assertEquals("", result,
            "If all account lists are empty, should return empty string");
    }

    //
    // Tests for getFuzzyDeviceId(...)
    //

    @Test
    void getFuzzyDeviceId_present_returnsValue() {
        InterdictionStatusResponse resp = mock(InterdictionStatusResponse.class);
        TmxResponse tmx = mock(TmxResponse.class);
        when(tmx.getFuzzyDeviceId()).thenReturn("FDID");
        when(resp.getTmxResponse()).thenReturn(tmx);

        String result = ReflectionTestUtils.invokeMethod(
            logger, "getFuzzyDeviceId", resp);

        assertEquals("FDID", result,
            "When TMX response has a fuzzyDeviceId, it should be returned");
    }

    @Test
    void getFuzzyDeviceId_nullValue_returnsNull() {
        InterdictionStatusResponse resp = mock(InterdictionStatusResponse.class);
        TmxResponse tmx = mock(TmxResponse.class);
        when(tmx.getFuzzyDeviceId()).thenReturn(null);
        when(resp.getTmxResponse()).thenReturn(tmx);

        String result = ReflectionTestUtils.invokeMethod(
            logger, "getFuzzyDeviceId", resp);

        assertNull(result,
            "If fuzzyDeviceId is null, method should return null");
    }

    @Test
    void getFuzzyDeviceId_tmxResponseNull_returnsNull() {
        InterdictionStatusResponse resp = mock(InterdictionStatusResponse.class);
        when(resp.getTmxResponse()).thenReturn(null);

        String result = ReflectionTestUtils.invokeMethod(
            logger, "getFuzzyDeviceId", resp);

        assertNull(result,
            "If TMX response itself is null, method should return null");
    }

    @Test
    void getFuzzyDeviceId_exceptionInTmxResponse_returnsNull() {
        InterdictionStatusResponse resp = mock(InterdictionStatusResponse.class);
        when(resp.getTmxResponse()).thenThrow(new RuntimeException("fail"));

        String result = ReflectionTestUtils.invokeMethod(
            logger, "getFuzzyDeviceId", resp);

        assertNull(result,
            "If accessing TMX response throws, method should catch and return null");
    }
}
