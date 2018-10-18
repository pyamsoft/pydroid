package com.pyamsoft.pydroid.bootstrap.libraries

enum class OssLicenses(
  internal val license: String,
  internal val location: String
) {
  APACHE2("Apache v2", "https://www.apache.org/licenses/LICENSE-2.0.html"),
  MIT("MIT", "https://mit-license.org/"),
  BSD2("Simplified BSD", "https://opensource.org/licenses/BSD-2-Clause"),
  BSD3("BSD 3-Clause", "https://opensource.org/licenses/BSD-3-Clause");
}
