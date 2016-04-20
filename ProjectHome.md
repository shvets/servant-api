This java library helps to build arbitrary client-server applications. Instead of specifying details of communication protocol, the library makes accent on high level details of communication process.

The communication between the client and the server is specified on general level. It allows to use this library for solving the most of the possible tasks, not losing in efficiency.

The library works with four main concepts: client, server, servant and connection. Client sends requests to a server. For each request the server creates special object - servant, that serves this request. To have multi-threading behavior and to handle all communication specific details the connection object is used as wrapper around servant object.