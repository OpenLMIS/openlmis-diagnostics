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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.google.common.collect.Maps;

import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.health.model.Check;
import com.ecwid.consul.v1.health.model.HealthService;

import org.assertj.core.util.Lists;

import java.util.List;
import java.util.Map;

final class ConsulHelper {
  static final String REFERENCEDATA = "referencedata";

  private ConsulHelper() {
    throw new UnsupportedOperationException();
  }

  static Response<List<HealthService>> generateHealthService() {
    Check check = new Check();
    check.setServiceName(REFERENCEDATA);
    check.setStatus(Check.CheckStatus.PASSING);

    HealthService healthService = new HealthService();
    healthService.setChecks(Lists.newArrayList(check));

    List<HealthService> healthServices = Lists.newArrayList(healthService);

    return new Response<>(healthServices, null, null, null);
  }

  static Response<List<HealthService>> generateEmptyHealthService() {
    return new Response<>(singletonList(new HealthService()), null, null, null);
  }

  static Response<List<HealthService>> generateEmptyHealthServiceResponse() {
    return new Response<>(emptyList(), null, null, null);
  }

  static Response<Map<String, List<String>>> generateCatalog(String serviceTag) {
    Map<String, List<String>> map = Maps.newHashMap();
    map.put(REFERENCEDATA, Lists.newArrayList(serviceTag));
    map.put("random-service", Lists.newArrayList());

    return new Response<>(map, null, null, null);
  }

}
