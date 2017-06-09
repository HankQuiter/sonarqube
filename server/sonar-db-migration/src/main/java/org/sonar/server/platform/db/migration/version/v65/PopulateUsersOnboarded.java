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
import org.sonar.db.Database;
import org.sonar.server.platform.db.migration.step.DataChange;
import org.sonar.server.platform.db.migration.step.Select;

public class PopulateUsersOnboarded extends DataChange {

  public PopulateUsersOnboarded(Database db) {
    super(db);
  }

  @Override
  public void execute(Context context) throws SQLException {
    context.prepareUpsert("update users set onboarded=?")
      .setBoolean(1, true)
      // TODO update updated_at
      .execute()
      .commit();
    long users = context.prepareSelect("select count(u.id) from users u").get(Select.LONG_READER);
    if (users == 1) {
      context.prepareUpsert("update users set onboarded=? where login=?")
        .setBoolean(1, false)
        // TODO update updated_at
        .setString(2, "admin")
        .execute()
        .commit();
    }
  }
}
