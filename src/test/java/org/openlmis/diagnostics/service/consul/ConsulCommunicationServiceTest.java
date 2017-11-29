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
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.openlmis.diagnostics.service.consul.ConsulHelper.REFERENCEDATA;
import static org.openlmis.diagnostics.service.consul.ConsulHelper.generateCatalog;
import static org.openlmis.diagnostics.service.consul.ConsulHelper.generateEmptyHealthService;
import static org.openlmis.diagnostics.service.consul.ConsulHelper.generateEmptyHealthServiceResponse;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.health.model.HealthService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

public class ConsulCommunicationServiceTest {
  private ConsulSettings consulSettings;
  private ConsulClient consulClient;
  private ConsulCommunicationService consulCommunicationService;

  @Before
  public void setUp() {
    // given
    consulSettings = new ConsulSettingsDataBuilder().build();

    consulClient = mock(ConsulClient.class);
    given(consulClient.getCatalogServices(QueryParams.DEFAULT))
        .willReturn(generateCatalog(consulSettings.getServiceTag()));

    consulCommunicationService = new ConsulCommunicationService();
    consulCommunicationService.setConsulSettings(consulSettings);
    consulCommunicationService.setClient(consulClient);
    consulCommunicationService.afterPropertiesSet();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionIfConsulSettingsIsNull() throws Exception {
    testAfterPropertiesSet(null);
  }

  @Test
  public void shouldSetConsulClient() throws Exception {
    testAfterPropertiesSet(consulSettings);
  }

  @Test
  public void shouldReturnEmptyListIfConsulResponseIsEmpty() throws Exception {
    testEmptySystemHealth(generateEmptyHealthServiceResponse());
  }

  @Test
  public void shouldReturnEmptyListIfThereIsNoChecks() throws Exception {
    testEmptySystemHealth(generateEmptyHealthService());
  }

  @Test
  public void shouldGetHealthStatuses() {
    testSystemHealth(ConsulHelper.generateHealthService());
  }

  private void testAfterPropertiesSet(ConsulSettings consulSettings) {
    consulCommunicationService.setConsulSettings(consulSettings);
    consulCommunicationService.setClient(null);
    consulCommunicationService.afterPropertiesSet();

    // we only check if field is set we don't use value to take any additional actions
    Field field = ReflectionUtils.findField(ConsulCommunicationService.class, "client");
    field.setAccessible(true);

    Object client = ReflectionUtils.getField(field, consulCommunicationService);

    assertThat(client, is(notNullValue()));
    assertThat(client, is(instanceOf(ConsulClient.class)));
    assertThat(client, is(not(consulClient)));
  }

  private void testSystemHealth(Response<List<HealthService>> response) {
    // when
    mockConsulHealthServices(response);

    SystemHealth system = consulCommunicationService.getSystemHealth();

    // then
    assertThat(system.getDetails(), hasSize(1));

    HealthDetails healthDetails = system.getDetails().iterator().next();

    assertThat(healthDetails, hasProperty("serviceName", equalTo(REFERENCEDATA)));
    assertThat(healthDetails, hasProperty("status", equalTo(HealthState.PASSING)));
  }

  private void testEmptySystemHealth(Response<List<HealthService>> response) {
    // when
    mockConsulHealthServices(response);

    SystemHealth system = consulCommunicationService.getSystemHealth();

    // then
    assertThat(system.getDetails(), hasSize(0));
  }

  private void mockConsulHealthServices(Response<List<HealthService>> response) {
    given(consulClient.getHealthServices(REFERENCEDATA, false, QueryParams.DEFAULT))
        .willReturn(response);
  }

}
