# SENTINEL AI 🛰️
**High-fidelity YouTube Summarization Platform**

Sentinel is a cloud-native intelligence tool that distills long-form YouTube videos into concise, professional summaries using Google Gemini AI. It features an **asynchronous processing pipeline** with job polling, **Google OAuth authentication**, **RAG-based Q&A** to ask questions about video content, and a **glassmorphic UI**.

---

### 🚀 Key Features

- **🔐 Google OAuth Authentication** — Secure sign-in with Google; personalized history per user.
- **⚡ Asynchronous Processing** — Video summarization runs in the background via Redis Streams (Producer-Consumer pattern). The frontend polls for the job status and displays results when ready.
- **💬 RAG-based Q&A (Ask the Video)** — After summarization, ask natural language questions about the video content. The system uses the transcript as context for Google Gemini to answer accurately.
- **📜 Personalized History** — Every user's past summaries are stored and displayed as "Recently Viewed" cards on the dashboard.
- **📦 Cache-First Architecture** — Already-summarized videos return instantly from PostgreSQL without re-processing.
- **🎨 Modern Glassmorphic UI** — Built with React, Framer Motion, and CSS Glassmorphism for an Apple-style aesthetic.
- **🏗️ Scalable Microservices** — Decoupled Ingestor (REST API + Queue producer) and Worker (queue consumer + AI summarizer) services.

---

### 🛠️ Tech Stack

| Layer       | Technology                                                              |
|-------------|-------------------------------------------------------------------------|
| **Frontend**  | React, React Router, Axios, CSS3 (Glassmorphism)                      |
| **Ingestor**  | Spring Boot, Spring Data JPA, Spring Security, OAuth2 Client, Redis    |
| **Worker**    | Spring Boot, Spring AI, Redis Stream Consumer, YouTube Data API v3     |
| **AI**        | Google Gemini Pro API (via Spring AI)                                  |
| **Storage**   | PostgreSQL (persistence), Redis (message queue & cache)                |
| **Infra**     | Docker Compose (Redis + PostgreSQL)                                    |

---

### 📸 Previews

| Dashboard | AI Summary View |
| :---: | :---: |
|![Dashboard](./assets/sentinel-ai-0.png) |![AI Summary View](./assets/sentinel-ai-2.png)
|![Dashboard](./assets/sentinel-ai-6.png) |![AI Summary View](./assets/sentinel-ai-1.png)
|![Dashboard](./assets/sentinel-ai-5.png) |
|![Dashboard](./assets/sentinel-ai-3.png) |

---

### 🧠 Architecture Overview

```
User → React Frontend → Ingestor (REST API)
                            ↓
                       Redis Stream (Job Queue)
                            ↓
                       Worker (Consumer)
                            ↓
                    ┌───────────┴───────────┐
                    ↓                       ↓
            YouTube Transcript        Google Gemini
            Data API v3               Pro API
                    ↓                       ↓
                    └───────────┬───────────┘
                                ↓
                          PostgreSQL
                        (VideoSummary)
```

**Flow:**
1. User pastes a YouTube URL → frontend calls `POST /api/v1/summarize` → Ingestor pushes a job to Redis Stream.
2. Frontend receives a `jobId` and begins polling `GET /api/v1/status/{jobId}`.
3. Worker consumes the job from Redis → fetches transcript via YouTube API → summarizes with Gemini → saves to PostgreSQL.
4. Frontend polling succeeds → summary is displayed.
5. User can then ask questions via `POST /api/v1/videos/{videoId}/ask` → RagService uses the stored transcript as context for Gemini.

---

### 🏃 Running Locally

#### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- YouTube Data API v3 key
- Google Gemini API key

#### 1. Clone & Configure

```bash
git clone https://github.com/KarthikAdyar/youtube-video-summarizer.git
cd sentinel-ai
```

#### 2. Start Infrastructure (Redis + PostgreSQL)

```bash
docker compose up -d
```

#### 3. Configure Secrets

Copy and fill in the configuration templates:

```bash
cp sentinel-ingestor/src/main/resources/application.yml.template sentinel-ingestor/src/main/resources/application.yml
cp sentinel-worker/src/main/resources/application.yml.template sentinel-worker/src/main/resources/application.yml
```

