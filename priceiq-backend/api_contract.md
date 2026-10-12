{
  "openapi": "3.1.0",
  "info": {
    "title": "PriceIQ API",
    "description": "Amazon-like Ecommerce Platform with ML-powered Recommendation and Dynamic Pricing",
    "contact": {
      "name": "PriceIQ Team",
      "email": "support@priceiq.com"
    },
    "license": {
      "name": "Apache 2.0",
      "url": "https://www.apache.org/licenses/LICENSE-2.0"
    },
    "version": "1.0.0"
  },
  "externalDocs": {
    "description": "PriceIQ Project Documentation",
    "url": "https://priceiq-docs.com"
  },
  "servers": [
    {
      "url": "http://localhost:8081",
      "description": "Generated server url"
    }
  ],
  "security": [
    {
      "bearerAuth": []
    }
  ],
  "paths": {
    "/api/v1/products/{productId}": {
      "get": {
        "tags": [
          "product-controller"
        ],
        "operationId": "getProductById",
        "parameters": [
          {
            "name": "productId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/ProductResponse"
                }
              }
            }
          }
        }
      },
      "put": {
        "tags": [
          "product-controller"
        ],
        "operationId": "updateProduct",
        "parameters": [
          {
            "name": "productId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          }
        ],
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/ProductRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/ProductResponse"
                }
              }
            }
          }
        }
      },
      "delete": {
        "tags": [
          "product-controller"
        ],
        "operationId": "deleteProduct",
        "parameters": [
          {
            "name": "productId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK"
          }
        }
      }
    },
    "/api/v1/cart/update-quantity/{cartItemId}": {
      "put": {
        "tags": [
          "cart-controller"
        ],
        "operationId": "updateQuantity",
        "parameters": [
          {
            "name": "cartItemId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          },
          {
            "name": "quantity",
            "in": "query",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int32"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/CartResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/wishlist/add": {
      "post": {
        "tags": [
          "wishlist-controller"
        ],
        "operationId": "addToWishlist",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/WishlistRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/WishlistResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/reviews": {
      "post": {
        "tags": [
          "review-controller"
        ],
        "operationId": "createReview",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/ReviewRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/ReviewResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/products": {
      "get": {
        "tags": [
          "product-controller"
        ],
        "operationId": "getAllProducts",
        "parameters": [
          {
            "name": "page",
            "in": "query",
            "required": false,
            "schema": {
              "type": "integer",
              "format": "int32",
              "default": 0
            }
          },
          {
            "name": "size",
            "in": "query",
            "required": false,
            "schema": {
              "type": "integer",
              "format": "int32",
              "default": 10
            }
          },
          {
            "name": "category",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "brand",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "minPrice",
            "in": "query",
            "required": false,
            "schema": {
              "type": "number",
              "format": "double"
            }
          },
          {
            "name": "maxPrice",
            "in": "query",
            "required": false,
            "schema": {
              "type": "number",
              "format": "double"
            }
          },
          {
            "name": "sortBy",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/PageProductResponse"
                }
              }
            }
          }
        }
      },
      "post": {
        "tags": [
          "product-controller"
        ],
        "operationId": "createProduct",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/ProductRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/ProductResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/payments": {
      "post": {
        "tags": [
          "payment-controller"
        ],
        "operationId": "processPayment",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/PaymentRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/PaymentResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/orders": {
      "post": {
        "tags": [
          "order-controller"
        ],
        "operationId": "createOrder",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/OrderRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/OrderResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/events": {
      "post": {
        "tags": [
          "event-controller"
        ],
        "operationId": "trackEvent",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/EventRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/GenericSuccessResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/cart/add": {
      "post": {
        "tags": [
          "cart-controller"
        ],
        "operationId": "addToCart",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/CartRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/CartResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/auth/register": {
      "post": {
        "tags": [
          "auth-controller"
        ],
        "operationId": "register",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/RegisterRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/AuthResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/auth/refresh-token": {
      "post": {
        "tags": [
          "auth-controller"
        ],
        "operationId": "refreshToken",
        "parameters": [
          {
            "name": "Authorization",
            "in": "header",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/AuthResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/auth/login": {
      "post": {
        "tags": [
          "auth-controller"
        ],
        "operationId": "login",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/LoginRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/AuthResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/images/upload": {
      "post": {
        "tags": [
          "image-controller"
        ],
        "operationId": "uploadImage",
        "requestBody": {
          "content": {
            "multipart/form-data": {
              "schema": {
                "type": "object",
                "properties": {
                  "file": {
                    "type": "string",
                    "format": "binary"
                  }
                },
                "required": [
                  "file"
                ]
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/ImageUploadResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/wishlist/{userId}": {
      "get": {
        "tags": [
          "wishlist-controller"
        ],
        "operationId": "getWishlist",
        "parameters": [
          {
            "name": "userId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "array",
                  "items": {
                    "$ref": "#/components/schemas/WishlistResponse"
                  }
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/reviews/product/{productId}": {
      "get": {
        "tags": [
          "review-controller"
        ],
        "operationId": "getReviewsByProduct",
        "parameters": [
          {
            "name": "productId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/recommendations/feed": {
      "get": {
        "tags": [
          "recommendation-controller"
        ],
        "operationId": "getFeed",
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "array",
                  "items": {
                    "$ref": "#/components/schemas/ProductResponse"
                  }
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/products/search": {
      "get": {
        "tags": [
          "product-controller"
        ],
        "operationId": "searchProducts",
        "parameters": [
          {
            "name": "keyword",
            "in": "query",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "page",
            "in": "query",
            "required": false,
            "schema": {
              "type": "integer",
              "format": "int32",
              "default": 0
            }
          },
          {
            "name": "size",
            "in": "query",
            "required": false,
            "schema": {
              "type": "integer",
              "format": "int32",
              "default": 10
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/PageProductResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/payments/{paymentId}": {
      "get": {
        "tags": [
          "payment-controller"
        ],
        "operationId": "getPaymentDetails",
        "parameters": [
          {
            "name": "paymentId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/PaymentResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/orders/{orderId}": {
      "get": {
        "tags": [
          "order-controller"
        ],
        "operationId": "getOrderById",
        "parameters": [
          {
            "name": "orderId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/OrderResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/orders/user/{userId}": {
      "get": {
        "tags": [
          "order-controller"
        ],
        "operationId": "getOrderHistory",
        "parameters": [
          {
            "name": "userId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/cart/{userId}": {
      "get": {
        "tags": [
          "cart-controller"
        ],
        "operationId": "getCart",
        "parameters": [
          {
            "name": "userId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/CartResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/analytics/trending-products": {
      "get": {
        "tags": [
          "analytics-controller"
        ],
        "operationId": "getTrendingProducts",
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/analytics/dashboard": {
      "get": {
        "tags": [
          "analytics-controller"
        ],
        "operationId": "getDashboardAnalytics",
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/AnalyticsResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/analytics/conversion-rate": {
      "get": {
        "tags": [
          "analytics-controller"
        ],
        "operationId": "getConversionRate",
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/wishlist/remove/{productId}": {
      "delete": {
        "tags": [
          "wishlist-controller"
        ],
        "operationId": "removeFromWishlist",
        "parameters": [
          {
            "name": "userId",
            "in": "query",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          },
          {
            "name": "productId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/WishlistResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/cart/remove/{cartItemId}": {
      "delete": {
        "tags": [
          "cart-controller"
        ],
        "operationId": "removeFromCart",
        "parameters": [
          {
            "name": "cartItemId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/CartResponse"
                }
              }
            }
          }
        }
      }
    },
    "/api/v1/cart/clear/{userId}": {
      "delete": {
        "tags": [
          "cart-controller"
        ],
        "operationId": "clearCart",
        "parameters": [
          {
            "name": "userId",
            "in": "path",
            "required": true,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK"
          }
        }
      }
    }
  },
  "components": {
    "schemas": {
      "ProductImageRequest": {
        "type": "object",
        "properties": {
          "imageUrl": {
            "type": "string"
          },
          "primaryImage": {
            "type": "boolean"
          },
          "displayOrder": {
            "type": "integer",
            "format": "int32"
          },
          "altText": {
            "type": "string"
          }
        }
      },
      "ProductRequest": {
        "type": "object",
        "properties": {
          "name": {
            "type": "string"
          },
          "description": {
            "type": "string"
          },
          "sku": {
            "type": "string"
          },
          "brand": {
            "type": "string"
          },
          "category": {
            "type": "string"
          },
          "basePrice": {
            "type": "number"
          },
          "currentPrice": {
            "type": "number"
          },
          "stockQuantity": {
            "type": "integer",
            "format": "int32"
          },
          "active": {
            "type": "boolean"
          },
          "images": {
            "type": "array",
            "items": {
              "$ref": "#/components/schemas/ProductImageRequest"
            }
          },
          "attributes": {
            "type": "object",
            "additionalProperties": {
              "type": "string"
            }
          }
        }
      },
      "ProductImageResponse": {
        "type": "object",
        "properties": {
          "id": {
            "type": "integer",
            "format": "int64"
          },
          "imageUrl": {
            "type": "string"
          },
          "primaryImage": {
            "type": "boolean"
          },
          "displayOrder": {
            "type": "integer",
            "format": "int32"
          },
          "altText": {
            "type": "string"
          }
        }
      },
      "ProductResponse": {
        "type": "object",
        "properties": {
          "id": {
            "type": "integer",
            "format": "int64"
          },
          "name": {
            "type": "string"
          },
          "description": {
            "type": "string"
          },
          "sku": {
            "type": "string"
          },
          "brand": {
            "type": "string"
          },
          "category": {
            "type": "string"
          },
          "basePrice": {
            "type": "number"
          },
          "currentPrice": {
            "type": "number"
          },
          "stockQuantity": {
            "type": "integer",
            "format": "int32"
          },
          "averageRating": {
            "type": "number",
            "format": "double"
          },
          "totalReviews": {
            "type": "integer",
            "format": "int32"
          },
          "active": {
            "type": "boolean"
          },
          "images": {
            "type": "array",
            "items": {
              "$ref": "#/components/schemas/ProductImageResponse"
            }
          },
          "attributes": {
            "type": "object",
            "additionalProperties": {
              "type": "string"
            }
          }
        }
      },
      "CartItemResponse": {
        "type": "object",
        "properties": {
          "cartItemId": {
            "type": "integer",
            "format": "int64"
          },
          "productId": {
            "type": "integer",
            "format": "int64"
          },
          "productName": {
            "type": "string"
          },
          "productImage": {
            "type": "string"
          },
          "quantity": {
            "type": "integer",
            "format": "int32"
          },
          "priceAtAddition": {
            "type": "number"
          },
          "totalPrice": {
            "type": "number"
          }
        }
      },
      "CartResponse": {
        "type": "object",
        "properties": {
          "cartId": {
            "type": "integer",
            "format": "int64"
          },
          "userId": {
            "type": "integer",
            "format": "int64"
          },
          "items": {
            "type": "array",
            "items": {
              "$ref": "#/components/schemas/CartItemResponse"
            }
          },
          "totalAmount": {
            "type": "number"
          }
        }
      },
      "WishlistRequest": {
        "type": "object",
        "properties": {
          "userId": {
            "type": "integer",
            "format": "int64"
          },
          "productId": {
            "type": "integer",
            "format": "int64"
          }
        },
        "required": [
          "productId",
          "userId"
        ]
      },
      "WishlistResponse": {
        "type": "object",
        "properties": {
          "wishlistId": {
            "type": "integer",
            "format": "int64"
          },
          "userId": {
            "type": "integer",
            "format": "int64"
          },
          "productId": {
            "type": "integer",
            "format": "int64"
          },
          "productName": {
            "type": "string"
          },
          "productImage": {
            "type": "string"
          },
          "brand": {
            "type": "string"
          },
          "category": {
            "type": "string"
          }
        }
      },
      "ReviewRequest": {
        "type": "object",
        "properties": {
          "userId": {
            "type": "integer",
            "format": "int64"
          },
          "productId": {
            "type": "integer",
            "format": "int64"
          },
          "rating": {
            "type": "integer",
            "format": "int32",
            "maximum": 5,
            "minimum": 1
          },
          "comment": {
            "type": "string"
          }
        },
        "required": [
          "productId",
          "rating",
          "userId"
        ]
      },
      "ReviewResponse": {
        "type": "object",
        "properties": {
          "reviewId": {
            "type": "integer",
            "format": "int64"
          },
          "userId": {
            "type": "integer",
            "format": "int64"
          },
          "productId": {
            "type": "integer",
            "format": "int64"
          },
          "userName": {
            "type": "string"
          },
          "rating": {
            "type": "integer",
            "format": "int32"
          },
          "comment": {
            "type": "string"
          },
          "verifiedPurchase": {
            "type": "boolean"
          },
          "createdAt": {
            "type": "string",
            "format": "date-time"
          }
        }
      },
      "PaymentRequest": {
        "type": "object",
        "properties": {
          "orderId": {
            "type": "integer",
            "format": "int64"
          },
          "paymentMethod": {
            "type": "string",
            "enum": [
              "CARD",
              "UPI",
              "NET_BANKING",
              "WALLET",
              "CASH_ON_DELIVERY"
            ]
          },
          "transactionId": {
            "type": "string"
          }
        },
        "required": [
          "orderId",
          "paymentMethod",
          "transactionId"
        ]
      },
      "PaymentResponse": {
        "type": "object",
        "properties": {
          "paymentId": {
            "type": "integer",
            "format": "int64"
          },
          "orderId": {
            "type": "integer",
            "format": "int64"
          },
          "transactionId": {
            "type": "string"
          },
          "paymentMethod": {
            "type": "string",
            "enum": [
              "CARD",
              "UPI",
              "NET_BANKING",
              "WALLET",
              "CASH_ON_DELIVERY"
            ]
          },
          "paymentStatus": {
            "type": "string",
            "enum": [
              "PENDING",
              "SUCCESS",
              "FAILED",
              "REFUNDED"
            ]
          },
          "amount": {
            "type": "number"
          },
          "paymentGateway": {
            "type": "string"
          },
          "createdAt": {
            "type": "string",
            "format": "date-time"
          }
        }
      },
      "OrderItemRequest": {
        "type": "object",
        "properties": {
          "productId": {
            "type": "integer",
            "format": "int64"
          },
          "quantity": {
            "type": "integer",
            "format": "int32",
            "minimum": 1
          }
        },
        "required": [
          "productId",
          "quantity"
        ]
      },
      "OrderRequest": {
        "type": "object",
        "properties": {
          "userId": {
            "type": "integer",
            "format": "int64"
          },
          "items": {
            "type": "array",
            "items": {
              "$ref": "#/components/schemas/OrderItemRequest"
            }
          },
          "shippingAddress": {
            "type": "string"
          },
          "city": {
            "type": "string"
          },
          "state": {
            "type": "string"
          },
          "country": {
            "type": "string"
          },
          "postalCode": {
            "type": "string"
          },
          "couponCode": {
            "type": "string"
          }
        },
        "required": [
          "city",
          "country",
          "items",
          "postalCode",
          "shippingAddress",
          "state",
          "userId"
        ]
      },
      "OrderItemResponse": {
        "type": "object",
        "properties": {
          "orderItemId": {
            "type": "integer",
            "format": "int64"
          },
          "productId": {
            "type": "integer",
            "format": "int64"
          },
          "productName": {
            "type": "string"
          },
          "quantity": {
            "type": "integer",
            "format": "int32"
          },
          "pricePerUnit": {
            "type": "number"
          },
          "totalPrice": {
            "type": "number"
          }
        }
      },
      "OrderResponse": {
        "type": "object",
        "properties": {
          "orderId": {
            "type": "integer",
            "format": "int64"
          },
          "userId": {
            "type": "integer",
            "format": "int64"
          },
          "orderNumber": {
            "type": "string"
          },
          "orderStatus": {
            "type": "string",
            "enum": [
              "PENDING",
              "CONFIRMED",
              "PROCESSING",
              "SHIPPED",
              "DELIVERED",
              "CANCELLED",
              "RETURNED"
            ]
          },
          "subtotal": {
            "type": "number"
          },
          "shippingCharge": {
            "type": "number"
          },
          "taxAmount": {
            "type": "number"
          },
          "discountAmount": {
            "type": "number"
          },
          "totalAmount": {
            "type": "number"
          },
          "shippingAddress": {
            "type": "string"
          },
          "city": {
            "type": "string"
          },
          "state": {
            "type": "string"
          },
          "country": {
            "type": "string"
          },
          "postalCode": {
            "type": "string"
          },
          "trackingNumber": {
            "type": "string"
          },
          "items": {
            "type": "array",
            "items": {
              "$ref": "#/components/schemas/OrderItemResponse"
            }
          },
          "createdAt": {
            "type": "string",
            "format": "date-time"
          }
        }
      },
      "EventRequest": {
        "type": "object",
        "properties": {
          "userId": {
            "type": "integer",
            "format": "int64"
          },
          "eventType": {
            "type": "string",
            "enum": [
              "PRODUCT_VIEW",
              "PRODUCT_CLICK",
              "SEARCH",
              "ADD_TO_CART",
              "REMOVE_FROM_CART",
              "ADD_TO_WISHLIST",
              "REMOVE_FROM_WISHLIST",
              "PURCHASE",
              "REVIEW",
              "LOGIN"
            ]
          },
          "productId": {
            "type": "integer",
            "format": "int64"
          },
          "searchKeyword": {
            "type": "string"
          },
          "sessionId": {
            "type": "string"
          },
          "metadata": {
            "type": "string"
          },
          "timestamp": {
            "type": "string",
            "format": "date-time"
          }
        }
      },
      "GenericSuccessResponse": {
        "type": "object",
        "properties": {
          "success": {
            "type": "boolean"
          },
          "message": {
            "type": "string"
          }
        }
      },
      "CartRequest": {
        "type": "object",
        "properties": {
          "userId": {
            "type": "integer",
            "format": "int64"
          },
          "productId": {
            "type": "integer",
            "format": "int64"
          },
          "quantity": {
            "type": "integer",
            "format": "int32",
            "minimum": 1
          }
        },
        "required": [
          "productId",
          "quantity",
          "userId"
        ]
      },
      "RegisterRequest": {
        "type": "object",
        "properties": {
          "firstName": {
            "type": "string"
          },
          "lastName": {
            "type": "string"
          },
          "email": {
            "type": "string"
          },
          "password": {
            "type": "string",
            "maxLength": 100,
            "minLength": 6
          },
          "phoneNumber": {
            "type": "string"
          }
        },
        "required": [
          "email",
          "firstName",
          "lastName",
          "password"
        ]
      },
      "AuthResponse": {
        "type": "object",
        "properties": {
          "userId": {
            "type": "integer",
            "format": "int64"
          },
          "firstName": {
            "type": "string"
          },
          "lastName": {
            "type": "string"
          },
          "email": {
            "type": "string"
          },
          "role": {
            "type": "string",
            "enum": [
              "USER",
              "ADMIN"
            ]
          },
          "accessToken": {
            "type": "string"
          },
          "refreshToken": {
            "type": "string"
          }
        }
      },
      "LoginRequest": {
        "type": "object",
        "properties": {
          "email": {
            "type": "string"
          },
          "password": {
            "type": "string"
          }
        },
        "required": [
          "email",
          "password"
        ]
      },
      "ImageUploadResponse": {
        "type": "object",
        "properties": {
          "imageUrl": {
            "type": "string"
          }
        }
      },
      "PageProductResponse": {
        "type": "object",
        "properties": {
          "totalElements": {
            "type": "integer",
            "format": "int64"
          },
          "totalPages": {
            "type": "integer",
            "format": "int32"
          },
          "pageable": {
            "$ref": "#/components/schemas/PageableObject"
          },
          "size": {
            "type": "integer",
            "format": "int32"
          },
          "content": {
            "type": "array",
            "items": {
              "$ref": "#/components/schemas/ProductResponse"
            }
          },
          "number": {
            "type": "integer",
            "format": "int32"
          },
          "sort": {
            "$ref": "#/components/schemas/SortObject"
          },
          "numberOfElements": {
            "type": "integer",
            "format": "int32"
          },
          "first": {
            "type": "boolean"
          },
          "last": {
            "type": "boolean"
          },
          "empty": {
            "type": "boolean"
          }
        }
      },
      "PageableObject": {
        "type": "object",
        "properties": {
          "paged": {
            "type": "boolean"
          },
          "pageNumber": {
            "type": "integer",
            "format": "int32"
          },
          "pageSize": {
            "type": "integer",
            "format": "int32"
          },
          "unpaged": {
            "type": "boolean"
          },
          "offset": {
            "type": "integer",
            "format": "int64"
          },
          "sort": {
            "$ref": "#/components/schemas/SortObject"
          }
        }
      },
      "SortObject": {
        "type": "object",
        "properties": {
          "sorted": {
            "type": "boolean"
          },
          "unsorted": {
            "type": "boolean"
          },
          "empty": {
            "type": "boolean"
          }
        }
      },
      "AnalyticsResponse": {
        "type": "object",
        "properties": {
          "totalUsers": {
            "type": "integer",
            "format": "int64"
          },
          "totalOrders": {
            "type": "integer",
            "format": "int64"
          },
          "totalProducts": {
            "type": "integer",
            "format": "int64"
          },
          "totalEvents": {
            "type": "integer",
            "format": "int64"
          },
          "totalRevenue": {
            "type": "number"
          },
          "totalReviews": {
            "type": "integer",
            "format": "int64"
          },
          "trendingProducts": {
            "type": "array",
            "items": {
              "type": "string"
            }
          },
          "ordersByStatus": {
            "type": "object",
            "additionalProperties": {
              "type": "integer",
              "format": "int64"
            }
          },
          "eventsByType": {
            "type": "object",
            "additionalProperties": {
              "type": "integer",
              "format": "int64"
            }
          },
          "conversionRates": {
            "type": "object",
            "additionalProperties": {
              "type": "number",
              "format": "double"
            }
          }
        }
      }
    },
    "securitySchemes": {
      "bearerAuth": {
        "type": "http",
        "name": "bearerAuth",
        "scheme": "bearer",
        "bearerFormat": "JWT"
      }
    }
  }
}

