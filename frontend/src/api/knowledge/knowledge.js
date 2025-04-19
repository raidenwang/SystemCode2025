
import request from '@/utils/request.js';
import form from 'element-plus/es/components/form/index.mjs';

// 表示当前正在轮询的知识库，当切换知识库后停止当前这个的轮询
// key为知识库id   value为对应的setIntervalID
// 切换标签就keys获取所有的key，然后clearInterval，delete除了active外的别的轮询
export const pollingPool = new Map()

export function getAllKnowledgeAPI() {
    return request({
        url: '/knowledgeBase/list',
        // url: 'http://10.16.53.4:8080/knowledgeBase/list',
        method: 'get'
    })
}

export function addNewKnowledgeAPI(id, name, type) {
    return request({
        // url: 'http://10.16.53.4:8080/knowledgeBase/add',
        url: '/knowledgeBase/add',
        method: 'post',
        data: {
            id,
            name,
            type
        }
    })
}

// 获取该知识库的所有文档，注意要手动加上status状态字段，控制“状态”表格栏目的文本
// export function getOneKnowledgeById(knowledgeId) {
//     return request({
//         url: `http://10.16.53.4:8080/knowledgeBase/list_files/${knowledgeId}`,
//         method: 'get'
//     })
// }

export async function getOneKnowledgeByIdAPI(knowledgeId) {
    const res = await request({
        // url: `http://10.16.53.4:8080/knowledgeBase/list_files/${knowledgeId}`,
        url: `/knowledgeBase/list_files/${knowledgeId}`,
        method: 'get'
    })
    res.data.forEach((_, idx) => {
        // 默认全是gray，代表正在上传，后面轮询获取状态再改变，还可以体现上传的过程
        res.data[idx] = { ...res.data[idx], status: 'gray' }
    })
    return res
}


export function removeKnowledgeByIdAPI(knowledgeId) {
    return request({
        // url: `http://10.16.53.4:8080/knowledgeBase/delete/${knowledgeId}`,
        url: `/knowledgeBase/delete/${knowledgeId}`,
        method: 'delete'
    })
}

export function updateKnowledgeAPI(id, name, type) {
    return request({
        // url: 'http://10.16.53.4:8080/knowledgeBase/update',
        url: '/knowledgeBase/update',
        method: 'put',
        data: {
            id,
            name,
            type
        }
    })
}

// export function uploadKnowledgeFile(knowledgeBaseId, institution, date, file) {
//     return request({
//         url: 'http://10.16.53.4:8080/knowledgeBase/uploadFiles',
//         method: 'post',
//         params:{
//             knowledgeBaseId:parseInt(knowledgeBaseId)
//         },
//         data: [{
//             institution,
//             date,
//             file
//         }]
//     })
// }

//  (knowledgeBaseId, file, institution, date,tag)
// export function uploadKnowledgeFileAPI(knowledgeBaseId, file, institution, date,tag) {
//     const formData = new FormData()
//     formData.append('fileList', JSON.stringify([{
//         filename: file.name,
//         institution,
//         date,
//         tag
//     }]))
//     formData.append('files', file)
//     return request({
//         // url: 'http://10.16.53.4:8080/knowledgeBase/uploadFiles',
//         url: '/knowledgeBase/uploadFiles',
//         method: 'post',
//         params: {
//             knowledgeBaseId: parseInt(knowledgeBaseId)
//         },
//         data: formData
//     })
// }

/**
 * 
 * @param {*} knowledgeBaseId 知识库id
 * @param {*} fileInfoList 文件信息数组，日期，文件名等等
 * @param {*} fileRawList 文件数组，file.raw
 * @returns 
 */
export function uploadKnowledgeFileAPI(knowledgeBaseId, fileInfoList, fileRawList) {
    if(!fileRawList)
    {
        console.log("文件为空")
    }
    const formData = new FormData()
    formData.append('fileList', JSON.stringify(fileInfoList))
    fileRawList.forEach(item => {
        formData.append('files', item)
    })
    return request({
        // url: 'http://10.16.53.4:8080/knowledgeBase/uploadFiles',
        url: '/knowledgeBase/uploadFiles',
        method: 'post',
        params: {
            knowledgeBaseId: parseInt(knowledgeBaseId)
        },
        data: formData
    })
}



export function deleteKnowledgeFileAPI(knowledgeBaseId, fileIds) {
    return request({
        // url: 'http://10.16.53.4:8080/knowledgeBase/delete_file',
        url: '/knowledgeBase/delete_file',
        method: 'delete',
        params: {
            knowledgeBaseId,
            fileIds
        }
    })
}

