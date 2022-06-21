package com.zup.nimbus.core.integration.setContent

const val SET_CONTENT_SCREEN = """{
  "_:component": "layout:column",
  "children": [
    {
      "_:component": "layout:column",
      "id": "board",
      "children": [
        {
          "_:component": "material:text",
          "properties": {
            "text": "Board"
          }
        }
      ]
    },
    {
      "_:component": "layout:column",
      "children": [
        {
          "_:component": "material:button",
          "id": "append",
          "properties": {
            "text": "Append component to Board",
            "onPress": [{
              "_:action": "setContent",
              "properties": {
                "id": "board",
                "value": {
                  "_:component": "material:text",
                  "properties": {
                    "text": "A new component"
                  }
                }
              }
            }]
          }
        },
        {
          "_:component": "material:button",
          "id": "prepend",
          "properties": {
            "text": "Prepend component to Board",
            "onPress": [{
              "_:action": "setContent",
              "properties": {
                "id": "board",
                "mode": "Prepend",
                "value": {
                  "_:component": "material:text",
                  "properties": {
                    "text": "A new component"
                  }
                }
              }
            }]
          }
        },
        {
          "_:component": "material:button",
          "id": "replace",
          "properties": {
            "text": "Replace the Board's content with a new component",
            "onPress": [{
              "_:action": "setContent",
              "properties": {
                "id": "board",
                "mode": "Replace",
                "value": {
                  "_:component": "material:text",
                  "properties": {
                    "text": "A new component"
                  }
                }
              }
            }]
          }
        },
        {
          "_:component": "material:button",
          "id": "replaceItself",
          "properties": {
            "text": "Replace the Board itself with a new component",
            "onPress": [{
              "_:action": "setContent",
              "properties": {
                "id": "board",
                "mode": "ReplaceItself",
                "value": {
                  "_:component": "material:text",
                  "properties": {
                    "text": "A new component"
                  }
                }
              }
            }]
          }
        }
      ]
    }
  ]
}"""
