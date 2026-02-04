/* Weather Logic */
async function loadWeather() {
  const container = document.getElementById('weather-container');

  try {
    // 1. weather.json 호출
    const response = await fetch('/weatherApi/weather.json');
    if (!response.ok) throw new Error('Network response was not ok');
    const data = await response.json();

    const temp = parseFloat(data.temp);
    const rain = data.rain;
    const skyCode = data.skyCode;
    const lastUpdate = data.lastUpdate; // [추가] 자바에서 저장한 시간 데이터

    // 2. 날씨 아이콘 및 조언 로직
    let weatherIcon = "fa-sun";
    let advice = "";

    if (rain !== "0" || skyCode !== "0") {
      weatherIcon = "fa-cloud-showers-heavy";
      advice = "비나 눈이 오는 날이니까 우산을 챙겨주세요 ☔";
    } else if (temp < -5) {
      weatherIcon = "fa-snowflake";
      advice = "날씨가 많이 춥습니다! 따듯하게 입으세요 🧣";
    } else {
      weatherIcon = "fa-cloud-sun";
      advice = "좋은 날씨네요! 오늘도 즐거운 하루 되세요. 😊";
    }

    // 3. HTML 렌더링 (홍대 고정 및 업데이트 시간 표시)
    container.innerHTML = `
      <div class="weather-info-main">
        <i class="fas ${weatherIcon} weather-icon"></i>
        <span class="weather-temp">${temp}°C</span>
      </div>
      <div class="weather-details">
        <p>강수량: ${rain}mm | <strong>현재 홍대(마포구)</strong>의 날씨 실황입니다.</p>
        <p class="update-time">최종 업데이트: ${formatTimeHM(lastUpdate)}</p>
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

// lastUpdate에서 "HH:mm"만 추출
function formatTimeHM(timeString) {
  // "2026-02-04 04:44:30" → "04:44"
  return timeString.substring(11, 16);
}
window.addEventListener('DOMContentLoaded', loadWeather);