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

package org.openlmis.diagnostics.web;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.Test;
import org.openlmis.diagnostics.service.consul.ConsulCommunicationService;
import org.openlmis.diagnostics.service.consul.ConsulHealthResponseDataBuilder;
import org.openlmis.diagnostics.service.consul.HealthState;
import org.springframework.boot.test.mock.mockito.MockBean;

import guru.nidi.ramltester.junit.RamlMatchers;

import java.io.IOException;

public class HealthControllerIntegrationTest extends BaseWebIntegrationTest {
  private static final String RESOURCE_URL = "/api/health";

  @MockBean
  private ConsulCommunicationService consulService;

  @Test
  public void shouldReturnHealthDetailsWithOkStatus() throws IOException {
    given(consulService.getSystemHealth())
        .willReturn(new ConsulHealthResponseDataBuilder().withPassingEntity().build());

    sendHealthRequest(HealthState.PASSING);
  }

  @Test
  public void shouldReturnHealthDetailsWithTooManyRequestsStatus() throws IOException {
    given(consulService.getSystemHealth())
        .willReturn(new ConsulHealthResponseDataBuilder().withWarningEntity().build());

    sendHealthRequest(HealthState.WARNING);
  }

  @Test
  public void shouldReturnHealthDetailsWithServiceUnavailableStatus() throws IOException {
    given(consulService.getSystemHealth())
        .willReturn(new ConsulHealthResponseDataBuilder().withCriticalEntity().build());

    sendHealthRequest(HealthState.CRITICAL);
  }

  private void sendHealthRequest(HealthState healthStatus) throws IOException {
    String json = restAssured.given()
        .when()
        .get(RESOURCE_URL)
        .then()
        .statusCode(healthStatus.getHttpStatus().value())
        .extract()
        .asString();

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());

    // because of custom setters/getters we can't convert service json to HealthDetails
    JsonNode array = objectMapper.readTree(json);

    assertThat(array.isArray(), is(true));
    assertThat(array.size(), is(1));
    assertThat(array.get(0).get("status").asText(), equalTo(healthStatus.toString()));
  }

}
