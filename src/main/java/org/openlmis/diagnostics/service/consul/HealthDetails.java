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

import static com.google.common.collect.Lists.newArrayList;
import static org.openlmis.diagnostics.service.consul.HealthState.CRITICAL;
import static org.openlmis.diagnostics.service.consul.HealthState.WARNING;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@NoArgsConstructor
public class HealthDetails implements ConsulEntity {
  // This class will be used to catch consul response and create service response.
  // Difference between those two responses are that consul response have different name
  // convention than OpenLMIS and that is why setters have different json property value
  // than getters.

  private String node;
  private String checkId;
  private String name;
  private HealthState status;
  private String notes;
  private String output;
  private String serviceId;
  private String serviceName;
  private List<String> serviceTags;

  @JsonProperty("node")
  public String getNode() {
    return node;
  }

  @JsonProperty("Node")
  public void setNode(String node) {
    this.node = node;
  }

  @JsonProperty("checkId")
  public String getCheckId() {
    return checkId;
  }

  @JsonProperty("CheckID")
  public void setCheckId(String checkId) {
    this.checkId = checkId;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("Name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("status")
  public HealthState getStatus() {
    return status;
  }

  @JsonProperty("Status")
  public void setStatus(HealthState status) {
    this.status = status;
  }

  @JsonProperty("notes")
  public String getNotes() {
    return notes;
  }

  @JsonProperty("Notes")
  public void setNotes(String notes) {
    this.notes = notes;
  }

  @JsonProperty("output")
  public String getOutput() {
    return output;
  }

  @JsonProperty("Output")
  public void setOutput(String output) {
    this.output = output;
  }

  @JsonProperty("serviceId")
  public String getServiceId() {
    return serviceId;
  }

  @JsonProperty("ServiceID")
  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  @JsonProperty("serviceName")
  public String getServiceName() {
    return serviceName;
  }

  @JsonProperty("ServiceName")
  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  @JsonProperty("serviceTags")
  public List<String> getServiceTags() {
    return Optional.ofNullable(serviceTags).orElse(newArrayList());
  }

  @JsonProperty("ServiceTags")
  public void setServiceTags(List<String> serviceTags) {
    this.serviceTags = Optional.ofNullable(serviceTags).orElse(newArrayList());
  }

  @JsonIgnore
  boolean hasCriticalStatus() {
    return CRITICAL == status;
  }

  @JsonIgnore
  boolean hasWarningStatus() {
    return WARNING == status;
  }

  @JsonIgnore
  boolean hasServiceTag(String serviceTag) {
    return getServiceTags().contains(serviceTag);
  }

}
