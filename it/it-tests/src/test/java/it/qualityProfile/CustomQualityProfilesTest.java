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
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.sonarqube.ws.Organizations.Organization;
import org.sonarqube.ws.QualityProfiles.CreateWsResponse.QualityProfile;
import org.sonarqube.ws.WsUsers.CreateWsResponse.User;
import util.OrganizationRule;
import util.QualityProfileRule;
import util.QualityProfileSupport;
import util.user.UserRule;

import static util.ItUtils.expectForbiddenError;
import static util.ItUtils.expectUnauthorizedError;

public class CustomQualityProfilesTest {

  private static final String A_PASSWORD = "a_password";

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
  public void activation_of_rules_is_isolated_among_organizations() {
    // create two profiles with same names in two organizations
    Organization org1 = organizations.create();
    Organization org2 = organizations.create();
    QualityProfile profileInOrg1 = profiles.createXooProfile(org1, p -> p.setProfileName("foo"));
    QualityProfile profileInOrg2 = profiles.createXooProfile(org2, p -> p.setProfileName("foo"));

    profiles
      .assertThatNumberOfActiveRulesEqualsTo(profileInOrg1, 0)
      .assertThatNumberOfActiveRulesEqualsTo(profileInOrg2, 0);

    profiles
      .activateRule(profileInOrg1, "xoo:OneIssuePerLine")
      .assertThatNumberOfActiveRulesEqualsTo(profileInOrg1, 1)
      .assertThatNumberOfActiveRulesEqualsTo(profileInOrg2, 0);

    profiles
      .activateRule(profileInOrg1, "xoo:OneIssuePerFile")
      .assertThatNumberOfActiveRulesEqualsTo(profileInOrg1, 2)
      .assertThatNumberOfActiveRulesEqualsTo(profileInOrg2, 0);

    profiles
      .activateRule(profileInOrg2, "xoo:OneIssuePerFile")
      .assertThatNumberOfActiveRulesEqualsTo(profileInOrg1, 2)
      .assertThatNumberOfActiveRulesEqualsTo(profileInOrg2, 1);

    profiles
      .delete(profileInOrg1)
      .assertThatNumberOfActiveRulesEqualsTo(profileInOrg1, 0)
      .assertThatNumberOfActiveRulesEqualsTo(profileInOrg2, 1);
  }

  @Test
  public void an_organization_administrator_can_manage_the_profiles_of_organization() {
    Organization org = organizations.create();
    User user = createAdminUser(org);

    QualityProfileSupport adminProfiles = profiles.as(user.getLogin(), A_PASSWORD);
    QualityProfile profile = adminProfiles.createXooProfile(org);
    adminProfiles.assertThatNumberOfActiveRulesEqualsTo(profile, 0);

    adminProfiles
      .activateRule(profile, "xoo:OneIssuePerFile")
      .assertThatNumberOfActiveRulesEqualsTo(profile, 1);

    adminProfiles
      .delete(profile)
      .assertThatNumberOfActiveRulesEqualsTo(profile, 0);
  }

  @Test
  public void an_organization_administrator_cannot_manage_the_profiles_of_other_organizations() {
    Organization org1 = organizations.create();
    Organization org2 = organizations.create();
    QualityProfile profileInOrg2 = profiles.createXooProfile(org2);
    User adminOfOrg1 = createAdminUser(org1);

    QualityProfileSupport adminProfiles = profiles.as(adminOfOrg1.getLogin(), A_PASSWORD);

    expectForbiddenError(() -> adminProfiles.createXooProfile(org2));
    expectForbiddenError(() -> adminProfiles.delete(profileInOrg2));
    expectForbiddenError(() -> adminProfiles.activateRule(profileInOrg2, "xoo:OneIssuePerFile"));
  }

  @Test
  public void anonymous_cannot_manage_the_profiles_of_an_organization() {
    Organization org = organizations.create();
    QualityProfile profile = profiles.createXooProfile(org);

    QualityProfileSupport anonymousProfiles = profiles.asAnonymous();

    expectUnauthorizedError(() -> anonymousProfiles.createXooProfile(org));
    expectUnauthorizedError(() -> anonymousProfiles.delete(profile));
    expectUnauthorizedError(() -> anonymousProfiles.activateRule(profile, "xoo:OneIssuePerFile"));
  }

  @Test
  public void root_can_manage_the_profiles_of_any_organization() {
    Organization org = organizations.create();

    User orgAdmin = createAdminUser(org);
    QualityProfileSupport adminProfiles = profiles.as(orgAdmin.getLogin(), A_PASSWORD);
    QualityProfile profile = adminProfiles.createXooProfile(org);

    // root can activate rule and delete the profile
    profiles
      .activateRule(profile, "xoo:OneIssuePerFile")
      .assertThatNumberOfActiveRulesEqualsTo(profile, 1);
    profiles
      .delete(profile)
      .assertThatNumberOfActiveRulesEqualsTo(profile, 0);
  }

  private User createAdminUser(Organization organization) {
    User result = users.createUser(p -> p.setPassword(A_PASSWORD));
    organizations.getWsService().addMember(organization.getKey(), result.getLogin());
    users.forOrganization(organization.getKey()).associateGroupsToUser(result.getLogin(), "Owners");
    return result;
  }
}
