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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class HealthDetails implements ConsulEntity {

  @JsonProperty("Node")
  private String node;

  @JsonProperty("CheckID")
  private String checkId;

  @JsonProperty("Name")
  private String name;

  @JsonProperty("Status")
  private HealthState status;

  @JsonProperty("Notes")
  private String notes;

  @JsonProperty("Output")
  private String output;

  @JsonProperty("ServiceID")
  private String serviceId;

  @JsonProperty("ServiceName")
  private String serviceName;

  @JsonProperty("ServiceTags")
  private List<String> serviceTags;

  boolean hasServiceTag(String serviceTag) {
    return null != serviceTags && serviceTags.contains(serviceTag);
  }

}
