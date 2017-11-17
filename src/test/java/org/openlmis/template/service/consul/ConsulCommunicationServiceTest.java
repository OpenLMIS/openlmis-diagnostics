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

package org.openlmis.template.service.consul;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    List<String> validServices = Arrays.asList("auth", "requisition");
    List<String> invalidServices = Collections.singletonList("random-service");
    ServicesListDto expectedBody = generateServicesList(validServices, invalidServices);
    mockExternalResponse(expectedBody);
    mockHealthResponse("auth", new Object());
    mockHealthResponse("requisition", new Object());

    // when
    Set<ResponseEntity> statuses = consulCommunicationService.getHealthStatuses();

    // then
    assertThat(statuses, hasSize(2));
  }

  private void mockExternalResponse(ServicesListDto body) {
    String expectedUrl = consulSettings.getServicesUrl();

    ResponseEntity<ServicesListDto> expectedResponse = mock(ResponseEntity.class);
    given(expectedResponse.getBody()).willReturn(body);

    given(restTemplate.getForEntity(expectedUrl, ServicesListDto.class))
        .willReturn(expectedResponse);
  }

  private void mockHealthResponse(String service, Object body) {
    String expectedUrl = consulSettings.getHealthUrl(service);

    ResponseEntity expectedResponse = mock(ResponseEntity.class);
    given(expectedResponse.getBody()).willReturn(body);

    given(restTemplate.getForEntity(expectedUrl, Object.class))
        .willReturn(expectedResponse);
  }

  private ServicesListDto generateServicesList(List<String> valid, List<String> invalid) {
    ServicesListDto services = new ServicesListDto();
    String serviceTag = consulSettings.getServiceTag();

    for (String service : valid) {
      services.put(service, Collections.singletonList(serviceTag));
    }

    for (String service : invalid) {
      services.put(service, Collections.emptyList());
    }

    return services;
  }

}
