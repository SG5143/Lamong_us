const chat = document.getElementById('chat');
const message = document.getElementById('message');
const send = document.getElementById('send');

let players = null;
let userUUID = null;
let round = 1;
let currentTurn = 1;
let myTurn = null;
let gameTopic = null;
let gameKeyword = null;
let countdownInterval;

const roomUUID = 'room1';
//const roomUUID = document.getElementById("roomUUID").value;

// WebSocket 서버에 연결
const ws =  new WebSocket(`${window.location.protocol}//${window.location.host}/ws/play/${roomUUID}`);

ws.onopen = function () {
};

ws.onmessage = function (event) {
	const data = JSON.parse(event.data);

	switch (data.type) {
		case 'SESSION_ID':
			userUUID = data.uuid;
			break;
		case 'CHAT_HISTORY':
			displayChatHistory(data.chatHistory);
			break;
		case 'GAME_START':
			handleGameStart(data.topic, data.keyword);
			break;
		case 'CLIENT_INFO':
			setPlayerList(data.clientInfo);
			break;
		case 'SET_TURN':
			initializePlayerTurn(data.turn);
			break;
		case 'ROUND_INFO':
			displayRoundInfo(data.round);
			break;
		case 'CUR_TURN':
			handleCurrentTurn(data.currentTurn);
			break;
		case 'MESSAGE':
			displayMessage(data.senderUUID, data.senderNickname, data.message);
			break;
		case 'STATE_CHANGE':
			handleStateChange(data.gamsState);
			break;
		case 'VOTE_RESULT':
			voteResult(data.votedPlayerNickname, data.voteResultMaxVotes, data.isLiarCorrect);
			break;
		case 'SYSTEM_MESSAGE':
			handleSystemMessage(data.messge)
			break;
		case 'GAME_END':
			endGameMessage(data.gameEnd);
			break;
	}
};

// ================ 채팅 제출 이벤트 리스너  ================
send.addEventListener('click', sendMessage);
message.addEventListener('keyup', function (event) {
	if (event.key === 'Enter') {
		sendMessage();
	}
});

// ================ 메시지 전송 ================ 
function sendMessage() {
	const msg = message.value;
	if (msg.trim() !== '' && !message.disabled) {
		ws.send(JSON.stringify({ type: 'MESSAGE', message: msg }));
		message.value = '';
	}
}
// ================ 진행중인 게임 플레이어 저장 ================ 
function setPlayerList(clientInfo) {
	players = clientInfo;
	
	const playerList = document.getElementById("player-ilst");
	playerList.innerHTML = ""; 

	players.forEach(player => {
		const li = document.createElement("li");
		li.setAttribute("data-uuid", player.uuid); 

		li.innerHTML = `
			<div class="profile-container">
				<img class="profile-image" src="${player.profileImage}" alt="${player.nickname}">
			</div>
			<div class="user-info">
				<p class="nickname">${player.uuid === userUUID ? `${player.nickname}(나)` : player.nickname}</p>
				<p class="user-explanation"></p>
			</div>
		`;

		playerList.appendChild(li);
	});
}

// ================ 기존 채팅 내역 출력 ================ 
function displayChatHistory(chatHistory) {
	chatHistory.forEach(chatMsg => {
		const historyMsg = document.createElement('div');
		historyMsg.textContent = `${chatMsg.writer}: ${chatMsg.message}`;
		chat.appendChild(historyMsg);
	});
}

