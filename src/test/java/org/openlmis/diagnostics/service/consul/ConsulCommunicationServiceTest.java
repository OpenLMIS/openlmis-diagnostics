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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.openlmis.diagnostics.service.consul.ConsulSettingsDataBuilder.VALID_SERVICE_TAG;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ConsulCommunicationServiceTest {
  private ConsulSettings consulSettings;
  private RestTemplate restTemplate;
  private ConsulCommunicationService consulCommunicationService;

  @Before
  public void setUp() {
    consulSettings = new ConsulSettingsDataBuilder().build();
    restTemplate = mock(RestTemplate.class);

    consulCommunicationService = new ConsulCommunicationService(
        restTemplate, consulSettings
    );
  }

  @Test
  public void shouldGetHealthStatuses() {
    // given
    ConsulResponse<HealthDetails> expected = new ConsulHealthResponseDataBuilder()
        .withValidEntity()
        .build();
    HealthDetails[] body = expected.getEntities().toArray(new HealthDetails[0]);
    mockHealthResponse(HealthState.ANY, body);

    // when
    ConsulResponse<HealthDetails> response = consulCommunicationService.getHealthStatuses();

    // then
    assertThat(response.getEntities(), equalTo(expected.getEntities()));
    assertThat(response.getStatusCode(), equalTo(expected.getStatusCode()));
  }

  @Test
  public void shouldSkipStatusesWithInvalidServiceTags() {
    // given
    ConsulResponse<HealthDetails> expected = new ConsulHealthResponseDataBuilder()
        .withValidEntity()
        .withInvalidEntity()
        .build();
    HealthDetails[] body = expected.getEntities().toArray(new HealthDetails[0]);
    mockHealthResponse(HealthState.ANY, body);

    // when
    ConsulResponse<HealthDetails> response = consulCommunicationService.getHealthStatuses();

    // then
    assertThat(response.getEntities(), hasSize(1));
    assertThat(response.getEntities().get(0).getServiceTags(), hasItem(VALID_SERVICE_TAG));
    assertThat(response.getStatusCode(), equalTo(expected.getStatusCode()));
  }

  private void mockHealthResponse(HealthState state, HealthDetails[] body) {
    String expectedUrl = consulSettings.getHealthStateUrl(state);
    ResponseEntity<HealthDetails[]> expectedResponse = new ResponseEntity<>(body, HttpStatus.OK);

    given(restTemplate.getForEntity(expectedUrl, HealthDetails[].class))
        .willReturn(expectedResponse);
  }

}
