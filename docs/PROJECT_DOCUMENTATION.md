## Digital Hub Pro – Project Documentation

### 1. Objectives & Scope
Digital Hub Pro is a YouTube-like platform enabling users to upload, watch, and interact with videos. The app addresses learning and community engagement use-cases by providing reactions, threaded comments, subscriptions, and recommendations.

Scope:
- Frontend: HTML/CSS/JS with Thymeleaf templates (responsive UI)
- Backend: Spring Boot (controllers/services), Spring Security, MongoDB
- Database: MongoDB collections for members, videos, comments, reactions, subscriptions
- Features: Upload, search, reactions, comments+replies, subscriptions, related videos, basic notifications (extensible)

### 2. Architecture Diagram
Client (Browser: HTML/CSS/JS) → Spring Boot Controllers → Services → MongoDB

- Frontend: Thymeleaf templates, static JS/CSS
- Backend: Spring MVC, Services, Repositories
- Database: MongoDB

### 3. Database Schema
Entities:
- Users (Members): id, name, email, password, roles, avatarPath
- Videos: id, title, description, videoUrl, thumbnailUrl, tags, category, uploaderId, uploadDate, viewCount, likeCount, durationSeconds
- Comments: id, videoId, author, text, timestamp, likeCount, dislikeCount, parentCommentId, depth
- Reactions: id, videoId, userId, type (LIKE/LOVE/SAD/WOW), createdAt
- Subscriptions: id, subscriberId, channelId, createdAt
- Playlists (planned): id, ownerId, title, description, videoIds[], createdAt
- Notifications (planned): id, userId, type, payload, read, createdAt

Table/Collection definitions (MongoDB):
- Primary key: `_id` (String)
- Foreign keys by id reference: `uploaderId` → Users, `videoId` → Videos
- Indexes: compound unique on Reactions(videoId,userId), Subscriptions(subscriberId,channelId)

### 4. Features
Core pages (views under `templates/user`):
- Login (`templates/authentication/login.html`): user login
- Home (`templates/user/home.html`): search, list videos
- Add Video (`templates/user/addVideos.html`): upload form (video + thumbnail)
- List Videos (`templates/user/listVideos.html`): list with actions
- Watch Video (`templates/user/showVideos.html` and `watchVideo.html`): play video, reactions, comments

Functional Highlights:
- Reactions API to set/remove and get counts
- Threaded comments with replies; endpoints to add comment or reply, list replies
- Subscriptions: subscribe/unsubscribe channels, view subscriber counts
- Related videos listing; view counts increment endpoint

### 5. Validation & Security
- Validation: `@Valid` on upload forms; server-side checks for video file presence
- Security: Spring Security form login with BCrypt; roles supported
- JWT: architecture ready for JWT token endpoints and filters (future work)
- Protection: server-side validation, DTOs for inputs, escaping output in views to reduce XSS

### 6. Testing
Manual test cases (sample):

| Test Case | Input | Expected Output | Result |
| --- | --- | --- | --- |
| Upload Video (valid) | title+file | 201, listed in `list-video` | Pass |
| Upload Video (no file) | title only | Validation error | Pass |
| Search Videos | keyword in title | Matching list | Pass |
| React to Video | type=LIKE | Counts increase | Pass |
| Comment Reply | parentCommentId set | Reply appears under parent | Pass |

### 7. Task Division
| Member | Contribution |
| --- | --- |
| Developer A | Backend controllers/services |
| Developer B | Frontend templates and CSS |
| Developer C | Database modeling/indexes |
| Developer D | Testing and documentation |

### 8. Reflection
Lessons learned: meaningful domain modeling and careful incremental feature additions help maintain stability. Challenges included handling authentication context across controllers and ensuring MongoDB indexes for uniqueness constraints.

### 9. References
- Fowler, M. (2002) Patterns of Enterprise Application Architecture. Addison-Wesley.
- Johnson, R. et al. (2016) Spring Framework Reference. Pivotal.
- MongoDB Inc. (2024) MongoDB Manual. Available at: `https://www.mongodb.com/docs/` (Accessed: 26 September 2025).
- OWASP Foundation (2024) OWASP Top Ten. Available at: `https://owasp.org/www-project-top-ten/` (Accessed: 26 September 2025).

### 10. README
See root `README.md` and controller-specific READMEs for endpoint details.