// ================ 넘겨받은 채팅 출력 ================ 
function displayMessage(senderId, senderName, message) {
    const msgBox = document.createElement('div');
	msgBox.className ="user-msg-container"
	
	const now = new Date();
	const hours = String(now.getHours()).padStart(2, '0'); 
	const minutes = String(now.getMinutes()).padStart(2, '0'); 
	const formattedTime = `${hours}:${minutes}`;
	
    msgBox.innerHTML = `
        <div class="message-content">
			<div class = "message-writer-time">
				<span class="message-writer">${senderId === userUUID ? senderName+"(나)" : senderName}</span>
				<span class="message-time">${formattedTime}</span>
			</div>
            <p class="message-text">${message}</p>
        </div>
        <div class="profile-image"></div>
    `;
    
    const profileImg = msgBox.querySelector('.profile-image');
	const writerTime = msgBox.querySelector('.message-writer-time');
	const writer = msgBox.querySelector('.message-writer');
	const time = msgBox.querySelector('.message-time');
	const text =  msgBox.querySelector('.message-text');
    
    if (senderId === userUUID) {
        msgBox.style.justifyContent = "end";
		writerTime.style.justifyContent = "end";
		writer.style.textAlign="right";
		text.style.textAlign="right";
        profileImg.style.order = 1; 
		time.style.order=-1;
    } else {
        msgBox.style.justifyContent = "start";
		writerTime.style.justifyContent = "start";
		writer.style.textAlign="left";
		text.style.textAlign="left";
        profileImg.style.order = -1; 
		time.style.order=1;
    }
	
	players.forEach(player => {
		if (player.uuid === senderId) {
			if (player.profileImage.startsWith("resources/images/Default")) {
				profileImg.style.backgroundImage = `url('${player.profileImage}')`;
			} else {
				profileImg.style.backgroundImage = `url('data:image/png;base64,${player.profileImage}')`;
			}
		}
	});
	
	const playerElement = document.querySelector(`[data-uuid="${senderId}"]`);
	if (playerElement) {
	    const userExplanation = playerElement.querySelector('.user-explanation');
	    if (userExplanation) {
	        userExplanation.textContent = message; 
	    }
	}
    chat.appendChild(msgBox);
    chat.scrollTop = chat.scrollHeight;
}

// ================ 초기 턴 정보 저장 및 출력 ================ 
function initializePlayerTurn(turn) {
	myTurn = turn;

	chat.innerHTML = '';
	const msgBox = document.createElement('div');
	const msgHeader = document.createElement('h4');
	msgHeader.textContent = `당신의 순서는 ${myTurn}번 입니다.`;
	msgBox.style.textAlign ="center"

	msgBox.appendChild(msgHeader);
	chat.appendChild(msgBox);
}

// ================ 게임 시작 메시지 출력 ================ 
function handleGameStart(topic, keyword) {
	gameTopic = topic;
	gameKeyword = keyword;

	const msgBox = document.createElement('div');
	const msgHeader = document.createElement("h3");
	msgHeader.textContent = '게임이 시작되었습니다!';
	msgHeader.style.textAlign ="center"

	const detailMessage = document.createElement("p");
	if (topic && keyword) {
		detailMessage.textContent = ` 주제 : ${topic}  키워드 : ${keyword}`;
		document.getElementById('topic').textContent = `주제 ${topic}`;
		document.getElementById('keyword').textContent = `제시어 ${keyword}`;
		document.getElementById('explanation-msg').textContent = `라이어를 찾아주세요.`;
	} else {
		detailMessage.textContent = `라이어입니다. 다른 플레이어들의 주제를 맞춰주세요!`;
		document.getElementById('topic').textContent = `주제 ${topic}`;
		document.getElementById('keyword').textContent = '당신은 라이어 입니다';
		document.getElementById('explanation-msg').textContent = `제시어를 유추하세요.`;
	}
	detailMessage.style.textAlign ="center"

	msgBox.appendChild(msgHeader);
	msgBox.appendChild(detailMessage);

	chat.appendChild(msgBox);
}

// ================ 순서를 받아 상태 변경  ================ 
function handleCurrentTurn(turn) {
	currentTurn = turn;
	clearInterval(countdownInterval);

	let time = 30;
	message.disabled = currentTurn !== myTurn;

	countdownInterval = setInterval(() => {
		if (time > 0) {
			if (currentTurn === myTurn) {
				message.placeholder = `제시어를 설명해주세요. 남은 시간: ${time}초`;
				send.style.display = "block";
				message.focus();
			} else {
				message.placeholder = `현재 ${turn}번의 차례입니다. 남은 시간: ${time}초`;
				send.style.display = "none";
			}
			time--;
		} else {
			clearInterval(countdownInterval);
			if (currentTurn === myTurn) {
				ws.send(JSON.stringify({ type: 'MESSAGE', message: "제한 시간 초과" }));
			}
		}
	}, 1000);
}

