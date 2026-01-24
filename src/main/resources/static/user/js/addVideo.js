const menuButtons = document.querySelectorAll(".menu-button"); // Renamed for clarity
const screenOverlay = document.querySelector(".screen-overlay");
const themeButton  = document.querySelector(".theme-button i");
let subMenu = document.getElementById("subMenu"); // Get the sub-menu element

// --- Theme Toggle ---
// Check and apply dark mode on page load
if (localStorage.getItem("darkMode") === "enabled") {
    document.body.classList.add("dark-mode");
    themeButton.classList.replace("uil-moon", "uil-sun");
} else {
    // Ensure the sun icon is replaced by moon if not in dark mode (handles initial state)
    themeButton.classList.replace("uil-sun", "uil-moon");
}

themeButton.addEventListener("click", () => {
    const isDarkMode = document.body.classList.toggle("dark-mode");
    localStorage.setItem("darkMode", isDarkMode ? "enabled" : "disabled"); // Corrected 'desabled' to 'disabled'
    themeButton.classList.toggle("uil-sun", isDarkMode);
    themeButton.classList.toggle("uil-moon", !isDarkMode);
});

// --- Sidebar Toggle ---
menuButtons.forEach(button => {
    button.addEventListener("click" , () => {
        document.body.classList.toggle("sidebar-hidden");
        // For mobile, also toggle overlay
        if (window.innerWidth <= 768) {
            screenOverlay.classList.toggle("active", !document.body.classList.contains("sidebar-hidden"));
        }
    });
});

screenOverlay.addEventListener("click" , () =>{
    document.body.classList.add("sidebar-hidden"); // Always hide sidebar
    if (window.innerWidth <= 768) {
        screenOverlay.classList.remove("active"); // Hide overlay on click
    }
});

// Initial check for sidebar on larger screens
if(window.innerWidth >= 768){
    document.body.classList.remove("sidebar-hidden");
}

// Listen for window resize to adjust sidebar
window.addEventListener('resize', () => {
    if (window.innerWidth >= 768) {
        document.body.classList.remove("sidebar-hidden");
        screenOverlay.classList.remove("active"); // Ensure overlay is hidden
    } else {
        // If resizing to mobile, ensure sidebar is hidden by default
        if (!document.body.classList.contains("sidebar-hidden")) {
            document.body.classList.add("sidebar-hidden");
        }
    }
});


// --- User Sub-Menu Toggle ---
function toggleMenu(){
    // Check if subMenu element exists before trying to toggle its class
    if (subMenu) {
        subMenu.classList.toggle("open-menu");
    } else {
        console.warn("SubMenu element not found. Make sure an element with id='subMenu' exists in your HTML.");
    }
}
// Make sure this function is globally accessible if used in onclick="toggleMenu()"

// Close sub-menu if clicking outside
document.addEventListener('click', (event) => {
    const userImage = document.querySelector('.user-image'); // Your image that triggers the menu
    const subMenuWrap = document.getElementById('subMenu');

    if (subMenuWrap && !userImage.contains(event.target) && !subMenuWrap.contains(event.target)) {
        if (subMenuWrap.classList.contains('open-menu')) {
            subMenuWrap.classList.remove('open-menu');
        }
    }
});


// --- Video List Shuffle (Only for homepage's main video list if exists) ---
// Note: Your HTML structure doesn't show a `.video-list` on the homepage content,
// but rather within the form section for "Uploaded Videos".
// I'm assuming you intended this shuffle for a *main* video feed section.
// If not, and you only have the "Uploaded Videos" section, this can be removed or adjusted.
const homepageVideoListContainer = document.querySelector('.content-wrapper .video-list'); // Target main video list

function shuffleVideos() {
    if (!homepageVideoListContainer) {
        // console.warn("Homepage video list container (.content-wrapper .video-list) not found. Cannot shuffle videos.");
        return;
    }

    const videoCards = Array.from(homepageVideoListContainer.children);

    for (let i = videoCards.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [videoCards[i], videoCards[j]] = [videoCards[j], videoCards[i]];
    }

    // Append shuffled cards back to the container
    videoCards.forEach(card => homepageVideoListContainer.appendChild(card));
    // console.log("Videos shuffled on page load!");
}

