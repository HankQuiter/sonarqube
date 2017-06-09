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
package org.sonar.server.platform.db.migration.version.v65;

import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class PopulateUsersOnboardedTest {

  @Rule
  public CoreDbTester db = CoreDbTester.createForSchema(PopulateUsersOnboardedTest.class, "users_with_onboarded_column.sql");

  public PopulateUsersOnboarded underTest = new PopulateUsersOnboarded(db.database());

  @Test
  public void set_onboarded_to_true() throws SQLException {
    db.executeInsert("USERS","LOGIN", "admin", "ONBOARDED", false, "IS_ROOT", true);
    db.executeInsert("USERS","LOGIN", "user", "ONBOARDED", false, "IS_ROOT", false);
    assertThat(db.select("SELECT ONBOARDED FROM USERS").stream().map(map -> (Boolean) map.get("ONBOARDED")).collect(toList())).containsExactly(false, false);

    underTest.execute();

    assertThat(db.select("SELECT ONBOARDED FROM USERS").stream().map(map -> (Boolean) map.get("ONBOARDED")).collect(toList())).containsExactly(true, true);
  }

  @Test
  public void set_onboarded_to_false_when_single_admin_user() throws SQLException {
    db.executeInsert("USERS","LOGIN", "admin", "ONBOARDED", false, "IS_ROOT", true);

    underTest.execute();

    assertThat(db.select("SELECT ONBOARDED FROM USERS").stream().map(map -> (Boolean) map.get("ONBOARDED")).collect(toList())).containsExactly(false);
  }

  @Test
  public void set_onboarded_to_true_when_single_user_but_not_admin() throws SQLException {
    db.executeInsert("USERS","LOGIN", "user", "ONBOARDED", false, "IS_ROOT", true);

    underTest.execute();

    assertThat(db.select("SELECT ONBOARDED FROM USERS").stream().map(map -> (Boolean) map.get("ONBOARDED")).collect(toList())).containsExactly(true);
  }
}
