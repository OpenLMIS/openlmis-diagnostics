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

import org.apache.commons.lang3.RandomStringUtils;

class HealthDetailsDataBuilder {
  private String node = "node";
  private String checkId = "checkId";
  private String name = "name";
  private HealthState status = HealthState.PASSING;
  private String notes = "";
  private String output = "";
  private String serviceId = "serviceId";
  private String serviceName = "serviceName";

  HealthDetailsDataBuilder withWarningStatus() {
    status = HealthState.WARNING;
    return this;
  }

  HealthDetailsDataBuilder withCriticalStatus() {
    status = HealthState.CRITICAL;
    return this;
  }

  HealthDetailsDataBuilder withRandomNotes() {
    notes = RandomStringUtils.randomAlphabetic(10);
    return this;
  }

  HealthDetailsDataBuilder withRandomOutput() {
    output = RandomStringUtils.randomAlphabetic(10);
    return this;
  }

  HealthDetails build() {
    HealthDetails details = new HealthDetails();
    details.setNode(node);
    details.setCheckId(checkId);
    details.setName(name);
    details.setStatus(status);
    details.setNotes(notes);
    details.setOutput(output);
    details.setServiceId(serviceId);
    details.setServiceName(serviceName);

    return details;
  }
}
