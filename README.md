# QuoraReactiveApp

A fully reactive, event-driven Q&A platform inspired by Quora, built with Spring WebFlux, Apache Kafka, MongoDB, and Elasticsearch. The system is designed around non-blocking, asynchronous processing to handle high concurrency without thread exhaustion.

---

## Table of Contents

1. [Technology Stack](#technology-stack)
2. [Core Concepts](#core-concepts)
3. [Domain Model](#domain-model)
4. [Features](#features)
   - [Questions](#questions)
   - [Answers](#answers)
   - [Comments](#comments)
   - [Likes & Dislikes](#likes--dislikes)
   - [Tags](#tags)
   - [Users](#users)
   - [Follow System](#follow-system)
   - [Personalized Feed](#personalized-feed)
   - [Full-Text Search](#full-text-search)
   - [View Counting](#view-counting)
5. [Event-Driven Architecture](#event-driven-architecture)
6. [Pagination Strategies](#pagination-strategies)
7. [Data Storage Strategy](#data-storage-strategy)
8. [API Reference](#api-reference)
9. [Validation](#validation)

---

## Technology Stack

| Layer | Technology |
|---|---|
| Runtime | Java 24 |
| Web Framework | Spring WebFlux (reactive, non-blocking) |
| Application Framework | Spring Boot 3.5.4 |
| Primary Database | MongoDB (via Spring Data Reactive MongoDB) |
| Full-Text Search | Elasticsearch (via Spring Data Reactive Elasticsearch) |
| Message Broker | Apache Kafka |
| Build Tool | Gradle |
| Boilerplate Reduction | Lombok |
| Input Validation | Jakarta Bean Validation |

---

## Core Concepts

### Reactive Programming with Project Reactor

Every operation in this system returns a `Mono<T>` (zero-or-one item) or `Flux<T>` (zero-to-many items) — the two reactive types from Project Reactor. This means no operation blocks a thread while waiting for I/O. The event loop handles thousands of concurrent requests with a fixed thread pool, making the system far more efficient than traditional thread-per-request models (Spring MVC).

### Non-Blocking I/O End-to-End

All database drivers (MongoDB, Elasticsearch) and the HTTP layer are fully reactive. A request arriving at an endpoint flows through the system without ever occupying a thread while waiting for disk or network. This is fundamentally different from `CompletableFuture` or `@Async` — it is truly non-blocking from HTTP ingress to database egress.

### Event-Driven Decoupling via Kafka

Operations that do not need to complete before responding to the client (view counting, feed generation) are decoupled via Apache Kafka. The producer publishes an event and returns immediately; a separate consumer processes the event asynchronously. This keeps API response latency low and ensures that secondary effects (like fan-out to follower feeds) do not block the request.

### Adapter Pattern for DTO Separation

Every entity has a strict separation between its persistence representation and its API representation. Dedicated adapter components handle bidirectional conversion between the two, ensuring that internal domain changes do not leak into the public API contract.

---

## Domain Model

### Question
The central entity of the platform. A question has a title, body content, an author, and a list of associated tags. It tracks a view count that is updated asynchronously. When a question is created, it is simultaneously persisted in MongoDB, indexed in Elasticsearch for full-text search, and an event is broadcast over Kafka to trigger feed generation.

### Answer
A response to a question authored by a user. Answers are associated with exactly one question and carry their own view counter. Multiple answers per question are supported.

### Comment
A threaded discussion layer that can be attached to a **Question**, an **Answer**, or another **Comment**. The `targetType` discriminator field (`QUESTION`, `ANSWER`, `COMMENT`) determines what is being commented on. This enables arbitrarily deep comment threads without separate collection per entity type.

### Like
A reaction entity that supports both **likes** and **dislikes** (the `isLike` boolean field), applicable to either a `QUESTION` or an `ANSWER`. The `likeType` discriminator field makes likes polymorphic across entity types.

### Tag
A categorization label for questions. Tags track their own `usageCount`, which is atomically incremented when a question referencing that tag is created and decremented when the question is deleted. Tag names are enforced as globally unique.

### User
Represents a platform member. Each user document maintains denormalized `followerCount` and `followingCount` counters that are updated atomically whenever a follow relationship is created, avoiding expensive aggregation queries at read time.

### Follow
An explicit directed-graph edge representing that one user subscribes to another user's content. The system prevents self-follows via validation. Creating a follow atomically updates the follower count on the followed user and the following count on the following user.

### UserFeed
A materialized, pre-computed feed entry. When a user publishes a question, the system fans out by creating one `UserFeed` document per follower of the author. This is the **push model** (also called fan-out-on-write) for feed delivery, as opposed to the pull model (fan-out-on-read). Reading a feed becomes a simple indexed lookup rather than a complex join across follows and questions.

---

## Features

### Questions

**Creating a Question** triggers a cascade of operations executed reactively:
1. The question is persisted to MongoDB.
2. The question is indexed to Elasticsearch for full-text search.
3. A `QuestionCreatedEvent` is published to the `question-created-topic` Kafka topic.
4. The `usageCount` on each referenced tag is atomically incremented.

All four operations are composed reactively. The Elasticsearch indexing and tag updates run in parallel (via `Mono.zip`) to minimize latency.

**Deleting a Question** is also a composite operation:
1. The question document is removed from MongoDB.
2. The question document is removed from the Elasticsearch index.
3. All `UserFeed` entries that reference this question are deleted (feed cleanup).
4. The `usageCount` on each referenced tag is decremented.

**Enrichment**: Question responses are always enriched — the raw `tagIds` and `createdById` stored in MongoDB are resolved to full `Tag` objects and a `User` object before the response is serialized. This happens reactively without N+1 query problems via batched lookups.

**Tag Filtering** supports three modes:
- **Single tag**: Questions matching exactly one specified tag.
- **ANY tags (OR)**: Questions that contain at least one of the specified tags.
- **ALL tags (AND)**: Questions that contain every one of the specified tags.

### Answers

Users can submit answers to any question. Each answer is associated with its creator and enriched with user details on retrieval. All answers for a given question can be fetched as a reactive stream (`Flux`), enabling streaming delivery to clients.

### Comments

Comments use a **polymorphic target model** — a single comment entity can reference a question, an answer, or another comment via the `targetId` and `targetType` fields. This avoids schema duplication while supporting nested discussion threads.

### Likes & Dislikes

The like system is **dual-mode**: the `isLike` boolean flag distinguishes a like from a dislike. Like entities carry a `likeType` discriminator (`QUESTION` or `ANSWER`) enabling a single collection to serve reactions across multiple entity types. Each like response is enriched with the reacting user's profile data.

### Tags

Tags are first-class entities with enforced global uniqueness. The `usageCount` field provides a lightweight way to surface popular or trending topics without an aggregation pipeline. Tag search supports partial name matching (`contains` semantics), making them easily discoverable during question authoring.

### Users

Users are created with unique username and email constraints enforced at the database index level. The user profile includes denormalized aggregate counters (`followerCount`, `followingCount`) that are maintained in real-time as follow relationships are created.

### Follow System

The follow system models a directed social graph:
- **Follower**: A user who subscribes to another user's content.
- **Following**: The user whose content is subscribed to.

Creating a follow relationship atomically updates both users' counter fields using reactive chaining (`flatMap`). Self-following is rejected at the service layer before any persistence occurs. The system supports querying all followers of a user and all users a given user is following.

### Personalized Feed

The feed system uses the **fan-out-on-write (push) model**:

1. When a question is created, a `QuestionCreatedEvent` is published to Kafka.
2. A Kafka consumer picks up the event, fetches all followers of the question's author from MongoDB.
3. For each follower, a `UserFeed` document is written that links the follower's userId to the questionId, preserving the original `questionCreatedAt` timestamp.
4. When a user requests their feed, the system performs a simple indexed read on `UserFeed` sorted by `questionCreatedAt` descending, then resolves each question (with full enrichment) in a reactive pipeline.

The result is a personalized, chronologically ordered stream of questions from people the user follows. Feed entries are automatically cleaned up when a question is deleted.

**Trade-off awareness**: The push model provides O(1) read latency at the cost of write amplification proportional to follower count. This trade-off favors read-heavy workloads typical of social feeds.

### Full-Text Search

Two search mechanisms are provided:

**MongoDB Regex Search**: Case-insensitive pattern matching against question titles and content. Suitable for prefix and substring matching but does not support relevance ranking. Offset-based pagination is applied.

**Elasticsearch Full-Text Search**: The Elasticsearch integration uses a `ReactiveElasticsearchRepository`, keeping the entire search path non-blocking. Questions are indexed with their title, content, and author. A manual sync endpoint allows bulk re-indexing of all MongoDB documents to Elasticsearch, useful for backfilling or recovering after index loss.

### View Counting

Each time a question is retrieved, a `ViewCountEvent` is asynchronously published to the `view-count-topic` Kafka topic. A consumer processes these events and increments the `views` counter on the corresponding question document. Because view counting is fire-and-forget (non-critical path), the Kafka decoupling ensures that view counting failures or slowdowns never affect read API latency.

---

## Event-Driven Architecture

### Kafka Topics

| Topic | Purpose | Producer | Consumer |
|---|---|---|---|
| `view-count-topic` | Asynchronous view count increments | Question read endpoint | View count updater |
| `question-created-topic` | Trigger feed fan-out on new question | Question create endpoint | Feed generator |

### Event Flow: Feed Generation

```
POST /api/questions
       │
       ▼
  QuestionService.createQuestion()
       │
       ├──► MongoDB: save question
       ├──► Elasticsearch: index question
       ├──► Tag: increment usageCount
       └──► Kafka: publish QuestionCreatedEvent
                         │
                         ▼
              KafkaEventConsumer (question-created-topic)
                         │
                         ▼
              Fetch all followers of authorId
                         │
                         ▼ (for each follower)
              MongoDB: save UserFeed { userId, questionId, authorId, questionCreatedAt }
```

### Event Flow: View Counting

```
GET /api/questions/{id}
       │
       ▼
  QuestionService.findQuestionById()
       │
       ├──► MongoDB: fetch question (blocking on result)
       └──► Kafka: publish ViewCountEvent (fire-and-forget)
                         │
                         ▼
              KafkaEventConsumer (view-count-topic)
                         │
                         ▼
              MongoDB: increment question.views by 1
```

### Kafka Consumer Design

Kafka listeners run in an inherently imperative, thread-based context — they are not reactive. To bridge this boundary, the consumers call `.subscribe()` on reactive `Mono`/`Flux` chains, which schedules the reactive work on the Project Reactor scheduler without blocking the Kafka consumer thread. Each consumer group is configured with a concurrency of 5 threads for parallel partition processing.

Deserialization uses `JsonDeserializer` with type headers, scoped to the trusted package `com.example.QuoraReactiveApp.events` to prevent deserialization of arbitrary class types (a security concern with polymorphic Kafka deserialization).

---

## Pagination Strategies

The system implements two distinct pagination strategies, each suited to different access patterns.

### Offset-Based Pagination

Applied to: Users, Tags, Comments, Answers, Questions list, Feed.

Uses a `page` (zero-indexed) and `size` parameter, translated to Spring Data's `PageRequest`. Simple to implement and allows jumping to any page, but performance degrades on large offsets because the database must skip over all preceding documents.

### Cursor-Based Pagination

Applied to: Questions cursor endpoint.

Uses a `cursor` parameter — an ISO-8601 timestamp representing the `createdAt` value of the last seen item. The query fetches records with `createdAt` greater than the cursor value, ordered ascending. This approach has **O(log n)** performance regardless of how deep into the result set you paginate, because it uses an indexed range scan rather than a skip. It does not support jumping to arbitrary pages but is significantly more efficient for infinite-scroll or streaming use cases.

---

## Data Storage Strategy

### MongoDB — Primary Persistence

MongoDB stores all domain entities. The reactive driver (`spring-data-mongodb` reactive) provides fully non-blocking query execution. Indexes are auto-created from entity annotations:

- **Unique indexes**: `user.username`, `user.email`, `tag.name`
- **Non-unique indexes**: `question.tagIds` (for tag filtering), `answer.questionId`, `answer.createdAt`, `userFeed.userId`, `userFeed.questionId`

MongoDB's document model is exploited for the polymorphic comment and like designs — the `targetType` discriminator field avoids the join complexity that would be required in a relational model.

### Elasticsearch — Full-Text Search Index

Elasticsearch serves as a secondary, read-optimized index for full-text question search. It is kept in sync with MongoDB through two mechanisms:
- **Real-time**: Each question creation also writes to the Elasticsearch index.
- **Bulk**: A dedicated sync endpoint pushes the entire MongoDB question collection to Elasticsearch, enabling manual recovery or initial population.

The Elasticsearch integration uses `ReactiveElasticsearchRepository`, maintaining the non-blocking property of the entire stack.

### Denormalization Strategy

Rather than relying on joins (which do not exist in document databases), the system stores foreign key references (`createdById`, `tagIds`, `questionId`) and resolves them at read time via batched reactive lookups. Frequently accessed aggregates (`followerCount`, `followingCount`, `usageCount`) are denormalized directly onto the parent document and maintained through explicit increment/decrement operations, avoiding expensive aggregation pipelines on hot read paths.

---

## API Reference

All endpoints are prefixed with `/api`. Responses are `application/json`. All list endpoints return a reactive stream.

### Questions — `/api/questions`

| Method | Path | Description | Query Params |
|---|---|---|---|
| `POST` | `/` | Create a question | — |
| `GET` | `/{id}` | Get question by ID | — |
| `GET` | `/` | List all questions | — |
| `DELETE` | `/{id}` | Delete question | — |
| `GET` | `/search` | Regex full-text search | `query`, `offset`, `pageSize` |
| `GET` | `/cursor` | Cursor-based pagination | `cursor` (ISO-8601), `size` |
| `GET` | `/tag/{tagId}` | Filter by single tag | `page`, `size` |
| `GET` | `/tags/any` | Filter by ANY of given tags | `tagIds` (comma-separated), `page`, `size` |
| `GET` | `/tags/all` | Filter by ALL of given tags | `tagIds` (comma-separated), `page`, `size` |
| `GET` | `/elasticsearch` | Elasticsearch full-text search | `query` |
| `GET` | `/sync-elasticsearch` | Bulk sync MongoDB → Elasticsearch | — |

**Request body (POST)**:
```json
{
  "title": "string (10-100 chars)",
  "content": "string (10-1000 chars)",
  "tagIds": ["string"],
  "createdById": "string"
}
```

---

### Answers — `/api/answers`

| Method | Path | Description |
|---|---|---|
| `POST` | `/` | Create an answer |
| `GET` | `/{id}` | Get answer by ID |
| `GET` | `/question/{questionId}` | Get all answers for a question |

**Request body (POST)**:
```json
{
  "content": "string (10-1000 chars)",
  "questionId": "string",
  "createdById": "string"
}
```

---

### Comments — `/api/comments`

| Method | Path | Description | Query Params |
|---|---|---|---|
| `POST` | `/` | Create a comment | — |
| `GET` | `/id/{id}` | Get comment by ID | — |
| `GET` | `/` | List all comments | `page`, `size` |

**Request body (POST)**:
```json
{
  "text": "string (2-1000 chars)",
  "targetId": "string",
  "targetType": "QUESTION | ANSWER | COMMENT",
  "createdById": "string"
}
```

---

### Likes — `/api/likes`

| Method | Path | Description |
|---|---|---|
| `POST` | `/` | Create a like or dislike |
| `GET` | `/{id}` | Get like by ID |

**Request body (POST)**:
```json
{
  "targetId": "string",
  "likeType": "QUESTION | ANSWER",
  "isLike": true,
  "createdById": "string"
}
```

---

### Tags — `/api/tags`

| Method | Path | Description | Query Params |
|---|---|---|---|
| `POST` | `/` | Create a tag | — |
| `GET` | `/{id}` | Get tag by ID | — |
| `GET` | `/name/{name}` | Find tags by name (contains) | — |
| `GET` | `/` | List all tags | `page`, `size` |

**Request body (POST)**:
```json
{
  "name": "string (2-50 chars, unique)",
  "description": "string (max 200 chars)"
}
```

---

### Users — `/api/users`

| Method | Path | Description | Query Params |
|---|---|---|---|
| `POST` | `/` | Create a user | — |
| `GET` | `/id/{id}` | Get user by ID | — |
| `GET` | `/` | List all users | `page`, `size` |

**Request body (POST)**:
```json
{
  "username": "string (2-100 chars, unique)",
  "email": "valid email (unique)",
  "bio": "string (max 500 chars)"
}
```

---

### Follows — `/api/follow`

| Method | Path | Description |
|---|---|---|
| `POST` | `/` | Create a follow relationship |
| `GET` | `/id/{id}` | Get follow record by ID |
| `GET` | `/follower/{userId}` | Get all followers of a user |
| `GET` | `/following/{userId}` | Get all users that a user is following |

**Request body (POST)**:
```json
{
  "followerId": "string",
  "followingId": "string"
}
```

> Self-following is rejected with a validation error.

---

### Feed — `/api/feed`

| Method | Path | Description | Query Params |
|---|---|---|---|
| `GET` | `/{userId}` | Get personalized feed for a user | `page`, `size` (default 20) |

Returns a paginated stream of enriched `Question` objects from users that `userId` follows, ordered by question creation time (newest first).

---

## Validation

All incoming request bodies are validated using Jakarta Bean Validation constraints before reaching any service layer. Constraint violations are rejected early with a `400 Bad Request`.

| Entity | Field | Constraint |
|---|---|---|
| Question | `title` | 10–100 characters, required |
| Question | `content` | 10–1000 characters, required |
| Question | `tagIds` | Maximum 10 tags |
| User | `username` | 2–100 characters, unique |
| User | `email` | Valid email format, unique |
| User | `bio` | Maximum 500 characters |
| Answer | `content` | 10–1000 characters, required |
| Comment | `text` | 2–1000 characters, required |
| Tag | `name` | 2–50 characters, unique |
| Tag | `description` | Maximum 200 characters |
| Follow | — | `followerId` ≠ `followingId` (self-follow prevented at service layer) |

---

## Running the Application

### Prerequisites

- Java 24+
- MongoDB running on `localhost:27017`
- Elasticsearch running (default port)
- Apache Kafka running on `localhost:9092`

### Starting

```bash
./gradlew bootRun
```

The application connects to the `search_ahead_db` MongoDB database. Auto-index creation is enabled — MongoDB indexes defined in entity annotations are created automatically on startup.

---

## Future Roadmap

- Strategy pattern for Kafka view-count processing (pluggable increment strategies)
- View counts on answers and comments
- Like toggle and aggregate count endpoints
- Authentication and authorization layer
