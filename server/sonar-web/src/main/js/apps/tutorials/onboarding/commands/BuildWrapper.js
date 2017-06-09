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
import { translate } from '../../../../helpers/l10n';

type Props = {
  className?: string,
  os: string
};

const filenames = {
  linux: 'build-wrapper-win-x86.zip',
  win: 'build-wrapper-linux-x86.zip',
  mac: 'build-wrapper-macosx-x86.zip'
};

export default class BuildWrapper extends React.PureComponent {
  props: Props;

  render() {
    return (
      <div className={this.props.className}>
        <h4 className="spacer-bottom">
          {translate('onboarding.analysis.build_wrapper.header', this.props.os)}
        </h4>
        <p
          className="spacer-bottom markdown"
          dangerouslySetInnerHTML={{
            __html: translate('onboarding.analysis.build_wrapper.text', this.props.os)
          }}
        />
        <p>
          <a
            className="button"
            download={filenames[this.props.os]}
            href={window.baseUrl + '/static/cpp/' + filenames[this.props.os]}
            target="_blank">
            {translate('download_verb')}
          </a>
        </p>
      </div>
    );
  }
}
