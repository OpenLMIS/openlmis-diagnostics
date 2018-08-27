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
import static org.openlmis.diagnostics.service.consul.HealthState.WARNING;

import com.ecwid.consul.v1.health.model.Check;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public final class HealthDetails {
  private final String node;
  private final String checkId;
  private final String name;
  private final HealthState status;
  private final String notes;
  private final String output;
  private final String serviceId;
  private final String serviceName;

  HealthDetails(Check check) {
    node = check.getNode();
    checkId = check.getCheckId();
    name = check.getName();
    status = HealthState.valueOf(check.getStatus().name());
    notes = check.getNotes();
    output = check.getOutput();
    serviceId = check.getServiceId();
    serviceName = check.getServiceName();
  }

  @JsonIgnore
  public boolean hasCriticalStatus() {
    return CRITICAL == status;
  }

  @JsonIgnore
  public boolean hasWarningStatus() {
    return WARNING == status;
  }

}
