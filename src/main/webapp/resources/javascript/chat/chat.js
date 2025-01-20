const chat = document.getElementById('chat');
const message = document.getElementById('message');
const send = document.getElementById('send');

let sessionId = null;
let currentTurn = 1;
let myTurn = null;
const roomType = 'playing';
const roomUUID = 'room1';

// WebSocket 서버에 연결
const ws = new WebSocket(`ws://localhost:8080/ws/${roomType}/${roomUUID}`);

ws.onopen = function () {
    ws.send('GET_CHAT_HISTORY');
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
        case 'MESSAGE':
            displayMessage(data.sessionId, data.message);
            break;
        case 'GAME_START':
            handleGameStart(data.topic, data.keyword);
            break;
        case 'SET_TURN':
            handleTurn(data.turn);
            break;
        case 'CUR_TURN':
            currentTurn = data.currentTurn;
            break;
    }  
};

send.addEventListener('click', function () {
    const msg = message.value;
    if (msg.trim() !== '' && !message.disabled) {
        ws.send(JSON.stringify({ type: 'MESSAGE', message: msg }));
        message.value = '';
    }
});

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

function handleGameStart(topic, keyword) {
    const startMsg = document.createElement('div');

    if (topic !== undefined && topic !== null && keyword !== undefined && keyword !== null) {
        startMsg.textContent = `게임이 시작되었습니다! 주제: ${topic}, 키워드: ${keyword}`;
    } else {
        startMsg.textContent = `게임이 시작되었습니다! 라이어입니다 다른 플레이어들의 주제를 맞춰주세요!`;
    }
    chat.appendChild(startMsg);
}

function handleTurn(turn) {
    myTurn = turn;
    const turnMsg = document.createElement('div');
    turnMsg.textContent = `당신의 순서는 ${myTurn}번 입니다.`;
    chat.appendChild(turnMsg);
}