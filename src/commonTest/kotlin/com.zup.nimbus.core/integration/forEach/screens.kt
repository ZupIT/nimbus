package com.zup.nimbus.core.integration.forEach

const val GENERAL_FOR_EACH = """{
  "_:component": "layout:column",
  "state": {
    "id": "users",
    "value": {
        "premium": [
          { "name": "John", "age": 30 },
          { "name": "Mary", "age": 22 },
          { "name": "Anthony", "age": 5 }
        ],
        "basic": [
          { "name": "Rose", "age": 21 },
          { "name": "Paul", "age": 54 }
        ]
      }
  },
  "children": [
    {
      "_:component": "material:text",
      "properties": {
        "text": "Here's all the people in the premium plan:"
      }
    },
    {
      "_:component": "layout:column",
      "children": [
        {
          "_:component": "forEach",
          "properties": {
            "items": "@{users.premium}"
          },
          "children": [
            {
              "_:component": "material:text",
              "properties": {
                "text": "@{index}"
              }
            },
            {
              "_:component": "material:text",
              "properties": {
                "text": "@{item.name}"
              }
            },
            {
              "_:component": "material:text",
              "properties": {
                "text": "@{item.age}"
              }
            }
          ]
        }
      ]
    },
    {
      "_:component": "material:text",
      "properties": {
        "text": "Here's all the people in the basic plan:"
      }
    },
    {
      "_:component": "forEach",
      "properties": {
        "items": "@{users.basic}",
        "iteratorName": "user",
        "indexName": "position"
      },
      "children": [
        {
          "_:component": "material:text",
          "properties": {
            "text": "@{position}. @{user.name} @{user.age}"
          }
        }
      ]
    },
    {
      "_:component": "forEach",
      "properties": {
        "items": "@{nothing}"
      },
      "children": [{
        "_:component": "material:text",
          "properties": {
            "text": "This should never be rendered"
          }
      }]
    },
    {
      "_:component": "material:text",
      "properties": {
        "text": "End of the list of people."
      }
    }
  ]
}"""

const val STATEFUL_FOR_EACH = """{
  "_:component": "layout:column",
  "children": [
    {
      "_:component": "forEach",
      "state": {
        "id": "listCounter",
        "value": 0
      },
      "properties": {
        "items": ["John", "Mary", "Anthony"]
      },
      "children": [
        {
          "_:component": "layout:row",
          "state": {
            "id": "itemCounter",
            "value": 0
          },
          "children": [
            {
              "_:component": "material:text",
              "properties": {
                "text": "@{item}"
              }
            },
            {
              "_:component": "material:button",
              "id": "increment-list",
              "properties": {
                "text": "Increment list counter: @{listCounter}",
                "onPress": [{
                  "_:action": "setState",
                  "properties": {
                    "path": "listCounter",
                    "value": "@{sum(listCounter, 1)}"
                  }
                }]
              }
            },
            {
              "_:component": "material:button",
              "id": "increment-item",
              "properties": {
                "text": "Increment item counter: @{itemCounter}",
                "onPress": [{
                  "_:action": "setState",
                  "properties": {
                    "path": "itemCounter",
                    "value": "@{sum(itemCounter, 1)}"
                  }
                }]
              }
            }
          ]
        }
      ]
    }
  ]
}"""

const val FOR_EACH_WITH_KEY = """{
  "_:component": "layout:column",
  "children": [
    {
      "_:component": "forEach",
      "properties": {
        "items": [
          { "name": "John", "age": 30 },
          { "name": "Mary", "age": 22 },
          { "name": "Anthony", "age": 5 }
        ],
        "key": "name"
      },
      "children": [{
        "_:component": "material:text",
        "id": "person",
        "properties": {
          "text": "@{item.name}: @{item.age}"
        }
      }]
    }
  ]
}"""

const val NESTED_EMPTY_FOR_EACH = """{
  "_:component": "layout:column",
  "children": [
    {
      "_:component": "forEach",
      "properties": {
        "items": [0, 1, 2]
      },
      "children": [
        {
          "_:component": "forEach",
          "properties": {
            "items": "@{nothing}"
          },
          "children": [
            {
              "_:component": "material:text",
              "properties": {
                "text": "never rendered"
              }
            }
          ]
        }
      ]
    }
  ]
}"""

const val NESTED_FOR_EACH = """{
  "_:component": "layout:column",
  "children": [
    {
      "_:component": "forEach",
      "properties": {
        "items": [
          {
            "plan": "premium",
            "price": 59.9,
            "clients": [
              {
                "name": "John",
                "documents": ["045.445.875-96", "MG14785987"]
              },
              {
                "name": "Mary",
                "documents": ["854.112.745-98", "SP51476321"]
              },
              {
                "name": "Anthony",
                "documents": ["856.334.857-85", "PR14786320"]
              }
            ]
          },
          {
            "plan": "super",
            "price": 39.9,
            "clients": [
              {
                "name": "Helen",
                "documents": ["555.412.744-88", "MG45127889"]
              }
            ]
          },
          {
            "plan": "basic",
            "price": 19.9,
            "clients": [
              {
                "name": "Rose",
                "documents": ["124.111.458-44", "RJ41775652"]
              },
              {
                "name": "Paul",
                "documents": ["122.225.974-87", "SC41257896"]
              }
            ]
          }
        ]
      },
      "children": [{
        "_:component": "layout:column",
        "children": [
          {
            "_:component": "material:text",
            "id": "header",
            "properties": {
              "text": "Documents of clients for the @{item.plan} plan (@{item.price}):"
            }
          },
          {
            "_:component": "forEach",
            "properties": {
              "items": "@{item.clients}"
            },
            "children": [
              {
                "_:component": "forEach",
                "properties": {
                  "items": "@{item.documents}",
                  "iteratorName": "document"
                },
                "children": [
                  {
                    "_:component": "material:text",
                    "id": "document",
                    "properties": {
                      "text": "@{document} (belonging to @{item.name})"
                    }
                  }
                ]
              }
            ]
          }
        ]
      }]
    }
  ]
}"""
