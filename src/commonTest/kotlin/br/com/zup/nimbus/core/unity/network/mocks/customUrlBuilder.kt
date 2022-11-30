package br.com.zup.nimbus.core.unity.network.mocks

import br.com.zup.nimbus.core.network.UrlBuilder

class CustomUrlBuilderTest: UrlBuilder {
  override fun build(path: String): String {
    return "/custom-builder/$path"
  }
}
