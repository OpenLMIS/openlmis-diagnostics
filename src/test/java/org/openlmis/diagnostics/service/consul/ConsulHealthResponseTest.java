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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.http.HttpStatus;

public class ConsulHealthResponseTest {

  @Test
  public void shouldReturnOriginalStatusIfItIsDifferentThan200() throws Exception {
    ConsulHealthResponse response = new ConsulHealthResponseDataBuilder()
        .withInternalServerErrorStatus()
        .withPassingEntity()
        .withWarningEntity()
        .withCriticalEntity()
        .build();

    assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @Test
  public void shouldReturnOkStatusIfAllEntitiesHasPassingStatus() {
    assertThat(
        new ConsulHealthResponseDataBuilder().withPassingEntity().build().getStatusCode(),
        equalTo(HttpStatus.OK)
    );
  }

  @Test
  public void shouldReturnTooManyRequestsStatusIfSingleEntityHasWarningStatus() {
    ConsulHealthResponse response = new ConsulHealthResponseDataBuilder()
        .withPassingEntity()
        .withWarningEntity()
        .build();

    assertThat(response.getStatusCode(), equalTo(HttpStatus.TOO_MANY_REQUESTS));
  }

  @Test
  public void shouldReturnServiceUnavailableStatusIfSingleEntityHasCriticalStatus() {
    ConsulHealthResponse response = new ConsulHealthResponseDataBuilder()
        .withPassingEntity()
        .withWarningEntity()
        .withCriticalEntity()
        .build();

    assertThat(response.getStatusCode(), equalTo(HttpStatus.SERVICE_UNAVAILABLE));
  }
}