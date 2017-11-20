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

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.http.HttpStatus;

import lombok.Getter;

import java.util.Locale;

public enum HealthState {

  @JsonProperty("any")
  ANY(null),

  @JsonProperty("passing")
  PASSING(HttpStatus.OK),

  @JsonProperty("warning")
  WARNING(HttpStatus.TOO_MANY_REQUESTS),

  @JsonProperty("critical")
  CRITICAL(HttpStatus.SERVICE_UNAVAILABLE);

  @Getter
  HttpStatus httpStatus;

  HealthState(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
  }

  @Override
  public String toString() {
    return name().toLowerCase(Locale.ENGLISH);
  }
}