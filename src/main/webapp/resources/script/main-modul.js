document.addEventListener("DOMContentLoaded", () => {
	const modalOverlay = document.getElementById("modal-overlay");
	const modalButtons = document.querySelectorAll("[data-modal]");
	const closeButtons = document.querySelectorAll("[data-close]");

	// 모달 열기
	modalButtons.forEach((button) => {
		button.addEventListener("click", () => {
			const modalId = button.getAttribute("data-modal");
			const modal = document.getElementById(modalId);

			if (modal) {
				document.querySelectorAll(".modal").forEach((modal) => {
					modal.style.display = "none";
				});

				modalOverlay.style.display = "flex";

				modal.style.display = "block";
			}
		});
	});

	// 모달 닫기
	closeButtons.forEach((button) => {
		button.addEventListener("click", () => {
			modalOverlay.style.display = "none";
			document.querySelectorAll(".modal").forEach((modal) => {
				modal.style.display = "none";
			});
		});
	});
});
