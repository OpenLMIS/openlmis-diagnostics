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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.diagnostics.service.consul.HealthState.PASSING;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HealthDetailsDeserializerTest {
  private ObjectMapper mapper;

  @Before
  public void setUp() throws Exception {
    mapper = new ObjectMapper();
  }

  @Test
  public void shouldDeserializeConsulResponse() throws Exception {
    String json = "{\n"
        + "    \"Node\": \"foobar\",\n"
        + "    \"CheckID\": \"serfHealth\",\n"
        + "    \"Name\": \"Serf Health Status\",\n"
        + "    \"Status\": \"passing\",\n"
        + "    \"Notes\": \"some notes\",\n"
        + "    \"Output\": \"some output\",\n"
        + "    \"ServiceID\": \"FooId\",\n"
        + "    \"ServiceName\": \"Bar service\",\n"
        + "    \"ServiceTags\": [\"primary\"]\n"
        + "  }";
    InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
    JsonParser parser = mapper.getFactory().createParser(stream);
    HealthDetails details = parser.readValueAs(HealthDetails.class);

    assertThat(details, is(notNullValue()));
    assertThat(details.getNode(), equalTo("foobar"));
    assertThat(details.getCheckId(), equalTo("serfHealth"));
    assertThat(details.getName(), equalTo("Serf Health Status"));
    assertThat(details.getStatus(), equalTo(PASSING));
    assertThat(details.getNotes(), equalTo("some notes"));
    assertThat(details.getOutput(), equalTo("some output"));
    assertThat(details.getServiceId(), equalTo("FooId"));
    assertThat(details.getServiceName(), equalTo("Bar service"));
    assertThat(details.getServiceTags(), hasSize(1));
    assertThat(details.getServiceTags().get(0), equalTo("primary"));
  }
}