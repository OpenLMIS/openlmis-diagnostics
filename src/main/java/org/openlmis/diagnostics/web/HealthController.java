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

import org.openlmis.diagnostics.service.consul.ConsulCommunicationService;
import org.openlmis.diagnostics.service.consul.HealthDetails;
import org.openlmis.diagnostics.service.consul.SystemHealth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class HealthController {

  @Autowired
  private ConsulCommunicationService consulService;

  @GetMapping(name = "/api/health")
  public ResponseEntity<Set<HealthDetails>> getHealth() {
    SystemHealth health = consulService.getSystemHealth();
    return ResponseEntity.status(health.getStatusCode()).body(health.getDetails());
  }

}
