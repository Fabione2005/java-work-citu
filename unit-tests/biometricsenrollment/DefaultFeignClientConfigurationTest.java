package com.citi.enroll.biometrics.client;

import com.citigroup.ccp.cloud.CloudContext;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;

import javax.net.ssl.SSLContext;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultFeignClientConfigurationTest {

    @InjectMocks
    private DefaultFeignClientConfiguration config;

    @Mock
    private CloudContext cloudContext;

    /**
     * Cubre el bloque:
     *
     * this.connectionManagerTimer.schedule(
     *     new TimerTask() { public void run() { connectionManager.closeExpired(); } },
     *     30000,
     *     httpClientProperties.getConnectionTimerRepeat());
     */
    @Test
    public void connectionManager_shouldScheduleTimerAndExecuteTask() throws Exception {
        // ---- Arrange ----
        // SSLContext válido para que no falle la creación del socket factory
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, new SecureRandom());
        when(cloudContext.getSslContext()).thenReturn(sslContext);

        // Timer de prueba que ejecuta el TimerTask inmediatamente
        TestTimer testTimer = new TestTimer();

        Field timerField =
                DefaultFeignClientConfiguration.class.getDeclaredField("connectionManagerTimer");
        timerField.setAccessible(true);
        timerField.set(config, testTimer);

        FeignHttpClientProperties props = new FeignHttpClientProperties();
        // No hace falta setear nada; los defaults sirven para el test

        // ---- Act ----
        HttpClientConnectionManager cm = config.connectionManager(props);

        // ---- Assert ----
        assertNotNull(cm);
        // Verificamos que el TimerTask asociado se ejecutó (y por tanto se llamó a run())
        assertTrue("TimerTask.run() should have been executed", testTimer.runCalled);
    }

    // Timer especial para el test: sobrescribe schedule() y ejecuta inmediatamente el TimerTask
    private static class TestTimer extends Timer {
        boolean runCalled = false;

        TestTimer() {
            super("test-timer", true);
        }

        @Override
        public void schedule(TimerTask task, long delay, long period) {
            runCalled = true;
            task.run(); // ejecuta el bloque: connectionManager.closeExpired();
        }
    }
}
