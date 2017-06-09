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
import SQScanner from './SQScanner';
import BuildWrapper from './BuildWrapper';
import { translate } from '../../../../helpers/l10n';

type Props = {
  host: string,
  os: string,
  organization?: string,
  projectKey: string,
  token: string
};

const s = ' \\' + '\n  ';

function getCommand1(os: string) {
  const executable = os === 'linux'
    ? 'build-wrapper-linux-x86-64'
    : os === 'win' ? 'build-wrapper-win-x86-64.exe' : 'build-wrapper-macosx-x86';
  return `${executable} --out-dir bw-output make clean all`;
}

function getCommand2(
  os: string,
  token: string,
  host: string,
  projectKey: string,
  organization?: string
) {
  /* eslint-disable max-len */
  const executable = os === 'win' ? 'sonar-scanner.bat' : 'sonar-scanner';
  const organizationPart = organization ? `-Dsonar.organization=${organization}${s}` : '';
  return `${executable}${s}-Dsonar.projectKey=${projectKey}${s}${organizationPart}-Dsonar.sources=.${s}-Dsonar.cfamily.build-wrapper-output=bw-output${s}-Dsonar.host.url=${host}${s}-Dsonar.login=${token}`;
  /* eslint-enable max-len */
}

export default class ClangGCC extends React.PureComponent {
  props: Props;

  render() {
    return (
      <div>
        <SQScanner os={this.props.os} />
        <BuildWrapper className="huge-spacer-top" os={this.props.os} />

        <h4 className="huge-spacer-top spacer-bottom">
          {translate('onboarding.analysis.sq_scanner.execute')}
        </h4>
        <p
          className="spacer-bottom markdown"
          dangerouslySetInnerHTML={{
            __html: translate('onboarding.analysis.sq_scanner.execute.text')
          }}
        />
        <Command command={getCommand1(this.props.os)} />
        <Command
          command={getCommand2(
            this.props.os,
            this.props.token,
            this.props.host,
            this.props.projectKey,
            this.props.organization
          )}
        />
        <p
          className="big-spacer-top markdown"
          dangerouslySetInnerHTML={{ __html: translate('onboarding.analysis.sq_scanner.docs') }}
        />
      </div>
    );
  }
}
