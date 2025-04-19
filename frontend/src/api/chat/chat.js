import request from '@/utils/request'

//
export function getAllChatId() {
    return request({
        url: '/chat/getAllChatId',
        method: 'get'
    })
}

export function getChatContend(chatId) {
    return request({
        url: '/chat/getHistoryRecords/' + chatId,
        method: 'get'
    })
}

export function addChat(chatTitle, promptId) {
    return request({
        url: '/chat/add?chatName=' + chatTitle + '&promptId=' + promptId,
        method: 'post'
    })
}

export function deleteChat(chatId) {
    return request({
        url: '/chat/delete/' + chatId,
        method: 'delete'
    })
}


export function chat(data) {
    return request({
        url: '/chat',
        method: 'post',
        data: data
    })
}


export function getPrompt() {
    return request({
        url: '/chat/getAllPrompt',
        method: 'get'
    })
}
