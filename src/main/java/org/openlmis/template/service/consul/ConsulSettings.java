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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
@AllArgsConstructor
class ConsulSettings {

  @Value("${consul.protocol}")
  private String protocol;

  @Value("${consul.host}")
  private String host;

  @Value("${consul.port}")
  private String port;

  @Value("${consul.services.url}")
  private String servicesUrl;

  @Getter
  @Value("${consul.services.serviceTag}")
  private String serviceTag;

  @Value("${consul.health.url}")
  private String healthUrl;

  String getServicesUrl() {
    return getUrl(servicesUrl);
  }

  String getHealthUrl(String service) {
    return getUrl(healthUrl + service);
  }

  private String getUrl(String path) {
    return String.format("%s://%s:%d%s", protocol, host, Integer.valueOf(port), path);
  }
}
