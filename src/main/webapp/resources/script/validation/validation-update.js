import {
	validatePassword,
	validatePhone,
	formatPhoneString,
	checkDuplEmail,
	checkDuplPhone,
	checkDuplnickname,
	updateErrorElementStyle
} from "./validation.js";

window.onload = () => {
	const form = document.querySelector("form");

	const elements = {
		password: document.getElementById("current-password"),
		newPassword: document.getElementById("new-password"),
		confirmPassword: document.getElementById("confirm-password"),
		nickname: document.getElementById("new-nickname"),
		phone: document.getElementById("new-phone"),
		email: document.getElementById("new-email"),
		profileImage: document.getElementById("profile-image"),
		profileInfo: document.getElementById("profile-info"),
		updateButton: form.querySelector("button[type='submit']")
	};

	elements.password.addEventListener("change", () => {
		const isPasswordFilled = elements.password.value !== '';
		elements.newPassword.disabled = !isPasswordFilled;
		elements.confirmPassword.disabled = !isPasswordFilled;

		if (!isPasswordFilled) {
			elements.newPassword.value = '';
			elements.confirmPassword.value = '';
		}
	});

	elements.newPassword.addEventListener("change", () => {
		const isValid = validatePassword(elements.newPassword.value);
		const errorElement = document.getElementById("error-msg-password-pattern");
		updateErrorElementStyle(errorElement, "비밀번호 형식이 올바르지 않습니다.", !isValid);
	});

	elements.confirmPassword.addEventListener("change", () => {
		const isMatched = elements.newPassword.value === elements.confirmPassword.value;
		const errorElement = document.getElementById("error-msg-confirm-password");
		updateErrorElementStyle(errorElement, "비밀번호가 일치하지 않습니다.", !isMatched);
	});

	elements.phone.addEventListener("change", async (e) => {
		elements.phone.value = formatPhoneString(e.target.value);
		const isValid = validatePhone(elements.phone.value);
		const errorElement = document.getElementById("error-msg-phone");

		if (!isValid) {
			updateErrorElementStyle(errorElement, "전화번호 형식이 올바르지 않습니다.", true);
		} else if (await checkDuplPhone(elements.phone.value)) {
			updateErrorElementStyle(errorElement, "이미 사용 중인 전화번호입니다.", true);
		} else {
			updateErrorElementStyle(errorElement, "", false);
		}
	});

	elements.email.addEventListener("change", async () => {
		const isDuplicate = await checkDuplEmail(elements.email.value);
		const errorElement = document.getElementById("error-msg-email");
		updateErrorElementStyle(errorElement, "이미 사용 중인 이메일입니다.", isDuplicate);
	});

	elements.nickname.addEventListener("change", async () => {
		const isDuplicate = await checkDuplnickname(elements.nickname.value);
		const errorElement = document.getElementById("error-msg-nickname");
		updateErrorElementStyle(errorElement, "이미 사용 중인 닉네임입니다.", isDuplicate);
	});

	elements.updateButton.addEventListener("click", async (e) => {
		e.preventDefault();
		console.log("update button clicked");

		const apiKey = sessionStorage.getItem("apiKey");
		const uuid = sessionStorage.getItem("uuid");

		console.log("API Key:", apiKey);
		console.log("UUID:", uuid);

		if (!apiKey || !uuid) {
			alert("API 키 또는 사용자 정보가 유효하지 않습니다.");
			return;
		}

		const formData = new FormData();
		formData.append("uuid", uuid);

		if (elements.password.value) {
			formData.append("password", elements.password.value);
			if (elements.newPassword.value) {
				formData.append("new_password", elements.newPassword.value);
			}
		}

		if (elements.nickname.value.trim()) formData.append("nickname", elements.nickname.value.trim());
		if (elements.phone.value.trim()) formData.append("phone", elements.phone.value.trim());
		if (elements.email.value.trim()) formData.append("email", elements.email.value.trim());
		if (elements.profileInfo.value.trim()) formData.append("profile_info", elements.profileInfo.value.trim());

		if (elements.profileImage.files.length > 0) {
			formData.append("profile_image", elements.profileImage.files[0]);
		}

		updateButton.disabled = true;

		try {
			const response = await fetch("/v1/members?command=update", {
				method: "PATCH",
				headers: {
					"Authorization": `Bearer ${apiKey}`,
				},
				body: formData
			});

			const result = await response.json();
			if (response.ok) {
				alert("회원 정보가 성공적으로 업데이트되었습니다.");
			} else {
				alert(`회원 정보 업데이트 실패: ${result.message || "알 수 없는 오류"}`);
			}
		} catch (error) {
			console.error("업데이트 요청 중 오류 발생", error);
			alert("회원 정보 업데이트 요청 중 오류가 발생했습니다.");
		} finally {
			elements.updateButton.disabled = false;
		}
	});
};
