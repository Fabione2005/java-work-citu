import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class FuipUtilsTest {

    @InjectMocks
    private FuipUtils fuipUtils;

    @Before
    public void setUp() {
        // Configurar valores predeterminados en cada prueba
        ReflectionTestUtils.setField(fuipUtils, "pwdReuseEnabled", true);
        ReflectionTestUtils.setField(fuipUtils, "pwdReuseThrottlePercent", 10);
        ReflectionTestUtils.setField(fuipUtils, "pwdReuseWhitelistedCcsIds", new HashSet<>(Arrays.asList("123456")));
    }

    @Test
    public void testPwdReuseCheckEnabled_WhenCcsidIsWhitelisted() {
        boolean result = fuipUtils.isPwdReuseCheckEnabled("123456");
        assertTrue(result);
    }

    @Test
    public void testPwdReuseCheckEnabled_WhenCcsidIsNotWhitelisted_ButMeetsThrottlePercent() {
        boolean result = fuipUtils.isPwdReuseCheckEnabled("111118");
        assertTrue(result);
    }

    @Test
    public void testPwdReuseCheckEnabled_WhenCcsidIsNotWhitelisted_AndDoesNotMeetThrottlePercent() {
        boolean result = fuipUtils.isPwdReuseCheckEnabled("111110");
        assertFalse(result);
    }

    @Test
    public void testPwdReuseCheckEnabled_WhenCcsidIsBlank() {
        boolean result = fuipUtils.isPwdReuseCheckEnabled("");
        assertFalse(result);
    }

    @Test
    public void testPwdReuseCheckEnabled_WhenPwdReuseIsDisabled() {
        ReflectionTestUtils.setField(fuipUtils, "pwdReuseEnabled", false);
        boolean result = fuipUtils.isPwdReuseCheckEnabled("123456");
        assertFalse(result);
    }

    @Test
    public void testPwdReuseCheckEnabled_WhenExceptionOccurs() {
        ReflectionTestUtils.setField(fuipUtils, "pwdReuseThrottlePercent", "invalid");
        boolean result = fuipUtils.isPwdReuseCheckEnabled("111118");
        assertFalse(result);
    }
}