export async function getKnowledgeAllFileStatusAPI(knowledgeId) {
    const res = await request({
        // url: `http://10.16.53.4:8080/knowledgeBase/get_file_status/${knowledgeId}`,
        url: `/knowledgeBase/get_file_status/${knowledgeId}`,
        method: 'get'
    })
    // 代表这个知识库已经被删除了
    if (res.status === 500) {
        throw new Error(`id为${knowledgeId}的知识库已经被删除了`)
    }
    return res
}




/**
 * 轮询获取文件解析状态，成功了自动停止。你说会不会发生什么人超快的上传后又去上传发送问题呢？intersting
 * 如果存在上传失败的文件那么会一直轮询，我的方法是20次后不再轮询，直接停止，可能这个文件上传失败了吧。
 * 而且要在切换知识库标签后就停止这一个的轮询
 * @param {Number} knowledgeId 知识库id号
 * @param {Array} knowledgeDocList 右侧表格的ref对象数组部分，传递过来被修改status
 * @param {Number} pollTime 轮询间隔时间
 */
export async function pollKnowledgeAllFilesStatusAPI(knowledgeId, knowledgeDocList, pollTime = 1000) {
    console.log('轮询的时间是第一次1s，第二次2s，第三次3s，以此类推...')
    let times = 0
    let hasRequest = false
    let len = knowledgeDocList.length
    console.log('当前轮询的kid为', knowledgeId)
    // console.log('轮询池为:',pollingPool)
    // 如果当前有这个id的轮询了，就关闭上一个，开启这一个，因为这一个可能是上传后端，len更长，文件更多
    if (pollingPool.has(parseInt(knowledgeId))) {
        const intervalID = pollingPool.get(parseInt(knowledgeId))
        console.log('老的轮询被停止，定时器id为', intervalID)
        clearInterval(intervalID)
        pollingPool.delete(parseInt(knowledgeId))
    }
    let id = setInterval(poll, pollTime)
    async function poll() {
        if (!pollingPool.has(parseInt(knowledgeId))) {
            pollingPool.set(parseInt(knowledgeId), id)
        }
        // console.log('轮询池为:',pollingPool)
        // 请求6次还是问题就不请求了
        if (times > 20) {
            console.log('超过20次轮询仍未改变状态，轮询停止')
            clearInterval(id)
            pollingPool.delete(parseInt(knowledgeId))
            id = null
        }
        let allFileStatusOK = true
        // 避免发送多个请求，下一个请求要等到上一个请求到了才可以
        if (!hasRequest) {
            console.log(`这是第${times}次轮询，轮询间隔为${pollTime}ms`)
            hasRequest = true
            // 可能这个知识库已经被删除了，此时就要停止轮询，虽然外面删除的时候已经手动停止了，但是还是可能这里触发
            let { data: statusArr } = await getKnowledgeAllFileStatusAPI(knowledgeId)
            times++
            // console.log(statusArr)
            // 我觉得这里可以二分查找，毕竟传递的文档有先来后到的顺序，red正好都在后面，但是我怂，先遍历，后面再改为二分
            // 这里二者的数组数目应该是对应的....吗？不一定，因为可能发生多个人同时操作的可能，这边就没有收到最新的数据
            // 所以我取二者最小值
            // 不不不，我直接对应这个id就可以了吧
            for (let i = 0; i < len; ++i) {
                // console.log('轮询执行中')
                const idx = statusArr.findIndex(item => item.id === knowledgeDocList[i].id)
                if (idx !== -1) {
                    knowledgeDocList[i].status = statusArr[idx].status
                    // console.log('当前文件的状态为',statusArr[idx].status)
                    if (statusArr[idx].status === 'gray') allFileStatusOK = false
                }
            }
            // 全部状态确定，可以退出轮询了
            if (allFileStatusOK) {
                console.log('轮询停止')
                clearInterval(id)
                pollingPool.delete(parseInt(knowledgeId))
                id = null
                statusArr = null
                return
            }
            pollTime += 1000
            clearInterval(id)
            id = setInterval(poll,pollTime)
            hasRequest = false
        }
    }
}


/**
 * 获取当前知识库所有文件，带有状态的返回
 * @param  knowledgeId 
 */
export async function getAllFilesWithStatusAPI(knowledgeId) {
    const response = await Promise.all([getOneKnowledgeByIdAPI(knowledgeId), getKnowledgeAllFileStatusAPI(knowledgeId)])
    console.log("API response",response)
    return response
}
