/*
 * Copyright 2016 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.base;

import com.pyamsoft.pydroid.about.AboutLibrariesComponent;
import com.pyamsoft.pydroid.support.SocialMediaComponent;
import com.pyamsoft.pydroid.version.ApiComponent;
import com.pyamsoft.pydroid.version.ApiModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = PYDroidModule.class) interface PYDroidComponent {

  AboutLibrariesComponent plusAboutLibrariesComponent();

  SocialMediaComponent plusSocialMediaComponent();

  ApiComponent plusApiComponent(ApiModule apiModule);
}
