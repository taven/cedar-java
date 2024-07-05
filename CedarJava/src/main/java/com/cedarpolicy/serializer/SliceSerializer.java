/*
 * Copyright 2022-2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cedarpolicy.serializer;

import com.cedarpolicy.model.slice.Slice;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/** Serialize a slice. Only used internally by CedarJson */
public class SliceSerializer extends JsonSerializer<Slice> {

    /** Serialize a slice. */
    @Override
    public void serialize(
            Slice slice, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("policies", slice.getPolicies());
        jsonGenerator.writeObjectField("entities", slice.getEntities());
        jsonGenerator.writeObjectField("templates", slice.getTemplates());
        jsonGenerator.writeObjectField(
                "templateInstantiations", slice.getTemplateLinks());
        jsonGenerator.writeEndObject();
    }
}