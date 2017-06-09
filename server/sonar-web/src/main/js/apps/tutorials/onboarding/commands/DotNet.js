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
import MSBuildScanner from './MSBuildScanner';
import { translate } from '../../../../helpers/l10n';

type Props = {|
  host: string,
  organization?: string,
  projectKey: string,
  token: string
|};

const s = ' \\' + '\n  ';

function getCommand1(token: string, host: string, projectKey: string, organization?: string) {
  const organizationPart = organization ? `/d:"sonar.organization=${organization}"${s}` : '';
  /* eslint-disable max-len */
  return `SonarQube.Scanner.MSBuild.exe begin${s}/k:"${projectKey}"${s}${organizationPart}/d:"sonar.host.url=${host}${s}/d:"sonar.login=${token}"`;
  /* eslint-enable max-len */
}

function getCommand2() {
  return 'MsBuild.exe /t:Rebuild';
}

function getCommand3(token: string) {
  return `SonarQube.Scanner.MSBuild.exe end${s}/d:"sonar.login=${token}"`;
}

export default class DotNet extends React.PureComponent {
  props: Props;

  render() {
    return (
      <div>
        <MSBuildScanner />

        <h4 className="huge-spacer-top spacer-bottom">
          {translate('onboarding.analysis.msbuild.execute')}
        </h4>
        <p
          className="spacer-bottom markdown"
          dangerouslySetInnerHTML={{
            __html: translate('onboarding.analysis.msbuild.execute.text')
          }}
        />
        <Command
          command={getCommand1(
            this.props.token,
            this.props.host,
            this.props.projectKey,
            this.props.organization
          )}
        />
        <Command command={getCommand2()} />
        <Command command={getCommand3(this.props.token)} />
        <p
          className="big-spacer-top markdown"
          dangerouslySetInnerHTML={{ __html: translate('onboarding.analysis.msbuild.docs') }}
        />
      </div>
    );
  }
}
