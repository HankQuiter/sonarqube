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
package util;

import java.util.function.Consumer;
import org.sonarqube.ws.Organizations;
import org.sonarqube.ws.QualityProfiles.CreateWsResponse.QualityProfile;
import org.sonarqube.ws.client.qualityprofile.CreateRequest;
import org.sonarqube.ws.client.qualityprofile.QualityProfilesService;

public interface QualityProfileSupport {

  QualityProfilesService getWsService();

  QualityProfile createXooProfile(Organizations.Organization organization, Consumer<CreateRequest.Builder>... populators);

  QualityProfileSupport delete(QualityProfile profile);

  QualityProfileSupport activateRule(QualityProfile profile, String ruleKey);

  QualityProfileSupport assertThatNumberOfActiveRulesEqualsTo(QualityProfile profile, int expectedActiveRules);

}
