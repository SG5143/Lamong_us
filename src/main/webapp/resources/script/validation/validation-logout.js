document.addEventListener("DOMContentLoaded", () => {
	const logoutButton = document.getElementById("logout-button");

	if (!logoutButton) {
		console.error("로그아웃 버튼을 찾을 수 없습니다.");
		return;
	}

	logoutButton.addEventListener("click", () => {
		if (confirm("정말 로그아웃 하시겠습니까?")) {
			sessionStorage.clear();

			localStorage.removeItem("userToken");

			alert("로그아웃 되었습니다.");
			window.location.href = "/main";
		}
	});
});
