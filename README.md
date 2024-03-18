# mainbot

![knowledge-explosion](./docs/images/knowledge-explosion.jpg)

Multi-tenant headless RAG chat bot.  

Currently it's a bit opinionated using OpenAI and Pinecone, but it wouldn't be difficult to include other implementations, or even a provider pattern and just use this framework as a dependency.  (Spring Boot Starter anyone?)  

An implementation for using `Replicate` is already included but currently turned off.  
Some of this code came from [Wanderling](https://wanderling.io), but all the game specific functions and rules-engine has been trimmed out (not ready to open source that one yet)   



## Features

- Namespaced vector storage for multi-tenant preparation
- OAuth authentication via JWT header
- User session isolation
- User session history and continuation
- Adding documents to vector storage with a simple embedding pipeline
- Retrieval Augmented Generation as default chat experience



## Quick start

Copy the `example.env` file to `.env`  
Update the values for openai and pinecone.  

```
make dev
```

open [http://localhost:8080/swagger-ui/index.html](http://localhost:4180/swagger-ui/index.html)


### Swagger


![swagger](./docs/images/swagger.png)


### Starting a session 

![start a session](./docs/images/start-session.png)  


### Sending a message

![send a message](./docs/images/session-message.png)