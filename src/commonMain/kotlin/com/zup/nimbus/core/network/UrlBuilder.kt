package com.zup.nimbus.core.network

interface UrlBuilder {
  /**
   * Builds a safe url based on your `baseUrl`.
   *
   * If path starts with `/`, it's considered to be relative to the base url. i.e. the path passed
   * as parameter is appended to the `baseUrl`.
   *
   * Otherwise, the url is considered to be absolute and it doesn't use the baseUrl to be built.
   *
   * In any case, paths with special characters gets encoded.
   *
   * @param path the path to generate the URL to
   * @returns the final url
   */
  fun build(path: String): String
}
