const skillItems = document.querySelectorAll(".skill-item");

function animateProgressBar(item) {
    const fill = item.querySelector(".progress-fill");
    const targetRate = fill.getAttribute("data-rate");
    fill.style.width = targetRate + "%";
}

function resetProgressBar(item) {
    const fill = item.querySelector(".progress-fill");
    fill.style.width = "0%";
}

// [수정] 클릭했을 때만 채워지도록 설정
skillItems.forEach((item) => {
    item.addEventListener("click", () => {
        // 이미 채워져 있다면 초기화 후 다시 채우기 (애니메이션 효과)
        resetProgressBar(item);

        setTimeout(() => {
            animateProgressBar(item);
        }, 10);
    });
});
