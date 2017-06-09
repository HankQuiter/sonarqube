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
// @flow
import React from 'react';
import Command from './Command';
import { translate } from '../../../../helpers/l10n';

type Props = {|
  host: string,
  organization?: string,
  token: string
|};

function getConfig() {
  return 'plugins {\n  id "org.sonarqube" version "2.2"\n}';
}

function getCommand(token: string, host: string, organization?: string) {
  const s = ' \\' + '\n  ';
  const organizationPart = organization ? `-Dsonar.organization=${organization}${s}` : '';
  /* eslint-disable max-len */
  return `./gradlew sonarqube${s}${organizationPart}-Dsonar.host.url=${host}${s}-Dsonar.login=${token}`;
  /* eslint-enable max-len */
}

export default class JavaGradle extends React.PureComponent {
  props: Props;

  render() {
    return (
      <div>
        <h4 className="spacer-bottom">{translate('onboarding.analysis.java.gradle.header')}</h4>
        <p
          className="spacer-bottom markdown"
          dangerouslySetInnerHTML={{ __html: translate('onboarding.analysis.java.gradle.text.1') }}
        />
        <Command command={getConfig()} />
        <p className="spacer-top spacer-bottom markdown">
          {translate('onboarding.analysis.java.gradle.text.2')}
        </p>
        <Command command={getCommand(this.props.token, this.props.host, this.props.organization)} />
        <p
          className="big-spacer-top markdown"
          dangerouslySetInnerHTML={{ __html: translate('onboarding.analysis.java.gradle.docs') }}
        />
      </div>
    );
  }
}
