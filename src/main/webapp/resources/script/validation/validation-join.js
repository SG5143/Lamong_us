import {
	updateErrorElementStyle,
	validateUsername,
	validatePassword,
	validateNickname,
	validatePhone,
	validateEmail,
	checkDuplUsername,
	checkDuplPhone,
	checkDuplEmail,
	checkDuplnickname,
} from "./validation.js";

document.addEventListener("DOMContentLoaded", () => {
	const username = document.getElementById("username-join");
	const password = document.getElementById("password-join");
	const email = document.getElementById("email-join");
	const phone = document.getElementById("phone-join");
	const nickname = document.getElementById("nickname-join");
	const loginType = document.getElementById("login_type-join");

	const form = document.querySelector("form");
	const joinButton = form.querySelector("button[type='submit']");

	let isValid = {
		username: false,
		password: false,
		nickname: false,
		phone: false,
		email: false,
	};

	const handleValidation = async (input, validateFn, duplicateFn, errorIds) => {
		const { emptyId, patternId, duplicateId } = errorIds;

		if (input.value === "") {
			updateErrorElementStyle(document.getElementById(emptyId), true);
			return false;
		} else {
			updateErrorElementStyle(document.getElementById(emptyId), false);
		}

		const isPatternValid = validateFn(input.value);
		updateErrorElementStyle(document.getElementById(patternId), !isPatternValid);
		if (!isPatternValid) return false;

		if (duplicateFn) {
			const isDuplicateValid = await duplicateFn(input.value);
			updateErrorElementStyle(document.getElementById(duplicateId), !isDuplicateValid);
			return isDuplicateValid;
		}

		return true;
	};

	username.addEventListener("change", async (e) => {
		isValid.username = await handleValidation(
			e.target,
			validateUsername,
			checkDuplUsername,
			{
				emptyId: "error-msg-username-empty",
				patternId: "error-msg-username-pattern",
				duplicateId: "error-msg-username",
			}
		);
	});

	password.addEventListener("change", (e) => {
		isValid.password = validatePassword(e.target.value);
		updateErrorElementStyle(
			document.getElementById("error-msg-password-empty"),
			e.target.value === ""
		);
		updateErrorElementStyle(
			document.getElementById("error-msg-password-pattern"),
			!isValid.password
		);
	});

	nickname.addEventListener("change", async (e) => {
		isValid.nickname = await handleValidation(
			e.target,
			validateNickname,
			checkDuplnickname,
			{
				emptyId: "error-msg-nickname-empty",
				patternId: "error-msg-nickname",
				duplicateId: "error-msg-nickname",
			}
		);
	});

	phone.addEventListener("change", async (e) => {
		isValid.phone = await handleValidation(
			e.target,
			validatePhone,
			checkDuplPhone,
			{
				emptyId: "error-msg-phone-empty",
				patternId: "error-msg-phone",
				duplicateId: "error-msg-phone",
			}
		);
	});

	email.addEventListener("change", async (e) => {
		isValid.email = await handleValidation(
			e.target,
			validateEmail,
			checkDuplEmail,
			{
				emptyId: "error-msg-email-empty",
				patternId: "error-msg-email",
				duplicateId: "error-msg-email",
			}
		);
	});



	joinButton.addEventListener("click", async (e) => {
		e.preventDefault();

		console.log("Login button clicked");
		resetErrorMessages();

		let loginTypeValue = null;
		if (loginType.value === "1") {
			loginTypeValue = "kakao";
		} else if (loginType.value === "2") {
			loginTypeValue = "google";
		}

		const payload = {
			username: username.value,
			password: password.value,
			nickname: nickname.value,
			phone: phone.value,
			email: email.value,
			login_type: loginTypeValue,
		};

		console.log("Sending payload:", JSON.stringify(payload));
		console.log("URL:", "/members?command=join");

		console.log(JSON.stringify(requestData));

		if (Object.values(isValid).every((value) => value)) {
			try {
				const response = await fetch("/v1/members?command=join", {
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify({
						username: username.value,
						password: password.value,
						nickname: nickname.value,
						phone: phone.value,
						email: email.value,
						login_type: loginTypeValue,
					}),
				});

				const data = await response.json();
				console.log("Response data:", data);

				if (data.status === 200) {
					alert("회원가입 성공!");
					window.location.href = "/lobby";
				} else {
					alert(data.message || "회원가입에 실패했습니다.");
					console.error("join failed:", data);
				}
			} catch (error) {
				console.error("Error during registration:", error);
				alert("회원가입 요청 중 오류가 발생했습니다.");
			}
		} else {
			alert("모든 정보를 올바르게 입력해주세요.");
		}
	});
});
