import {
	updateErrorElementStyle,
	validateUsername,
	validatePassword,
	validateNickname,
	validatePhone,
	validateEmail,
	checkPassword,
	checkDuplUsername,
	checkDuplPhone,
	checkDuplEmail,
	checkDuplNickname,
} from "./validation.js";

window.onload = () => {
	const form = document.getElementById("form-join");

	const elements = {
		username: document.getElementById("username-join"),
		password: document.getElementById("new-password"),
		confirmPassword: document.getElementById("confirm-password"),
		nickname: document.getElementById("nickname-join"),
		phone: document.getElementById("phone-join"),
		email: document.getElementById("email-join"),
		joinButton: form.querySelector("button[type='submit']"),
	};

	const isValid = {
		username: false,
		password: false,
		checkPassword: false,
		nickname: false,
		phone: false,
		email: false,
	};

	const resetErrorMessages = () => {
		document.querySelectorAll(".error-msg-group").forEach((msg) => {
			msg.style.display = "none";
		});
	};

	const handleValidation = async (input, validateFn, duplicateFn, errorIds, field) => {
		resetErrorMessages();

		const { patternId, duplicateId } = errorIds;

		if (!patternId) {
			console.error("Pattern ID is missing in errorIds for", field);
			return false;
		}

		const patternElement = document.getElementById(patternId);
		if (!patternElement) {
			console.error(`Element with ID '${patternId}' not found for ${field}`);
			return false;
		}

		const isPatternValid = validateFn ? validateFn(input.value) : true;
		updateErrorElementStyle(patternElement, !isPatternValid);
		if (!isPatternValid) {
			isValid[field] = false;
			return false;
		}

		if (duplicateId) {
			const duplicateElement = document.getElementById(duplicateId);
			if (!duplicateElement) {
				console.error(`Element with ID '${duplicateId}' not found for ${field}`);
				return false;
			}

			const isDuplicateValid = await duplicateFn(input.value);
			updateErrorElementStyle(duplicateElement, !isDuplicateValid);
			if (!isDuplicateValid) {
				isValid[field] = false;
				return false;
			}
		}

		isValid[field] = true;
		return true;
	};

	const setInputEventListener = (inputElement, validateFn, duplicateFn, errorIds, field) => {
		inputElement.addEventListener('input', async () => {
			await handleValidation(inputElement, validateFn, duplicateFn, errorIds, field);
		});
	};

	const setupEventListeners = () => {
		const { username, password, confirmPassword, nickname, email, phone } = elements;

		// 유효성 검사 이벤트 리스너 등록
		setInputEventListener(username, validateUsername, checkDuplUsername, {
			emptyId: "error-msg-username-empty",
			patternId: "error-msg-username-pattern",
			duplicateId: "error-msg-username-duplicate"
		}, "username");

		setInputEventListener(password, validatePassword, null, {
			emptyId: "error-msg-password-empty",
			patternId: "error-msg-password-pattern"
		}, "password");

		setInputEventListener(confirmPassword, checkPassword, null, {
			emptyId: "error-msg-checkpassword-empty",
			patternId: "error-msg-checkpassword-pattern"
		}, "checkPassword");

		setInputEventListener(nickname, validateNickname, checkDuplNickname, {
			emptyId: "error-msg-nickname-empty",
			patternId: "error-msg-nickname-pattern",
			duplicateId: "error-msg-nickname-duplicate"
		}, "nickname");

		setInputEventListener(phone, validatePhone, checkDuplPhone, {
			emptyId: "error-msg-phone-empty",
			patternId: "error-msg-phone-pattern",
			duplicateId: "error-msg-phone-duplicate"
		}, "phone");

		setInputEventListener(email, validateEmail, checkDuplEmail, {
			emptyId: "error-msg-email-empty",
			patternId: "error-msg-email-pattern",
			duplicateId: "error-msg-email-duplicate"
		}, "email");
	};

	// 이벤트 리스너 설정
	setupEventListeners();

	// 회원가입 버튼 클릭 시 처리
	elements.joinButton.addEventListener("click", async (e) => {
		e.preventDefault();

		resetErrorMessages();

		const formData = {
			username: elements.username.value,
			password: elements.password.value,
			nickname: elements.nickname.value,
			phone: elements.phone.value,
			email: elements.email.value,
		};

		// 모든 유효성 검사 통과 여부 확인
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
				elements.joinButton.disabled = false;
			}
		} else {
			alert("모든 정보를 정확히 입력해 주세요.");
		}
	});
};