// ================ 게임 상태 변화에 따른 기능  ================ 
function handleStateChange(state) {
	if (state === "ROUND_END") {
		clearInterval(countdownInterval);

		let timeLeft = 30;
		message.disabled = false;
		send.style.display = "block";

		const infoBox = document.createElement('div');
		const roundTitle = document.createElement("h4");

		roundTitle.textContent = `-- 30초 후 라이어 투표가 진행됩니다.  --`;
		roundTitle.style.fontSize = "24px";
		roundTitle.style.margin = "20px 0"
		roundTitle.style.textAlign = "center"

		infoBox.appendChild(roundTitle);
		chat.appendChild(infoBox);
		chat.scrollTop = chat.scrollHeight;

		countdownInterval = setInterval(() => {
			if (timeLeft > 0) {
				message.placeholder = `${timeLeft}초 동안 자유 채팅이 가능합니다.`;
				timeLeft--;
			} else {
				if (gameTopic && gameKeyword) {
					message.placeholder = `라이어를 찾아주세요.`;
				} else {
					message.placeholder = `투표 중 입니다.`;
				}
				message.disabled = true;
				send.style.display = "none";
				clearInterval(countdownInterval);
			}
		}, 1000);
	}
	
	if (state === "VOTE_PHASE") {
	    showVoteModal();
	}
	
	if(state === "FINAL_CHANCE"){
		finalChance();
	}
}

// ================ 라운드 변경 시 실행 ================ 
function displayRoundInfo(currentRound) {
	round = currentRound;

	const roundBox = document.createElement('div');
	const roundTitle = document.createElement("h4");

	roundTitle.textContent = `<< Round ${currentRound} >>`;
	roundTitle.style.fontSize ="24px";
	roundTitle.style.margin = "20px 0"
	roundTitle.style.textAlign ="center"
	
	roundBox.appendChild(roundTitle);
	chat.appendChild(roundBox);
	chat.scrollTop = chat.scrollHeight;
}

// ================ 투표 모달 띄우고 투표 진행  ================ 
function showVoteModal() {
	const modal = document.getElementById('vote-modal');
	const timerDisplay = document.getElementById('vote-timer');
	const voteBox = document.getElementById('vote-options');
	const submitBtn = document.getElementById('submit-vote');

	voteBox.innerHTML = '';
	modal.style.display = 'block';

	let time = 15;

	const timer = setInterval(() => {
	    if (time > 0) {
	        const minutes = String(Math.floor(time / 60)).padStart(2, '0');
	        const seconds = String(time % 60).padStart(2, '0');
	        timerDisplay.textContent = `${minutes}:${seconds}`;
	        time--;
	    } else {
	        clearInterval(timer);
	        timerDisplay.textContent = '00:00';
	        modal.style.display = 'none';
	    }
	}, 1000);

	players.forEach(player => {
		const option = document.createElement('label');
		option.className = 'vote-option';
		option.htmlFor = `vote-${player.uuid}`;
			
		const radio = document.createElement('input');
		radio.type = 'radio';
		radio.name = 'vote';
		radio.value = player.uuid;
		radio.id = `vote-${player.uuid}`;
		
		const img = document.createElement('img');
		img.src = player.profileImage;

		const label = document.createElement('span');	
		label.textContent = player.uuid === userUUID ? "나" :  player.nickname;
		
		label.appendChild(radio);
		option.appendChild(radio);
		option.appendChild(img);
		option.appendChild(label);
		voteBox.appendChild(option);
		
		submitBtn.addEventListener('click', () => {
		    const selectedVote = document.querySelector('input[name="vote"]:checked');
		    if (selectedVote) {
				ws.send(JSON.stringify({ type: 'VOTE', userUUID: userUUID, vote: selectedVote.value }));
		    }
		    clearInterval(timer);
		    modal.style.display = 'none';
		});
	});
}

