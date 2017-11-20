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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Maps;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.health.model.Check;
import com.ecwid.consul.v1.health.model.HealthService;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ConsulCommunicationServiceTest {
  private static final String REFERENCEDATA = "referencedata";
  private static final String REQUISITION = "requisition";

  private ConsulSettings consulSettings;
  private ConsulClient consulClient;
  private ConsulCommunicationService consulCommunicationService;

  @Before
  public void setUp() {
    consulSettings = new ConsulSettingsDataBuilder().build();
    consulClient = mock(ConsulClient.class);

    consulCommunicationService = new ConsulCommunicationService(consulSettings, consulClient);
  }

  @Test
  public void shouldGetHealthStatuses() {
    // given
    Response<Map<String, List<String>>> catalog = generateCatalog();
    Response<List<HealthService>> healthService = generateHealthService();
    Response<List<HealthService>> emptyHealthService = generateEmptyHealthService();

    // when
    when(consulClient.getCatalogServices(QueryParams.DEFAULT))
        .thenReturn(catalog);
    when(consulClient.getHealthServices(REFERENCEDATA, false, QueryParams.DEFAULT))
        .thenReturn(healthService);
    when(consulClient.getHealthServices(REQUISITION, false, QueryParams.DEFAULT))
        .thenReturn(emptyHealthService);

    SystemHealth system = consulCommunicationService.getSystemHealth();

    // then
    assertThat(system.getDetails(), hasSize(1));

    HealthDetails healthDetails = system.getDetails().iterator().next();

    assertThat(healthDetails, hasProperty("serviceName", equalTo(REFERENCEDATA)));
    assertThat(healthDetails, hasProperty("status", equalTo(HealthState.PASSING)));
  }

  private Response<List<HealthService>> generateEmptyHealthService() {
    return new Response<>(Lists.newArrayList(new HealthService()), null, null, null);
  }

  private Response<List<HealthService>> generateHealthService() {
    Check check = new Check();
    check.setServiceName(REFERENCEDATA);
    check.setStatus(Check.CheckStatus.PASSING);

    HealthService healthService = new HealthService();
    healthService.setChecks(Lists.newArrayList(check));

    List<HealthService> healthServices = Lists.newArrayList(healthService);

    return new Response<>(healthServices, null, null, null);
  }

  private Response<Map<String, List<String>>> generateCatalog() {
    Map<String, List<String>> map = Maps.newHashMap();
    map.put(REFERENCEDATA, Lists.newArrayList(consulSettings.getServiceTag()));
    map.put(REQUISITION, Lists.newArrayList(consulSettings.getServiceTag()));
    map.put("random-service", Lists.newArrayList());

    return new Response<>(map, null, null, null);
  }

}
