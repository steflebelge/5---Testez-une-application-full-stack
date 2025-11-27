package com.openclassrooms.starterjwt.unit.payload.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageResponseTest {

    @Test
    void constructor_shouldSetMessage() {
        MessageResponse response = new MessageResponse("hello");

        assertEquals("hello", response.getMessage());
    }

    @Test
    void setter_shouldUpdateMessage() {
        MessageResponse response = new MessageResponse("initial");

        response.setMessage("updated");

        assertEquals("updated", response.getMessage());
    }

    @Test
    void getter_shouldReturnCurrentMessage() {
        MessageResponse response = new MessageResponse("test");

        String msg = response.getMessage();

        assertEquals("test", msg);
    }
}
