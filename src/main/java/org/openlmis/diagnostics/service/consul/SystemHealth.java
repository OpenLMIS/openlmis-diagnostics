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

import static org.openlmis.diagnostics.service.consul.HealthState.CRITICAL;
import static org.openlmis.diagnostics.service.consul.HealthState.PASSING;
import static org.openlmis.diagnostics.service.consul.HealthState.WARNING;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class SystemHealth {

  @Getter
  private Set<HealthDetails> details;

  /**
   * Returns http status. The status code depends on {@link HealthDetails#status} field. If any
   * entity has WARNING status, the 429 HTTP status will be returned. If any entity has CRITICAL
   * status, the 503 HTTP status will be returned. If all entities has PASSING status, the 200 HTTP
   * code will be returned.
   */
  public HttpStatus getStatusCode() {
    if (details.stream().anyMatch(HealthDetails::hasCriticalStatus)) {
      return CRITICAL.getHttpStatus();
    }

    if (details.stream().anyMatch(HealthDetails::hasWarningStatus)) {
      return WARNING.getHttpStatus();
    }

    return PASSING.getHttpStatus();
  }
}
