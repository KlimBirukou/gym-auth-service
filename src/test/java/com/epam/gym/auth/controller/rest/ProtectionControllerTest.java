package com.epam.gym.auth.controller.rest;

import com.epam.gym.auth.controller.rest.dto.response.BruteForceStatusResponse;
import com.epam.gym.auth.facade.protection.IProtectionFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ProtectionControllerTest {

    private static final UUID USER_UID = UUID.randomUUID();
    private static final BruteForceStatusResponse STATUS_RESPONSE = BruteForceStatusResponse.blocked(15);

    @Mock
    private IProtectionFacade protectionFacade;

    @InjectMocks
    private ProtectionController testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(protectionFacade);
    }

    @Test
    void getStatus_shouldReturnStatus() {
        doReturn(STATUS_RESPONSE).when(protectionFacade).getStatus(USER_UID);

        var actual = testObject.getStatus(USER_UID);

        assertNotNull(actual);
        assertEquals(STATUS_RESPONSE, actual);
        verify(protectionFacade).getStatus(USER_UID);
    }

    @Test
    void recordFailedAttempt_shouldInvokeFacade() {
        doNothing().when(protectionFacade).recordFailedAttempt(USER_UID);

        testObject.recordFailedAttempt(USER_UID);

        verify(protectionFacade).recordFailedAttempt(USER_UID);
    }
}
