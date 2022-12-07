package br.com.zup.nimbus.core.integration.ifThenElse

fun createIfThenElseScreen(
  conditionValue: Boolean,
  includeThen: Boolean = true,
  includeElse: Boolean = false,
  includeInvalid: Boolean = false,
  includeButton: Boolean = false,
): String {
  val thenComponent = """{
    "_:component": "then",
    "children": [
      {
        "_:component": "material:text",
        "properties": {
          "text": "Good morning!"
        }
      },
      {
        "_:component": "layout:image",
        "properties": {
          "id": "sun"
        }
      }
    ]
  }"""

  val elseComponent = """{
    "_:component": "else",
    "children": [
      {
        "_:component": "material:text",
        "properties": {
          "text": "Good evening!"
        }
      },
      {
        "_:component": "layout:image",
        "properties": {
          "id": "moon"
        }
      }
    ]
  }"""

  val invalidComponent = """{
    "_:component": "material:text",
    "properties": {
      "text": "Hello World!"
    }
  }"""

  val button = if (includeButton) """, {
    "_:component": "material:button",
    "id": "toggle",
    "properties": {
      "text": "toggle",
      "onPress": [
        {
          "_:action": "setState",
          "properties": {
            "path": "isMorning",
            "value": "@{not(isMorning)}"
          }
        }
      ]
    }
  }""" else ""

  val components = ArrayList<String>()
  if (includeThen) components.add(thenComponent)
  if (includeElse) components.add(elseComponent)
  if (includeInvalid) components.add(invalidComponent)

  return """{
    "_:component": "layout:column",
    "state": {
      "isMorning": $conditionValue
    },
    "children": [
      {
        "_:component": "if",
        "properties": {
          "condition": "@{isMorning}"
        },
        "children": [
          ${components.joinToString(",")}
        ]
      }$button
    ]
  }"""
}

private fun createRootIfButton(value: Boolean) = """{
  "_:component": "material:button",
  "id": "toggle-$value",
  "properties": {
    "text": "value is $value",
    "onPress": [{
      "_:action": "setState",
      "properties": {
        "path": "test",
        "value": ${!value}
      }
    }]
  }
}"""

val simpleRootIf = """{
  "_:component": "if",
  "state": {
    "test": true
  },
  "properties": {
    "condition": "@{test}"
  },
  "children": [
    {
      "_:component": "then",
      "children": [${createRootIfButton(true)}]
    },
    {
      "_:component": "else",
      "children": [${createRootIfButton(false)}]
    }
  ]
}"""

const val INVALID_IF = """{
  "_:component": "if",
  "children": [
    {
      "_:component": "then",
      "children": [
        {
          "_:component": "layout:text",
          "properties": {
            "text": "Then"
          }
        }
      ]
    },
    {
      "_:component": "else",
      "children": [
        {
          "_:component": "layout:text",
          "properties": {
            "text": "Else"
          }
        }
      ]
    }
  ]
}"""

const val NESTED_IF = """{
  "_:component": "layout:column",
  "id": "counter-column",
  "state": {
    "counter": 0
  },
  "children": [
    {
      "_:component": "layout:column",
      "state": {
        "showCounter": false
      },
      "children": [
        {
          "_:component": "material:button",
          "id": "show",
          "properties": {
            "onPress": [
              {
                "_:action": "setState",
                "properties": {
                  "path": "showCounter",
                  "value": true
                }
              }
            ]
          }
        },
        {
          "_:component": "material:button",
          "id": "count",
          "properties": {
            "onPress": [
              {
                "_:action": "setState",
                "properties": {
                  "path": "counter",
                  "value": "@{sum(counter, 1)}"
                }
              }
            ]
          }
        },
        {
          "_:component": "if",
          "id": "outer-if",
          "properties": {
            "condition": "@{showCounter}"
          },
          "children": [
            {
              "_:component": "then",
              "children": [
                {
                  "_:component": "if",
                  "id": "inner-if",
                  "properties": {
                    "condition": "@{eq(counter, 0)}"
                  },
                  "children": [
                    {
                      "_:component": "then",
                      "children": [
                        {
                          "_:component": "layout:text",
                          "id": "counter-zero",
                          "properties": {
                            "text": "Counter is zero"
                          }
                        }
                      ]
                    },
                    {
                      "_:component": "else",
                      "children": [
                        {
                          "_:component": "layout:text",
                          "id": "counter",
                          "properties": {
                            "text": "@{counter}"
                          }
                        }
                      ]
                    }
                  ]
                }
              ]
            },
            {
              "_:component": "else",
              "children": [
                {
                  "_:component": "layout:text",
                  "id": "not-showing",
                  "properties": {
                    "text": "Not showing counter"
                  }
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}"""
