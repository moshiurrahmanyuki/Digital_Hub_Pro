## Digital Hub Pro

Digital Hub Pro is a YouTube-like video platform built with Spring Boot and MongoDB. It supports video upload and playback, search, comments with threaded replies, reactions (👍 ❤️ 😢 😮), subscriptions, and more.

### Features
- Video upload with thumbnails
- Search by title/description/tags
- Reactions: Like, Love, Sad, Wow
- Comments and threaded replies
- Related videos and view counts
- Subscriptions (subscribe/unsubscribe channels)
- Authentication with Spring Security (form login), ready for JWT extension
- Responsive pages (templates under `src/main/resources/templates/user`)

### Tech Stack
- Backend: Spring Boot, Spring Security, Spring Data MongoDB
- Database: MongoDB
- Frontend: Thymeleaf templates, HTML/CSS/JS

### Getting Started
1. Prerequisites: JDK 17+, Maven, MongoDB running locally
2. Configure `src/main/resources/application.properties` for your MongoDB connection
3. Build and run:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Visit `http://localhost:8080/login`

### Project Structure
- `src/main/java/.../authentication` authentication and member management
- `src/main/java/.../user` video domain (controllers, models, services)
- `src/main/resources/templates` Thymeleaf views
- `src/main/resources/static` static assets

### API Highlights
- `POST /api/videos/{videoId}/reactions?type=LIKE|LOVE|SAD|WOW`
- `DELETE /api/videos/{videoId}/reactions`
- `GET /api/videos/{videoId}/reactions/counts`
- `POST /api/videos/{videoId}/comments` (supports `parentCommentId` for replies)
- `GET /api/videos/{videoId}/comments/{commentId}/replies`
- `POST /api/subscriptions/channels/{channelId}`
- `DELETE /api/subscriptions/channels/{channelId}`
- `GET /api/subscriptions`

### Development Notes
- Do not remove existing code; extend and comment old lines if updates are necessary.
- MongoDB collections are auto-created on first save.

### License
Proprietary - internal academic/learning project usage.


