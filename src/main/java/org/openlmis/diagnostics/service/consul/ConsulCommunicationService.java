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

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.health.model.Check;
import com.ecwid.consul.v1.health.model.HealthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ConsulCommunicationService {
  private ConsulSettings consulSettings;
  private ConsulClient client;

  @Autowired
  ConsulCommunicationService(ConsulSettings consulSettings) {
    this(consulSettings, new ConsulClient(consulSettings.getHost(), consulSettings.getPort()));
  }

  public ConsulCommunicationService(ConsulSettings consulSettings, ConsulClient client) {
    this.consulSettings = consulSettings;
    this.client = client;
  }

  /**
   * Retrieves health statuses for all services registered in consul.
   */
  public SystemHealth getSystemHealth() {
    Set<HealthDetails> details = getAvailableServices()
        .parallelStream()
        .map(this::getHealthStatus)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(HealthDetails::new)
        .collect(Collectors.toSet());

    return new SystemHealth(details);
  }

  private Optional<Check> getHealthStatus(String service) {
    List<HealthService> healthService = client
        .getHealthServices(service, false, QueryParams.DEFAULT)
        .getValue();

    if (null == healthService || healthService.isEmpty()) {
      return Optional.empty();
    }

    List<Check> checks = healthService.get(0).getChecks();

    if (null == checks || checks.isEmpty()) {
      return Optional.empty();
    }

    return checks
        .stream()
        .filter(check -> service.equalsIgnoreCase(check.getServiceName()))
        .findFirst();
  }

  private Set<String> getAvailableServices() {
    Map<String, List<String>> catalog = client.getCatalogServices(QueryParams.DEFAULT).getValue();
    String serviceTag = consulSettings.getServiceTag();

    return catalog
        .keySet()
        .stream()
        .filter(service -> catalog.get(service).contains(serviceTag))
        .collect(Collectors.toSet());
  }

}
