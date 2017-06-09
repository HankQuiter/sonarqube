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
import Step from './Step';
import LanguageStep from './LanguageStep';
import type { Result } from './LanguageStep';
import { translate } from '../../../helpers/l10n';

type Props = {
  open: boolean,
  organization?: string,
  stepNumber: number,
  token: string
};

type State = {
  result?: Result
};

export default class AnalysisStep extends React.PureComponent {
  props: Props;
  state: State = {};

  handleLanguageSelect = (result?: Result) => {
    this.setState({ result });
  };

  handleLanguageReset = () => {
    this.setState({ result: undefined });
  };

  renderForm = () => {
    return (
      <div className="boxed-group-inner">
        <div className="flex-columns">
          <div className="flex-column flex-column-half bordered-right">
            <LanguageStep onDone={this.handleLanguageSelect} onReset={this.handleLanguageReset} />
          </div>
          <div className="flex-column flex-column-half">
            {this.state.result != null && <i className="icon-check" />}
          </div>
        </div>
      </div>
    );
  };

  renderResult = () => null;

  render() {
    return (
      <Step
        open={this.props.open}
        renderForm={this.renderForm}
        renderResult={this.renderResult}
        stepNumber={this.props.stepNumber}
        stepTitle={translate('onboarding.analysis.header')}
      />
    );
  }
}
