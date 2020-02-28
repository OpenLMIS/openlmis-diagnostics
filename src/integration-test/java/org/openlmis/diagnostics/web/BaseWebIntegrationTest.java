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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import guru.nidi.ramltester.RamlDefinition;
import guru.nidi.ramltester.RamlLoaders;
import guru.nidi.ramltester.restassured.RestAssuredClient;
import javax.annotation.PostConstruct;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseWebIntegrationTest {

  private static final String BASE_URL = System.getenv("BASE_URL");
  static final String RAML_ASSERT_MESSAGE = "HTTP request/response should match RAML definition.";

  private static final RamlDefinition ramlDefinition = RamlLoaders
      .fromClasspath()
      .load("api-definition-raml.yaml")
      .ignoringXheaders();

  RestAssuredClient restAssured;

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(80);

  @LocalServerPort
  private int randomPort;

  @Autowired
  ObjectMapper objectMapper;

  /**
   * Initialize the REST Assured client. Done here and not in the constructor, so that randomPort is
   * available.
   */
  @PostConstruct
  public void init() {
    RestAssured.baseURI = BASE_URL;
    RestAssured.port = randomPort;
    RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
        new ObjectMapperConfig().jackson2ObjectMapperFactory((clazz, charset) -> objectMapper)
    );
    restAssured = ramlDefinition.createRestAssured();
  }

}
