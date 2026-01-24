## User Controllers – API Reference

### ReactionController
Base: `/api/videos/{videoId}/reactions`
- `POST /` set reaction
  - params: `type=LIKE|LOVE|SAD|WOW`
  - 200: counts per type
- `DELETE /` remove reaction
- `GET /counts` get counts

### SubscriptionController
Base: `/api/subscriptions`
- `POST /channels/{channelId}` subscribe
- `DELETE /channels/{channelId}` unsubscribe
- `GET /` list my subscriptions

### WatchController (partial)
- `POST /api/videos/{id}/increment-view`
- `POST /api/videos/{videoId}/comments` add comment or reply (use `parentCommentId` for reply)
- `GET /api/videos/{videoId}/comments/{commentId}/replies` list replies

### VideoController (UI)
- `POST /upload` upload video and optional thumbnail
- `GET /add-video`, `GET /list-video`, `GET /delete/{id}`


