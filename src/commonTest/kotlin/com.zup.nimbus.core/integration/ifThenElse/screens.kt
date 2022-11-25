package com.zup.nimbus.core.integration.ifThenElse

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
      "id": "isMorning",
      "value": $conditionValue
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
    "id": "test",
    "value": true
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
