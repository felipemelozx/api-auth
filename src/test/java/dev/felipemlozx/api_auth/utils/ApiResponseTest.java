package dev.felipemlozx.api_auth.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void successWithDataShouldReturnSuccessTrueAndDefaultMessage() {
        String data = "data";
        ApiResponse<String> response = ApiResponse.success(data);
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(data, response.getData());
    }

    @Test
    void successWithMessageAndDataShouldReturnSuccessTrueAndCustomMessage() {
        String data = "data";
        String message = "Custom success";
        ApiResponse<String> response = ApiResponse.success(message, data);
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
    }

    @Test
    void errorWithMessageShouldReturnSuccessFalseAndNullData() {
        String message = "Error occurred";
        ApiResponse<String> response = ApiResponse.error(message);
        assertFalse(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void errorWithMessageAndDataShouldReturnSuccessFalseAndErrorData() {
        String message = "Error occurred";
        String errorData = "error details";
        ApiResponse<String> response = ApiResponse.error(message, errorData);
        assertFalse(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(errorData, response.getData());
    }

    @Test
    void errorWithDataShouldReturnSuccessFalseAndDefaultMessage() {
        List<String> errorData = List.of("error details");
        ApiResponse<List<String>> response = ApiResponse.error(errorData);
        assertFalse(response.isSuccess());
        assertEquals("Error", response.getMessage());
        assertEquals(errorData, response.getData());
    }

  @Test
  void errorWithDataShouldReturnSuccessFalse() {
    String errorData = "error details";
    ApiResponse<String> response = ApiResponse.error(errorData);
    assertFalse(response.isSuccess());
    assertEquals("error details", response.getMessage());
    assertNull(response.getData());
  }

    @Test
    void settersAndGettersShouldWorkCorrectly() {
        ApiResponse<Integer> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("msg");
        response.setData(123);

        assertTrue(response.isSuccess());
        assertEquals("msg", response.getMessage());
        assertEquals(123, response.getData());
    }

    @Test
    void constructorShouldSetAllFields() {
        ApiResponse<Double> response = new ApiResponse<>(true, "ok", 1.5);
        assertTrue(response.isSuccess());
        assertEquals("ok", response.getMessage());
        assertEquals(1.5, response.getData());
    }
}