// ================ 시스템 메시지 출력 ================ 
function handleSystemMessage(msg){
	chat.innerHTML = '';
	const msgBox = document.createElement('div');
	const msgHeader = document.createElement('h4');
	msgHeader.textContent = `${msg}`;
	msgBox.style.textAlign ="center"

	msgBox.appendChild(msgHeader);
	chat.appendChild(msgBox);
}

// ================ 투표 결과 출력 ================ 
function voteResult(nickname, maxVotes, isLiar) {
	const msgBox = document.createElement('div');
	const msgHeader = document.createElement('h4');
	msgBox.style.textAlign = "center"
	
	msgHeader.textContent = isLiar 
		? `라이어 ${nickname}님이 ${maxVotes}표를 받았습니다.` 
		: `라이어를 찾지 못했습니다.`;


	msgBox.appendChild(msgHeader);
	chat.appendChild(msgBox);
	chat.scrollTop = chat.scrollHeight;
}

// ================ 최종 제시어 입력 ================ 
function finalChance(){
	if (gameTopic && gameKeyword) {
		const msgBox = document.createElement('div');
		const msgHeader = document.createElement('h4');
		msgHeader.textContent = `라이어를 찾았습니다. 최종 키워드를 입력할 때까지 기다려주세요.`;
		msgBox.style.textAlign = "center"
		msgBox.appendChild(msgHeader);
		chat.appendChild(msgBox);
		chat.scrollTop = chat.scrollHeight;
	} else {
		const modal = document.getElementById("liar-chance-modal");
		modal.style.display = "block";
		startCountdown(15);
		setupSubmitButton();
	}
}

function startCountdown(time) {
	const countdownElement = document.getElementById("close-modal");

	if (countdownInterval) clearInterval(countdownInterval);

	countdownInterval = setInterval(() => {
		time--;
		const minutes = String(Math.floor(time / 60)).padStart(2, '0');
		const seconds = String(time % 60).padStart(2, '0');
		countdownElement.textContent = `${minutes}:${seconds}`;

		if (time <= 0) {
			clearInterval(countdownInterval);
			handleTimeout();
		}
	}, 1000);
}

function handleTimeout() {
	const modal = document.getElementById("liar-chance-modal");
	modal.style.display = "none";
	document.getElementById("liar-chance-input").value = "";

	ws.send(JSON.stringify({ type: 'FINAL_CHANCE', keyword: "" }));
}

function setupSubmitButton() {
	const submitBtn = document.getElementById("submit-keyword");
	const input = document.getElementById("liar-chance-input");

	submitBtn.addEventListener("click", () => {
		const userInput = input.value.trim();
		console.log(userInput);
		ws.send(JSON.stringify({ type: 'FINAL_CHANCE', keyword: userInput }));

		const modal = document.getElementById("liar-chance-modal");
		modal.style.display = "none";
		input.value = "";
		clearInterval(countdownInterval);
	});
}

// ================ 결과 및 종료 메시지 ================ 
function endGameMessage(status) {
	const modal = document.getElementById("game-result-modal");
	const content = modal.querySelector('.modal-content');
	content.innerHTML ="";
	modal.style.display = "block";

	const msgBox = document.createElement('div');
	const msgHeader = document.createElement('h2');
	const msgSpan =document.createElement('span');

	if (status === "liarVictory") {
		msgHeader.innerText = "라이어가 승리했습니다.";
	} else if (status === "liarDefeat") {
		msgHeader.innerText = "라이어를 찾아냈습니다.";
	} else if (status === "userLeave") {
		msgHeader.innerText = "최소인원을 충족시키지 못해 게임을 종료합니다.";
	}
	msgSpan.innerText ="잠시 후 게임 준비방으로 이동합니다.";
	
	msgBox.appendChild(msgHeader);
	msgBox.appendChild(msgSpan);
	content.appendChild(msgBox);
	
	setTimeout(() => {
	    window.location.href = "/lobby";
	}, 5000);
}
