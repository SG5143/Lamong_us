const chat = document.getElementById('chat');
const message = document.getElementById('message');
const send = document.getElementById('send');

let players = null;

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

ws.onopen = function () {
	// ws.send('GET_CHAT_HISTORY');
	ws.send('TEST_SESSION_ID')
};

ws.onmessage = function (event) {
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
		case 'ROUND_INFO':
			displayRoundInfo(data.round);
			break;
		case 'ROUND_END':
			handleRoundEnd();
			break;
		case 'CLIENT_INFO':
			setPlayerList(data.clientInfo);
			break;
	}
};

send.addEventListener('click', sendMessage);

message.addEventListener('keyup', function (event) {
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

function setPlayerList(clientInfo) {
	players = clientInfo;
}


function displayChatHistory(chatHistory) {
	chatHistory.forEach(chatMsg => {
		const historyMsg = document.createElement('div');
		historyMsg.textContent = `${chatMsg.writer}: ${chatMsg.message}`;
		chat.appendChild(historyMsg);
	});
}

function displayMessage(senderSessionId, message) {
    const messageContainer = document.createElement('div');
	messageContainer.className ="user-msg-container"
	
	const now = new Date();
	const hours = String(now.getHours()).padStart(2, '0'); 
	const minutes = String(now.getMinutes()).padStart(2, '0'); 
	const formattedTime = `${hours}:${minutes}`;
	
    messageContainer.innerHTML = `
        <div class="message-content">
			<div class = "message-writer-time">
				<span class="message-writer">${senderSessionId === sessionId ? '나' : senderSessionId}</span>
				<span class="message-time">${formattedTime}</span>
			</div>
            <p class="message-text">${message}</p>
        </div>
        <div class="profile-image"></div>
    `;
    
    const profileImage = messageContainer.querySelector('.profile-image');
	const container = messageContainer.querySelector('.message-writer-time');
	const time = messageContainer.querySelector('.message-time')
    
    if (senderSessionId === sessionId) {
        messageContainer.style.justifyContent = "end";
		container.style.justifyContent = "end";
		messageContainer.querySelector('p').style.textAlign = "right";
        profileImage.style.order = 1; 
		time.style.order=-1;
    } else {
        messageContainer.style.justifyContent = "start";
		container.style.justifyContent = "start";
		messageContainer.querySelector('p').style.textAlign = "left";
        profileImage.style.order = -1; 
		time.style.order=1;
    }

    chat.appendChild(messageContainer);
    chat.scrollTop = chat.scrollHeight;
}

function initializePlayerTurn(playerTurn) {
	myTurn = playerTurn;

	chat.innerHTML = '';
	const turnMessageContainer = document.createElement('div');
	const turnMessageHeader = document.createElement('h4');
	turnMessageHeader.textContent = `당신의 순서는 ${myTurn}번 입니다.`;
	turnMessageContainer.style.textAlign ="center"

	turnMessageContainer.appendChild(turnMessageHeader);
	chat.appendChild(turnMessageContainer);
}

function handleGameStart(topic, keyword) {
	gameTopic = topic;
	gameKeyword = keyword;

	const startMessageContainer = document.createElement('div');
	const headerMessage = document.createElement("h3");
	headerMessage.textContent = '게임이 시작되었습니다!';
	headerMessage.style.textAlign ="center"

	const detailMessage = document.createElement("p");
	if (topic !== undefined && topic !== null && keyword !== undefined && keyword !== null) {
		detailMessage.textContent = ` 주제 : ${topic}  키워드 : ${keyword}`;
	} else {
		detailMessage.textContent = `라이어입니다. 다른 플레이어들의 제시어를 맞춰주세요!`;
	}
	detailMessage.style.textAlign ="center"

	startMessageContainer.appendChild(headerMessage);
	startMessageContainer.appendChild(detailMessage);

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
				send.style.display = "block";
			} else {
				message.placeholder = `현재 ${curTurn}번의 차례입니다. 남은 시간: ${timeLeft}초`;
				send.style.display = "none";
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
	send.style.display = "block";
	
	const roundInfoContainer = document.createElement('div');
	const roundHeader = document.createElement("h4");

	roundHeader.textContent = `-- 60초 후 라이어 투표가 진행됩니다.  --`;
	roundHeader.style.fontSize ="24px";
	roundHeader.style.margin = "20px 0"
	roundHeader.style.textAlign ="center"

	roundInfoContainer.appendChild(roundHeader);
	chat.appendChild(roundInfoContainer);
	chat.scrollTop = chat.scrollHeight;

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
			send.style.display = "none";
			clearInterval(countdownInterval);
			handleVote();
		}
	}, 1000);
}

function displayRoundInfo(currentRound) {
	round = currentRound;

	const roundInfoContainer = document.createElement('div');
	const roundHeader = document.createElement("h4");

	roundHeader.textContent = `<< Round ${currentRound} >>`;
	roundHeader.style.fontSize ="24px";
	roundHeader.style.margin = "20px 0"
	roundHeader.style.textAlign ="center"
	
	roundInfoContainer.appendChild(roundHeader);
	chat.appendChild(roundInfoContainer);
}

function handleVote() {
	const modal = document.getElementById('liar-vote-modal');
	const closeModal = document.getElementById('close-modal');
	const voteContainer = document.getElementById('vote-options');
	const submitVoteButton = document.getElementById('submit-vote');
	
	voteContainer.innerHTML = '';
	modal.style.display = 'block';
	
	let timeLeft = 30;
	
	const timer = setInterval(() => {
	    if (timeLeft > 0) {
	        const minutes = String(Math.floor(timeLeft / 60)).padStart(2, '0');
	        const seconds = String(timeLeft % 60).padStart(2, '0');
	        closeModal.textContent = `${minutes}:${seconds}`;
	        timeLeft--;
	    } else {
	        clearInterval(timer);
	        closeModal.textContent = '00:00';
	        modal.style.display = 'none';
	    }
	}, 1000);

	players.forEach(player => {
		const voteOption = document.createElement('label');
		voteOption.className = 'vote-option';
		voteOption.htmlFor = `vote-${player.uuid}`;
			
		const radioInput = document.createElement('input');
		radioInput.type = 'radio';
		radioInput.name = 'vote';
		radioInput.value = player.uuid;
		radioInput.id = `vote-${player.uuid}`;
		
		const img = document.createElement('img');
		//img.src = player.img;

		const label = document.createElement('span');	
		label.textContent = player.uuid == sessionId ? "나" :  player.uuid;
		
		label.appendChild(radioInput);

		voteOption.appendChild(radioInput);
		voteOption.appendChild(img);
		voteOption.appendChild(label);
		voteContainer.appendChild(voteOption);
		
		submitVoteButton.addEventListener('click', () => {
		    const selectedVote = document.querySelector('input[name="vote"]:checked');
		    if (selectedVote) {
				ws.send(JSON.stringify({ type: 'VOTE', sessionId: sessionId, vote: selectedVote.value }));
		    }
		    clearInterval(timer);
		    modal.style.display = 'none';
		});
	});
}


