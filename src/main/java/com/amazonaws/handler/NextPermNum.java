package com.amazonaws.handler;

import com.amazonaws.exception.InputNotInRangeException;
import com.amazonaws.model.response.ErrorMessage;
import com.amazonaws.model.response.GatewayResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Arrays;
import javax.inject.Inject;

public class GetNextPermNumHandler implements NextPermRequestStreamHandler {
    @Inject
    ObjectMapper objectMapper;

    public static final int MAX_INPUT_VAL = 9223372036854775807;
    public static final int MIN_INPUT_VAL = 0;

    public static int[] swap(int data[], int left, int right)
    {
  
        int temp = data[left];
        data[left] = data[right];
        data[right] = temp;
  
        return data;
    }

    public static int[] reverse(int data[], int left, int right)
    {
  
        while (left < right) {
            int temp = data[left];
            data[left++] = data[right];
            data[right--] = temp;
        }
  
        return data;
    }

    public static boolean findNextPermutation(int data[])
    {
  
        if (data.length <= 1)
            return false;
  
        int last = data.length - 2;
  
        while (last >= 0) {
            if (data[last] < data[last + 1]) {
                break;
            }
            last--;
        }
  
        if (last < 0)
            return false;
  
        int nextGreater = data.length - 1;
  
        for (int i = data.length - 1; i > last; i--) {
            if (data[i] > data[last]) {
                nextGreater = i;
                break;
            }
        }
  
        data = swap(data, nextGreater, last);
        data = reverse(data, last + 1, data.length - 1);
  
        return true;
    }

    public int[] convertStringToArrayNum(String input_num) {
      int[] num = new int[input_num.length()];

      for (int i = 0; i < input_num.length(); i++) {
            ch[i] = input_num.charAt(i) - '0';
      }
      return num
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output,
                              Context context) throws IOException {
        final JsonNode event;
        try {
            event = objectMapper.readTree(input);
        } catch (JsonMappingException e) {
            writeInvalidJsonInStreamResponse(objectMapper, output, e.getMessage());
            return;
        }
        if (event == null) {
            writeInvalidJsonInStreamResponse(objectMapper, output, "event was null");
            return;
        }
        final JsonNode pathParameterMap = event.findValue("pathParameters");
        final String input_num = Optional.ofNullable(pathParameterMap)
                .map(mapNode -> mapNode.get("input_num"))
                .map(JsonNode::asText)
                .orElse(null);
        if (isNullOrEmpty(input_num)) {
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString(INPUT_NUM_WAS_NOT_SET),
                            APPLICATION_JSON, SC_BAD_REQUEST));
            return;
        }
        try {
          input_num = Integer.parseInt(input_num)
        } catch (NumberFormatException e) {
          writeInvalidJsonInStreamResponse(objectMapper, output, "Invalid Input Number");
          return;
        }
        if (input_num < MIN_INPUT_VAL || input_num > MAX_INPUT_VAL) {
          throw new InputNotInRangeException(input_num + " is not in range " + MIN_INPUT_VAL + " - " + MAX_INPUT_VAL);
        }
        try {
            input_num_array = convertStringToArrayNum(input_num)
            next_perm_exists = findNextPermutation(input_num_array)
            if next_perm_exists {
              objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString(Arrays.toString(input_num_array)),
                            APPLICATION_JSON, SC_OK));
            } else {
              objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString("No Answer"),
                            APPLICATION_JSON, SC_OK));
            }
        } catch (InputNotInRangeException e) {
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString(
                                    new ErrorMessage(e.getMessage(), SC_BAD_REQUEST)),
                            APPLICATION_JSON, SC_BAD_REQUEST));
        }
    }
}
