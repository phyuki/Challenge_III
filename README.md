# Challenge III
Development of a real-time RESTful API using Spring Boot for handling data from an external API

This application asynchronously fetches posts from an external API, enriches them with comment data, and keeps a log of processing updates. The client will then be able to search for posts and the history of states through the API immediately.

## Implemented features
* CRUD implementation
* Async fetch (with OpenFeign to consume data from API)
* OpenAPI (using Swagger)  
* ProblemDetails -> Global Exception Handler

## API documentation
From the endpoint **/swagger-ui/index.html.**, you can access the API documentation. It has the default attributes provided by the OpenAPI Specification through Swagger framework.
