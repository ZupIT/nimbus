package com.zup.nimbus.core.integration.setState

const val SET_STATE_SCREEN = """{
  "_:component": "layout:column",
  "state": {
    "id": "user",
    "value": {
      "name": "",
      "age": 0
    }
  },
  "children": [
    {
      "_:component": "layout:row",
      "children": [
        {
          "_:component": "material:text",
          "properties": {
            "text": "Name: @{user.name}"
          }
        },
        {
          "_:component": "material:text",
          "properties": {
            "text": "Age: @{user.age}"
          }
        }
      ]
    },
    {
      "_:component": "layout:row",
      "children": [
        {
          "_:component": "material:button",
          "id": "setName",
          "properties": {
            "text": "Set name to John",
            "onPress": [{
              "_:action": "setState",
              "properties": {
                "path": "user.name",
                "value": "John"
              }
            }]
          }
        },
        {
          "_:component": "material:button",
          "id": "setAge",
          "properties": {
            "text": "Set age to 30",
            "onPress": [{
              "_:action": "setState",
              "properties": {
                "path": "user.age",
                "value": 30
              }
            }]
          }
        },
        {
          "_:component": "material:button",
          "id": "setButtonText",
          "state": {
            "id": "btnText",
            "value": "aaa"
          },
          "properties": {
            "text": "Set this next text to bbb: @{btnText}",
            "onPress": [{
              "_:action": "setState",
              "properties": {
                "path": "btnText",
                "value": "bbb"
              }
            }]
          }
        }
      ]
    }
  ]
}"""
