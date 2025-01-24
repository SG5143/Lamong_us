import { updateErrorElementStyle, validateUsername, validatePassword, validateNickname, validatePhone, validateEmail, checkDuplUsername, checkDuplPhone, checkDuplEmail, checkDuplnickname } from "./validation.js";

window.onload = () => {
	const form = document.getElementById("form-join");

	const username = document.getElementById("username-join");
	const password = document.getElementById("password-join");
	const nickname = document.getElementById("nickname-join");
	const phone = document.getElementById("phone-join");
	const email = document.getElementById("email-join");
	const loginType = document.getElementById("login_type-join");
	const joinButton = form.querySelector("button[type='submit']");

	const isValid = {
		username: false,
		password: false,
		nickname: false,
		phone: false,
		email: false,
	};

	const resetErrorMessages = () => {
		document.querySelectorAll(".error-msg").forEach((msg) => {
			msg.style.display = "none";
		});
	};

	const handleValidation = async (input, validateFn, duplicateFn, errorIds) => {
		const { emptyId, patternId, duplicateId } = errorIds;

		// emptyId가 비어 있는지 확인
		const emptyElement = document.getElementById(emptyId);
		if (emptyElement) {
			if (input.value === "") {
				updateErrorElementStyle(emptyElement, true);
				return false;
			} else {
				updateErrorElementStyle(emptyElement, false);
			}
		} else {
			console.warn(`${emptyId} not found`);
		}

		// 패턴 유효성 검사
		const patternElement = document.getElementById(patternId);
		const isPatternValid = validateFn(input.value);
		if (patternElement) {
			updateErrorElementStyle(patternElement, !isPatternValid);
		}
		if (!isPatternValid) return false;

		// 중복 검사
		if (duplicateFn) {
			const duplicateElement = document.getElementById(duplicateId);
			const isDuplicateValid = await duplicateFn(input.value);
			if (duplicateElement) {
				updateErrorElementStyle(duplicateElement, !isDuplicateValid);
			}
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
		console.log("isValid.username", isValid.username);

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
		console.log("isValid.password", isValid.password);

	});

	nickname.addEventListener("change", async (e) => {
		isValid.nickname = await handleValidation(
			e.target,
			validateNickname,
			checkDuplnickname,
			{
				emptyId: "error-msg-nickname-empty",
				patternId: "error-msg-nickname-pattern",
				duplicateId: "error-msg-nickname",
			}
		);
		console.log("isValid.nickname", isValid.nickname);

	});

	phone.addEventListener("change", async (e) => {
		isValid.phone = await handleValidation(
			e.target,
			validatePhone,
			checkDuplPhone,
			{
				emptyId: "error-msg-phone-empty",
				patternId: "error-msg-phone-pattern",
				duplicateId: "error-msg-phone",
			}
		);
		console.log("isValid.phone", isValid.phone);

	});

	email.addEventListener("change", async (e) => {
		isValid.email = await handleValidation(
			e.target,
			validateEmail,
			checkDuplEmail,
			{
				emptyId: "error-msg-email-empty",
				patternId: "error-msg-email-pattern",
				duplicateId: "error-msg-email",
			}
		);
		console.log("isValid.email", isValid.email);

	});

	joinButton.addEventListener("click", async (e) => {
		e.preventDefault();

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

		if (Object.values(isValid).every((value) => value)) {
			try {
				const response = await fetch("/v1/members?command=join", {
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify(payload),
				});

				const data = await response.json();
				console.log("Response data:", data);

				if (data.status === 200 || data.status === 201) {
					alert("회원가입 성공!");
					window.location.href = "/main";
				} else {
					alert(data.message || "회원가입에 실패했습니다.");
					console.error("Join failed:", data);
				}
			} catch (error) {
				console.error("Error during registration:", error);
				alert("회원가입 요청 중 오류가 발생했습니다.");
			}
		} else {
			alert("모든 정보를 올바르게 입력해주세요.");
		}
	});
};
