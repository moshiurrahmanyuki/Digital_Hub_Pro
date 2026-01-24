
const mamuButton = document.querySelectorAll(".menu-button");
const screenOverlay = document.querySelector(".screen-overlay");
const themeButton  = document.querySelector(".theme-button i");

if(localStorage.getItem("darkMode") === "enabled"){
    document.body.classList.add("dark-mode");
    themeButton.classList.replace("uil-moon", "uil-sun");
}else{
    themeButton.classList.replace("uil-sun", "uil-moon");
}

themeButton.addEventListener("click", () =>{
    const isDarkMode = document.body.classList.toggle("dark-mode");
    localStorage.setItem("darkMode",isDarkMode ? "enabled" : "desabled");
    themeButton.classList.toggle("uil-sun", isDarkMode);
    themeButton.classList.toggle("uil-moon", !isDarkMode);
});


mamuButton.forEach(button => {
    button.addEventListener("click" , () => {
        document.body.classList.toggle("sidebar-hidden");
        console.log("Click Button!")
    });
});

screenOverlay.addEventListener("click" , () =>{
    document.body.classList.toggle("sidebar-hidden");

});
if(window.innerWidth >= 768){
    document.body.classList.remove("sidebar-hidden");
}

let subMenu = document.getElementById("subMenu"); // This variable will be null if the element isn't in HTML
function toggleMenu(){
    subMenu.classList.toggle("open-menu");
}



const videoListContainer = document.querySelector('.video-list');


function shuffleVideos() {
    if (!videoListContainer) {
        console.warn("Video list container (.video-list) not found. Cannot shuffle videos.");
        return;
    }

    const videoCards = Array.from(videoListContainer.children);

    for (let i = videoCards.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));


        [videoCards[i], videoCards[j]] = [videoCards[j], videoCards[i]];
    }

    videoCards.forEach(card => videoListContainer.appendChild(card));

    console.log("Videos shuffled on page load!");
}

document.addEventListener('DOMContentLoaded', () => {
    shuffleVideos();
});

