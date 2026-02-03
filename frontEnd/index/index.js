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

/* [추가] Weather Logic */
async function loadWeather() {
  const container = document.getElementById('weather-container');
  
  try {
    // 1. weather.json 호출
    const response = await fetch('../../weather.json');
    if (!response.ok) throw new Error('Network response was not ok');
    const data = await response.json();

    const temp = parseFloat(data.temp);
    const rain = data.rain;
    const skyCode = data.skyCode; // PTY 코드: 0(없음), 1(비), 2(비/눈), 3(눈), 4(소나기)

    // 2. 날씨 아이콘 및 조언 로직
    let weatherIcon = "fa-sun";
    let advice = "";

    if (rain !== "0" || skyCode !== "0") {
      weatherIcon = "fa-cloud-showers-heavy";
      advice = "비나 눈이 오는 날에는 하체 운동이 제맛이죠! 접지력 좋은 신발 신고 안전하게 득근하세요. 🏋️‍♂️";
    } else if (temp < -5) {
      weatherIcon = "fa-snowflake";
      advice = "날씨가 많이 춥습니다! 🥶 실내에서 웜업 충분히 하시고 이두/삼두 운동으로 팔 펌핑 어떠신가요?";
    } else {
      weatherIcon = "fa-cloud-sun";
      advice = "운동하기 딱 좋은 날씨네요! 오늘 같은 날은 등 운동 후 가벼운 유산소까지 강력 추천합니다. 🔥";
    }

    // 3. HTML 렌더링
    container.innerHTML = `
      <div class="weather-info-main">
        <i class="fas ${weatherIcon} weather-icon"></i>
        <span class="weather-temp">${temp}°C</span>
      </div>
      <div class="weather-details">
        <span>강수량: ${rain}mm | 현재 수원의 날씨 실황입니다.</span>
      </div>
      <div class="fitness-comment">
        ${advice}
      </div>
    `;

  } catch (error) {
    console.error('Weather loading error:', error);
    container.innerHTML = `<div class="weather-loading">날씨 정보를 업데이트하는 중입니다. 잠시 후 다시 확인해 주세요!</div>`;
  }
}

// 페이지 로드 시 날씨 함수 실행
window.addEventListener('DOMContentLoaded', loadWeather);