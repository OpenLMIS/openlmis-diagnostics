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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ConsulCommunicationService {
  private RestOperations restTemplate;
  private ConsulSettings consulSettings;

  @Autowired
  public ConsulCommunicationService(ConsulSettings consulSettings) {
    this(new RestTemplate(), consulSettings);
  }

  ConsulCommunicationService(RestOperations restTemplate, ConsulSettings consulSettings) {
    this.restTemplate = restTemplate;
    this.consulSettings = consulSettings;
  }

  /**
   * Retrieves health statuses for all services registered in consul.
   */
  public Set<ResponseEntity> getHealthStatuses() {
    return getAvailableServices()
        .parallelStream()
        .map(this::getHealthStatus)
        .collect(Collectors.toSet());
  }

  private ResponseEntity getHealthStatus(String service) {
    return restTemplate.getForEntity(
        consulSettings.getHealthUrl(service), Object.class
    );
  }

  private Set<String> getAvailableServices() {
    ResponseEntity<ServicesListDto> response = restTemplate.getForEntity(
        consulSettings.getServicesUrl(),
        ServicesListDto.class
    );

    ServicesListDto services = response.getBody();
    String serviceTag = consulSettings.getServiceTag();

    return services
        .keySet()
        .stream()
        .filter(service -> services.isTagged(service, serviceTag))
        .collect(Collectors.toSet());
  }

}
