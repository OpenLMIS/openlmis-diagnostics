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

import java.util.List;

public class ConsulHealthResponse extends ConsulResponse<HealthDetails> {

  ConsulHealthResponse(List<HealthDetails> entities, HttpStatus statusCode) {
    super(entities, statusCode);
  }

  /**
   * Returns status code of consul health response. If response contains HTTP status different than
   * 200, it will be immediately return it. Otherwise the return value depends on {@link
   * HealthDetails#status} field. If any entity has {@link HealthState#WARNING}, the 429 HTTP status
   * will be returned. If any entity has {@link HealthState#CRITICAL}, the 503 HTTP status will be
   * returned. If all entities has {@link HealthState#PASSING}, the 200 HTTP code will be returned.
   */
  @Override
  public HttpStatus getStatusCode() {
    HttpStatus code = super.getStatusCode();

    if (HttpStatus.OK != code) {
      return code;
    }

    if (getEntities().stream().anyMatch(HealthDetails::hasCriticalStatus)) {
      return CRITICAL.getHttpStatus();
    }

    if (getEntities().stream().anyMatch(HealthDetails::hasWarningStatus)) {
      return WARNING.getHttpStatus();
    }

    return PASSING.getHttpStatus();
  }

}
