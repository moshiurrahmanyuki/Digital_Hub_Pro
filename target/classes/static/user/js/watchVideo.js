/*<![CDATA[*/
document.addEventListener('DOMContentLoaded', () => {
    // === Video View Increment Logic ===
    const videoId = /*[[${video != null ? video.id : ''}]]*/ '';
    const videoPlayer = document.getElementById('videoPlayer');
    const incrementInterval = 3; // Keep at 3 for quick testing, change to 30 for production
    let lastIncrementTime = 0;

    const viewCountDisplay = document.querySelector('.view-count-display'); // Selector for dynamic update

    // Get CSRF token and header name if Spring Security is enabled
    const csrfToken = document.querySelector('meta[name="_csrf"]') ? document.querySelector('meta[name="_csrf"]').getAttribute('content') : null;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]') ? document.querySelector('meta[name="_csrf_header"]').getAttribute('content') : null;

    if (!videoPlayer) {
        console.error("Error: Video player element with ID 'videoPlayer' not found!");
    } else {
        videoPlayer.addEventListener('timeupdate', function() {
            if (!videoPlayer.paused && !videoPlayer.ended && videoPlayer.currentTime >= (lastIncrementTime + incrementInterval)) {
                console.log('Video played for another ' + incrementInterval + ' seconds. Triggering view increment.');

                lastIncrementTime = Math.floor(videoPlayer.currentTime / incrementInterval) * incrementInterval;

                const headers = {
                    'Content-Type': 'application/json'
                };
                if (csrfToken && csrfHeader) {
                    headers[csrfHeader] = csrfToken;
                } else {
                    console.warn("CSRF token or header is null. Fetch request might be rejected.");
                }

                fetch('/api/videos/' + videoId + '/increment-view', {
                    method: 'POST',
                    headers: headers
                })
                    .then(response => {
                        if (response.ok) {
                            console.log('View count incremented successfully!');
                            // Dynamically update view count on the page
                            if (viewCountDisplay) {
                                let currentViews = parseInt(viewCountDisplay.textContent.split(' ')[0]) || 0;
                                viewCountDisplay.textContent = (currentViews + 1) + ' views';
                            }
                        } else {
                            console.error('Failed to increment view count. Status:', response.status);
                            response.text().then(text => console.error('Response body:', text));
                        }
                    })
                    .catch(error => {
                        console.error('Error sending increment view count request:', error);
                    });
            }
        });

        videoPlayer.addEventListener('seeking', function() {
            if (videoPlayer.currentTime < lastIncrementTime) {
                lastIncrementTime = Math.floor(videoPlayer.currentTime / incrementInterval) * incrementInterval;
            }
        });

        videoPlayer.addEventListener('play', function() {
            if (videoPlayer.currentTime < incrementInterval && lastIncrementTime !== 0) {
                lastIncrementTime = 0;
            }
        });
    }

    // === Comment Section Logic ===
    const commentTextarea = document.getElementById('comment-textarea');
    const postCommentBtn = document.getElementById('post-comment-btn');
    const cancelCommentBtn = document.getElementById('cancel-comment-btn');
    const commentsList = document.getElementById('comments-list');
    const commentSectionHeader = document.querySelector('.comment-section-header'); // For updating comment count

    const currentVideoId = /*[[${video != null ? video.id : null}]]*/ null; // Ensure videoId is available for comments

    if (postCommentBtn) {
        postCommentBtn.disabled = true; // Disable by default
    }

    if (commentTextarea && postCommentBtn) {
        commentTextarea.addEventListener('input', () => {
            postCommentBtn.disabled = commentTextarea.value.trim() === '';
        });
    }

    if (cancelCommentBtn && commentTextarea && postCommentBtn) {
        cancelCommentBtn.addEventListener('click', () => {
            commentTextarea.value = '';
            postCommentBtn.disabled = true;
        });
    }

    const commentForm = document.getElementById('comment-form');
    if (commentForm) {
        commentForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const commentText = commentTextarea.value.trim();
            const authorName = "GuestUser"; // You might want to get this from a logged-in user context

            if (!commentText || !currentVideoId) {
                console.error("Comment text or video ID missing.");
                return;
            }

            const headers = {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            };
            if (csrfToken && csrfHeader) {
                headers[csrfHeader] = csrfToken; // Add CSRF token for security
            }

            try {
                const response = await fetch(`/api/videos/${currentVideoId}/comments`, {
                    method: 'POST',
                    headers: headers, // Use combined headers including CSRF
                    body: JSON.stringify({ commentText, authorName })
                });

                if (response.ok) {
                    const newComment = await response.json();
                    console.log('Comment added:', newComment);

                    addNewCommentToDOM(newComment);

                    commentTextarea.value = '';
                    postCommentBtn.disabled = true;

                    // Update comment count displayed in the header
                    if (commentSectionHeader) {
                        let currentCount = 0;
                        const match = commentSectionHeader.textContent.match(/\((\d+)\)/);
                        if (match) {
                            currentCount = parseInt(match[1]);
                        }
                        commentSectionHeader.textContent = `Comments (${currentCount + 1})`;
                    }

                    const noCommentsMessage = commentsList.querySelector('.no-comments-message'); // Use the class
                    if (noCommentsMessage) {
                        noCommentsMessage.remove();
                    }

                    const originalText = postCommentBtn.textContent;
                    postCommentBtn.textContent = 'Posted!';
                    setTimeout(() => {
                        postCommentBtn.textContent = originalText;
                    }, 1500);

                } else {
                    console.error('Failed to add comment:', response.status);
                    response.text().then(text => console.error('Response body:', text));
                }
            } catch (error) {
                console.error('Error adding comment:', error);
            }
        });
    }

    async function handleLikeDislike(event) {
        const button = event.currentTarget;
        const commentId = button.dataset.commentId;
        const videoId = button.dataset.videoId; // This is currentVideoId from the Thymeleaf context
        const isLike = button.classList.contains('like-btn');
        const endpoint = isLike ?
            `/api/videos/${videoId}/comments/${commentId}/like` :
            `/api/videos/${videoId}/comments/${commentId}/dislike`;

        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        };
        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken; // Add CSRF token for security
        }

        try {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: headers // Use combined headers including CSRF
            });

            if (response.ok) {
                const updatedComment = await response.json();
                const likeCountSpan = button.querySelector('.like-count');
                if (likeCountSpan) {
                    likeCountSpan.textContent = updatedComment.likeCount; // Update the count from the response
                }
                // Optionally handle dislike count update if you add a span for it
                console.log(`Comment ${commentId} ${isLike ? 'liked' : 'disliked'}. New count: ${updatedComment.likeCount}`);
            } else {
                console.error(`Failed to ${isLike ? 'like' : 'dislike'} comment:`, response.status);
                response.text().then(text => console.error('Response body:', text));
            }
        } catch (error) {
            console.error(`Error ${isLike ? 'liking' : 'disliking'} comment:`, error);
        }
    }

    function addNewCommentToDOM(comment) {
        const commentDiv = document.createElement('div');
        commentDiv.id = `comment-${comment.id}`;
        commentDiv.className = 'comment-item flex items-start gap-4 py-4 border-b border-gray-800 last:border-b-0';

        // Format timestamp with specific format 'dd MMMപ്പെടെ HH:mm'
        const timestampDate = new Date(comment.timestamp);
        const options = { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' };
        const formattedTimestamp = timestampDate.toLocaleString('en-US', options).replace(/,/, ''); // Remove comma after year

        commentDiv.innerHTML = `
        <div class="w-10 h-10 rounded-full bg-gray-700 flex-shrink-0"></div>
        <div class="flex-grow">
          <div class="flex items-center gap-2">
            <p class="font-semibold text-white text-sm">${comment.authorName}</p>
            <p class="text-xs text-gray-400">${formattedTimestamp}</p>
          </div>
          <p class="text-gray-200 mt-1">${comment.text}</p>
          <div class="flex items-center gap-4 mt-2 text-gray-400">
            <button data-comment-id="${comment.id}" data-video-id="${currentVideoId}" class="like-btn flex items-center gap-1 hover:text-white">
              <i class="fa-solid fa-thumbs-up"></i>
              <span class="like-count text-xs">${comment.likeCount || 0}</span>
            </button>
            <button data-comment-id="${comment.id}" data-video-id="${currentVideoId}" class="dislike-btn hover:text-white">
              <i class="fa-solid fa-thumbs-down"></i>
            </button>
            <button class="text-xs font-semibold hover:text-white">Reply</button>
          </div>
        </div>
      `;
        commentsList.prepend(commentDiv); // Add new comment to the top
        addLikeDislikeEventListeners(commentDiv); // Attach listeners to the new comment
    }

    function addLikeDislikeEventListeners(container) {
        container.querySelectorAll('.like-btn').forEach(button => {
            button.removeEventListener('click', handleLikeDislike); // Prevent duplicate listeners
            button.addEventListener('click', handleLikeDislike);
        });
        container.querySelectorAll('.dislike-btn').forEach(button => {
            button.removeEventListener('click', handleLikeDislike); // Prevent duplicate listeners
            button.addEventListener('click', handleLikeDislike);
        });
    }

    addLikeDislikeEventListeners(commentsList);

    // --- MY ADDED CODE STARTS HERE ---

    /**
     * Attempts to filter related videos on the frontend based on simple keyword matching.
     * This is a conceptual demonstration. For true relevance, backend logic is required.
     */
    function filterTrulyRelatedVideosFrontend() {
        const relatedVideosContainer = document.querySelector('.lg\\:w-1\\/3'); // Main container for related videos section
        if (!relatedVideosContainer) {
            console.warn("Related videos container not found. Cannot apply frontend filtering.");
            return;
        }

        // Get the main video's title to use as a very basic "relevance" keyword source
        const mainVideoTitleElement = document.querySelector('.text-xl.md\\:text-2xl.font-bold.text-white.mt-4');
        const mainVideoTitle = mainVideoTitleElement ? mainVideoTitleElement.textContent.toLowerCase() : '';

        // Extract significant keywords from the main video title (e.g., words longer than 3 characters)
        const mainVideoKeywords = mainVideoTitle.split(/\s+/)
            .filter(word => word.length > 3 && !['top', 'best', 'the', 'and', 'for', 'with', 'you', 'how', 'what', 'why'].includes(word)); // Basic stop words

        const allRelatedVideoCards = relatedVideosContainer.querySelectorAll('.related-video-card');
        let visibleCount = 0;

        if (allRelatedVideoCards.length === 0) {
            console.log("No related video cards found to filter.");
            return;
        }

        console.log("Applying frontend filtering for 'truly related' videos based on keywords.");
        console.log("Main video keywords:", mainVideoKeywords);


        allRelatedVideoCards.forEach(card => {
            const relatedVideoTitleElement = card.querySelector('.related-video-info .title');
            const relatedVideoTitle = relatedVideoTitleElement ? relatedVideoTitleElement.textContent.toLowerCase() : '';

            let isTrulyRelatedByFrontendLogic = false;

            // Check if any of the main video's keywords are present in the related video's title
            if (mainVideoKeywords.length > 0 && relatedVideoTitle) {
                for (const keyword of mainVideoKeywords) {
                    if (relatedVideoTitle.includes(keyword)) {
                        isTrulyRelatedByFrontendLogic = true;
                        break; // Found a match, no need to check other keywords for this card
                    }
                }
            } else {
                // If no keywords from main video, or no related video title, consider it not truly related by this logic
                isTrulyRelatedByFrontendLogic = false;
            }

            // Show or hide the card based on the filtering result
            if (isTrulyRelatedByFrontendLogic) {
                card.style.display = 'flex'; // Ensure the card is visible
                visibleCount++;
            } else {
                card.style.display = 'none'; // Hide the card
            }
        });

        // Update the "No related videos found" message based on the filtering result
        const defaultNoRelatedMessage = relatedVideosContainer.querySelector('.text-gray-500.text-center.py-4');
        const customNoRelatedMessageClass = 'no-truly-related-message-frontend';
        let customNoRelatedMessage = relatedVideosContainer.querySelector(`.${customNoRelatedMessageClass}`);

        if (visibleCount === 0) {
            if (defaultNoRelatedMessage) {
                defaultNoRelatedMessage.style.display = 'none'; // Hide default message
            }
            if (!customNoRelatedMessage) {
                customNoRelatedMessage = document.createElement('p');
                customNoRelatedMessage.className = `text-gray-500 text-center py-4 ${customNoRelatedMessageClass}`;
                relatedVideosContainer.appendChild(customNoRelatedMessage);
            }
            customNoRelatedMessage.textContent = "No truly related videos found by frontend filtering.";
            customNoRelatedMessage.style.display = 'block';
        } else {
            if (defaultNoRelatedMessage) {
                defaultNoRelatedMessage.style.display = 'none'; // Always hide default message if my filter ran
            }
            if (customNoRelatedMessage) {
                customNoRelatedMessage.style.display = 'none'; // Hide custom message if videos are shown
            }
        }
    }

    // Call the frontend filtering function once the DOM is fully loaded
    filterTrulyRelatedVideosFrontend();

    // --- MY ADDED CODE ENDS HERE ---

});