document.addEventListener('DOMContentLoaded', () => {
    shuffleVideos(); // Only runs if homepageVideoListContainer is found
});


// --- Video Upload Form Previews & Validation ---
function previewThumbnail(event) {
    const file = event.target.files[0];
    const previewContainer = document.getElementById('thumbnail-preview');
    const uploadedThumbnail = document.getElementById('uploaded-thumbnail');
    const imageError = document.getElementById('image-error');

    if (file) {
        // Basic file type validation (optional, can also be done backend)
        if (!file.type.startsWith('image/')) {
            imageError.textContent = 'Please select a valid image file (e.g., JPG, PNG).';
            previewContainer.style.display = 'none';
            uploadedThumbnail.src = '';
            event.target.value = ''; // Clear the input
            return;
        }
        imageError.textContent = ''; // Clear previous errors

        const reader = new FileReader();
        reader.onload = (e) => {
            uploadedThumbnail.src = e.target.result;
            previewContainer.style.display = 'block';
        };
        reader.readAsDataURL(file);
    } else {
        previewContainer.style.display = 'none';
        uploadedThumbnail.src = '';
        imageError.textContent = '';
    }
}

function previewVideo(event) {
    const file = event.target.files[0];
    const previewContainer = document.getElementById('video-preview');
    const uploadedVideo = document.getElementById('uploaded-video');
    const fileError = document.getElementById('file-error');
    const fileUploadText = document.getElementById('file-upload-text');


    if (file) {
        if (!file.type.startsWith('video/')) {
            fileError.textContent = 'Please select a valid video file (e.g., MP4, WebM).';
            previewContainer.style.display = 'none';
            uploadedVideo.src = '';
            event.target.value = '';
            fileUploadText.textContent = 'Drag and drop your video here, or click to select a file'; // Reset text
            return;
        }
        fileError.textContent = ''; // Clear previous errors

        const reader = new FileReader();
        reader.onload = (e) => {
            uploadedVideo.src = e.target.result;
            previewContainer.style.display = 'block';
            fileUploadText.textContent = `Selected: ${file.name}`; // Update text with file name
        };
        reader.readAsDataURL(file);

        // Populate duration automatically (this is client-side estimate)
        // For accurate duration, you might need backend processing or a library.
        uploadedVideo.onloadedmetadata = function() {
            const durationInput = document.getElementById('duration');
            const duration = uploadedVideo.duration;
            const hours = Math.floor(duration / 3600);
            const minutes = Math.floor((duration % 3600) / 60);
            const seconds = Math.floor(duration % 60);

            const formattedDuration = [hours, minutes, seconds]
                .map(v => v < 10 ? "0" + v : v)
                .filter((v, i) => v !== "00" || i > 0) // Remove leading "00" for hours if zero
                .join(":");
            durationInput.value = formattedDuration;
        };

    } else {
        previewContainer.style.display = 'none';
        uploadedVideo.src = '';
        fileError.textContent = '';
        fileUploadText.textContent = 'Drag and drop your video here, or click to select a file';
        document.getElementById('duration').value = ''; // Clear duration if no file
    }
}


// Drag and drop functionality for video file input
const videoFileUploadArea = document.getElementById('video-file-upload');
if (videoFileUploadArea) {
    videoFileUploadArea.addEventListener('dragover', (e) => {
        e.preventDefault(); // Prevent default to allow drop
        e.stopPropagation();
        videoFileUploadArea.classList.add('dragover');
    });

    videoFileUploadArea.addEventListener('dragleave', (e) => {
        e.preventDefault();
        e.stopPropagation();
        videoFileUploadArea.classList.remove('dragover');
    });

    videoFileUploadArea.addEventListener('drop', (e) => {
        e.preventDefault();
        e.stopPropagation();
        videoFileUploadArea.classList.remove('dragover');

        const files = e.dataTransfer.files;
        const fileInput = document.getElementById('fileVideo');
        if (files.length > 0) {
            // Assign the dropped file to the input's files property
            fileInput.files = files;
            // Trigger the change event manually to call previewVideo
            const event = new Event('change', { bubbles: true });
            fileInput.dispatchEvent(event);
        }
    });
}