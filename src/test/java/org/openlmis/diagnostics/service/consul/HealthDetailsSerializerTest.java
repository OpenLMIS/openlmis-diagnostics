/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.diagnostics.service.consul;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HealthDetailsSerializerTest {
  private ObjectMapper mapper;

  @Before
  public void setUp() throws Exception {
    mapper = new ObjectMapper();
  }

  @Test
  public void shouldSerializeToServiceResponse() throws Exception {
    HealthDetails details = new HealthDetailsDataBuilder()
        .withRandomNotes()
        .withRandomOutput()
        .withValidServiceTag()
        .build();

    JsonNode json = mapper.valueToTree(details);

    assertThat(json, is(notNullValue()));
    assertSingleField(json, "node", details.getNode());
    assertSingleField(json, "checkId", details.getCheckId());
    assertSingleField(json, "name", details.getName());
    assertSingleField(json, "status", details.getStatus().toString());
    assertSingleField(json, "notes", details.getNotes());
    assertSingleField(json, "output", details.getOutput());
    assertSingleField(json, "serviceId", details.getServiceId());
    assertSingleField(json, "serviceName", details.getServiceName());
    assertListField(json, "serviceTags", details.getServiceTags());
  }

  private void assertListField(JsonNode json, String field, List<String> values) {
    assertThat(json.has(field), is(true));
    assertThat(json.get(field).isArray(), is(true));

    ArrayNode array = (ArrayNode) json.get(field);
    Set<String> arrayValues = StreamSupport
        .stream(array.spliterator(), false)
        .map(JsonNode::asText)
        .collect(Collectors.toSet());

    assertThat(arrayValues.size(), is(values.size()));
    assertThat(arrayValues, equalTo(new HashSet<>(values)));
  }

  private void assertSingleField(JsonNode json, String field, String value) {
    assertThat(json.has(field), is(true));
    assertThat(json.get(field).asText(), equalTo(value));
  }
}