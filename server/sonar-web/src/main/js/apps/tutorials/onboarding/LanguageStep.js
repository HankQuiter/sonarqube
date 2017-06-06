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
import ProjectKeyStep from './ProjectKeyStep';
import RadioToggle from '../../../components/controls/RadioToggle';
import { translate } from '../../../helpers/l10n';

type LanguageOptions = 'java' | 'dotnet' | 'c-family' | 'other';
type JavaBuildOptions = 'maven' | 'gradle';
type CFamilyCompilerOptions = 'msvc' | 'clang-gcc';
type OSOptions = 'linux' | 'win' | 'mac' | 'any';

export type Result =
  | { language: 'java', javaBuild: JavaBuildOptions }
  | { language: 'dotnet' }
  | { language: 'c-family', cFamilyCompiler: CFamilyCompilerOptions }
  | { language: 'other', os: OSOptions, projectKey: string };

type Props = {
  onDone: (result: Result) => void,
  onReset: () => void,
  organization?: string
};

type State = {
  language?: LanguageOptions,
  javaBuild?: JavaBuildOptions,
  cFamilyCompiler?: CFamilyCompilerOptions,
  os?: OSOptions,
  projectKey?: string
};

export default class LanguageStep extends React.PureComponent {
  props: Props;
  state: State = {};

  handleLanguageChange = (language: LanguageOptions) => {
    this.setState({
      language,
      javaBuild: undefined,
      cFamilyCompiler: undefined,
      os: undefined,
      projectKey: undefined
    });
    if (language === 'dotnet') {
      this.props.onDone({ language: 'dotnet' });
    } else {
      this.props.onReset();
    }
  };

  handleJavaBuildChange = (javaBuild: JavaBuildOptions) => {
    this.setState({ javaBuild });
    this.props.onDone({ language: 'java', javaBuild });
  };

  handleCFamilyCompilerChange = (cFamilyCompiler: CFamilyCompilerOptions) => {
    this.setState({ cFamilyCompiler });
    this.props.onDone({ language: 'c-family', cFamilyCompiler });
  };

  handleOSChange = (os: OSOptions) => {
    this.setState({ os });
  };

  handleProjectKeyChange = (event: { target: HTMLInputElement }) => {
    this.setState({ projectKey: event.target.value });
  };

  handleProjectKeyDone = (projectKey: string) => {
    if (this.state.os) {
      this.props.onDone({
        language: 'other',
        os: this.state.os,
        projectKey
      });
    }
  };

  renderJavaBuild = () => (
    <div className="big-spacer-top">
      <h4 className="spacer-bottom">
        {translate('onboarding.language.java.build_technology')}
      </h4>
      <RadioToggle
        name="java-build"
        onCheck={this.handleJavaBuildChange}
        options={['maven', 'gradle'].map(build => ({
          label: translate('onboarding.language.java.build_technology', build),
          value: build
        }))}
        value={this.state.javaBuild}
      />
    </div>
  );

  renderCFamilyCompiler = () => (
    <div className="big-spacer-top">
      <h4 className="spacer-bottom">
        {translate('onboarding.language.c-family.compiler')}
      </h4>
      <RadioToggle
        name="c-family-compiler"
        onCheck={this.handleCFamilyCompilerChange}
        options={['msvc', 'clang-gcc'].map(compiler => ({
          label: translate('onboarding.language.c-family.compiler', compiler),
          value: compiler
        }))}
        value={this.state.cFamilyCompiler}
      />
    </div>
  );

  renderOS = () => (
    <div className="big-spacer-top">
      <h4 className="spacer-bottom">
        {translate('onboarding.language.os')}
      </h4>
      <RadioToggle
        name="c-family-compiler"
        onCheck={this.handleOSChange}
        options={['linux', 'win', 'mac', 'any'].map(os => ({
          label: translate('onboarding.language.os', os),
          value: os
        }))}
        value={this.state.os}
      />
    </div>
  );

  renderProjectKey = () => (
    <ProjectKeyStep
      onDelete={this.props.onReset}
      onDone={this.handleProjectKeyDone}
      organization={this.props.organization}
    />
  );

  render() {
    return (
      <div>
        <div>
          <h4 className="spacer-bottom">{translate('onboarding.language')}</h4>
          <RadioToggle
            name="language"
            onCheck={this.handleLanguageChange}
            options={['java', 'dotnet', 'c-family', 'other'].map(language => ({
              label: translate('onboarding.language', language),
              value: language
            }))}
            value={this.state.language}
          />
        </div>
        {this.state.language === 'java' && this.renderJavaBuild()}
        {this.state.language === 'c-family' && this.renderCFamilyCompiler()}
        {this.state.language === 'other' && this.renderOS()}
        {this.state.language === 'other' && this.state.os !== undefined && this.renderProjectKey()}
      </div>
    );
  }
}
