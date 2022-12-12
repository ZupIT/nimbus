/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
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

package br.com.zup.nimbus.core.integration.setState

const val GENERAL_SET_STATE = """{
  "_:component": "layout:column",
  "state": {
    "user": {
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
            "btnText": "aaa"
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

const val UNREACHABLE_STATE = """{
  "_:component": "layout:lifecycle",
  "properties": {
    "onInit": [{
      "_:action": "setState",
      "path": "test",
      "value": "hello"
    }]
  },
  "children": [
    {
      "_:component": "material:text",
      "state": {
        "test": ""
      },
      "properties": {
        "text": "@{test}"
      }
    }
  ]
}"""

const val MANY_TYPES = """{
  "_:component": "layout:column",
  "state": {
    "test": "string"
  },
  "children": [
    {
      "_:component": "material:text",
      "properties": {
        "text": "@{test}"
      }
    },
    {
      "_:component": "material:button",
      "id": "setInt",
      "properties": {
        "text": "Set integer value",
        "onPress": [{
          "_:action": "setState",
          "properties": {
            "path": "test",
            "value": 10
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "id": "setDouble",
      "properties": {
        "text": "Set double value",
        "onPress": [{
          "_:action": "setState",
          "properties": {
            "path": "test",
            "value": 5.64
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "id": "setArray",
      "properties": {
        "text": "Set array value",
        "onPress": [{
          "_:action": "setState",
          "properties": {
            "path": "test",
            "value": [0, 1, 2]
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "id": "setMap",
      "properties": {
        "text": "Set map value",
        "onPress": [{
          "_:action": "setState",
          "properties": {
            "path": "test",
            "value": {
              "a": 0,
              "b": 1
            }
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "id": "setBoolean",
      "properties": {
        "text": "Set boolean value",
        "onPress": [{
          "_:action": "setState",
          "properties": {
            "path": "test",
            "value": true
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "id": "setNull",
      "properties": {
        "text": "Set null",
        "onPress": [{
          "_:action": "setState",
          "properties": {
            "path": "test",
            "value": null
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "id": "setString",
      "properties": {
        "text": "Set string value",
        "onPress": [{
          "_:action": "setState",
          "properties": {
            "path": "test",
            "value": "string"
          }
        }]
      }
    }
  ]
}"""

const val DEEP_STATE = """{
  "_:component": "layout:column",
  "state": {
    "test": {
      "a": {
        "b": {
          "c": {
            "d": {
              "e": 0,
              "f": 1
            },
            "g": 2
          }
        }
      }
    }
  },
  "children": [
    {
      "_:component": "layout:text",
      "properties": {
        "text": "@{test}"
      }
    },
    {
      "_:component": "material:button",
      "id": "abhiTo3",
      "properties": {
        "text": "set test.a.b.h.i to 3",
        "onPress": [{
          "_:action": "setState",
          "properties": {
            "path": "test.a.b.h.i",
            "value": 3
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "id": "abcdeTo4",
      "properties": {
        "text": "set test.a.b.c.d.e to 4",
        "onPress": [{
          "_:action": "setState",
          "properties": {
            "path": "test.a.b.c.d.e",
            "value": 4
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "id": "abTo5",
      "properties": {
        "text": "set test.a.b to 5",
        "onPress": [{
          "_:action": "setState",
          "properties": {
            "path": "test.a.b",
            "value": 5
          }
        }]
      }
    }
  ]
}"""

const val GLOBAL_STATE = """{
  "_:component": "layout:column",
  "children": [
    {
      "_:component": "material:text",
      "properties": {
        "text": "Hey @{global.user.name}!"
      }
    },
    {
      "_:component": "material:button",
      "id": "setUserName",
      "properties": {
        "text": "set username to John",
        "onPress": [{
          "_:action": "setState",
          "properties": {
            "path": "global.user.name",
            "value": "John"
          }
        }]
      }
    }
  ]
}"""

const val MULTIPLE_STATES = """{
  "_:component": "layout:column",
  "state": {
    "counterA": 0,
    "counterB": 0,
    "counterC": 0
  },
  "children": [
    {
      "_:component": "material:button",
      "id": "incCounterA",
      "properties": {
        "text": "@{counterA}",
        "onPress": [
          {
            "_:action": "setState",
            "properties": {
              "path": "counterA",
              "value": "@{sum(counterA, 1)}"
            }
          }
        ]
      }
    },
    {
      "_:component": "material:button",
      "id": "incCounterB",
      "properties": {
        "text": "@{counterB}",
        "onPress": [
          {
            "_:action": "setState",
            "properties": {
              "path": "counterB",
              "value": "@{sum(counterB, 1)}"
            }
          }
        ]
      }
    },
    {
      "_:component": "material:button",
      "id": "incCounterC",
      "properties": {
        "text": "@{counterC}",
        "onPress": [
          {
            "_:action": "setState",
            "properties": {
              "path": "counterC",
              "value": "@{sum(counterC, 1)}"
            }
          }
        ]
      }
    }
  ]
}"""
