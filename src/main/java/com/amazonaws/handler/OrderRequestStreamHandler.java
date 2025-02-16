/*
 * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.handler;

import com.amazonaws.model.response.ErrorMessage;
import com.amazonaws.model.response.GatewayResponse;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Collections;
import java.util.Map;

public interface NextPermRequestStreamHandler extends RequestStreamHandler {
    int SC_OK = 200;
    int SC_BAD_REQUEST = 400;
    Map<String, String> APPLICATION_JSON = Collections.singletonMap("Content-Type",
            "application/json");
    ErrorMessage REQUEST_WAS_INVALID_ERROR
            = new ErrorMessage("Request was invalid", SC_BAD_REQUEST);

    /**
     * This method writes a body has invalid JSON response.
     * @param objectMapper the mappeter to use for converting the error response to JSON.
     * @param output the output stream to write with the mapper.
     * @param details a detailed message describing why the JSON was invalid.
     * @throws IOException if there was an issue converting the ErrorMessage object to JSON.
     */
    default void writeInvalidJsonInStreamResponse(ObjectMapper objectMapper,
                                                  OutputStream output,
                                                  String details) throws IOException {
        objectMapper.writeValue(output, new GatewayResponse<>(
                objectMapper.writeValueAsString(new ErrorMessage("Invalid input in request: "
                        + details, SC_BAD_REQUEST)),
                APPLICATION_JSON, SC_BAD_REQUEST));
    }

    default boolean isNullOrEmpty(final String string) {
        return string == null || string.isEmpty();
    }
}
