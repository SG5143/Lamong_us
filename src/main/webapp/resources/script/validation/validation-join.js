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
		document.querySelectorAll(".error-msg-group").forEach((msg) => {
			msg.style.display = "none";
		});
	};

	const handleValidation = async (input, validateFn, duplicateFn, errorIds) => {
		const { emptyId, patternId, duplicateId } = errorIds;

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

		const patternElement = document.getElementById(patternId);
		const isPatternValid = validateFn(input.value);
		if (patternElement) {
			updateErrorElementStyle(patternElement, !isPatternValid);
		}
		if (!isPatternValid) return false;

		if (duplicateFn) {
			const duplicateElement = document.getElementById(duplicateId);
			const isDuplicateValid = await duplicateFn(input.value);
			if (duplicateElement)
				updateErrorElementStyle(duplicateElement, !isDuplicateValid);

			return isDuplicateValid;
		}

		return true;
	};

	const setInputEventListener = (input, validateFn, duplicateFn, errorIds, field) => {
		input.addEventListener("change", async (e) => {
			isValid[field] = await handleValidation(e.target, validateFn, duplicateFn, errorIds);
			console.log(`isValid.${field}`, isValid[field]);
		});
	};

	setInputEventListener(username, validateUsername, checkDuplUsername, {
		emptyId: "error-msg-username-empty",
		patternId: "error-msg-username-pattern",
		duplicateId: "error-msg-username"
	}, "username");

	setInputEventListener(password, validatePassword, null, {
		emptyId: "error-msg-password-empty",
		patternId: "error-msg-password-pattern"
	}, "password");

	setInputEventListener(nickname, validateNickname, checkDuplnickname, {
		emptyId: "error-msg-nickname-empty",
		patternId: "error-msg-nickname-pattern",
		duplicateId: "error-msg-nickname"
	}, "nickname");

	setInputEventListener(phone, validatePhone, checkDuplPhone, {
		emptyId: "error-msg-phone-empty",
		patternId: "error-msg-phone-pattern",
		duplicateId: "error-msg-phone"
	}, "phone");

	setInputEventListener(email, validateEmail, checkDuplEmail, {
		emptyId: "error-msg-email-empty",
		patternId: "error-msg-email-pattern",
		duplicateId: "error-msg-email"
	}, "email");

	joinButton.addEventListener("click", async (e) => {
		e.preventDefault();

		resetErrorMessages();

		const loginTypeValue = loginType.value === "1" ? "kakao" : (loginType.value === "2" ? "google" : null);
		const formData = {
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
					body: JSON.stringify(formData)

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
			} finally {
				joinButton.disabled = false;
			}
		}
	});
};
