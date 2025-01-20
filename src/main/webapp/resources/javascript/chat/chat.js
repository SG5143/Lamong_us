const chat = document.getElementById('chat');
const message = document.getElementById('message');
const send = document.getElementById('send');

let sessionId = null;
let round = 1;
let currentTurn = 1;
let myTurn = null;

let gameTopic = null;
let gameKeyword = null;

let countdownInterval;

const roomType = 'playing';
const roomUUID = 'room1';

// WebSocket 서버에 연결
const ws = new WebSocket(`ws://localhost:8080/ws/${roomType}/${roomUUID}`);

ws.onopen = function() {
	// ws.send('GET_CHAT_HISTORY');
	ws.send('TEST_SESSION_ID')
};

ws.onmessage = function(event) {
	const data = JSON.parse(event.data);

	switch (data.type) {
		case 'SESSION_ID':
			sessionId = data.sessionId;
			break;
		case 'CHAT_HISTORY':
			displayChatHistory(data.chatHistory);
			break;
		case 'GAME_START':
			handleGameStart(data.topic, data.keyword);
			break;
		case 'SET_TURN':
			initializePlayerTurn(data.turn);
			break;
		case 'CUR_TURN':
			handleCurrentTurn(data.currentTurn);
			break;
		case 'MESSAGE':
			displayMessage(data.sessionId, data.message);
			break;
		case 'ROUND_END':
			handleRoundEnd();
			break;
		case 'ROUND_INFO':
			displayRoundInfo(data.round);
			break;
	}
};

send.addEventListener('click', sendMessage);

message.addEventListener('keyup', function(event) {
	if (event.key === 'Enter') {
		sendMessage();
	}
});

function sendMessage() {
	const msg = message.value;
	if (msg.trim() !== '' && !message.disabled) {
		ws.send(JSON.stringify({ type: 'MESSAGE', message: msg }));
		message.value = '';
	}
}

function displayChatHistory(chatHistory) {
	chatHistory.forEach(chatMsg => {
		const historyMsg = document.createElement('div');
		historyMsg.textContent = `${chatMsg.writer}: ${chatMsg.message}`;
		chat.appendChild(historyMsg);
	});
}

function displayMessage(senderSessionId, message) {
	const msg = document.createElement('div');
	msg.textContent = `${senderSessionId}: ${message} `;
	chat.appendChild(msg);
}

function initializePlayerTurn(playerTurn) {
	myTurn = playerTurn;

	chat.innerHTML = '';
	const turnMessageContainer = document.createElement('div');
	const turnMessageHeader = document.createElement('h4');
	turnMessageHeader.textContent = `당신의 순서는 ${myTurn}번 입니다.`;

	turnMessageContainer.appendChild(turnMessageHeader);
	chat.appendChild(turnMessageContainer);
}

function handleGameStart(topic, keyword) {
	gameTopic = topic;
	gameKeyword = keyword;

	const startMessageContainer = document.createElement('div');
	const headerMessage = document.createElement("h3");
	headerMessage.textContent = '게임이 시작되었습니다!';

	const detailMessage = document.createElement("p");
	if (topic !== undefined && topic !== null && keyword !== undefined && keyword !== null) {
		detailMessage.textContent = ` 주제 : ${topic}  키워드 : ${keyword}`;
	} else {
		detailMessage.textContent = `라이어입니다. 다른 플레이어들의 주제를 맞춰주세요!`;
	}

	startMessageContainer.appendChild(msg);
	startMessageContainer.appendChild(msg2);

	chat.appendChild(startMessageContainer);
}

function handleCurrentTurn(curTurn) {
	currentTurn = curTurn;
	clearInterval(countdownInterval);

	let timeLeft = 30;
	message.disabled = currentTurn !== myTurn;

	countdownInterval = setInterval(() => {
		if (timeLeft > 0) {
			if (currentTurn === myTurn) {
				message.placeholder = `제시어를 설명해주세요. 남은 시간: ${timeLeft}초`;
			} else {
				message.placeholder = `현재 ${curTurn}번의 차례입니다. 남은 시간: ${timeLeft}초`;
			}
			timeLeft--;
		} else {
			clearInterval(countdownInterval);
			if (currentTurn === myTurn) {
				ws.send(JSON.stringify({ type: 'MESSAGE', message: "제한 시간 초과" }));
			}
		}
	}, 1000);
}

function handleRoundEnd() {
	clearInterval(countdownInterval);

	let timeLeft = 60;
	message.disabled = false;

	countdownInterval = setInterval(() => {
		if (timeLeft > 0) {
			message.placeholder = `${timeLeft}초 동안 자유 채팅이 가능합니다.`;
			timeLeft--;
		} else {
			if (gameTopic !== undefined && gameTopic !== null && gameKeyword !== undefined && gameKeyword !== null) {
				message.placeholder = `라이어를 찾아주세요.`;
			} else {
				message.placeholder = `투표 중 입니다.`;
			}
			message.disabled = true;
			clearInterval(countdownInterval);
		}
	}, 1000);
}

function displayRoundInfo(currentRound) {
    round = currentRound;

    const roundInfoContainer = document.createElement('div');
    const roundHeader = document.createElement("h4");

    roundHeader.textContent = `<< Round ${currentRound} >>`;
    roundInfoContainer.appendChild(roundHeader);
    chat.appendChild(roundInfoContainer);
}