Set the required values in each `application.yml`:
- **Ingestor:** Google OAuth2 client ID/secret, JWT secret, PostgreSQL credentials, Redis host
- **Worker:** Gemini API key, YouTube API key, PostgreSQL credentials, Redis host

#### 4. Start Backend Services

```bash
# Terminal 1 — Ingestor
cd sentinel-ingestor && ./mvnw spring-boot:run

# Terminal 2 — Worker
cd sentinel-worker && ./mvnw spring-boot:run
```

#### 5. Start Frontend

```bash
cd sentinel-frontend && npm install && npm run dev
```

Open [http://localhost:5173](http://localhost:5173) in your browser.

---

### 📡 API Endpoints

| Method | Path                           | Auth required | Description                                      |
|--------|--------------------------------|---------------|--------------------------------------------------|
| POST   | `/api/v1/summarize`            | Yes           | Submit a YouTube URL for summarization. Returns `jobId`. |
| GET    | `/api/v1/status/{jobId}`       | Yes           | Poll for completed summary by job ID.            |
| GET    | `/api/v1/history`              | Yes           | Get all past summaries for the authenticated user. |
| POST   | `/api/v1/videos/{videoId}/ask` | Yes           | Ask a question about a summarized video (RAG).   |

---

### 📁 Project Structure

```
sentinel-ai/
├── sentinel-frontend/          # React SPA
│   ├── src/
│   │   ├── components/
│   │   │   ├── Auth/           # Login / Register pages
│   │   │   ├── AuthRedirect/   # OAuth callback handler
│   │   │   ├── Home/           # Dashboard with input & history
│   │   │   └── Summary/        # Summary detail + Q&A chat
│   │   ├── services/
│   │   │   ├── api.js          # Axios client with JWT interceptor
│   │   │   └── auth.js         # Token storage / Google OAuth helpers
│   │   └── App.jsx             # React Router setup
│   └── package.json
│
├── sentinel-ingestor/          # Spring Boot REST API
│   ├── src/main/java/...
│   │   ├── controller/
│   │   │   ├── IngestorController.java   # /summarize, /status, /history, /ask
│   │   │   └── AuthController.java       # OAuth endpoints
│   │   ├── rag/
│   │   │   └── RagService.java           # RAG-based Q&A using Gemini
│   │   ├── security/
│   │   │   ├── JwtUtil.java              # JWT generation / validation
│   │   │   ├── OAuth2SuccessHandler.java # Post-login JWT creation
│   │   │   └── OAuth2FailureHandler.java
│   │   ├── config/
│   │   │   └── SecurityConfig.java       # OAuth2 + JWT filter chain
│   │   ├── entity/
│   │   │   ├── User.java                 # JPA entity
│   │   │   └── VideoSummary.java         # JPA entity (videoId, title, summary, transcript, ownerId, jobId)
│   │   ├── service/
│   │   │   └── QueueService.java         # Redis Stream producer
│   │   └── ...
│   └── pom.xml
│
├── sentinel-worker/            # Spring Boot background worker
│   ├── src/main/java/...
│   │   ├── service/
│   │   │   ├── StreamConsumer.java      # Redis Stream consumer (listens for jobs)
│   │   │   ├── SummarizerService.java   # Calls Gemini API for summarization
│   │   │   └── YoutubeService.java      # Fetches transcript via YouTube API
│   │   └── ...
│   └── pom.xml
│
├── docker-compose.yml          # Redis + PostgreSQL
└── README.md
```

---

### 🔑 Environment Variables (application.yml)

| Variable             | Service   | Description                       |
|----------------------|-----------|-----------------------------------|
| `GEMINI_API_KEY`     | Worker    | Google Gemini Pro API key         |
| `YOUTUBE_API_KEY`    | Worker    | YouTube Data API v3 key           |
| `SPRING_DATASOURCE_*`| Both      | PostgreSQL connection details     |
| `SPRING_REDIS_*`     | Both      | Redis connection details          |
| `OAUTH2_CLIENT_ID`   | Ingestor  | Google OAuth client ID            |
| `OAUTH2_CLIENT_SECRET`| Ingestor | Google OAuth client secret        |
| `JWT_SECRET`         | Ingestor  | Secret for signing JWT tokens     |

---

### 📄 License

MIT License — see the [LICENSE](LICENSE) file for details.