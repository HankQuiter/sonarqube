/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package it.qualityProfile;

import com.sonar.orchestrator.Orchestrator;
import it.Category6Suite;
import java.util.function.Predicate;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.sonarqube.ws.Organizations.Organization;
import org.sonarqube.ws.QualityProfiles.CreateWsResponse;
import org.sonarqube.ws.QualityProfiles.SearchWsResponse;
import org.sonarqube.ws.QualityProfiles.SearchWsResponse.QualityProfile;
import org.sonarqube.ws.client.qualityprofile.ActivateRuleWsRequest;
import org.sonarqube.ws.client.qualityprofile.DeleteRequest;
import org.sonarqube.ws.client.qualityprofile.SearchWsRequest;
import org.sonarqube.ws.client.qualityprofile.SetDefaultRequest;
import util.OrganizationRule;
import util.QualityProfileRule;
import util.user.UserRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static util.ItUtils.expectBadRequestError;

public class BuiltInQualityProfilesTest {
  private static final String RULE_ONE_BUG_PER_LINE = "xoo:OneBugIssuePerLine";

  private static Orchestrator orchestrator = Category6Suite.ORCHESTRATOR;
  private static OrganizationRule organizations = new OrganizationRule(orchestrator);
  private static QualityProfileRule profiles = new QualityProfileRule(orchestrator);
  private static UserRule users = new UserRule(orchestrator);

  @ClassRule
  public static TestRule chain = RuleChain.outerRule(orchestrator)
    .around(organizations)
    .around(profiles)
    .around(users);

  @Test
  public void built_in_profiles_are_available_in_new_organization() {
    Organization org = organizations.create();
    SearchWsResponse result = profiles.getWsService().search(new SearchWsRequest().setOrganizationKey(org.getKey()));

    assertThat(result.getProfilesList())
      .extracting(QualityProfile::getName, QualityProfile::getLanguage, QualityProfile::getIsBuiltIn, QualityProfile::getIsDefault)
      .containsExactlyInAnyOrder(
        tuple("Basic", "xoo", true, true),
        tuple("empty", "xoo", true, false),
        tuple("Basic", "xoo2", true, true));
  }

  @Test
  public void built_in_profiles_are_available_in_default_organization() {
    SearchWsResponse result = profiles.getWsService().search(new SearchWsRequest().setOrganizationKey("default-organization"));

    assertThat(result.getProfilesList())
      .extracting(QualityProfile::getOrganization, QualityProfile::getName, QualityProfile::getLanguage, QualityProfile::getIsBuiltIn, QualityProfile::getIsDefault)
      .containsExactlyInAnyOrder(
        tuple("default-organization", "Basic", "xoo", true, true),
        tuple("default-organization", "empty", "xoo", true, false),
        tuple("default-organization", "Basic", "xoo2", true, true));
  }

  @Test
  public void cannot_delete_built_in_profile_even_when_not_the_default_profile() {
    Organization org = organizations.create();
    QualityProfile builtInProfile = getProfile(org, p -> p.getIsBuiltIn() && p.getIsDefault() && "Basic".equals(p.getName()) && "xoo".equals(p.getLanguage()));

    CreateWsResponse.QualityProfile profileInOrg = profiles.createXooProfile(org);
    profiles.getWsService().setDefault(new SetDefaultRequest(profileInOrg.getKey()));

    expectBadRequestError(() ->
      profiles.getWsService().delete(new DeleteRequest(builtInProfile.getKey())));
  }

  @Test
  public void fail_to_modify_built_in_quality_profile() {
    Organization org = organizations.create();
    QualityProfile builtInProfile = getProfile(org, p -> p.getIsBuiltIn() && p.getIsDefault() && "Basic".equals(p.getName()) && "xoo".equals(p.getLanguage()));
    assertThat(builtInProfile.getIsBuiltIn()).isTrue();

    expectBadRequestError(() ->
      profiles.getWsService().activateRule(
        ActivateRuleWsRequest.builder()
          .setOrganization(org.getKey())
          .setProfileKey(builtInProfile.getKey())
          .setRuleKey(RULE_ONE_BUG_PER_LINE)
          .build()));
  }

  @Test
  public void fail_to_delete_built_in_quality_profile() {
    Organization org = organizations.create();
    QualityProfile builtInProfile = getProfile(org, p -> p.getIsBuiltIn() && p.getIsDefault() && "Basic".equals(p.getName()) && "xoo".equals(p.getLanguage()));
    assertThat(builtInProfile.getIsBuiltIn()).isTrue();

    expectBadRequestError(() ->
      profiles.getWsService().delete(new DeleteRequest(builtInProfile.getKey())));
  }

  private QualityProfile getProfile(Organization organization, Predicate<QualityProfile> filter) {
    return profiles.getWsService().search(new SearchWsRequest()
      .setOrganizationKey(organization.getKey())).getProfilesList()
      .stream()
      .filter(filter)
      .findAny().orElseThrow(IllegalStateException::new);
  }
}
