package com.zup.nimbus.core.network

interface UrlBuilder {
  /**
   * Builds the full url based on your `baseUrl`.
   *
   * If path starts with `/`, it's considered to be relative to the base url. i.e. the path passed
   * as parameter is appended to the `baseUrl`.
   *
   * Otherwise, the url is considered to be absolute and and the baseUrl is not used to build it.
   *
   * Important: this method expect the path to be encoded if it contains special characters.
   *
   * @param path the path to generate the URL (should be encoded if it contains special characters)
   * @returns the final url
   */
  fun build(path: String): String
}
